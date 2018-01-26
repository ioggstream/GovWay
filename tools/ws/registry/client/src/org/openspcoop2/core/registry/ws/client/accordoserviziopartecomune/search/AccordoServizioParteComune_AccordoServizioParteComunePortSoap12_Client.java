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

package org.openspcoop2.core.registry.ws.client.accordoserviziopartecomune.search;

/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;

/**
 * This class was generated by Apache CXF 3.1.7
 * 2017-04-24T11:59:41.374+02:00
 * Generated source version: 3.1.7
 * 
 */
public final class AccordoServizioParteComune_AccordoServizioParteComunePortSoap12_Client {

    private static final QName SERVICE_NAME = new QName("http://www.openspcoop2.org/core/registry/management", "AccordoServizioParteComuneSoap12Service");

    private AccordoServizioParteComune_AccordoServizioParteComunePortSoap12_Client() {
    }

    public static void main(String args[]) throws java.lang.Exception {
        URL wsdlURL = AccordoServizioParteComuneSoap12Service.WSDL_LOCATION;
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
      
        AccordoServizioParteComuneSoap12Service ss = new AccordoServizioParteComuneSoap12Service(wsdlURL, AccordoServizioParteComune_AccordoServizioParteComunePortSoap12_Client.SERVICE_NAME);
        AccordoServizioParteComune port = ss.getAccordoServizioParteComunePortSoap12();
	
		new org.openspcoop2.core.registry.ws.client.utils.RequestContextUtils("accordoServizioParteComune.soap12").addRequestContextParameters((javax.xml.ws.BindingProvider)port);  
        
        {
        System.out.println("Invoking exists...");
        org.openspcoop2.core.registry.IdAccordoServizioParteComune _exists_idAccordoServizioParteComune = new org.openspcoop2.core.registry.IdAccordoServizioParteComune();
        try {
            boolean _exists__return = port.exists(_exists_idAccordoServizioParteComune);
            System.out.println("exists.result=" + _exists__return);

        } catch (RegistryNotAuthorizedException_Exception e) { 
            System.out.println("Expected exception: registry-not-authorized-exception has occurred.");
            System.out.println(e.toString());
        } catch (RegistryServiceException_Exception e) { 
            System.out.println("Expected exception: registry-service-exception has occurred.");
            System.out.println(e.toString());
        } catch (RegistryNotImplementedException_Exception e) { 
            System.out.println("Expected exception: registry-not-implemented-exception has occurred.");
            System.out.println(e.toString());
        } catch (RegistryMultipleResultException_Exception e) { 
            System.out.println("Expected exception: registry-multiple-result-exception has occurred.");
            System.out.println(e.toString());
        }
            }
        {
        System.out.println("Invoking inUse...");
        org.openspcoop2.core.registry.IdAccordoServizioParteComune _inUse_idAccordoServizioParteComune = new org.openspcoop2.core.registry.IdAccordoServizioParteComune();
        try {
            org.openspcoop2.core.registry.ws.client.accordoserviziopartecomune.search.UseInfo _inUse__return = port.inUse(_inUse_idAccordoServizioParteComune);
            System.out.println("inUse.result=" + _inUse__return);

        } catch (RegistryNotAuthorizedException_Exception e) { 
            System.out.println("Expected exception: registry-not-authorized-exception has occurred.");
            System.out.println(e.toString());
        } catch (RegistryServiceException_Exception e) { 
            System.out.println("Expected exception: registry-service-exception has occurred.");
            System.out.println(e.toString());
        } catch (RegistryNotImplementedException_Exception e) { 
            System.out.println("Expected exception: registry-not-implemented-exception has occurred.");
            System.out.println(e.toString());
        } catch (RegistryNotFoundException_Exception e) { 
            System.out.println("Expected exception: registry-not-found-exception has occurred.");
            System.out.println(e.toString());
        }
            }
        {
        System.out.println("Invoking get...");
        org.openspcoop2.core.registry.IdAccordoServizioParteComune _get_idAccordoServizioParteComune = new org.openspcoop2.core.registry.IdAccordoServizioParteComune();
        try {
            org.openspcoop2.core.registry.AccordoServizioParteComune _get__return = port.get(_get_idAccordoServizioParteComune);
            System.out.println("get.result=" + _get__return);

        } catch (RegistryNotAuthorizedException_Exception e) { 
            System.out.println("Expected exception: registry-not-authorized-exception has occurred.");
            System.out.println(e.toString());
        } catch (RegistryServiceException_Exception e) { 
            System.out.println("Expected exception: registry-service-exception has occurred.");
            System.out.println(e.toString());
        } catch (RegistryNotImplementedException_Exception e) { 
            System.out.println("Expected exception: registry-not-implemented-exception has occurred.");
            System.out.println(e.toString());
        } catch (RegistryNotFoundException_Exception e) { 
            System.out.println("Expected exception: registry-not-found-exception has occurred.");
            System.out.println(e.toString());
        } catch (RegistryMultipleResultException_Exception e) { 
            System.out.println("Expected exception: registry-multiple-result-exception has occurred.");
            System.out.println(e.toString());
        }
            }
        {
        System.out.println("Invoking count...");
        org.openspcoop2.core.registry.ws.client.accordoserviziopartecomune.search.SearchFilterAccordoServizioParteComune _count_filter = new org.openspcoop2.core.registry.ws.client.accordoserviziopartecomune.search.SearchFilterAccordoServizioParteComune();
        try {
            long _count__return = port.count(_count_filter);
            System.out.println("count.result=" + _count__return);

        } catch (RegistryNotAuthorizedException_Exception e) { 
            System.out.println("Expected exception: registry-not-authorized-exception has occurred.");
            System.out.println(e.toString());
        } catch (RegistryServiceException_Exception e) { 
            System.out.println("Expected exception: registry-service-exception has occurred.");
            System.out.println(e.toString());
        } catch (RegistryNotImplementedException_Exception e) { 
            System.out.println("Expected exception: registry-not-implemented-exception has occurred.");
            System.out.println(e.toString());
        }
            }
        {
        System.out.println("Invoking findAll...");
        org.openspcoop2.core.registry.ws.client.accordoserviziopartecomune.search.SearchFilterAccordoServizioParteComune _findAll_filter = new org.openspcoop2.core.registry.ws.client.accordoserviziopartecomune.search.SearchFilterAccordoServizioParteComune();
        try {
            java.util.List<org.openspcoop2.core.registry.AccordoServizioParteComune> _findAll__return = port.findAll(_findAll_filter);
            System.out.println("findAll.result=" + _findAll__return);

        } catch (RegistryNotAuthorizedException_Exception e) { 
            System.out.println("Expected exception: registry-not-authorized-exception has occurred.");
            System.out.println(e.toString());
        } catch (RegistryServiceException_Exception e) { 
            System.out.println("Expected exception: registry-service-exception has occurred.");
            System.out.println(e.toString());
        } catch (RegistryNotImplementedException_Exception e) { 
            System.out.println("Expected exception: registry-not-implemented-exception has occurred.");
            System.out.println(e.toString());
        }
            }
        {
        System.out.println("Invoking findAllIds...");
        org.openspcoop2.core.registry.ws.client.accordoserviziopartecomune.search.SearchFilterAccordoServizioParteComune _findAllIds_filter = new org.openspcoop2.core.registry.ws.client.accordoserviziopartecomune.search.SearchFilterAccordoServizioParteComune();
        try {
            java.util.List<org.openspcoop2.core.registry.IdAccordoServizioParteComune> _findAllIds__return = port.findAllIds(_findAllIds_filter);
            System.out.println("findAllIds.result=" + _findAllIds__return);

        } catch (RegistryNotAuthorizedException_Exception e) { 
            System.out.println("Expected exception: registry-not-authorized-exception has occurred.");
            System.out.println(e.toString());
        } catch (RegistryServiceException_Exception e) { 
            System.out.println("Expected exception: registry-service-exception has occurred.");
            System.out.println(e.toString());
        } catch (RegistryNotImplementedException_Exception e) { 
            System.out.println("Expected exception: registry-not-implemented-exception has occurred.");
            System.out.println(e.toString());
        }
            }
        {
        System.out.println("Invoking find...");
        org.openspcoop2.core.registry.ws.client.accordoserviziopartecomune.search.SearchFilterAccordoServizioParteComune _find_filter = new org.openspcoop2.core.registry.ws.client.accordoserviziopartecomune.search.SearchFilterAccordoServizioParteComune();
        try {
            org.openspcoop2.core.registry.AccordoServizioParteComune _find__return = port.find(_find_filter);
            System.out.println("find.result=" + _find__return);

        } catch (RegistryNotAuthorizedException_Exception e) { 
            System.out.println("Expected exception: registry-not-authorized-exception has occurred.");
            System.out.println(e.toString());
        } catch (RegistryServiceException_Exception e) { 
            System.out.println("Expected exception: registry-service-exception has occurred.");
            System.out.println(e.toString());
        } catch (RegistryNotImplementedException_Exception e) { 
            System.out.println("Expected exception: registry-not-implemented-exception has occurred.");
            System.out.println(e.toString());
        } catch (RegistryNotFoundException_Exception e) { 
            System.out.println("Expected exception: registry-not-found-exception has occurred.");
            System.out.println(e.toString());
        } catch (RegistryMultipleResultException_Exception e) { 
            System.out.println("Expected exception: registry-multiple-result-exception has occurred.");
            System.out.println(e.toString());
        }
            }

        System.exit(0);
    }

}
