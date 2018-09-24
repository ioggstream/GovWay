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

package org.openspcoop2.utils.jaxrs;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

/**	
 * JacksonXmlProvider
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JacksonXmlProvider extends com.fasterxml.jackson.jaxrs.xml.JacksonXMLProvider {

	public static XmlMapper getObjectMapper() {
		XmlMapper mapper = new XmlMapper();
		mapper.registerModule(new JodaModule());
		mapper.configure(com.fasterxml.jackson.databind.SerializationFeature.
			    WRITE_DATES_AS_TIMESTAMPS , false);
		return mapper;
	}
	
	public JacksonXmlProvider() {
		super(getObjectMapper());
	}
	
}
