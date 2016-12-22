/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
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
package backend.ecodex.org._1_1.model;

import backend.ecodex.org._1_1.ErrorResultImplArray;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ErrorResultImplArray 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ErrorResultImplArrayModel extends AbstractModel<ErrorResultImplArray> {

	public ErrorResultImplArrayModel(){
	
		super();
	
		this.ITEM = new backend.ecodex.org._1_1.model.ErrorResultImplModel(new Field("item",backend.ecodex.org._1_1.ErrorResultImpl.class,"errorResultImplArray",ErrorResultImplArray.class));
	
	}
	
	public ErrorResultImplArrayModel(IField father){
	
		super(father);
	
		this.ITEM = new backend.ecodex.org._1_1.model.ErrorResultImplModel(new ComplexField(father,"item",backend.ecodex.org._1_1.ErrorResultImpl.class,"errorResultImplArray",ErrorResultImplArray.class));
	
	}
	
	

	public backend.ecodex.org._1_1.model.ErrorResultImplModel ITEM = null;
	 

	@Override
	public Class<ErrorResultImplArray> getModeledClass(){
		return ErrorResultImplArray.class;
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