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
package org.openspcoop2.core.commons.search;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for servizio-applicativo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="servizio-applicativo">
 * 		&lt;sequence>
 * 			&lt;element name="nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="tipologia_fruizione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="tipologia_erogazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="id-soggetto" type="{http://www.openspcoop2.org/core/commons/search}id-soggetto" minOccurs="1" maxOccurs="1"/>
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
@XmlType(name = "servizio-applicativo", 
  propOrder = {
  	"nome",
  	"tipologiaFruizione",
  	"tipologiaErogazione",
  	"idSoggetto"
  }
)

@XmlRootElement(name = "servizio-applicativo")

public class ServizioApplicativo extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ServizioApplicativo() {
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

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public java.lang.String getTipologiaFruizione() {
    return this.tipologiaFruizione;
  }

  public void setTipologiaFruizione(java.lang.String tipologiaFruizione) {
    this.tipologiaFruizione = tipologiaFruizione;
  }

  public java.lang.String getTipologiaErogazione() {
    return this.tipologiaErogazione;
  }

  public void setTipologiaErogazione(java.lang.String tipologiaErogazione) {
    this.tipologiaErogazione = tipologiaErogazione;
  }

  public IdSoggetto getIdSoggetto() {
    return this.idSoggetto;
  }

  public void setIdSoggetto(IdSoggetto idSoggetto) {
    this.idSoggetto = idSoggetto;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;

  private static org.openspcoop2.core.commons.search.model.ServizioApplicativoModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.commons.search.ServizioApplicativo.modelStaticInstance==null){
  			org.openspcoop2.core.commons.search.ServizioApplicativo.modelStaticInstance = new org.openspcoop2.core.commons.search.model.ServizioApplicativoModel();
	  }
  }
  public static org.openspcoop2.core.commons.search.model.ServizioApplicativoModel model(){
	  if(org.openspcoop2.core.commons.search.ServizioApplicativo.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.commons.search.ServizioApplicativo.modelStaticInstance;
  }


  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome",required=true,nillable=false)
  protected java.lang.String nome;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipologia_fruizione",required=true,nillable=false)
  protected java.lang.String tipologiaFruizione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipologia_erogazione",required=true,nillable=false)
  protected java.lang.String tipologiaErogazione;

  @XmlElement(name="id-soggetto",required=true,nillable=false)
  protected IdSoggetto idSoggetto;

}
