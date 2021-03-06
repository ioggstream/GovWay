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

package org.openspcoop2.core.controllo_traffico.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;

/**
 * DatiTransazione 
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatiTransazione extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private TipoPdD tipoPdD;
	private String nomePorta;
	private IDSoggetto dominio;
	private String modulo;
	private String idTransazione;
	
	private String protocollo;
	
	private IDSoggetto soggettoFruitore;
	private IDServizio idServizio;
	
	private String servizioApplicativoFruitore;
	private List<String> listServiziApplicativiErogatori = new ArrayList<String>();
	
	public String getServiziApplicativiErogatoreAsString(){
		StringBuffer bf = new StringBuffer();
		if(this.listServiziApplicativiErogatori==null || this.listServiziApplicativiErogatori.size()<=0){
			return null;
		}
		for (int i = 0; i < this.listServiziApplicativiErogatori.size(); i++) {
			if(bf.length()>0){
				bf.append(",");
			}
			bf.append(this.listServiziApplicativiErogatori.get(i));
		}
		return bf.toString();
	}
	
	public TipoPdD getTipoPdD() {
		return this.tipoPdD;
	}
	public void setTipoPdD(TipoPdD tipoPdD) {
		this.tipoPdD = tipoPdD;
	}
	public String getProtocollo() {
		return this.protocollo;
	}
	public void setProtocollo(String protocollo) {
		this.protocollo = protocollo;
	}
	public IDSoggetto getSoggettoFruitore() {
		return this.soggettoFruitore;
	}
	public void setSoggettoFruitore(IDSoggetto soggettoFruitore) {
		this.soggettoFruitore = soggettoFruitore;
	}
	public IDServizio getIdServizio() {
		return this.idServizio;
	}
	public void setIdServizio(IDServizio idServizio) {
		this.idServizio = idServizio;
	}
	public String getServizioApplicativoFruitore() {
		return this.servizioApplicativoFruitore;
	}
	public void setServizioApplicativoFruitore(String servizioApplicativoFruitore) {
		this.servizioApplicativoFruitore = servizioApplicativoFruitore;
	}
	public List<String> getListServiziApplicativiErogatori() {
		return this.listServiziApplicativiErogatori;
	}
	public void setListServiziApplicativiErogatori(List<String> listServiziApplicativiErogatori) {
		this.listServiziApplicativiErogatori = listServiziApplicativiErogatori;
	}
	public IDSoggetto getDominio() {
		return this.dominio;
	}

	public void setDominio(IDSoggetto dominio) {
		this.dominio = dominio;
	}

	public String getModulo() {
		return this.modulo;
	}

	public void setModulo(String modulo) {
		this.modulo = modulo;
	}

	public String getIdTransazione() {
		return this.idTransazione;
	}

	public void setIdTransazione(String idTransazione) {
		this.idTransazione = idTransazione;
	}
	public String getNomePorta() {
		return this.nomePorta;
	}

	public void setNomePorta(String nomePorta) {
		this.nomePorta = nomePorta;
	}
}
