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


package org.openspcoop2.pdd.services.core;

import java.util.List;

import org.openspcoop2.message.constants.IntegrationError;
import org.openspcoop2.message.exception.ParseException;
import org.openspcoop2.pdd.logger.Tracciamento;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.Integrazione;
import org.openspcoop2.protocol.sdk.constants.ErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.security.message.MessageSecurityContext;

/**
 * Informazioni per la gestione della risposta inviata come errore
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RicezioneBusteParametriGenerazioneBustaErrore  extends RicezioneBusteParametriGestioneBustaErrore{

	private IntegrationError integrationError = IntegrationError.INTERNAL_ERROR; // default
	
	private ParseException parseException;
	
	private Tracciamento tracciamento;
	private boolean isErroreProcessamento;
	
	private ErroreCooperazione erroreCooperazione;
	private List<Eccezione> error;
	private ErroreIntegrazione erroreIntegrazione;
	
	private FlowProperties flowPropertiesResponse;
	private MessageSecurityContext messageSecurityContext;
	private Exception eccezioneProcessamento;
	private Integrazione integrazione;
	
	public IntegrationError getIntegrationError() {
		return this.integrationError;
	}
	public void setIntegrationError(IntegrationError integrationError) {
		this.integrationError = integrationError;
	}
	public Tracciamento getTracciamento() {
		return this.tracciamento;
	}
	public void setTracciamento(Tracciamento tracciamento) {
		this.tracciamento = tracciamento;
	}
	public boolean isErroreProcessamento() {
		return this.isErroreProcessamento;
	}
	public void setErroreProcessamento(boolean isErroreProcessamento) {
		this.isErroreProcessamento = isErroreProcessamento;
	}
	public List<Eccezione> getError() {
		return this.error;
	}
	public void setError(List<Eccezione> error) {
		this.error = error;
	}
	public FlowProperties getFlowPropertiesResponse() {
		return this.flowPropertiesResponse;
	}
	public void setFlowPropertiesResponse(FlowProperties flowPropertiesResponse) {
		this.flowPropertiesResponse = flowPropertiesResponse;
	}
	public MessageSecurityContext getMessageSecurityContext() {
		return this.messageSecurityContext;
	}
	public void setMessageSecurityContext(MessageSecurityContext messageSecurityContext) {
		this.messageSecurityContext = messageSecurityContext;
	}
	public Exception getEccezioneProcessamento() {
		return this.eccezioneProcessamento;
	}
	public void setEccezioneProcessamento(Exception eccezioneProcessamento) {
		this.eccezioneProcessamento = eccezioneProcessamento;
	}
	public Integrazione getIntegrazione() {
		return this.integrazione;
	}
	public void setIntegrazione(Integrazione integrazione) {
		this.integrazione = integrazione;
	}
	public ErroreCooperazione getErroreCooperazione() {
		return this.erroreCooperazione;
	}
	public void setErroreCooperazione(
			ErroreCooperazione msgErroreCooperazione) {
		this.erroreCooperazione = msgErroreCooperazione;
	}
	public ErroreIntegrazione getErroreIntegrazione() {
		return this.erroreIntegrazione;
	}
	public void setErroreIntegrazione(
			ErroreIntegrazione msgErroreIntegrazione) {
		this.erroreIntegrazione = msgErroreIntegrazione;
	}
	public ParseException getParseException() {
		return this.parseException;
	}
	public void setParseException(ParseException parseException) {
		this.parseException = parseException;
	}
}
