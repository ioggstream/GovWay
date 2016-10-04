/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

package org.openspcoop2.core.diagnostica.ws.client.informazioniprotocollotransazione.search;

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
 * 2015-02-20T15:28:43.074+01:00
 * Generated source version: 2.7.4
 * 
 */
public final class InformazioniProtocolloTransazione_InformazioniProtocolloTransazionePortSoap12_Client {

    private static final QName SERVICE_NAME = new QName("http://www.openspcoop2.org/core/diagnostica/management", "InformazioniProtocolloTransazioneSoap12Service");

    private InformazioniProtocolloTransazione_InformazioniProtocolloTransazionePortSoap12_Client() {
    }

    public static void main(String args[]) throws java.lang.Exception {
        URL wsdlURL = InformazioniProtocolloTransazioneSoap12Service.WSDL_LOCATION;
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
      
        InformazioniProtocolloTransazioneSoap12Service ss = new InformazioniProtocolloTransazioneSoap12Service(wsdlURL, InformazioniProtocolloTransazione_InformazioniProtocolloTransazionePortSoap12_Client.SERVICE_NAME);
        InformazioniProtocolloTransazione port = ss.getInformazioniProtocolloTransazionePortSoap12();
	
		new org.openspcoop2.core.diagnostica.ws.client.utils.RequestContextUtils("informazioniProtocolloTransazione.soap12").addRequestContextParameters((javax.xml.ws.BindingProvider)port);  
        
        {
        System.out.println("Invoking findAll...");
        org.openspcoop2.core.diagnostica.ws.client.informazioniprotocollotransazione.search.SearchFilterInformazioniProtocolloTransazione _findAll_filter = new org.openspcoop2.core.diagnostica.ws.client.informazioniprotocollotransazione.search.SearchFilterInformazioniProtocolloTransazione();
        try {
            java.util.List<org.openspcoop2.core.diagnostica.InformazioniProtocolloTransazione> _findAll__return = port.findAll(_findAll_filter);
            System.out.println("findAll.result=" + _findAll__return);

        } catch (DiagnosticaServiceException_Exception e) { 
            System.out.println("Expected exception: diagnostica-service-exception has occurred.");
            System.out.println(e.toString());
        } catch (DiagnosticaNotAuthorizedException_Exception e) { 
            System.out.println("Expected exception: diagnostica-not-authorized-exception has occurred.");
            System.out.println(e.toString());
        } catch (DiagnosticaNotImplementedException_Exception e) { 
            System.out.println("Expected exception: diagnostica-not-implemented-exception has occurred.");
            System.out.println(e.toString());
        }
            }
        {
        System.out.println("Invoking get...");
        org.openspcoop2.core.diagnostica.IdInformazioniProtocolloTransazione _get_idInformazioniProtocolloTransazione = new org.openspcoop2.core.diagnostica.IdInformazioniProtocolloTransazione();
        try {
            org.openspcoop2.core.diagnostica.InformazioniProtocolloTransazione _get__return = port.get(_get_idInformazioniProtocolloTransazione);
            System.out.println("get.result=" + _get__return);

        } catch (DiagnosticaServiceException_Exception e) { 
            System.out.println("Expected exception: diagnostica-service-exception has occurred.");
            System.out.println(e.toString());
        } catch (DiagnosticaNotFoundException_Exception e) { 
            System.out.println("Expected exception: diagnostica-not-found-exception has occurred.");
            System.out.println(e.toString());
        } catch (DiagnosticaNotAuthorizedException_Exception e) { 
            System.out.println("Expected exception: diagnostica-not-authorized-exception has occurred.");
            System.out.println(e.toString());
        } catch (DiagnosticaNotImplementedException_Exception e) { 
            System.out.println("Expected exception: diagnostica-not-implemented-exception has occurred.");
            System.out.println(e.toString());
        } catch (DiagnosticaMultipleResultException_Exception e) { 
            System.out.println("Expected exception: diagnostica-multiple-result-exception has occurred.");
            System.out.println(e.toString());
        }
            }
        {
        System.out.println("Invoking find...");
        org.openspcoop2.core.diagnostica.ws.client.informazioniprotocollotransazione.search.SearchFilterInformazioniProtocolloTransazione _find_filter = new org.openspcoop2.core.diagnostica.ws.client.informazioniprotocollotransazione.search.SearchFilterInformazioniProtocolloTransazione();
        try {
            org.openspcoop2.core.diagnostica.InformazioniProtocolloTransazione _find__return = port.find(_find_filter);
            System.out.println("find.result=" + _find__return);

        } catch (DiagnosticaServiceException_Exception e) { 
            System.out.println("Expected exception: diagnostica-service-exception has occurred.");
            System.out.println(e.toString());
        } catch (DiagnosticaNotFoundException_Exception e) { 
            System.out.println("Expected exception: diagnostica-not-found-exception has occurred.");
            System.out.println(e.toString());
        } catch (DiagnosticaNotAuthorizedException_Exception e) { 
            System.out.println("Expected exception: diagnostica-not-authorized-exception has occurred.");
            System.out.println(e.toString());
        } catch (DiagnosticaNotImplementedException_Exception e) { 
            System.out.println("Expected exception: diagnostica-not-implemented-exception has occurred.");
            System.out.println(e.toString());
        } catch (DiagnosticaMultipleResultException_Exception e) { 
            System.out.println("Expected exception: diagnostica-multiple-result-exception has occurred.");
            System.out.println(e.toString());
        }
            }
        {
        System.out.println("Invoking findAllIds...");
        org.openspcoop2.core.diagnostica.ws.client.informazioniprotocollotransazione.search.SearchFilterInformazioniProtocolloTransazione _findAllIds_filter = new org.openspcoop2.core.diagnostica.ws.client.informazioniprotocollotransazione.search.SearchFilterInformazioniProtocolloTransazione();
        try {
            java.util.List<org.openspcoop2.core.diagnostica.IdInformazioniProtocolloTransazione> _findAllIds__return = port.findAllIds(_findAllIds_filter);
            System.out.println("findAllIds.result=" + _findAllIds__return);

        } catch (DiagnosticaServiceException_Exception e) { 
            System.out.println("Expected exception: diagnostica-service-exception has occurred.");
            System.out.println(e.toString());
        } catch (DiagnosticaNotAuthorizedException_Exception e) { 
            System.out.println("Expected exception: diagnostica-not-authorized-exception has occurred.");
            System.out.println(e.toString());
        } catch (DiagnosticaNotImplementedException_Exception e) { 
            System.out.println("Expected exception: diagnostica-not-implemented-exception has occurred.");
            System.out.println(e.toString());
        }
            }
        {
        System.out.println("Invoking exists...");
        org.openspcoop2.core.diagnostica.IdInformazioniProtocolloTransazione _exists_idInformazioniProtocolloTransazione = new org.openspcoop2.core.diagnostica.IdInformazioniProtocolloTransazione();
        try {
            boolean _exists__return = port.exists(_exists_idInformazioniProtocolloTransazione);
            System.out.println("exists.result=" + _exists__return);

        } catch (DiagnosticaServiceException_Exception e) { 
            System.out.println("Expected exception: diagnostica-service-exception has occurred.");
            System.out.println(e.toString());
        } catch (DiagnosticaNotAuthorizedException_Exception e) { 
            System.out.println("Expected exception: diagnostica-not-authorized-exception has occurred.");
            System.out.println(e.toString());
        } catch (DiagnosticaNotImplementedException_Exception e) { 
            System.out.println("Expected exception: diagnostica-not-implemented-exception has occurred.");
            System.out.println(e.toString());
        } catch (DiagnosticaMultipleResultException_Exception e) { 
            System.out.println("Expected exception: diagnostica-multiple-result-exception has occurred.");
            System.out.println(e.toString());
        }
            }
        {
        System.out.println("Invoking count...");
        org.openspcoop2.core.diagnostica.ws.client.informazioniprotocollotransazione.search.SearchFilterInformazioniProtocolloTransazione _count_filter = new org.openspcoop2.core.diagnostica.ws.client.informazioniprotocollotransazione.search.SearchFilterInformazioniProtocolloTransazione();
        try {
            long _count__return = port.count(_count_filter);
            System.out.println("count.result=" + _count__return);

        } catch (DiagnosticaServiceException_Exception e) { 
            System.out.println("Expected exception: diagnostica-service-exception has occurred.");
            System.out.println(e.toString());
        } catch (DiagnosticaNotAuthorizedException_Exception e) { 
            System.out.println("Expected exception: diagnostica-not-authorized-exception has occurred.");
            System.out.println(e.toString());
        } catch (DiagnosticaNotImplementedException_Exception e) { 
            System.out.println("Expected exception: diagnostica-not-implemented-exception has occurred.");
            System.out.println(e.toString());
        }
            }

        System.exit(0);
    }

}
