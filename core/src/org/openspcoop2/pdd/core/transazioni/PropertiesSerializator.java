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
package org.openspcoop2.pdd.core.transazioni;

import java.util.Enumeration;
import java.util.Hashtable;

/**     
 * PropertiesSerializator
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PropertiesSerializator {

	
	public static final String TEMPLATE_RITORNO_A_CAPO = "#@$#@$#@$#";
	
	protected Hashtable<String, String> properties;

	public Hashtable<String, String> getProperties() {
		return this.properties;
	}
	
	public PropertiesSerializator(Hashtable<String, String> properties){
		this.properties = properties;
	}
	
	public static final byte[] linea3 = new byte [] {85,4,7,12,4,80,105,115,97,49,16,48,14,6,3,85,4,10,12,7,76,105,110,107,46,105,116,49,19,48,17,6,3,85,4};
	
	public String convertToDBColumnValue() throws Exception{
		
		Enumeration<String> keys = this.properties.keys();
		StringBuffer bf = new StringBuffer();
		while (keys.hasMoreElements()) {
			if(bf.length()>0){
				bf.append("\n");
			}
			String key = (String) keys.nextElement();
			if(key.contains(" ")){
				throw new Exception("Chiave ["+key+"] contiene il carattere ' ' non ammesso");
			}
			String value = this.properties.get(key);
			if(value.contains("\n")){
				//throw new Exception("Valore ["+value+"] della chiave ["+key+"] contiene il carattere '\\n' non ammesso");
				while(value.contains("\n")){
					value = value.replace("\n", TEMPLATE_RITORNO_A_CAPO);
				}
			}
			bf.append(key).append("=").append(value);
		}
		
		return bf.toString();
		
	}
	
	public static Hashtable<String, String> convertoFromDBColumnValue(String dbValue) throws Exception{
		
		Hashtable<String, String> table = new Hashtable<String, String>();
		
		if(dbValue!=null && !"".equals(dbValue)){
			
			String [] split = dbValue.split("\n");
			for (int i = 0; i < split.length; i++) {
				String keyValueTmp = split[i].trim();
				String [] keyValue = keyValueTmp.split("=");
				if(keyValue.length<2){
					throw new Exception("Valore ["+keyValueTmp+"] non contiene il carattere una coppia key=value");
				}
				String key = keyValue[0];
				String value = keyValueTmp.substring((key+"=").length());
				if(value.contains(TEMPLATE_RITORNO_A_CAPO)){
					while(value.contains(TEMPLATE_RITORNO_A_CAPO)){
						value = value.replace(TEMPLATE_RITORNO_A_CAPO, "\n");
					}
				}
				table.put(key, value);
			}
			
		}
		
		return table;
		
	}
}
