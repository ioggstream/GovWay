/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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
package org.openspcoop2.core.registry.constants;

/**
 * PddTipologia
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 12564 $, $Date: 2017-01-11 14:31:31 +0100 (Wed, 11 Jan 2017) $
 */
public enum PddTipologia {

	ESTERNO("esterno"),OPERATIVO("operativo"),NONOPERATIVO("nonoperativo");
	
	private String value;
	PddTipologia(String value){
		this.value = value;
	}
	
	@Override
	public String toString(){
		return this.value;
	}
	
	public boolean equals(PddTipologia t){
		if(t==null){
			return false;
		}
		return t.toString().equals(this.toString());
	}
	
	public static PddTipologia toPddTipologia(String value){
		if(value==null){
			return null;
		}
		else if(OPERATIVO.toString().equals(value)){
			return OPERATIVO;
		}
		else if(NONOPERATIVO.toString().equals(value)){
			return NONOPERATIVO;
		}
		else if(ESTERNO.toString().equals(value)){
			return ESTERNO;
		}
		else{
			return null;
		}
	}
	
	public final static String[] TIPI = { OPERATIVO.toString(), NONOPERATIVO.toString(), ESTERNO.toString() };
	public final static String[] TIPI_SOLO_OPERATIVI = { OPERATIVO.toString(), NONOPERATIVO.toString() };
	public final static String[] TIPO_SOLO_ESTERNO = { ESTERNO.toString() };
}