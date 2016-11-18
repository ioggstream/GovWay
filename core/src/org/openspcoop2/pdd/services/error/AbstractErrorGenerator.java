package org.openspcoop2.pdd.services.error;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.config.IntegrationErrorCollection;
import org.openspcoop2.message.config.IntegrationErrorConfiguration;
import org.openspcoop2.message.constants.IntegrationError;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.services.RequestInfo;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.slf4j.Logger;

public abstract class AbstractErrorGenerator {

	protected boolean internalErrorConfiguration;
	
	protected Logger log;
	protected IProtocolFactory protocolFactory;
	public IProtocolFactory getProtocolFactory() {
		return this.protocolFactory;
	}
	protected String idModulo;
	protected IDSoggetto identitaPdD;
	
	protected IDSoggetto mittente;
	protected IDServizio idServizio;
	protected String servizioApplicativo;
	
	protected OpenSPCoop2Properties openspcoopProperties;
	protected RequestInfo requestInfo;
	
	protected TipoPdD tipoPdD;
	public void updateTipoPdD(TipoPdD tipoPdD) {
		this.tipoPdD = tipoPdD;
	}
	
	protected MessageType forceMessageTypeResponse;
	public void setForceMessageTypeResponse(MessageType forceMessageTypeResponse) {
		this.forceMessageTypeResponse = forceMessageTypeResponse;
	}
	
	protected AbstractErrorGenerator(Logger log, String idModulo, RequestInfo requestInfo,
			TipoPdD tipoPdD, boolean internalErrorConfiguration) throws ProtocolException{
		this.log = log;
		this.protocolFactory = requestInfo.getProtocolFactory();
		this.idModulo = idModulo;
		this.identitaPdD = requestInfo.getIdentitaPdD();
		
		this.openspcoopProperties = OpenSPCoop2Properties.getInstance();
		
		this.requestInfo = requestInfo;
		
		this.tipoPdD = tipoPdD;
	}
	
	public void updateDominio(IDSoggetto identitaPdD){
		this.identitaPdD = identitaPdD;
	}
	
	public void updateInformazioniCooperazione(IDSoggetto mittente, IDServizio idServizio){
		this.mittente = mittente;
		this.idServizio = idServizio;
	}
	public void updateInformazioniCooperazione(String servizioApplicativo){
		this.servizioApplicativo = servizioApplicativo;
	}
	
	protected IntegrationErrorConfiguration getConfigurationForError(IntegrationError integrationError){
		IntegrationErrorCollection errorCollection = null;
		if(this.internalErrorConfiguration){
			errorCollection = this.requestInfo.getBindingConfig().getInternalIntegrationErrorConfiguration(this.requestInfo.getServiceBinding());
		}else{
			errorCollection = this.requestInfo.getBindingConfig().getExternalIntegrationErrorConfiguration(this.requestInfo.getServiceBinding());
		}
		IntegrationErrorConfiguration config = errorCollection.getIntegrationError(integrationError);
		if(config==null){
			config = errorCollection.getIntegrationError(IntegrationError.DEFAULT);
		}
		return config;
	}
	protected MessageType getMessageTypeForError(IntegrationError integrationError){
		IntegrationErrorConfiguration config = this.getConfigurationForError(integrationError);
		return config.getMessageType(this.requestInfo.getRequestMessageType());
	}

	protected int getReturnCodeForError(IntegrationError integrationError){
		IntegrationErrorConfiguration config = this.getConfigurationForError(integrationError);
		return config.getHttpReturnCode();
	}

	protected MessageType getMessageTypeForErrorSafeMode(IntegrationError integrationError){
		MessageType msgTypeErrorResponse = null;
		try{
			msgTypeErrorResponse = this.requestInfo.getRequestMessageType();
			if(this.forceMessageTypeResponse!=null){
				msgTypeErrorResponse = this.forceMessageTypeResponse;
			}
			else{
				msgTypeErrorResponse = getMessageTypeForError(integrationError);
			}
		}catch(Exception eError){
			// non dovrebbero avvenire errori, altrimenti si utilizza il message type della richiesta 
		}
		return msgTypeErrorResponse;
	}
	
	public OpenSPCoop2Message buildFault(Exception e){
		MessageType msgTypeErrorResponse = this.getMessageTypeForErrorSafeMode(IntegrationError.INTERNAL_ERROR);
		return OpenSPCoop2MessageFactory.getMessageFactory().createFaultMessage(msgTypeErrorResponse, e);
	}
	public OpenSPCoop2Message buildFault(String errore){
		MessageType msgTypeErrorResponse = this.getMessageTypeForErrorSafeMode(IntegrationError.INTERNAL_ERROR);
		return OpenSPCoop2MessageFactory.getMessageFactory().createFaultMessage(msgTypeErrorResponse, errore);
	}
}