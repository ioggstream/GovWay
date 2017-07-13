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

package org.openspcoop2.utils.rest;

import java.util.List;

import org.openspcoop2.utils.rest.api.Api;
import org.openspcoop2.utils.rest.api.ApiOperation;
import org.openspcoop2.utils.rest.api.ApiRequestBodyParameter;
import org.openspcoop2.utils.rest.api.ApiRequestDynamicPathParameter;
import org.openspcoop2.utils.rest.api.ApiRequestHeaderParameter;
import org.openspcoop2.utils.rest.api.ApiRequestQueryParameter;
import org.openspcoop2.utils.rest.api.ApiResponse;
import org.openspcoop2.utils.rest.entity.HttpBaseEntity;
import org.openspcoop2.utils.rest.entity.HttpBaseRequestEntity;
import org.openspcoop2.utils.rest.entity.HttpBaseResponseEntity;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;

/**
 * ApiValidatorConfig
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 12564 $, $Date: 2017-01-11 14:31:31 +0100 (Wed, 11 Jan 2017) $
 */
public abstract class AbstractApiValidator   {

	public abstract void validatePreConformanceCheck(HttpBaseEntity<?> httpEntity,ApiOperation operation,Object ... args) throws ProcessingException,ValidatorException;
	
	public abstract void validatePostConformanceCheck(HttpBaseEntity<?> httpEntity,ApiOperation operation,Object ... args) throws ProcessingException,ValidatorException;
	
	public abstract void validateValueAsType(String value,String type) throws ProcessingException,ValidatorException;
	
	public void validate(Api api, HttpBaseEntity<?> httpEntity, Object ... args) throws ProcessingException,ValidatorException{
		
		ApiOperation operation = api.findOperation(httpEntity.getMethod(), httpEntity.getUrl());
		if(operation==null){
			throw new ProcessingException("Operation httpMethod["+httpEntity.getMethod()+"] path["+httpEntity.getUrl()+"] not found");
		}

		// es. xsd
		validatePreConformanceCheck(httpEntity, operation, args);
		
		validateConformanceCheck(httpEntity, operation);
		
		// es. elementi specifici come nomi nel xsd etc..
		validatePostConformanceCheck(httpEntity, operation, args);
		
	}
	
	private void validateConformanceCheck(HttpBaseEntity<?> httpEntity,ApiOperation operation) throws ProcessingException,ValidatorException{

		try{
			
			if(httpEntity.getContentType() != null) {
				boolean contentTypeSupported = false;
				List<ApiRequestBodyParameter> requestBodyParametersList = null;
				List<ApiResponse> responses = null;
				if(operation.getRequest()!=null &&  operation.getRequest().sizeBodyParameters()>0){
					requestBodyParametersList = operation.getRequest().getBodyParameters();
				}
				if(operation.sizeResponses()>0){
					responses = operation.getResponses();
				}
				int status = -1;
				
				if(httpEntity instanceof HttpBaseRequestEntity<?>) {
					
					if(requestBodyParametersList != null) {
						for(ApiRequestBodyParameter input: requestBodyParametersList) {
							if(input.getMediaType().equals(httpEntity.getContentType())) {
								contentTypeSupported = true;
							} 
						}
					}
				} else if(httpEntity instanceof HttpBaseResponseEntity<?>) {
					status = ((HttpBaseResponseEntity<?>)httpEntity).getStatus();
					
					if(responses != null) {
						for(ApiResponse output: responses) {
							if(status==output.getHttpReturnCode() && 
									output.getMediaType().equalsIgnoreCase(ContentTypeUtilities.readBaseTypeFromContentType(httpEntity.getContentType()))) {
								contentTypeSupported = true;
							} 
						}
					}
	
				}
				
				if(!contentTypeSupported) {
					if(status>0)
						throw new ValidatorException("Content-Type ["+httpEntity.getContentType()+"] (http response with status '"+status+"') unsupported");
					else
						throw new ValidatorException("Content-Type ["+httpEntity.getContentType()+"] unsupported");
				}
			}
			
			if(httpEntity instanceof HttpBaseRequestEntity<?>) {
			
				HttpBaseRequestEntity<?> request = (HttpBaseRequestEntity<?>) httpEntity;
				
				if(operation.getRequest()!=null &&  operation.getRequest().sizeHeaderParameters()>0){
					for (ApiRequestHeaderParameter paramHeader : operation.getRequest().getHeaderParameters()) {
						String name = paramHeader.getName();
						String value = request.getParametersTrasporto().getProperty(name);
						if(value==null){
							value = request.getParametersTrasporto().getProperty(name.toLowerCase());
						}
						if(value==null){
							value = request.getParametersTrasporto().getProperty(name.toUpperCase());
						}
						if(value==null){
							if(paramHeader.isRequired()){
								throw new ValidatorException("Required HttpHeader ["+name+"] not found");
							}
						}
						if(value!=null){
							try{
								validateValueAsType(value,paramHeader.getType());
							}catch(ValidatorException val){
								throw new ValidatorException("HttpHeader ["+name+"] with value ["+value+"] not valid (expected type ["+paramHeader.getType()+"]): "+val.getMessage(),val);
							}
						}
					}
				}
				
				if(operation.getRequest()!=null &&  operation.getRequest().sizeQueryParameters()>0){
					for (ApiRequestQueryParameter paramQuery : operation.getRequest().getQueryParameters()) {
						String name = paramQuery.getName();
						String value = request.getParametersFormBased().getProperty(name);
						if(value==null){
							value = request.getParametersFormBased().getProperty(name.toLowerCase());
						}
						if(value==null){
							value = request.getParametersFormBased().getProperty(name.toUpperCase());
						}
						if(value==null){
							if(paramQuery.isRequired()){
								throw new ValidatorException("Required QueryParameter ["+name+"] not found");
							}
						}
						if(value!=null){
							try{
								validateValueAsType(value,paramQuery.getType());
							}catch(ValidatorException val){
								throw new ValidatorException("HttpHeader ["+name+"] with value ["+value+"] not valid (expected type ["+paramQuery.getType()+"]): "+val.getMessage(),val);
							}
						}
					}
				}
				
				if(operation.getRequest()!=null &&  operation.getRequest().sizeDynamicPathParameters()>0){
					for (ApiRequestDynamicPathParameter paramDynamicPath : operation.getRequest().getDynamicPathParameters()) {
						boolean find = false;
						String valueFound = null;
						for (int i = 0; i < operation.sizePath(); i++) {
							if(operation.isDynamicPath()){
								String idDinamic = operation.getDynamicPathId(i);
								if(paramDynamicPath.getName().equals(idDinamic)){
									find = true;
									valueFound = operation.getPath(i);
									break;
								}
							}
						}
						if(!find && paramDynamicPath.isRequired()){
							throw new ValidatorException("Required DynamicPath ["+paramDynamicPath.getName()+"] not found");
						}
						if(find){
							try{
								validateValueAsType(valueFound,paramDynamicPath.getType());
							}catch(ValidatorException val){
								throw new ValidatorException("DynamicPath ["+paramDynamicPath.getName()+"] with value ["+valueFound+"] not valid (expected type ["+paramDynamicPath.getType()+"]): "+val.getMessage(),val);
							}
						}
					}
				}
				
			}

		}catch(ProcessingException e){
			throw e;
		}catch(ValidatorException e){
			throw e;
		}catch(Exception e){
			throw new ProcessingException(e.getMessage(),e);
		}
		
	}
	
}
