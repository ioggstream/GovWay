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
package it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2;

import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.constants.SocioUnicoType;
import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.constants.StatoLiquidazioneType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for IscrizioneREAType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IscrizioneREAType">
 * 		&lt;sequence>
 * 			&lt;element name="Ufficio" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}string" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="NumeroREA" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}normalizedString" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="CapitaleSociale" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}decimal" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="SocioUnico" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}SocioUnicoType" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="StatoLiquidazione" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}StatoLiquidazioneType" minOccurs="1" maxOccurs="1"/>
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
@XmlType(name = "IscrizioneREAType", 
  propOrder = {
  	"ufficio",
  	"numeroREA",
  	"_decimalWrapper_capitaleSociale",
  	"socioUnico",
  	"statoLiquidazione"
  }
)

@XmlRootElement(name = "IscrizioneREAType")

public class IscrizioneREAType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public IscrizioneREAType() {
  }

  public java.lang.String getUfficio() {
    return this.ufficio;
  }

  public void setUfficio(java.lang.String ufficio) {
    this.ufficio = ufficio;
  }

  public java.lang.String getNumeroREA() {
    return this.numeroREA;
  }

  public void setNumeroREA(java.lang.String numeroREA) {
    this.numeroREA = numeroREA;
  }

  public java.lang.Double getCapitaleSociale() {
    if(this._decimalWrapper_capitaleSociale!=null){
		return (java.lang.Double) this._decimalWrapper_capitaleSociale.getObject(java.lang.Double.class);
	}else{
		return this.capitaleSociale;
	}
  }

  public void setCapitaleSociale(java.lang.Double capitaleSociale) {
    if(capitaleSociale!=null){
		this._decimalWrapper_capitaleSociale = new org.openspcoop2.utils.jaxb.DecimalWrapper(1,11,2,2,capitaleSociale);
	}
  }

  public void set_value_socioUnico(String value) {
    this.socioUnico = (SocioUnicoType) SocioUnicoType.toEnumConstantFromString(value);
  }

  public String get_value_socioUnico() {
    if(this.socioUnico == null){
    	return null;
    }else{
    	return this.socioUnico.toString();
    }
  }

  public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.constants.SocioUnicoType getSocioUnico() {
    return this.socioUnico;
  }

  public void setSocioUnico(it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.constants.SocioUnicoType socioUnico) {
    this.socioUnico = socioUnico;
  }

  public void set_value_statoLiquidazione(String value) {
    this.statoLiquidazione = (StatoLiquidazioneType) StatoLiquidazioneType.toEnumConstantFromString(value);
  }

  public String get_value_statoLiquidazione() {
    if(this.statoLiquidazione == null){
    	return null;
    }else{
    	return this.statoLiquidazione.toString();
    }
  }

  public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.constants.StatoLiquidazioneType getStatoLiquidazione() {
    return this.statoLiquidazione;
  }

  public void setStatoLiquidazione(it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.constants.StatoLiquidazioneType statoLiquidazione) {
    this.statoLiquidazione = statoLiquidazione;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="Ufficio",required=true,nillable=false)
  protected java.lang.String ufficio;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="NumeroREA",required=true,nillable=false)
  protected java.lang.String numeroREA;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="CapitaleSociale",required=false,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_capitaleSociale = null;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.Double capitaleSociale;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_socioUnico;

  @XmlElement(name="SocioUnico",required=false,nillable=false)
  protected SocioUnicoType socioUnico;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_statoLiquidazione;

  @XmlElement(name="StatoLiquidazione",required=true,nillable=false)
  protected StatoLiquidazioneType statoLiquidazione;

}
