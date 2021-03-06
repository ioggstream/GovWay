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

import java.io.ByteArrayOutputStream;
import java.util.Enumeration;
import java.util.Properties;

import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageProperties;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.rest.RestUtilities;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
 * ConnettoreBaseHTTP
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public abstract class ConnettoreBaseHTTP extends ConnettoreBaseWithResponse {
	
	/** httpMethod */
	protected HttpRequestMethod httpMethod = null;
	public HttpRequestMethod getHttpMethod() {
		return this.httpMethod;
	}
	public void setHttpMethod(OpenSPCoop2Message msg) throws ConnettoreException {
		// HttpMethod
		if(ServiceBinding.SOAP.equals(msg.getServiceBinding())){
			this.httpMethod = HttpRequestMethod.POST;
		}
		else{
			if(msg.getTransportRequestContext()==null || msg.getTransportRequestContext().getRequestType()==null){
				throw new ConnettoreException("HttpRequestMethod non definito");
			}
			this.httpMethod = HttpRequestMethod.valueOf(msg.getTransportRequestContext().getRequestType().toUpperCase());
			if(this.httpMethod==null){
				throw new ConnettoreException("HttpRequestMethod sconosciuto ("+this.httpMethod+")");
			}
		}
	}
	
	/** InputStream Risposta */
	protected String resultHTTPMessage;

	
	/** REST Proxy Pass Reverse */
	protected boolean rest_proxyPassReverse = false;
	
	
	@Override
	protected boolean initialize(ConnettoreMsg request, boolean connectorPropertiesRequired){
		boolean init = super.initialize(request, connectorPropertiesRequired);
		
		// Location
		if(this.properties.get(CostantiConnettori.CONNETTORE_LOCATION)==null){
			this.errore = "Proprieta' '"+CostantiConnettori.CONNETTORE_LOCATION+"' non fornita e richiesta da questo tipo di connettore ["+request.getTipoConnettore()+"]";
			return false;
		}
		
		if(this.isRest) {
			if(ConsegnaContenutiApplicativi.ID_MODULO.equals(this.idModulo)){
				this.rest_proxyPassReverse = this.openspcoopProperties.isRESTServices_consegnaContenutiApplicativi_proxyPassReverse();
			}
			else{
				// InoltroBuste e InoltroRisposte
				this.rest_proxyPassReverse = this.openspcoopProperties.isRESTServices_inoltroBuste_proxyPassReverse();
			}
		}
		
		return init;
	}
	
	
	protected void forwardHttpRequestHeader() throws Exception{
		OpenSPCoop2MessageProperties forwardHeader = null;
		if(ServiceBinding.REST.equals(this.requestMsg.getServiceBinding())) {
			forwardHeader = this.requestMsg.getForwardTransportHeader(this.openspcoopProperties.getRESTServicesHeadersForwardConfig(true));
		}
		else {
			forwardHeader = this.requestMsg.getForwardTransportHeader(this.openspcoopProperties.getSOAPServicesHeadersForwardConfig(true));
		}
				
		if(forwardHeader!=null && forwardHeader.size()>0){
			if(this.debug)
				this.logger.debug("Forward header di trasporto (size:"+forwardHeader.size()+") ...");
			if(this.propertiesTrasporto==null){
				this.propertiesTrasporto = new Properties();
			}
			Enumeration<?> keys = forwardHeader.getKeys();
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				String value = forwardHeader.getProperty(key);
				if(this.debug)
					this.logger.debug("Forward Transport Header ["+key+"]=["+value+"]");
				this.propertiesTrasporto.put(key, value);
			}
		}
	}
	
	
	@Override
	protected boolean doSoapResponse() throws Exception{

		// gestione ordinaria via WS/SOAP
		
		if(this.debug)
			this.logger.debug("gestione WS/SOAP in corso (check HTML) ...");
		
		/*
		 * Se il messaggio e' un html di errore me ne esco 
		 */			
		if(this.codice>=400 && this.tipoRisposta!=null && this.tipoRisposta.contains(HttpConstants.CONTENT_TYPE_HTML)){
			String tmpResultHTTPMessage=this.resultHTTPMessage;
			if(tmpResultHTTPMessage==null) {
				// provo a tradurlo
				tmpResultHTTPMessage = HttpUtilities.getHttpReason(this.codice);
			}
			String tipoLetturaRisposta = "("+this.codice+") " +  tmpResultHTTPMessage;
			
			// Registro HTML ricevuto.
			String htmlRicevuto = null;
			if(this.isResponse!=null){
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				byte [] readB = new byte[Utilities.DIMENSIONE_BUFFER];
				int readByte = 0;
				while((readByte = this.isResponse.read(readB))!= -1){
					bout.write(readB,0,readByte);
				}
				this.isResponse.close();
				bout.flush();
				bout.close();
				htmlRicevuto = bout.toString();
			}
			
			if(htmlRicevuto!=null && !"".equals(htmlRicevuto))
				this.errore = tipoLetturaRisposta +"\nhttp response: "+htmlRicevuto;
			else
				this.errore = tipoLetturaRisposta;
			return false;
		}
		
		return super.doSoapResponse();
	}
	
	@Override
	protected boolean doRestResponse() throws Exception{
		
		 if(this.debug)
			 this.logger.debug("gestione REST - proxyPassReverse:"+this.rest_proxyPassReverse+" ...");
			                                 
		 if(this.rest_proxyPassReverse) {
			 String redirectLocation = this.propertiesTrasportoRisposta.getProperty(HttpConstants.REDIRECT_LOCATION);
			 if(redirectLocation==null){
				 redirectLocation = this.propertiesTrasportoRisposta.getProperty(HttpConstants.REDIRECT_LOCATION.toLowerCase());
			 }
			 if(redirectLocation==null){
				 redirectLocation = this.propertiesTrasportoRisposta.getProperty(HttpConstants.REDIRECT_LOCATION.toUpperCase());
			 }
			 if(redirectLocation!=null) {
				 if(this.debug)
					 this.logger.debug("Trovato Header '"+HttpConstants.REDIRECT_LOCATION+"':["+redirectLocation+"] ...");
                   
				 // Aggiorno url
				 try {
					 String baseUrl = this.properties.get(CostantiConnettori.CONNETTORE_LOCATION);
					 if(baseUrl==null) {
						 throw new Exception("BaseURL undefined");
					 }
					 if(this.debug)
						 this.logger.debug("Base URL: ["+baseUrl+"] ...");
					 
					 String newRedirectLocation = RestUtilities.buildPassReverseUrl(this.requestMsg.getTransportRequestContext(), baseUrl, redirectLocation);
					 if(this.debug)
						 this.logger.debug("Nuovo Header '"+HttpConstants.REDIRECT_LOCATION+"':["+newRedirectLocation+"] ...");
               
					 this.propertiesTrasportoRisposta.remove(HttpConstants.REDIRECT_LOCATION);
					 this.propertiesTrasportoRisposta.remove(HttpConstants.REDIRECT_LOCATION.toLowerCase());
					 this.propertiesTrasportoRisposta.remove(HttpConstants.REDIRECT_LOCATION.toUpperCase());
					 this.propertiesTrasportoRisposta.put(HttpConstants.REDIRECT_LOCATION, newRedirectLocation);
				 }catch(Exception e) {
					 throw new Exception("Errore durante l'aggiornamento della Redirect '"+HttpConstants.REDIRECT_LOCATION+
							 "' attraverso la funzione di proxy pass reverse: "+e.getMessage(),e);
				 }
			 }
		 }

		
		return super.doRestResponse();
	}
}
