package org.openspcoop2.core.transazioni.ws.server.exception;

import java.io.Serializable;

/**     
 * TransazioniNotAuthorizedException_Exception (contains FaultInfo TransazioniNotAuthorizedException)
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


@javax.xml.ws.WebFault(name = "transazioni-not-authorized-exception", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management")
public class TransazioniNotAuthorizedException_Exception extends Exception implements Serializable {
	
	private static final long serialVersionUID = -1L;

	private org.openspcoop2.core.transazioni.ws.server.exception.TransazioniNotAuthorizedException faultInfo;

	// Deprecated: Costruttori utili se i beans vengono usati al posto di quelli generati tramite tools automatici
	@Deprecated
	public TransazioniNotAuthorizedException_Exception(String message, java.lang.Throwable t){
		super(message, t);
	}
	@Deprecated
	public TransazioniNotAuthorizedException_Exception(String message){
		super(message);
	}
	
	public TransazioniNotAuthorizedException_Exception(String message, org.openspcoop2.core.transazioni.ws.server.exception.TransazioniNotAuthorizedException faultInfo) {
		super(message);
		this.faultInfo = faultInfo;
    }

	public TransazioniNotAuthorizedException_Exception(String message, org.openspcoop2.core.transazioni.ws.server.exception.TransazioniNotAuthorizedException faultInfo, Throwable cause) {
		super(message, cause);
		this.faultInfo = faultInfo;
	}

	public org.openspcoop2.core.transazioni.ws.server.exception.TransazioniNotAuthorizedException getFaultInfo() {
		return this.faultInfo;
	}

}