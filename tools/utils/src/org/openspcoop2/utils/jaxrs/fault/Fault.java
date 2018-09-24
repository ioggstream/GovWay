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

package org.openspcoop2.utils.jaxrs.fault;

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

/**	
 * Fault
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Fault  {
  
  @Schema(required = true, description = "Codice dell'errore riscontrato")
 /**
   * Codice dell'errore riscontrato  
  **/
  private String codice = null;
  
  @Schema(required = true, description = "Descrizione dell'errore")
 /**
   * Descrizione dell'errore  
  **/
  private String descrizione = null;
  
  @Schema(description = "Dettagli aggiuntivi sull'errore")
 /**
   * Dettagli aggiuntivi sull'errore  
  **/
  private String dettaglio = null;
 /**
   * Codice dell&#x27;errore riscontrato
   * @return codice
  **/
  @JsonProperty("codice")
  @NotNull
  public String getCodice() {
    return this.codice;
  }

  public void setCodice(String codice) {
    this.codice = codice;
  }

  public Fault codice(String codice) {
    this.codice = codice;
    return this;
  }

 /**
   * Descrizione dell&#x27;errore
   * @return descrizione
  **/
  @JsonProperty("descrizione")
  @NotNull
  public String getDescrizione() {
    return this.descrizione;
  }

  public void setDescrizione(String descrizione) {
    this.descrizione = descrizione;
  }

  public Fault descrizione(String descrizione) {
    this.descrizione = descrizione;
    return this;
  }

 /**
   * Dettagli aggiuntivi sull&#x27;errore
   * @return dettaglio
  **/
  @JsonProperty("dettaglio")
  public String getDettaglio() {
    return this.dettaglio;
  }

  public void setDettaglio(String dettaglio) {
    this.dettaglio = dettaglio;
  }

  public Fault dettaglio(String dettaglio) {
    this.dettaglio = dettaglio;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Fault {\n");
    
    sb.append("    codice: ").append(toIndentedString(this.codice)).append("\n");
    sb.append("    descrizione: ").append(toIndentedString(this.descrizione)).append("\n");
    sb.append("    dettaglio: ").append(toIndentedString(this.dettaglio)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private static String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
