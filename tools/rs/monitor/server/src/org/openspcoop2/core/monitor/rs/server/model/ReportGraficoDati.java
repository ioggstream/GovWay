package org.openspcoop2.core.monitor.rs.server.model;

import java.util.ArrayList;
import java.util.List;
import org.openspcoop2.core.monitor.rs.server.model.ReportGraficoDatoY;
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

public class ReportGraficoDati  {
  
  @Schema(example = "mag/17", description = "")
  private String datoX = null;
  
  @Schema(description = "")
  private List<ReportGraficoDatoY> datoY = null;
 /**
   * Get datoX
   * @return datoX
  **/
  @JsonProperty("datoX")
  public String getDatoX() {
    return datoX;
  }

  public void setDatoX(String datoX) {
    this.datoX = datoX;
  }

  public ReportGraficoDati datoX(String datoX) {
    this.datoX = datoX;
    return this;
  }

 /**
   * Get datoY
   * @return datoY
  **/
  @JsonProperty("datoY")
  public List<ReportGraficoDatoY> getDatoY() {
    return datoY;
  }

  public void setDatoY(List<ReportGraficoDatoY> datoY) {
    this.datoY = datoY;
  }

  public ReportGraficoDati datoY(List<ReportGraficoDatoY> datoY) {
    this.datoY = datoY;
    return this;
  }

  public ReportGraficoDati addDatoYItem(ReportGraficoDatoY datoYItem) {
    this.datoY.add(datoYItem);
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReportGraficoDati {\n");
    
    sb.append("    datoX: ").append(toIndentedString(datoX)).append("\n");
    sb.append("    datoY: ").append(toIndentedString(datoY)).append("\n");
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
