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
package backend.ecodex.org._1_1.model;

import backend.ecodex.org._1_1.SendRequestURL;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model SendRequestURL 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SendRequestURLModel extends AbstractModel<SendRequestURL> {

	public SendRequestURLModel(){
	
		super();
	
		this.BODYLOAD = new backend.ecodex.org._1_1.model.PayloadURLTypeModel(new Field("bodyload",backend.ecodex.org._1_1.PayloadURLType.class,"sendRequestURL",SendRequestURL.class));
		this.PAYLOAD = new backend.ecodex.org._1_1.model.PayloadURLTypeModel(new Field("payload",backend.ecodex.org._1_1.PayloadURLType.class,"sendRequestURL",SendRequestURL.class));
	
	}
	
	public SendRequestURLModel(IField father){
	
		super(father);
	
		this.BODYLOAD = new backend.ecodex.org._1_1.model.PayloadURLTypeModel(new ComplexField(father,"bodyload",backend.ecodex.org._1_1.PayloadURLType.class,"sendRequestURL",SendRequestURL.class));
		this.PAYLOAD = new backend.ecodex.org._1_1.model.PayloadURLTypeModel(new ComplexField(father,"payload",backend.ecodex.org._1_1.PayloadURLType.class,"sendRequestURL",SendRequestURL.class));
	
	}
	
	

	public backend.ecodex.org._1_1.model.PayloadURLTypeModel BODYLOAD = null;
	 
	public backend.ecodex.org._1_1.model.PayloadURLTypeModel PAYLOAD = null;
	 

	@Override
	public Class<SendRequestURL> getModeledClass(){
		return SendRequestURL.class;
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