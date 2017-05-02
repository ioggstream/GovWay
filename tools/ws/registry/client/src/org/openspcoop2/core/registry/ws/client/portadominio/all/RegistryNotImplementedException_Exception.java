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

package org.openspcoop2.core.registry.ws.client.portadominio.all;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 3.1.7
 * 2017-04-24T12:00:50.781+02:00
 * Generated source version: 3.1.7
 */

@WebFault(name = "registry-not-implemented-exception", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
public class RegistryNotImplementedException_Exception extends Exception {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private org.openspcoop2.core.registry.ws.client.portadominio.all.RegistryNotImplementedException registryNotImplementedException;

    public RegistryNotImplementedException_Exception() {
        super();
    }
    
    public RegistryNotImplementedException_Exception(String message) {
        super(message);
    }
    
    public RegistryNotImplementedException_Exception(String message, Throwable cause) {
        super(message, cause);
    }

    public RegistryNotImplementedException_Exception(String message, org.openspcoop2.core.registry.ws.client.portadominio.all.RegistryNotImplementedException registryNotImplementedException) {
        super(message);
        this.registryNotImplementedException = registryNotImplementedException;
    }

    public RegistryNotImplementedException_Exception(String message, org.openspcoop2.core.registry.ws.client.portadominio.all.RegistryNotImplementedException registryNotImplementedException, Throwable cause) {
        super(message, cause);
        this.registryNotImplementedException = registryNotImplementedException;
    }

    public org.openspcoop2.core.registry.ws.client.portadominio.all.RegistryNotImplementedException getFaultInfo() {
        return this.registryNotImplementedException;
    }
}
