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
package backend.ecodex.org._1_1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for sendRequestURL complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="sendRequestURL">
 * 		&lt;sequence>
 * 			&lt;element name="bodyload" type="{http://org.ecodex.backend/1_1/}PayloadURLType" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="payload" type="{http://org.ecodex.backend/1_1/}PayloadURLType" minOccurs="0" maxOccurs="unbounded"/>
 * 		&lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "sendRequestURL", 
  propOrder = {
  	"bodyload",
  	"payload"
  }
)

@XmlRootElement(name = "sendRequestURL")

public class SendRequestURL extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public SendRequestURL() {
  }

  public PayloadURLType getBodyload() {
    return this.bodyload;
  }

  public void setBodyload(PayloadURLType bodyload) {
    this.bodyload = bodyload;
  }

  public void addPayload(PayloadURLType payload) {
    this.payload.add(payload);
  }

  public PayloadURLType getPayload(int index) {
    return this.payload.get( index );
  }

  public PayloadURLType removePayload(int index) {
    return this.payload.remove( index );
  }

  public List<PayloadURLType> getPayloadList() {
    return this.payload;
  }

  public void setPayloadList(List<PayloadURLType> payload) {
    this.payload=payload;
  }

  public int sizePayloadList() {
    return this.payload.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="bodyload",required=false,nillable=false)
  protected PayloadURLType bodyload;

  @XmlElement(name="payload",required=true,nillable=false)
  protected List<PayloadURLType> payload = new ArrayList<PayloadURLType>();

  /**
   * @deprecated Use method getPayloadList
   * @return List<PayloadURLType>
  */
  @Deprecated
  public List<PayloadURLType> getPayload() {
  	return this.payload;
  }

  /**
   * @deprecated Use method setPayloadList
   * @param payload List<PayloadURLType>
  */
  @Deprecated
  public void setPayload(List<PayloadURLType> payload) {
  	this.payload=payload;
  }

  /**
   * @deprecated Use method sizePayloadList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizePayload() {
  	return this.payload.size();
  }

}
