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
package it.gov.spcoop.sica.manifest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for SpecificaInterfaccia complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SpecificaInterfaccia">
 * 		&lt;sequence>
 * 			&lt;element name="interfacciaConcettuale" type="{http://spcoop.gov.it/sica/manifest}DocumentoInterfaccia" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="interfacciaLogicaLatoErogatore" type="{http://spcoop.gov.it/sica/manifest}DocumentoInterfaccia" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="interfacciaLogicaLatoFruitore" type="{http://spcoop.gov.it/sica/manifest}DocumentoInterfaccia" minOccurs="0" maxOccurs="1"/>
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
@XmlType(name = "SpecificaInterfaccia", 
  propOrder = {
  	"interfacciaConcettuale",
  	"interfacciaLogicaLatoErogatore",
  	"interfacciaLogicaLatoFruitore"
  }
)

@XmlRootElement(name = "SpecificaInterfaccia")

public class SpecificaInterfaccia extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public SpecificaInterfaccia() {
  }

  public DocumentoInterfaccia getInterfacciaConcettuale() {
    return this.interfacciaConcettuale;
  }

  public void setInterfacciaConcettuale(DocumentoInterfaccia interfacciaConcettuale) {
    this.interfacciaConcettuale = interfacciaConcettuale;
  }

  public DocumentoInterfaccia getInterfacciaLogicaLatoErogatore() {
    return this.interfacciaLogicaLatoErogatore;
  }

  public void setInterfacciaLogicaLatoErogatore(DocumentoInterfaccia interfacciaLogicaLatoErogatore) {
    this.interfacciaLogicaLatoErogatore = interfacciaLogicaLatoErogatore;
  }

  public DocumentoInterfaccia getInterfacciaLogicaLatoFruitore() {
    return this.interfacciaLogicaLatoFruitore;
  }

  public void setInterfacciaLogicaLatoFruitore(DocumentoInterfaccia interfacciaLogicaLatoFruitore) {
    this.interfacciaLogicaLatoFruitore = interfacciaLogicaLatoFruitore;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="interfacciaConcettuale",required=true,nillable=false)
  protected DocumentoInterfaccia interfacciaConcettuale;

  @XmlElement(name="interfacciaLogicaLatoErogatore",required=true,nillable=false)
  protected DocumentoInterfaccia interfacciaLogicaLatoErogatore;

  @XmlElement(name="interfacciaLogicaLatoFruitore",required=false,nillable=false)
  protected DocumentoInterfaccia interfacciaLogicaLatoFruitore;

}
