package org.openspcoop2.core.monitor.rs.server.model;

import java.util.ArrayList;
import java.util.List;
import org.openspcoop2.core.monitor.rs.server.model.Diagnostico;
import org.openspcoop2.core.monitor.rs.server.model.Traccia;
import org.openspcoop2.core.monitor.rs.server.model.TransazioneItem;
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

public class TransazioneDetail  {
  
  @Schema(description = "")
  private TransazioneItem datiGenerali = null;
  
  @Schema(description = "")
  private Traccia tracciaRichiesta = null;
  
  @Schema(description = "")
  private Traccia tracciaRisposta = null;
  
  @Schema(description = "")
  private List<Diagnostico> diagnostici = null;
  
  @Schema(description = "")
  private byte[] faultCooperazione = null;
  
  @Schema(description = "")
  private byte[] faultIntegrazione = null;
 /**
   * Get datiGenerali
   * @return datiGenerali
  **/
  @JsonProperty("datiGenerali")
  public TransazioneItem getDatiGenerali() {
    return datiGenerali;
  }

  public void setDatiGenerali(TransazioneItem datiGenerali) {
    this.datiGenerali = datiGenerali;
  }

  public TransazioneDetail datiGenerali(TransazioneItem datiGenerali) {
    this.datiGenerali = datiGenerali;
    return this;
  }

 /**
   * Get tracciaRichiesta
   * @return tracciaRichiesta
  **/
  @JsonProperty("tracciaRichiesta")
  public Traccia getTracciaRichiesta() {
    return tracciaRichiesta;
  }

  public void setTracciaRichiesta(Traccia tracciaRichiesta) {
    this.tracciaRichiesta = tracciaRichiesta;
  }

  public TransazioneDetail tracciaRichiesta(Traccia tracciaRichiesta) {
    this.tracciaRichiesta = tracciaRichiesta;
    return this;
  }

 /**
   * Get tracciaRisposta
   * @return tracciaRisposta
  **/
  @JsonProperty("tracciaRisposta")
  public Traccia getTracciaRisposta() {
    return tracciaRisposta;
  }

  public void setTracciaRisposta(Traccia tracciaRisposta) {
    this.tracciaRisposta = tracciaRisposta;
  }

  public TransazioneDetail tracciaRisposta(Traccia tracciaRisposta) {
    this.tracciaRisposta = tracciaRisposta;
    return this;
  }

 /**
   * Get diagnostici
   * @return diagnostici
  **/
  @JsonProperty("diagnostici")
  public List<Diagnostico> getDiagnostici() {
    return diagnostici;
  }

  public void setDiagnostici(List<Diagnostico> diagnostici) {
    this.diagnostici = diagnostici;
  }

  public TransazioneDetail diagnostici(List<Diagnostico> diagnostici) {
    this.diagnostici = diagnostici;
    return this;
  }

  public TransazioneDetail addDiagnosticiItem(Diagnostico diagnosticiItem) {
    this.diagnostici.add(diagnosticiItem);
    return this;
  }

 /**
   * Get faultCooperazione
   * @return faultCooperazione
  **/
  @JsonProperty("faultCooperazione")
  public byte[] getFaultCooperazione() {
    return faultCooperazione;
  }

  public void setFaultCooperazione(byte[] faultCooperazione) {
    this.faultCooperazione = faultCooperazione;
  }

  public TransazioneDetail faultCooperazione(byte[] faultCooperazione) {
    this.faultCooperazione = faultCooperazione;
    return this;
  }

 /**
   * Get faultIntegrazione
   * @return faultIntegrazione
  **/
  @JsonProperty("faultIntegrazione")
  public byte[] getFaultIntegrazione() {
    return faultIntegrazione;
  }

  public void setFaultIntegrazione(byte[] faultIntegrazione) {
    this.faultIntegrazione = faultIntegrazione;
  }

  public TransazioneDetail faultIntegrazione(byte[] faultIntegrazione) {
    this.faultIntegrazione = faultIntegrazione;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransazioneDetail {\n");
    
    sb.append("    datiGenerali: ").append(toIndentedString(datiGenerali)).append("\n");
    sb.append("    tracciaRichiesta: ").append(toIndentedString(tracciaRichiesta)).append("\n");
    sb.append("    tracciaRisposta: ").append(toIndentedString(tracciaRisposta)).append("\n");
    sb.append("    diagnostici: ").append(toIndentedString(diagnostici)).append("\n");
    sb.append("    faultCooperazione: ").append(toIndentedString(faultCooperazione)).append("\n");
    sb.append("    faultIntegrazione: ").append(toIndentedString(faultIntegrazione)).append("\n");
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
