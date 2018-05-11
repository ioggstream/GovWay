/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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
package org.openspcoop2.generic_project.serializer;


import java.io.InputStream;

import org.openspcoop2.utils.serialization.XMLDeserializer;


/**
 * JsonXmlDeserializer
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JsonXmlDeserializer extends AbstractDeserializer {

	@SuppressWarnings("unchecked")
	@Override
	protected <T> T _xmlToObj(InputStream is, Class<T> c) throws Exception {
		XMLDeserializer jsonDeserializer = new XMLDeserializer();
		try{
			return (T) jsonDeserializer.readObject(is, c);
		}finally{
		}
	}
	
}

