package org.openspcoop2.pdd.core.token;

import java.util.Map;
import java.util.Properties;

import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.core.mvc.properties.provider.ProviderValidationException;
import org.openspcoop2.core.mvc.properties.utils.MultiPropertiesUtilities;

public class TokenUtilities {

	public static Properties getDefaultProperties(Map<String, Properties> mapProperties) throws ProviderException, ProviderValidationException {
		return MultiPropertiesUtilities.getDefaultProperties(mapProperties);
	}
	
	public static boolean isValidazioneEnabled(Map<String, Properties> mapProperties) throws ProviderException, ProviderValidationException {
		return isValidazioneEnabled(getDefaultProperties(mapProperties));
	}
	public static boolean isValidazioneEnabled(Properties pDefault) throws ProviderException, ProviderValidationException {
		return isEnabled(pDefault, Costanti.POLICY_VALIDAZIONE_STATO);
	}
	
	public static boolean isIntrospectionEnabled(Map<String, Properties> mapProperties) throws ProviderException, ProviderValidationException {
		return isIntrospectionEnabled(getDefaultProperties(mapProperties));
	}
	public static boolean isIntrospectionEnabled(Properties pDefault) throws ProviderException, ProviderValidationException {
		return isEnabled(pDefault, Costanti.POLICY_INTROSPECTION_STATO);
	}
	
	public static boolean isUserInfoEnabled(Map<String, Properties> mapProperties) throws ProviderException, ProviderValidationException {
		return isUserInfoEnabled(getDefaultProperties(mapProperties));
	}
	public static boolean isUserInfoEnabled(Properties pDefault) throws ProviderException, ProviderValidationException {
		return isEnabled(pDefault, Costanti.POLICY_USER_INFO_STATO);
	}
	
	public static boolean isTokenForwardEnabled(Map<String, Properties> mapProperties) throws ProviderException, ProviderValidationException {
		return isTokenForwardEnabled(getDefaultProperties(mapProperties));
	}
	public static boolean isTokenForwardEnabled(Properties pDefault) throws ProviderException, ProviderValidationException {
		return isEnabled(pDefault, Costanti.POLICY_TOKEN_FORWARD_STATO);
	}
	
	public static boolean isEnabled(Properties p, String propertyName) throws ProviderException, ProviderValidationException {
		return MultiPropertiesUtilities.isEnabled(p, propertyName);
	}
	
}