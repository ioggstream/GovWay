/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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

package org.openspcoop2.utils.rest.api;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.utils.beans.BaseBean;
import org.openspcoop2.utils.rest.ProcessingException;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;

/**
 * ApiOperation
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 12564 $, $Date: 2017-01-11 14:31:31 +0100 (Wed, 11 Jan 2017) $
 */
public class ApiOperation extends BaseBean {

	private HttpRequestMethod httpMethod;
	private String path;
	private String description;
	
	private ApiRequest request;
	
	private List<ApiResponse> responses = new ArrayList<ApiResponse>();
	
	public ApiOperation(HttpRequestMethod httpMethod,String path){
		this.httpMethod = httpMethod;
		this.path = path;
		this.normalizePath();
	}
	
	private void normalizePath(){
		if(this.path.startsWith("/")==false){
			this.path = "/"+this.path;
		}
		while(this.path.contains("${")){
			// in wadl viene usato ${xx}
			// in swagger {xx}
			this.path = this.path.replace("${", "{");
		}
	}
	
	public String getDescription() {
		return this.description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public HttpRequestMethod getHttpMethod() {
		return this.httpMethod;
	}
	public void setHttpMethod(HttpRequestMethod httpMethod) {
		this.httpMethod = httpMethod;
	}
	public String getPath() {
		return this.path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public int sizePath(){
		return this._split(this.path).size();
	}
	public String getPath(int position) throws ProcessingException {
		List<String> tmp = _split(this.path);
		if(position>=tmp.size()){
			throw new ProcessingException("Path ["+this.path+"] (split:"+tmp.size()+") non contiene posizione indicata ("+position+")");
		}
		return tmp.get(position);
	}
	public String getDynamicPathId(int position) throws ProcessingException {
		List<String> tmp = _split(this.path);
		if(position>=tmp.size()){
			throw new ProcessingException("Path ["+this.path+"] (split:"+tmp.size()+") non contiene posizione indicata ("+position+")");
		}
		String p = tmp.get(position);
		return p.substring(p.length()-1).substring(1);
	}
	public boolean isDynamicPath(){
		return this.path.contains("{");
	}	
	public boolean isDynamicPath(int position) throws ProcessingException {
		List<String> tmp = _split(this.path);
		if(position>=tmp.size()){
			throw new ProcessingException("Path ["+this.path+"] (split:"+tmp.size()+") non contiene posizione indicata ("+position+")");
		}
		return tmp.get(position).startsWith("{") && tmp.get(position).endsWith("}");
	}
	
	private List<String> _split(String url){
		List<String> l = new ArrayList<>();
		for(String s : url.split("/")) {
			if(s!=null && !s.equals("")) {
				l.add(s);
			}
		}
		return l;
	}
		
	
	public ApiRequest getRequest() {
		return this.request;
	}

	public void setRequest(ApiRequest request) {
		this.request = request;
	}
	
	
	public void addResponse(ApiResponse response) {
		this.responses.add(response);
	}

	public ApiResponse getResponse(int index) {
		return this.responses.get( index );
	}

	public ApiResponse removeResponse(int index) {
		return this.responses.remove( index );
	}

	public List<ApiResponse> getResponses() {
		return this.responses;
	}

	public void setResponses(List<ApiResponse> responses) {
		this.responses=responses;
	}

	public int sizeResponses() {
		return this.responses.size();
	}
}
