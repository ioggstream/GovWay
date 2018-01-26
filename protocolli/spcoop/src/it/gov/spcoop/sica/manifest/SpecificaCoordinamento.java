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
package it.gov.spcoop.sica.manifest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class SpecificaCoordinamento.
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 */

public class SpecificaCoordinamento extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  private Long id;


  public SpecificaCoordinamento() {
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

  public void addDocumentoCoordinamento(DocumentoCoordinamento documentoCoordinamento) {
    this.documentoCoordinamento.add(documentoCoordinamento);
  }

  public DocumentoCoordinamento getDocumentoCoordinamento(int index) {
    return this.documentoCoordinamento.get( index );
  }

  public DocumentoCoordinamento removeDocumentoCoordinamento(int index) {
    return this.documentoCoordinamento.remove( index );
  }

  public List<DocumentoCoordinamento> getDocumentoCoordinamentoList() {
    return this.documentoCoordinamento;
  }

  public void setDocumentoCoordinamentoList(List<DocumentoCoordinamento> documentoCoordinamento) {
    this.documentoCoordinamento=documentoCoordinamento;
  }

  public int sizeDocumentoCoordinamentoList() {
    return this.documentoCoordinamento.size();
  }

  private static final long serialVersionUID = 1L;

	@Override
	public String serialize(org.openspcoop2.utils.beans.WriteToSerializerType type) throws org.openspcoop2.utils.UtilsException {
		if(type!=null && org.openspcoop2.utils.beans.WriteToSerializerType.JAXB.equals(type)){
			throw new org.openspcoop2.utils.UtilsException("Jaxb annotations not generated");
		}
		else{
			return super.serialize(type);
		}
	}
	@Override
	public String toXml_Jaxb() throws org.openspcoop2.utils.UtilsException {
		throw new org.openspcoop2.utils.UtilsException("Jaxb annotations not generated");
	}

  protected List<DocumentoCoordinamento> documentoCoordinamento = new ArrayList<DocumentoCoordinamento>();

  /**
   * @deprecated Use method getDocumentoCoordinamentoList
   * @return List<DocumentoCoordinamento>
  */
  @Deprecated
  public List<DocumentoCoordinamento> getDocumentoCoordinamento() {
  	return this.documentoCoordinamento;
  }

  /**
   * @deprecated Use method setDocumentoCoordinamentoList
   * @param documentoCoordinamento List<DocumentoCoordinamento>
  */
  @Deprecated
  public void setDocumentoCoordinamento(List<DocumentoCoordinamento> documentoCoordinamento) {
  	this.documentoCoordinamento=documentoCoordinamento;
  }

  /**
   * @deprecated Use method sizeDocumentoCoordinamentoList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeDocumentoCoordinamento() {
  	return this.documentoCoordinamento.size();
  }

  public static final String DOCUMENTO_COORDINAMENTO = "documentoCoordinamento";

}
