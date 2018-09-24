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

public class ReportGraficoCategorie  {
  
  @Schema(example = "errore", description = "")
  private String key = null;
  
  @Schema(example = "Fallite", description = "")
  private String label = null;
  
  @Schema(example = "#CD4A50", description = "")
  private String colore = null;
 /**
   * Get key
   * @return key
  **/
  @JsonProperty("key")
  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public ReportGraficoCategorie key(String key) {
    this.key = key;
    return this;
  }

 /**
   * Get label
   * @return label
  **/
  @JsonProperty("label")
  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public ReportGraficoCategorie label(String label) {
    this.label = label;
    return this;
  }

 /**
   * Get colore
   * @return colore
  **/
  @JsonProperty("colore")
  public String getColore() {
    return colore;
  }

  public void setColore(String colore) {
    this.colore = colore;
  }

  public ReportGraficoCategorie colore(String colore) {
    this.colore = colore;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReportGraficoCategorie {\n");
    
    sb.append("    key: ").append(toIndentedString(key)).append("\n");
    sb.append("    label: ").append(toIndentedString(label)).append("\n");
    sb.append("    colore: ").append(toIndentedString(colore)).append("\n");
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
