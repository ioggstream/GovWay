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
package org.openspcoop2.pdd.services.connector.messages;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.services.connector.ConnectorException;

/**
 * ConnectorOutMessage
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface ConnectorOutMessage {

	public void sendResponse(OpenSPCoop2Message message, boolean consume) throws ConnectorException; 
	
	public void sendResponse(byte[] message) throws ConnectorException; 
	
	public void setHeader(String key,String value) throws ConnectorException; 
	
	public void setContentLength(int length) throws ConnectorException; 
	
	public void setContentType(String type) throws ConnectorException; 
	
	public void setStatus(int status) throws ConnectorException;
	public int getResponseStatus() throws ConnectorException; // ritorna lo stato precedentemente impostato (puo' essere "nattato")
	
	public void flush(boolean throwException) throws ConnectorException; 
	
	public void close(boolean throwException) throws ConnectorException; 
	
}
