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


package org.openspcoop2.pdd.core.token;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.openspcoop2.core.config.InvocazioneCredenziali;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.core.mvc.properties.provider.ProviderValidationException;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.utils.WWWAuthenticateErrorCode;
import org.openspcoop2.message.utils.WWWAuthenticateGenerator;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.connettori.ConnettoreBaseHTTP;
import org.openspcoop2.pdd.core.connettori.ConnettoreHTTP;
import org.openspcoop2.pdd.core.connettori.ConnettoreHTTPS;
import org.openspcoop2.pdd.core.connettori.ConnettoreMsg;
import org.openspcoop2.pdd.core.token.pa.EsitoGestioneTokenPortaApplicativa;
import org.openspcoop2.pdd.core.token.pa.EsitoPresenzaTokenPortaApplicativa;
import org.openspcoop2.pdd.core.token.parser.ITokenParser;
import org.openspcoop2.pdd.core.token.pd.EsitoGestioneTokenPortaDelegata;
import org.openspcoop2.pdd.core.token.pd.EsitoPresenzaTokenPortaDelegata;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.connector.FormUrlEncodedHttpServletRequest;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.cache.Cache;
import org.openspcoop2.utils.cache.CacheAlgorithm;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.security.JOSERepresentation;
import org.openspcoop2.utils.security.JsonDecrypt;
import org.openspcoop2.utils.security.JsonEncrypt;
import org.openspcoop2.utils.security.JsonSignature;
import org.openspcoop2.utils.security.JsonVerifySignature;
import org.openspcoop2.utils.transport.TransportRequestContext;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**     
 * GestoreToken
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreToken {

	public static final boolean PORTA_DELEGATA = true;
	public static final boolean PORTA_APPLICATIVA = false;
	
	/** Chiave della cache per la gestione dei token  */
	private static final String TOKEN_CACHE_NAME = "token";
	/** Cache */
	private static Cache cacheToken = null;
	/** Logger log */
	private static Logger logger = null;
	private static Logger logConsole = OpenSPCoop2Logger.getLoggerOpenSPCoopConsole();
	
	/* --------------- Cache --------------------*/
	public static void resetCache() throws TokenException{
		if(GestoreToken.cacheToken!=null){
			try{
				GestoreToken.cacheToken.clear();
			}catch(Exception e){
				throw new TokenException(e.getMessage(),e);
			}
		}
	}
	public static String printStatsCache(String separator) throws TokenException{
		try{
			if(GestoreToken.cacheToken!=null){
				return GestoreToken.cacheToken.printStats(separator);
			}
			else{
				throw new Exception("Cache non abilitata");
			}
		}catch(Exception e){
			throw new TokenException("Visualizzazione Statistiche riguardante la cache sulla gestione dei token non riuscita: "+e.getMessage(),e);
		}
	}
	public static void abilitaCache() throws TokenException{
		if(GestoreToken.cacheToken!=null)
			throw new TokenException("Cache gia' abilitata");
		else{
			try{
				GestoreToken.cacheToken = new Cache(GestoreToken.TOKEN_CACHE_NAME);
			}catch(Exception e){
				throw new TokenException(e.getMessage(),e);
			}
		}
	}
	public static void abilitaCache(Long dimensioneCache,Boolean algoritmoCacheLRU,Long itemIdleTime,Long itemLifeSecond) throws TokenException{
		if(GestoreToken.cacheToken!=null)
			throw new TokenException("Cache gia' abilitata");
		else{
			try{
				int dimensioneCacheInt = -1;
				if(dimensioneCache!=null){
					dimensioneCacheInt = dimensioneCache.intValue();
				}
				
				String algoritmoCache = null;
				if(algoritmoCacheLRU!=null){
					if(algoritmoCacheLRU)
						 algoritmoCache = CostantiConfigurazione.CACHE_LRU.toString();
					else
						 algoritmoCache = CostantiConfigurazione.CACHE_MRU.toString();
				}else{
					algoritmoCache = CostantiConfigurazione.CACHE_LRU.toString();
				}
				
				long itemIdleTimeLong = -1;
				if(itemIdleTime!=null){
					itemIdleTimeLong = itemIdleTime;
				}
				
				long itemLifeSecondLong = -1;
				if(itemLifeSecond!=null){
					itemLifeSecondLong = itemLifeSecond;
				}
				
				GestoreToken.initCacheToken(dimensioneCacheInt, algoritmoCache, itemIdleTimeLong, itemLifeSecondLong, null);
			}catch(Exception e){
				throw new TokenException(e.getMessage(),e);
			}
		}
	}
	public static void disabilitaCache() throws TokenException{
		if(GestoreToken.cacheToken==null)
			throw new TokenException("Cache gia' disabilitata");
		else{
			try{
				GestoreToken.cacheToken.clear();
				GestoreToken.cacheToken = null;
			}catch(Exception e){
				throw new TokenException(e.getMessage(),e);
			}
		}
	}
	public static boolean isCacheAbilitata(){
		return GestoreToken.cacheToken != null;
	}
	public static String listKeysCache(String separator) throws TokenException{
		if(GestoreToken.cacheToken!=null){
			try{
				return GestoreToken.cacheToken.printKeys(separator);
			}catch(Exception e){
				throw new TokenException(e.getMessage(),e);
			}
		}else{
			throw new TokenException("Cache non abilitata");
		}
	}
	public static String getObjectCache(String key) throws TokenException{
		if(GestoreToken.cacheToken!=null){
			try{
				Object o = GestoreToken.cacheToken.get(key);
				if(o!=null){
					return o.toString();
				}else{
					return "oggetto con chiave ["+key+"] non presente";
				}
			}catch(Exception e){
				throw new TokenException(e.getMessage(),e);
			}
		}else{
			throw new TokenException("Cache non abilitata");
		}
	}
	public static void removeObjectCache(String key) throws TokenException{
		if(GestoreToken.cacheToken!=null){
			try{
				GestoreToken.cacheToken.remove(key);
			}catch(Exception e){
				throw new TokenException(e.getMessage(),e);
			}
		}else{
			throw new TokenException("Cache non abilitata");
		}
	}
	


	/*----------------- INIZIALIZZAZIONE --------------------*/
	public static void initialize(Logger log) throws Exception{
		GestoreToken.initialize(false, -1,null,-1l,-1l, log);
	}
	public static void initialize(int dimensioneCache,String algoritmoCache,
			long idleTime, long itemLifeSecond, Logger log) throws Exception{
		GestoreToken.initialize(true, dimensioneCache,algoritmoCache,idleTime,itemLifeSecond, log);
	}

	private static void initialize(boolean cacheAbilitata,int dimensioneCache,String algoritmoCache,
			long idleTime, long itemLifeSecond, Logger log) throws Exception{

		// Inizializzo log
		GestoreToken.logger = log;
				
		// Inizializzazione Cache
		if(cacheAbilitata){
			GestoreToken.initCacheToken(dimensioneCache, algoritmoCache, idleTime, itemLifeSecond, log);
		}

	}


	public static void initCacheToken(int dimensioneCache,String algoritmoCache,
			long idleTime, long itemLifeSecond, Logger log) throws Exception {
		
		if(log!=null)
			log.info("Inizializzazione cache Token");

		GestoreToken.cacheToken = new Cache(GestoreToken.TOKEN_CACHE_NAME);

		if( (dimensioneCache>0) ||
				(algoritmoCache != null) ){

			if( dimensioneCache>0 ){
				try{
					String msg = "Dimensione della cache (Token) impostata al valore: "+dimensioneCache;
					if(log!=null)
						log.info(msg);
					GestoreToken.logConsole.info(msg);
					GestoreToken.cacheToken.setCacheSize(dimensioneCache);
				}catch(Exception error){
					throw new TokenException("Parametro errato per la dimensione della cache (Gestore Messaggi): "+error.getMessage(),error);
				}
			}
			if(algoritmoCache != null ){
				String msg = "Algoritmo di cache (Token) impostato al valore: "+algoritmoCache;
				if(log!=null)
					log.info(msg);
				GestoreToken.logConsole.info(msg);
				if(CostantiConfigurazione.CACHE_MRU.toString().equalsIgnoreCase(algoritmoCache))
					GestoreToken.cacheToken.setCacheAlgoritm(CacheAlgorithm.MRU);
				else
					GestoreToken.cacheToken.setCacheAlgoritm(CacheAlgorithm.LRU);
			}

		}

		if( (idleTime > 0) ||
				(itemLifeSecond > 0) ){

			if( idleTime > 0  ){
				try{
					String msg = "Attributo 'IdleTime' (Token) impostato al valore: "+idleTime;
					if(log!=null)
						log.info(msg);
					GestoreToken.logConsole.info(msg);
					GestoreToken.cacheToken.setItemIdleTime(idleTime);
				}catch(Exception error){
					throw new TokenException("Parametro errato per l'attributo 'IdleTime' (Gestore Messaggi): "+error.getMessage(),error);
				}
			}
			if( itemLifeSecond > 0  ){
				try{
					String msg = "Attributo 'MaxLifeSecond' (Token) impostato al valore: "+itemLifeSecond;
					if(log!=null)
						log.info(msg);
					GestoreToken.logConsole.info(msg);
					GestoreToken.cacheToken.setItemLifeTime(itemLifeSecond);
				}catch(Exception error){
					throw new TokenException("Parametro errato per l'attributo 'MaxLifeSecond' (Gestore Messaggi): "+error.getMessage(),error);
				}
			}

		}
	}
	
	
	
	
	
	
	
	// ********* VALIDAZIONE CONFIGURAZIONE ****************** */
	
	public static void validazioneConfigurazione(AbstractDatiInvocazione datiInvocazione) throws ProviderException, ProviderValidationException {
		TokenProvider p = new TokenProvider();
		p.validate(datiInvocazione.getPolicyGestioneToken().getProperties());
	}
	
	
	
	
	// ********* VERIFICA POSIZIONE TOKEN ****************** */
	
	public static EsitoPresenzaToken verificaPosizioneToken(Logger log, AbstractDatiInvocazione datiInvocazione, boolean portaDelegata) {
		
		EsitoPresenzaToken esitoPresenzaToken = null;
		if(portaDelegata) {
			esitoPresenzaToken = new EsitoPresenzaTokenPortaDelegata();
		}
		else {
			esitoPresenzaToken = new EsitoPresenzaTokenPortaApplicativa();
		}
		
		esitoPresenzaToken.setPresente(false);
		try{
			PolicyGestioneToken policyGestioneToken = datiInvocazione.getPolicyGestioneToken();
    		String source = policyGestioneToken.getTokenSource();
    		

			String token = null;

    		String detailsErrorHeader = null;
    		if(Costanti.POLICY_TOKEN_SOURCE_RFC6750.equals(source) ||
    				Costanti.POLICY_TOKEN_SOURCE_RFC6750_HEADER.equals(source) ||
    				Costanti.POLICY_TOKEN_SOURCE_CUSTOM_HEADER.equals(source)) {
    			if(datiInvocazione.getInfoConnettoreIngresso()==null || 
    					datiInvocazione.getInfoConnettoreIngresso().getUrlProtocolContext()==null) {
    				detailsErrorHeader = "Informazioni di trasporto non presenti";
    			}
    			else {
    				URLProtocolContext urlProtocolContext = datiInvocazione.getInfoConnettoreIngresso().getUrlProtocolContext();
    				if(urlProtocolContext.getParametersTrasporto()==null || urlProtocolContext.getParametersTrasporto().size()<=0) {
    					detailsErrorHeader = "Header di trasporto non presenti";
        			}
    				if(Costanti.POLICY_TOKEN_SOURCE_RFC6750.equals(source) ||
    	    				Costanti.POLICY_TOKEN_SOURCE_RFC6750_HEADER.equals(source)) {
    					if(urlProtocolContext.getCredential()==null || urlProtocolContext.getCredential().getBearerToken()==null) {
    						if(urlProtocolContext.getCredential()!=null && urlProtocolContext.getCredential().getUsername()!=null) {
    							detailsErrorHeader = "Riscontrato header http '"+HttpConstants.AUTHORIZATION+"' valorizzato tramite autenticazione '"+HttpConstants.AUTHORIZATION_PREFIX_BASIC+
    									"'; la configurazione richiede invece la presenza di un token valorizzato tramite autenticazione '"+HttpConstants.AUTHORIZATION_PREFIX_BEARER+"' ";
    						}
    						else {
    							detailsErrorHeader = "Non è stato riscontrato un header http '"+HttpConstants.AUTHORIZATION+"' valorizzato tramite autenticazione '"+HttpConstants.AUTHORIZATION_PREFIX_BEARER+"' e contenente un token";
    						}
    					}
    					else {
    						token = urlProtocolContext.getCredential().getBearerToken();
    						esitoPresenzaToken.setHeaderHttp(HttpConstants.AUTHORIZATION);
    					}
    				}
    				else {
    					String headerName = policyGestioneToken.getTokenSourceHeaderName();
    					token =  urlProtocolContext.getParameterTrasporto(headerName);
    					if(token==null) {
    						detailsErrorHeader = "Non è stato riscontrato l'header http '"+headerName+"' contenente il token";
    					}
    					else {
    						esitoPresenzaToken.setHeaderHttp(headerName);
    					}
    				}
    			}
    		}
    		
    		String detailsErrorUrl = null;
    		if( (token==null && Costanti.POLICY_TOKEN_SOURCE_RFC6750.equals(source)) ||
    				Costanti.POLICY_TOKEN_SOURCE_RFC6750_URL.equals(source) ||
    				Costanti.POLICY_TOKEN_SOURCE_CUSTOM_URL.equals(source)) {
    			if(datiInvocazione.getInfoConnettoreIngresso()==null || 
    					datiInvocazione.getInfoConnettoreIngresso().getUrlProtocolContext()==null) {
    				detailsErrorUrl = "Informazioni di trasporto non presenti";
    			}
    			else {
    				URLProtocolContext urlProtocolContext = datiInvocazione.getInfoConnettoreIngresso().getUrlProtocolContext();
    				if(urlProtocolContext.getParametersFormBased()==null || urlProtocolContext.getParametersFormBased().size()<=0) {
    					detailsErrorUrl = "Parametri nella URL non presenti";
        			}
    				String propertyUrlName = null;
    				if(Costanti.POLICY_TOKEN_SOURCE_RFC6750.equals(source) ||
    	    				Costanti.POLICY_TOKEN_SOURCE_RFC6750_URL.equals(source)) {
    					propertyUrlName = Costanti.RFC6750_URI_QUERY_PARAMETER_ACCESS_TOKEN;
    				}
    				else {
    					propertyUrlName = policyGestioneToken.getTokenSourceUrlPropertyName();
    				}
    				token =  urlProtocolContext.getParameterFormBased(propertyUrlName);
					if(token==null) {
						detailsErrorUrl = "Non è stato riscontrata la proprietà della URL '"+propertyUrlName+"' contenente il token";
					}
					else {
						esitoPresenzaToken.setPropertyUrl(propertyUrlName);
					}
    			}
    		}
    		
    		String detailsErrorForm = null;
    		if( (token==null && Costanti.POLICY_TOKEN_SOURCE_RFC6750.equals(source)) ||
    				Costanti.POLICY_TOKEN_SOURCE_RFC6750_FORM.equals(source)) {
    			if(datiInvocazione.getInfoConnettoreIngresso()==null || 
    					datiInvocazione.getInfoConnettoreIngresso().getUrlProtocolContext()==null ||
    					datiInvocazione.getInfoConnettoreIngresso().getUrlProtocolContext().getHttpServletRequest()==null) {
    				detailsErrorForm = "Informazioni di trasporto non presenti";
    			}
    			else {
    				HttpServletRequest httpServletRequest = datiInvocazione.getInfoConnettoreIngresso().getUrlProtocolContext().getHttpServletRequest();
    				if(httpServletRequest instanceof FormUrlEncodedHttpServletRequest) {
    					FormUrlEncodedHttpServletRequest form = (FormUrlEncodedHttpServletRequest) httpServletRequest;
    					token = form.getFormUrlEncodedParameter(Costanti.RFC6750_FORM_PARAMETER_ACCESS_TOKEN);
    					if(token==null) {
    						detailsErrorForm = "Non è stato riscontrata la proprietà della Form '"+Costanti.RFC6750_FORM_PARAMETER_ACCESS_TOKEN+"' contenente il token";
    					}
    					else {
    						esitoPresenzaToken.setPropertyFormBased(Costanti.RFC6750_FORM_PARAMETER_ACCESS_TOKEN);
    					}
    				}
    				else {
    					detailsErrorForm = "Non è stato riscontrata la presenza di un contenuto 'Form-Encoded'";
    				}
	    		}
    		}
    		
    		String detailsError = null;
    		if(detailsErrorHeader!=null) {
    			if(Costanti.POLICY_TOKEN_SOURCE_RFC6750.equals(source)) {
//    				if(detailsError!=null) {
//    					detailsError = detailsError+"; ";
//    				}
    				if(detailsErrorUrl!=null || detailsErrorForm!=null) {
    					detailsError = "\n";
    				}
    				else {
    					detailsError = "";
    				}
    				detailsError = detailsError + "(Authorization Request Header) " +detailsErrorHeader;
    			}
    			else {
    				detailsError = detailsErrorHeader;
    			}
    		}
    		if(detailsErrorUrl!=null) {
    			if(Costanti.POLICY_TOKEN_SOURCE_RFC6750.equals(source)) {
    				if(detailsError!=null) {
    					detailsError = detailsError+"\n";
    				}
    				else {
    					if(detailsErrorForm!=null) {
    						detailsError = "\n";
    					}
    					else {
    						detailsError = "";
    					}
    				}
    				detailsError = detailsError + "(URI Query Parameter) " +detailsErrorUrl;
    			}
    			else {
    				detailsError = detailsErrorUrl;
    			}
    		}
    		if(detailsErrorForm!=null) {
    			if(Costanti.POLICY_TOKEN_SOURCE_RFC6750.equals(source)) {
    				if(detailsError!=null) {
    					detailsError = detailsError+"\n";
    				}
    				else {
    					detailsError = "";
    				}
    				detailsError = detailsError + "(Form-Encoded Body Parameter) " +detailsErrorForm;
    			}
    			else {
    				detailsError = detailsErrorForm;
    			}
    		}
    		
    		
    		if(token!=null) {
				esitoPresenzaToken.setToken(token);
				esitoPresenzaToken.setPresente(true);
			}
    		else {
    			if(detailsError!=null) {
					esitoPresenzaToken.setDetails(detailsError);	
				}
				else {
					esitoPresenzaToken.setDetails("Token non individuato tramite la configurazione indicata");	
				}
    			if(policyGestioneToken.isMessageErrorGenerateEmptyMessage()) {
	    			esitoPresenzaToken.setErrorMessage(WWWAuthenticateGenerator.buildErrorMessage(WWWAuthenticateErrorCode.invalid_request, policyGestioneToken.getRealm(), 
	    					policyGestioneToken.isMessageErrorGenerateGenericMessage(), esitoPresenzaToken.getDetails()));   			
    			}
    			else {
    				esitoPresenzaToken.setWwwAuthenticateErrorHeader(WWWAuthenticateGenerator.buildHeaderValue(WWWAuthenticateErrorCode.invalid_request, policyGestioneToken.getRealm(), 
	    					policyGestioneToken.isMessageErrorGenerateGenericMessage(), esitoPresenzaToken.getDetails()));
    			}
    		}
    		
    	}catch(Exception e){
    		esitoPresenzaToken.setDetails(e.getMessage());
    		esitoPresenzaToken.setEccezioneProcessamento(e);
    	}
		
		return esitoPresenzaToken;
	}
	
	
	
	
	
	// ********* VALIDAZIONE JWT TOKEN ****************** */
	
	public static EsitoGestioneToken validazioneJWTToken(Logger log, AbstractDatiInvocazione datiInvocazione, String token, boolean portaDelegata) throws Exception {
		
		EsitoGestioneToken esitoGestioneToken = null;
		
		if(GestoreToken.cacheToken==null){
			esitoGestioneToken = _validazioneJWTToken(log, datiInvocazione, token, portaDelegata);
		}
    	else{
    		String funzione = "ValidazioneJWT";
    		String keyCache = buildCacheKey(funzione, portaDelegata, token);

			synchronized (GestoreToken.cacheToken) {

				org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) GestoreToken.cacheToken.get(keyCache);
				if(response != null){
					if(response.getObject()!=null){
						GestoreToken.logger.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (method:"+funzione+") in cache.");
						esitoGestioneToken = (EsitoGestioneToken) response.getObject();
						esitoGestioneToken.setInCache(true);
					}else if(response.getException()!=null){
						GestoreToken.logger.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] (method:"+funzione+") in cache.");
						throw (Exception) response.getException();
					}else{
						GestoreToken.logger.error("In cache non e' presente ne un oggetto ne un'eccezione.");
					}
				}

				if(esitoGestioneToken==null) {
					// Effettuo la query
					GestoreToken.logger.debug("oggetto con chiave ["+keyCache+"] (method:"+funzione+") eseguo operazione...");
					esitoGestioneToken = _validazioneJWTToken(log, datiInvocazione, token, portaDelegata);
						
					// Aggiungo la risposta in cache (se esiste una cache)	
					// Sempre. Se la risposta non deve essere cachata l'implementazione può in alternativa:
					// - impostare una eccezione di processamento (che setta automaticamente noCache a true)
					// - impostare il noCache a true
					if(esitoGestioneToken!=null){
						esitoGestioneToken.setInCache(false); // la prima volta che lo recupero sicuramente non era in cache
						if(!esitoGestioneToken.isNoCache()){
							GestoreToken.logger.info("Aggiungo oggetto ["+keyCache+"] in cache");
							try{	
								org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
								responseCache.setObject(esitoGestioneToken);
								GestoreToken.cacheToken.put(keyCache,responseCache);
							}catch(UtilsException e){
								GestoreToken.logger.error("Errore durante l'inserimento in cache ["+keyCache+"]: "+e.getMessage());
							}
						}
					}else{
						throw new TokenException("Metodo (GestoreToken."+funzione+") ha ritornato un valore di esito null");
					}
				}
			}
    	}
		
		if(esitoGestioneToken.isValido()) {
			// ricontrollo tutte le date
			_validazioneInformazioniToken(esitoGestioneToken, datiInvocazione.getPolicyGestioneToken(), 
					datiInvocazione.getPolicyGestioneToken().isValidazioneJWT_saveErrorInCache());
		}
		
		return esitoGestioneToken;
	}
	
	private static EsitoGestioneToken _validazioneJWTToken(Logger log, AbstractDatiInvocazione datiInvocazione, String token, boolean portaDelegata) {
		EsitoGestioneToken esitoGestioneToken = null;
		if(portaDelegata) {
			esitoGestioneToken = new EsitoGestioneTokenPortaDelegata();
		}
		else {
			esitoGestioneToken = new EsitoGestioneTokenPortaApplicativa();
		}
		
		esitoGestioneToken.setValido(false);
		esitoGestioneToken.setToken(token);
		
		try{
			PolicyGestioneToken policyGestioneToken = datiInvocazione.getPolicyGestioneToken();
    		String tokenType = policyGestioneToken.getTipoToken();
    		
    		String detailsError = null;
			InformazioniToken informazioniToken = null;
			Exception eProcess = null;
			
			ITokenParser tokenParser = policyGestioneToken.getValidazioneJWT_TokenParser();
			
    		if(Costanti.POLICY_TOKEN_TYPE_JWS.equals(tokenType)) {
    			// JWS Compact   			
    			JsonVerifySignature jsonCompactVerify = null;
    			try {
    				jsonCompactVerify = new JsonVerifySignature(policyGestioneToken.getProperties().get(Costanti.POLICY_VALIDAZIONE_JWS_VERIFICA_PROP_REF_ID),
    						JOSERepresentation.COMPACT);
    				if(jsonCompactVerify.verify(token)) {
    					informazioniToken = new InformazioniToken(jsonCompactVerify.getDecodedPayload(),tokenParser);
    				}
    				else {
    					detailsError = "Token non valido";
    				}
    			}catch(Exception e) {
    				detailsError = "Token non valido: "+e.getMessage();
    				eProcess = e;
    			}
    		}
    		else {
    			// JWE Compact
    			JsonDecrypt jsonDecrypt = null;
    			try {
    				jsonDecrypt = new JsonDecrypt(policyGestioneToken.getProperties().get(Costanti.POLICY_VALIDAZIONE_JWE_DECRYPT_PROP_REF_ID),
    						JOSERepresentation.COMPACT);
    				jsonDecrypt.decrypt(token);
    				informazioniToken = new InformazioniToken(jsonDecrypt.getDecodedPayload(),tokenParser);
    			}catch(Exception e) {
    				detailsError = "Token non valido: "+e.getMessage();
    				eProcess = e;
    			}
    		}
    		  		
    		if(informazioniToken!=null && informazioniToken.isValid()) {
    			esitoGestioneToken.setValido(true);
    			esitoGestioneToken.setInformazioniToken(informazioniToken);
    			esitoGestioneToken.setNoCache(false);
			}
    		else {
    			if(policyGestioneToken.isValidazioneJWT_saveErrorInCache()) {
    				esitoGestioneToken.setNoCache(false);
    			}
    			else {
    				esitoGestioneToken.setNoCache(true);
    			}
    			esitoGestioneToken.setEccezioneProcessamento(eProcess);
    			if(detailsError!=null) {
    				esitoGestioneToken.setDetails(detailsError);	
				}
				else {
					esitoGestioneToken.setDetails("Token non valido");	
				}
    			if(policyGestioneToken.isMessageErrorGenerateEmptyMessage()) {
    				esitoGestioneToken.setErrorMessage(WWWAuthenticateGenerator.buildErrorMessage(WWWAuthenticateErrorCode.invalid_token, policyGestioneToken.getRealm(), 
	    					policyGestioneToken.isMessageErrorGenerateGenericMessage(), esitoGestioneToken.getDetails()));   			
    			}
    			else {
    				esitoGestioneToken.setWwwAuthenticateErrorHeader(WWWAuthenticateGenerator.buildHeaderValue(WWWAuthenticateErrorCode.invalid_token, policyGestioneToken.getRealm(), 
	    					policyGestioneToken.isMessageErrorGenerateGenericMessage(), esitoGestioneToken.getDetails()));
    			} 			
    		}
    		
		}catch(Exception e){
			esitoGestioneToken.setDetails(e.getMessage());
			esitoGestioneToken.setEccezioneProcessamento(e);
    	}
		
		return esitoGestioneToken;
	}
	
	
	
	
	
	
	// ********* INTROSPECTION TOKEN ****************** */
	
	public static EsitoGestioneToken introspectionToken(Logger log, AbstractDatiInvocazione datiInvocazione, 
			PdDContext pddContext, IProtocolFactory<?> protocolFactory,
			String token, boolean portaDelegata) throws Exception {
		EsitoGestioneToken esitoGestioneToken = null;
		
		if(GestoreToken.cacheToken==null){
			esitoGestioneToken = _introspectionToken(log, datiInvocazione, 
					pddContext, protocolFactory,
					token, portaDelegata);
		}
    	else{
    		String funzione = "Introspection";
    		String keyCache = buildCacheKey(funzione, portaDelegata, token);

			synchronized (GestoreToken.cacheToken) {

				org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) GestoreToken.cacheToken.get(keyCache);
				if(response != null){
					if(response.getObject()!=null){
						GestoreToken.logger.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (method:"+funzione+") in cache.");
						esitoGestioneToken = (EsitoGestioneToken) response.getObject();
						esitoGestioneToken.setInCache(true);
					}else if(response.getException()!=null){
						GestoreToken.logger.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] (method:"+funzione+") in cache.");
						throw (Exception) response.getException();
					}else{
						GestoreToken.logger.error("In cache non e' presente ne un oggetto ne un'eccezione.");
					}
				}

				if(esitoGestioneToken==null) {
					// Effettuo la query
					GestoreToken.logger.debug("oggetto con chiave ["+keyCache+"] (method:"+funzione+") eseguo operazione...");
					esitoGestioneToken = _introspectionToken(log, datiInvocazione, 
							pddContext, protocolFactory,
							token, portaDelegata);
						
					// Aggiungo la risposta in cache (se esiste una cache)	
					// Sempre. Se la risposta non deve essere cachata l'implementazione può in alternativa:
					// - impostare una eccezione di processamento (che setta automaticamente noCache a true)
					// - impostare il noCache a true
					if(esitoGestioneToken!=null){
						esitoGestioneToken.setInCache(false); // la prima volta che lo recupero sicuramente non era in cache
						if(!esitoGestioneToken.isNoCache()){
							GestoreToken.logger.info("Aggiungo oggetto ["+keyCache+"] in cache");
							try{	
								org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
								responseCache.setObject(esitoGestioneToken);
								GestoreToken.cacheToken.put(keyCache,responseCache);
							}catch(UtilsException e){
								GestoreToken.logger.error("Errore durante l'inserimento in cache ["+keyCache+"]: "+e.getMessage());
							}
						}
					}else{
						throw new TokenException("Metodo (GestoreToken."+funzione+") ha ritornato un valore di esito null");
					}
				}
			}
    	}
		
		if(esitoGestioneToken.isValido()) {
			// ricontrollo tutte le date
			_validazioneInformazioniToken(esitoGestioneToken, datiInvocazione.getPolicyGestioneToken(), 
					datiInvocazione.getPolicyGestioneToken().isIntrospection_saveErrorInCache());
		}
		
		return esitoGestioneToken;
	}
	
	private static EsitoGestioneToken _introspectionToken(Logger log, AbstractDatiInvocazione datiInvocazione, 
			PdDContext pddContext, IProtocolFactory<?> protocolFactory,
			String token, boolean portaDelegata) {
		EsitoGestioneToken esitoGestioneToken = null;
		if(portaDelegata) {
			esitoGestioneToken = new EsitoGestioneTokenPortaDelegata();
		}
		else {
			esitoGestioneToken = new EsitoGestioneTokenPortaApplicativa();
		}
		
		esitoGestioneToken.setValido(false);
		esitoGestioneToken.setToken(token);
		
		try{
			PolicyGestioneToken policyGestioneToken = datiInvocazione.getPolicyGestioneToken();
    		
    		String detailsError = null;
			InformazioniToken informazioniToken = null;
			Exception eProcess = null;
			
			ITokenParser tokenParser = policyGestioneToken.getIntrospection_TokenParser();
			
			HttpResponse httpResponse = null;
			Integer httpResponseCode = null;
			byte[] risposta = null;
			try {
				httpResponse = http(log, policyGestioneToken, INTROSPECTION, token,
						pddContext, protocolFactory);
				risposta = httpResponse.getContent();
				httpResponseCode = httpResponse.getResultHTTPOperation();
			}catch(Exception e) {
				detailsError = "(Errore di Connessione) "+ e.getMessage();
				eProcess = e;
			}
			
			if(detailsError==null) {
				try {
					informazioniToken = new InformazioniToken(httpResponseCode, new String(risposta),tokenParser);
				}catch(Exception e) {
					detailsError = "Risposta del servizio di Introspection non valida: "+e.getMessage();
					eProcess = e;
				}
			}
    		  		
    		if(informazioniToken!=null && informazioniToken.isValid()) {
    			esitoGestioneToken.setValido(true);
    			esitoGestioneToken.setInformazioniToken(informazioniToken);
    			esitoGestioneToken.setNoCache(false);
			}
    		else {
    			if(policyGestioneToken.isIntrospection_saveErrorInCache()) {
    				esitoGestioneToken.setNoCache(false);
    			}
    			else {
    				esitoGestioneToken.setNoCache(true);
    			}
    			esitoGestioneToken.setEccezioneProcessamento(eProcess);
    			if(detailsError!=null) {
    				esitoGestioneToken.setDetails(detailsError);	
				}
				else {
					esitoGestioneToken.setDetails("Token non valido");	
				}
    			if(policyGestioneToken.isMessageErrorGenerateEmptyMessage()) {
    				esitoGestioneToken.setErrorMessage(WWWAuthenticateGenerator.buildErrorMessage(WWWAuthenticateErrorCode.invalid_token, policyGestioneToken.getRealm(), 
	    					policyGestioneToken.isMessageErrorGenerateGenericMessage(), esitoGestioneToken.getDetails()));   			
    			}
    			else {
    				esitoGestioneToken.setWwwAuthenticateErrorHeader(WWWAuthenticateGenerator.buildHeaderValue(WWWAuthenticateErrorCode.invalid_token, policyGestioneToken.getRealm(), 
	    					policyGestioneToken.isMessageErrorGenerateGenericMessage(), esitoGestioneToken.getDetails()));
    			} 	
    		}
    		
		}catch(Exception e){
			esitoGestioneToken.setDetails(e.getMessage());
			esitoGestioneToken.setEccezioneProcessamento(e);
    	}
		
		return esitoGestioneToken;
	}
	
	
	
	
	
	
	// ********* USER INFO TOKEN ****************** */
	
	public static EsitoGestioneToken userInfoToken(Logger log, AbstractDatiInvocazione datiInvocazione, 
			PdDContext pddContext, IProtocolFactory<?> protocolFactory,
			String token, boolean portaDelegata) throws Exception {
		EsitoGestioneToken esitoGestioneToken = null;
		
		if(GestoreToken.cacheToken==null){
			esitoGestioneToken = _userInfoToken(log, datiInvocazione, 
					pddContext, protocolFactory,
					token, portaDelegata);
		}
    	else{
    		String funzione = "UserInfo";
    		String keyCache = buildCacheKey(funzione, portaDelegata, token);

			synchronized (GestoreToken.cacheToken) {

				org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) GestoreToken.cacheToken.get(keyCache);
				if(response != null){
					if(response.getObject()!=null){
						GestoreToken.logger.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (method:"+funzione+") in cache.");
						esitoGestioneToken = (EsitoGestioneToken) response.getObject();
						esitoGestioneToken.setInCache(true);
					}else if(response.getException()!=null){
						GestoreToken.logger.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] (method:"+funzione+") in cache.");
						throw (Exception) response.getException();
					}else{
						GestoreToken.logger.error("In cache non e' presente ne un oggetto ne un'eccezione.");
					}
				}

				if(esitoGestioneToken==null) {
					// Effettuo la query
					GestoreToken.logger.debug("oggetto con chiave ["+keyCache+"] (method:"+funzione+") eseguo operazione...");
					esitoGestioneToken = _userInfoToken(log, datiInvocazione, 
							pddContext, protocolFactory,
							token, portaDelegata);
						
					// Aggiungo la risposta in cache (se esiste una cache)	
					// Sempre. Se la risposta non deve essere cachata l'implementazione può in alternativa:
					// - impostare una eccezione di processamento (che setta automaticamente noCache a true)
					// - impostare il noCache a true
					if(esitoGestioneToken!=null){
						esitoGestioneToken.setInCache(false); // la prima volta che lo recupero sicuramente non era in cache
						if(!esitoGestioneToken.isNoCache()){
							GestoreToken.logger.info("Aggiungo oggetto ["+keyCache+"] in cache");
							try{	
								org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
								responseCache.setObject(esitoGestioneToken);
								GestoreToken.cacheToken.put(keyCache,responseCache);
							}catch(UtilsException e){
								GestoreToken.logger.error("Errore durante l'inserimento in cache ["+keyCache+"]: "+e.getMessage());
							}
						}
					}else{
						throw new TokenException("Metodo (GestoreToken."+funzione+") ha ritornato un valore di esito null");
					}
				}
			}
    	}
		
		if(esitoGestioneToken.isValido()) {
			// ricontrollo tutte le date
			_validazioneInformazioniToken(esitoGestioneToken, datiInvocazione.getPolicyGestioneToken(), 
					datiInvocazione.getPolicyGestioneToken().isUserInfo_saveErrorInCache());
		}
		
		return esitoGestioneToken;
	}
	
	private static EsitoGestioneToken _userInfoToken(Logger log, AbstractDatiInvocazione datiInvocazione, 
			PdDContext pddContext, IProtocolFactory<?> protocolFactory,
			String token, boolean portaDelegata) {
		EsitoGestioneToken esitoGestioneToken = null;
		if(portaDelegata) {
			esitoGestioneToken = new EsitoGestioneTokenPortaDelegata();
		}
		else {
			esitoGestioneToken = new EsitoGestioneTokenPortaApplicativa();
		}
		
		esitoGestioneToken.setValido(false);
		esitoGestioneToken.setToken(token);
		
		try{
			PolicyGestioneToken policyGestioneToken = datiInvocazione.getPolicyGestioneToken();
    		
    		String detailsError = null;
			InformazioniToken informazioniToken = null;
			Exception eProcess = null;
			
			ITokenParser tokenParser = policyGestioneToken.getUserInfo_TokenParser();
			
			HttpResponse httpResponse = null;
			Integer httpResponseCode = null;
			byte[] risposta = null;
			try {
				httpResponse = http(log, policyGestioneToken, USER_INFO, token,
						pddContext, protocolFactory);
				risposta = httpResponse.getContent();
				httpResponseCode = httpResponse.getResultHTTPOperation();
			}catch(Exception e) {
				detailsError = "(Errore di Connessione) "+ e.getMessage();
				eProcess = e;
			}
			
			if(detailsError==null) {
				try {
					informazioniToken = new InformazioniToken(httpResponseCode, new String(risposta),tokenParser);
				}catch(Exception e) {
					detailsError = "Risposta del servizio di UserInfo non valida: "+e.getMessage();
					eProcess = e;
				}
			}
    		  		
    		if(informazioniToken!=null && informazioniToken.isValid()) {
    			esitoGestioneToken.setValido(true);
    			esitoGestioneToken.setInformazioniToken(informazioniToken);
    			esitoGestioneToken.setNoCache(false);
			}
    		else {
    			if(policyGestioneToken.isUserInfo_saveErrorInCache()) {
    				esitoGestioneToken.setNoCache(false);
    			}
    			else {
    				esitoGestioneToken.setNoCache(true);
    			}
    			esitoGestioneToken.setEccezioneProcessamento(eProcess);
    			if(detailsError!=null) {
    				esitoGestioneToken.setDetails(detailsError);	
				}
				else {
					esitoGestioneToken.setDetails("Token non valido");	
				}
    			if(policyGestioneToken.isMessageErrorGenerateEmptyMessage()) {
    				esitoGestioneToken.setErrorMessage(WWWAuthenticateGenerator.buildErrorMessage(WWWAuthenticateErrorCode.invalid_token, policyGestioneToken.getRealm(), 
	    					policyGestioneToken.isMessageErrorGenerateGenericMessage(), esitoGestioneToken.getDetails()));   			
    			}
    			else {
    				esitoGestioneToken.setWwwAuthenticateErrorHeader(WWWAuthenticateGenerator.buildHeaderValue(WWWAuthenticateErrorCode.invalid_token, policyGestioneToken.getRealm(), 
	    					policyGestioneToken.isMessageErrorGenerateGenericMessage(), esitoGestioneToken.getDetails()));
    			} 		
    		}
    		
		}catch(Exception e){
			esitoGestioneToken.setDetails(e.getMessage());
			esitoGestioneToken.setEccezioneProcessamento(e);
    	}
		
		return esitoGestioneToken;
	}
	
	
	
	
	
	
	// ********* FORWARD TOKEN ****************** */
	
	public static void forwardToken(Logger log, String idTransazione, AbstractDatiInvocazione datiInvocazione, EsitoPresenzaToken esitoPresenzaToken, 
			EsitoGestioneToken esitoValidazioneJWT, EsitoGestioneToken esitoIntrospection, EsitoGestioneToken esitoUserInfo, 
			InformazioniToken informazioniTokenNormalizzate,
			boolean portaDelegata) throws Exception {
		
		PolicyGestioneToken policyGestioneToken = datiInvocazione.getPolicyGestioneToken();
			
		TokenForward tokenForward = new TokenForward();
		String token = esitoPresenzaToken.getToken();
		
		boolean trasparente = false;
		String forwardTrasparenteMode = null;
		String forwardTrasparenteMode_header = null;
		String forwardTrasparenteMode_url = null;
		if(policyGestioneToken.isForwardToken()) {
			trasparente = policyGestioneToken.isForwardToken_trasparente();
			if(trasparente) {
				forwardTrasparenteMode = policyGestioneToken.getForwardToken_trasparenteMode();
				if(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_AS_RECEIVED.equals(forwardTrasparenteMode)) {
					forwardTrasparenteMode_header = esitoPresenzaToken.getHeaderHttp();
					forwardTrasparenteMode_url = esitoPresenzaToken.getPropertyUrl();
				}
				else if(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_RFC6750_HEADER.equals(forwardTrasparenteMode)) {
					forwardTrasparenteMode_header = HttpConstants.AUTHORIZATION;
				}
				else if(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_RFC6750_URL.equals(forwardTrasparenteMode)) {
					forwardTrasparenteMode_url = Costanti.RFC6750_URI_QUERY_PARAMETER_ACCESS_TOKEN;
				}
				else if(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_CUSTOM_HEADER.equals(forwardTrasparenteMode)) {
					forwardTrasparenteMode_header = policyGestioneToken.getForwardToken_trasparenteModeCustomHeader();
				} 
				else if(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_CUSTOM_URL.equals(forwardTrasparenteMode)) {
					forwardTrasparenteMode_url = policyGestioneToken.getForwardToken_trasparenteModeCustomUrl();
				} 
			}
		}
		
		boolean infoRaccolte = false;
		String forwardInformazioniRaccolteMode = null;
		Properties jwtSecurity = null;
		boolean encodeBase64 = false;
		boolean forwardValidazioneJWT = false;	
		String forwardValidazioneJWT_mode = null;
		String forwardValidazioneJWT_name = null;
		boolean forwardIntrospection = false;	
		String forwardIntrospection_mode = null;
		String forwardIntrospection_name = null;
		boolean forwardUserInfo = false;	
		String forwardUserInfo_mode = null;
		String forwardUserInfo_name = null;
		if(policyGestioneToken.isForwardToken()) {
			infoRaccolte = policyGestioneToken.isForwardToken_informazioniRaccolte();
			if(infoRaccolte) {
				forwardInformazioniRaccolteMode = policyGestioneToken.getForwardToken_informazioniRaccolteMode();
				encodeBase64 = policyGestioneToken.isForwardToken_informazioniRaccolteEncodeBase64();
				if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_OP2_JWS.equals(forwardInformazioniRaccolteMode) ||
						Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JWS.equals(forwardInformazioniRaccolteMode)) {
					jwtSecurity = policyGestioneToken.getProperties().get(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_SIGNATURE_PROP_REF_ID);
				}
				else if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JWE.equals(forwardInformazioniRaccolteMode)) {
					jwtSecurity = policyGestioneToken.getProperties().get(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_ENCRYP_PROP_REF_ID);
				}
				
				forwardValidazioneJWT = policyGestioneToken.isForwardToken_informazioniRaccolte_validazioneJWT();
				if(forwardValidazioneJWT) {
					forwardValidazioneJWT_mode = policyGestioneToken.getForwardToken_informazioniRaccolte_validazioneJWT_mode();
					if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_NO_OPENSPCOOP_CUSTOM_HEADER.equals(forwardValidazioneJWT_mode)) {
						forwardValidazioneJWT_name = policyGestioneToken.getForwardToken_informazioniRaccolte_validazioneJWT_mode_headerName();
					}
					else {
						forwardValidazioneJWT_name = policyGestioneToken.getForwardToken_informazioniRaccolte_validazioneJWT_mode_queryParameterName();
					}
				}
				
				forwardIntrospection = policyGestioneToken.isForwardToken_informazioniRaccolte_introspection();
				if(forwardIntrospection) {
					forwardIntrospection_mode = policyGestioneToken.getForwardToken_informazioniRaccolte_introspection_mode();
					if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_NO_OPENSPCOOP_CUSTOM_HEADER.equals(forwardIntrospection_mode)) {
						forwardIntrospection_name = policyGestioneToken.getForwardToken_informazioniRaccolte_introspection_mode_headerName();
					}
					else {
						forwardIntrospection_name = policyGestioneToken.getForwardToken_informazioniRaccolte_introspection_mode_queryParameterName();
					}
				}
				
				forwardUserInfo = policyGestioneToken.isForwardToken_informazioniRaccolte_userInfo();
				if(forwardUserInfo) {
					forwardUserInfo_mode = policyGestioneToken.getForwardToken_informazioniRaccolte_userInfo_mode();
					if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_NO_OPENSPCOOP_CUSTOM_HEADER.equals(forwardUserInfo_mode)) {
						forwardUserInfo_name = policyGestioneToken.getForwardToken_informazioniRaccolte_userInfo_mode_headerName();
					}
					else {
						forwardUserInfo_name = policyGestioneToken.getForwardToken_informazioniRaccolte_userInfo_mode_queryParameterName();
					}
				}
			}
		}
		
		// Elimino token ricevuto
		boolean delete = _deleteTokenReceived(datiInvocazione, esitoPresenzaToken, trasparente, forwardTrasparenteMode_header, forwardTrasparenteMode_url);
		
		if(trasparente) {
			
			// Forward trasparente
			
			if(delete) { 
				// la delete ha tenuto conto dell'opzione di forward prima di eliminare
				_forwardTokenTrasparente(token, esitoPresenzaToken, tokenForward, forwardTrasparenteMode, forwardTrasparenteMode_header, forwardTrasparenteMode_url);
			}
		}
		
		if(infoRaccolte) {
			
			// Forward informazioni raccolte
			
			_forwardInfomazioniRaccolte(portaDelegata, idTransazione, token, tokenForward, 
					esitoValidazioneJWT, esitoIntrospection, esitoUserInfo, 
					informazioniTokenNormalizzate,
					forwardInformazioniRaccolteMode, jwtSecurity, encodeBase64, 
					forwardValidazioneJWT, forwardValidazioneJWT_mode, forwardValidazioneJWT_name,
					forwardIntrospection, forwardIntrospection_mode, forwardIntrospection_name,
					forwardUserInfo, forwardUserInfo_mode, forwardUserInfo_name);
			
		}
		
		// Imposto token forward nel messaggio
		if(tokenForward.getUrl().size()>0 || tokenForward.getTrasporto().size()>0) {
			datiInvocazione.getMessage().addContextProperty(Costanti.MSG_CONTEXT_TOKEN_FORWARD, tokenForward);
		}

	}
	
	private static boolean _deleteTokenReceived(AbstractDatiInvocazione datiInvocazione, EsitoPresenzaToken esitoPresenzaToken,
			boolean trasparente, String forwardTrasparenteMode_header, String forwardTrasparenteMode_url) throws Exception {
		
		PolicyGestioneToken policyGestioneToken = datiInvocazione.getPolicyGestioneToken();
		
		boolean remove = false;
		if(esitoPresenzaToken.getHeaderHttp()!=null) {
			if(!HttpConstants.AUTHORIZATION.equals(esitoPresenzaToken.getHeaderHttp())) {
				if(!policyGestioneToken.isForwardToken()) {
					remove = true;
				}
				else if(!trasparente) {
					remove = true;
				}
				else if(!esitoPresenzaToken.getHeaderHttp().equals(forwardTrasparenteMode_header)) {
					remove = true;
				}
			}
			if(remove) {
				datiInvocazione.getMessage().getTransportRequestContext().removeParameterTrasporto(esitoPresenzaToken.getHeaderHttp());
			}
		}
		else if(esitoPresenzaToken.getPropertyUrl()!=null) {
			if(!policyGestioneToken.isForwardToken()) {
				remove = true;
			}
			else if(!trasparente) {
				remove = true;
			}
			else if(!esitoPresenzaToken.getPropertyUrl().equals(forwardTrasparenteMode_url)) {
				remove = true;
			}
			if(remove) {
				datiInvocazione.getMessage().getTransportRequestContext().removeParameterFormBased(esitoPresenzaToken.getPropertyUrl());
			}
		}
		else if(esitoPresenzaToken.getPropertyFormBased()!=null) {
			if(!policyGestioneToken.isForwardToken()) {
				remove = true;
			}
			else if(!trasparente) {
				remove = true;
			}
			if(remove) {
				byte[] contenuto = datiInvocazione.getMessage().castAsRestBinary().getContent(); // dovrebbe essere un binary
				// MyVariableOne=ValueOne&MyVariableTwo=ValueTwo
				String contenutoAsString = new String(contenuto);
				String [] split = contenutoAsString.split("&");
				StringBuffer bf = new StringBuffer();
				for (int i = 0; i < split.length; i++) {
					String nameValue = split[i];
					if(nameValue.contains("=")) {
						String [] tmp = nameValue.split("=");
						String name = tmp[0];
						String value = tmp[1];
						if(!esitoPresenzaToken.getPropertyFormBased().equals(name)) {
							bf.append(name).append("=").append(value);
						}
					}
					else {
						if(bf.length()>0) {
							bf.append("&");
						}
						bf.append(nameValue);
					}
				}
				if(bf.length()>0) {
					byte [] newContenuto = bf.toString().getBytes();
					datiInvocazione.getMessage().castAsRestBinary().updateContent(newContenuto);
				}
				else {
					datiInvocazione.getMessage().castAsRestBinary().updateContent(null);
				}
			}
		}
		return remove;
	}
	
	private static void _forwardTokenTrasparente(String token, EsitoPresenzaToken esitoPresenzaToken, TokenForward tokenForward,
			String forwardTrasparenteMode, String forwardTrasparenteMode_header, String forwardTrasparenteMode_url) throws Exception {
		if(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_AS_RECEIVED.equals(forwardTrasparenteMode)) {
			if(esitoPresenzaToken.getHeaderHttp()!=null) {
				if(HttpConstants.AUTHORIZATION.equals(esitoPresenzaToken.getHeaderHttp())) {
					tokenForward.getTrasporto().put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+token);
				}
				else {
					tokenForward.getTrasporto().put(esitoPresenzaToken.getHeaderHttp(), token);
				}
			}
			else if(esitoPresenzaToken.getPropertyUrl()!=null) {
				tokenForward.getUrl().put(esitoPresenzaToken.getPropertyUrl(), token);
			}
			else if(esitoPresenzaToken.getPropertyFormBased()!=null) {
				throw new Exception("Configurazione non supportata"); // non dovrebbe mai entrare in questo ramo poichè il token non viene eliminato
			}
		}
		else if(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_RFC6750_HEADER.equals(forwardTrasparenteMode)) {
			tokenForward.getTrasporto().put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+token);
		}
		else if(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_RFC6750_URL.equals(forwardTrasparenteMode)) {
			tokenForward.getUrl().put(Costanti.RFC6750_URI_QUERY_PARAMETER_ACCESS_TOKEN, token);
		}
		else if(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_CUSTOM_HEADER.equals(forwardTrasparenteMode)) {
			tokenForward.getTrasporto().put(forwardTrasparenteMode_header, token);
		}
		else if(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_CUSTOM_URL.equals(forwardTrasparenteMode)) {
			tokenForward.getUrl().put(forwardTrasparenteMode_url, token);
		}
	}
	
	private static void _forwardInfomazioniRaccolte(boolean portaDelegata, String idTransazione, String token, TokenForward tokenForward,
			EsitoGestioneToken esitoValidazioneJWT, EsitoGestioneToken esitoIntrospection, EsitoGestioneToken esitoUserInfo,
			InformazioniToken informazioniTokenNormalizzate,
			String forwardInforRaccolteMode, Properties jwtSecurity, boolean encodeBase64,
			boolean forwardValidazioneJWT, String forwardValidazioneJWT_mode, String forwardValidazioneJWT_name,
			boolean forwardIntrospection, String forwardIntrospection_mode, String forwardIntrospection_name,
			boolean forwardUserInfo, String forwardUserInfo_mode, String forwardUserInfo_name) throws Exception {
		
		if(informazioniTokenNormalizzate==null) {
			return; // può succedere nei casi warning only, significa che non vi sono token validi
		}
		
		if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_OP2_HEADERS.equals(forwardInforRaccolteMode) ||
				Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_OP2_JSON.equals(forwardInforRaccolteMode) ||
				Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_OP2_JWS.equals(forwardInforRaccolteMode)) {
			
			OpenSPCoop2Properties properties = OpenSPCoop2Properties.getInstance();
			java.util.Properties headerNames = null;
			HashMap<String, Boolean> set = null;
			JSONUtils jsonUtils = null;
			ObjectNode jsonNode = null;
			boolean op2headers = Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_OP2_HEADERS.equals(forwardInforRaccolteMode);
			SimpleDateFormat sdf = null;
			if(op2headers) {
				headerNames = properties.getKeyValue_gestioneTokenHeaderIntegrazioneTrasporto();
				if(portaDelegata) {
					set = properties.getKeyPASetEnabled_gestioneTokenHeaderIntegrazioneTrasporto();
				}
				else {
					set = properties.getKeyPASetEnabled_gestioneTokenHeaderIntegrazioneTrasporto();
				}
				String pattern = properties.getGestioneTokenFormatDate();
				if(pattern!=null && !"".equals(pattern)) {
					sdf = new SimpleDateFormat(pattern);
				}
			}
			else {
				if(portaDelegata) {
					set = properties.getKeyPASetEnabled_gestioneTokenHeaderIntegrazioneJson();
				}
				else {
					set = properties.getKeyPASetEnabled_gestioneTokenHeaderIntegrazioneJson();
				}
				jsonUtils = JSONUtils.getInstance();
				jsonNode = jsonUtils.newObjectNode();
				jsonNode.put("id", idTransazione);
			}
			
			if(informazioniTokenNormalizzate.getIss()!=null) {
				if(set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_ISSUER)) {
					if(op2headers) {
						tokenForward.getTrasporto().put(headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_ISSUER), informazioniTokenNormalizzate.getIss());
					}
					else {
						jsonNode.put("issuer", informazioniTokenNormalizzate.getIss());
					}
				}
			}
			if(informazioniTokenNormalizzate.getSub()!=null) {
				if(set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_SUBJECT)) {
					if(op2headers) {
						tokenForward.getTrasporto().put(headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_SUBJECT), informazioniTokenNormalizzate.getSub());
					}
					else {
						jsonNode.put("subject", informazioniTokenNormalizzate.getSub());
					}
				}
			}
			if(informazioniTokenNormalizzate.getUsername()!=null) {
				if(set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_USERNAME)) {
					if(op2headers) {
						tokenForward.getTrasporto().put(headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_USERNAME), informazioniTokenNormalizzate.getUsername());
					}
					else {
						jsonNode.put("username", informazioniTokenNormalizzate.getUsername());
					}
				}
			}
			if(informazioniTokenNormalizzate.getAud()!=null) {
				if(set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_AUDIENCE)) {
					if(op2headers) {
						tokenForward.getTrasporto().put(headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_AUDIENCE), informazioniTokenNormalizzate.getAud());
					}
					else {
						jsonNode.put("audience", informazioniTokenNormalizzate.getAud());
					}
				}
			}
			if(informazioniTokenNormalizzate.getClientId()!=null) {
				if(set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_CLIENT_ID)) {
					if(op2headers) {
						tokenForward.getTrasporto().put(headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_CLIENT_ID), informazioniTokenNormalizzate.getClientId());
					}
					else {
						jsonNode.put("clientId", informazioniTokenNormalizzate.getClientId());
					}
				}
			}
			if(informazioniTokenNormalizzate.getIat()!=null) {
				if(set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_ISSUED_AT)) {
					if(op2headers) {
						String value = null;
						if(sdf!=null) {
							value = sdf.format(informazioniTokenNormalizzate.getIat());
						}
						else {
							value = (informazioniTokenNormalizzate.getIat().getTime() / 1000) + "";
						}
						tokenForward.getTrasporto().put(headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_ISSUED_AT), value);
					}
					else {
						jsonNode.put("iat", jsonUtils.getDateFormat().format(informazioniTokenNormalizzate.getIat()));
					}
				}
			}
			if(informazioniTokenNormalizzate.getExp()!=null) {
				if(set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_EXPIRED)) {
					if(op2headers) {
						String value = null;
						if(sdf!=null) {
							value = sdf.format(informazioniTokenNormalizzate.getExp());
						}
						else {
							value = (informazioniTokenNormalizzate.getExp().getTime() / 1000) + "";
						}
						tokenForward.getTrasporto().put(headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_EXPIRED), value);
					}
					else {
						jsonNode.put("expire", jsonUtils.getDateFormat().format(informazioniTokenNormalizzate.getExp()));
					}
				}
			}
			if(informazioniTokenNormalizzate.getNbf()!=null) {
				if(set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_NBF)) {
					if(op2headers) {
						String value = null;
						if(sdf!=null) {
							value = sdf.format(informazioniTokenNormalizzate.getNbf());
						}
						else {
							value = (informazioniTokenNormalizzate.getNbf().getTime() / 1000) + "";
						}
						tokenForward.getTrasporto().put(headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_NBF), value);
					}
					else {
						jsonNode.put("nbf", jsonUtils.getDateFormat().format(informazioniTokenNormalizzate.getNbf()));
					}
				}
			}
			if(informazioniTokenNormalizzate.getRoles()!=null && informazioniTokenNormalizzate.getRoles().size()>0) {
				if(set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_ROLES)) {
					ArrayNode array = null;
					StringBuffer bf = new StringBuffer();
					if(!op2headers) {
						array =  jsonUtils.newArrayNode();
					}
					for (String role : informazioniTokenNormalizzate.getRoles()) {
						if(op2headers) {
							if(bf.length()>0) {
								bf.append(" ");
							}
							bf.append(role);
						}
						else {
							array.add(role);
						}
					}
					if(op2headers) {
						tokenForward.getTrasporto().put(headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_ROLES), bf.toString());
					}
					else {
						jsonNode.set("roles", array);
					}
				}
			}
			if(informazioniTokenNormalizzate.getScopes()!=null && informazioniTokenNormalizzate.getScopes().size()>0) {
				if(set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_SCOPES)) {
					ArrayNode array = null;
					StringBuffer bf = new StringBuffer();
					if(!op2headers) {
						array =  jsonUtils.newArrayNode();
					}
					for (String scope : informazioniTokenNormalizzate.getScopes()) {
						if(op2headers) {
							if(bf.length()>0) {
								bf.append(" ");
							}
							bf.append(scope);
						}
						else {
							array.add(scope);
						}
					}
					if(op2headers) {
						tokenForward.getTrasporto().put(headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_SCOPES), bf.toString());
					}
					else {
						jsonNode.set("scopes", array);
					}
				}
			}
			if(informazioniTokenNormalizzate.getUserInfo()!=null) {
				ObjectNode userInfoNode = null;
				if(!op2headers) {
					userInfoNode = jsonUtils.newObjectNode();
				}
				
				boolean add = false;
				
				if(informazioniTokenNormalizzate.getUserInfo().getFullName()!=null) {
					if(set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_FULL_NAME)) {
						if(op2headers) {
							tokenForward.getTrasporto().put(headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_FULL_NAME), informazioniTokenNormalizzate.getUserInfo().getFullName());
						}
						else {
							userInfoNode.put("fullName", informazioniTokenNormalizzate.getUserInfo().getFullName());
							add = true;
						}
					}
				}
				if(informazioniTokenNormalizzate.getUserInfo().getFirstName()!=null) {
					if(set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_FIRST_NAME)) {
						if(op2headers) {
							tokenForward.getTrasporto().put(headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_FIRST_NAME), informazioniTokenNormalizzate.getUserInfo().getFirstName());
						}
						else {
							userInfoNode.put("firstName", informazioniTokenNormalizzate.getUserInfo().getFirstName());
							add = true;
						}
					}
				}
				if(informazioniTokenNormalizzate.getUserInfo().getMiddleName()!=null) {
					if(set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_MIDDLE_NAME)) {
						if(op2headers) {
							tokenForward.getTrasporto().put(headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_MIDDLE_NAME), informazioniTokenNormalizzate.getUserInfo().getMiddleName());
						}
						else {
							userInfoNode.put("middleName", informazioniTokenNormalizzate.getUserInfo().getMiddleName());
							add = true;
						}
					}
				}
				if(informazioniTokenNormalizzate.getUserInfo().getFamilyName()!=null) {
					if(set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_FAMILY_NAME)) {
						if(op2headers) {
							tokenForward.getTrasporto().put(headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_FAMILY_NAME), informazioniTokenNormalizzate.getUserInfo().getFamilyName());
						}
						else {
							userInfoNode.put("familyName", informazioniTokenNormalizzate.getUserInfo().getFamilyName());
							add = true;
						}
					}
				}
				if(informazioniTokenNormalizzate.getUserInfo().getEMail()!=null) {
					if(set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_EMAIL)) {
						if(op2headers) {
							tokenForward.getTrasporto().put(headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_EMAIL), informazioniTokenNormalizzate.getUserInfo().getEMail());
						}
						else {
							userInfoNode.put("eMail", informazioniTokenNormalizzate.getUserInfo().getEMail());
							add = true;
						}
					}
				}
				
				if(!op2headers && add) {
					jsonNode.set("userInfo", userInfoNode);
				}
			}	
			List<String> listCustomClaims = properties.getCustomClaimsKeys_gestioneTokenForward();
			if(listCustomClaims!=null && !listCustomClaims.isEmpty()) {
				
				ArrayNode customClaimsNode = null;
				if(!op2headers) {
					customClaimsNode = jsonUtils.newArrayNode();
				}
				
				boolean add = false;
				
				for (String claimKey : listCustomClaims) {
				
					String claimName = properties.getCustomClaimsName_gestioneTokenHeaderIntegrazione(claimKey);
					
					if(informazioniTokenNormalizzate.getClaims()!=null && informazioniTokenNormalizzate.getClaims().containsKey(claimName)) {
						
						String claimValue = informazioniTokenNormalizzate.getClaims().get(claimName);
						String headerName = null;
						if(claimValue!=null) {
							boolean setCustomClaims = false;
							if(op2headers) {
								headerName = properties.getCustomClaimsHeaderName_gestioneTokenHeaderIntegrazioneTrasporto(claimKey);
								if(portaDelegata) {
									setCustomClaims = properties.getCustomClaimsKeyPDSetEnabled_gestioneTokenHeaderIntegrazioneTrasporto(claimKey);
								}
								else {
									setCustomClaims = properties.getCustomClaimsKeyPASetEnabled_gestioneTokenHeaderIntegrazioneTrasporto(claimKey);
								}
							}
							else {
								headerName = properties.getCustomClaimsJsonPropertyName_gestioneTokenHeaderIntegrazioneJson(claimKey);
								if(portaDelegata) {
									setCustomClaims = properties.getCustomClaimsKeyPDSetEnabled_gestioneTokenHeaderIntegrazioneJson(claimKey);
								}
								else {
									setCustomClaims = properties.getCustomClaimsKeyPASetEnabled_gestioneTokenHeaderIntegrazioneJson(claimKey);
								}
							}
							
							if(setCustomClaims) {
								if(op2headers) {
									tokenForward.getTrasporto().put(headerName, claimValue);
								}
								else {
									ObjectNode propertyNode = jsonUtils.newObjectNode();
									propertyNode.put("name", headerName);
									propertyNode.put("value", claimValue);
									customClaimsNode.add(propertyNode);
									add = true;
								}
							}
						}
					}
					
				}
				
				if(!op2headers && add) {
					jsonNode.set("claims", customClaimsNode);
				}
			}

			Date processTime = DateManager.getDate();
			if(set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_PROCESS_TIME)) {
				if(op2headers) {
					String value = null;
					if(sdf!=null) {
						value = sdf.format(processTime);
					}
					else {
						value = (processTime.getTime() / 1000) + "";
					}
					tokenForward.getTrasporto().put(headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_PROCESS_TIME), value);
				}
				else {
					jsonNode.put("processTime", jsonUtils.getDateFormat().format(informazioniTokenNormalizzate.getIat()));
				}
			}
			
			if(!op2headers) {
				String json = jsonUtils.toString(jsonNode);
				if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_OP2_JSON.equals(forwardInforRaccolteMode)) {
					String headerName = properties.getGestioneTokenHeaderTrasportoJSON();
					if(encodeBase64) {
						tokenForward.getTrasporto().put(headerName, Base64Utilities.encodeAsString(json.getBytes()));
					}
					else {
						tokenForward.getTrasporto().put(headerName, json);
					}
				}
				else {
					// JWS Compact   			
	    			JsonSignature jsonCompactSignature = new JsonSignature(jwtSecurity,JOSERepresentation.COMPACT);
	    			String compact = jsonCompactSignature.sign(json);
	    			String headerName = properties.getGestioneTokenHeaderTrasportoJWT();
	    			tokenForward.getTrasporto().put(headerName, compact);
				}
			}
		} 
		else {
			
			if(forwardValidazioneJWT && esitoValidazioneJWT!=null && esitoValidazioneJWT.isValido() && 
					esitoValidazioneJWT.getInformazioniToken()!=null &&
					esitoValidazioneJWT.getInformazioniToken().getRawResponse()!=null) {
				
				String value = esitoValidazioneJWT.getInformazioniToken().getRawResponse();
				if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JWS.equals(forwardInforRaccolteMode)) {
					// JWS Compact   			
	    			JsonSignature jsonCompactSignature = new JsonSignature(jwtSecurity,JOSERepresentation.COMPACT);
	    			value = jsonCompactSignature.sign(value);
				}
				else if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JWE.equals(forwardInforRaccolteMode)) {
					// JWE Compact
					JsonEncrypt jsonCompactEncrypt = new JsonEncrypt(jwtSecurity,JOSERepresentation.COMPACT);
					value = jsonCompactEncrypt.encrypt(value);
				}
				else {
					//Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JSON
					if(encodeBase64) {
						value = Base64Utilities.encodeAsString(value.getBytes());
					}
				}
				if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_NO_OPENSPCOOP_CUSTOM_HEADER.equals(forwardValidazioneJWT_mode)) {
					tokenForward.getTrasporto().put(forwardValidazioneJWT_name, value);
				}
				else {
					tokenForward.getUrl().put(forwardValidazioneJWT_name, value);
				}
				
			}
			
			if(forwardIntrospection && esitoIntrospection!=null && esitoIntrospection.isValido() && 
					esitoIntrospection.getInformazioniToken()!=null &&
					esitoIntrospection.getInformazioniToken().getRawResponse()!=null) {
				
				String value = esitoIntrospection.getInformazioniToken().getRawResponse();
				if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JWS.equals(forwardInforRaccolteMode)) {
					// JWS Compact   			
	    			JsonSignature jsonCompactSignature = new JsonSignature(jwtSecurity,JOSERepresentation.COMPACT);
	    			value = jsonCompactSignature.sign(value);
				}
				else if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JWE.equals(forwardInforRaccolteMode)) {
					// JWE Compact
					JsonEncrypt jsonCompactEncrypt = new JsonEncrypt(jwtSecurity,JOSERepresentation.COMPACT);
					value = jsonCompactEncrypt.encrypt(value);
				}
				else {
					//Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JSON
					if(encodeBase64) {
						value = Base64Utilities.encodeAsString(value.getBytes());
					}
				}
				if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_NO_OPENSPCOOP_CUSTOM_HEADER.equals(forwardIntrospection_mode)) {
					tokenForward.getTrasporto().put(forwardIntrospection_name, value);
				}
				else {
					tokenForward.getUrl().put(forwardIntrospection_name, value);
				}
				
			}
			
			if(forwardUserInfo && esitoUserInfo!=null && esitoUserInfo.isValido() && 
					esitoUserInfo.getInformazioniToken()!=null &&
					esitoUserInfo.getInformazioniToken().getRawResponse()!=null) {
				
				String value = esitoUserInfo.getInformazioniToken().getRawResponse();
				if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JWS.equals(forwardInforRaccolteMode)) {
					// JWS Compact   			
	    			JsonSignature jsonCompactSignature = new JsonSignature(jwtSecurity,JOSERepresentation.COMPACT);
	    			value = jsonCompactSignature.sign(value);
				}
				else if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JWE.equals(forwardInforRaccolteMode)) {
					// JWE Compact
					JsonEncrypt jsonCompactEncrypt = new JsonEncrypt(jwtSecurity,JOSERepresentation.COMPACT);
					value = jsonCompactEncrypt.encrypt(value);
				}
				else {
					//Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JSON
					if(encodeBase64) {
						value = Base64Utilities.encodeAsString(value.getBytes());
					}
				}
				if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_NO_OPENSPCOOP_CUSTOM_HEADER.equals(forwardUserInfo_mode)) {
					tokenForward.getTrasporto().put(forwardUserInfo_name, value);
				}
				else {
					tokenForward.getUrl().put(forwardUserInfo_name, value);
				}
				
			}
			
		}
		
	}
	
	public static List<InformazioniToken> getInformazioniTokenValide(EsitoGestioneToken esitoValidazioneJWT, EsitoGestioneToken esitoIntrospection, EsitoGestioneToken esitoUserInfo){
		List<InformazioniToken> list = new ArrayList<>();
		if(esitoValidazioneJWT!=null && esitoValidazioneJWT.isValido() && esitoValidazioneJWT.getInformazioniToken()!=null) {
			list.add(esitoValidazioneJWT.getInformazioniToken());
		}
		if(esitoIntrospection!=null && esitoIntrospection.isValido() && esitoIntrospection.getInformazioniToken()!=null) {
			list.add(esitoIntrospection.getInformazioniToken());
		}
		if(esitoUserInfo!=null && esitoUserInfo.isValido() && esitoUserInfo.getInformazioniToken()!=null) {
			list.add(esitoUserInfo.getInformazioniToken());
		}
		return list;
	}
	
	public static InformazioniToken normalizeInformazioniToken(List<InformazioniToken> list) throws Exception {
		if(list.size()==1) {
			return list.get(0);
		}
		else {
			return new InformazioniToken(list.toArray(new InformazioniToken[1]));
		}
	}
	
	
	
	// ********* UTILITIES INTERNE ****************** */
	
	private static final String format = "yyyy-MM-dd HH:mm:ss.SSS";
	
	private static void _validazioneInformazioniToken(EsitoGestioneToken esitoGestioneToken, PolicyGestioneToken policyGestioneToken, boolean saveErrorInCache) throws Exception {
		
		Date now = DateManager.getDate();
		
		if(esitoGestioneToken.isValido()) {			
			if(esitoGestioneToken.getInformazioniToken().getExp()!=null) {				
				/*
				 *   The "exp" (expiration time) claim identifies the expiration time on
   				 *   or after which the JWT MUST NOT be accepted for processing.  The
   				 *   processing of the "exp" claim requires that the current date/time
   				 *   MUST be before the expiration date/time listed in the "exp" claim.
				 **/
				if(!now.before(esitoGestioneToken.getInformazioniToken().getExp())){
					esitoGestioneToken.setValido(false);
					esitoGestioneToken.setDetails("Token expired");
					esitoGestioneToken.setErrorMessage(WWWAuthenticateGenerator.buildErrorMessage(WWWAuthenticateErrorCode.invalid_token, policyGestioneToken.getRealm(), 
	    					false, // ritorno l'errore preciso in questo caso // policyGestioneToken.isGenericError(), 
	    					esitoGestioneToken.getDetails()));  
				}
			}
			
		}
			
		if(esitoGestioneToken.isValido()) {
			if(esitoGestioneToken.getInformazioniToken().getNbf()!=null) {				
				/*
				 *   The "nbf" (not before) claim identifies the time before which the JWT
				 *   MUST NOT be accepted for processing.  The processing of the "nbf"
				 *   claim requires that the current date/time MUST be after or equal to
				 *   the not-before date/time listed in the "nbf" claim. 
				 **/
				if(!esitoGestioneToken.getInformazioniToken().getNbf().before(now)){
					esitoGestioneToken.setValido(false);
					SimpleDateFormat sdf = new SimpleDateFormat(format);
					esitoGestioneToken.setDetails("Token not usable before "+sdf.format(esitoGestioneToken.getInformazioniToken().getNbf()));
					esitoGestioneToken.setErrorMessage(WWWAuthenticateGenerator.buildErrorMessage(WWWAuthenticateErrorCode.invalid_token, policyGestioneToken.getRealm(), 
	    					false, // ritorno l'errore preciso in questo caso // policyGestioneToken.isGenericError(), 
	    					esitoGestioneToken.getDetails()));  
				}
			}
		}
		
		if(esitoGestioneToken.isValido()) {
			if(esitoGestioneToken.getInformazioniToken().getIat()!=null) {				
				/*
				 *   The "iat" (issued at) claim identifies the time at which the JWT was
   				 *   issued.  This claim can be used to determine the age of the JWT.
   				 *   The iat Claim can be used to reject tokens that were issued too far away from the current time, 
   				 *   limiting the amount of time that nonces need to be stored to prevent attacks. The acceptable range is Client specific. 
				 **/
				Integer old = OpenSPCoop2Properties.getInstance().getGestioneToken_iatTimeCheck_milliseconds();
				if(old!=null) {
					Date oldMax = new Date((DateManager.getTimeMillis() - old.intValue()));
					if(esitoGestioneToken.getInformazioniToken().getIat().before(oldMax)) {
						esitoGestioneToken.setValido(false);
						SimpleDateFormat sdf = new SimpleDateFormat(format);
						esitoGestioneToken.setDetails("Token expired; iat time '"+sdf.format(esitoGestioneToken.getInformazioniToken().getIat())+"' too old");
						esitoGestioneToken.setErrorMessage(WWWAuthenticateGenerator.buildErrorMessage(WWWAuthenticateErrorCode.invalid_token, policyGestioneToken.getRealm(), 
		    					false, // ritorno l'errore preciso in questo caso // policyGestioneToken.isGenericError(), 
		    					esitoGestioneToken.getDetails()));  
					}
				}
			}
			
		}
		
		if(esitoGestioneToken.isValido()==false) {
			if(saveErrorInCache) {
				esitoGestioneToken.setNoCache(false);
			}
			else {
				esitoGestioneToken.setNoCache(true);
			}
		}
	}
	
	private static String buildCacheKey(String funzione, boolean portaDelegata, String token) {
    	StringBuffer bf = new StringBuffer(funzione);
    	bf.append("_");
    	if(portaDelegata){
    		bf.append("PD");
    	}
    	else {
    		bf.append("PA");
    	}
    	bf.append("_");
    	bf.append(token);
    	return bf.toString();
    }
	
	private static final boolean INTROSPECTION = true;
	private static final boolean USER_INFO = false;
	private static HttpResponse http(Logger log, PolicyGestioneToken policyGestioneToken, boolean introspection, String token,
			PdDContext pddContext, IProtocolFactory<?> protocolFactory) throws Exception {
		
		// *** Raccola Parametri ***
		
		String endpoint = null;
		if(introspection) {
			endpoint = policyGestioneToken.getIntrospection_endpoint();
		}else {
			endpoint = policyGestioneToken.getUserInfo_endpoint();
		}
		
		TipoTokenRequest tipoTokenRequest = null;
		String positionTokenName = null;
		if(introspection) {
			tipoTokenRequest = policyGestioneToken.getIntrospection_tipoTokenRequest();
		}else {
			tipoTokenRequest = policyGestioneToken.getUserInfo_tipoTokenRequest();
		}
		switch (tipoTokenRequest) {
		case authorization:
			break;
		case header:
			if(introspection) {
				positionTokenName = policyGestioneToken.getIntrospection_tipoTokenRequest_headerName();
			}else {
				positionTokenName = policyGestioneToken.getUserInfo_tipoTokenRequest_headerName();
			}
			break;
		case url:
			if(introspection) {
				positionTokenName = policyGestioneToken.getIntrospection_tipoTokenRequest_urlPropertyName();
			}else {
				positionTokenName = policyGestioneToken.getUserInfo_tipoTokenRequest_urlPropertyName();
			}
			break;
		case form:
			if(introspection) {
				positionTokenName = policyGestioneToken.getIntrospection_tipoTokenRequest_formPropertyName();
			}else {
				positionTokenName = policyGestioneToken.getUserInfo_tipoTokenRequest_formPropertyName();
			}
			break;
		}
		
		String contentType = null;
		if(introspection) {
			contentType = policyGestioneToken.getIntrospection_contentType();
		}else {
			contentType = policyGestioneToken.getUserInfo_contentType();
		}
		
		HttpRequestMethod httpMethod = null;
		if(introspection) {
			httpMethod = policyGestioneToken.getIntrospection_httpMethod();
		}else {
			httpMethod = policyGestioneToken.getUserInfo_httpMethod();
		}
		
		
		// Nell'endpoint config ci finisce i timeout e la configurazione proxy
		Properties endpointConfig = policyGestioneToken.getProperties().get(Costanti.POLICY_ENDPOINT_CONFIG);
		
		boolean https = policyGestioneToken.isEndpointHttps();
		boolean httpsClient = false;
		Properties sslConfig = null;
		Properties sslClientConfig = null;
		if(https) {
			sslConfig = policyGestioneToken.getProperties().get(Costanti.POLICY_ENDPOINT_SSL_CONFIG);
			if(introspection) {
				httpsClient = policyGestioneToken.isIntrospection_httpsAuthentication();
			}else {
				httpsClient = policyGestioneToken.isUserInfo_httpsAuthentication();
			}
			if(httpsClient) {
				sslClientConfig = policyGestioneToken.getProperties().get(Costanti.POLICY_ENDPOINT_SSL_CLIENT_CONFIG);
			}
		}
		
		boolean basic = false;
		String username = null;
		String password = null;
		if(introspection) {
			basic = policyGestioneToken.isIntrospection_basicAuthentication();
		}else {
			basic = policyGestioneToken.isUserInfo_basicAuthentication();
		}
		if(basic) {
			if(introspection) {
				username = policyGestioneToken.getIntrospection_basicAuthentication_username();
				password = policyGestioneToken.getIntrospection_basicAuthentication_password();
			}
			else {
				username = policyGestioneToken.getUserInfo_basicAuthentication_username();
				password = policyGestioneToken.getUserInfo_basicAuthentication_password();
			}
		}
		
		boolean bearer = false;
		String bearerToken = null;
		if(introspection) {
			bearer = policyGestioneToken.isIntrospection_bearerAuthentication();
		}else {
			bearer = policyGestioneToken.isUserInfo_bearerAuthentication();
		}
		if(bearer) {
			if(introspection) {
				bearerToken = policyGestioneToken.getIntrospection_beareAuthentication_token();
			}
			else {
				bearerToken = policyGestioneToken.getUserInfo_beareAuthentication_token();
			}
		}
		
		
		
		// *** Definizione Connettore ***
		
		ConnettoreMsg connettoreMsg = new ConnettoreMsg();
		ConnettoreBaseHTTP connettore = null;
		if(https) {
			connettoreMsg.setTipoConnettore(TipiConnettore.HTTPS.getNome());
			connettore = new ConnettoreHTTPS();
		}
		else {
			connettoreMsg.setTipoConnettore(TipiConnettore.HTTP.getNome());
			connettore = new ConnettoreHTTP();
		}
		connettore.init(pddContext, protocolFactory);
		
		if(basic){
			InvocazioneCredenziali credenziali = new InvocazioneCredenziali();
			credenziali.setUser(username);
			credenziali.setPassword(password);
			connettoreMsg.setCredenziali(credenziali);
		}
		
		connettoreMsg.setConnectorProperties(new java.util.Hashtable<String,String>());
		connettoreMsg.getConnectorProperties().put(CostantiConnettori.CONNETTORE_LOCATION, endpoint);
		addProperties(connettoreMsg, endpointConfig);
		if(https) {
			addProperties(connettoreMsg, sslConfig);
			if(httpsClient) {
				addProperties(connettoreMsg, sslClientConfig);
			}
		}
		
		byte[] content = null;
		
		TransportRequestContext transportRequestContext = new TransportRequestContext();
		transportRequestContext.setRequestType(httpMethod.name());
		transportRequestContext.setParametersTrasporto(new Properties());
		if(bearer) {
			String authorizationHeader = HttpConstants.AUTHORIZATION_PREFIX_BEARER+bearerToken;
			transportRequestContext.getParametersTrasporto().put(HttpConstants.AUTHORIZATION, authorizationHeader);
		}
		if(contentType!=null) {
			transportRequestContext.getParametersTrasporto().put(HttpConstants.CONTENT_TYPE, contentType);
		}
		switch (tipoTokenRequest) {
		case authorization:
			transportRequestContext.removeParameterTrasporto(HttpConstants.AUTHORIZATION);
			String authorizationHeader = HttpConstants.AUTHORIZATION_PREFIX_BEARER+token;
			transportRequestContext.getParametersTrasporto().put(HttpConstants.AUTHORIZATION, authorizationHeader);
			break;
		case header:
			transportRequestContext.getParametersTrasporto().put(positionTokenName, token);
			break;
		case url:
			transportRequestContext.setParametersFormBased(new Properties());
			transportRequestContext.getParametersFormBased().put(positionTokenName, token);
			break;
		case form:
			transportRequestContext.removeParameterTrasporto(HttpConstants.CONTENT_TYPE);
			transportRequestContext.getParametersTrasporto().put(HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_X_WWW_FORM_URLENCODED);
			content = (positionTokenName+"="+token).getBytes();
			break;
		}
		
		OpenSPCoop2MessageParseResult pr = OpenSPCoop2MessageFactory.getMessageFactory().createMessage(MessageType.BINARY, transportRequestContext, content);
		OpenSPCoop2Message msg = pr.getMessage_throwParseException();
		connettoreMsg.setRequestMessage(msg);
		connettoreMsg.setGenerateErrorWithConnectorPrefix(false);
		connettore.setHttpMethod(msg);
		
		boolean send = connettore.send(connettoreMsg);
		if(send==false) {
			if(connettore.getEccezioneProcessamento()!=null) {
				throw new Exception(connettore.getErrore(), connettore.getEccezioneProcessamento());
			}
			else {
				throw new Exception(connettore.getErrore());
			}
		}
		
		OpenSPCoop2Message msgResponse = connettore.getResponse();
		ByteArrayOutputStream bout = null;
		if(msgResponse!=null) {
			bout = new ByteArrayOutputStream();
			if(msgResponse!=null) {
				msgResponse.writeTo(bout, true);
			}
			bout.flush();
			bout.close();
		}
		
		HttpResponse httpResponse = new HttpResponse();
		httpResponse.setResultHTTPOperation(connettore.getCodiceTrasporto());
		
		if(connettore.getCodiceTrasporto() >= 200 &&  connettore.getCodiceTrasporto() < 299) {
			String msgSuccess = "Connessione completata con successo (codice trasporto: "+connettore.getCodiceTrasporto()+")";
			if(bout!=null && bout.size()>0) {
				log.debug(msgSuccess);
				httpResponse.setContent(bout.toByteArray());
				return httpResponse;
			}
			else {
				throw new Exception(msgSuccess+"; non è pervenuta alcuna risposta");
			}
		}
		else {
			String msgError = "Connessione terminata con errore (codice trasporto: "+connettore.getCodiceTrasporto()+")";
			if(bout!=null && bout.size()>0) {
				log.debug(msgError+": "+bout.toString());
				httpResponse.setContent(bout.toByteArray());
				return httpResponse;
			}
			else {
				log.error(msgError);
				throw new Exception(msgError);
			}
		}
		
		
	}
	
	private static void addProperties(ConnettoreMsg connettoreMsg, Properties p) {
		if(p!=null && p.size()>0) {
			Enumeration<?> en = p.propertyNames();
			while (en.hasMoreElements()) {
				Object oKey = (Object) en.nextElement();
				if(oKey!=null) {
					String key = (String) oKey;
					String value = p.getProperty(key);
					connettoreMsg.getConnectorProperties().put(key,value);
				}
			}
		}
	}
}

