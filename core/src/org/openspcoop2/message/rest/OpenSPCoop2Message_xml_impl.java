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

package org.openspcoop2.message.rest;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.openspcoop2.message.OpenSPCoop2RestXmlMessage;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.exception.MessageNotSupportedException;
import org.openspcoop2.message.xml.XMLUtils;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * Implementazione dell'OpenSPCoop2Message utilizzabile per messaggi xml
 *
 * @author Andrea Poli <poli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OpenSPCoop2Message_xml_impl extends AbstractBaseOpenSPCoop2RestMessage<Element> implements OpenSPCoop2RestXmlMessage {

	public OpenSPCoop2Message_xml_impl() {
		super();
	}
	public OpenSPCoop2Message_xml_impl(InputStream is,String contentType) throws MessageException {
		super(is, contentType);
	}
	
	@Override
	protected Element buildContent() throws MessageException{
		InputStreamReader isr = null;
		InputSource isSax = null;
		try{
			isr = new InputStreamReader(this.countingInputStream,this.contentTypeCharsetName);
			isSax = new InputSource(isr);
			return XMLUtils.getInstance().newElement(isSax);
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}finally{
			try{
				if(isr!=null){
					isr.close();
				}
			}catch(Exception eClose){}
			try{
				this.countingInputStream.close();
			}catch(Exception eClose){}
		}
	}

	@Override
	protected String buildContentAsString() throws MessageException{
		try{
			return this.getAsString(this.content, false);
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	@Override
	protected void serializeContent(OutputStream os, boolean consume) throws MessageException {
		try{
			XMLUtils.getInstance().writeTo(this.content, os, true);
			os.flush();
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}

	@Override
	public boolean isProblemDetailsForHttpApis_RFC7808() throws MessageException,MessageNotSupportedException {
		try{
			if(this.contentType==null) {
				return false;
			}
			String baseType = ContentTypeUtilities.readBaseTypeFromContentType(this.contentType);
			return HttpConstants.CONTENT_TYPE_XML_PROBLEM_DETAILS_RFC_7808.equalsIgnoreCase(baseType);
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
}
