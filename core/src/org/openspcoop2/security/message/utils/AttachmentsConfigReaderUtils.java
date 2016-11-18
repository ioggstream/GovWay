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

package org.openspcoop2.security.message.utils;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.openspcoop2.message.soap.reference.AttachmentReference;
import org.openspcoop2.message.soap.reference.Reference;
import org.openspcoop2.security.message.MessageSecurityContext;
import org.openspcoop2.security.message.constants.SecurityConstants;

/**
 * AttachmentsConfigReaderUtils
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 12237 $, $Date: 2016-10-04 11:41:45 +0200 (Tue, 04 Oct 2016) $
 */
public class AttachmentsConfigReaderUtils {

	public static List<String> getListCIDAttachmentsForSecurity(MessageSecurityContext wssContext) throws Exception{
		List<String> cidSecurity = new ArrayList<String>();
		if(wssContext.getReferences()!=null && wssContext.getReferences().size()>0){
			for (Reference reference : wssContext.getReferences()) {
				if(reference instanceof AttachmentReference){
					AttachmentReference ar = (AttachmentReference)reference;
					//System.out.println("ADD TYPE-REF["+ar.getType()+"] ["+ar.getReference()+"]");
					cidSecurity.add(ar.getReference());
				}
			}
		}
		return cidSecurity;
	}
	
	public static AttachmentProcessingPart getSecurityOnAttachments(MessageSecurityContext wssContext) throws Exception{

		AttachmentProcessingPart ap = null;

		Hashtable<?,?> wssProperties = null;
		if(wssContext.isFunctionAsClient())
			wssProperties = wssContext.getOutgoingProperties();
		else
			wssProperties = wssContext.getIncomingProperties();
		if (wssProperties != null && wssProperties.size() > 0) {
			if(wssProperties.containsKey(SecurityConstants.ENCRYPTION_PARTS)){
				String value = (String) wssProperties.get(SecurityConstants.ENCRYPTION_PARTS);
				List<ProcessingPart<?,?>> listProcessingParts = ProcessingPartUtils.getEncryptionInstance().getProcessingParts(value);
				AttachmentProcessingPart apFound = findAttachmentProcessingPart(listProcessingParts, ap, SecurityConstants.ENCRYPTION_PARTS); 
				if(ap==null){
					ap = apFound;
				}
			}
			else if(wssProperties.containsKey(SecurityConstants.SIGNATURE_PARTS)){
				String value = (String) wssProperties.get(SecurityConstants.SIGNATURE_PARTS);
				List<ProcessingPart<?,?>> listProcessingParts = ProcessingPartUtils.getEncryptionInstance().getProcessingParts(value);
				AttachmentProcessingPart apFound = findAttachmentProcessingPart(listProcessingParts, ap, SecurityConstants.SIGNATURE_PARTS); 
				if(ap==null){
					ap = apFound;
				}
			}
		}

		return ap;
	}

	public static  AttachmentProcessingPart findAttachmentProcessingPart(List<ProcessingPart<?,?>> listProcessingParts, AttachmentProcessingPart ap, String parts) throws Exception{
		AttachmentProcessingPart apFound = null;
		boolean found = false;
		for (ProcessingPart<?, ?> processingPart : listProcessingParts) {
			if(processingPart instanceof AttachmentProcessingPart){
				if(found){
					throw new Exception("Only one configuration for attachments is allowed in "+parts);
				}
				apFound = (AttachmentProcessingPart) processingPart;
				if(ap!=null){
					// trovato anche in signature
					if(ap.isAllAttachments()){
						if(!apFound.isAllAttachments()){
							throw new Exception("The configuration of signature and encryption for the attachments must be the same (found difference in "+SecurityConstants.ENCRYPTION_PARTS
									+" and "+SecurityConstants.SIGNATURE_PARTS+")");
						}
					}
					else {
						int apIntValue = ap.getPart().intValue();
						if(apFound.isAllAttachments() || apIntValue!=apFound.getPart().intValue() ){
							throw new Exception("The configuration of signature and encryption for the attachments must be the same (found difference in "+SecurityConstants.ENCRYPTION_PARTS
									+" and "+SecurityConstants.SIGNATURE_PARTS+")");
						}
					}
				}

				found = true;
			}
		}
		return apFound;
	}

}
