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
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for SpecificaSicurezza complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SpecificaSicurezza">
 * 		&lt;sequence>
 * 			&lt;element name="documentoSicurezza" type="{http://spcoop.gov.it/sica/manifest}DocumentoSicurezza" minOccurs="1" maxOccurs="unbounded"/>
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
@XmlType(name = "SpecificaSicurezza", 
  propOrder = {
  	"documentoSicurezza"
  }
)

@XmlRootElement(name = "SpecificaSicurezza")

public class SpecificaSicurezza extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public SpecificaSicurezza() {
  }

  public void addDocumentoSicurezza(DocumentoSicurezza documentoSicurezza) {
    this.documentoSicurezza.add(documentoSicurezza);
  }

  public DocumentoSicurezza getDocumentoSicurezza(int index) {
    return this.documentoSicurezza.get( index );
  }

  public DocumentoSicurezza removeDocumentoSicurezza(int index) {
    return this.documentoSicurezza.remove( index );
  }

  public List<DocumentoSicurezza> getDocumentoSicurezzaList() {
    return this.documentoSicurezza;
  }

  public void setDocumentoSicurezzaList(List<DocumentoSicurezza> documentoSicurezza) {
    this.documentoSicurezza=documentoSicurezza;
  }

  public int sizeDocumentoSicurezzaList() {
    return this.documentoSicurezza.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="documentoSicurezza",required=true,nillable=false)
  protected List<DocumentoSicurezza> documentoSicurezza = new ArrayList<DocumentoSicurezza>();

  /**
   * @deprecated Use method getDocumentoSicurezzaList
   * @return List<DocumentoSicurezza>
  */
  @Deprecated
  public List<DocumentoSicurezza> getDocumentoSicurezza() {
  	return this.documentoSicurezza;
  }

  /**
   * @deprecated Use method setDocumentoSicurezzaList
   * @param documentoSicurezza List<DocumentoSicurezza>
  */
  @Deprecated
  public void setDocumentoSicurezza(List<DocumentoSicurezza> documentoSicurezza) {
  	this.documentoSicurezza=documentoSicurezza;
  }

  /**
   * @deprecated Use method sizeDocumentoSicurezzaList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeDocumentoSicurezza() {
  	return this.documentoSicurezza.size();
  }

}
