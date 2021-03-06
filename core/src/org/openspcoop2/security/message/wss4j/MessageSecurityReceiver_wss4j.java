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


package org.openspcoop2.security.message.wss4j;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.message.Attachment;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.ExchangeImpl;
import org.apache.cxf.message.MessageImpl;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.engine.WSSecurityEngineResult;
import org.apache.wss4j.dom.handler.WSHandlerConstants;
import org.apache.wss4j.dom.handler.WSHandlerResult;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.soap.reference.Reference;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.message.AbstractSOAPMessageSecurityReceiver;
import org.openspcoop2.security.message.MessageSecurityContext;
import org.openspcoop2.security.message.SubErrorCodeSecurity;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.security.message.engine.MessageUtilities;
import org.openspcoop2.security.message.engine.WSSUtilities;
import org.openspcoop2.security.message.utils.AttachmentProcessingPart;
import org.openspcoop2.security.message.utils.AttachmentsConfigReaderUtils;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.id.IDUtilities;



/**
 * Classe per la gestione della WS-Security (WSDoAllReceiver).
 *
 * @author Lorenzo Nardi <nardi@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class MessageSecurityReceiver_wss4j extends AbstractSOAPMessageSecurityReceiver{


	@Override
	public void process(MessageSecurityContext wssContext,OpenSPCoop2Message messageParam,Busta busta) throws SecurityException{
		try{
			
			if(ServiceBinding.SOAP.equals(messageParam.getServiceBinding())==false){
				throw new SecurityException("WSS4J Engine usable only with SOAP Binding");
			}
			OpenSPCoop2SoapMessage message = messageParam.castAsSoap();
			
			
			// ** Inizializzo handler CXF **/
			
			WSS4JInInterceptor inHandler = new WSS4JInInterceptor();
			SoapMessage msgCtx = new SoapMessage(new MessageImpl());
			Exchange ex = new ExchangeImpl();
	        ex.setInMessage(msgCtx);
			msgCtx.setContent(SOAPMessage.class, message.getSOAPMessage());
	        setIncomingProperties(wssContext,inHandler,msgCtx);
	        
	        
	        // ** Registro attachments da trattare **/
	        
	        AttachmentProcessingPart app = AttachmentsConfigReaderUtils.getSecurityOnAttachments(wssContext);
	        List<Attachment> listAttachments = null;
	        if(app!=null){
	        	// Non è possibile effettuare il controllo della posizione puntuale sulla ricezione. 
	        	// Può essere usato solo per specificare quale attach firmare/cifrare in spedizione
	        	// Alcune implementazioni modificano l'ordine degli attachments una volta applicata la sicurezza
	        	if(app.isAllAttachments()==false){
	        		List<String> cidAttachmentsForSecurity = AttachmentsConfigReaderUtils.getListCIDAttachmentsForSecurity(wssContext);
	        		listAttachments = org.openspcoop2.security.message.wss4j.WSSUtilities.readAttachments(cidAttachmentsForSecurity, message);
	        	}
	        	else{
	        		listAttachments = org.openspcoop2.security.message.wss4j.WSSUtilities.readAttachments(app, message);
	        	}
	        	if(listAttachments!=null && listAttachments.size()>0){
	        		msgCtx.setAttachments(listAttachments);
	        	}
	        }
	        
	        
	        // ** Applico sicurezza tramite CXF **/
	        
			inHandler.handleMessage(msgCtx);
			List<?> results = (List<?>) msgCtx.getContextualProperty(WSHandlerConstants.RECV_RESULTS);
			wssContext.getLog().debug("Print wssRecever results...");
			org.openspcoop2.security.message.wss4j.WSSUtilities.printWSResult(wssContext.getLog(), results);
			
			
			// ** Riporto modifica degli attachments **/
			
			org.openspcoop2.security.message.wss4j.WSSUtilities.updateAttachments(listAttachments, message);
			
			
			// ** Lettura Subject Certificato **/
			
			this.certificate=getPrincipal(msgCtx,wssContext.getActor());
						
		} catch (Exception e) {
			
			SecurityException wssException = new SecurityException(e.getMessage(), e);
			
			
			/* **** MESSAGGIO ***** */
			String msg = Utilities.getInnerNotEmptyMessageException(e).getMessage();
			
			Throwable innerExc = Utilities.getLastInnerException(e);
			String innerMsg = null;
			if(innerExc!=null){
				innerMsg = innerExc.getMessage();
			}
			
			String messaggio = null;
			if(msg!=null){
				messaggio = new String(msg);
				if(innerMsg!=null && !innerMsg.equals(msg)){
					messaggio = messaggio + " ; " + innerMsg;
				}
			}
			else{
				if(innerMsg!=null){
					messaggio = innerMsg;
				}
			}
			
			wssException.setMsgErrore(messaggio);
			
			
			/* ***** CODICE **** */
			
			boolean signature = false;
			boolean encrypt = false;
			try{
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				PrintStream printStream = new PrintStream(bout);
				e.printStackTrace(printStream);
				bout.flush();
				printStream.flush();
				bout.close();
				printStream.close();
				
				if(bout.toString().contains(".processor.SignatureProcessor")){
					signature = true;
				}
				else if(bout.toString().contains(".processor.SignatureConfirmationProcessor")){
					signature = true;
				}
				else if(bout.toString().contains(".processor.EncryptedKeyProcessor")){
					encrypt = true;
				}
				else if(bout.toString().contains(".processor.EncryptedDataProcessor")){
					encrypt = true;
				}
				
			}catch(Exception eClose){}
			
			if(signature){
				wssException.setCodiceErrore(CodiceErroreCooperazione.SICUREZZA_FIRMA_NON_VALIDA);
			}
			else if(encrypt){
				wssException.setCodiceErrore(CodiceErroreCooperazione.SICUREZZA_CIFRATURA_NON_VALIDA);
			}
			else if(Utilities.existsInnerMessageException(e, "The signature or decryption was invalid", true)){
				wssException.setCodiceErrore(CodiceErroreCooperazione.SICUREZZA_FIRMA_NON_VALIDA);
			} else {
				wssException.setCodiceErrore(CodiceErroreCooperazione.SICUREZZA);
			}
			
			
			throw wssException;
		}

	}


	private String certificate;
	@Override
	public String getCertificate() throws SecurityException{
		return this.certificate;
	}

	
	private void setIncomingProperties(MessageSecurityContext wssContext,WSS4JInInterceptor interceptor, SoapMessage msgCtx) {
		Hashtable<?,?> wssIncomingProperties =  wssContext.getIncomingProperties();
		if (wssIncomingProperties != null && wssIncomingProperties.size() > 0) {
			for (Enumeration<?> e = wssIncomingProperties.keys(); e.hasMoreElements();) {
				String key = (String) e.nextElement();
				Object oValue = wssIncomingProperties.get(key);
				String value = null;
				if(oValue!=null && oValue instanceof String) {
					value = (String) oValue;
				}
				if(SecurityConstants.PASSWORD_CALLBACK_REF.equals(key)) {
					msgCtx.put(key, oValue);
				}
				else if(SecurityConstants.SIGNATURE_PROPERTY_REF_ID.equals(key) || 
						SecurityConstants.SIGNATURE_VERIFICATION_PROPERTY_REF_ID.equals(key) || 
						SecurityConstants.ENCRYPTION_PROPERTY_REF_ID.equals(key) || 
						SecurityConstants.DECRYPTION_PROPERTY_REF_ID.equals(key) ) {
					if(value!=null) {
						msgCtx.put(key, value);
					}
					else { 
						String id = key+"_"+IDUtilities.getUniqueSerialNumber();
						msgCtx.put(key, id);
						msgCtx.put(id, oValue);
					}
				}
				else{
					msgCtx.put(key, value);
					interceptor.setProperty(key, value);
				}
			}
		}
		if(wssContext.getActor()!=null){
			interceptor.setProperty(SecurityConstants.ACTOR, wssContext.getActor());
			msgCtx.put(SecurityConstants.ACTOR, wssContext.getActor());
		}
    }

	private String getPrincipal(SoapMessage msgCtx, String actor) {
		
		
		String principal = null;
		List<?> results = (List<?>) msgCtx.get(WSHandlerConstants.RECV_RESULTS);
		//System.out.println("Potential number of usernames: " + results.size());
		for (int i = 0; results != null && i < results.size(); i++) {
			WSHandlerResult hResult = (WSHandlerResult)results.get(i);
			if (actor != null) {
				//prendo solo i risultati dell'actor in config.xml
				if (hResult.getActor().compareTo(actor) == 0) {
					List<WSSecurityEngineResult> hResults = hResult.getResults();
					for (int j = 0; j < hResults.size(); j++) {
						WSSecurityEngineResult eResult = hResults.get(j);
						// An encryption or timestamp action does not have an associated principal,
						// only Signature and UsernameToken actions return a principal
						int actionGet = ((java.lang.Integer)  eResult.get(WSSecurityEngineResult.TAG_ACTION)).intValue();
						if ((actionGet == WSConstants.SIGN) || 
								(actionGet == WSConstants.UT) || 
								(actionGet == WSConstants.ST_SIGNED)) {
							principal =  ((Principal) eResult.get(WSSecurityEngineResult.TAG_PRINCIPAL)).getName();
							// Signature and UsernameToken actions return a principal
							//System.out.println("Principal's name: " + principal);
						}
					}
				}
			} else {
				List<WSSecurityEngineResult> hResults = hResult.getResults();
				for (int j = 0; j < hResults.size(); j++) {
					WSSecurityEngineResult eResult = hResults.get(j);
					// An encryption or timestamp action does not have an associated principal,
					// only Signature and UsernameToken actions return a principal
					int actionGet = ((java.lang.Integer)  eResult.get(WSSecurityEngineResult.TAG_ACTION)).intValue();
					if ((actionGet == WSConstants.SIGN) || 
							(actionGet == WSConstants.UT) || 
							(actionGet == WSConstants.ST_SIGNED)) {
						principal =  ((Principal) eResult.get(WSSecurityEngineResult.TAG_PRINCIPAL)).getName();
						// Signature and UsernameToken actions return a principal
						//System.out.println("Principal's name: " + principal);
					}
				}
			}
		}
		return principal;
	}
	

	@Override
	public List<Reference> getDirtyElements(
			org.openspcoop2.security.message.MessageSecurityContext messageSecurityContext,
			OpenSPCoop2SoapMessage message) throws SecurityException {
		return WSSUtilities.getDirtyElements(messageSecurityContext, message);
	}

	@Override
	public Map<QName, QName> checkEncryptSignatureParts(
			org.openspcoop2.security.message.MessageSecurityContext messageSecurityContext,
			List<Reference> elementsToClean, OpenSPCoop2SoapMessage message,
			List<SubErrorCodeSecurity> codiciErrore) throws SecurityException {
		return MessageUtilities.checkEncryptSignatureParts(messageSecurityContext, elementsToClean, message, codiciErrore, SecurityConstants.QNAME_WSS_ELEMENT_SECURITY);
	}

	@Override
	public void checkEncryptionPartElements(Map<QName, QName> notResolved,
			OpenSPCoop2SoapMessage message,
			List<SubErrorCodeSecurity> erroriRilevati) throws SecurityException {
		MessageUtilities.checkEncryptionPartElements(notResolved, message, erroriRilevati);
	}

	@Override
	public void cleanDirtyElements(
			org.openspcoop2.security.message.MessageSecurityContext messageSecurityContext,
			OpenSPCoop2SoapMessage message, List<Reference> elementsToClean,
			boolean detachHeaderWSSecurity, boolean removeAllIdRef)
			throws SecurityException {
		WSSUtilities.cleanDirtyElements(messageSecurityContext, message, elementsToClean,detachHeaderWSSecurity,removeAllIdRef);
		
	}
}
