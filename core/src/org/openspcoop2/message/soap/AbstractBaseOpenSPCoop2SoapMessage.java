/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import java.util.List;

import javax.xml.soap.SOAPMessage;

import org.openspcoop2.message.AbstractBaseOpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.Costanti;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.exception.MessageNotSupportedException;
import org.openspcoop2.message.soap.mtom.MTOMUtilities;
import org.openspcoop2.message.soap.mtom.MtomXomPackageInfo;
import org.openspcoop2.message.soap.mtom.MtomXomReference;

/**
 * AbstractBaseOpenSPCoop2SoapMessage_impl
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author: apoli $
 * @version $Rev: 12237 $, $Date: 2016-10-04 11:41:45 +0200 (Tue, 04 Oct 2016) $
 */
public abstract class AbstractBaseOpenSPCoop2SoapMessage extends AbstractBaseOpenSPCoop2Message implements OpenSPCoop2SoapMessage {

	/* SOAPAction */
	public String soapAction;
	
	
	/* Elementi SOAP */
	
	protected abstract SOAPMessage _getSOAPMessage() throws MessageException;
	
	@Override
	public SOAPMessage getSOAPMessage() throws MessageException,MessageNotSupportedException{
		try{
			SOAPMessage soapMessage = this._getSOAPMessage();
			if(soapMessage!=null){
				soapMessage.setProperty(Costanti.SOAP_MESSAGE_PROPERTY_MESSAGE_TYPE, this.getMessageType());
			}
			return soapMessage;
		}
		catch(MessageException me){
			throw me;
		}
		catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	

	/* SOAPAction */
	
	@Override
	public String getSoapAction(){
		return this.soapAction;
	}
	
	@Override
	public void setSoapAction(String soapAction){
		this.soapAction = soapAction;
	}
	
	
	
	/* MTOM */
	
	@Override
	public List<MtomXomReference> mtomUnpackaging() throws MessageException,MessageNotSupportedException{
		return MTOMUtilities.unpackaging(this, false, true);
	}
	@Override
	public List<MtomXomReference> mtomPackaging( List<MtomXomPackageInfo> packageInfos) throws MessageException,MessageNotSupportedException{
		return MTOMUtilities.packaging(this, packageInfos, true);
	}
	@Override
	public List<MtomXomReference> mtomVerify( List<MtomXomPackageInfo> packageInfos) throws MessageException,MessageNotSupportedException{
		return MTOMUtilities.verify(this, packageInfos, true);
	}
	@Override
	public List<MtomXomReference> mtomFastUnpackagingForXSDConformance() throws MessageException,MessageNotSupportedException{
		return MTOMUtilities.unpackaging(this, true, true);
	}
	@Override
	public void mtomRestoreAfterXSDConformance(List<MtomXomReference> references) throws MessageException,MessageNotSupportedException{
		MTOMUtilities.restoreAfterFastUnpackaging(this, references, true);
	}
}
