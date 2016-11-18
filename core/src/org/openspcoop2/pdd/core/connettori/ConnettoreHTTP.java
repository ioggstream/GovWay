/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

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
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.utils.transport.http.RFC2047Encoding;
import org.openspcoop2.utils.transport.http.RFC2047Utilities;
import org.openspcoop2.utils.transport.http.SSLUtilities;



/**
 * Classe utilizzata per effettuare consegne di messaggi Soap, attraverso
 * l'invocazione di un server http. 
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ConnettoreHTTP extends ConnettoreBaseHTTP {

	
	/* ********  F I E L D S  P R I V A T I  ******** */

	public ByteArrayOutputStream outByte = new ByteArrayOutputStream();
	
	/** SSL Configuration */
	protected boolean connettoreHttps = false;
	protected ConnettoreHTTPSProperties sslContextProperties;
	
	/** Proxy Configuration */
	protected Proxy.Type proxyType = null;
	protected String proxyUrl = null;
	protected int proxyPort;
	protected String proxyUsername;
	protected String proxyPassword;
	
	/** Redirect */
	protected boolean followRedirects = false;
	protected String routeRedirect = null;
	protected int numberRedirect = 0;
	protected int maxNumberRedirects = 5;
	
	/** Connessione */
	protected HttpURLConnection httpConn = null;
			
	/* Costruttori */
	public ConnettoreHTTP(){
		this.connettoreHttps = false;
	}
	public ConnettoreHTTP(boolean https){
		this.connettoreHttps = https;
	}
	
	
	
	/* ********  METODI  ******** */

	protected void setSSLContext() throws Exception{
		if(this.connettoreHttps){
			this.sslContextProperties = ConnettoreHTTPSProperties.readProperties(this.properties);
		}
	}
	
	/**
	 * Si occupa di effettuare la consegna.
	 *
	 * @param request Messaggio da Consegnare
	 * @return true in caso di consegna con successo, false altrimenti
	 * 
	 */
	@Override
	public boolean send(ConnettoreMsg request){

		if(this.initialize(request, true)==false){
			return false;
		}
		
		// Location
		if(this.properties.get(CostantiConnettori.CONNETTORE_LOCATION)==null){
			this.errore = "Proprieta' '"+CostantiConnettori.CONNETTORE_LOCATION+"' non fornita e richiesta da questo tipo di connettore ["+request.getTipoConnettore()+"]";
			return false;
		}
		
		// HTTPS
		try{
			this.setSSLContext();
		}catch(Exception e){
			this.eccezioneProcessamento = e;
			this.logger.error("[HTTPS error]"+ this.readExceptionMessageFromException(e),e);
			this.errore = "[HTTPS error]"+ this.readExceptionMessageFromException(e);
			return false;
		}
	
		// Proxy
		if(this.properties.get(CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE)!=null){
			
			String tipo = this.properties.get(CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE).trim();
			if(CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE_VALUE_HTTP.equals(tipo)){
				this.proxyType = Proxy.Type.HTTP;
			}
			else if(CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE_VALUE_HTTPS.equals(tipo)){
				this.proxyType = Proxy.Type.HTTP;
			}
			else{
				this.errore = "Proprieta' '"+CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE
						+"' non corretta. Impostato un tipo sconosciuto ["+tipo+"] (valori ammessi: "+CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE_VALUE_HTTP
						+","+CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE_VALUE_HTTPS+")";
				return false;
			}
			
			this.proxyUrl = this.properties.get(CostantiConnettori.CONNETTORE_HTTP_PROXY_URL);
			if(this.proxyUrl!=null){
				this.proxyUrl = this.proxyUrl.trim();
			}else{
				this.errore = "Proprieta' '"+CostantiConnettori.CONNETTORE_HTTP_PROXY_URL+
						"' non impostata, obbligatoria in presenza della proprietà '"+CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE+"'";
				return false;
			}
			
			String proxyPortTmp = this.properties.get(CostantiConnettori.CONNETTORE_HTTP_PROXY_PORT);
			if(proxyPortTmp!=null){
				proxyPortTmp = proxyPortTmp.trim();
			}else{
				this.errore = "Proprieta' '"+CostantiConnettori.CONNETTORE_HTTP_PROXY_PORT+
						"' non impostata, obbligatoria in presenza della proprietà '"+CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE+"'";
				return false;
			}
			try{
				this.proxyPort = Integer.parseInt(proxyPortTmp);
			}catch(Exception e){
				this.errore = "Proprieta' '"+CostantiConnettori.CONNETTORE_HTTP_PROXY_PORT+"' non corretta: "+this.readExceptionMessageFromException(e);
				return false;
			}
			
			
			this.proxyUsername = this.properties.get(CostantiConnettori.CONNETTORE_HTTP_PROXY_USERNAME);
			if(this.proxyUsername!=null){
				this.proxyUsername = this.proxyUsername.trim();
			}
			
			this.proxyPassword = this.properties.get(CostantiConnettori.CONNETTORE_HTTP_PROXY_PASSWORD);
			if(this.proxyPassword!=null){
				this.proxyPassword = this.proxyPassword.trim();
			}else{
				if(this.proxyUsername!=null){
					this.errore = "Proprieta' '"+CostantiConnettori.CONNETTORE_HTTP_PROXY_PASSWORD
							+"' non impostata, obbligatoria in presenza della proprietà '"+CostantiConnettori.CONNETTORE_HTTP_PROXY_USERNAME+"'";
					return false;
				}
			}
		}
				
		// Redirect
		if(ConsegnaContenutiApplicativi.ID_MODULO.equals(this.idModulo)){
			this.followRedirects = this.openspcoopProperties.isFollowRedirects_consegnaContenutiApplicativi();
			this.maxNumberRedirects = this.openspcoopProperties.getFollowRedirectsMaxHop_consegnaContenutiApplicativi();
		}
		else{
			// InoltroBuste e InoltroRisposte
			this.followRedirects = this.openspcoopProperties.isFollowRedirects_inoltroBuste();
			this.maxNumberRedirects = this.openspcoopProperties.getFollowRedirectsMaxHop_inoltroBuste();
		}		
		String redirectTmp = this.properties.get(CostantiConnettori.CONNETTORE_HTTP_REDIRECT_FOLLOW);
		if(redirectTmp!=null){
			redirectTmp = redirectTmp.trim();
			this.followRedirects = Boolean.parseBoolean(redirectTmp);
		}
		//this.log.info("FOLLOW! ("+this.followRedirects+")");
		if(this.followRedirects){
			
			redirectTmp = this.properties.get(CostantiConnettori._CONNETTORE_HTTP_REDIRECT_NUMBER);
			//this.log.info("PROPERTY! ("+redirectTmp+")");
			if(redirectTmp!=null){
				redirectTmp = redirectTmp.trim();
				this.numberRedirect = Integer.parseInt(redirectTmp);
			}
			
			redirectTmp = this.properties.get(CostantiConnettori._CONNETTORE_HTTP_REDIRECT_ROUTE);
			//this.log.info("PROPERTY! ("+redirectTmp+")");
			if(redirectTmp!=null){
				redirectTmp = redirectTmp.trim();
				this.routeRedirect = redirectTmp;
			}
		}
			
		return sendHTTP(request);

	}

	/**
	 * Si occupa di effettuare la consegna HTTP_POST (sbustando il messaggio SOAP).
	 * Si aspetta di ricevere una risposta non sbustata.
	 *
	 * @return true in caso di consegna con successo, false altrimenti
	 * 
	 */
	protected boolean sendHTTP(ConnettoreMsg request){
		
		try{

			// Gestione https
			SSLContext sslContext = null;
			if(this.sslContextProperties!=null){
				
				StringBuffer bfSSLConfig = new StringBuffer();
				sslContext = SSLUtilities.generateSSLContext(this.sslContextProperties, bfSSLConfig);
				
				if(this.debug)
					this.logger.info(bfSSLConfig.toString(),false);					
			}
			

			// Creazione URL
			if(this.debug)
				this.logger.debug("Creazione URL...");

			this.location = this.properties.get(CostantiConnettori.CONNETTORE_LOCATION);			
			this.location = ConnettoreUtils.buildLocationWithURLBasedParameter(this.requestMsg, this.propertiesUrlBased, this.location);			
			if(this.debug)
				this.logger.debug("Creazione URL ["+this.location+"]...");
			URL url = new URL( this.location );	


			
			// Creazione Connessione
			URLConnection connection = null;
			if(this.proxyType==null){
				if(this.debug)
					this.logger.info("Creazione connessione alla URL ["+this.location+"]...",false);
				connection = url.openConnection();
			}
			else{
				if(this.debug)
					this.logger.info("Creazione connessione alla URL ["+this.location+"] (via proxy "+
								this.proxyUrl+":"+this.proxyPassword+") (username["+this.proxyUsername+"] password["+this.proxyPassword+"])...",false);
					
				if(this.proxyUsername!=null){
					Authenticator.setDefault(new HttpAuthenticator(this.proxyUsername, this.proxyPassword));
				}
				
				Proxy proxy = new Proxy(this.proxyType, new InetSocketAddress(this.proxyUrl, this.proxyPort));
				connection = url.openConnection(proxy);
			}
			this.httpConn = (HttpURLConnection) connection;	
			
			
			// Imposta Contesto SSL se attivo
			if(this.sslContextProperties!=null){
				HttpsURLConnection httpsConn = (HttpsURLConnection) this.httpConn;
				httpsConn.setSSLSocketFactory(sslContext.getSocketFactory());
				
				StringBuffer bfLog = new StringBuffer();
				HostnameVerifier hostnameVerifier = SSLUtilities.generateHostnameVerifier(this.sslContextProperties, bfLog, 
						this.logger.getLogger(), this.loader);
				if(this.debug)
					this.logger.debug(bfLog.toString());
				if(hostnameVerifier!=null){
					httpsConn.setHostnameVerifier(hostnameVerifier);
				}
			}

			
			// Gestione automatica del redirect
			// The HttpURLConnection‘s follow redirect is just an indicator, in fact it won’t help you to do the “real” http redirection, you still need to handle it manually.
			/*
			if(followRedirect){
				this.httpConn.setInstanceFollowRedirects(true);
			}
			*/
			
			
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
			String contentTypeRichiesta = null;
			if(this.debug)
				this.logger.debug("Impostazione content type...");
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
				this.httpConn.setRequestProperty(HttpConstants.CONTENT_TYPE,contentTypeRichiesta);
			}
			
			
			// HttpMethod
			if(this.isSoap){
				this.httpMethod = HttpRequestMethod.POST.name();
			}
			else{
				if(this.requestMsg.getTransportRequestContext()==null || this.requestMsg.getTransportRequestContext().getRequestType()==null){
					throw new Exception("HttpRequestMethod non definito");
				}
				this.httpMethod = this.requestMsg.getTransportRequestContext().getRequestType();
				HttpRequestMethod method = HttpRequestMethod.valueOf(this.httpMethod.toUpperCase());
				if(method==null){
					throw new Exception("HttpRequestMethod sconosciuto ("+this.httpMethod+")");
				}
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
				HttpUtilities.setChunkedStreamingMode(this.httpConn, chunkLength, this.httpMethod, contentTypeRichiesta);
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
					this.logger.error("Parametro "+CostantiConnettori.CONNETTORE_READ_CONNECTION_TIMEOUT+" errato",e);
				}
			}
			if(readConnectionTimeout==-1){
				readConnectionTimeout = HttpUtilities.HTTP_READ_CONNECTION_TIMEOUT;
			}
			if(this.debug)
				this.logger.info("Impostazione http timeout CT["+connectionTimeout+"] RT["+readConnectionTimeout+"]",false);
			this.httpConn.setConnectTimeout(connectionTimeout);
			this.httpConn.setReadTimeout(readConnectionTimeout);


			// Aggiunga del SoapAction Header in caso di richiesta SOAP
			if(this.isSoap && this.sbustamentoSoap == false){
				if(this.debug)
					this.logger.debug("Impostazione soap action...");
				this.soapAction = soapMessageRequest.getSoapAction();
				if(this.soapAction==null && MessageType.SOAP_11.equals(this.requestMsg.getMessageType())){
					this.soapAction="\"OpenSPCoop\"";
					// NOTA non quotare la soap action, per mantenere la trasparenza della PdD
					this.httpConn.setRequestProperty(Costanti.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION,this.soapAction);
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
				user = this.credenziali.getUsername();
				password = this.credenziali.getPassword();
			}else{
				user = this.properties.get(CostantiConnettori.CONNETTORE_USERNAME);
				password = this.properties.get(CostantiConnettori.CONNETTORE_PASSWORD);
			}
			if(user!=null && password!=null){
				String authentication = user + ":" + password;
				authentication = HttpConstants.AUTHORIZATION_PREFIX_BASIC + 
				Base64.encode(authentication.getBytes());
				this.httpConn.setRequestProperty(HttpConstants.AUTHORIZATION,authentication);
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
				Enumeration<?> enumProperties = this.propertiesTrasporto.keys();
				while( enumProperties.hasMoreElements() ) {
					String key = (String) enumProperties.nextElement();
					String value = (String) this.propertiesTrasporto.get(key);
					if(this.debug)
						this.logger.info("Set Transport Header ["+key+"]=["+value+"]",false);
					
					if(encodingRFC2047){
						if(RFC2047Utilities.isAllCharactersInCharset(value, charsetRFC2047)==false){
							String encoded = RFC2047Utilities.encode(new String(value), charsetRFC2047, encodingAlgorithmRFC2047);
							//System.out.println("@@@@ CODIFICA ["+value+"] in ["+encoded+"]");
							if(this.debug)
								this.logger.info("RFC2047 Encoded value in ["+encoded+"] (charset:"+charsetRFC2047+" encoding-algorithm:"+encodingAlgorithmRFC2047+")",false);
							setRequestHeader(validazioneHeaderRFC2047, key, encoded, this.logger);
						}
						else{
							setRequestHeader(validazioneHeaderRFC2047, key, value, this.logger);
						}
					}
					else{
						setRequestHeader(validazioneHeaderRFC2047, key, value, this.logger);
					}
				}
			}
			
			
			// Impostazione Metodo
			if(this.debug)
				this.logger.info("Impostazione "+this.httpMethod+"...",false);
			HttpUtilities.setStream(this.httpConn, this.httpMethod, contentTypeRichiesta);
			HttpBodyParameters httpBody = new  HttpBodyParameters(this.httpMethod, contentTypeRichiesta);
//			this.httpConn.setRequestMethod( method );
//			this.httpConn.setDoOutput(false);
//			this.httpConn.setDoInput(true);

			
			// Spedizione byte
			if(httpBody.isDoOutput()){
				boolean consumeRequestMessage = true;
				if(this.followRedirects){
					consumeRequestMessage = false;
				}
				if(this.debug)
					this.logger.debug("Spedizione byte (consume-request-message:"+consumeRequestMessage+")...");
				OutputStream out = this.httpConn.getOutputStream();
				if(this.debug){
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					if(this.isSoap && this.sbustamentoSoap){
						this.logger.debug("Sbustamento...");
						TunnelSoapUtils.sbustamentoMessaggio(soapMessageRequest,bout);
					}else{
						this.requestMsg.writeTo(bout, consumeRequestMessage);
					}
					bout.flush();
					bout.close();
					out.write(bout.toByteArray());
					this.logger.info("Messaggio inviato (ContentType:"+contentTypeRichiesta+") :\n"+bout.toString(),false);
					bout.close();
				}else{
					if(this.isSoap && this.sbustamentoSoap){
						if(this.debug)
							this.logger.debug("Sbustamento...");
						TunnelSoapUtils.sbustamentoMessaggio(soapMessageRequest,out);
					}else{
						this.requestMsg.writeTo(out, consumeRequestMessage);
					}
				}
				out.flush();
				out.close();
			}
			

			// Analisi MimeType e ContentLocation della risposta
			if(this.debug)
				this.logger.debug("Analisi risposta...");
			Map<String, List<String>> mapHeaderHttpResponse = this.httpConn.getHeaderFields();
			if(mapHeaderHttpResponse!=null && mapHeaderHttpResponse.size()>0){
				if(this.propertiesTrasportoRisposta==null){
					this.propertiesTrasportoRisposta = new Properties();
				}
				Iterator<String> itHttpResponse = mapHeaderHttpResponse.keySet().iterator();
				while(itHttpResponse.hasNext()){
					String keyHttpResponse = itHttpResponse.next();
					List<String> valueHttpResponse = mapHeaderHttpResponse.get(keyHttpResponse);
					StringBuffer bfHttpResponse = new StringBuffer();
					for(int i=0;i<valueHttpResponse.size();i++){
						if(i>0){
							bfHttpResponse.append(",");
						}
						bfHttpResponse.append(valueHttpResponse.get(i));
					}
					if(keyHttpResponse==null){ // Check per evitare la coppia che ha come chiave null e come valore HTTP OK 200
						keyHttpResponse=HttpConstants.RETURN_CODE;
					}
					this.propertiesTrasportoRisposta.put(keyHttpResponse, bfHttpResponse.toString());
				}
			}
			
			// TipoRisposta
			this.tipoRisposta = this.httpConn.getHeaderField(HttpConstants.CONTENT_TYPE);
			if(this.tipoRisposta==null){
				this.tipoRisposta = this.httpConn.getHeaderField(HttpConstants.CONTENT_TYPE.toLowerCase());
			}
			if(this.tipoRisposta==null){
				this.tipoRisposta = this.httpConn.getHeaderField(HttpConstants.CONTENT_TYPE.toUpperCase());
			}

			// ContentLength della risposta
			String contentLenghtString = this.httpConn.getHeaderField(HttpConstants.CONTENT_LENGTH);
			
			if(contentLenghtString==null){
				contentLenghtString = this.httpConn.getHeaderField(HttpConstants.CONTENT_LENGTH.toLowerCase());
			}
			if(contentLenghtString==null){
				contentLenghtString = this.httpConn.getHeaderField(HttpConstants.CONTENT_LENGTH.toUpperCase());
			}
			if(contentLenghtString!=null){
				this.contentLength = Integer.valueOf(contentLenghtString);
			}
			else{
				if(this.httpConn.getContentLength()>0){
					this.contentLength = this.httpConn.getContentLength();
				}
			}		
			
		
			// Parametri di imbustamento
			if(this.isSoap){
				this.imbustamentoConAttachment = false;
				if("true".equals(this.httpConn.getHeaderField(this.openspcoopProperties.getTunnelSOAPKeyWord_headerTrasporto()))){
					this.imbustamentoConAttachment = true;
				}
				this.mimeTypeAttachment = this.httpConn.getHeaderField(this.openspcoopProperties.getTunnelSOAPKeyWordMimeType_headerTrasporto());
				if(this.mimeTypeAttachment==null)
					this.mimeTypeAttachment = HttpConstants.CONTENT_TYPE_OPENSPCOOP2_TUNNEL_SOAP;
				//System.out.println("IMB["+imbustamentoConAttachment+"] MIME["+mimeTypeAttachment+"]");
			}

			// Ricezione Risposta
			if(this.debug)
				this.logger.debug("Analisi risposta input stream e risultato http...");
			this.initConfigurationAcceptOnlyReturnCode_202_200();
			
			// return code
			this.codice = this.httpConn.getResponseCode();
			this.resultHTTPMessage = this.httpConn.getResponseMessage();
			
			if(this.codice>=400){
				this.isResponse = this.httpConn.getErrorStream();
			}
			else{
				if(this.codice>299){
					
					String redirectLocation = this.httpConn.getHeaderField(HttpConstants.REDIRECT_LOCATION);
					if(redirectLocation==null){
						redirectLocation = this.httpConn.getHeaderField(HttpConstants.REDIRECT_LOCATION.toLowerCase());
					}
					if(redirectLocation==null){
						redirectLocation = this.httpConn.getHeaderField(HttpConstants.REDIRECT_LOCATION.toUpperCase());
					}
					if(redirectLocation==null){
						throw new Exception("Non è stato rilevato l'header HTTP ["+HttpConstants.REDIRECT_LOCATION+"] necessario alla gestione del Redirect (code:"+this.codice+")"); 
					}
					
					// 3XX
					if(this.followRedirects){
												
						request.getConnectorProperties().remove(CostantiConnettori.CONNETTORE_LOCATION);
						request.getConnectorProperties().remove(CostantiConnettori._CONNETTORE_HTTP_REDIRECT_NUMBER);
						request.getConnectorProperties().remove(CostantiConnettori._CONNETTORE_HTTP_REDIRECT_ROUTE);
						request.getConnectorProperties().put(CostantiConnettori.CONNETTORE_LOCATION, redirectLocation);
						request.getConnectorProperties().put(CostantiConnettori._CONNETTORE_HTTP_REDIRECT_NUMBER, (this.numberRedirect+1)+"" );
						if(this.routeRedirect!=null){
							request.getConnectorProperties().put(CostantiConnettori._CONNETTORE_HTTP_REDIRECT_ROUTE, this.routeRedirect+" -> "+redirectLocation );
						}else{
							request.getConnectorProperties().put(CostantiConnettori._CONNETTORE_HTTP_REDIRECT_ROUTE, redirectLocation );
						}
						
						this.logger.warn("(hope:"+(this.numberRedirect+1)+") Redirect verso ["+redirectLocation+"] ...");
						
						if(this.numberRedirect==this.maxNumberRedirects){
							throw new Exception("Gestione redirect (code:"+this.codice+" "+HttpConstants.REDIRECT_LOCATION+":"+redirectLocation+") non consentita ulteriormente, sono già stati gestiti "+this.maxNumberRedirects+" redirects: "+this.routeRedirect);
						}
						
						boolean acceptOnlyReturnCode_307 = false;
						if(ConsegnaContenutiApplicativi.ID_MODULO.equals(this.idModulo)){
							acceptOnlyReturnCode_307 = this.openspcoopProperties.isAcceptOnlyReturnCode_307_consegnaContenutiApplicativi();
						}
						else{
							// InoltroBuste e InoltroRisposte
							acceptOnlyReturnCode_307 = this.openspcoopProperties.isAcceptOnlyReturnCode_307_inoltroBuste();
						}
						if(acceptOnlyReturnCode_307){
							if(this.codice!=307){
								throw new Exception("Return code ["+this.codice+"] (redirect "+HttpConstants.REDIRECT_LOCATION+":"+redirectLocation+") non consentito dal WS-I Basic Profile (http://www.ws-i.org/Profiles/BasicProfile-1.1-2004-08-24.html#HTTP_Redirect_Status_Codes)");
							}
						}
						
						return this.send(request);
						
					}else{
						throw new Exception("Gestione redirect (code:"+this.codice+" "+HttpConstants.REDIRECT_LOCATION+":"+redirectLocation+") non attiva");
					}
				}
				else{
					if(this.acceptOnlyReturnCode_202_200){
						if(this.codice!=200 && this.codice!=202){
							throw new Exception("Return code ["+this.codice+"] non consentito dal WS-I Basic Profile (http://www.ws-i.org/Profiles/BasicProfile-1.1-2004-08-24.html#HTTP_Success_Status_Codes)");
						}
					}
					if(httpBody.isDoInput()){
						this.isResponse = this.httpConn.getInputStream();
					}
				}
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
				this.dumpResponse();
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
			this.errore = "Errore avvenuto durante la consegna HTTP: "+this.readExceptionMessageFromException(e);
			this.logger.error("Errore avvenuto durante la consegna HTTP: "+this.readExceptionMessageFromException(e),e);
			return false;
		} 
	}
	
    /**
     * Effettua la disconnessione 
     */
    @Override
	public void disconnect() throws ConnettoreException{
    	try{
    	
			// Gestione finale della connessione
	    	if(this.isResponse!=null){
	    		if(this.debug && this.logger!=null)
	    			this.logger.debug("Chiusura socket...");
				this.isResponse.close();
			}
	
			// fine HTTP.
	    	if(this.httpConn!=null){
	    		if(this.debug && this.logger!=null)
					this.logger.debug("Chiusura connessione...");
				this.httpConn.disconnect();
	    	}
	    	
	    	// super.disconnect (Per risorse base)
	    	super.disconnect();
			
    	}catch(Exception e){
    		throw new ConnettoreException("Chiusura connessione non riuscita: "+e.getMessage(),e);
    	}
    }

    
    /**
     * Ritorna l'informazione su dove il connettore sta spedendo il messaggio
     * 
     * @return location di inoltro del messaggio
     */
    @Override
	public String getLocation(){
    	if(this.routeRedirect==null){
    		return this.location;
    	}
    	else{
    		return this.location+" [redirects route path: "+this.routeRedirect+"]";
    	}
    }
    

    private void setRequestHeader(boolean validazioneHeaderRFC2047, String key, String value, ConnettoreLogger logger) {
    	
    	if(validazioneHeaderRFC2047){
    		try{
        		RFC2047Utilities.validHeader(key, value);
        		this.httpConn.setRequestProperty(key,value);
        	}catch(UtilsException e){
        		logger.error(e.getMessage(),e);
        	}
    	}
    	else{
    		this.httpConn.setRequestProperty(key,value);
    	}
    	
    }
}


class HttpAuthenticator extends Authenticator{
	
	private String username;
	private String password;
	
	HttpAuthenticator(String u,String p){
		super();
		this.username = u;
		this.password = p;
	}
	
	@Override
	public PasswordAuthentication getPasswordAuthentication(){
		return new PasswordAuthentication(this.username, this.password.toCharArray());
	}
}




