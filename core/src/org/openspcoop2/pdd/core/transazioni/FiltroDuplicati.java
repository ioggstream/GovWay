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
package org.openspcoop2.pdd.core.transazioni;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.core.transazioni.dao.jdbc.converter.TransazioneFieldConverter;
import org.openspcoop2.pdd.config.DBTransazioniManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.Resource;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.state.OpenSPCoopState;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.engine.driver.IFiltroDuplicati;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.builder.IBustaBuilder;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**     
 * FiltroDuplicati
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FiltroDuplicati implements IFiltroDuplicati {

	private OpenSPCoop2Properties openspcoop2Properties;
	
	private String idTransazione = null;
	
	private org.openspcoop2.protocol.engine.driver.FiltroDuplicati filtroDuplicatiProtocol;
		
	private OpenSPCoopState openspcoop2State = null;
	private Boolean initDsResource = null;
	private Connection connection = null;
	private boolean isDirectConnection = false;
	@SuppressWarnings("unused")
	private String modeGetConnection = null;
	private String tipoDatabaseRuntime = null; 
	private Logger log = null;
	private Logger logSql = null;
	private boolean debug = false;
	
	private TransazioneFieldConverter transazioneFieldConverter = null;
	private String nomeTabellaTransazioni = null;
	private String colonna_data_id_msg_richiesta = null;
	private String colonna_id_messaggio_richiesta = null;
	private String colonna_pdd_ruolo = null;
	private String colonna_data_id_msg_risposta = null;
	private String colonna_id_messaggio_risposta = null;
	
	private static final String ID_MODULO = "FiltroDuplicati";
	

	@Override
	public void init(Object context) throws ProtocolException{
		
		this.openspcoop2Properties = OpenSPCoop2Properties.getInstance();
		
		if(this.openspcoop2Properties.isTransazioniFiltroDuplicatiTramiteTransazioniEnabled() == false) {
			this.filtroDuplicatiProtocol = new org.openspcoop2.protocol.engine.driver.FiltroDuplicati();
			this.filtroDuplicatiProtocol.init(context);
		}
		else {
			this.idTransazione = (String) ((PdDContext)context).getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
			if(this.idTransazione==null){
				throw new ProtocolException("Id di transazione non fornito");
			}
			
			if(this.initDsResource==null && this.connection==null){
				this.init((PdDContext)context);
			}
		}
		
	}
	
	@Override
	public boolean isDuplicata(IProtocolFactory<?> protocolFactory, String idBusta) throws ProtocolException {
		
		if(this.filtroDuplicatiProtocol!=null) {
			return this.filtroDuplicatiProtocol.isDuplicata(protocolFactory, idBusta);
		}
		
		//System.out.println("@@IS_DUPLICATA ["+idBusta+"] ...");
		
		// E' duplicata se esiste nel contesto una transazione con tale idBusta 
		if(TransactionContext.containsIdentificativoProtocollo(idBusta)){
			//System.out.println("@@IS_DUPLICATA ["+idBusta+"] TRUE (CONTEXT)");
			try{
				TransactionContext.getTransaction(this.idTransazione).addIdProtocolloDuplicato(idBusta);
			}catch(Exception e){
				new ProtocolException(e);
			}
			return true;
		}
		
		// oppure se esiste una transazione registrata con tale idBusta sul database (Richiesta o Risposta).
		//System.out.println("@@IS_DUPLICATA ["+idBusta+"] READ FROM DB");
		if(esisteTransazione(protocolFactory,idBusta,idBusta)){
			//System.out.println("@@IS_DUPLICATA ["+idBusta+"] TRUE (DATABASE)");
			try{
				TransactionContext.getTransaction(this.idTransazione).addIdProtocolloDuplicato(idBusta);
			}catch(Exception e){
				new ProtocolException(e);
			}
			return true;
		}
		
		// Se non esiste registro nel contesto questa transazione.
		// Comunque OpenSPCoop se torno false, procedera a chiamare registraBusta, il quale metodo non fara' nulla (vedi implementazione sotto stante)
		// Il metodo di registrazione dell'identificativo busta e' synchronized e controlla che non sia possibile registrare due identificativi busta.
		// Tale implementazione garantisce che nel contesto puo' esistere solo un idBusta, e tale id viene eliminato SOLO dopo 
		// aver salvato la transazione nel database (vedi implementazione PostOutResponseHandler, metodo removeIdentificativoProtocollo)
		// Se dopo aver eliminato dal contesto l'id, arriva una nuova busta con stesso id, questo metodo la trova nella tabelle delle transazioni 
		// e quindi ritornera' immediatamente l'informazione di busta duplicata.
		try{
			//System.out.println("@@IS_DUPLICATA ["+idBusta+"] FALSE ....");
			TransactionContext.registraIdentificativoProtocollo(idBusta);
			//System.out.println("@@IS_DUPLICATA ["+idBusta+"] FALSE REGISTRATA");
		}catch(Exception e){
			if(e.getMessage()!=null && "DUPLICATA".equals(e.getMessage())){
				//System.out.println("@@IS_DUPLICATA ["+idBusta+"] TRUE (ERRORE ECCEZIONE)");
				try{
					TransactionContext.getTransaction(this.idTransazione).addIdProtocolloDuplicato(idBusta);
				}catch(Exception eSetDuplicata){
					new ProtocolException(eSetDuplicata);
				}
				return true;
			}else{
				throw new ProtocolException(e);
			}
		}
		
		//System.out.println("@@IS_DUPLICATA ["+idBusta+"] FALSE FINE");
		return false;
	}

	@Override
	public void incrementaNumeroDuplicati(IProtocolFactory<?> protocolFactory, String idBusta) throws ProtocolException {
		
		if(this.filtroDuplicatiProtocol!=null) {
			this.filtroDuplicatiProtocol.incrementaNumeroDuplicati(protocolFactory, idBusta);
			return;
		}
		
		//System.out.println("@@incrementaNumeroDuplicati ["+idBusta+"] ...");
		
		// Aggiorno numero duplicati per transazione che possiede tale idBusta (Richiesta o Risposta)
		// Se non esiste una transazione sul database, devo attendere che questa compaia,
		// significa che una precedente transazione con stesso idBusta e' ancora in gestione
		
		boolean esisteRichiesta = false;
		boolean esisteRisposta = false;
		esisteRichiesta = esisteTransazione(protocolFactory,idBusta,null);
		if(!esisteRichiesta){
			esisteRisposta = esisteTransazione(protocolFactory,null,idBusta);
		}
		//System.out.println("@@incrementaNumeroDuplicati richiesta["+esisteRichiesta+"] risposta["+esisteRisposta+"] ...");
		
		int i=0;
		while(!esisteRichiesta && !esisteRisposta && i<60){
			//System.out.println("@@incrementaNumeroDuplicati WHILE richiesta["+esisteRichiesta+"] risposta["+esisteRisposta+"]  SLEEP ...");
			// ATTENDI
			org.openspcoop2.utils.Utilities.sleep(1000);
			i++;
			esisteRichiesta = esisteTransazione(protocolFactory,idBusta,null);
			if(!esisteRichiesta){
				esisteRisposta = esisteTransazione(protocolFactory,null,idBusta);
			}
			//System.out.println("@@incrementaNumeroDuplicati WHILE richiesta["+esisteRichiesta+"] risposta["+esisteRisposta+"]  SLEEP FINE ...");
		}
		
		if(esisteRichiesta){
			incrementDuplicatiTransazione(protocolFactory,true, idBusta);
		}
		else if(esisteRisposta){
			incrementDuplicatiTransazione(protocolFactory,false, idBusta);
		}
		else{
			throw new ProtocolException("Precedente transazione con solito idBusta risulta in gestione da oltre 60 secondi");
		}
		
		//System.out.println("@@incrementaNumeroDuplicati richiesta["+esisteRichiesta+"] risposta["+esisteRisposta+"] FINE");
	}

	@Override
	public void registraBusta(IProtocolFactory<?> protocolFactory, Busta busta) throws ProtocolException {
		
		if(this.filtroDuplicatiProtocol!=null) {
			this.filtroDuplicatiProtocol.registraBusta(protocolFactory, busta);
			return;
		}
		
		// Implementazione inserita in isDuplicata
		//System.out.println("@@registraBusta ["+busta.getID()+"] NON IMPLEMENTATO");
	}

	
	
	/* **** METODI INTERNI **** */
	
	private synchronized void init(PdDContext pddContext) throws ProtocolException {

		//if(this.dsRuntime==null && this.connection==null){
		if(this.initDsResource==null && this.connection==null){
			
			try{
				// Debug
				this.debug = this.openspcoop2Properties.isTransazioniDebug();
				this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopTransazioni(this.debug);
				this.logSql = OpenSPCoop2Logger.getLoggerOpenSPCoopTransazioniSql(this.debug);
			}catch(Exception e){
				throw new ProtocolException("Errore durante l'inizializzazione del logger: "+e.getMessage(),e);
			}
			
			try{
				
				// TipoDatabase
				this.tipoDatabaseRuntime = this.openspcoop2Properties.getDatabaseType();
				if(this.tipoDatabaseRuntime==null){
					throw new Exception("Tipo Database non definito");
				}
				
				// DB Resource
				Object openspcoopstate = pddContext.getObject(org.openspcoop2.core.constants.Costanti.OPENSPCOOP_STATE);
				if(openspcoopstate!=null) {
					this.openspcoop2State = (OpenSPCoopState) openspcoopstate;
				}
				if(this.openspcoop2State!=null && this.openspcoop2Properties.isTransazioniFiltroDuplicatiTramiteTransazioniUsePdDConnection()){
					//System.out.println("[FILTRO] INIZIALIZZO CONNESSIONE");
					this.connection = this.openspcoop2State.getConnectionDB();
					//this.datasourceRuntime = "DirectConnection";
					this.modeGetConnection = "DirectConnection";
					this.isDirectConnection = true;
				}
				else{
					//System.out.println("[FILTRO] INIZIALIZZO DS");
//					this.datasourceRuntime = PddInterceptorConfig.getDataSource();
//					if(this.datasourceRuntime==null){
//						throw new Exception("Datasource non definito");
//					}
					this.initDsResource = true;
					this.modeGetConnection = "DatasourceRuntime";
					
					// Inizializzazione datasource
//					GestoreJNDI jndi = new GestoreJNDI();
//					this.dsRuntime = (DataSource) jndi.lookup(this.datasourceRuntime);
				}
								
				//System.out.println("DS["+this.datasource+"] TIPODB["+this.tipoDatabase+"]");
								
				this.transazioneFieldConverter = new TransazioneFieldConverter(this.tipoDatabaseRuntime);
				
				this.nomeTabellaTransazioni = this.transazioneFieldConverter.toTable(Transazione.model());
				this.colonna_data_id_msg_richiesta = this.transazioneFieldConverter.toColumn(Transazione.model().DATA_ID_MSG_RICHIESTA, false);
				this.colonna_id_messaggio_richiesta = this.transazioneFieldConverter.toColumn(Transazione.model().ID_MESSAGGIO_RICHIESTA, false);
				this.colonna_pdd_ruolo = this.transazioneFieldConverter.toColumn(Transazione.model().PDD_RUOLO, false);
				this.colonna_data_id_msg_risposta = this.transazioneFieldConverter.toColumn(Transazione.model().DATA_ID_MSG_RISPOSTA, false);
				this.colonna_id_messaggio_risposta = this.transazioneFieldConverter.toColumn(Transazione.model().ID_MESSAGGIO_RISPOSTA, false);
				
			}catch(Exception e){
				throw new ProtocolException("Errore durante l'inizializzazione dell'appender: "+e.getMessage(),e);
			}
		}
		
	}
	
	
	
	private boolean esisteTransazione(IProtocolFactory<?> protocolFactory, String idBustaRichiesta,String idBustaRisposta) throws ProtocolException {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		DBTransazioniManager dbManager = null;
    	Resource r = null;
    	String idModulo = ID_MODULO+".esisteTransazione"; //_"+idBustaRichiesta+"/"+idBustaRisposta;
		try{
			
			IBustaBuilder<?> protocolBustaBuilder = null;
			if(this.openspcoop2State!=null) {
				if(idBustaRichiesta!=null) {
					protocolBustaBuilder = protocolFactory.createBustaBuilder(this.openspcoop2State.getStatoRichiesta());
				}
				else {
					protocolBustaBuilder = protocolFactory.createBustaBuilder(this.openspcoop2State.getStatoRisposta());
				}
			}
			else {					
				protocolBustaBuilder = protocolFactory.createBustaBuilder(null);
			}
			
			if(idBustaRichiesta==null && idBustaRisposta==null){
				throw new ProtocolException("ID busta non forniti");
			}
			
			if(this.connection!=null){
				//System.out.println("[FILTRO] esisteTransazione idBustaRichiesta["+idBustaRichiesta+"] idBustaRisposta["+idBustaRisposta+"] BY CONNECTION");
				con = this.connection;
			}else{
				//System.out.println("[FILTRO] esisteTransazione idBustaRichiesta["+idBustaRichiesta+"] idBustaRisposta["+idBustaRisposta+"] BY DATASOURCE");
				dbManager = DBTransazioniManager.getInstance();
				r = dbManager.getResource(this.openspcoop2Properties.getIdentitaPortaDefault(protocolFactory.getProtocol()), idModulo, this.idTransazione);
				if(r==null){
					throw new Exception("Risorsa al database non disponibile");
				}
				con = (Connection) r.getResource();
			}
			if(con==null){
				throw new Exception("Connection is null");
			}
			
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabaseRuntime);
			if(this.openspcoop2Properties.isTransazioniFiltroDuplicatiTramiteTransazioniForceIndex()){
				if(this.openspcoop2Properties.isTransazioniFiltroDuplicatiSaveDateEnabled()){
					//System.out.println("ADD FORCE INDEX esisteTransazione INDEX2");
					sqlQueryObject.addSelectForceIndex(this.nomeTabellaTransazioni, CostantiDB.TABLE_TRANSAZIONI_INDEX_FILTRO_REQ_2);
					sqlQueryObject.addSelectForceIndex(this.nomeTabellaTransazioni, CostantiDB.TABLE_TRANSAZIONI_INDEX_FILTRO_RES_2);
				}else{
					//System.out.println("ADD FORCE INDEX esisteTransazione INDEX1");
					sqlQueryObject.addSelectForceIndex(this.nomeTabellaTransazioni, CostantiDB.TABLE_TRANSAZIONI_INDEX_FILTRO_REQ_1);
					sqlQueryObject.addSelectForceIndex(this.nomeTabellaTransazioni, CostantiDB.TABLE_TRANSAZIONI_INDEX_FILTRO_RES_1);
				}
			}/*else{
				System.out.println("NON USO FORCE INDEX esisteTransazione");
			}*/
			sqlQueryObject.addFromTable(this.nomeTabellaTransazioni);
			if(idBustaRichiesta!=null){
				// Solo una porta applicativa puo' ricevere una busta di richiesta (serve per evitare i problemi in caso di loopback)
				if(this.openspcoop2Properties.isTransazioniFiltroDuplicatiSaveDateEnabled()){
					sqlQueryObject.addWhereCondition(true,this.colonna_data_id_msg_richiesta+"=?",this.colonna_id_messaggio_richiesta+"=?",this.colonna_pdd_ruolo+"=?");	
				}
				else{
					sqlQueryObject.addWhereCondition(true,this.colonna_id_messaggio_richiesta+"=?",this.colonna_pdd_ruolo+"=?");	
				}
			}
			if(idBustaRisposta!=null){
				 // Solo una porta delegata puo' ricevere una busta di risposta (serve per evitare i problemi in caso di loopback)
				if(this.openspcoop2Properties.isTransazioniFiltroDuplicatiSaveDateEnabled()){
					sqlQueryObject.addWhereCondition(true,this.colonna_data_id_msg_risposta+"=?",this.colonna_id_messaggio_risposta+"=?",this.colonna_pdd_ruolo+"=?");
				}
				else{
					sqlQueryObject.addWhereCondition(true,this.colonna_id_messaggio_risposta+"=?",this.colonna_pdd_ruolo+"=?");	
				}
			}
			sqlQueryObject.setANDLogicOperator(false); // OR
			
			String sql = sqlQueryObject.createSQLQuery();
			pstmt = con.prepareStatement(sql);
			int index = 1;
			Timestamp t = null;
			if(idBustaRichiesta!=null){
				if(this.openspcoop2Properties.isTransazioniFiltroDuplicatiSaveDateEnabled()){
					t = DateUtility.getTimestampIntoIdProtocollo(this.log,protocolBustaBuilder,idBustaRichiesta);
					if(t==null){
						throw new Exception("Estrazione data dall'id busta ["+idBustaRichiesta+"] non riuscita");
					}
					pstmt.setTimestamp(index++, t);
				}
				pstmt.setString(index++, idBustaRichiesta);
				pstmt.setString(index++, TipoPdD.APPLICATIVA.getTipo());
			}
			if(idBustaRisposta!=null){
				if(this.openspcoop2Properties.isTransazioniFiltroDuplicatiSaveDateEnabled()){
					t = DateUtility.getTimestampIntoIdProtocollo(this.log,protocolBustaBuilder,idBustaRisposta);
					if(t==null){
						throw new Exception("Estrazione data dall'id busta ["+idBustaRisposta+"] non riuscita");
					}
					pstmt.setTimestamp(index++, t);
				}
				pstmt.setString(index++, idBustaRisposta);
				pstmt.setString(index++, TipoPdD.DELEGATA.getTipo());
			}
			
			if(this.debug){
				SimpleDateFormat dateformat = null;
				if(this.openspcoop2Properties.isTransazioniFiltroDuplicatiSaveDateEnabled()){
					dateformat = new SimpleDateFormat ("yyyy-MM-dd HH:mm"); // SimpleDateFormat non e' thread-safe
				}
				if(idBustaRichiesta!=null){
					if(this.openspcoop2Properties.isTransazioniFiltroDuplicatiSaveDateEnabled()){
						sql = sql.replaceFirst("\\?", "'"+dateformat.format(t)+"'");
					}
					sql = sql.replaceFirst("\\?", "'"+idBustaRichiesta+"'");
					sql = sql.replaceFirst("\\?", "'"+TipoPdD.APPLICATIVA.getTipo()+"'");
				}
				if(idBustaRisposta!=null){
					if(this.openspcoop2Properties.isTransazioniFiltroDuplicatiSaveDateEnabled()){
						sql = sql.replaceFirst("\\?", "'"+dateformat.format(t)+"'");
					}
					sql = sql.replaceFirst("\\?", "'"+idBustaRisposta+"'");
					sql = sql.replaceFirst("\\?", "'"+TipoPdD.DELEGATA.getTipo()+"'");
				}
				this.logSql.debug("Eseguo query: "+sql);
			}
			//System.out.println("esisteTransazione SQL: "+sql);
			
			rs = pstmt.executeQuery();
			if(rs.next()){
				if(this.debug){
					this.logSql.debug("Risultato query: "+true);
				}
				return true;
			}else{
				if(this.debug){
					this.logSql.debug("Risultato query: "+false);
				}
				return false;
			}
			
		}catch(Exception e){
			throw new ProtocolException(e);
		}
		finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception eClose){}
			try{
				if(pstmt!=null)
					pstmt.close();
			}catch(Exception eClose){}
			if(this.isDirectConnection==false){
				try{
					if(r!=null)
						dbManager.releaseResource(this.openspcoop2Properties.getIdentitaPortaDefault(protocolFactory.getProtocol()), idModulo, r);
				}catch(Exception eClose){}
			}
		}
		
	}
	
	private void incrementDuplicatiTransazione(IProtocolFactory<?> protocolFactory, boolean richiesta,String idBusta) throws ProtocolException {
		Connection con = null;
		PreparedStatement pstmt = null;
		DBTransazioniManager dbManager = null;
    	Resource r = null;
    	String idModulo = ID_MODULO+".incrementDuplicatiTransazione"; //_"+richiesta+"_"+idBusta;
		try{
			
			IBustaBuilder<?> protocolBustaBuilder = null;
			if(this.openspcoop2State!=null) {
				if(richiesta) {
					protocolBustaBuilder = protocolFactory.createBustaBuilder(this.openspcoop2State.getStatoRichiesta());
				}
				else {
					protocolBustaBuilder = protocolFactory.createBustaBuilder(this.openspcoop2State.getStatoRisposta());
				}
			}
			else {					
				protocolBustaBuilder = protocolFactory.createBustaBuilder(null);
			}
			
			if(idBusta==null){
				throw new ProtocolException("ID busta non fornito");
			}
			
			if(this.connection!=null){
				//System.out.println("[FILTRO] incrementDuplicatiTransazione richiesta["+richiesta+"] idBusta["+idBusta+"] BY CONNECTION");
				con = this.connection;
			}else{
				//System.out.println("[FILTRO] incrementDuplicatiTransazione richiesta["+richiesta+"] idBusta["+idBusta+"] BY DATASOURCE");
				dbManager = DBTransazioniManager.getInstance();
				r = dbManager.getResource(this.openspcoop2Properties.getIdentitaPortaDefault(protocolFactory.getProtocol()), idModulo, this.idTransazione);
				if(r==null){
					throw new Exception("Risorsa al database non disponibile");
				}
				con = (Connection) r.getResource();
			}
			if(con==null){
				throw new Exception("Connection is null");
			}
				
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabaseRuntime);
			if(this.openspcoop2Properties.isTransazioniFiltroDuplicatiTramiteTransazioniForceIndex()){
				if(this.openspcoop2Properties.isTransazioniFiltroDuplicatiSaveDateEnabled()){
					//	System.out.println("ADD FORCE INDEX esisteTransazione INDEX1");
					sqlQueryObject.addSelectForceIndex(this.nomeTabellaTransazioni, CostantiDB.TABLE_TRANSAZIONI_INDEX_FILTRO_REQ_2);
					sqlQueryObject.addSelectForceIndex(this.nomeTabellaTransazioni, CostantiDB.TABLE_TRANSAZIONI_INDEX_FILTRO_RES_2);
				}else{
					//	System.out.println("ADD FORCE INDEX esisteTransazione INDEX2");
					sqlQueryObject.addSelectForceIndex(this.nomeTabellaTransazioni, CostantiDB.TABLE_TRANSAZIONI_INDEX_FILTRO_REQ_1);
					sqlQueryObject.addSelectForceIndex(this.nomeTabellaTransazioni, CostantiDB.TABLE_TRANSAZIONI_INDEX_FILTRO_RES_1);
				}
			}
			sqlQueryObject.addUpdateTable(this.nomeTabellaTransazioni);
			if(richiesta){
				sqlQueryObject.addUpdateField("duplicati_richiesta", "duplicati_richiesta+1");
				if(this.openspcoop2Properties.isTransazioniFiltroDuplicatiSaveDateEnabled()){
					sqlQueryObject.addWhereCondition(this.colonna_data_id_msg_richiesta+"=?");
				}
				sqlQueryObject.addWhereCondition(this.colonna_id_messaggio_richiesta+"=?");
				sqlQueryObject.addWhereCondition("duplicati_richiesta>=?");
			}
			else{
				sqlQueryObject.addUpdateField("duplicati_risposta", "duplicati_risposta+1");
				if(this.openspcoop2Properties.isTransazioniFiltroDuplicatiSaveDateEnabled()){
					sqlQueryObject.addWhereCondition(this.colonna_data_id_msg_risposta+"=?");
				}
				sqlQueryObject.addWhereCondition(this.colonna_id_messaggio_risposta+"=?");
				sqlQueryObject.addWhereCondition("duplicati_risposta>=?");
			}
			sqlQueryObject.setANDLogicOperator(true); 
			
			Timestamp t = DateUtility.getTimestampIntoIdProtocollo(this.log,protocolBustaBuilder,idBusta);
			if(t==null){
				throw new Exception("Estrazione data dall'id busta ["+idBusta+"] non riuscita");
			}
			
			String sql = sqlQueryObject.createSQLUpdate();
			//System.out.println("incrementDuplicatiTransazione SQL: "+sql);
			pstmt = con.prepareStatement(sql);
			int index = 1;
			if(this.openspcoop2Properties.isTransazioniFiltroDuplicatiSaveDateEnabled()){
				pstmt.setTimestamp(index++, t);
			}
			pstmt.setString(index++, idBusta);
			pstmt.setInt(index++, 0);
			
			if(this.debug){
				if(this.openspcoop2Properties.isTransazioniFiltroDuplicatiSaveDateEnabled()){
					SimpleDateFormat dateformat = new SimpleDateFormat ("yyyy-MM-dd HH:mm"); // SimpleDateFormat non e' thread-safe
					sql = sql.replaceFirst("\\?", "'"+dateformat.format(t)+"'");
				}
				if(idBusta!=null){
					sql = sql.replaceFirst("\\?", "'"+idBusta+"'");
				}
				sql = sql.replaceFirst("\\?", "0");
				this.logSql.debug("Eseguo query: "+sql);
			}
			
			int righeModificate = pstmt.executeUpdate();
			if(this.debug){
				this.logSql.debug("ID["+idBusta+"] richiesta["+richiesta+"] modificate righe: "+righeModificate);
			}
			
		}catch(Exception e){
			throw new ProtocolException(e);
		}
		finally{
			try{
				if(pstmt!=null)
					pstmt.close();
			}catch(Exception eClose){}
			if(this.isDirectConnection==false){
				try{
					if(r!=null)
						dbManager.releaseResource(this.openspcoop2Properties.getIdentitaPortaDefault(protocolFactory.getProtocol()), idModulo, r);
				}catch(Exception eClose){}
			}
		}
		
	}
}
