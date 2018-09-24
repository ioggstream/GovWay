package org.openspcoop2.core.monitor.rs.server.model;

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FiltroIdApplicativo  {
  
  @Schema(description = "")
  private Boolean ricercaEsatta = null;
  
  @Schema(description = "")
  private Boolean caseSensitive = null;
  
  @Schema(example = "abc123", description = "")
  private String idApplicativo = null;
 /**
   * Get ricercaEsatta
   * @return ricercaEsatta
  **/
  @JsonProperty("ricercaEsatta")
  public Boolean isisRicercaEsatta() {
    return ricercaEsatta;
  }

  public void setRicercaEsatta(Boolean ricercaEsatta) {
    this.ricercaEsatta = ricercaEsatta;
  }

  public FiltroIdApplicativo ricercaEsatta(Boolean ricercaEsatta) {
    this.ricercaEsatta = ricercaEsatta;
    return this;
  }

 /**
   * Get caseSensitive
   * @return caseSensitive
  **/
  @JsonProperty("caseSensitive")
  public Boolean isisCaseSensitive() {
    return caseSensitive;
  }

  public void setCaseSensitive(Boolean caseSensitive) {
    this.caseSensitive = caseSensitive;
  }

  public FiltroIdApplicativo caseSensitive(Boolean caseSensitive) {
    this.caseSensitive = caseSensitive;
    return this;
  }

 /**
   * Get idApplicativo
   * @return idApplicativo
  **/
  @JsonProperty("idApplicativo")
  public String getIdApplicativo() {
    return idApplicativo;
  }

  public void setIdApplicativo(String idApplicativo) {
    this.idApplicativo = idApplicativo;
  }

  public FiltroIdApplicativo idApplicativo(String idApplicativo) {
    this.idApplicativo = idApplicativo;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FiltroIdApplicativo {\n");
    
    sb.append("    ricercaEsatta: ").append(toIndentedString(ricercaEsatta)).append("\n");
    sb.append("    caseSensitive: ").append(toIndentedString(caseSensitive)).append("\n");
    sb.append("    idApplicativo: ").append(toIndentedString(idApplicativo)).append("\n");
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
