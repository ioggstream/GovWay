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
import java.net.URL;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.soap.encoding.soapenc.Base64;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.TransferLengthModes;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.Costanti;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.soap.TunnelSoapUtils;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.transport.http.HttpBodyParameters;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.utils.transport.http.RFC2047Encoding;
import org.openspcoop2.utils.transport.http.RFC2047Utilities;

/**
 * Connettore che utilizza la libreria httpcore
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreHTTPCORE extends ConnettoreBaseHTTP {

	public static final String ENDPOINT_TYPE = "httpcore";
	
	
	private static boolean USE_POOL = true;
	
	private HttpEntity httpEntityResponse = null;
	private HttpClient httpClient = null;
		
	private static PoolingHttpClientConnectionManager cm = null;
	private static synchronized void initialize(){
		if(ConnettoreHTTPCORE.cm==null){
						
			ConnettoreHTTPCORE.cm = new PoolingHttpClientConnectionManager();
			// Increase max total connection to 200
			ConnettoreHTTPCORE.cm.setMaxTotal(10000);
			// Increase default max connection per route to 20
			ConnettoreHTTPCORE.cm.setDefaultMaxPerRoute(1000);
			// Increase max connections for localhost:80 to 50
			//HttpHost localhost = new HttpHost("locahost", 80);
			//cm.setMaxPerRoute(new HttpRoute(localhost), 50);
			 
			
		}
	}
	private static HttpClient getHttpClient(ConnectionKeepAliveStrategy keepAliveStrategy){
		// Caso senza pool
		
		HttpClientBuilder httpClientBuilder = HttpClients.custom();
		
		if(keepAliveStrategy!=null){
			httpClientBuilder.setKeepAliveStrategy(keepAliveStrategy);
		}
		
		return httpClientBuilder.build();
	}
	private static HttpClient getHttpClientFromPool(ConnectionKeepAliveStrategy keepAliveStrategy){
		// Caso con pool
		if(ConnettoreHTTPCORE.cm==null){
			ConnettoreHTTPCORE.initialize();
		}
		//System.out.println("-----GET CONNECTION [START] ----");
		//System.out.println("PRIMA CLOSE AVAILABLE["+cm.getTotalStats().getAvailable()+"] LEASED["
		//		+cm.getTotalStats().getLeased()+"] MAX["+cm.getTotalStats().getMax()+"] PENDING["+cm.getTotalStats().getPending()+"]");
		ConnettoreHTTPCORE.cm.closeExpiredConnections();
		ConnettoreHTTPCORE.cm.closeIdleConnections(30, TimeUnit.SECONDS);
		//System.out.println("DOPO CLOSE AVAILABLE["+cm.getTotalStats().getAvailable()+"] LEASED["
		//		+cm.getTotalStats().getLeased()+"] MAX["+cm.getTotalStats().getMax()+"] PENDING["+cm.getTotalStats().getPending()+"]");
		
		HttpClientBuilder httpClientBuilder = HttpClients.custom();
		httpClientBuilder.setConnectionManager(ConnettoreHTTPCORE.cm);
		if(keepAliveStrategy!=null){
			httpClientBuilder.setKeepAliveStrategy(keepAliveStrategy);
		}
		
		HttpClient http = httpClientBuilder.build();
		
		//System.out.println("PRESA LA CONNESSIONE AVAILABLE["+cm.getTotalStats().getAvailable()+"] LEASED["
		//		+cm.getTotalStats().getLeased()+"] MAX["+cm.getTotalStats().getMax()+"] PENDING["+cm.getTotalStats().getPending()+"]");
		//System.out.println("-----GET CONNECTION [END] ----");
		return http;
	}
	
	
	@Override
	public boolean send(ConnettoreMsg request) {
		
		if(this.initialize(request, true)==false){
			return false;
		}
	
		try{
			
			// Creazione URL
			if(this.debug)
				this.logger.debug("Creazione URL...");
			this.location = this.properties.get(CostantiConnettori.CONNETTORE_LOCATION);
			this.location = ConnettoreUtils.buildLocationWithURLBasedParameter(this.requestMsg, ConnettoreHTTPCORE.ENDPOINT_TYPE, this.propertiesUrlBased, this.location,
					this.getProtocolFactory(), this.idModulo);

			if(this.debug)
				this.logger.debug("Creazione URL ["+this.location+"]...");
			URL url = new URL( this.location );	
			
			
			// Keep-alive
			ConnectionKeepAliveStrategy keepAliveStrategy = new ConnectionKeepAliveStrategyCustom();
			
			
			// Creazione Connessione
			if(this.debug)
				this.logger.info("Creazione connessione alla URL ["+this.location+"]...",false);
			if(ConnettoreHTTPCORE.USE_POOL){
				this.httpClient = ConnettoreHTTPCORE.getHttpClientFromPool(keepAliveStrategy);
			}
			else{
				this.httpClient = ConnettoreHTTPCORE.getHttpClient(keepAliveStrategy);
			}
			
			// HttpMethod
			if(this.httpMethod==null){
				throw new Exception("HttpRequestMethod non definito");
			}
			HttpRequestBase httpRequest = null;
			switch (this.httpMethod) {
				case GET:
					httpRequest = new HttpGet(url.toString());
					break;
				case DELETE:
					httpRequest = new HttpDelete(url.toString());
					break;
				case HEAD:
					httpRequest = new HttpHead(url.toString());
					break;
				case POST:
					httpRequest = new HttpPost(url.toString());
					break;
				case PUT:
					httpRequest = new HttpPost(url.toString());
					break;
				case OPTIONS:
					httpRequest = new HttpOptions(url.toString());
					break;
				case TRACE:
					httpRequest = new HttpTrace(url.toString());
					break;
				case PATCH:
					httpRequest = new HttpPatch(url.toString());
					break;	
				default:
					break;
			}
			if(this.httpMethod==null){
				throw new Exception("HttpRequest non definito ?");
			}
			RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
			
			
			
			// Tipologia di servizio
			OpenSPCoop2SoapMessage soapMessageRequest = null;
			if(this.debug)
				this.logger.debug("Tipologia Servizio: "+this.requestMsg.getServiceBinding());
			if(this.isSoap){
				soapMessageRequest = this.requestMsg.castAsSoap();
			}
			
			
			
			// Alcune implementazioni richiedono di aggiornare il Content-Type
			this.requestMsg.updateContentType();
			
						
			
			// Impostazione Content-Type della Spedizione su HTTP
	        
			if(this.debug)
				this.logger.debug("Impostazione content type...");
			String contentTypeRichiesta = null;
			if(this.isSoap){
				if(this.sbustamentoSoap && soapMessageRequest.countAttachments()>0 && TunnelSoapUtils.isTunnelOpenSPCoopSoap(soapMessageRequest.getSOAPBody())){
					contentTypeRichiesta = TunnelSoapUtils.getContentTypeTunnelOpenSPCoopSoap(soapMessageRequest.getSOAPBody());
				}else{
					contentTypeRichiesta = this.requestMsg.getContentType();
				}
				if(contentTypeRichiesta==null){
					throw new Exception("Content-Type del messaggio da spedire non definito");
				}
			}
			else{
				contentTypeRichiesta = this.requestMsg.getContentType();
				// Content-Type non obbligatorio in REST
			}
			if(this.debug)
				this.logger.info("Impostazione http Content-Type ["+contentTypeRichiesta+"]",false);
			if(contentTypeRichiesta!=null){
				httpRequest.setHeader(HttpConstants.CONTENT_TYPE, contentTypeRichiesta);
			}
			
			
			
			// Impostazione transfer-length
			if(this.debug)
				this.logger.debug("Impostazione transfer-length...");
			boolean transferEncodingChunked = false;
			TransferLengthModes tlm = null;
			int chunkLength = -1;
			if(ConsegnaContenutiApplicativi.ID_MODULO.equals(this.idModulo)){
				tlm = this.openspcoopProperties.getTransferLengthModes_consegnaContenutiApplicativi();
				chunkLength = this.openspcoopProperties.getChunkLength_consegnaContenutiApplicativi();
			}
			else{
				// InoltroBuste e InoltroRisposte
				tlm = this.openspcoopProperties.getTransferLengthModes_inoltroBuste();
				chunkLength = this.openspcoopProperties.getChunkLength_inoltroBuste();
			}
			transferEncodingChunked = TransferLengthModes.TRANSFER_ENCODING_CHUNKED.equals(tlm);
			if(transferEncodingChunked){
				//this.httpConn.setChunkedStreamingMode(chunkLength);
			}
			if(this.debug)
				this.logger.info("Impostazione transfer-length effettuata (chunkLength:"+chunkLength+"): "+tlm,false);
			
			
			
			// Impostazione timeout
			if(this.debug)
				this.logger.debug("Impostazione timeout...");
			int connectionTimeout = -1;
			int readConnectionTimeout = -1;
			if(this.properties.get(CostantiConnettori.CONNETTORE_CONNECTION_TIMEOUT)!=null){
				try{
					connectionTimeout = Integer.parseInt(this.properties.get(CostantiConnettori.CONNETTORE_CONNECTION_TIMEOUT));
				}catch(Exception e){
					this.logger.error("Parametro '"+CostantiConnettori.CONNETTORE_CONNECTION_TIMEOUT+"' errato",e);
				}
			}
			if(connectionTimeout==-1){
				connectionTimeout = HttpUtilities.HTTP_CONNECTION_TIMEOUT;
			}
			if(this.properties.get(CostantiConnettori.CONNETTORE_READ_CONNECTION_TIMEOUT)!=null){
				try{
					readConnectionTimeout = Integer.parseInt(this.properties.get(CostantiConnettori.CONNETTORE_READ_CONNECTION_TIMEOUT));
				}catch(Exception e){
					this.logger.error("Parametro '"+CostantiConnettori.CONNETTORE_READ_CONNECTION_TIMEOUT+"' errato",e);
				}
			}
			if(readConnectionTimeout==-1){
				readConnectionTimeout = HttpUtilities.HTTP_READ_CONNECTION_TIMEOUT;
			}
			if(this.debug)
				this.logger.info("Impostazione http timeout CT["+connectionTimeout+"] RT["+readConnectionTimeout+"]",false);
			requestConfigBuilder.setConnectionRequestTimeout(connectionTimeout);
			requestConfigBuilder.setConnectTimeout(connectionTimeout);
			requestConfigBuilder.setSocketTimeout(readConnectionTimeout);
			


			
			
			
			// Gestione automatica del redirect
			//this.httpConn.setInstanceFollowRedirects(true); 
			
			
			
			
			
			
			// Aggiunga del SoapAction Header in caso di richiesta SOAP
			if(this.isSoap && this.sbustamentoSoap == false){
				if(this.debug)
					this.logger.debug("Impostazione soap action...");
				this.soapAction = soapMessageRequest.getSoapAction();
				if(this.soapAction==null){
					this.soapAction="\"OpenSPCoop\"";
				}
				if(MessageType.SOAP_11.equals(this.requestMsg.getMessageType())){
					// NOTA non quotare la soap action, per mantenere la trasparenza della PdD
					httpRequest.setHeader(Costanti.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION,this.soapAction);
				}
				if(this.debug)
					this.logger.info("SOAP Action inviata ["+this.soapAction+"]",false);
			}
			
			
			
			
			// Authentication BASIC
			if(this.debug)
				this.logger.debug("Impostazione autenticazione...");
			String user = null;
			String password = null;
			if(this.credenziali!=null){
				user = this.credenziali.getUser();
				password = this.credenziali.getPassword();
			}else{
				user = this.properties.get(CostantiConnettori.CONNETTORE_USERNAME);
				password = this.properties.get(CostantiConnettori.CONNETTORE_PASSWORD);
			}
			if(user!=null && password!=null){
				String authentication = user + ":" + password;
				authentication = HttpConstants.AUTHORIZATION_PREFIX_BASIC + 
				Base64.encode(authentication.getBytes());
				httpRequest.setHeader(HttpConstants.AUTHORIZATION,authentication);
				if(this.debug)
					this.logger.info("Impostazione autenticazione (username:"+user+" password:"+password+") ["+authentication+"]",false);
			}

			
			
			// Impostazione Proprieta del trasporto
			if(this.debug)
				this.logger.debug("Impostazione header di trasporto...");
			boolean encodingRFC2047 = false;
			Charset charsetRFC2047 = null;
			RFC2047Encoding encodingAlgorithmRFC2047 = null;
			boolean validazioneHeaderRFC2047 = false;
			if(this.idModulo!=null){
				if(ConsegnaContenutiApplicativi.ID_MODULO.equals(this.idModulo)){
					encodingRFC2047 = this.openspcoopProperties.isEnabledEncodingRFC2047HeaderValue_consegnaContenutiApplicativi();
					charsetRFC2047 = this.openspcoopProperties.getCharsetEncodingRFC2047HeaderValue_consegnaContenutiApplicativi();
					encodingAlgorithmRFC2047 = this.openspcoopProperties.getEncodingRFC2047HeaderValue_consegnaContenutiApplicativi();
					validazioneHeaderRFC2047 = this.openspcoopProperties.isEnabledValidazioneRFC2047HeaderNameValue_consegnaContenutiApplicativi();
				}else{
					encodingRFC2047 = this.openspcoopProperties.isEnabledEncodingRFC2047HeaderValue_inoltroBuste();
					charsetRFC2047 = this.openspcoopProperties.getCharsetEncodingRFC2047HeaderValue_inoltroBuste();
					encodingAlgorithmRFC2047 = this.openspcoopProperties.getEncodingRFC2047HeaderValue_inoltroBuste();
					validazioneHeaderRFC2047 = this.openspcoopProperties.isEnabledValidazioneRFC2047HeaderNameValue_inoltroBuste();
				}
			}
			this.forwardHttpRequestHeader();
			if(this.propertiesTrasporto != null){
				Enumeration<?> enumSPC = this.propertiesTrasporto.keys();
				while( enumSPC.hasMoreElements() ) {
					String key = (String) enumSPC.nextElement();
					String value = (String) this.propertiesTrasporto.get(key);
					if(this.debug)
						this.logger.info("Set proprieta' ["+key+"]=["+value+"]",false);
					
					if(encodingRFC2047){
						if(RFC2047Utilities.isAllCharactersInCharset(value, charsetRFC2047)==false){
							String encoded = RFC2047Utilities.encode(new String(value), charsetRFC2047, encodingAlgorithmRFC2047);
							//System.out.println("@@@@ CODIFICA ["+value+"] in ["+encoded+"]");
							if(this.debug)
								this.logger.info("RFC2047 Encoded value in ["+encoded+"] (charset:"+charsetRFC2047+" encoding-algorithm:"+encodingAlgorithmRFC2047+")",false);
							setRequestHeader(httpRequest,validazioneHeaderRFC2047, key, encoded, this.logger);
						}
						else{
							setRequestHeader(httpRequest,validazioneHeaderRFC2047, key, value, this.logger);
						}
					}
					else{
						setRequestHeader(httpRequest,validazioneHeaderRFC2047, key, value, this.logger);
					}
				}
			}
			
			
			
			// Impostazione Metodo
			HttpBodyParameters httpBody = new  HttpBodyParameters(this.httpMethod, contentTypeRichiesta);
			
			
			
			// Preparazione messaggio da spedire
			// Spedizione byte
			if(httpBody.isDoOutput()){
				if(this.debug)
					this.logger.debug("Spedizione byte...");
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				if(this.isSoap && this.sbustamentoSoap){
					if(this.debug)
						this.logger.debug("Sbustamento...");
					TunnelSoapUtils.sbustamentoMessaggio(soapMessageRequest,out);
				}else{
					this.requestMsg.writeTo(out, true);
				}
				out.flush();
				out.close();
				if(this.debug){
					this.logger.info("Messaggio inviato (ContentType:"+contentTypeRichiesta+") :\n"+out.toString(),false);
					
					this.dumpBinarioRichiestaUscita(out.toByteArray(), this.location, this.propertiesTrasporto);
				}
				HttpEntity httpEntity = new ByteArrayEntity(out.toByteArray());
				if(httpRequest instanceof HttpEntityEnclosingRequestBase){
					((HttpEntityEnclosingRequestBase)httpRequest).setEntity(httpEntity);
				}
				else{
					throw new Exception("Tipo ["+httpRequest.getClass().getName()+"] non utilizzabile per una richiesta di tipo ["+this.httpMethod+"]");
				}
			}
			
			
			// Imposto Configurazione
			httpRequest.setConfig(requestConfigBuilder.build());
			
			
			
			// Spedizione byte
			if(this.debug)
				this.logger.debug("Spedizione byte...");
			// Eseguo la richiesta e prendo la risposta
			HttpResponse httpResponse = this.httpClient.execute(httpRequest);
			this.httpEntityResponse = httpResponse.getEntity();
			
			
			
			if(this.debug)
				this.logger.debug("Analisi risposta...");
			Header [] hdrRisposta = httpResponse.getAllHeaders();
			if(hdrRisposta!=null){
				for (int i = 0; i < hdrRisposta.length; i++) {
					if(hdrRisposta[i].getName()==null){
						// Check per evitare la coppia che ha come chiave null e come valore HTTP OK 200
						if(this.debug)
							this.logger.debug("HTTP risposta ["+HttpConstants.RETURN_CODE+"] ["+hdrRisposta[i].getValue()+"]...");
						this.propertiesTrasportoRisposta.put(HttpConstants.RETURN_CODE, hdrRisposta[i].getValue());
					}
					else{
						if(this.debug)
							this.logger.debug("HTTP risposta ["+hdrRisposta[i].getName()+"] ["+hdrRisposta[i].getValue()+"]...");
						this.propertiesTrasportoRisposta.put(hdrRisposta[i].getName(), hdrRisposta[i].getValue());
					}
				}
			}
			Header tipoRispostaHdr = httpResponse.getFirstHeader(HttpConstants.CONTENT_TYPE);
			if(tipoRispostaHdr==null){
				tipoRispostaHdr = httpResponse.getFirstHeader(HttpConstants.CONTENT_TYPE.toLowerCase());
			}
			if(tipoRispostaHdr==null){
				tipoRispostaHdr = httpResponse.getFirstHeader(HttpConstants.CONTENT_TYPE.toUpperCase());
			}
			this.tipoRisposta = null;
			if(tipoRispostaHdr!=null){
				this.tipoRisposta = tipoRispostaHdr.getValue();
			}
			
			Header contentLengthHdr = httpResponse.getFirstHeader(HttpConstants.CONTENT_LENGTH);
			if(contentLengthHdr==null){
				contentLengthHdr = httpResponse.getFirstHeader(HttpConstants.CONTENT_LENGTH.toLowerCase());
			}
			if(contentLengthHdr==null){
				contentLengthHdr = httpResponse.getFirstHeader(HttpConstants.CONTENT_LENGTH.toUpperCase());
			}
			if(contentLengthHdr!=null){
				this.contentLength = Long.parseLong(contentLengthHdr.getValue());
			}
			
			
			
			//System.out.println("TIPO RISPOSTA["+tipoRisposta+"] LOCATION["+locationRisposta+"]");
			
			// ContentLength della risposta
			if(this.httpEntityResponse.getContentLength()>0){
				this.contentLength = this.httpEntityResponse.getContentLength();
			}
			
			// Parametri di imbustamento
			if(this.isSoap){
				Header tunnelSoapHdr = httpResponse.getFirstHeader(this.openspcoopProperties.getTunnelSOAPKeyWord_headerTrasporto());
				if(tunnelSoapHdr!=null && "true".equals(tunnelSoapHdr.getValue())){
					this.imbustamentoConAttachment = true;
				}
				Header mimeTypeAttachmentHdr = httpResponse.getFirstHeader(this.openspcoopProperties.getTunnelSOAPKeyWordMimeType_headerTrasporto());
				if(mimeTypeAttachmentHdr!=null){
					this.mimeTypeAttachment = mimeTypeAttachmentHdr.getValue();
				}
				if(this.mimeTypeAttachment==null)
					this.mimeTypeAttachment = HttpConstants.CONTENT_TYPE_OPENSPCOOP2_TUNNEL_SOAP;
				//System.out.println("IMB["+imbustamentoConAttachment+"] MIME["+mimeTypeAttachment+"]");
			}

			// Ricezione Risposta
			if(this.debug)
				this.logger.debug("Analisi risposta input stream e risultato http...");
			this.initConfigurationAcceptOnlyReturnCode_202_200();
			
			this.codice = httpResponse.getStatusLine().getStatusCode();
			this.resultHTTPMessage = httpResponse.getStatusLine().getReasonPhrase();
			
			if(this.codice<300)
				if(this.isSoap && this.acceptOnlyReturnCode_202_200){
					if(this.codice!=200 && this.codice!=202){
						throw new Exception("Return code ["+this.codice+"] non consentito dal WS-I Basic Profile (http://www.ws-i.org/Profiles/BasicProfile-1.1-2004-08-24.html#HTTP_Success_Status_Codes)");
					}
				}
				if(httpBody.isDoInput()){
					this.isResponse = this.httpEntityResponse.getContent();
				}
			else{
				this.isResponse = this.httpEntityResponse.getContent();
			}
			
			
			
						
			
			
			
			/* ------------  PostOutRequestHandler ------------- */
			this.postOutRequest();
			
			
			
			
			/* ------------  PreInResponseHandler ------------- */
			this.preInResponse();
			
			// Lettura risposta parametri NotifierInputStream per la risposta
			this.notifierInputStreamParams = null;
			if(this.preInResponseContext!=null){
				this.notifierInputStreamParams = this.preInResponseContext.getNotifierInputStreamParams();
			}
			
			
			
			/* ------------  Gestione Risposta ------------- */
			
			this.normalizeInputStreamResponse();
			
			this.initCheckContentTypeConfiguration();
			
			if(this.debug){
				this.dumpResponse(this.propertiesTrasportoRisposta);
			}
					
			if(this.isRest){
				
				if(this.doRestResponse()==false){
					return false;
				}
				
			}
			else{
			
				if(this.doSoapResponse()==false){
					return false;
				}
				
			}
			
			if(this.debug)
				this.logger.info("Gestione invio/risposta http effettuata con successo",false);
			
			return true;			
			
			
		}  catch(Exception e){ 
			this.eccezioneProcessamento = e;
			String msgErrore = this.readExceptionMessageFromException(e);
			if(this.generateErrorWithConnectorPrefix) {
				this.errore = "Errore avvenuto durante la consegna HTTP: "+msgErrore;
			}
			else {
				this.errore = msgErrore;
			}
			this.logger.error("Errore avvenuto durante la consegna HTTP: "+msgErrore,e);
			return false;
		} 

	}

	
	@Override
	public void disconnect() throws ConnettoreException{
    	try{
			// Gestione finale della connessione    		
    		//System.out.println("CHECK CLOSE STREAM...");
	    	if(this.isResponse!=null){
	    		if(this.debug && this.logger!=null)
	    			this.logger.debug("Chiusura socket...");
	    		//System.out.println("CLOSE STREAM...");
				this.isResponse.close();
				//System.out.println("CLOSE STREAM");
			}				
    	}catch(Exception e){
    		throw new ConnettoreException("Chiusura connessione non riuscita: "+e.getMessage(),e);
    	}
    	try{
			// Gestione finale della connessione
    		//System.out.println("CHECK ENTITY...");
	    	if(this.httpEntityResponse!=null){
	    		if(this.debug && this.logger!=null)
	    			this.logger.debug("Chiusura httpEntityResponse...");
	    		//System.out.println("CLOSE ENTITY...");
	    		EntityUtils.consume(this.httpEntityResponse);
	    		//System.out.println("CLOSE ENTITY");
			}
	    	
	    	if(this.httpEntityResponse!=null){
	    		
	    	}
				
    	}catch(Exception e){
    		throw new ConnettoreException("Chiusura connessione non riuscita: "+e.getMessage(),e);
    	}
    	try{
	    	// super.disconnect (Per risorse base)
	    	super.disconnect();
    	}catch(Exception e){
    		throw new ConnettoreException("Chiusura risorse non riuscita: "+e.getMessage(),e);
    	}
    }
	
    private void setRequestHeader(HttpRequestBase httppost, boolean validazioneHeaderRFC2047, String key, String value, ConnettoreLogger logger) {
    	
    	if(validazioneHeaderRFC2047){
    		try{
        		RFC2047Utilities.validHeader(key, value);
        		httppost.setHeader(key,value);
        	}catch(UtilsException e){
        		logger.error(e.getMessage(),e);
        	}
    	}
    	else{
    		httppost.setHeader(key,value);
    	}
    	
    }
}

class ConnectionKeepAliveStrategyCustom implements ConnectionKeepAliveStrategy{

	@Override
	public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
		
		 // Honor 'keep-alive' header
        HeaderElementIterator it = new BasicHeaderElementIterator(
                response.headerIterator(HTTP.CONN_KEEP_ALIVE));
        while (it.hasNext()) {
            HeaderElement he = it.nextElement();
            String param = he.getName(); 
            String value = he.getValue();
            if (value != null && param.equalsIgnoreCase("timeout")) {
                try {
                	//System.out.println("RETURN HEADER ["+ (Long.parseLong(value) * 1000)+"]");
                    return Long.parseLong(value) * 1000;
                } catch(NumberFormatException ignore) {
                }
            }
        }
//        HttpHost target = (HttpHost) context.getAttribute(
//                ExecutionContext.HTTP_TARGET_HOST);
//        if ("www.naughty-server.com".equalsIgnoreCase(target.getHostName())) {
//            // Keep alive for 5 seconds only
//            return 5 * 1000;
//        } else {
//            // otherwise keep alive for 30 seconds
//            return 30 * 1000;
//        }
        // otherwise keep alive for 2 minutes
        //System.out.println("RETURN 2 minuti");
        return 2 * 60 * 1000;
		
	}
	
}
