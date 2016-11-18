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
package org.openspcoop2.protocol.manifest.model;

import org.openspcoop2.protocol.manifest.Versions;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Versions 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class VersionsModel extends AbstractModel<Versions> {

	public VersionsModel(){
	
		super();
	
		this.VERSION = new org.openspcoop2.protocol.manifest.model.VersionModel(new Field("version",org.openspcoop2.protocol.manifest.Version.class,"Versions",Versions.class));
	
	}
	
	public VersionsModel(IField father){
	
		super(father);
	
		this.VERSION = new org.openspcoop2.protocol.manifest.model.VersionModel(new ComplexField(father,"version",org.openspcoop2.protocol.manifest.Version.class,"Versions",Versions.class));
	
	}
	
	

	public org.openspcoop2.protocol.manifest.model.VersionModel VERSION = null;
	 

	@Override
	public Class<Versions> getModeledClass(){
		return Versions.class;
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