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
package org.openspcoop2.pdd.core.token.parser;

import java.util.Map;

/**     
 * BasicTokenUserInfoParser
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BasicTokenUserInfoParser implements ITokenUserInfoParser {

	protected String raw;
	protected Map<String, String> claims;
	protected TipologiaClaims parser;
	
	public BasicTokenUserInfoParser(TipologiaClaims parser) {
		this.parser = parser;
	}
	
	@Override
	public void init(String raw, Map<String, String> claims) {
		this.raw = raw;
		this.claims = claims;
	}

	@Override
	public String getFullName() {
		switch (this.parser) {
		case OIDC_ID_TOKEN:
			return this.claims.get(Claims.OIDC_ID_CLAIMS_NAME);
		case GOOGLE:
			return this.claims.get(Claims.GOOGLE_CLAIMS_NAME);
		case JSON_WEB_TOKEN_RFC_7519:
		case INTROSPECTION_RESPONSE_RFC_7662:
		case CUSTOM:
			return null;
		}
		return null;
	}
	
	@Override
	public String getFirstName() {
		switch (this.parser) {
		case OIDC_ID_TOKEN:
			return this.claims.get(Claims.OIDC_ID_CLAIMS_GIVE_NAME);
		case GOOGLE:
			return this.claims.get(Claims.GOOGLE_CLAIMS_GIVE_NAME);
		case JSON_WEB_TOKEN_RFC_7519:
		case INTROSPECTION_RESPONSE_RFC_7662:
		case CUSTOM:
			return null;
		}
		return null;
	}
	
	@Override
	public String getMiddleName() {
		switch (this.parser) {
		case OIDC_ID_TOKEN:
			return this.claims.get(Claims.OIDC_ID_CLAIMS_MIDDLE_NAME);
		case GOOGLE:
			return this.claims.get(Claims.GOOGLE_CLAIMS_MIDDLE_NAME);
		case JSON_WEB_TOKEN_RFC_7519:
		case INTROSPECTION_RESPONSE_RFC_7662:
		case CUSTOM:
			return null;
		}
		return null;
	}
	
	
	@Override
	public String getFamilyName() {
		switch (this.parser) {
		case OIDC_ID_TOKEN:
			String tmp = this.claims.get(Claims.OIDC_ID_CLAIMS_FAMILY_NAME);
			if(tmp==null) {
				tmp = this.claims.get(Claims.OIDC_ID_CLAIMS_LAST_NAME);
			}
			return tmp;
		case GOOGLE:
			return this.claims.get(Claims.GOOGLE_CLAIMS_FAMILY_NAME);
		case JSON_WEB_TOKEN_RFC_7519:
		case INTROSPECTION_RESPONSE_RFC_7662:
		case CUSTOM:
			return null;
		}
		return null;
	}
	
	
	@Override
	public String getEMail() {
		switch (this.parser) {
		case OIDC_ID_TOKEN:
			return this.claims.get(Claims.OIDC_ID_CLAIMS_EMAIL);
		case GOOGLE:
			return this.claims.get(Claims.GOOGLE_CLAIMS_EMAIL);
		case JSON_WEB_TOKEN_RFC_7519:
		case INTROSPECTION_RESPONSE_RFC_7662:
		case CUSTOM:
			return null;
		}
		return null;
	}
	
	
}
