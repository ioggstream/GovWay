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
package it.gov.spcoop.sica.wsbl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for transitionsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="transitionsType">
 * 		&lt;sequence>
 * 			&lt;element name="transition" type="{http://spcoop.gov.it/sica/wsbl}transitionType" minOccurs="2" maxOccurs="unbounded"/>
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
@XmlType(name = "transitionsType", 
  propOrder = {
  	"transition"
  }
)

@XmlRootElement(name = "transitionsType")

public class TransitionsType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public TransitionsType() {
  }

  public void addTransition(TransitionType transition) {
    this.transition.add(transition);
  }

  public TransitionType getTransition(int index) {
    return this.transition.get( index );
  }

  public TransitionType removeTransition(int index) {
    return this.transition.remove( index );
  }

  public List<TransitionType> getTransitionList() {
    return this.transition;
  }

  public void setTransitionList(List<TransitionType> transition) {
    this.transition=transition;
  }

  public int sizeTransitionList() {
    return this.transition.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="transition",required=true,nillable=false)
  protected List<TransitionType> transition = new ArrayList<TransitionType>();

  /**
   * @deprecated Use method getTransitionList
   * @return List<TransitionType>
  */
  @Deprecated
  public List<TransitionType> getTransition() {
  	return this.transition;
  }

  /**
   * @deprecated Use method setTransitionList
   * @param transition List<TransitionType>
  */
  @Deprecated
  public void setTransition(List<TransitionType> transition) {
  	this.transition=transition;
  }

  /**
   * @deprecated Use method sizeTransitionList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeTransition() {
  	return this.transition.size();
  }

}
