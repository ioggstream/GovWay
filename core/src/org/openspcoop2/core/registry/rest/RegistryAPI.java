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

package org.openspcoop2.core.registry.rest;

import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;

import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.ResourceParameter;
import org.openspcoop2.core.registry.ResourceRepresentation;
import org.openspcoop2.core.registry.ResourceResponse;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.utils.rest.api.Api;
import org.openspcoop2.utils.rest.api.ApiBodyParameter;
import org.openspcoop2.utils.rest.api.ApiCookieParameter;
import org.openspcoop2.utils.rest.api.ApiHeaderParameter;
import org.openspcoop2.utils.rest.api.ApiOperation;
import org.openspcoop2.utils.rest.api.ApiRequest;
import org.openspcoop2.utils.rest.api.ApiRequestDynamicPathParameter;
import org.openspcoop2.utils.rest.api.ApiRequestFormParameter;
import org.openspcoop2.utils.rest.api.ApiRequestQueryParameter;
import org.openspcoop2.utils.rest.api.ApiResponse;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;

/**
 * RegistryAPI
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RegistryAPI extends Api {

	private AccordoServizioParteComune aspc = null;
	
	public AccordoServizioParteComune getAspc() {
		return this.aspc;
	}

	public RegistryAPI(AccordoServizioParteComune aspc,String baseURL) throws DriverRegistroServiziException {
		try {
		
			this.aspc = aspc;
			
			this.setName(aspc.getNome());
			this.setDescription(aspc.getDescrizione());
			this.setBaseURL(new URL(baseURL));
			
			if(aspc.sizeResourceList()>0) {
				for (Resource resource : aspc.getResourceList()) {
					HttpRequestMethod httpMethod = null;
					if(resource.getMethod()!=null) {
						httpMethod = HttpRequestMethod.valueOf(resource.getMethod().getValue());
					}
					ApiOperation apiOp = new ApiOperation(httpMethod, resource.getPath());
					apiOp.setDescription(resource.getDescrizione());
					
					// Richiesta
					if(resource.getRequest()!=null) {
						apiOp.setRequest(new ApiRequest());						
						if(resource.getRequest().sizeParameterList()>0) {
							initParameterList(resource.getRequest().getParameterList(), apiOp.getRequest(), null);
						}
						if(resource.getRequest().sizeRepresentationList()>0) {
							initRepresentationList(resource.getRequest().getRepresentationList(), apiOp.getRequest(), null);
						}
					}
					
					// Risposta
					if(resource.sizeResponseList()>0) {
						for (ResourceResponse response : resource.getResponseList()) {
							ApiResponse apiResponse = new ApiResponse();
							if(ApiResponse.isDefaultHttpReturnCode(response.getStatus())) {
								apiResponse.setDefaultHttpReturnCode();
							}
							else {
								apiResponse.setHttpReturnCode(response.getStatus());
							}
							apiResponse.setDescription(response.getDescrizione());
							if(response.sizeParameterList()>0) {
								initParameterList(response.getParameterList(), null, apiResponse);
							}
							if(response.sizeRepresentationList()>0) {
								initRepresentationList(response.getRepresentationList(), null, apiResponse);
							}
						}
					}
					
					this.addOperation(apiOp);
				}
			}
			
		}catch(Exception e) {
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	
	private static void initParameterList(List<ResourceParameter> rpList, ApiRequest apiRequest, ApiResponse apiResponse) {
		for (ResourceParameter rp : rpList) {
			switch (rp.getParameterType()) {
			case COOKIE:
				ApiCookieParameter cookie = new ApiCookieParameter(rp.getNome(), rp.getTipo());
				cookie.setDescription(rp.getDescrizione());
				cookie.setRequired(rp.isRequired());
				if(apiRequest!=null) {
					apiRequest.addCookieParameter(cookie);
				}
				else {
					apiResponse.addCookieParameter(cookie);
				}
				break;
			case DYNAMIC_PATH:
				ApiRequestDynamicPathParameter dynamicPath = new ApiRequestDynamicPathParameter(rp.getNome(), rp.getTipo());
				dynamicPath.setDescription(rp.getDescrizione());
				dynamicPath.setRequired(rp.isRequired());
				apiRequest.addDynamicPathParameter(dynamicPath);
				break;
			case FORM:
				ApiRequestFormParameter form = new ApiRequestFormParameter(rp.getNome(), rp.getTipo());
				form.setDescription(rp.getDescrizione());
				form.setRequired(rp.isRequired());
				apiRequest.addFormParameter(form);
				break;
			case HEADER:
				ApiHeaderParameter header = new ApiHeaderParameter(rp.getNome(), rp.getTipo());
				header.setDescription(rp.getDescrizione());
				header.setRequired(rp.isRequired());
				if(apiRequest!=null) {
					apiRequest.addHeaderParameter(header);
				}
				else {
					apiResponse.addHeaderParameter(header);
				}
				break;
			case QUERY:
				ApiRequestQueryParameter query = new ApiRequestQueryParameter(rp.getNome(), rp.getTipo());
				query.setDescription(rp.getDescrizione());
				query.setRequired(rp.isRequired());
				apiRequest.addQueryParameter(query);
				break;
			default:
				break;
			}	
		}
	}
	
	private static void initRepresentationList(List<ResourceRepresentation> rrList, ApiRequest apiRequest, ApiResponse apiResponse) {
		for (ResourceRepresentation rr : rrList) {
			ApiBodyParameter parameter = new ApiBodyParameter(rr.getNome());
			parameter.setDescription(rr.getDescrizione());
			parameter.setMediaType(rr.getMediaType());
			if(rr.getRepresentationType()!=null) {
				switch (rr.getRepresentationType()) {
				case JSON:
					if(rr.getJson()!=null) {
						parameter.setElement(rr.getJson().getTipo());
					}
					break;
				case XML:
					if(rr.getXml()!=null) {
						parameter.setElement(new QName(rr.getXml().getNamespace(),rr.getXml().getNome()));
					}
					break;
				}
			}
			if(apiRequest!=null) {
				apiRequest.addBodyParameter(parameter);
			}
			else {
				apiResponse.addBodyParameter(parameter);
			}
		}
	}
}
