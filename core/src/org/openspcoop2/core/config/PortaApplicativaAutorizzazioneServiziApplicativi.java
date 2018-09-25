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


/** <p>Java class for porta-applicativa-autorizzazione-servizi-applicativi complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="porta-applicativa-autorizzazione-servizi-applicativi">
 * 		&lt;sequence>
 * 			&lt;element name="soggetto" type="{http://www.openspcoop2.org/core/config}porta-applicativa-autorizzazione-servizio-applicativo" minOccurs="1" maxOccurs="unbounded"/>
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
@XmlType(name = "porta-applicativa-autorizzazione-servizi-applicativi", 
  propOrder = {
  	"soggetto"
  }
)

@XmlRootElement(name = "porta-applicativa-autorizzazione-servizi-applicativi")

public class PortaApplicativaAutorizzazioneServiziApplicativi extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public PortaApplicativaAutorizzazioneServiziApplicativi() {
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

  public void addSoggetto(PortaApplicativaAutorizzazioneServizioApplicativo soggetto) {
    this.soggetto.add(soggetto);
  }

  public PortaApplicativaAutorizzazioneServizioApplicativo getSoggetto(int index) {
    return this.soggetto.get( index );
  }

  public PortaApplicativaAutorizzazioneServizioApplicativo removeSoggetto(int index) {
    return this.soggetto.remove( index );
  }

  public List<PortaApplicativaAutorizzazioneServizioApplicativo> getSoggettoList() {
    return this.soggetto;
  }

  public void setSoggettoList(List<PortaApplicativaAutorizzazioneServizioApplicativo> soggetto) {
    this.soggetto=soggetto;
  }

  public int sizeSoggettoList() {
    return this.soggetto.size();
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="soggetto",required=true,nillable=false)
  protected List<PortaApplicativaAutorizzazioneServizioApplicativo> soggetto = new ArrayList<PortaApplicativaAutorizzazioneServizioApplicativo>();

  /**
   * @deprecated Use method getSoggettoList
   * @return List<PortaApplicativaAutorizzazioneServizioApplicativo>
  */
  @Deprecated
  public List<PortaApplicativaAutorizzazioneServizioApplicativo> getSoggetto() {
  	return this.soggetto;
  }

  /**
   * @deprecated Use method setSoggettoList
   * @param soggetto List<PortaApplicativaAutorizzazioneServizioApplicativo>
  */
  @Deprecated
  public void setSoggetto(List<PortaApplicativaAutorizzazioneServizioApplicativo> soggetto) {
  	this.soggetto=soggetto;
  }

  /**
   * @deprecated Use method sizeSoggettoList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeSoggetto() {
  	return this.soggetto.size();
  }

}
