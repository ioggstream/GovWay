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

public class ListaPaginata  {
  
  @Schema(example = "100", description = "")
  private Integer numRisultati = null;
  
  @Schema(example = "4", description = "")
  private Integer numPagine = null;
  
  @Schema(example = "25", description = "")
  private Integer risultatiPerPagina = null;
  
  @Schema(example = "1", description = "")
  private Integer pagina = null;
  
  @Schema(example = "/risorsa?pagina=2", description = "")
  private String prossimiRisultati = null;
 /**
   * Get numRisultati
   * @return numRisultati
  **/
  @JsonProperty("numRisultati")
  public Integer getNumRisultati() {
    return numRisultati;
  }

  public void setNumRisultati(Integer numRisultati) {
    this.numRisultati = numRisultati;
  }

  public ListaPaginata numRisultati(Integer numRisultati) {
    this.numRisultati = numRisultati;
    return this;
  }

 /**
   * Get numPagine
   * @return numPagine
  **/
  @JsonProperty("numPagine")
  public Integer getNumPagine() {
    return numPagine;
  }

  public void setNumPagine(Integer numPagine) {
    this.numPagine = numPagine;
  }

  public ListaPaginata numPagine(Integer numPagine) {
    this.numPagine = numPagine;
    return this;
  }

 /**
   * Get risultatiPerPagina
   * @return risultatiPerPagina
  **/
  @JsonProperty("risultatiPerPagina")
  public Integer getRisultatiPerPagina() {
    return risultatiPerPagina;
  }

  public void setRisultatiPerPagina(Integer risultatiPerPagina) {
    this.risultatiPerPagina = risultatiPerPagina;
  }

  public ListaPaginata risultatiPerPagina(Integer risultatiPerPagina) {
    this.risultatiPerPagina = risultatiPerPagina;
    return this;
  }

 /**
   * Get pagina
   * @return pagina
  **/
  @JsonProperty("pagina")
  public Integer getPagina() {
    return pagina;
  }

  public void setPagina(Integer pagina) {
    this.pagina = pagina;
  }

  public ListaPaginata pagina(Integer pagina) {
    this.pagina = pagina;
    return this;
  }

 /**
   * Get prossimiRisultati
   * @return prossimiRisultati
  **/
  @JsonProperty("prossimiRisultati")
  public String getProssimiRisultati() {
    return prossimiRisultati;
  }

  public void setProssimiRisultati(String prossimiRisultati) {
    this.prossimiRisultati = prossimiRisultati;
  }

  public ListaPaginata prossimiRisultati(String prossimiRisultati) {
    this.prossimiRisultati = prossimiRisultati;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ListaPaginata {\n");
    
    sb.append("    numRisultati: ").append(toIndentedString(numRisultati)).append("\n");
    sb.append("    numPagine: ").append(toIndentedString(numPagine)).append("\n");
    sb.append("    risultatiPerPagina: ").append(toIndentedString(risultatiPerPagina)).append("\n");
    sb.append("    pagina: ").append(toIndentedString(pagina)).append("\n");
    sb.append("    prossimiRisultati: ").append(toIndentedString(prossimiRisultati)).append("\n");
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
