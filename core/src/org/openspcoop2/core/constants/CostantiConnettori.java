package org.openspcoop2.core.constants;

/**
 * CostantiConnettori
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author: mergefairy $
 * @version $Rev: 10512 $, $Date: 2015-01-19 15:11:51 +0100 (Mon, 19 Jan 2015) $
 */
public class CostantiConnettori {

	/** HTTP HEADER */
	
	public final static String HEADER_HTTP_REDIRECT_LOCATION = "Location";
	
	public final static String HEADER_HTTP_AUTHORIZATION = "Authorization";
	public final static String HEADER_HTTP_AUTHORIZATION_PREFIX_BASIC = "Basic ";
	
	// Check per evitare la coppia che ha come chiave null e come valore HTTP OK 200
	public final static String HEADER_HTTP_RETURN_CODE = "ReturnCode";
	
	
	/** COMMONS PROPERTIES */
	
	public final static String CONNETTORE_LOCATION = "location";
	public final static String CONNETTORE_DEBUG = "debug";
    public static final String CONNETTORE_USERNAME = "user";
    public static final String CONNETTORE_PASSWORD = "password";
    public static final String CONNETTORE_SUBJECT = "subject";
    public static final String CONNETTORE_CONNECTION_TIMEOUT = "connection-timeout";
    public static final String CONNETTORE_READ_CONNECTION_TIMEOUT = "read-connection-timeout";
	
    
    /** HTTP PROPERTIES */
	
    public static final String CONNETTORE_HTTP_PROXY_TYPE = "proxyType";
    public static final String CONNETTORE_HTTP_PROXY_URL = "proxyUrl";
    public static final String CONNETTORE_HTTP_PROXY_PORT = "proxyPort";
    public static final String CONNETTORE_HTTP_PROXY_USERNAME = "proxyUsername";
    public static final String CONNETTORE_HTTP_PROXY_PASSWORD = "proxyPassword";

    public static final String CONNETTORE_HTTP_PROXY_TYPE_VALUE_HTTP = TipiConnettore.HTTP.getNome();
    public static final String CONNETTORE_HTTP_PROXY_TYPE_VALUE_HTTPS = TipiConnettore.HTTPS.getNome();
    
    public static final String CONNETTORE_HTTP_REDIRECT_FOLLOW = "followRedirects";
    public static final String _CONNETTORE_HTTP_REDIRECT_NUMBER = "numberRedirect";
    public static final String _CONNETTORE_HTTP_REDIRECT_ROUTE = "routeRedirect";

    
	/** JMS PROPERTIES */
	
    public static final String CONNETTORE_JMS_TIPO = "tipo";
    public static final String CONNETTORE_JMS_CONTEXT_PREFIX="context-";
    public static final String CONNETTORE_JMS_POOL_PREFIX="pool-";
    public static final String CONNETTORE_JMS_LOOKUP_DESTINATION_PREFIX="lookupDestination-";
    public static final String CONNETTORE_JMS_CONNECTION_FACTORY="connection-factory";
    public static final String CONNETTORE_JMS_SEND_AS="send-as";
    public static final String CONNETTORE_JMS_LOCATIONS_CACHE = "locations-cache";
    public static final String CONNETTORE_JMS_ACKNOWLEDGE_MODE = "acknowledgeMode";
    
    public static final String CONNETTORE_JMS_LOCATION_REPLACE_TOKEN_NOME_SERVIZIO = "#Servizio"; 
    public static final String CONNETTORE_JMS_LOCATION_REPLACE_TOKEN_TIPO_SERVIZIO = "#TipoServizio";
    public static final String CONNETTORE_JMS_LOCATION_REPLACE_TOKEN_AZIONE = "#Azione";
    
    public static final String CONNETTORE_JMS_TIPO_QUEUE = "queue";
    public static final String CONNETTORE_JMS_TIPO_TOPIC = "topic";
	
    public static final String CONNETTORE_JMS_SEND_AS_TEXT_MESSAGE = "TextMessage";
    public static final String CONNETTORE_JMS_SEND_AS_BYTES_MESSAGE = "BytesMessage";
	
    public static final String CONNETTORE_JMS_LOCATIONS_CACHE_ABILITATA = "abilitata";
    public static final String CONNETTORE_JMS_LOCATIONS_CACHE_DISABILITATA = "disabilitata";
    
    
    /** HTTPS PROPERTIES */
    
    public static final String CONNETTORE_HTTPS_TRUST_STORE_LOCATION = "trustStoreLocation";
    public static final String CONNETTORE_HTTPS_TRUST_STORE_PASSWORD = "trustStorePassword";
    public static final String CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM = "trustManagementAlgorithm";
    public static final String CONNETTORE_HTTPS_TRUST_STORE_TYPE = "trustStoreType";
    public static final String CONNETTORE_HTTPS_KEY_STORE_LOCATION = "keyStoreLocation";
    public static final String CONNETTORE_HTTPS_KEY_STORE_PASSWORD = "keyStorePassword";
    public static final String CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM = "keyManagementAlgorithm";
    public static final String CONNETTORE_HTTPS_KEY_STORE_TYPE = "keyStoreType";
    public static final String CONNETTORE_HTTPS_KEY_PASSWORD = "keyPassword";
    public static final String CONNETTORE_HTTPS_HOSTNAME_VERIFIER = "hostnameVerifier";
    public static final String CONNETTORE_HTTPS_CLASSNAME_HOSTNAME_VERIFIER = "classNameHostnameVerifier";
    public static final String CONNETTORE_HTTPS_SSL_TYPE = "sslType";
    
    public static final String CONNETTORE_HTTPS_SSL_TYPE_VALUE_SSL="SSL";
    public static final String CONNETTORE_HTTPS_SSL_TYPE_VALUE_SSLv3="SSLv3";
    public static final String CONNETTORE_HTTPS_SSL_TYPE_VALUE_TLS="TLS";
    public static final String CONNETTORE_HTTPS_SSL_TYPE_VALUE_TLSv1="TLSv1";
	
    
    /** DIRECT VM PROPERTIES */
    
    public static final String CONNETTORE_DIRECT_VM_PROTOCOL = "protocol";
    public static final String CONNETTORE_DIRECT_VM_CONTEXT = "context";
    public static final String CONNETTORE_DIRECT_VM_PD = "pd";
    public static final String CONNETTORE_DIRECT_VM_PA = "pa";
    
    
    
    /** STRESSTEST PROPERTIES */
    
    public static final String CONNETTORE_STRESS_TEST_SLEEP_MAX = "sleepMax";
    public static final String CONNETTORE_STRESS_TEST_SLEEP_MIN = "sleepMin";
}