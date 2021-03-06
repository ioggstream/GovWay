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

package org.openspcoop2.core.mvc.properties.utils;

import java.util.Map;
import java.util.Properties;

import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.core.mvc.properties.provider.ProviderValidationException;

/**     
 * MultiPropertiesUtilities
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MultiPropertiesUtilities {

	public static Properties getDefaultProperties(Map<String, Properties> mapProperties) throws ProviderException, ProviderValidationException {
		return mapProperties.get(org.openspcoop2.core.mvc.properties.utils.Costanti.NOME_MAPPA_PROPERTIES_DEFAULT);
	}
	
	public static Properties removeDefaultProperties(Map<String, Properties> mapProperties) throws ProviderException, ProviderValidationException {
		return mapProperties.remove(org.openspcoop2.core.mvc.properties.utils.Costanti.NOME_MAPPA_PROPERTIES_DEFAULT);
	}
	
	public static boolean isEnabled(Properties p, String propertyName) throws ProviderException, ProviderValidationException {
		boolean validazione = false;
		if(p.containsKey(propertyName)) {
			validazione = Boolean.valueOf(p.getProperty(propertyName));
		}
		return validazione;
	}
	
}
