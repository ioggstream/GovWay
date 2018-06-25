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

package org.openspcoop2.pdd.monitor.ws.client.statopdd.all;

/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;

/**
 * This class was generated by Apache CXF 2.7.4
 * 2014-12-01T13:15:49.770+01:00
 * Generated source version: 2.7.4
 * 
 */
public final class StatoPdd_StatoPddPortSoap12_Client {

    private static final QName SERVICE_NAME = new QName("http://www.openspcoop2.org/pdd/monitor/management", "StatoPddSoap12Service");

    private StatoPdd_StatoPddPortSoap12_Client() {
    }

    public static void main(String args[]) throws java.lang.Exception {
        URL wsdlURL = StatoPddSoap12Service.WSDL_LOCATION;
        if (args.length > 0 && args[0] != null && !"".equals(args[0])) { 
            File wsdlFile = new File(args[0]);
            try {
                if (wsdlFile.exists()) {
                    wsdlURL = wsdlFile.toURI().toURL();
                } else {
                    wsdlURL = new URL(args[0]);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
      
        StatoPddSoap12Service ss = new StatoPddSoap12Service(wsdlURL, StatoPdd_StatoPddPortSoap12_Client.SERVICE_NAME);
        StatoPdd port = ss.getStatoPddPortSoap12();
	
		new org.openspcoop2.pdd.monitor.ws.client.utils.RequestContextUtils("statoPdd.soap12").addRequestContextParameters((javax.xml.ws.BindingProvider)port);  
        
        {
        System.out.println("Invoking find...");
        org.openspcoop2.pdd.monitor.ws.client.statopdd.all.SearchFilterStatoPdd _find_filter = null;
        try {
            org.openspcoop2.pdd.monitor.StatoPdd _find__return = port.find(_find_filter);
            System.out.println("find.result=" + _find__return);

        } catch (MonitorNotFoundException_Exception e) { 
            System.out.println("Expected exception: monitor-not-found-exception has occurred.");
            System.out.println(e.toString());
        } catch (MonitorMultipleResultException_Exception e) { 
            System.out.println("Expected exception: monitor-multiple-result-exception has occurred.");
            System.out.println(e.toString());
        } catch (MonitorServiceException_Exception e) { 
            System.out.println("Expected exception: monitor-service-exception has occurred.");
            System.out.println(e.toString());
        } catch (MonitorNotImplementedException_Exception e) { 
            System.out.println("Expected exception: monitor-not-implemented-exception has occurred.");
            System.out.println(e.toString());
        } catch (MonitorNotAuthorizedException_Exception e) { 
            System.out.println("Expected exception: monitor-not-authorized-exception has occurred.");
            System.out.println(e.toString());
        }
            }

        System.exit(0);
    }

}
