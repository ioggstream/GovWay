/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */



package org.openspcoop2.pdd.timers;

import java.sql.Connection;
import java.util.List;

import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateful;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.engine.driver.RepositoryBuste;
import org.openspcoop2.protocol.sdk.state.StateMessage;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.id.serial.InfoStatistics;
import org.openspcoop2.utils.semaphore.Semaphore;
import org.openspcoop2.utils.semaphore.SemaphoreConfiguration;
import org.openspcoop2.utils.semaphore.SemaphoreMapping;
import org.slf4j.Logger;

/**
 * Implementazione dell'interfaccia {@link TimerGestoreRepositoryBuste} del Gestore
 * dei threads di servizio di OpenSPCoop.
 * 
 *  
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class TimerGestoreRepositoryBusteLib {

	private MsgDiagnostico msgDiag = null;
	private Logger logTimer = null;
	private OpenSPCoop2Properties propertiesReader = null;
	private boolean logQuery = false;
	private int limit = CostantiPdD.LIMIT_MESSAGGI_GESTORI;
	private boolean orderByQuery;

	private TimerLock timerLock = null;

	/** Semaforo */
	private Semaphore semaphore = null;
	private InfoStatistics semaphore_statistics;

	public TimerGestoreRepositoryBusteLib(MsgDiagnostico msgDiag,Logger log,OpenSPCoop2Properties p,boolean logQuery,int limit,boolean orderByQuery) throws TimerException{
		this.msgDiag = msgDiag;
		this.logTimer = log;
		this.propertiesReader = p;
		this.logQuery = logQuery;
		this.limit = limit;
		this.orderByQuery = orderByQuery;

		// deve essere utilizzato lo stesso lock per GestoreMessaggi, ConsegnaContenuti, GestoreBuste per risolvere problema di eliminazione descritto in GestoreMessaggi metodo deleteMessageWithLock 
		this.timerLock = new TimerLock(TipoLock.GESTIONE_REPOSITORY_MESSAGGI); 

		if(this.propertiesReader.isTimerLockByDatabase()) {
			this.semaphore_statistics = new InfoStatistics();

			SemaphoreConfiguration config = GestoreMessaggi.newSemaphoreConfiguration(this.propertiesReader.getTimerGestoreRepositoryBuste_lockMaxLife(), 
					this.propertiesReader.getTimerGestoreRepositoryBuste_lockIdleTime());

			TipiDatabase databaseType = TipiDatabase.toEnumConstant(this.propertiesReader.getDatabaseType());
			try {
				this.semaphore = new Semaphore(this.semaphore_statistics, SemaphoreMapping.newInstance(this.timerLock.getIdLock()), 
						config, databaseType, this.logTimer);
			}catch(Exception e) {
				throw new TimerException(e.getMessage(),e);
			}
		}
	}

	public void check() throws TimerException {

		// Controllo che il sistema non sia andando in shutdown
		if(OpenSPCoop2Startup.contextDestroyed){
			this.logTimer.error("["+TimerGestoreRepositoryBuste.ID_MODULO+"] Rilevato sistema in shutdown");
			return;
		}

		// Controllo che l'inizializzazione corretta delle risorse sia effettuata
		if(OpenSPCoop2Startup.initialize==false){
			this.msgDiag.logFatalError("inizializzazione di OpenSPCoop non effettuata", "Check Inizializzazione");
			String msgErrore = "Riscontrato errore: inizializzazione del Timer o di OpenSPCoop non effettuata";
			this.logTimer.error(msgErrore);
			throw new TimerException(msgErrore);
		}

		// Controllo risorse di sistema disponibili
		if( TimerMonitoraggioRisorseThread.risorseDisponibili == false){
			this.logTimer.error("["+TimerGestoreRepositoryBuste.ID_MODULO+"] Risorse di sistema non disponibili: "+TimerMonitoraggioRisorseThread.risorsaNonDisponibile.getMessage(),TimerMonitoraggioRisorseThread.risorsaNonDisponibile);
			return;
		}
		if( MsgDiagnostico.gestoreDiagnosticaDisponibile == false){
			this.logTimer.error("["+TimerGestoreRepositoryBuste.ID_MODULO+"] Sistema di diagnostica non disponibile: "+MsgDiagnostico.motivoMalfunzionamentoDiagnostici.getMessage(),MsgDiagnostico.motivoMalfunzionamentoDiagnostici);
			return;
		}

		this.msgDiag.logPersonalizzato("controlloInCorso");
		this.logTimer.info(this.msgDiag.getMessaggio_replaceKeywords("controlloInCorso"));
		long startControlloRepositoryBuste = DateManager.getTimeMillis();

		OpenSPCoopStateful openspcoopstate = new OpenSPCoopStateful();
		try {

			openspcoopstate.initResource(this.propertiesReader.getIdentitaPortaDefault(null), TimerGestoreRepositoryBuste.ID_MODULO, null);
			Connection connectionDB = ((StateMessage)openspcoopstate.getStatoRichiesta()).getConnectionDB();

			// Messaggi da eliminare 
			RepositoryBuste repositoryBuste = new RepositoryBuste(openspcoopstate.getStatoRichiesta(),this.logTimer,null);
			boolean trovatiMessaggi = true;

			while(trovatiMessaggi){

				trovatiMessaggi = false;

				// Eliminazione Messaggi from INBOX
				String causaMessaggiINBOX = "Eliminazione buste (INBOX) marcate logicamente da eliminare";
				List<String> idMsgINBOX = null;
				try{
					GestoreMessaggi.acquireLock(
							this.semaphore, connectionDB, this.timerLock,
							this.msgDiag, causaMessaggiINBOX, this.propertiesReader.getMsgGiaInProcessamento_AttesaAttiva(), this.propertiesReader.getMsgGiaInProcessamento_CheckInterval());

					idMsgINBOX = repositoryBuste.getBusteDaEliminareFromInBox(this.limit,this.logQuery,
							this.propertiesReader.isForceIndex(),this.propertiesReader.isRepositoryBusteFiltraBusteScaduteRispettoOraRegistrazione(),
							this.orderByQuery);
					int gestiti = 0;
					if(idMsgINBOX.size()>0){
						if(this.logQuery)
							this.logTimer.info("Trovate "+idMsgINBOX.size()+" buste da eliminare nel repository (INBOX) ...");
						trovatiMessaggi = true;

						this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_MESSAGGIO,Costanti.INBOX);
						for(int i=0; i<idMsgINBOX.size(); i++){

							String idMsgDaEliminare = idMsgINBOX.get(i);
							this.msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_DA_ELIMINARE,idMsgDaEliminare);

							try{
								try{
									GestoreMessaggi.updateLock(
											this.semaphore, connectionDB, this.timerLock,
											this.msgDiag, "Eliminazione busta INBOX con id ["+idMsgDaEliminare+"] ...");
								}catch(Throwable e){
									this.msgDiag.logErroreGenerico(e,"EliminazioneBustaInbox("+idMsgDaEliminare+")-UpdateLock");
									this.logTimer.error("ErroreEliminazioneBustaInbox("+idMsgDaEliminare+")-UpdateLock: "+e.getMessage(),e);
									break;
								}

								repositoryBuste.eliminaBustaFromInBox(idMsgDaEliminare);

								this.msgDiag.logPersonalizzato("eliminazioneMessaggio");
								if(this.logQuery)
									this.logTimer.debug(this.msgDiag.getMessaggio_replaceKeywords("eliminazioneMessaggio"));

								gestiti++;

							}catch(Exception e){
								this.msgDiag.logErroreGenerico(e,"EliminazioneBustaInbox("+idMsgDaEliminare+")");
								this.logTimer.error("ErroreEliminazioneBustaInbox("+idMsgDaEliminare+"): "+e.getMessage(),e);
							}
						}

						if(this.logQuery)
							this.logTimer.info("Eliminate "+gestiti+" buste nel repository (INBOX)");
					}
					else{
						if(this.logQuery)
							this.logTimer.info("Non sono state trovate buste da eliminare nel repository (INBOX)");
					}
				}finally{
					try{
						GestoreMessaggi.releaseLock(
								this.semaphore, connectionDB, this.timerLock,
								this.msgDiag, causaMessaggiINBOX);
					}catch(Exception e){}
				}

				//	Eliminazione Messaggi from OUTBOX
				String causaMessaggiOUTBOX = "Eliminazione buste (OUTBOX) marcate logicamente da eliminare";
				List<String> idMsgOUTBOX = null;
				try{
					GestoreMessaggi.acquireLock(
							this.semaphore, connectionDB, this.timerLock,
							this.msgDiag, causaMessaggiOUTBOX, this.propertiesReader.getMsgGiaInProcessamento_AttesaAttiva(), this.propertiesReader.getMsgGiaInProcessamento_CheckInterval());

					idMsgOUTBOX = repositoryBuste.getBusteDaEliminareFromOutBox(this.limit,this.logQuery,
							this.propertiesReader.isForceIndex(),this.propertiesReader.isRepositoryBusteFiltraBusteScaduteRispettoOraRegistrazione(),
							this.orderByQuery);
					int gestiti = 0;
					if(idMsgOUTBOX.size()>0){
						if(this.logQuery)
							this.logTimer.info("Trovate "+idMsgOUTBOX.size()+" buste da eliminare nel repository (OUTBOX) ...");
						trovatiMessaggi = true;

						this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_MESSAGGIO,Costanti.OUTBOX);
						for(int i=0; i<idMsgOUTBOX.size(); i++){

							String idMsgDaEliminare = idMsgOUTBOX.get(i);
							this.msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_DA_ELIMINARE,idMsgDaEliminare);

							try{
								try{
									GestoreMessaggi.updateLock(
											this.semaphore, connectionDB, this.timerLock,
											this.msgDiag, "Eliminazione busta OUTBOX con id ["+idMsgDaEliminare+"] ...");
								}catch(Throwable e){
									this.msgDiag.logErroreGenerico(e,"EliminazioneBustaOutbox("+idMsgDaEliminare+")-UpdateLock");
									this.logTimer.error("ErroreEliminazioneBustaOutbox("+idMsgDaEliminare+")-UpdateLock: "+e.getMessage(),e);
									break;
								}

								repositoryBuste.eliminaBustaFromOutBox(idMsgDaEliminare);

								this.msgDiag.logPersonalizzato("eliminazioneMessaggio");
								if(this.logQuery)
									this.logTimer.debug(this.msgDiag.getMessaggio_replaceKeywords("eliminazioneMessaggio"));

								gestiti++;

							}catch(Exception e){
								this.msgDiag.logErroreGenerico(e,"EliminazioneBustaOutbox("+idMsgDaEliminare+")");
								this.logTimer.error("ErroreEliminazioneBustaOutbox("+idMsgDaEliminare+"): "+e.getMessage(),e);
							}
						}

						if(this.logQuery)
							this.logTimer.info("Eliminate "+gestiti+" buste nel repository (OUTBOX)");
					}
					else{
						if(this.logQuery)
							this.logTimer.info("Non sono state trovate buste da eliminare nel repository (OUTBOX)");
					}
				}finally{
					try{
						GestoreMessaggi.releaseLock(
								this.semaphore, connectionDB, this.timerLock,
								this.msgDiag, causaMessaggiOUTBOX);
					}catch(Exception e){}
				}


				if(trovatiMessaggi){
					this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_REPOSITORY_BUSTE_NUM_MSG_INBOX, idMsgINBOX.size()+"");
					this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_REPOSITORY_BUSTE_NUM_MSG_OUTBOX, idMsgOUTBOX.size()+"");
					this.msgDiag.logPersonalizzato("ricercaMessaggiDaEliminare");
				}

			}


			long endControlloRepositoryBuste = DateManager.getTimeMillis();
			long diff = (endControlloRepositoryBuste-startControlloRepositoryBuste);
			this.logTimer.info("Controllo Repository Buste terminato in "+Utilities.convertSystemTimeIntoString_millisecondi(diff, true));

		} 
		catch(TimerLockNotAvailableException t) {
			// msg diagnostico emesso durante l'emissione dell'eccezione
			this.logTimer.info(t.getMessage(),t);
		}
		catch (Exception e) {
			this.msgDiag.logErroreGenerico(e,"GestioneBuste");
			this.logTimer.error("Riscontrato errore durante l'eliminazione delle buste: "+e.getMessage(),e);
		}finally{
			if(openspcoopstate!=null)
				openspcoopstate.releaseResource();
		}
	}

}
