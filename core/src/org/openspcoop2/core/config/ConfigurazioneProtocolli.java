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
package org.openspcoop2.core.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for configurazione-protocolli complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="configurazione-protocolli">
 * 		&lt;sequence>
 * 			&lt;element name="protocollo" type="{http://www.openspcoop2.org/core/config}configurazione-protocollo" minOccurs="1" maxOccurs="unbounded"/>
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
@XmlType(name = "configurazione-protocolli", 
  propOrder = {
  	"protocollo"
  }
)

@XmlRootElement(name = "configurazione-protocolli")

public class ConfigurazioneProtocolli extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ConfigurazioneProtocolli() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return Long.valueOf(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=Long.valueOf(-1);
  }

  public void addProtocollo(ConfigurazioneProtocollo protocollo) {
    this.protocollo.add(protocollo);
  }

  public ConfigurazioneProtocollo getProtocollo(int index) {
    return this.protocollo.get( index );
  }

  public ConfigurazioneProtocollo removeProtocollo(int index) {
    return this.protocollo.remove( index );
  }

  public List<ConfigurazioneProtocollo> getProtocolloList() {
    return this.protocollo;
  }

  public void setProtocolloList(List<ConfigurazioneProtocollo> protocollo) {
    this.protocollo=protocollo;
  }

  public int sizeProtocolloList() {
    return this.protocollo.size();
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="protocollo",required=true,nillable=false)
  protected List<ConfigurazioneProtocollo> protocollo = new ArrayList<ConfigurazioneProtocollo>();

  /**
   * @deprecated Use method getProtocolloList
   * @return List<ConfigurazioneProtocollo>
  */
  @Deprecated
  public List<ConfigurazioneProtocollo> getProtocollo() {
  	return this.protocollo;
  }

  /**
   * @deprecated Use method setProtocolloList
   * @param protocollo List<ConfigurazioneProtocollo>
  */
  @Deprecated
  public void setProtocollo(List<ConfigurazioneProtocollo> protocollo) {
  	this.protocollo=protocollo;
  }

  /**
   * @deprecated Use method sizeProtocolloList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeProtocollo() {
  	return this.protocollo.size();
  }

}
