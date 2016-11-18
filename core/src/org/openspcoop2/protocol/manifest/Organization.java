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
package org.openspcoop2.protocol.manifest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for Organization complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Organization">
 * 		&lt;sequence>
 * 			&lt;element name="types" type="{http://www.openspcoop2.org/protocol/manifest}OrganizationTypes" minOccurs="1" maxOccurs="1"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="codeIPA" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/>
 * 		&lt;attribute name="replyToAddress" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Organization", 
  propOrder = {
  	"types"
  }
)

@XmlRootElement(name = "Organization")

public class Organization extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Organization() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return new Long(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=new Long(-1);
  }

  public OrganizationTypes getTypes() {
    return this.types;
  }

  public void setTypes(OrganizationTypes types) {
    this.types = types;
  }

  public boolean isCodeIPA() {
    return this.codeIPA;
  }

  public boolean getCodeIPA() {
    return this.codeIPA;
  }

  public void setCodeIPA(boolean codeIPA) {
    this.codeIPA = codeIPA;
  }

  public boolean isReplyToAddress() {
    return this.replyToAddress;
  }

  public boolean getReplyToAddress() {
    return this.replyToAddress;
  }

  public void setReplyToAddress(boolean replyToAddress) {
    this.replyToAddress = replyToAddress;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="types",required=true,nillable=false)
  protected OrganizationTypes types;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="codeIPA",required=false)
  protected boolean codeIPA = false;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="replyToAddress",required=false)
  protected boolean replyToAddress = false;

}