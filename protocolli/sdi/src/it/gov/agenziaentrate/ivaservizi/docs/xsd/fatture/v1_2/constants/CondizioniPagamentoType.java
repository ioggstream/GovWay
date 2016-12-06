/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.constants;

import java.io.Serializable;
import java.util.List;

import org.openspcoop2.generic_project.beans.IEnumeration;

/**     
 * Enumeration dell'elemento CondizioniPagamentoType xsd (tipo:string) 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@javax.xml.bind.annotation.XmlType(name = "CondizioniPagamentoType")
@javax.xml.bind.annotation.XmlEnum(String.class)
public enum CondizioniPagamentoType implements IEnumeration , Serializable , Cloneable {

	@javax.xml.bind.annotation.XmlEnumValue("TP01")
	TP01 ("TP01"),
	@javax.xml.bind.annotation.XmlEnumValue("TP02")
	TP02 ("TP02"),
	@javax.xml.bind.annotation.XmlEnumValue("TP03")
	TP03 ("TP03");
	
	
	/** Value */
	private String value;
	@Override
	public String getValue()
	{
		return this.value;
	}


	/** Official Constructor */
	CondizioniPagamentoType(String value)
	{
		this.value = value;
	}


	
	@Override
	public String toString(){
		return this.value;
	}
	public boolean equals(CondizioniPagamentoType object){
		if(object==null)
			return false;
		if(object.getValue()==null)
			return false;
		return object.getValue().equals(this.getValue());	
	}
	public boolean equals(String object){
		if(object==null)
			return false;
		return object.equals(this.getValue());	
	}
	
		
	
	/** compatibility with the generated bean (reflection) */
	public boolean equals(Object object,List<String> fieldsNotCheck){
		if( !(object instanceof CondizioniPagamentoType) ){
			throw new RuntimeException("Wrong type: "+object.getClass().getName());
		}
		return this.equals(((CondizioniPagamentoType)object));
	}
	public String toString(boolean reportHTML){
		return toString();
	}
  	public String toString(boolean reportHTML,List<String> fieldsNotIncluded){
  		return toString();
  	}
  	public String diff(Object object,StringBuffer bf,boolean reportHTML){
		return bf.toString();
	}
	public String diff(Object object,StringBuffer bf,boolean reportHTML,List<String> fieldsNotIncluded){
		return bf.toString();
	}
	
	
	/** Utilities */
	
	public static String[] toArray(){
		String[] res = new String[values().length];
		int i=0;
		for (CondizioniPagamentoType tmp : values()) {
			res[i]=tmp.getValue();
			i++;
		}
		return res;
	}	
	public static String[] toStringArray(){
		String[] res = new String[values().length];
		int i=0;
		for (CondizioniPagamentoType tmp : values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[values().length];
		int i=0;
		for (CondizioniPagamentoType tmp : values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}
	
	public static boolean contains(String value){
		return toEnumConstant(value)!=null;
	}
	
	public static CondizioniPagamentoType toEnumConstant(String value){
		CondizioniPagamentoType res = null;
		if(CondizioniPagamentoType.TP01.getValue().equals(value)){
			res = CondizioniPagamentoType.TP01;
		}else if(CondizioniPagamentoType.TP02.getValue().equals(value)){
			res = CondizioniPagamentoType.TP02;
		}else if(CondizioniPagamentoType.TP03.getValue().equals(value)){
			res = CondizioniPagamentoType.TP03;
		}
		return res;
	}
	
	public static IEnumeration toEnumConstantFromString(String value){
		CondizioniPagamentoType res = null;
		if(CondizioniPagamentoType.TP01.toString().equals(value)){
			res = CondizioniPagamentoType.TP01;
		}else if(CondizioniPagamentoType.TP02.toString().equals(value)){
			res = CondizioniPagamentoType.TP02;
		}else if(CondizioniPagamentoType.TP03.toString().equals(value)){
			res = CondizioniPagamentoType.TP03;
		}
		return res;
	}
}
