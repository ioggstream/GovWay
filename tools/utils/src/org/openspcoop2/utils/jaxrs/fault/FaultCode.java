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

package org.openspcoop2.utils.jaxrs.fault;

import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;


/**	
 * WebApplicationExceptionMapper
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FaultCode {

	public static final FaultCode RICHIESTA_NON_VALIDA = new FaultCode(400, "RICHIESTE NON VALIDA", "Richiesta non correttamente formata");
	public static final FaultCode AUTENTICAZIONE = new FaultCode(401, "AUTENTICAZIONE", "Mittente della richiesta non autenticato"); 
	public static final FaultCode AUTORIZZAZIONE = new FaultCode(403, "AUTORIZZAZIONE", "Richiesta non permessa");
	public static final FaultCode NOT_FOUND = new FaultCode(404, "NOT_FOUND", "Risorsa non trovata");
	public static final FaultCode ERRORE_INTERNO = new FaultCode(500,"ERRORE INTERNO","Errore interno");


	private final String name;
	private final String descrizione;
	private final int code;

	public FaultCode(int code, String name, String descrizione)
	{
		this.code = code;
		this.name = name;
		this.descrizione = descrizione;
	}

	public int getCode() {
		return this.code;
	}
	public String getName() {
		return this.name;
	}
	public String getDescrizione()
	{
		return this.descrizione;
	}

	@Override
	public String toString() {
		return this.descrizione;
	}

	public Fault toFault() {
		Fault Fault = new Fault();
		Fault.setCodice(this.name);
		Fault.setDescrizione(this.descrizione);
		return Fault;
	}
	public Fault toFault(String dettaglio) {
		Fault Fault = this.toFault();
		Fault.setDettaglio(dettaglio);
		return Fault;
	}
	public Fault toFault(Exception e) {
		Fault Fault = this.toFault();
		Fault.setDettaglio(e.getMessage());
		return Fault;
	}

	public ResponseBuilder toFaultResponseBuilder() {
		return this.toFaultResponseBuilder(true);
	}
	public ResponseBuilder toFaultResponseBuilder(boolean addFault) {
		Fault Fault = this.toFault();
		ResponseBuilder rb = Response.status(this.code);
		if(addFault) {
			rb.entity(Fault).type(MediaType.APPLICATION_JSON);
		}
		return rb;
	}
	public ResponseBuilder toFaultResponseBuilder(String dettaglio) {
		Fault Fault = this.toFault(dettaglio);
		return Response.status(this.code).entity(Fault).type(MediaType.APPLICATION_JSON);
	}
	public ResponseBuilder toFaultResponseBuilder(Exception e) {
		Fault Fault = this.toFault(e);
		return Response.status(this.code).entity(Fault).type(MediaType.APPLICATION_JSON);
	}
	
	public Response toFaultResponse() {
		return this.toFaultResponse(true);
	}
	public Response toFaultResponse(boolean addFault) {
		return this.toFaultResponseBuilder(addFault).build();
	}
	public Response toFaultResponse(String dettaglio) {
		return this.toFaultResponseBuilder(dettaglio).build();
	}
	public Response toFaultResponse(Exception e) {
		return this.toFaultResponseBuilder(e).build();
	}

	public javax.ws.rs.WebApplicationException toException(ResponseBuilder responseBuilder){
		return new javax.ws.rs.WebApplicationException(responseBuilder.build());
	}
	public javax.ws.rs.WebApplicationException toException(ResponseBuilder responseBuilder, Map<String, String> headers){
		if(headers!=null && !headers.isEmpty()) {
			headers.keySet().stream().forEach(k -> {
				responseBuilder.header(k, headers.get(k));
			});
		}
		return new javax.ws.rs.WebApplicationException(responseBuilder.build());
	}
	public javax.ws.rs.WebApplicationException toException(Response response){
		// Aggiunta eccezione nel costruttore, in modo che cxf chiami la classe WebApplicationExceptionMapper
		return new javax.ws.rs.WebApplicationException(new Exception(response.getEntity().toString()),response);
	}
	public javax.ws.rs.WebApplicationException toException(){
		return this.toException(true);
	}
	public javax.ws.rs.WebApplicationException toException(boolean addFault){
		Response r = this.toFaultResponse(addFault);
		return this.toException(r);
	}
	public javax.ws.rs.WebApplicationException toException(String dettaglio){
		Response r = this.toFaultResponse(dettaglio);
		return this.toException(r);
	}
	public javax.ws.rs.WebApplicationException toException(Exception e){
		Response r = this.toFaultResponse(e);
		return this.toException(r);
	}

	public void throwException(ResponseBuilder responseBuilder) throws javax.ws.rs.WebApplicationException{
		throw this.toException(responseBuilder);
	}
	public void throwException(Response response) throws javax.ws.rs.WebApplicationException{
		throw this.toException(response);
	}
	public void throwException() throws javax.ws.rs.WebApplicationException{
		throw this.toException();
	}
	public void throwException(boolean addFault) throws javax.ws.rs.WebApplicationException{
		throw toException(addFault);
	}
	public void throwException(String dettaglio) throws javax.ws.rs.WebApplicationException{
		throw toException(dettaglio);
	}
	public void throwException(Exception e) throws javax.ws.rs.WebApplicationException{
		throw toException(e);
	}
	

}
