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
package org.openspcoop2.core.commons.search;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for porta-applicativa-servizio-applicativo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="porta-applicativa-servizio-applicativo">
 * 		&lt;sequence>
 * 			&lt;element name="id-servizio-applicativo" type="{http://www.openspcoop2.org/core/commons/search}id-servizio-applicativo" minOccurs="1" maxOccurs="1"/>
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
@XmlType(name = "porta-applicativa-servizio-applicativo", 
  propOrder = {
  	"idServizioApplicativo"
  }
)

@XmlRootElement(name = "porta-applicativa-servizio-applicativo")

public class PortaApplicativaServizioApplicativo extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public PortaApplicativaServizioApplicativo() {
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

  public IdServizioApplicativo getIdServizioApplicativo() {
    return this.idServizioApplicativo;
  }

  public void setIdServizioApplicativo(IdServizioApplicativo idServizioApplicativo) {
    this.idServizioApplicativo = idServizioApplicativo;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="id-servizio-applicativo",required=true,nillable=false)
  protected IdServizioApplicativo idServizioApplicativo;

}