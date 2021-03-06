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


package org.openspcoop2.core.id;

import java.io.Serializable;


/**
 * Classe utilizzata per rappresentare un identificatore di una fruizione
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class IDFruizione implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected IDServizio idServizio;
	
	protected IDSoggetto idFruitore;
	
	
	public IDServizio getIdServizio() {
		return this.idServizio;
	}

	public void setIdServizio(IDServizio idServizio) {
		this.idServizio = idServizio;
	}

	public IDSoggetto getIdFruitore() {
		return this.idFruitore;
	}

	public void setIdFruitore(IDSoggetto idFruitore) {
		this.idFruitore = idFruitore;
	}

	
	@Override 
	public String toString(){
		StringBuffer bf = new StringBuffer();
		if(this.idServizio!=null)
			bf.append(" idServizio["+this.idServizio+"]");
		if(this.idFruitore!=null)
			bf.append(" idFruitore["+this.idFruitore+"]");
		return bf.toString();
	}
	
	@Override 
	public boolean equals(Object object){
		if(object == null)
			return false;
		if(object.getClass().getName().equals(this.getClass().getName()) == false)
			return false;
		IDFruizione id = (IDFruizione) object;
		
		if(this.idServizio==null){
			if(id.idServizio!=null)
				return false;
		}else{
			if(this.idServizio.equals(id.idServizio)==false)
				return false;
		}
		
		if(this.idFruitore==null){
			if(id.idFruitore!=null)
				return false;
		}else{
			if(this.idFruitore.equals(id.idFruitore)==false)
				return false;
		}
		
		return true;
	}
	
	// Utile per usare l'oggetto in hashtable come chiave
	@Override
	public int hashCode(){
		return this.toString().hashCode();
	}
	
	@Override
	public IDFruizione clone(){
		IDFruizione idFruizione = new IDFruizione();
		if(this.idServizio!=null){
			idFruizione.idServizio = this.idServizio.clone();
		}
		if(this.idFruitore!=null){
			idFruizione.idFruitore = this.idFruitore.clone();
		}
		return idFruizione;
	}
}


