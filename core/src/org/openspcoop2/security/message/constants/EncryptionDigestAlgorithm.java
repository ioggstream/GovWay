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

package org.openspcoop2.security.message.constants;

/**     
 * EncryptionDigestAlgorithm
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum EncryptionDigestAlgorithm {

	SHA1("http://www.w3.org/2000/09/xmldsig#sha1"),
	SHA224("http://www.w3.org/2001/04/xmldsig-more#sha224"),
	SHA256("http://www.w3.org/2001/04/xmlenc#sha256"),
	SHA384("http://www.w3.org/2000/09/xmldsig#sha384"),
	SHA512("http://www.w3.org/2001/04/xmlenc#sha512"),
	RIPEMD160("http://www.w3.org/2001/04/xmlenc#ripemd160");
	
	private String uri;
	EncryptionDigestAlgorithm(String uri) {
		this.uri = uri;
	}
	
	public String getUri() {
		return this.uri;
	}
}
