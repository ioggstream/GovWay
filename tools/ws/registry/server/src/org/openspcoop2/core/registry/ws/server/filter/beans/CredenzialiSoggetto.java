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
package org.openspcoop2.core.registry.ws.server.filter.beans;

/**
 * <p>Java class for CredenzialiSoggetto complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="credenziali-soggetto">
 *     &lt;sequence>
 *         &lt;element name="tipo" type="{http://www.openspcoop2.org/core/registry}CredenzialeTipo" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="user" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="subject" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *     &lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;
import org.openspcoop2.core.registry.constants.CredenzialeTipo;

/**     
 * CredenzialiSoggetto
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "credenziali-soggetto", namespace="http://www.openspcoop2.org/core/registry/management", propOrder = {
    "tipo",
    "user",
    "password",
    "subject"
})
@javax.xml.bind.annotation.XmlRootElement(name = "credenziali-soggetto")
public class CredenzialiSoggetto extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@XmlElement(name="tipo",required=false,nillable=false)
	private CredenzialeTipo tipo;
	
	public void setTipo(CredenzialeTipo tipo){
		this.tipo = tipo;
	}
	
	public CredenzialeTipo getTipo(){
		return this.tipo;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="user",required=false,nillable=false)
	private String user;
	
	public void setUser(String user){
		this.user = user;
	}
	
	public String getUser(){
		return this.user;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="password",required=false,nillable=false)
	private String password;
	
	public void setPassword(String password){
		this.password = password;
	}
	
	public String getPassword(){
		return this.password;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="subject",required=false,nillable=false)
	private String subject;
	
	public void setSubject(String subject){
		this.subject = subject;
	}
	
	public String getSubject(){
		return this.subject;
	}
	
	
	
	
}