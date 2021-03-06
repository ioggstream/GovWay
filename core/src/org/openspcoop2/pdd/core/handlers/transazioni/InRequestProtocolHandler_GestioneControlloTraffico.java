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
package org.openspcoop2.pdd.core.handlers.transazioni;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy;
import org.openspcoop2.core.controllo_traffico.ElencoIdPolicyAttive;
import org.openspcoop2.core.controllo_traffico.IdActivePolicy;
import org.openspcoop2.core.controllo_traffico.beans.DatiTransazione;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicy;
import org.openspcoop2.core.controllo_traffico.beans.UniqueIdentifierUtilities;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.core.controllo_traffico.constants.TipoErrore;
import org.openspcoop2.core.controllo_traffico.constants.TipoRisorsa;
import org.openspcoop2.core.controllo_traffico.driver.IGestorePolicyAttive;
import org.openspcoop2.core.controllo_traffico.utils.PolicyUtilities;
import org.openspcoop2.core.eventi.constants.CodiceEventoControlloTraffico;
import org.openspcoop2.core.eventi.constants.TipoEvento;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.controllo_traffico.ConfigurazioneControlloTraffico;
import org.openspcoop2.pdd.core.controllo_traffico.ConnettoreUtilities;
import org.openspcoop2.pdd.core.controllo_traffico.CostantiControlloTraffico;
import org.openspcoop2.pdd.core.controllo_traffico.DatiTempiRisposta;
import org.openspcoop2.pdd.core.controllo_traffico.GeneratoreMessaggiDiagnostici;
import org.openspcoop2.pdd.core.controllo_traffico.GeneratoreMessaggiErrore;
import org.openspcoop2.pdd.core.controllo_traffico.GestoreControlloTraffico;
import org.openspcoop2.pdd.core.controllo_traffico.RisultatoVerificaPolicy;
import org.openspcoop2.pdd.core.controllo_traffico.StatoTraffico;
import org.openspcoop2.pdd.core.controllo_traffico.policy.InterceptorPolicyUtilities;
import org.openspcoop2.pdd.core.controllo_traffico.policy.PolicyFiltroApplicativoUtilities;
import org.openspcoop2.pdd.core.controllo_traffico.policy.PolicyVerifier;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.GestorePolicyAttive;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.InRequestProtocolContext;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.pdd.core.transazioni.TransactionDeletedException;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.utils.Utilities;
import org.slf4j.Logger;

/**     
 * InRequestProtocolHandler_GestioneControlloTraffico
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InRequestProtocolHandler_GestioneControlloTraffico {

	
	public void process(InRequestProtocolContext context, Transaction tr) throws HandlerException{
		
		ElencoIdPolicyAttive idActivePolicyFiltratoPerPorta = null;
	
		DatiTransazione datiTransazione = null;
		
		IGestorePolicyAttive gestorePolicy = null;
		GestoreControlloTraffico gestoreControlloTraffico = null;
		ConfigurazionePdDManager configPdDManager = null;
		
		TipoErrore tipoErrore = TipoErrore.FAULT;
		boolean includiDescrizioneErrore = true;
		
		ConfigurazioneControlloTraffico ConfigurazioneControlloTraffico = null;
		
		Logger log = null;
		
		boolean registerThread = true;
		OpenSPCoop2Properties op2Properties = null;
		try{
			
			// ConfigurazioneControlloTraffico
			op2Properties = OpenSPCoop2Properties.getInstance();
			ConfigurazioneControlloTraffico =  op2Properties.getConfigurazioneControlloTraffico();
			
			// Logger
			log = OpenSPCoop2Logger.getLoggerOpenSPCoopControlloTraffico(op2Properties.isControlloTrafficoDebug());
			
			// Leggo i dati del servizio
			datiTransazione = InterceptorPolicyUtilities.readDatiTransazione(context);
			registerThread = InterceptorPolicyUtilities.checkRegisterThread(datiTransazione);
			// Se registerThread == false, sicuramente la richiesta non verra' veicolata fuori e terminerà con un errore all'interno della PdD.

			if(registerThread){				
											
				// Prelevo la configurazione del Controllo del Traffico per quanto concerne le policy attive
				configPdDManager = ConfigurazionePdDManager.getInstance();
				ElencoIdPolicyAttive idActivePolicy = null;
				try {
					idActivePolicy = configPdDManager.getElencoIdPolicyAttive(ConfigurazioneControlloTraffico.isPolicyReadedWithDynamicCache());
				}catch(DriverConfigurazioneNotFound notFound) {}

				// Gestore delle Policy
				gestorePolicy = GestorePolicyAttive.getInstance();
				
				// Gestore del Traffico
				gestoreControlloTraffico = GestoreControlloTraffico.getInstance();
				
				// Configurazione Generale del RateLimiting
				ConfigurazioneGenerale configurazioneGenerale = configPdDManager.getConfigurazioneControlloTraffico();		
				if(configurazioneGenerale.getRateLimiting()!=null){
					if(configurazioneGenerale.getRateLimiting().getTipoErrore()!=null) {
						TipoErrore tipoErrorTmp = TipoErrore.toEnumConstant(configurazioneGenerale.getRateLimiting().getTipoErrore());
						if(tipoErrorTmp!=null) {
							tipoErrore = tipoErrorTmp;
						}
					}
					includiDescrizioneErrore = configurazioneGenerale.getRateLimiting().isTipoErroreIncludiDescrizione();
				}
				
				// Conservo tutte le policy attive che non riguardano alcuna porta specifica e solamente quelle attive per questa porta
				// Le altre non ha senso vederle poichè sono di poco interesse per la porta invocata, essendo sempre filtrate
				idActivePolicyFiltratoPerPorta = new ElencoIdPolicyAttive();
				if(idActivePolicy!=null && idActivePolicy.sizeIdActivePolicyList()>0){
					for (IdActivePolicy idActive : idActivePolicy.getIdActivePolicyList()) {
						if(idActive.getFiltroNomePorta()==null) {
							idActivePolicyFiltratoPerPorta.addIdActivePolicy(idActive);
						}
						else {
							if(datiTransazione.getNomePorta().equals(idActive.getFiltroNomePorta())) {
								// verifico che sia anche stessa tipologia fruizione o erogazione
								if(idActive.getFiltroRuoloPorta()==null || RuoloPolicy.ENTRAMBI.equals(idActive.getFiltroRuoloPorta()) ||
										idActive.getFiltroRuoloPorta().getValue().equals(context.getTipoPorta().getTipo())	) {
									idActivePolicyFiltratoPerPorta.addIdActivePolicy(idActive);
								}
							}
						}
					}
				}
				
			}
			
			//System.out.println("PROCESS registerThread["+registerThread+"] datiControlloTraffico["+(datiControlloTraffico!=null)+"]");
			
		}catch(Exception e){
			throw new HandlerException("Configurazione non disponibile: "+e.getMessage(), e);
		}
		
		if(registerThread){ 

			
			try{
				

				MsgDiagnostico msgDiag = this.buildMsgDiagnostico(context);
				
				// Verifico se la Pdd è congestionata ed emetto l'opportuno diagnostico
				boolean pddCongestionata = 
						isCondizioneCongestionamentoPortaDominio(context, gestoreControlloTraffico, msgDiag, tr);
				
				
				// Effettuo controllo di rate limiting
				if(idActivePolicyFiltratoPerPorta!=null && idActivePolicyFiltratoPerPorta.sizeIdActivePolicyList()>0){
									
					// Emetto Diagnostico  Controllo di RateLimiting
					msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_NUMERO_POLICY, idActivePolicyFiltratoPerPorta.sizeIdActivePolicyList()+"");
					context.getPddContext().addObject(GeneratoreMessaggiErrore.TEMPLATE_NUMERO_POLICY, idActivePolicyFiltratoPerPorta.sizeIdActivePolicyList());
					msgDiag.logPersonalizzato(GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_POLICY_CONTROLLO_IN_CORSO);
					
					int policyDisabilitate = 0;
					int policyFiltrate = 0;
					int policyNonApplicabili = 0;
					int policyRispettate = 0;
					int policyViolate = 0;
					int policyViolateWarningOnly = 0;
					int policyInErrore = 0;
					
					List<RisultatoVerificaPolicy> policyBloccanti = new ArrayList<RisultatoVerificaPolicy>();
					
					List<String> pddContext_uniqueIdsPolicy = new ArrayList<String>();
					List<IDUnivocoGroupByPolicy> pddContext_idUnivociGroupBy = new ArrayList<IDUnivocoGroupByPolicy>();
					List<Boolean> pddContext_policyApplicabile = new ArrayList<Boolean>();
					
					DatiTempiRisposta datiTempiRisposta = null;
					
					List<RisultatoVerificaPolicy> risultatiVerificaPolicyViolate = new ArrayList<>();
					Hashtable<TipoRisorsa,List<RisultatoVerificaPolicy>> risultatiVerificaPolicyRispettate = new Hashtable<>();
					for (TipoRisorsa tipoRisorsa : TipoRisorsa.values()) {
						risultatiVerificaPolicyRispettate.put(tipoRisorsa, new ArrayList<>());
					}
					
					try{
											
						for (IdActivePolicy idActive : idActivePolicyFiltratoPerPorta.getIdActivePolicyList()) {
							
							msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_POLICY_ACTIVE_ID, idActive.getNome());
													
							try{
							
								if(idActive.isEnabled()){
								
									// Prelevo la configurazione del Controllo del Traffico per quanto concerne le policy attive
									AttivazionePolicy attivazionePolicy = null;
									try {
										attivazionePolicy = configPdDManager.getAttivazionePolicy(ConfigurazioneControlloTraffico.isPolicyReadedWithDynamicCache(), 
											UniqueIdentifierUtilities.getUniqueId(idActive));
									}catch(DriverConfigurazioneNotFound notFound) {
										throw new Exception("Istanza di Policy con id ["+idActive.getNome()+"] non esistente: "+notFound.getMessage(),notFound);
									}
									if(attivazionePolicy==null){
										throw new Exception("Istanza di Policy con id ["+idActive.getNome()+"] non esistente?");
									}
									
									// Verifico se un eventuale filtro configurato per la policy matcha con i dati della transazione
									boolean matchFiltro = InterceptorPolicyUtilities.filter(attivazionePolicy.getFiltro(), datiTransazione);
									if(matchFiltro){
										
										if(attivazionePolicy.getFiltro().isEnabled() && attivazionePolicy.getFiltro().isInformazioneApplicativaEnabled()){
										
											// Verifico Filtro Applicativo
											String valorePresente = PolicyFiltroApplicativoUtilities.getValore(log,
													attivazionePolicy.getFiltro().getInformazioneApplicativaTipo(), 
													attivazionePolicy.getFiltro().getInformazioneApplicativaNome(), 
													context, datiTransazione, true);
											if(valorePresente==null || valorePresente.equals(attivazionePolicy.getFiltro().getInformazioneApplicativaValore())==false){
												
												policyFiltrate++;
												
												// Emetto Diagnostico Policy Filtrata
												if(valorePresente==null){
													valorePresente = "n.d.";
												}
												msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_POLICY_FILTRATA_MOTIVO, "Filtro Applicativo atteso ["+
														attivazionePolicy.getFiltro().getInformazioneApplicativaValore()+"] differente da quello estratto dalla transazione ["+
														valorePresente+"]");
												GeneratoreMessaggiDiagnostici.emitDiagnostic(msgDiag, 
														GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_POLICY_FILTRATA, log);
												
												matchFiltro = false;
												
											}
											
										}
										
										if(matchFiltro){
												
											// Prelevo la configurazione del Controllo del Traffico per quanto concerne le policy istanziata
											ConfigurazionePolicy configurazionePolicy = null;
											try {
												configurazionePolicy = configPdDManager.getConfigurazionePolicy(ConfigurazioneControlloTraffico.isPolicyReadedWithDynamicCache(), idActive.getIdPolicy());
											}catch(DriverConfigurazioneNotFound notFound) {
												throw new Exception("Policy con id ["+idActive.getIdPolicy()+"] non esistente: "+notFound.getMessage(),notFound);
											}
											if(configurazionePolicy==null){
												throw new Exception("Policy con id ["+idActive.getIdPolicy()+"] non esistente?");
											}
											
											org.openspcoop2.core.controllo_traffico.beans.ActivePolicy activePolicy = 
													new org.openspcoop2.core.controllo_traffico.beans.ActivePolicy();
											activePolicy.setInstanceConfiguration(attivazionePolicy);
											activePolicy.setConfigurazionePolicy(configurazionePolicy);
											activePolicy.setTipoRisorsaPolicy(TipoRisorsa.toEnumConstant(configurazionePolicy.getRisorsa(), true));
											activePolicy.setConfigurazioneControlloTraffico(ConfigurazioneControlloTraffico);
											
											
											// Creo Gruppo
											IDUnivocoGroupByPolicy idUnivocoGroupBy = InterceptorPolicyUtilities.convertToID(log,
													datiTransazione, attivazionePolicy.getGroupBy(), context);
											pddContext_idUnivociGroupBy.add(idUnivocoGroupBy);
											pddContext_uniqueIdsPolicy.add(UniqueIdentifierUtilities.getUniqueId(attivazionePolicy));
											
											// Se la Policy richiede una condizione di applicabilità per degrado prestazionale
											// e non ho ancora recuperato l'informazione sui tempi lo faccio adesso
											if(datiTempiRisposta==null){
												datiTempiRisposta = ConnettoreUtilities.getDatiTempiRisposta(context, datiTransazione);
											}
																						
											// Verifico rispetto ai dati collezionati
											RisultatoVerificaPolicy risultatoVerifica = PolicyVerifier.verifica(
													gestorePolicy, log,
													activePolicy,
													idUnivocoGroupBy, context.getPddContext(), 
													msgDiag, tr, datiTransazione, pddCongestionata, datiTempiRisposta,
													pddContext_policyApplicabile);
											
											// Gestisco Risultato Verifica
											if(risultatoVerifica.isErroreGenerico()){
												
												policyInErrore++;
												
												// Emetto Diagnostico Policy in Errore
												log.error("Errore durante il controllo della policy con id["+idActive.getNome()+"]: "+risultatoVerifica.getDescrizione());
												msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_POLICY_VIOLATA_MOTIVO, risultatoVerifica.getDescrizione());
												GeneratoreMessaggiDiagnostici.emitDiagnostic(msgDiag, 
														GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_POLICY_IN_ERRORE, log);
												
												// Aggiungo tra le policy bloccanti
												policyBloccanti.add(risultatoVerifica);
												
											}
											else if(risultatoVerifica.isNonApplicabile()){
												
												policyNonApplicabili++;
												
												// Emetto Diagnostico Policy  Non Applicabile
												msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_POLICY_NON_APPLICABILE_MOTIVO, risultatoVerifica.getDescrizione());
												GeneratoreMessaggiDiagnostici.emitDiagnostic(msgDiag, 
														GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_POLICY_NON_APPLICABILE, log);
												
											}
											else if(risultatoVerifica.isViolata()){
											
												risultatiVerificaPolicyViolate.add(risultatoVerifica);
												
												if(risultatoVerifica.isWarningOnly()){
	
													policyViolateWarningOnly++;
													
													// Emetto Diagnostico Policy Violata in Warning Only
													msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_POLICY_VIOLATA_MOTIVO, risultatoVerifica.getDescrizione());
													GeneratoreMessaggiDiagnostici.emitDiagnostic(msgDiag, 
															GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_POLICY_VIOLATA_WARNING_ONLY, log);
													
												}
												else{
												
													policyViolate++;
													
													// Emetto Diagnostico Policy Violata
													msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_POLICY_VIOLATA_MOTIVO, risultatoVerifica.getDescrizione());
													GeneratoreMessaggiDiagnostici.emitDiagnostic(msgDiag, 
															GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_POLICY_VIOLATA, log);
													
													// Aggiungo tra le policy bloccanti
													policyBloccanti.add(risultatoVerifica);
													
												}
											}
											else{
												
												risultatiVerificaPolicyRispettate.get(risultatoVerifica.getRisorsa()).add(risultatoVerifica);
												
												// Emetto Diagnostico Policy Rispettata
												GeneratoreMessaggiDiagnostici.emitDiagnostic(msgDiag, 
														GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_POLICY_RISPETTATA, log);
												
												policyRispettate++;
												
											}
											
										}
										
									}
									else{
										
										policyFiltrate++;
										
										// Emetto Diagnostico Policy Filtrata
										msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_POLICY_FILTRATA_MOTIVO, PolicyUtilities.toStringFilter(attivazionePolicy.getFiltro()));
										GeneratoreMessaggiDiagnostici.emitDiagnostic(msgDiag, 
												GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_POLICY_FILTRATA, log);
										
									}
									
								}
								else{
									
									policyDisabilitate++;
									
									// Emetto Diagnostico Policy Disabilitata
									GeneratoreMessaggiDiagnostici.emitDiagnostic(msgDiag, 
											GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_POLICY_DISABILITATA, log);
									
								}
								
							}
							catch(Throwable e){
								
								policyInErrore++;
								
								// Messaggio di Errore
								Throwable tConMessaggio = Utilities.getInnerNotEmptyMessageException(e);
								String eMessaggio = tConMessaggio.getMessage();
								if(eMessaggio==null || "".equals(eMessaggio) || "null".equalsIgnoreCase(eMessaggio)){
									eMessaggio = "Internal Error"; // molto probabilmente si tratta di un null Pointer. Lo si vedra' nel logger
								}
								
								// Emetto Diagnostico Policy in Errore
								log.error("Errore durante il controllo della policy con id["+idActive.getNome()+"]: "+eMessaggio,e);
								msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_POLICY_VIOLATA_MOTIVO, eMessaggio);
								GeneratoreMessaggiDiagnostici.emitDiagnostic(msgDiag, 
										GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_POLICY_IN_ERRORE, log);
								
								// Aggiungo tra le policy bloccanti
								RisultatoVerificaPolicy policyError = new RisultatoVerificaPolicy();
								policyError.setErroreGenerico(true);
								policyError.setDescrizione(eMessaggio);
								policyBloccanti.add(policyError);
								
							}
							
							// Effettuo clean dei dati dinamici della policy
							GeneratoreMessaggiDiagnostici.cleanPolicyValues(msgDiag);
							
							
						} // -- fine for delle policy
						
					}finally{
						if(pddContext_idUnivociGroupBy.size()>0){
							context.getPddContext().addObject(CostantiControlloTraffico.PDD_CONTEXT_LIST_GROUP_BY_CONDITION, pddContext_idUnivociGroupBy);
							context.getPddContext().addObject(CostantiControlloTraffico.PDD_CONTEXT_LIST_UNIQUE_ID_POLICY,pddContext_uniqueIdsPolicy);
							context.getPddContext().addObject(CostantiControlloTraffico.PDD_CONTEXT_LIST_POLICY_APPLICABILE,pddContext_policyApplicabile);
						}
					}

					// Gestione header HTTP da aggiungere alla transazione
					
					Properties headerTrasportoRateLimiting = new Properties();
					if(risultatiVerificaPolicyViolate.size()>0) {
						// calcolo tempo maggiore
						long ms = -1;
						Hashtable<TipoRisorsa,List<RisultatoVerificaPolicy>> risultatiVerificaPolicyViolateMap = new Hashtable<>();
						for (RisultatoVerificaPolicy risultatoVerificaPolicy : risultatiVerificaPolicyViolate) {
							
							List<RisultatoVerificaPolicy> l = null;
							if(risultatiVerificaPolicyViolateMap.containsKey(risultatoVerificaPolicy.getRisorsa())) {
								l = risultatiVerificaPolicyViolateMap.get(risultatoVerificaPolicy.getRisorsa());
							}
							else {
								l = new ArrayList<>();
								risultatiVerificaPolicyViolateMap.put(risultatoVerificaPolicy.getRisorsa(), l);
							}
							l.add(risultatoVerificaPolicy);
							
							if(risultatoVerificaPolicy.getMsBeforeResetCounters()!=null) {
								long tmp = risultatoVerificaPolicy.getMsBeforeResetCounters().longValue();
								if(tmp>ms) {
									ms = tmp;
								}
							}
						}
						if(ms>1000) {
							// trasformo in secondi
							long sec = ms / 1000;
							String [] headers = op2Properties.getControlloTrafficoRetryAfterHeader();
							if(headers!=null && headers.length>0) {
								for (String header : headers) {
									headerTrasportoRateLimiting.put(header, sec+"");
								}
							}
						}
						if(risultatiVerificaPolicyViolateMap.size()>0) {
							// produco anche i normali header
							gestioneHeaderRateLimit(risultatiVerificaPolicyViolateMap, headerTrasportoRateLimiting, op2Properties);
						}
					}
					else if(risultatiVerificaPolicyRispettate.size()>0) {
						gestioneHeaderRateLimit(risultatiVerificaPolicyRispettate, headerTrasportoRateLimiting, op2Properties);
					}
						
					//Aggiungo nel pddContext e poi li recupero nei servizi
					if(headerTrasportoRateLimiting.size()>0) {
						context.getPddContext().addObject(CostantiControlloTraffico.PDD_CONTEXT_HEADER_RATE_LIMITING,headerTrasportoRateLimiting);
					}
					
					
					// Emetto Diagnostico Terminazione Controllo Rate limiting
					
					msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_NUMERO_POLICY_DISABILITATE, policyDisabilitate+"");
					context.getPddContext().addObject(GeneratoreMessaggiErrore.TEMPLATE_NUMERO_POLICY_DISABILITATE, policyDisabilitate);
					
					msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_NUMERO_POLICY_FILTRATE, policyFiltrate+"");
					context.getPddContext().addObject(GeneratoreMessaggiErrore.TEMPLATE_NUMERO_POLICY_FILTRATE, policyFiltrate);
					
					msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_NUMERO_POLICY_NON_APPLICATE, policyNonApplicabili+"");
					context.getPddContext().addObject(GeneratoreMessaggiErrore.TEMPLATE_NUMERO_POLICY_NON_APPLICATE, policyNonApplicabili);
					
					msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_NUMERO_POLICY_RISPETTATE, policyRispettate+"");
					context.getPddContext().addObject(GeneratoreMessaggiErrore.TEMPLATE_NUMERO_POLICY_RISPETTATE, policyRispettate);
					
					msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_NUMERO_POLICY_VIOLATE, policyViolate+"");
					context.getPddContext().addObject(GeneratoreMessaggiErrore.TEMPLATE_NUMERO_POLICY_VIOLATE, policyViolate);
					
					msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_NUMERO_POLICY_VIOLATE_WARNING_ONLY, policyViolateWarningOnly+"");
					context.getPddContext().addObject(GeneratoreMessaggiErrore.TEMPLATE_NUMERO_POLICY_VIOLATE_WARNING_ONLY, policyViolateWarningOnly);
					
					msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_NUMERO_POLICY_IN_ERRORE, policyInErrore+"");
					context.getPddContext().addObject(GeneratoreMessaggiErrore.TEMPLATE_NUMERO_POLICY_IN_ERRORE, policyInErrore);
					
					if(policyViolate>0 || policyInErrore>0){
						msgDiag.logPersonalizzato(GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_POLICY_CONTROLLO_TERMINATO_CON_ERRORE);
						
						GeneratoreMessaggiErrore.addPddContextInfo_ControlloTrafficoPolicyViolated(context.getPddContext(), false);
						
						HandlerException he = GeneratoreMessaggiErrore.getControlloTrafficoPolicyViolated(policyBloccanti,
								ConfigurazioneControlloTraffico.isErroreGenerico(), context.getPddContext());
						he.setEmettiDiagnostico(false);
						GeneratoreMessaggiErrore.configureHandlerExceptionByTipoErrore(he, tipoErrore, includiDescrizioneErrore);
						throw he;
					}
					else if(policyViolateWarningOnly>0){
						
						GeneratoreMessaggiErrore.addPddContextInfo_ControlloTrafficoPolicyViolated(context.getPddContext(), true);
						
						msgDiag.logPersonalizzato(GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_POLICY_CONTROLLO_TERMINATO_CON_SUCCESSO);
						
					}
					else{
						msgDiag.logPersonalizzato(GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_POLICY_CONTROLLO_TERMINATO_CON_SUCCESSO);
					}
				}
					
			}catch(Exception e){
							
				if(e instanceof HandlerException){
					throw (HandlerException)e;
				}
				else{
					
					GeneratoreMessaggiErrore.addPddContextInfo_ControlloTrafficoGenericError(context.getPddContext());
					
					throw GeneratoreMessaggiErrore.getErroreProcessamento(e, context.getPddContext());
				}
			}
			
		}

	}
	
	private void gestioneHeaderRateLimit(Hashtable<TipoRisorsa,List<RisultatoVerificaPolicy>> risultatiVerificaPolicy, Properties headerTrasportoRateLimiting,
			OpenSPCoop2Properties op2Properties) {
		Enumeration<TipoRisorsa> tipiRisorsaEnum = risultatiVerificaPolicy.keys();
		while (tipiRisorsaEnum.hasMoreElements()) {
			TipoRisorsa tipoRisorsa = (TipoRisorsa) tipiRisorsaEnum.nextElement();
			List<RisultatoVerificaPolicy> risultato = risultatiVerificaPolicy.get(tipoRisorsa);
			// in presenza di piu' policy dello stesso tipo considero la policy che termina più lontano nel tempo: NO Sbagliato poiche' se una ha un numero minore non va bene.
			// Prendo a questo punto quella con il minore limite.
			
			RisultatoVerificaPolicy risultatoUtilizzato = null;
			RisultatoVerificaPolicy risultatoUtilizzatoSimultanee = null;
			
			for (RisultatoVerificaPolicy risultatoVerificaPolicy : risultato) {
				if(TipoRisorsa.NUMERO_RICHIESTE.equals(tipoRisorsa) && risultatoVerificaPolicy.isSimultanee()) {
					if(risultatoUtilizzatoSimultanee==null) {
						risultatoUtilizzatoSimultanee = risultatoVerificaPolicy;
					}
					else {
						long numeroRimasti = -1;
						if(risultatoUtilizzatoSimultanee.getMaxValue()!=null) {
							numeroRimasti = risultatoUtilizzatoSimultanee.getMaxValue().longValue();
						}
						long numeroCheck = -1;
						if(risultatoVerificaPolicy.getMaxValue()!=null) {
							numeroCheck = risultatoVerificaPolicy.getMaxValue().longValue();
						}
						if(numeroCheck>0) {
							if(numeroRimasti>0) {
								if(numeroCheck<numeroRimasti) {
									risultatoUtilizzatoSimultanee = risultatoVerificaPolicy;
								}
							}
							else {
								risultatoUtilizzatoSimultanee = risultatoVerificaPolicy;
							}
						}
					}
				}
				else {
					if(risultatoUtilizzato==null) {
						risultatoUtilizzato = risultatoVerificaPolicy;
					}
					else {
						long numeroRimasti = -1;
						if(risultatoUtilizzato.getMaxValue()!=null) {
							numeroRimasti = risultatoUtilizzato.getMaxValue().longValue();
						}
						long numeroCheck = -1;
						if(risultatoVerificaPolicy.getMaxValue()!=null) {
							numeroCheck = risultatoVerificaPolicy.getMaxValue().longValue();
						}
						if(numeroCheck>0) {
							if(numeroRimasti>0) {
								if(numeroCheck<numeroRimasti) {
									risultatoUtilizzato = risultatoVerificaPolicy;
								}
							}
							else {
								risultatoUtilizzato = risultatoVerificaPolicy;
							}
						}
					}
				}
			}
			
			if(risultatoUtilizzatoSimultanee!=null) {
				if(risultatoUtilizzatoSimultanee.getMaxValue()!=null) {
					try {
						String [] headers = op2Properties.getControlloTrafficoNumeroRichiesteSimultaneeHeaderLimit();
						if(headers!=null && headers.length>0) {
							for (String header : headers) {
								headerTrasportoRateLimiting.put(header, risultatoUtilizzatoSimultanee.getMaxValue().longValue()+"");
							}
						}
					}catch(Exception e) { 
						// errore non dovrebbe succedere
					}
					
					if(risultatoUtilizzatoSimultanee.getActualValue()!=null) {
						long rimanenti = risultatoUtilizzatoSimultanee.getMaxValue().longValue() - risultatoUtilizzatoSimultanee.getActualValue().longValue();
						if(rimanenti<0) {  // gli attuali conteggia anche quelle bloccate
							rimanenti = 0;
						}
						try {
							String [] headers = op2Properties.getControlloTrafficoNumeroRichiesteSimultaneeHeaderRemaining();
							if(headers!=null && headers.length>0) {
								for (String header : headers) {
									headerTrasportoRateLimiting.put(header, rimanenti+"");
								}
							}
						}catch(Exception e) { 
							// errore non dovrebbe succedere
						}
					}
				}
			}
			
			if(risultatoUtilizzato!=null) {
				if(risultatoUtilizzato.getMaxValue()!=null) {
					try {
						String [] headers = null;
						switch (tipoRisorsa) {
						case NUMERO_RICHIESTE:
							headers = op2Properties.getControlloTrafficoNumeroRichiesteHeaderLimit();
							break;
						case OCCUPAZIONE_BANDA:
							headers = op2Properties.getControlloTrafficoOccupazioneBandaHeaderLimit();
							break;
						case TEMPO_MEDIO_RISPOSTA:
							headers = op2Properties.getControlloTrafficoTempoMedioRispostaHeaderLimit();
							break;
						case TEMPO_COMPLESSIVO_RISPOSTA:
							headers = op2Properties.getControlloTrafficoTempoComplessivoRispostaHeaderLimit();
							break;
						case NUMERO_RICHIESTE_COMPLETATE_CON_SUCCESSO:
							headers = op2Properties.getControlloTrafficoNumeroRichiesteCompletateConSuccessoHeaderLimit();
							break;
						case NUMERO_RICHIESTE_FALLITE:
							headers = op2Properties.getControlloTrafficoNumeroRichiesteFalliteHeaderLimit();
							break;
						case NUMERO_FAULT_APPLICATIVI:
							headers = op2Properties.getControlloTrafficoNumeroFaultApplicativiHeaderLimit();
							break;
						}
						
						if(headers!=null && headers.length>0) {
							for (String header : headers) {
								headerTrasportoRateLimiting.put(header, risultatoUtilizzato.getMaxValue().longValue()+"");
							}
						}
					}catch(Exception e) { 
						// errore non dovrebbe succedere
					}
					
					if(risultatoUtilizzato.getActualValue()!=null) {
						long rimanenti = risultatoUtilizzato.getMaxValue().longValue() - risultatoUtilizzato.getActualValue().longValue();
						if(rimanenti<0) {  // gli attuali conteggia anche quelle bloccate
							rimanenti = 0;
						}
						try {
							String [] headers = null;
							switch (tipoRisorsa) {
							case NUMERO_RICHIESTE:
								headers = op2Properties.getControlloTrafficoNumeroRichiesteHeaderRemaining();
								break;
							case OCCUPAZIONE_BANDA:
								headers = op2Properties.getControlloTrafficoOccupazioneBandaHeaderRemaining();
								break;
							case TEMPO_MEDIO_RISPOSTA:
								// non ha senso
								//headers = op2Properties.getControlloTrafficoTempoMedioRispostaHeaderRemaining();
								break;
							case TEMPO_COMPLESSIVO_RISPOSTA:
								headers = op2Properties.getControlloTrafficoTempoComplessivoRispostaHeaderRemaining();
								break;
							case NUMERO_RICHIESTE_COMPLETATE_CON_SUCCESSO:
								headers = op2Properties.getControlloTrafficoNumeroRichiesteCompletateConSuccessoHeaderRemaining();
								break;
							case NUMERO_RICHIESTE_FALLITE:
								headers = op2Properties.getControlloTrafficoNumeroRichiesteFalliteHeaderRemaining();
								break;
							case NUMERO_FAULT_APPLICATIVI:
								headers = op2Properties.getControlloTrafficoNumeroFaultApplicativiHeaderRemaining();
								break;
							}
							if(headers!=null && headers.length>0) {
								for (String header : headers) {
									headerTrasportoRateLimiting.put(header, rimanenti+"");
								}
							}
						}catch(Exception e) { 
							// errore non dovrebbe succedere
						}
					}
				}
				if(risultatoUtilizzato.getMsBeforeResetCounters()!=null) {
					
					long ms = risultatoUtilizzato.getMsBeforeResetCounters().longValue();
					long sec = -1;
					if(ms>1000) {
						// trasformo in secondi
						sec = ms / 1000;
					}
					
					if(sec>0) {
						try {
							String [] headers = null;
							switch (tipoRisorsa) {
							case NUMERO_RICHIESTE:
								headers = op2Properties.getControlloTrafficoNumeroRichiesteHeaderReset();
								break;
							case OCCUPAZIONE_BANDA:
								headers = op2Properties.getControlloTrafficoOccupazioneBandaHeaderReset();
								break;
							case TEMPO_MEDIO_RISPOSTA:
								headers = op2Properties.getControlloTrafficoTempoMedioRispostaHeaderReset();
								break;
							case TEMPO_COMPLESSIVO_RISPOSTA:
								headers = op2Properties.getControlloTrafficoTempoComplessivoRispostaHeaderReset();
								break;
							case NUMERO_RICHIESTE_COMPLETATE_CON_SUCCESSO:
								headers = op2Properties.getControlloTrafficoNumeroRichiesteCompletateConSuccessoHeaderReset();
								break;
							case NUMERO_RICHIESTE_FALLITE:
								headers = op2Properties.getControlloTrafficoNumeroRichiesteFalliteHeaderReset();
								break;
							case NUMERO_FAULT_APPLICATIVI:
								headers = op2Properties.getControlloTrafficoNumeroFaultApplicativiHeaderReset();
								break;
							}
							
							if(headers!=null && headers.length>0) {
								for (String header : headers) {
									headerTrasportoRateLimiting.put(header, sec+"");
								}
							}
						}catch(Exception e) { 
							// errore non dovrebbe succedere
						}
					}
				}
			}
		}
		
	}

	
	private boolean isCondizioneCongestionamentoPortaDominio(InRequestProtocolContext context, 
			GestoreControlloTraffico gestoreControlloCongestione,
			MsgDiagnostico msgDiag, Transaction tr) throws TransactionDeletedException{
		
		PdDContext pddContext = context.getPddContext();
		
		boolean isPddCongestionataInformazioneIdentificataDalThread = false;
		Object oPddCongestionataThread = pddContext.getObject(CostantiControlloTraffico.PDD_CONTEXT_PDD_CONGESTIONATA);
		if(oPddCongestionataThread!=null && oPddCongestionataThread instanceof Boolean){
			isPddCongestionataInformazioneIdentificataDalThread = (Boolean) oPddCongestionataThread;
		}
		
		StatoTraffico statoControlloCongestione = gestoreControlloCongestione.getStatoControlloTraffico();
		if(statoControlloCongestione.getPddCongestionata()!=isPddCongestionataInformazioneIdentificataDalThread){
			//System.out.println("Rilevata differenza tra controllo del traffico attivo PREINHANDLER ["+controlloTrafficoAttivoThread+"] e stato attuale["+statoControlloCongestione.getControlloTraffico()+"]");
			pddContext.removeObject(CostantiControlloTraffico.PDD_CONTEXT_PDD_CONGESTIONATA);
			pddContext.addObject(CostantiControlloTraffico.PDD_CONTEXT_PDD_CONGESTIONATA, statoControlloCongestione.getPddCongestionata());
		}
		
		// Aggiungo al msgDiag i valori delle tre costanti poichè mi servirà anche per diagnostici durante la verifica delle policy.
		// Vanno aggiunti anche se la pdd non risulta congestionata
		
		msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_ACTIVE_THREADS, statoControlloCongestione.getActiveThreads()+"");
		pddContext.addObject(GeneratoreMessaggiErrore.TEMPLATE_ACTIVE_THREADS, statoControlloCongestione.getActiveThreads());
		
		Object oMaxThreads = pddContext.getObject(GeneratoreMessaggiErrore.TEMPLATE_MAX_THREADS_THRESHOLD);
		if(oMaxThreads!=null){
			msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_MAX_THREADS_THRESHOLD, oMaxThreads.toString());
		}
		
		Object oThreshold = pddContext.getObject(GeneratoreMessaggiErrore.TEMPLATE_CONTROLLO_TRAFFICO_THRESHOLD);
		if(oThreshold!=null){
			msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_CONTROLLO_TRAFFICO_THRESHOLD, oThreshold.toString());
		}
		
		if(statoControlloCongestione.getPddCongestionata()){
			// Emetto qua il diagnostico che la porta è congestionata.
			// Potrei emetterlo anche nel preInHandler, però lo emetto anzi qua perche viene poi usato qua la condizione per capire se attivare o meno una policy.
			
			
			// Aggiunto come evento della transazione l'informazione che la PdD è congestionata
			tr.addEventoGestione(TipoEvento.CONTROLLO_TRAFFICO_SOGLIA_CONGESTIONE.getValue()+"_"+CodiceEventoControlloTraffico.VIOLAZIONE.getValue());
			
			
			// Emetto Diagnostico Controllo del Traffico Attivato
			msgDiag.logPersonalizzato(GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_PDD_CONGESTIONATA);
			
		}
		
		return statoControlloCongestione.getPddCongestionata();
	}
	
	
	// Ritorna un MsgDiagnostico generator
	private MsgDiagnostico buildMsgDiagnostico(InRequestProtocolContext context) throws HandlerException{
		MsgDiagnostico msgDiag = null;
		try{
			String nomePorta = null;
			if(context.getIntegrazione()!=null) {
				if(context.getIntegrazione().getIdPA()!=null) {
					nomePorta = context.getIntegrazione().getIdPA().getNome();
				}
				else if(context.getIntegrazione().getIdPD()!=null) {
					nomePorta = context.getIntegrazione().getIdPD().getNome();
				}
			}
			msgDiag = MsgDiagnostico.newInstance(context.getTipoPorta(),context.getIdModulo(),nomePorta);
			msgDiag.setPddContext(context.getPddContext(), context.getProtocolFactory());
			if(org.openspcoop2.core.constants.TipoPdD.DELEGATA.equals(context.getTipoPorta())){
				msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI);
			}
			else{
				msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE);
			}
		}catch(Exception e){
			throw new HandlerException("Generazione Messaggio Diagnostico non riuscita: "+e.getMessage(), e);
		}
		return msgDiag;
	}
	

}
