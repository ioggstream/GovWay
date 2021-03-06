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
package org.openspcoop2.pdd.core;

/**
 * MessaggioServizioApplicativo
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MessaggioServizioApplicativo {

	private String idMessaggio;
	private String servizioApplicativo;
	private boolean sbustamentoSoap;
	private boolean sbustamentoInformazioniProtocollo;
	private String nomePorta;
	
	public String getNomePorta() {
		return this.nomePorta;
	}
	public void setNomePorta(String nomePorta) {
		this.nomePorta = nomePorta;
	}
	public String getIdMessaggio() {
		return this.idMessaggio;
	}
	public void setIdMessaggio(String idMessaggio) {
		this.idMessaggio = idMessaggio;
	}
	public String getServizioApplicativo() {
		return this.servizioApplicativo;
	}
	public void setServizioApplicativo(String servizioApplicativo) {
		this.servizioApplicativo = servizioApplicativo;
	}
	public boolean isSbustamentoSoap() {
		return this.sbustamentoSoap;
	}
	public void setSbustamentoSoap(boolean sbustamentoSoap) {
		this.sbustamentoSoap = sbustamentoSoap;
	}
	public boolean isSbustamentoInformazioniProtocollo() {
		return this.sbustamentoInformazioniProtocollo;
	}
	public void setSbustamentoInformazioniProtocollo(
			boolean sbustamentoInformazioniProtocollo) {
		this.sbustamentoInformazioniProtocollo = sbustamentoInformazioniProtocollo;
	}
}
