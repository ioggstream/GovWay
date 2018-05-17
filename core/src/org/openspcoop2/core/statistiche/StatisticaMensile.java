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
package org.openspcoop2.core.statistiche;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for statistica-mensile complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="statistica-mensile">
 * 		&lt;sequence>
 * 			&lt;element name="statistica-base" type="{http://www.openspcoop2.org/core/statistiche}statistica" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="statistica-mensile-contenuti" type="{http://www.openspcoop2.org/core/statistiche}statistica-contenuti" minOccurs="0" maxOccurs="unbounded"/>
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
@XmlType(name = "statistica-mensile", 
  propOrder = {
  	"statisticaBase",
  	"statisticaMensileContenuti"
  }
)

@XmlRootElement(name = "statistica-mensile")

public class StatisticaMensile extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public StatisticaMensile() {
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

  public Statistica getStatisticaBase() {
    return this.statisticaBase;
  }

  public void setStatisticaBase(Statistica statisticaBase) {
    this.statisticaBase = statisticaBase;
  }

  public void addStatisticaMensileContenuti(StatisticaContenuti statisticaMensileContenuti) {
    this.statisticaMensileContenuti.add(statisticaMensileContenuti);
  }

  public StatisticaContenuti getStatisticaMensileContenuti(int index) {
    return this.statisticaMensileContenuti.get( index );
  }

  public StatisticaContenuti removeStatisticaMensileContenuti(int index) {
    return this.statisticaMensileContenuti.remove( index );
  }

  public List<StatisticaContenuti> getStatisticaMensileContenutiList() {
    return this.statisticaMensileContenuti;
  }

  public void setStatisticaMensileContenutiList(List<StatisticaContenuti> statisticaMensileContenuti) {
    this.statisticaMensileContenuti=statisticaMensileContenuti;
  }

  public int sizeStatisticaMensileContenutiList() {
    return this.statisticaMensileContenuti.size();
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;

  private static org.openspcoop2.core.statistiche.model.StatisticaMensileModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.statistiche.StatisticaMensile.modelStaticInstance==null){
  			org.openspcoop2.core.statistiche.StatisticaMensile.modelStaticInstance = new org.openspcoop2.core.statistiche.model.StatisticaMensileModel();
	  }
  }
  public static org.openspcoop2.core.statistiche.model.StatisticaMensileModel model(){
	  if(org.openspcoop2.core.statistiche.StatisticaMensile.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.statistiche.StatisticaMensile.modelStaticInstance;
  }


  @XmlElement(name="statistica-base",required=true,nillable=false)
  protected Statistica statisticaBase;

  @XmlElement(name="statistica-mensile-contenuti",required=true,nillable=false)
  protected List<StatisticaContenuti> statisticaMensileContenuti = new ArrayList<StatisticaContenuti>();

  /**
   * @deprecated Use method getStatisticaMensileContenutiList
   * @return List<StatisticaContenuti>
  */
  @Deprecated
  public List<StatisticaContenuti> getStatisticaMensileContenuti() {
  	return this.statisticaMensileContenuti;
  }

  /**
   * @deprecated Use method setStatisticaMensileContenutiList
   * @param statisticaMensileContenuti List<StatisticaContenuti>
  */
  @Deprecated
  public void setStatisticaMensileContenuti(List<StatisticaContenuti> statisticaMensileContenuti) {
  	this.statisticaMensileContenuti=statisticaMensileContenuti;
  }

  /**
   * @deprecated Use method sizeStatisticaMensileContenutiList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeStatisticaMensileContenuti() {
  	return this.statisticaMensileContenuti.size();
  }

}