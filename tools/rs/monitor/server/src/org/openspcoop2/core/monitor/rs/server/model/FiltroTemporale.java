package org.openspcoop2.core.monitor.rs.server.model;

import org.joda.time.DateTime;
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

public class FiltroTemporale  {
  
  @Schema(description = "")
  private DateTime dataDa = null;
  
  @Schema(description = "")
  private DateTime dataA = null;
 /**
   * Get dataDa
   * @return dataDa
  **/
  @JsonProperty("dataDa")
  public DateTime getDataDa() {
    return dataDa;
  }

  public void setDataDa(DateTime dataDa) {
    this.dataDa = dataDa;
  }

  public FiltroTemporale dataDa(DateTime dataDa) {
    this.dataDa = dataDa;
    return this;
  }

 /**
   * Get dataA
   * @return dataA
  **/
  @JsonProperty("dataA")
  public DateTime getDataA() {
    return dataA;
  }

  public void setDataA(DateTime dataA) {
    this.dataA = dataA;
  }

  public FiltroTemporale dataA(DateTime dataA) {
    this.dataA = dataA;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FiltroTemporale {\n");
    
    sb.append("    dataDa: ").append(toIndentedString(dataDa)).append("\n");
    sb.append("    dataA: ").append(toIndentedString(dataA)).append("\n");
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
