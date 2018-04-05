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
package org.openspcoop2.web.lib.mvc.properties.model;

import org.openspcoop2.web.lib.mvc.properties.Equals;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Equals 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EqualsModel extends AbstractModel<Equals> {

	public EqualsModel(){
	
		super();
	
		this.NAME = new Field("name",java.lang.String.class,"equals",Equals.class);
		this.VALUE = new Field("value",java.lang.String.class,"equals",Equals.class);
	
	}
	
	public EqualsModel(IField father){
	
		super(father);
	
		this.NAME = new ComplexField(father,"name",java.lang.String.class,"equals",Equals.class);
		this.VALUE = new ComplexField(father,"value",java.lang.String.class,"equals",Equals.class);
	
	}
	
	

	public IField NAME = null;
	 
	public IField VALUE = null;
	 

	@Override
	public Class<Equals> getModeledClass(){
		return Equals.class;
	}
	
	@Override
	public String toString(){
		if(this.getModeledClass()!=null){
			return this.getModeledClass().getName();
		}else{
			return "N.D.";
		}
	}

}