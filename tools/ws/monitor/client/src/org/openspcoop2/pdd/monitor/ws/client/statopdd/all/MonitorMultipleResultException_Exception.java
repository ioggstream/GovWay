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

package org.openspcoop2.pdd.monitor.ws.client.statopdd.all;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 2.7.4
 * 2014-12-01T13:15:49.860+01:00
 * Generated source version: 2.7.4
 */

@WebFault(name = "monitor-multiple-result-exception", targetNamespace = "http://www.openspcoop2.org/pdd/monitor/management")
public class MonitorMultipleResultException_Exception extends Exception {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private org.openspcoop2.pdd.monitor.ws.client.statopdd.all.MonitorMultipleResultException monitorMultipleResultException;

    public MonitorMultipleResultException_Exception() {
        super();
    }
    
    public MonitorMultipleResultException_Exception(String message) {
        super(message);
    }
    
    public MonitorMultipleResultException_Exception(String message, Throwable cause) {
        super(message, cause);
    }

    public MonitorMultipleResultException_Exception(String message, org.openspcoop2.pdd.monitor.ws.client.statopdd.all.MonitorMultipleResultException monitorMultipleResultException) {
        super(message);
        this.monitorMultipleResultException = monitorMultipleResultException;
    }

    public MonitorMultipleResultException_Exception(String message, org.openspcoop2.pdd.monitor.ws.client.statopdd.all.MonitorMultipleResultException monitorMultipleResultException, Throwable cause) {
        super(message, cause);
        this.monitorMultipleResultException = monitorMultipleResultException;
    }

    public org.openspcoop2.pdd.monitor.ws.client.statopdd.all.MonitorMultipleResultException getFaultInfo() {
        return this.monitorMultipleResultException;
    }
}
