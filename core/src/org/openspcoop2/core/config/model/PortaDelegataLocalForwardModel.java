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
package org.openspcoop2.core.config.model;

import org.openspcoop2.core.config.PortaDelegataLocalForward;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model PortaDelegataLocalForward 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PortaDelegataLocalForwardModel extends AbstractModel<PortaDelegataLocalForward> {

	public PortaDelegataLocalForwardModel(){
	
		super();
	
		this.STATO = new Field("stato",java.lang.String.class,"porta-delegata-local-forward",PortaDelegataLocalForward.class);
		this.PORTA_APPLICATIVA = new Field("porta-applicativa",java.lang.String.class,"porta-delegata-local-forward",PortaDelegataLocalForward.class);
	
	}
	
	public PortaDelegataLocalForwardModel(IField father){
	
		super(father);
	
		this.STATO = new ComplexField(father,"stato",java.lang.String.class,"porta-delegata-local-forward",PortaDelegataLocalForward.class);
		this.PORTA_APPLICATIVA = new ComplexField(father,"porta-applicativa",java.lang.String.class,"porta-delegata-local-forward",PortaDelegataLocalForward.class);
	
	}
	
	

	public IField STATO = null;
	 
	public IField PORTA_APPLICATIVA = null;
	 

	@Override
	public Class<PortaDelegataLocalForward> getModeledClass(){
		return PortaDelegataLocalForward.class;
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