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
package it.cnipa.schemas._2003.egovit.exception1_0.model;

import it.cnipa.schemas._2003.egovit.exception1_0.Eccezione;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Eccezione 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EccezioneModel extends AbstractModel<Eccezione> {

	public EccezioneModel(){
	
		super();
	
		this.ECCEZIONE_BUSTA = new it.cnipa.schemas._2003.egovit.exception1_0.model.EccezioneBustaModel(new Field("EccezioneBusta",it.cnipa.schemas._2003.egovit.exception1_0.EccezioneBusta.class,"Eccezione",Eccezione.class));
		this.ECCEZIONE_PROCESSAMENTO = new it.cnipa.schemas._2003.egovit.exception1_0.model.EccezioneProcessamentoModel(new Field("EccezioneProcessamento",it.cnipa.schemas._2003.egovit.exception1_0.EccezioneProcessamento.class,"Eccezione",Eccezione.class));
	
	}
	
	public EccezioneModel(IField father){
	
		super(father);
	
		this.ECCEZIONE_BUSTA = new it.cnipa.schemas._2003.egovit.exception1_0.model.EccezioneBustaModel(new ComplexField(father,"EccezioneBusta",it.cnipa.schemas._2003.egovit.exception1_0.EccezioneBusta.class,"Eccezione",Eccezione.class));
		this.ECCEZIONE_PROCESSAMENTO = new it.cnipa.schemas._2003.egovit.exception1_0.model.EccezioneProcessamentoModel(new ComplexField(father,"EccezioneProcessamento",it.cnipa.schemas._2003.egovit.exception1_0.EccezioneProcessamento.class,"Eccezione",Eccezione.class));
	
	}
	
	

	public it.cnipa.schemas._2003.egovit.exception1_0.model.EccezioneBustaModel ECCEZIONE_BUSTA = null;
	 
	public it.cnipa.schemas._2003.egovit.exception1_0.model.EccezioneProcessamentoModel ECCEZIONE_PROCESSAMENTO = null;
	 

	@Override
	public Class<Eccezione> getModeledClass(){
		return Eccezione.class;
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