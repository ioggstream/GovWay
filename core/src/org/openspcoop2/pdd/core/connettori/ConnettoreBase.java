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

package org.openspcoop2.pdd.core.connettori;

import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import org.openspcoop2.core.config.InvocazioneCredenziali;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.exception.ParseExceptionUtils;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.core.handlers.GestoreHandlers;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.OutRequestContext;
import org.openspcoop2.pdd.core.handlers.PostOutRequestContext;
import org.openspcoop2.pdd.core.handlers.PreInResponseContext;
import org.openspcoop2.pdd.logger.Dump;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.dump.DumpException;
import org.openspcoop2.utils.DynamicStringReplace;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;
import org.openspcoop2.utils.resources.Loader;

/**	
 * Contiene una classe base per i connettori
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class ConnettoreBase extends AbstractCore implements IConnettore {

	/** Proprieta' del connettore */
	protected java.util.Hashtable<String,String> properties;
	
	/** Tipo di Connettore */
	protected String tipoConnettore;
	
	/** Msg di richiesta */
	protected OpenSPCoop2Message requestMsg;	
	protected boolean isSoap = false;
	protected boolean isRest = false;
	
	/** Indicazione su di un eventuale sbustamento SOAP */
	protected boolean sbustamentoSoap;
	
	/** Proprieta' del trasporto che deve gestire il connettore */
	protected java.util.Properties propertiesTrasporto;
	
	/** Proprieta' urlBased che deve gestire il connettore */
	protected java.util.Properties propertiesUrlBased;
	
	/** Tipo di Autenticazione */
	//private String tipoAutenticazione;
	/** Credenziali per l'autenticazione */
	protected InvocazioneCredenziali credenziali;
	
	/** Busta */
	protected Busta busta;
	
	/** Indicazione se siamo in modalita' debug */
	protected boolean debug = false;

	/** OpenSPCoopProperties */
	protected OpenSPCoop2Properties openspcoopProperties = null;

	/** Identificativo */
	protected String idMessaggio;
	
	/** Logger */
	protected ConnettoreLogger logger = null;

	/** Loader loader */
	protected Loader loader = null;
	
	/** Errore generato con un prefisso del connettore */
	protected boolean generateErrorWithConnectorPrefix = true;
	
	/** Identificativo Modulo */
	protected String idModulo = null;
	
	/** motivo di un eventuale errore */
	protected String errore = null;
	/** Messaggio di risposta */
	protected OpenSPCoop2Message responseMsg = null;
	/** Codice Operazione Effettuata */
	protected int codice;
	/** ContentLength */
	protected long contentLength = -1;
	/** File tmp */
	protected String location = null;
	/** Eccezione processamento */
	protected Exception eccezioneProcessamento = null;
	/** Proprieta' del trasporto della risposta */
	protected java.util.Properties propertiesTrasportoRisposta = new Properties();
	/** CreationDate */
	protected Date creationDate;
	
	/** MessaggioDiagnostico */
	protected MsgDiagnostico msgDiagnostico;
	/** OutRequestContext */
	protected OutRequestContext outRequestContext;
	/** PostOutRequestContext */
	protected PostOutRequestContext postOutRequestContext;
	/** PreInResponseContext */
	protected PreInResponseContext preInResponseContext;
	
	/** RequestInfo */
	protected RequestInfo requestInfo;

	protected Date dataAccettazioneRisposta;
    @Override
	public Date getDataAccettazioneRisposta(){
    	return this.dataAccettazioneRisposta;
    }
    
    protected Dump dump;
	

	protected ConnettoreBase(){
		this.creationDate = DateManager.getDate();
	}
	
	protected boolean initialize(ConnettoreMsg request, boolean connectorPropertiesRequired){
		
		this.openspcoopProperties = OpenSPCoop2Properties.getInstance();
		this.loader = Loader.getInstance();

		if(request==null){
			this.errore = "Messaggio da consegnare is Null (ConnettoreMsg)";
			return false;
		}
		
		// Raccolta parametri per costruttore logger
		this.properties = request.getConnectorProperties();
		if(connectorPropertiesRequired){
			if(this.properties == null)
				this.errore = "Proprieta' del connettore non definite";
			if(this.properties.size() == 0)
				this.errore = "Proprieta' del connettore non definite";
		}
		
		// tipoConnetore
		this.tipoConnettore = request.getTipoConnettore();
		
		// generateErrorWithConnectorPrefix
		this.generateErrorWithConnectorPrefix = request.isGenerateErrorWithConnectorPrefix();
		
		// - Busta
		this.busta = request.getBusta();
		if(this.busta!=null)
			this.idMessaggio=this.busta.getID();
		
		// - Debug mode
		if(this.properties.get(CostantiConnettori.CONNETTORE_DEBUG)!=null){
			if("true".equalsIgnoreCase(this.properties.get(CostantiConnettori.CONNETTORE_DEBUG).trim()))
				this.debug = true;
		}
	
		// Logger
		this.logger = new ConnettoreLogger(this.debug, this.idMessaggio, this.getPddContext());
		
		// MessageConfiguration
		if(this.getPddContext()!=null && this.getPddContext().containsKey(org.openspcoop2.core.constants.Costanti.REQUEST_INFO)){
			this.requestInfo = (RequestInfo) this.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
		}
		
		// Raccolta altri parametri
		try{
			this.requestMsg =  request.getRequestMessage(this.requestInfo);
		}catch(Exception e){
			this.eccezioneProcessamento = e;
			this.logger.error("Errore durante la lettura del messaggio da consegnare: "+this.readExceptionMessageFromException(e),e);
			this.errore = "Errore durante la lettura del messaggio da consegnare: "+this.readExceptionMessageFromException(e);
			return false;
		}
		if(this.requestMsg==null){
			this.errore = "Messaggio da consegnare is Null (Msg)";
			return false;
		}
		if(ServiceBinding.SOAP.equals(this.requestMsg.getServiceBinding())){
			this.isSoap = true;
		}
		else{
			this.isRest = true;
		}
		this.sbustamentoSoap = request.isSbustamentoSOAP();
		this.propertiesTrasporto = request.getPropertiesTrasporto();
		this.propertiesUrlBased = request.getPropertiesUrlBased();

		// Credenziali
		//this.tipoAutenticazione = request.getAutenticazione();
		this.credenziali = request.getCredenziali();

		// Identificativo modulo
		this.idModulo = request.getIdModulo();
				
		// Context per invocazioni handler
		this.outRequestContext = request.getOutRequestContext();
		this.msgDiagnostico = request.getMsgDiagnostico();

		// Dump
		if(this.openspcoopProperties.isDumpBinario_registrazioneDatabase()) {
			IDSoggetto dominio = this.requestInfo!=null ? this.requestInfo.getIdentitaPdD() : this.openspcoopProperties.getIdentitaPortaDefault(this.outRequestContext.getProtocolFactory().getProtocol());
			String nomePorta = (this.requestInfo!=null && this.requestInfo.getProtocolContext()!=null) ? this.requestInfo.getProtocolContext().getInterfaceName() : null;
			try{
				if(this.outRequestContext!=null) {
					// sara' null nel caso in cui il connettore viene utilizzato per funzionalita' esterne come ad esempio il token. Es: org.openspcoop2.pdd.core.token.GestoreToken.http
					this.dump = new Dump(dominio,this.idModulo, this.outRequestContext.getTipoPorta(), nomePorta, this.outRequestContext.getPddContext());
				}
			}catch(Exception e){
				this.eccezioneProcessamento = e;
				this.logger.error("Errore durante l'inizializzazione del dump binario: "+this.readExceptionMessageFromException(e),e);
				this.errore = "Errore durante l'inizializzazione del dump binario: "+this.readExceptionMessageFromException(e);
				return false;
			}
		}
		
		return true;
	}
	
	
	
	/**
	 * In caso di avvenuto errore in fase di consegna, questo metodo ritorna il motivo dell'errore.
	 *
	 * @return motivo dell'errore (se avvenuto in fase di consegna).
	 * 
	 */
	@Override
	public String getErrore(){
		return this.errore;
	}

	/**
	 * In caso di avvenuta consegna, questo metodo ritorna il codice di trasporto della consegna.
	 *
	 * @return se avvenuta una consegna ritorna il codice di trasporto della consegna.
	 * 
	 */
	@Override
	public int getCodiceTrasporto(){
		return this.codice;
	}

    /**
     * In caso di avvenuta consegna, questo metodo ritorna l'header del trasporto
     *
     * @return se avvenuta una consegna ritorna l'header del trasporto
     * 
     */
    @Override
	public java.util.Properties getHeaderTrasporto(){
    	if(this.propertiesTrasportoRisposta.size()<=0){
    		return null;
    	}else{
    		return this.propertiesTrasportoRisposta;
    	}
    }
	
	/**
	 * Ritorna la risposta pervenuta in seguita alla consegna effettuata
	 *
	 * @return la risposta.
	 * 
	 */
	@Override
	public OpenSPCoop2Message getResponse(){
		return this.responseMsg;
	}

	/**
     * In caso di avvenuta consegna, questo metodo ritorna, se disponibile, la dimensione della risposta (-1 se non disponibile)
     *
     * @return se avvenuta una consegna,  questo metodo ritorna, se disponibile, la dimensione della risposta (-1 se non disponibile)
     * 
     */
	@Override
	public long getContentLength() {
		return this.contentLength;
	}
	
    /**
     * Ritorna l'informazione su dove il connettore sta spedendo il messaggio
     * 
     * @return location di inoltro del messaggio
     */
    @Override
	public String getLocation() throws ConnettoreException{
    	return this.location;
    }
    
    /**
     * Ritorna l'eccezione in caso di errore di processamento
     * 
     * @return eccezione in caso di errore di processamento
     */
    @Override
	public Exception getEccezioneProcessamento(){
    	return this.eccezioneProcessamento;
    }
	
	/**
     * Effettua la disconnessione 
     */
    @Override
	public void disconnect() throws ConnettoreException{
        
    	try{
    	
    		// Rilascio eventuali risorse associate al messaggio di risposte 
			// Tali risorse non sono ancora rilasciate, se durante il flusso e' stato generato un nuovo messaggio.
			// Due chiamate della close non provocano problemi, la seconda non comporta nessun effetto essendo lo stream 'NotifierInputStream' gia' chiuso
			if(this.responseMsg!=null && this.responseMsg.getNotifierInputStream()!=null){
				this.responseMsg.getNotifierInputStream().close();
			}
			
    	}catch(Exception e){
    		throw new ConnettoreException("Chiusura connessione non riuscita: "+e.getMessage(),e);
    	}
    	
    }
	
    /**
     * Data di creazione del connettore 
     */
    @Override
	public Date getCreationDate() throws ConnettoreException{
    	return this.creationDate;
    }
	
    
    /**
     * Ritorna le opzioni di NotifierInputStreamParams per la risposta
     * 
     * @return NotifierInputStreamParams
     * @throws ConnettoreException
     */
    @Override
	public NotifierInputStreamParams getNotifierInputStreamParamsResponse() throws ConnettoreException{
    	if(this.preInResponseContext !=null){
    		return this.preInResponseContext.getNotifierInputStreamParams();
    	}
    	return null;
    }
    
    
   protected void postOutRequest() throws Exception{
    	
    	if(this.msgDiagnostico!=null && this.outRequestContext!=null){
    		
    		this.postOutRequestContext = new PostOutRequestContext(this.outRequestContext);
    		this.postOutRequestContext.setCodiceTrasporto(this.getCodiceTrasporto());
    		this.postOutRequestContext.setPropertiesTrasportoRisposta(this.getHeaderTrasporto());
    		
    		// invocazione handler
    		try{
    			GestoreHandlers.postOutRequest(this.postOutRequestContext, this.msgDiagnostico, OpenSPCoop2Logger.getLoggerOpenSPCoopCore());
    		}catch(Exception e){
				if(e instanceof HandlerException){
					HandlerException he = (HandlerException) e;
					if(he.isEmettiDiagnostico()){
						this.msgDiagnostico.logErroreGenerico(e, ((HandlerException)e).getIdentitaHandler());
					}
					else{
						OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error(e.getMessage(),e);
					}
				}else{
					this.msgDiagnostico.logErroreGenerico(e, "PostOutRequestHandler");
				}
				throw e;
    		}
    	}
    	
    }
    
    protected void preInResponse() throws Exception{
    	
    	this.dataAccettazioneRisposta = DateManager.getDate();
    	
    	if(this.msgDiagnostico!=null && this.outRequestContext!=null){
        
    		PostOutRequestContext postContext = this.postOutRequestContext;
    		if(postContext==null){
    			postContext = new PostOutRequestContext(this.outRequestContext);
    			postContext.setCodiceTrasporto(this.getCodiceTrasporto());
    			postContext.setPropertiesTrasportoRisposta(this.getHeaderTrasporto());
    		}
    		
    		this.preInResponseContext = new PreInResponseContext(postContext);
    		
    		// invocazione handler
    		try{
    			GestoreHandlers.preInResponse(this.preInResponseContext, this.msgDiagnostico, OpenSPCoop2Logger.getLoggerOpenSPCoopCore());
    		}catch(Exception e){
				if(e instanceof HandlerException){
					HandlerException he = (HandlerException) e;
					if(he.isEmettiDiagnostico()){
						this.msgDiagnostico.logErroreGenerico(e, ((HandlerException)e).getIdentitaHandler());
					}
					else{
						OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error(e.getMessage(),e);
					}
				}else{
					this.msgDiagnostico.logErroreGenerico(e, "PreInResponseHandler");
				}
				throw e;
    		}
    	}
    	 	
    }
    
    protected String readExceptionMessageFromException(Throwable e) {
    	
    	// In questo metodo è possibile gestire meglio la casistica dei messaggi di errore ritornati.
    	
    	// 1. Host Unknown
    	// java.net.UnknownHostException
    	if(e instanceof java.net.UnknownHostException){
    		return "unknown host '"+e.getMessage()+"'";
    	}
    	
    	// 2. java.net.SocketException
    	if(e instanceof java.net.SocketException){
    		java.net.SocketException s = (java.net.SocketException) e;
    		if(isNotNullMessageException(s)){
    			return s.getMessage();
    		}
    	}
    	if(Utilities.existsInnerException(e, java.net.SocketException.class)){
    		Throwable internal = Utilities.getInnerException(e, java.net.SocketException.class);
    		if(isNotNullMessageException(internal)){
    			return this.buildException(e, internal);
    		}
    	}
    	
    	// 3. java.io.IOException
    	if(e instanceof java.io.IOException){
    		java.io.IOException io = (java.io.IOException) e;
    		if(isNotNullMessageException(io)){
    			return io.getMessage();
    		}
    	}
    	if(Utilities.existsInnerException(e, java.io.IOException.class)){
    		Throwable internal = Utilities.getInnerException(e, java.io.IOException.class);
    		if(isNotNullMessageException(internal)){
    			return this.buildException(e, internal);
    		}
    	}
    	
    	// 4. ClientAbortException (Succede nel caso il buffer del client non sia più disponibile)
    	if(Utilities.existsInnerException(e, "org.apache.catalina.connector.ClientAbortException")){
    		Throwable internal = Utilities.getInnerException(e, "org.apache.catalina.connector.ClientAbortException");
    		if(isNotNullMessageException(internal)){
    			return "ClientAbortException - "+ this.buildException(e, internal);
    		}
    	}
    	
    	// Check Null Message
    	if(this.isNotNullMessageException(e)){
    		return e.getMessage();
    	}
    	else{
    		Throwable tNotEmpty = ParseExceptionUtils.getInnerNotEmptyMessageException(e);
    		if(tNotEmpty!=null){
    			return tNotEmpty.getMessage();
    		}
    		else{
    			return "ErrorOccurs - "+ e.getMessage();
    		}
    	}
    	
    }
    private String buildException(Throwable original,Throwable internal){
    	if(isNotNullMessageException(original)){
    		return internal.getMessage() + " (sourceException: "+original.getMessage()+")";
    	}
    	else{
    		return internal.getMessage();
    	}
    }
    private boolean isNotNullMessageException(Throwable tmp){
    	return tmp.getMessage()!=null && !"".equals(tmp.getMessage()) && !"null".equalsIgnoreCase(tmp.getMessage());
    }
    
    
    private InfoConnettoreUscita infoConnettoreUscita = null;
    protected void dumpBinarioRichiestaUscita(byte[]raw,String location, Properties trasporto) throws DumpException {
    	if(this.dump!=null) {
			this.infoConnettoreUscita = new InfoConnettoreUscita();
			this.infoConnettoreUscita.setLocation(location);
			this.infoConnettoreUscita.setPropertiesTrasporto(trasporto);
			this.dump.dumpBinarioRichiestaUscita(raw, this.infoConnettoreUscita);
    	}
    }
    protected void dumpBinarioRispostaIngresso(byte[]raw,Properties trasportoRisposta) throws DumpException {
    	if(this.dump!=null) {
			this.dump.dumpBinarioRispostaIngresso(raw, this.infoConnettoreUscita, trasportoRisposta);
    	}
    }
    
    private Map<String, Object> dynamicMap = null;
    protected synchronized Map<String, Object> buildDynamicMap(ConnettoreMsg connettoreMsg){
    	if(this.dynamicMap==null) {
    		this.dynamicMap = new Hashtable<String, Object>();
    	}
    	if(this.dynamicMap.containsKey(CostantiConnettori._CONNETTORE_FILE_MAP_DATE_OBJECT)==false) {
    		this.dynamicMap.put(CostantiConnettori._CONNETTORE_FILE_MAP_DATE_OBJECT, DateManager.getDate());
    	}
    	if(this.dynamicMap.containsKey(CostantiConnettori._CONNETTORE_FILE_MAP_BUSTA_OBJECT)==false && connettoreMsg!=null && connettoreMsg.getBusta()!=null) {
    		this.dynamicMap.put(CostantiConnettori._CONNETTORE_FILE_MAP_BUSTA_OBJECT, connettoreMsg.getBusta());
    	}
    	if(this.dynamicMap.containsKey(CostantiConnettori._CONNETTORE_FILE_MAP_CTX_OBJECT)==false && this.getPddContext()!=null && this.getPddContext().getContext()!=null) {
    		this.dynamicMap.put(CostantiConnettori._CONNETTORE_FILE_MAP_CTX_OBJECT, this.getPddContext().getContext());
    	}
		return this.dynamicMap;
    }
    
    protected String getDynamicProperty(String tipoConnettore,boolean required,String name,Map<String,Object> dynamicMap) throws ConnettoreException{
		String tmp = this.properties.get(name);
		if(tmp!=null){
			tmp = tmp.trim();
		}
		if(tmp==null || "".equals(tmp)){
			if(required){
				throw new ConnettoreException("Proprieta' '"+name+"' non fornita e richiesta da questo tipo di connettore ["+tipoConnettore+"]");
			}
			return null;
		}
		return this.convertDynamicPropertyValue(name, tmp, dynamicMap);
    }
    
    protected String convertDynamicPropertyValue(String name,String tmp,Map<String,Object> dynamicMap) throws ConnettoreException{
		if(tmp.contains(CostantiConnettori._CONNETTORE_FILE_MAP_TRANSACTION_ID)){
			String idTransazione = (String)this.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
			while(tmp.contains(CostantiConnettori._CONNETTORE_FILE_MAP_TRANSACTION_ID)){
				tmp = tmp.replace(CostantiConnettori._CONNETTORE_FILE_MAP_TRANSACTION_ID, idTransazione);
			}
		}
		try{
			tmp = DynamicStringReplace.replace(tmp, dynamicMap);
		}catch(Exception e){
			throw new ConnettoreException("Proprieta' '"+name+"' contiene un valore non corretto: "+e.getMessage(),e);
		}
		return tmp;
    }
    
    protected boolean isBooleanProperty(String tipoConnettore,boolean defaultValue,String name){
  		String tmp = this.properties.get(name);
  		if(tmp!=null){
  			tmp = tmp.trim();
  		}
  		if(tmp==null || "".equals(tmp)){
  			return defaultValue;
  		}
  		return "true".equalsIgnoreCase(tmp) || CostantiConfigurazione.ABILITATO.getValue().equalsIgnoreCase(tmp);
    }
    
    protected Integer getIntegerProperty(String tipoConnettore,boolean required,String name) throws ConnettoreException{
		String tmp = this.properties.get(name);
		if(tmp!=null){
			tmp = tmp.trim();
		}
		if(tmp==null || "".equals(tmp)){
			if(required){
				throw new ConnettoreException("Proprieta' '"+name+"' non fornita e richiesta da questo tipo di connettore ["+tipoConnettore+"]");
			}
			return null;
		}
		try{
			return Integer.parseInt(tmp);
		}catch(Exception e){
			throw new ConnettoreException("Proprieta' '"+name+"' contiene un valore non corretto: "+e.getMessage(),e);
		}

    }
}
