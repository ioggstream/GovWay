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


package org.openspcoop2.pdd.core.handlers;

import org.slf4j.Logger;
import org.openspcoop2.pdd.core.ProtocolContext;
import org.openspcoop2.pdd.core.IntegrationContext;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.state.IState;

/**
 * OutResponseContext
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OutResponseContext extends BaseContext {

	public OutResponseContext(Logger logger,IProtocolFactory<?> protocolFactory, IState state){
		super.setLogCore(logger);
		super.setProtocolFactory(protocolFactory);
		super.setStato(state);
	}
	
	/** Informazioni protocollo */
	private ProtocolContext protocollo;
	
	/** Informazioni di integrazione */
	private IntegrationContext integrazione;

	/** Proprieta' di trasporto della risposta */
	private java.util.Properties propertiesRispostaTrasporto;
	
	public ProtocolContext getProtocollo() {
		return this.protocollo;
	}

	public void setProtocollo(ProtocolContext p) {
		this.protocollo = p;
	}

	public IntegrationContext getIntegrazione() {
		return this.integrazione;
	}

	public void setIntegrazione(IntegrationContext integrazione) {
		this.integrazione = integrazione;
	}

	public java.util.Properties getPropertiesRispostaTrasporto() {
		return this.propertiesRispostaTrasporto;
	}

	public void setPropertiesRispostaTrasporto(
			java.util.Properties propertiesRispostaTrasporto) {
		this.propertiesRispostaTrasporto = propertiesRispostaTrasporto;
	}
}
