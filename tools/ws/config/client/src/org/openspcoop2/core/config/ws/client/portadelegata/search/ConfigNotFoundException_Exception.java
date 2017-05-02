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

package org.openspcoop2.core.config.ws.client.portadelegata.search;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 3.1.7
 * 2017-04-24T11:45:47.674+02:00
 * Generated source version: 3.1.7
 */

@WebFault(name = "config-not-found-exception", targetNamespace = "http://www.openspcoop2.org/core/config/management")
public class ConfigNotFoundException_Exception extends Exception {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private org.openspcoop2.core.config.ws.client.portadelegata.search.ConfigNotFoundException configNotFoundException;

    public ConfigNotFoundException_Exception() {
        super();
    }
    
    public ConfigNotFoundException_Exception(String message) {
        super(message);
    }
    
    public ConfigNotFoundException_Exception(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigNotFoundException_Exception(String message, org.openspcoop2.core.config.ws.client.portadelegata.search.ConfigNotFoundException configNotFoundException) {
        super(message);
        this.configNotFoundException = configNotFoundException;
    }

    public ConfigNotFoundException_Exception(String message, org.openspcoop2.core.config.ws.client.portadelegata.search.ConfigNotFoundException configNotFoundException, Throwable cause) {
        super(message, cause);
        this.configNotFoundException = configNotFoundException;
    }

    public org.openspcoop2.core.config.ws.client.portadelegata.search.ConfigNotFoundException getFaultInfo() {
        return this.configNotFoundException;
    }
}
