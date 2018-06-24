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



package org.openspcoop2.core.registry.driver;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Permette il filtro di ricerca attraverso i driver che implementano l'interfaccia 'get'
 * 
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class FiltroRicerca implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Intervallo inferiore */
	private Date minDate;
	
	/** Intervallo superiore */
	private Date maxDate;
	
	/** Nome */
	private String nome;
	
	/** Tipo */
	private String tipo;
	
	/** ProtocolProperty */
	private List<FiltroRicercaProtocolProperty> protocolProperties = new ArrayList<FiltroRicercaProtocolProperty>();

	public List<FiltroRicercaProtocolProperty> getProtocolProperties() {
		return this.protocolProperties;
	}

	public void setProtocolProperties(
			List<FiltroRicercaProtocolProperty> list) {
		this.protocolProperties = list;
	}

	public void addProtocolProperty(FiltroRicercaProtocolProperty filtro){
		this.protocolProperties.add(filtro);
	}
	
	/**
	 * @return the maxDate
	 */
	public Date getMaxDate() {
		return this.maxDate;
	}

	/**
	 * @param maxDate the maxDate to set
	 */
	public void setMaxDate(Date maxDate) {
		this.maxDate = maxDate;
	}

	/**
	 * @return the minDate
	 */
	public Date getMinDate() {
		return this.minDate;
	}

	/**
	 * @param minDate the minDate to set
	 */
	public void setMinDate(Date minDate) {
		this.minDate = minDate;
	}

	/**
	 * @return the nome
	 */
	public String getNome() {
		return this.nome;
	}

	/**
	 * @param nome the nome to set
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	/**
	 * @return the tipo
	 */
	public String getTipo() {
		return this.tipo;
	}

	/**
	 * @param tipo the tipo to set
	 */
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	
	@Override
	public String toString(){
		return this.toString(true);
	}
	public String toString(boolean checkEmpty){
		StringBuffer bf = new StringBuffer();
		bf.append("Filtro:");
		this.addDetails(bf);
		if(checkEmpty){
			if(bf.length()=="Filtro:".length()){
				bf.append(" nessun filtro presente");
			}
		}
		return bf.toString();
	}
	public void addDetails(StringBuffer bf){
		if(this.minDate!=null)
			bf.append(" [intervallo-inferiore-data:"+this.minDate+"]");
		if(this.maxDate!=null)
			bf.append(" [intervallo-superiore-data:"+this.maxDate+"]");
		if(this.tipo!=null)
			bf.append(" [tipo:"+this.tipo+"]");
		if(this.nome!=null)
			bf.append(" [nome:"+this.nome+"]");
		if(this.protocolProperties!=null && this.protocolProperties.size()>0){
			bf.append(" [protocol-properties:"+this.protocolProperties.size()+"]");
			for (int i = 0; i < this.protocolProperties.size(); i++) {
				bf.append(" [protocol-properties["+i+"]:");
				this.protocolProperties.get(i).addDetails(bf);
				bf.append("]");
			}
		}
	}
}
