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

package org.openspcoop2.protocol.sdk.registry;

import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;

/**
 *  FiltroRicercaResources
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 12566 $, $Date: 2017-01-11 15:21:56 +0100 (Wed, 11 Jan 2017) $
 */
public class FiltroRicercaRisorse extends FiltroRicercaAccordi {

	private String nomeRisorsa;
	private ProtocolProperties protocolPropertiesRisorsa;

	public String getNomeRisorsa() {
		return this.nomeRisorsa;
	}
	public void setNomeRisorsa(String nomeRisorsa) {
		this.nomeRisorsa = nomeRisorsa;
	}
	public ProtocolProperties getProtocolPropertiesRisorsa() {
		return this.protocolPropertiesRisorsa;
	}
	public void setProtocolPropertiesRisorsa(ProtocolProperties protocolPropertiesResources) {
		this.protocolPropertiesRisorsa = protocolPropertiesResources;
	}


}
