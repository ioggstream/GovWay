/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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

package org.openspcoop2.core.registry.ws.client.soggetto.all;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 3.1.7
 * 2017-04-24T12:04:01.880+02:00
 * Generated source version: 3.1.7
 */

@WebFault(name = "registry-multiple-result-exception", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
public class RegistryMultipleResultException_Exception extends Exception {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private org.openspcoop2.core.registry.ws.client.soggetto.all.RegistryMultipleResultException registryMultipleResultException;

    public RegistryMultipleResultException_Exception() {
        super();
    }
    
    public RegistryMultipleResultException_Exception(String message) {
        super(message);
    }
    
    public RegistryMultipleResultException_Exception(String message, Throwable cause) {
        super(message, cause);
    }

    public RegistryMultipleResultException_Exception(String message, org.openspcoop2.core.registry.ws.client.soggetto.all.RegistryMultipleResultException registryMultipleResultException) {
        super(message);
        this.registryMultipleResultException = registryMultipleResultException;
    }

    public RegistryMultipleResultException_Exception(String message, org.openspcoop2.core.registry.ws.client.soggetto.all.RegistryMultipleResultException registryMultipleResultException, Throwable cause) {
        super(message, cause);
        this.registryMultipleResultException = registryMultipleResultException;
    }

    public org.openspcoop2.core.registry.ws.client.soggetto.all.RegistryMultipleResultException getFaultInfo() {
        return this.registryMultipleResultException;
    }
}
