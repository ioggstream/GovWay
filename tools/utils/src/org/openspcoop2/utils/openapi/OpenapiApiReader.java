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


package org.openspcoop2.utils.openapi;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.rest.ApiReaderConfig;
import org.openspcoop2.utils.rest.IApiReader;
import org.openspcoop2.utils.rest.ProcessingException;
import org.openspcoop2.utils.rest.api.AbstractApiParameter;
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
import org.openspcoop2.utils.rest.api.ApiSchema;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import io.swagger.oas.models.OpenAPI;
import io.swagger.oas.models.Operation;
import io.swagger.oas.models.PathItem;
import io.swagger.oas.models.headers.Header;
import io.swagger.oas.models.media.MediaType;
import io.swagger.oas.models.media.Schema;
import io.swagger.oas.models.parameters.CookieParameter;
import io.swagger.oas.models.parameters.HeaderParameter;
import io.swagger.oas.models.parameters.Parameter;
import io.swagger.oas.models.parameters.PathParameter;
import io.swagger.oas.models.parameters.QueryParameter;
import io.swagger.oas.models.parameters.RequestBody;
import io.swagger.parser.v3.OpenAPIV3Parser;


/**
 * SwaggerApiReader
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 13468 $, $Date: 2017-11-27 12:45:09 +0100(lun, 27 nov 2017) $
 *
 */
public class OpenapiApiReader implements IApiReader {

	private OpenAPI openApi;

	@Override
	public void init(Logger log, File file, ApiReaderConfig config) throws ProcessingException {
		this._init(log, file, config);
	}
	@Override
	public void init(Logger log, File file, ApiReaderConfig config, ApiSchema ... schema) throws ProcessingException {
		this._init(log, file, config, schema);
	}
	private void _init(Logger log, File file, ApiReaderConfig config, ApiSchema ... schema) throws ProcessingException {
		try {
			this.openApi = new OpenAPIV3Parser().read(file.getAbsolutePath());
		} catch(Exception e) {
			throw new ProcessingException(e);
		}
	}

	@Override
	public void init(Logger log, String content, String charsetName, ApiReaderConfig config) throws ProcessingException {
		this._init(log, content, charsetName, config);
	}
	@Override
	public void init(Logger log, String content, String charsetName, ApiReaderConfig config, ApiSchema ... schema) throws ProcessingException {
		this._init(log, content, charsetName, config, schema);
	}
	private void _init(Logger log, String content, String charsetName, ApiReaderConfig config, ApiSchema ... schema) throws ProcessingException {
		try {
			this.init(log, content.getBytes(charsetName), config);
		} catch(UnsupportedEncodingException e) {
			throw new ProcessingException(e);
		}

	}

	@Override
	public void init(Logger log, byte[] content, ApiReaderConfig config) throws ProcessingException {
		this._init(log, content, config);
	}
	@Override
	public void init(Logger log, byte[] content, ApiReaderConfig config, ApiSchema ... schema) throws ProcessingException {
		this._init(log, content, config, schema);
	}
	private void _init(Logger log, byte[] content, ApiReaderConfig config, ApiSchema ... schema) throws ProcessingException {
		File tempFile = null;
		try {
			tempFile = File.createTempFile("openapi", ""+System.currentTimeMillis());
			FileSystemUtilities.writeFile(tempFile, content);
			this.init(log, tempFile, config);
		} catch(Exception e) {
			throw new ProcessingException(e);
		} finally {
			if(tempFile!=null)
				tempFile.delete();
		}

	}

	@Override
	public void init(Logger log, Document doc, ApiReaderConfig config) throws ProcessingException {
		this._init(log, doc, config);
	}
	@Override
	public void init(Logger log, Document doc, ApiReaderConfig config, ApiSchema ... schema) throws ProcessingException {
		this._init(log, doc, config, schema);
	}
	private void _init(Logger log, Document doc, ApiReaderConfig config, ApiSchema ... schema) throws ProcessingException {
		throw new ProcessingException("Not implemented");
	}

	@Override
	public void init(Logger log, Element element, ApiReaderConfig config) throws ProcessingException {
		this._init(log, element, config);
	}
	@Override
	public void init(Logger log, Element element, ApiReaderConfig config, ApiSchema ... schema) throws ProcessingException {
		this._init(log, element, config, schema);
	}
	private void _init(Logger log, Element element, ApiReaderConfig config, ApiSchema ... schema) throws ProcessingException {
		throw new ProcessingException("Not implemented");
	}

	@Override
	public void init(Logger log, URI uri, ApiReaderConfig config) throws ProcessingException {
		this._init(log, uri, config);
	}
	@Override
	public void init(Logger log, URI uri, ApiReaderConfig config, ApiSchema ... schema) throws ProcessingException {
		this._init(log, uri, config, schema);
	}
	private void _init(Logger log, URI uri, ApiReaderConfig config, ApiSchema ... schema) throws ProcessingException {
		this.openApi = new OpenAPIV3Parser().read(uri.toString());

	}

	@Override
	public Api read() throws ProcessingException {
		if(this.openApi == null)
			throw new ProcessingException("Api non correttamente inizializzata");
		try {
			OpenapiApi api = new OpenapiApi(this.openApi);
			if(!this.openApi.getServers().isEmpty())
				api.setBaseURL(new URL(this.openApi.getServers().get(0).getUrl()));

			if(this.openApi.getPaths() != null){
				for (String pathK : this.openApi.getPaths().keySet()) {
					PathItem path = this.openApi.getPaths().get(pathK);
					if(path.getGet() != null) {
						ApiOperation operation = getOperation(path.getGet(), HttpRequestMethod.GET, pathK, api);
						api.addOperation(operation);
					}
					if(path.getPost() != null) {
						ApiOperation operation = getOperation(path.getPost(), HttpRequestMethod.POST, pathK, api);
						api.addOperation(operation);
					}
					if(path.getPut() != null) {
						ApiOperation operation = getOperation(path.getPut(), HttpRequestMethod.PUT, pathK, api);
						api.addOperation(operation);
					}
					if(path.getDelete() != null) {
						ApiOperation operation = getOperation(path.getDelete(), HttpRequestMethod.DELETE, pathK, api);
						api.addOperation(operation);
					}
					if(path.getPatch() != null) {
						ApiOperation operation = getOperation(path.getPatch(), HttpRequestMethod.PATCH, pathK, api);
						api.addOperation(operation);
					}
					if(path.getOptions() != null) {
						ApiOperation operation = getOperation(path.getOptions(), HttpRequestMethod.OPTIONS, pathK, api);
						api.addOperation(operation);
					}
				}
			}
			return api;
		} catch(Exception e){
			throw new ProcessingException(e);
		}
	}

	private ApiOperation getOperation(Operation operation, HttpRequestMethod method, String pathK, OpenapiApi api) {
		ApiOperation apiOperation = new ApiOperation(method, pathK);
		if(operation.getParameters() != null || operation.getRequestBody() != null) {
			ApiRequest request = new ApiRequest();

			if(operation.getParameters() != null) {
				for(Parameter param: operation.getParameters()) {

					AbstractApiParameter abstractParam = createRequestParameter(param);

					if(abstractParam != null) {

						if(abstractParam instanceof ApiRequestDynamicPathParameter) {
							request.addDynamicPathParameter((ApiRequestDynamicPathParameter) abstractParam);
						} else if(abstractParam instanceof ApiRequestQueryParameter) {
							request.addQueryParameter((ApiRequestQueryParameter) abstractParam);
						} else if(abstractParam instanceof ApiHeaderParameter) {
							request.addHeaderParameter((ApiHeaderParameter) abstractParam);
						} else if(abstractParam instanceof ApiCookieParameter) {
							request.addCookieParameter((ApiCookieParameter) abstractParam);
						} else if(abstractParam instanceof ApiRequestFormParameter) {
							request.addFormParameter((ApiRequestFormParameter) abstractParam);
						}
					}
				}
			}
			if(operation.getRequestBody() != null) {
				List<ApiBodyParameter> lst = createRequestBody(operation.getRequestBody(), method, pathK, api);
				for(ApiBodyParameter param: lst) {
					request.addBodyParameter(param);
				}
			}

			apiOperation.setRequest(request);
		}

		if(operation.getResponses()!= null && !operation.getResponses().isEmpty()) {
			List<ApiResponse> responses = new ArrayList<ApiResponse>();
			for(String responseK: operation.getResponses().keySet()) {
				responses.add(createResponses(responseK, operation.getResponses().get(responseK), method, pathK, api));	
			}
			apiOperation.setResponses(responses);
		}

		return apiOperation;
	}

	private List<ApiBodyParameter> createRequestBody(RequestBody requestBody, HttpRequestMethod method, String path, OpenapiApi api) {

		List<ApiBodyParameter> lst = new ArrayList<ApiBodyParameter>();

		if(requestBody.getContent() != null && !requestBody.getContent().isEmpty()) {
			for(String consume: requestBody.getContent().keySet()) {

				Schema<?> model = requestBody.getContent().get(consume).getSchema();

				if(model.get$ref()!= null) {

					ApiBodyParameter bodyParam = new ApiBodyParameter(model.get$ref());
					bodyParam.setMediaType(consume);
					bodyParam.setElement(model.get$ref().replaceAll("#/components/schemas/", ""));
					if(requestBody.getRequired() != null)
						bodyParam.setRequired(requestBody.getRequired());
					lst.add(bodyParam);
				} else {
					String type = ("request_" + method.toString() + "_" + path+ "_" + consume).replace("/", "_");
	
					api.getDefinitions().put(type, model);
	
					ApiBodyParameter bodyParam = new ApiBodyParameter(type);
					bodyParam.setMediaType(consume);
					bodyParam.setElement(type);
					if(requestBody.getRequired() != null)
						bodyParam.setRequired(requestBody.getRequired());
					lst.add(bodyParam);
				}
			}
		}

		return lst;
	}

	private AbstractApiParameter createRequestParameter(Parameter param) {

		AbstractApiParameter abstractParam = null;
		if(param instanceof PathParameter) {
			abstractParam = new ApiRequestDynamicPathParameter(param.getName(), ((PathParameter)param).getSchema().getType());
		} else if(param instanceof QueryParameter) {
			abstractParam = new ApiRequestQueryParameter(param.getName(), ((QueryParameter)param).getSchema().getType());
		} else if(param instanceof HeaderParameter) {
			abstractParam = new ApiHeaderParameter(param.getName(), ((HeaderParameter)param).getSchema().getType());
		} else if(param instanceof CookieParameter) {
			abstractParam = new ApiCookieParameter(param.getName(), ((CookieParameter)param).getSchema().getType());
		}

		if(abstractParam != null) {
			abstractParam.setDescription(param.getDescription());
			abstractParam.setRequired(param.getRequired());
		}

		return abstractParam;
	}

	private ApiResponse createResponses(String responseK, io.swagger.oas.models.responses.ApiResponse response, HttpRequestMethod method, String path, OpenapiApi api) {

		ApiResponse apiResponse= new ApiResponse();

		int status = -1;
		try{
			status = Integer.parseInt(responseK);
		} catch(NumberFormatException e) {}
		if(status<=0) {
			status = 200;
		}
		apiResponse.setDescription(response.getDescription());
		apiResponse.setHttpReturnCode(status);

		if(response.getHeaders() != null) {
			for(String header: response.getHeaders().keySet()) {
				Header property = response.getHeaders().get(header);
				
				String type = null;
				if(property.get$ref() != null) {
					type = property.get$ref().replaceAll("#/components/schemas/", "");
				} else if(property.getSchema() != null) {
					if(property.getSchema().get$ref() != null) {
						type = property.getSchema().get$ref().replaceAll("#/components/schemas/", "");
					} else {
						if(property.getSchema().getFormat() != null) {
							type = property.getSchema().getFormat();
						} else {
							type = property.getSchema().getType();						
						}
					}
				}
				
				ApiHeaderParameter parameter = new ApiHeaderParameter(header, type);
				parameter.setDescription(property.getDescription());
				if(property.getRequired() != null)
					parameter.setRequired(property.getRequired());
				
				apiResponse.addHeaderParameter(parameter);
			}
		}

		if(response.getContent() != null && !response.getContent().isEmpty()) {
			for(String contentType: response.getContent().keySet()) {

				MediaType mediaType = response.getContent().get(contentType);
				Schema<?> model = mediaType.getSchema();

				String type = ("response_" +method.toString() + "_" + path + "_" + contentType).replace("/", "_");

				api.getDefinitions().put(type, model);

				ApiBodyParameter bodyParam = new ApiBodyParameter(type);
				bodyParam.setMediaType(contentType);
				bodyParam.setElement(type);
				apiResponse.addBodyParameter(bodyParam);
			}
		}

		return apiResponse;
	}

}
