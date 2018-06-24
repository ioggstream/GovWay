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
package it.gov.fatturapa.sdi.fatturapa.v1_1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for FatturaPrincipaleType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FatturaPrincipaleType">
 * 		&lt;sequence>
 * 			&lt;element name="NumeroFatturaPrincipale" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}normalizedString" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="DataFatturaPrincipale" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="1" maxOccurs="1"/>
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
@XmlType(name = "FatturaPrincipaleType", 
  propOrder = {
  	"numeroFatturaPrincipale",
  	"dataFatturaPrincipale"
  }
)

@XmlRootElement(name = "FatturaPrincipaleType")

public class FatturaPrincipaleType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public FatturaPrincipaleType() {
  }

  public java.lang.String getNumeroFatturaPrincipale() {
    return this.numeroFatturaPrincipale;
  }

  public void setNumeroFatturaPrincipale(java.lang.String numeroFatturaPrincipale) {
    this.numeroFatturaPrincipale = numeroFatturaPrincipale;
  }

  public java.util.Date getDataFatturaPrincipale() {
    return this.dataFatturaPrincipale;
  }

  public void setDataFatturaPrincipale(java.util.Date dataFatturaPrincipale) {
    this.dataFatturaPrincipale = dataFatturaPrincipale;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="NumeroFatturaPrincipale",required=true,nillable=false)
  protected java.lang.String numeroFatturaPrincipale;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Date2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="date")
  @XmlElement(name="DataFatturaPrincipale",required=true,nillable=false,type=java.lang.String.class)
  protected java.util.Date dataFatturaPrincipale;

}
