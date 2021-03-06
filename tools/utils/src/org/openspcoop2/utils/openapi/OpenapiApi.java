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


package org.openspcoop2.utils.openapi;

import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.utils.rest.api.Api;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;

/**
 * SwaggerApi
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class OpenapiApi extends Api {
	private OpenAPI api;
	private Map<String, Schema<?>> definitions;


	public OpenapiApi(OpenAPI swagger) {
		this.api = swagger;
		this.definitions = new HashMap<String, Schema<?>>();
	}
	
	public OpenAPI getApi() {
		return this.api;
	}

	public void setApi(OpenAPI swagger) {
		this.api = swagger;
	}

	public Map<String, Schema<?>> getDefinitions() {
		return this.definitions;
	}

	public Map<String, Schema<?>> getAllDefinitions() {
		Map<String, Schema<?>> map = new HashMap<>();
		map.putAll(this.getDefinitions());
		if(this.api.getComponents() != null && this.api.getComponents().getSchemas() != null)
			for(String k: this.api.getComponents().getSchemas().keySet()) {
				map.put(k, this.api.getComponents().getSchemas().get(k));
			}
		return map;
	}

	public void setDefinitions(Map<String, Schema<?>> definitions) {
		this.definitions = definitions;
	}

}
