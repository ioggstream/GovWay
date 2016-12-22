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
package org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for Schema complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Schema">
 * 		&lt;attribute name="location" type="{http://www.w3.org/2001/XMLSchema}anyURI" use="required"/>
 * 		&lt;attribute name="version" type="{http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/}string" use="optional"/>
 * 		&lt;attribute name="namespace" type="{http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/}string" use="optional"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Schema")

@XmlRootElement(name = "Schema")

public class Schema extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Schema() {
  }

  public java.net.URI getLocation() {
    return this.location;
  }

  public void setLocation(java.net.URI location) {
    this.location = location;
  }

  public java.lang.String getVersion() {
    return this.version;
  }

  public void setVersion(java.lang.String version) {
    this.version = version;
  }

  public java.lang.String getNamespace() {
    return this.namespace;
  }

  public void setNamespace(java.lang.String namespace) {
    this.namespace = namespace;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlSchemaType(name="anyURI")
  @XmlAttribute(name="location",required=true)
  protected java.net.URI location;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="version",required=false)
  protected java.lang.String version;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="namespace",required=false)
  protected java.lang.String namespace;

}
