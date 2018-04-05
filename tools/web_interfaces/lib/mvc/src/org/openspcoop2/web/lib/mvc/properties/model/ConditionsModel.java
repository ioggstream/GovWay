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

import org.openspcoop2.web.lib.mvc.properties.Conditions;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Conditions 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConditionsModel extends AbstractModel<Conditions> {

	public ConditionsModel(){
	
		super();
	
		this.CONDITION = new org.openspcoop2.web.lib.mvc.properties.model.ConditionModel(new Field("condition",org.openspcoop2.web.lib.mvc.properties.Condition.class,"conditions",Conditions.class));
		this.AND = new Field("and",boolean.class,"conditions",Conditions.class);
		this.NOT = new Field("not",boolean.class,"conditions",Conditions.class);
	
	}
	
	public ConditionsModel(IField father){
	
		super(father);
	
		this.CONDITION = new org.openspcoop2.web.lib.mvc.properties.model.ConditionModel(new ComplexField(father,"condition",org.openspcoop2.web.lib.mvc.properties.Condition.class,"conditions",Conditions.class));
		this.AND = new ComplexField(father,"and",boolean.class,"conditions",Conditions.class);
		this.NOT = new ComplexField(father,"not",boolean.class,"conditions",Conditions.class);
	
	}
	
	

	public org.openspcoop2.web.lib.mvc.properties.model.ConditionModel CONDITION = null;
	 
	public IField AND = null;
	 
	public IField NOT = null;
	 

	@Override
	public Class<Conditions> getModeledClass(){
		return Conditions.class;
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