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

package org.openspcoop2.message.soap;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.namespace.QName;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.io.input.CountingInputStream;
import org.openspcoop2.message.constants.Costanti;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;

import com.sun.xml.messaging.saaj.packaging.mime.internet.ContentType;

/**
 * Implementazione dell'OpenSPCoop2Message
 *
 * @author Lorenzo Nardi <nardi@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class OpenSPCoop2Message_saaj_12_impl extends AbstractOpenSPCoop2Message_saaj_impl
{
	
	/* Costruttori */
	
	public OpenSPCoop2Message_saaj_12_impl() {	
		super(new Message1_2_FIX_Impl());
	}
	
	public OpenSPCoop2Message_saaj_12_impl(MimeHeaders mhs, InputStream is) throws SOAPException, IOException{
		super(new Message1_2_FIX_Impl(mhs, new CountingInputStream(is)));
	}
	
	public OpenSPCoop2Message_saaj_12_impl(SOAPMessage msg) {	
		//TODO questo costruttore non funziona con messaggi con attachment. 
		//C'e' un bug nell'implementazione della sun che non copia gli attachment
		//In particolare il parametro super.mimePart (protetto non accessibile).
		// Per questo motivo essite la classe 1_2 FIX che utilizza direttamente il messaggio fornito 
		super(new Message1_2_FIX_Impl(msg));
	}
	

	/* Initialize ed internal Message Impl */
	
	public void initialize(long overhead){
		getMessage1_2_FIX_Impl().setLazyAttachments(false);
		this.incomingsize = getMessage1_2_FIX_Impl().getCountingInputStream().getByteCount() - overhead;
	}

	private Message1_2_FIX_Impl getMessage1_2_FIX_Impl(){
		return ((Message1_2_FIX_Impl)this.getSoapMessage());
	}
	
	
	
	/* ContentType */
	
	@Override
	protected String _super_getContentType() {
		return getMessage1_2_FIX_Impl().getContentType();
	}
	
	@Override
	public void setContentType(String type) throws MessageException{
		getMessage1_2_FIX_Impl().setContentType(type);
	}
	
	@Override
	public String getContentType() throws MessageException{
		
		try {
			ContentType cType = new ContentType(getMessage1_2_FIX_Impl().getContentType());
			if(cType.getBaseType().equals(HttpConstants.CONTENT_TYPE_MULTIPART)) {
				if(getMessage1_2_FIX_Impl().getMimeMultipart() != null)
					cType.setParameter(HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_BOUNDARY, 
							getMessage1_2_FIX_Impl().getMimeMultipart().getContentType().getParameter(HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_BOUNDARY));
			}
			if(this.soapAction!=null){
				String pSoapAction = cType.getParameter(Costanti.SOAP12_OPTIONAL_CONTENT_TYPE_PARAMETER_SOAP_ACTION);
				if(pSoapAction!=null){
					cType.getParameterList().remove(Costanti.SOAP12_OPTIONAL_CONTENT_TYPE_PARAMETER_SOAP_ACTION);
				}
				if(this.contentTypeParamaters!=null && this.contentTypeParamaters.containsKey(Costanti.SOAP12_OPTIONAL_CONTENT_TYPE_PARAMETER_SOAP_ACTION)){
					this.contentTypeParamaters.remove(Costanti.SOAP12_OPTIONAL_CONTENT_TYPE_PARAMETER_SOAP_ACTION);
				}
				this.contentTypeParamaters.put(Costanti.SOAP12_OPTIONAL_CONTENT_TYPE_PARAMETER_SOAP_ACTION, this.soapAction);
			}
			return ContentTypeUtilities.buildContentType(cType.toString(),this.contentTypeParamaters);	
		} catch (Exception e) {
			e.printStackTrace();
			try{
				return ContentTypeUtilities.buildContentType(getMessage1_2_FIX_Impl().getContentType(),this.contentTypeParamaters);
			}catch(Exception eInternal){
				throw new RuntimeException(eInternal.getMessage(),eInternal);
			}
		}
	}
	

	
	/* SOAP Utilities */
	
	@Override
	public void setFaultCode(SOAPFault fault, SOAPFaultCode code, QName eccezioneName) throws MessageException {
		try{
			QName faultCode = new QName(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE, code.toString());
			fault.setFaultCode(faultCode);
			fault.appendFaultSubcode(eccezioneName);
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
}
