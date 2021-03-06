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

package org.openspcoop2.pdd.services.service;

import java.util.Date;
import java.util.Properties;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.message.config.ServiceBindingConfiguration;
import org.openspcoop2.message.constants.IntegrationError;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.config.CachedConfigIntegrationReader;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.handlers.GestoreHandlers;
import org.openspcoop2.pdd.core.handlers.PostOutResponseContext;
import org.openspcoop2.pdd.core.handlers.transazioni.PreInRequestHandler;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.pdd.core.transazioni.TransactionContext;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.services.connector.ConnectorDispatcherErrorInfo;
import org.openspcoop2.pdd.services.connector.ConnectorDispatcherUtils;
import org.openspcoop2.pdd.services.connector.ConnectorException;
import org.openspcoop2.pdd.services.connector.messages.ConnectorInMessage;
import org.openspcoop2.pdd.services.connector.messages.ConnectorOutMessage;
import org.openspcoop2.pdd.services.core.RicezioneBusteContext;
import org.openspcoop2.pdd.services.error.RicezioneBusteExternalErrorGenerator;
import org.openspcoop2.protocol.basic.registry.IdentificazionePortaApplicativa;
import org.openspcoop2.protocol.basic.registry.ServiceIdentificationReader;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.engine.constants.IDService;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.slf4j.Logger;

/**
 * RicezioneBusteServiceUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RicezioneBusteServiceUtils {

	
	public static ConnectorDispatcherErrorInfo updatePortaApplicativaRequestInfo(RequestInfo requestInfo, Logger logCore, 
			ConnectorOutMessage res, RicezioneBusteExternalErrorGenerator generatoreErrore,
			ServiceIdentificationReader serviceIdentificationReader,
			MsgDiagnostico msgDiag) throws ConnectorException{
		
		URLProtocolContext protocolContext = requestInfo.getProtocolContext();
		ServiceBindingConfiguration bindingConfig = requestInfo.getBindingConfig();

						
		
		
		IDPortaApplicativa idPA = null;
		try{
			idPA = serviceIdentificationReader.findPortaApplicativa(protocolContext, true);
		}catch(RegistryNotFound notFound){
			if(bindingConfig.existsContextUrlMapping()==false){
				logCore.error("Porta Applicativa non trovata: "+notFound.getMessage(),notFound);
				msgDiag.addKeywordErroreProcessamento(notFound);
				msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,"portaApplicativaNonEsistente");
				return ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore, serviceIdentificationReader.getErroreIntegrazioneNotFound(), 
						IntegrationError.NOT_FOUND, notFound, null, res, logCore, ConnectorDispatcherUtils.CLIENT_ERROR);
			}
		}
		if(idPA!=null){
			return updatePortaApplicativaRequestInfo(requestInfo, logCore, 
					res, generatoreErrore, 
					serviceIdentificationReader, 
					msgDiag,
					protocolContext,idPA);
		}
		
		return null;
	}
	
	public static ConnectorDispatcherErrorInfo updatePortaApplicativaRequestInfo(RequestInfo requestInfo, Logger logCore, 
			RicezioneBusteExternalErrorGenerator generatoreErrore,
			ServiceIdentificationReader serviceIdentificationReader,
			MsgDiagnostico msgDiag, 
			URLProtocolContext protocolContext, IDPortaApplicativa idPA) throws ConnectorException{
		return updatePortaApplicativaRequestInfo(requestInfo, logCore, 
				null, generatoreErrore, 
				serviceIdentificationReader, 
				msgDiag, 
				protocolContext, idPA);
	}
	private static ConnectorDispatcherErrorInfo updatePortaApplicativaRequestInfo(RequestInfo requestInfo, Logger logCore, 
			ConnectorOutMessage res, RicezioneBusteExternalErrorGenerator generatoreErrore,
			ServiceIdentificationReader serviceIdentificationReader,
			MsgDiagnostico msgDiag, 
			URLProtocolContext protocolContext, IDPortaApplicativa idPA) throws ConnectorException{

			
		// da ora in avanti, avendo localizzato una PA, se avviene un errore genero l'errore stesso
		protocolContext.setInterfaceName(idPA.getNome());
		if(protocolContext.getFunctionParameters()==null) {
			protocolContext.setFunctionParameters(idPA.getNome());
		}
		
		IProtocolFactory<?> pf = requestInfo.getProtocolFactory();
		ServiceBindingConfiguration bindingConfig = requestInfo.getBindingConfig();
		ServiceBinding integrationServiceBinding = requestInfo.getIntegrationServiceBinding();
		ServiceBinding protocolServiceBinding = requestInfo.getProtocolServiceBinding();
		
		IDServizio idServizio = null;
		if(idPA.getIdentificativiErogazione()!=null){
			if(idPA.getIdentificativiErogazione().getIdServizio()!=null) {
				idServizio = idPA.getIdentificativiErogazione().getIdServizio().clone(); // effettuo clone altrimenti nella cache viene memorizzata l'azione impostata dopo!
			}
		}
		
		if(idServizio==null){
			try{
				idServizio = serviceIdentificationReader.convertToIDServizio(idPA);
			}catch(RegistryNotFound notFound){
				if(res!=null) {
					logCore.debug("Conversione Dati PortaDelegata in identificativo servizio fallita (notFound): "+notFound.getMessage(),notFound);
					msgDiag.addKeywordErroreProcessamento(notFound);
					msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,"portaApplicativaNonEsistente");
					return ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore, serviceIdentificationReader.getErroreIntegrazioneNotFound(), 
							IntegrationError.NOT_FOUND, notFound, null, res, logCore, ConnectorDispatcherUtils.CLIENT_ERROR);
				}
				return null;
			}
		}
		if(idServizio!=null){
			
			CachedConfigIntegrationReader configIntegrationReader = (CachedConfigIntegrationReader) serviceIdentificationReader.getConfigIntegrationReader();
			IRegistryReader registryReader = serviceIdentificationReader.getRegistryReader();
			
			// provo a leggere anche l'azione
			// l'eventuale errore lo genero dopo
			try{
				idServizio.setAzione(configIntegrationReader.getAzione(configIntegrationReader.getPortaApplicativa(idPA), protocolContext, requestInfo.getProtocolFactory()));
			}catch(Exception e){
				logCore.debug("Azione non trovata: "+e.getMessage(),e);
			}
			
			try{
				if(idServizio.getAzione()!=null) {
					PortaApplicativa pa = ConfigurazionePdDManager.getInstance().getPortaApplicativa_SafeMethod(idPA);
					IdentificazionePortaApplicativa identificazione = new IdentificazionePortaApplicativa(logCore, pf, null, pa);
					if(identificazione.find(idServizio.getAzione())) {
						IDPortaApplicativa idPA_action = identificazione.getIDPortaApplicativa(idServizio.getAzione());
						if(idPA_action!=null) {
							protocolContext.setInterfaceName(idPA_action.getNome());
							msgDiag.updatePorta(idPA_action.getNome());
							idPA = idPA_action;
						}
					}
				}
			}catch(Exception e){
				logCore.debug("Gestione porta specifica per azione fallita: "+e.getMessage(),e);
			}
			
			// updateInformazioniCooperazione
			generatoreErrore.updateInformazioniCooperazione(null, idServizio);
			requestInfo.setIdServizio(idServizio);
			
			// Aggiorno service binding rispetto al servizio localizzato
			try{
				integrationServiceBinding = pf.createProtocolConfiguration().getIntegrationServiceBinding(idServizio, registryReader);
				requestInfo.setIntegrationServiceBinding(integrationServiceBinding);
				
				protocolServiceBinding = pf.createProtocolConfiguration().getProtocolServiceBinding(integrationServiceBinding, protocolContext);
				requestInfo.setProtocolServiceBinding(protocolServiceBinding);
				
				generatoreErrore.updateServiceBinding(protocolServiceBinding);
			}catch(RegistryNotFound notFound){
				if(res!=null) {
					logCore.debug("Lettura ServiceBinding fallita (notFound): "+notFound.getMessage(),notFound);
					msgDiag.addKeywordErroreProcessamento(notFound);
					msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,"portaApplicativaNonEsistente");
					return ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore,
							ErroriIntegrazione.ERRORE_405_SERVIZIO_NON_TROVATO.getErroreIntegrazione(),
							IntegrationError.NOT_FOUND, notFound, null, res, logCore, ConnectorDispatcherUtils.CLIENT_ERROR);
				}
				return null;
			}catch(Exception error){
				if(res!=null) {
					logCore.error("Lettura ServiceBinding fallita: "+error.getMessage(),error);
					msgDiag.addKeywordErroreProcessamento(error);
					msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,"portaApplicativaNonEsistente");
					return ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento("Lettura ServiceBinding fallita"),
							IntegrationError.INTERNAL_ERROR, error, null, res, logCore, ConnectorDispatcherUtils.GENERAL_ERROR);
				}
				return null;
			}
			
			// Aggiorno service binding configuration rispetto al servizio localizzato
			try{
				bindingConfig = pf.createProtocolConfiguration().getServiceBindingConfiguration(protocolContext, integrationServiceBinding, idServizio, registryReader);
				requestInfo.setBindingConfig(bindingConfig);
			}catch(RegistryNotFound notFound){
				if(res!=null) {
					logCore.debug("Lettura Configurazione Servizio fallita (notFound): "+notFound.getMessage(),notFound);
					msgDiag.addKeywordErroreProcessamento(notFound);
					msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,"portaApplicativaNonEsistente");
					return ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore,
							ErroriIntegrazione.ERRORE_405_SERVIZIO_NON_TROVATO.getErroreIntegrazione(),
							IntegrationError.NOT_FOUND, notFound, null, res, logCore, ConnectorDispatcherUtils.CLIENT_ERROR);
				}
				return null;
			}catch(Exception error){
				if(res!=null) {
					logCore.error("Lettura Configurazione Servizio fallita: "+error.getMessage(),error);
					msgDiag.addKeywordErroreProcessamento(error);
					msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,"portaApplicativaNonEsistente");
					return ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento("Lettura Configurazione Servizio fallita"),
							IntegrationError.INTERNAL_ERROR, error, null, res, logCore, ConnectorDispatcherUtils.GENERAL_ERROR);
				}
				return null;
			}
			
			// Aggiorno message type
			try{
				MessageType requestMessageTypeIntegration = bindingConfig.getRequestMessageType(integrationServiceBinding, 
						protocolContext, protocolContext.getContentType());
				requestInfo.setIntegrationRequestMessageType(requestMessageTypeIntegration);
				
				MessageType requestMessageTypeProtocol = bindingConfig.getRequestMessageType(protocolServiceBinding, 
						protocolContext, protocolContext.getContentType());
				requestInfo.setProtocolRequestMessageType(requestMessageTypeProtocol);
				
				generatoreErrore.updateRequestMessageType(requestMessageTypeProtocol);
			}catch(Exception error){
				if(res!=null) {
					logCore.error("Comprensione MessageType fallita: "+error.getMessage(),error);
					msgDiag.addKeywordErroreProcessamento(error);
					msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,"portaApplicativaNonEsistente");
					return ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento("Comprensione MessageType fallita"),
							IntegrationError.INTERNAL_ERROR, error, null, res, logCore, ConnectorDispatcherUtils.GENERAL_ERROR);
				}
				return null;
			}
		}
	
		return null;
	}
	
	
	
	
	public static void emitTransactionError(Logger logCore, ConnectorInMessage req, PdDContext pddContext, Date dataAccettazioneRichiesta,
			ConnectorDispatcherErrorInfo info) {
		emitTransactionError(null, logCore, req, pddContext, dataAccettazioneRichiesta, info);
	}
	public static void emitTransactionError(RicezioneBusteContext context,Logger logCore, ConnectorInMessage req, PdDContext pddContext, Date dataAccettazioneRichiesta,
			ConnectorDispatcherErrorInfo info) {
		try {
			String idModulo = req.getIdModulo();
			IDService idModuloAsService = req.getIdModuloAsIDService();
			IProtocolFactory<?> protocolFactory = req.getProtocolFactory();
			RequestInfo requestInfo = req.getRequestInfo();
			
			if(context!=null && context.getPddContext()!=null) {
				PreInRequestHandler.readClientAddress(logCore, req, context.getPddContext());
				if(OpenSPCoop2Properties.getInstance().isTransazioniEnabled()) {
					try {
						String idTransazione = (String) context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
						if(idTransazione!=null) {
							Transaction tr = TransactionContext.getTransaction(idTransazione);
							PreInRequestHandler.setInfoInvocation(tr, requestInfo, req);
						}
					}catch(Throwable e){
						logCore.error("postOutResponse, lettura parametri fallita: "+e.getMessage(),e);
					}
				}
			}
			
			emitTransactionError(context,logCore, idModulo, idModuloAsService, protocolFactory, requestInfo, pddContext, dataAccettazioneRichiesta, info);
		}catch(Throwable e){
			logCore.error("postOutResponse, registrazione errore fallita: "+e.getMessage(),e);
		}
	}
	
	public static void emitTransactionError(RicezioneBusteContext context,Logger logCore,String idModulo,IDService idModuloAsService, IProtocolFactory<?> protocolFactory, RequestInfo requestInfo,
			PdDContext pddContext, Date dataAccettazioneRichiesta,
			ConnectorDispatcherErrorInfo info) {
		
		try {
		
			if(context==null) {
				context = new RicezioneBusteContext(idModuloAsService,dataAccettazioneRichiesta,requestInfo);
			}
			
			PostOutResponseContext postOutResponseContext = new PostOutResponseContext(logCore,protocolFactory);
			postOutResponseContext.setTipoPorta(TipoPdD.APPLICATIVA);
			postOutResponseContext.setIdModulo(idModulo);
			
			postOutResponseContext.setPddContext(pddContext);
			if(pddContext==null) {
				postOutResponseContext.setPddContext(context.getPddContext());
			}
			else {
				pddContext.addAll(context.getPddContext(), true);
			}
			postOutResponseContext.getPddContext().addObject(CostantiPdD.DATA_ACCETTAZIONE_RICHIESTA, dataAccettazioneRichiesta);

			if(OpenSPCoop2Properties.getInstance().isTransazioniEnabled()) {
				// NOTA: se gia' esiste con l'id di transazione, non viene ricreata
				TransactionContext.createTransaction((String)postOutResponseContext.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE));
			}
			
			postOutResponseContext.setDataElaborazioneMessaggio(DateManager.getDate());
			postOutResponseContext.setEsito(info.getEsitoTransazione());
			postOutResponseContext.setReturnCode(info.getStatus());
			postOutResponseContext.setPropertiesRispostaTrasporto(info.getTrasporto());
			if(info.getContentType()!=null) {
				postOutResponseContext.setPropertiesRispostaTrasporto(new Properties());
				postOutResponseContext.getPropertiesRispostaTrasporto().put(HttpConstants.CONTENT_TYPE, info.getContentType());
			}
					
			if(info.getErrorMessage()!=null){
				
				postOutResponseContext.setOutputResponseMessageSize(info.getErrorMessage().getOutgoingMessageContentLength());
				
				postOutResponseContext.setMessaggio(info.getErrorMessage());
			}
	
			MsgDiagnostico msgDiag = MsgDiagnostico.newInstance(TipoPdD.APPLICATIVA,idModulo);
			msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE);
			
			GestoreHandlers.postOutResponse(postOutResponseContext, msgDiag, logCore);
			
		}catch(Throwable e){
			logCore.error("postOutResponse, registrazione errore fallita: "+e.getMessage(),e);
		}
	
	}
}
