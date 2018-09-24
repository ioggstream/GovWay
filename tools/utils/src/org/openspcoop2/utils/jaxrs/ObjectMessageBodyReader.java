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

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

/**	
 * ObjectMessageBodyReader
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@Provider
//@javax.ws.rs.Consumes({"application/zip","application/xml"})
public class ObjectMessageBodyReader implements javax.ws.rs.ext.MessageBodyReader<Object>
	//, javax.ws.rs.ext.MessageBodyWriter<Object>
{

	@Override
	public boolean isReadable(Class<?> paramClass, Type paramType,
			Annotation[] paramArrayOfAnnotation, MediaType paramMediaType) {
		// paramMediaType is application/zip, application/xml ...
		// realizzare un if se si desidera gestire solo un tipo in particolare
		return true;
	}

	@Override
	public Object readFrom(Class<Object> paramClass, Type paramType,
			Annotation[] paramArrayOfAnnotation, MediaType paramMediaType,
			MultivaluedMap<String, String> paramMultivaluedMap,
			InputStream paramInputStream) throws IOException, WebApplicationException {
		return paramInputStream;
	}

}

