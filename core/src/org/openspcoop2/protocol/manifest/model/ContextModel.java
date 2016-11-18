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

import org.openspcoop2.protocol.manifest.Context;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Context 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ContextModel extends AbstractModel<Context> {

	public ContextModel(){
	
		super();
	
		this.SUB_CONTEXT = new org.openspcoop2.protocol.manifest.model.SubContextMappingModel(new Field("subContext",org.openspcoop2.protocol.manifest.SubContextMapping.class,"Context",Context.class));
		this.EMPTY_SUB_CONTEXT = new org.openspcoop2.protocol.manifest.model.EmptySubContextMappingModel(new Field("emptySubContext",org.openspcoop2.protocol.manifest.EmptySubContextMapping.class,"Context",Context.class));
		this.SOAP_MEDIA_TYPE_COLLECTION = new org.openspcoop2.protocol.manifest.model.SoapMediaTypeCollectionModel(new Field("soapMediaTypeCollection",org.openspcoop2.protocol.manifest.SoapMediaTypeCollection.class,"Context",Context.class));
		this.REST_MEDIA_TYPE_COLLECTION = new org.openspcoop2.protocol.manifest.model.RestMediaTypeCollectionModel(new Field("restMediaTypeCollection",org.openspcoop2.protocol.manifest.RestMediaTypeCollection.class,"Context",Context.class));
		this.NAME = new Field("name",java.lang.String.class,"Context",Context.class);
		this.BINDING = new Field("binding",java.lang.String.class,"Context",Context.class);
	
	}
	
	public ContextModel(IField father){
	
		super(father);
	
		this.SUB_CONTEXT = new org.openspcoop2.protocol.manifest.model.SubContextMappingModel(new ComplexField(father,"subContext",org.openspcoop2.protocol.manifest.SubContextMapping.class,"Context",Context.class));
		this.EMPTY_SUB_CONTEXT = new org.openspcoop2.protocol.manifest.model.EmptySubContextMappingModel(new ComplexField(father,"emptySubContext",org.openspcoop2.protocol.manifest.EmptySubContextMapping.class,"Context",Context.class));
		this.SOAP_MEDIA_TYPE_COLLECTION = new org.openspcoop2.protocol.manifest.model.SoapMediaTypeCollectionModel(new ComplexField(father,"soapMediaTypeCollection",org.openspcoop2.protocol.manifest.SoapMediaTypeCollection.class,"Context",Context.class));
		this.REST_MEDIA_TYPE_COLLECTION = new org.openspcoop2.protocol.manifest.model.RestMediaTypeCollectionModel(new ComplexField(father,"restMediaTypeCollection",org.openspcoop2.protocol.manifest.RestMediaTypeCollection.class,"Context",Context.class));
		this.NAME = new ComplexField(father,"name",java.lang.String.class,"Context",Context.class);
		this.BINDING = new ComplexField(father,"binding",java.lang.String.class,"Context",Context.class);
	
	}
	
	

	public org.openspcoop2.protocol.manifest.model.SubContextMappingModel SUB_CONTEXT = null;
	 
	public org.openspcoop2.protocol.manifest.model.EmptySubContextMappingModel EMPTY_SUB_CONTEXT = null;
	 
	public org.openspcoop2.protocol.manifest.model.SoapMediaTypeCollectionModel SOAP_MEDIA_TYPE_COLLECTION = null;
	 
	public org.openspcoop2.protocol.manifest.model.RestMediaTypeCollectionModel REST_MEDIA_TYPE_COLLECTION = null;
	 
	public IField NAME = null;
	 
	public IField BINDING = null;
	 

	@Override
	public Class<Context> getModeledClass(){
		return Context.class;
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