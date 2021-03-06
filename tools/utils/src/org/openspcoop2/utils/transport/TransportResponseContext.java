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

package org.openspcoop2.utils.transport;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
 * RequestContext
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class TransportResponseContext implements java.io.Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		
	/* ---- Coppie nome/valori di invocazione inserite nell'header del trasporto --- */
	protected java.util.Properties parametersTrasporto;
	
	protected String codiceTrasporto = null; 
	protected long contentLength = -1;
	protected String errore = null;
	protected Exception exception = null;
	
	
	public TransportResponseContext() throws UtilsException{
		
	}
	public TransportResponseContext(java.util.Properties parametersTrasporto,String codiceTrasporto,long contentLength,String errore,Exception exception) throws UtilsException{
		this.parametersTrasporto = parametersTrasporto;
		this.codiceTrasporto = codiceTrasporto;
		this.contentLength = contentLength;
		this.errore = errore;
		this.exception = exception;
	}
	
	
	public String getCodiceTrasporto() {
		return this.codiceTrasporto;
	}
	public long getContentLength() {
		return this.contentLength;
	}
	public String getErrore() {
		return this.errore;
	}
	public java.util.Properties getParametersTrasporto() {
		return this.parametersTrasporto;
	}
	public String getParameterTrasporto(String name){
		if(this.parametersTrasporto==null){
			return null;
		}
		String value = this.parametersTrasporto.getProperty(name);
		if(value==null){
			value = this.parametersTrasporto.getProperty(name.toLowerCase());
		}
		if(value==null){
			value = this.parametersTrasporto.getProperty(name.toUpperCase());
		}
		return value;
	}
	public Object removeParameterTrasporto(String name){
		if(this.parametersTrasporto==null){
			return null;
		}
		Object value = this.parametersTrasporto.remove(name);
		if(value==null){
			value = this.parametersTrasporto.remove(name.toLowerCase());
		}
		if(value==null){
			value = this.parametersTrasporto.remove(name.toUpperCase());
		}
		return value;
	}
	public String getContentType(){
		if(this.parametersTrasporto!=null){
			return this.getParameterTrasporto(HttpConstants.CONTENT_TYPE);
		}
		return null;
	}

	public void setCodiceTrasporto(String codiceTrasporto) {
		this.codiceTrasporto = codiceTrasporto;
	}
	public void setContentLength(long contentLength) {
		this.contentLength = contentLength;
	}
	public void setErrore(String errore) {
		this.errore = errore;
	}
	public void setParametersTrasporto(java.util.Properties parametersTrasporto) {
		this.parametersTrasporto = parametersTrasporto;
	}

}
