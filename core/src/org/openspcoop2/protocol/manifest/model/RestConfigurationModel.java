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
package org.openspcoop2.protocol.manifest.model;

import org.openspcoop2.protocol.manifest.RestConfiguration;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model RestConfiguration 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RestConfigurationModel extends AbstractModel<RestConfiguration> {

	public RestConfigurationModel(){
	
		super();
	
		this.INTEGRATION_ERROR = new org.openspcoop2.protocol.manifest.model.IntegrationErrorConfigurationModel(new Field("integrationError",org.openspcoop2.protocol.manifest.IntegrationErrorConfiguration.class,"RestConfiguration",RestConfiguration.class));
		this.MEDIA_TYPE_COLLECTION = new org.openspcoop2.protocol.manifest.model.RestMediaTypeCollectionModel(new Field("mediaTypeCollection",org.openspcoop2.protocol.manifest.RestMediaTypeCollection.class,"RestConfiguration",RestConfiguration.class));
		this.INTERFACES = new org.openspcoop2.protocol.manifest.model.InterfacesConfigurationModel(new Field("interfaces",org.openspcoop2.protocol.manifest.InterfacesConfiguration.class,"RestConfiguration",RestConfiguration.class));
		this.XML = new Field("xml",boolean.class,"RestConfiguration",RestConfiguration.class);
		this.JSON = new Field("json",boolean.class,"RestConfiguration",RestConfiguration.class);
		this.BINARY = new Field("binary",boolean.class,"RestConfiguration",RestConfiguration.class);
		this.MIME_MULTIPART = new Field("mimeMultipart",boolean.class,"RestConfiguration",RestConfiguration.class);
	
	}
	
	public RestConfigurationModel(IField father){
	
		super(father);
	
		this.INTEGRATION_ERROR = new org.openspcoop2.protocol.manifest.model.IntegrationErrorConfigurationModel(new ComplexField(father,"integrationError",org.openspcoop2.protocol.manifest.IntegrationErrorConfiguration.class,"RestConfiguration",RestConfiguration.class));
		this.MEDIA_TYPE_COLLECTION = new org.openspcoop2.protocol.manifest.model.RestMediaTypeCollectionModel(new ComplexField(father,"mediaTypeCollection",org.openspcoop2.protocol.manifest.RestMediaTypeCollection.class,"RestConfiguration",RestConfiguration.class));
		this.INTERFACES = new org.openspcoop2.protocol.manifest.model.InterfacesConfigurationModel(new ComplexField(father,"interfaces",org.openspcoop2.protocol.manifest.InterfacesConfiguration.class,"RestConfiguration",RestConfiguration.class));
		this.XML = new ComplexField(father,"xml",boolean.class,"RestConfiguration",RestConfiguration.class);
		this.JSON = new ComplexField(father,"json",boolean.class,"RestConfiguration",RestConfiguration.class);
		this.BINARY = new ComplexField(father,"binary",boolean.class,"RestConfiguration",RestConfiguration.class);
		this.MIME_MULTIPART = new ComplexField(father,"mimeMultipart",boolean.class,"RestConfiguration",RestConfiguration.class);
	
	}
	
	

	public org.openspcoop2.protocol.manifest.model.IntegrationErrorConfigurationModel INTEGRATION_ERROR = null;
	 
	public org.openspcoop2.protocol.manifest.model.RestMediaTypeCollectionModel MEDIA_TYPE_COLLECTION = null;
	 
	public org.openspcoop2.protocol.manifest.model.InterfacesConfigurationModel INTERFACES = null;
	 
	public IField XML = null;
	 
	public IField JSON = null;
	 
	public IField BINARY = null;
	 
	public IField MIME_MULTIPART = null;
	 

	@Override
	public Class<RestConfiguration> getModeledClass(){
		return RestConfiguration.class;
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