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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.protocol.manifest.constants.DefaultIntegrationErrorMessageType;
import java.io.Serializable;


/** <p>Java class for DefaultIntegrationError complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DefaultIntegrationError">
 * 		&lt;attribute name="httpReturnCode" type="{http://www.w3.org/2001/XMLSchema}int" use="required"/>
 * 		&lt;attribute name="messageType" type="{http://www.openspcoop2.org/protocol/manifest}DefaultIntegrationErrorMessageType" use="required"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DefaultIntegrationError")

@XmlRootElement(name = "DefaultIntegrationError")

public class DefaultIntegrationError extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public DefaultIntegrationError() {
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

  public int getHttpReturnCode() {
    return this.httpReturnCode;
  }

  public void setHttpReturnCode(int httpReturnCode) {
    this.httpReturnCode = httpReturnCode;
  }

  public void set_value_messageType(String value) {
    this.messageType = (DefaultIntegrationErrorMessageType) DefaultIntegrationErrorMessageType.toEnumConstantFromString(value);
  }

  public String get_value_messageType() {
    if(this.messageType == null){
    	return null;
    }else{
    	return this.messageType.toString();
    }
  }

  public org.openspcoop2.protocol.manifest.constants.DefaultIntegrationErrorMessageType getMessageType() {
    return this.messageType;
  }

  public void setMessageType(org.openspcoop2.protocol.manifest.constants.DefaultIntegrationErrorMessageType messageType) {
    this.messageType = messageType;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlSchemaType(name="int")
  @XmlAttribute(name="httpReturnCode",required=true)
  protected int httpReturnCode;

  @XmlTransient
  protected java.lang.String _value_messageType;

  @XmlAttribute(name="messageType",required=true)
  protected DefaultIntegrationErrorMessageType messageType;

}
