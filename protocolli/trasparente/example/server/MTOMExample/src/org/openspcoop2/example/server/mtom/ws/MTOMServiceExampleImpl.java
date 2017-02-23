
/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package org.openspcoop2.example.server.mtom.ws;

import java.util.logging.Logger;

/**
 * This class was generated by Apache CXF 2.7.4
 * 2017-02-21T15:25:10.682+01:00
 * Generated source version: 2.7.4
 * 
 */

@javax.jws.WebService(
                      serviceName = "MTOMServiceExampleSOAP12Service",
                      portName = "MTOMServiceExampleSOAP12InterfaceEndpoint",
                      targetNamespace = "http://www.openspcoop2.org/example/server/mtom/ws",
                      wsdlLocation = "configurazionePdD/wsdl/implementazioneErogatoreSoap12.wsdl",
                      endpointInterface = "org.openspcoop2.example.server.mtom.ws.MTOMServiceExample")
                      
public class MTOMServiceExampleImpl implements MTOMServiceExample {

    private static final Logger LOG = Logger.getLogger(MTOMServiceExampleImpl.class.getName());

    /* (non-Javadoc)
     * @see org.openspcoop2.example.server.mtom.ws.MTOMServiceExample#echo(java.lang.String  richiesta ,)javax.xml.transform.Source  imageData ,)java.lang.String  risposta ,)javax.xml.transform.Source  imageDataResponse )*
     */
    @Override
	public void echo(java.lang.String richiesta,javax.xml.transform.Source imageData,javax.xml.ws.Holder<java.lang.String> risposta,javax.xml.ws.Holder<javax.xml.transform.Source> imageDataResponse) { 
        MTOMServiceExampleImpl.LOG.info("Executing operation echo");
        System.out.println(richiesta);
        System.out.println(imageData);
        try {
            java.lang.String rispostaValue = richiesta;
            risposta.value = rispostaValue;
            javax.xml.transform.Source imageDataResponseValue = imageData;
            imageDataResponse.value = imageDataResponseValue;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

}
