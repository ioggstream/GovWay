package org.openspcoop2.core.monitor.rs.server.model;

import org.joda.time.DateTime;
import org.openspcoop2.core.monitor.rs.server.model.FiltroServizio;
import org.openspcoop2.core.monitor.rs.server.model.FiltroTemporale;
import org.openspcoop2.core.monitor.rs.server.model.FiltroTransazione;
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

public class RicercaIntervalloTemporale extends FiltroTemporale {
@XmlType(name="ProtocolloEnum")
@XmlEnum(String.class)
public enum ProtocolloEnum {

@XmlEnumValue("spcoop") SPCOOP(String.valueOf("spcoop")), @XmlEnumValue("trasparente") TRASPARENTE(String.valueOf("trasparente")), @XmlEnumValue("sdi") SDI(String.valueOf("sdi")), @XmlEnumValue("edelivery") EDELIVERY(String.valueOf("edelivery"));


    private String value;

    ProtocolloEnum (String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public static ProtocolloEnum fromValue(String v) {
        for (ProtocolloEnum b : ProtocolloEnum.values()) {
            if (String.valueOf(b.value).equals(v)) {
                return b;
            }
        }
        return null;
    }
}
  
  @Schema(example = "trasparente", description = "")
  private ProtocolloEnum protocollo = null;
  
  @Schema(example = "EnteInterno", description = "")
  private String soggettoLocale = null;
  
  @Schema(example = "EnteEsterno", description = "")
  private String soggettoRemoto = null;
  
  @Schema(example = "applicativo1", description = "")
  private String applicativo = null;
  
  @Schema(example = "servizio1", description = "")
  private String servizio = null;
  
  @Schema(example = "azione1", description = "")
  private String azione = null;
@XmlType(name="TipologiaEnum")
@XmlEnum(String.class)
public enum TipologiaEnum {

@XmlEnumValue("fruzione") FRUZIONE(String.valueOf("fruzione")), @XmlEnumValue("erogazione") EROGAZIONE(String.valueOf("erogazione"));


    private String value;

    TipologiaEnum (String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public static TipologiaEnum fromValue(String v) {
        for (TipologiaEnum b : TipologiaEnum.values()) {
            if (String.valueOf(b.value).equals(v)) {
                return b;
            }
        }
        return null;
    }
}
  
  @Schema(example = "fruizione", description = "")
  private TipologiaEnum tipologia = null;
@XmlType(name="EsitoEnum")
@XmlEnum(String.class)
public enum EsitoEnum {

@XmlEnumValue("fallite") FALLITE(String.valueOf("fallite")), @XmlEnumValue("fault applicativo") FAULT_APPLICATIVO(String.valueOf("fault applicativo")), @XmlEnumValue("completate con successo") COMPLETATE_CON_SUCCESSO(String.valueOf("completate con successo"));


    private String value;

    EsitoEnum (String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public static EsitoEnum fromValue(String v) {
        for (EsitoEnum b : EsitoEnum.values()) {
            if (String.valueOf(b.value).equals(v)) {
                return b;
            }
        }
        return null;
    }
}
  
  @Schema(example = "fallite", description = "")
  private EsitoEnum esito = null;
@XmlType(name="DettaglioEsitoEnum")
@XmlEnum(String.class)
public enum DettaglioEsitoEnum {

@XmlEnumValue("ok") OK(String.valueOf("ok")), @XmlEnumValue("ok (presenza anomalie") OK_PRESENZA_ANOMALIE(String.valueOf("ok (presenza anomalie")), @XmlEnumValue("fault applicativo") FAULT_APPLICATIVO(String.valueOf("fault applicativo")), @XmlEnumValue("autenticazione fallita") AUTENTICAZIONE_FALLITA(String.valueOf("autenticazione fallita")), @XmlEnumValue("autorizzazione negata") AUTORIZZAZIONE_NEGATA(String.valueOf("autorizzazione negata")), @XmlEnumValue("richiesta client rifiutata") RICHIESTA_CLIENT_RIFIUTATA(String.valueOf("richiesta client rifiutata")), @XmlEnumValue("errore di connessione") ERRORE_DI_CONNESSIONE(String.valueOf("errore di connessione")), @XmlEnumValue("errore di protocollo") ERRORE_DI_PROTOCOLLO(String.valueOf("errore di protocollo")), @XmlEnumValue("violazione rate limiting") VIOLAZIONE_RATE_LIMITING(String.valueOf("violazione rate limiting")), @XmlEnumValue("violazione rate limiting warningOnly") VIOLAZIONE_RATE_LIMITING_WARNINGONLY(String.valueOf("violazione rate limiting warningOnly")), @XmlEnumValue("superamento limite richieste") SUPERAMENTO_LIMITE_RICHIESTE(String.valueOf("superamento limite richieste")), @XmlEnumValue("superamento limite richieste warningOnly") SUPERAMENTO_LIMITE_RICHIESTE_WARNINGONLY(String.valueOf("superamento limite richieste warningOnly")), @XmlEnumValue("fault pdd esterna") FAULT_PDD_ESTERNA(String.valueOf("fault pdd esterna")), @XmlEnumValue("contenuto richiesta non riconosciuto") CONTENUTO_RICHIESTA_NON_RICONOSCIUTO(String.valueOf("contenuto richiesta non riconosciuto")), @XmlEnumValue("contenuto risposta non riconosciuto") CONTENUTO_RISPOSTA_NON_RICONOSCIUTO(String.valueOf("contenuto risposta non riconosciuto")), @XmlEnumValue("connessione client interrotta") CONNESSIONE_CLIENT_INTERROTTA(String.valueOf("connessione client interrotta")), @XmlEnumValue("fault generato dalla pdd") FAULT_GENERATO_DALLA_PDD(String.valueOf("fault generato dalla pdd")), @XmlEnumValue("errore interno pdd") ERRORE_INTERNO_PDD(String.valueOf("errore interno pdd"));


    private String value;

    DettaglioEsitoEnum (String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public static DettaglioEsitoEnum fromValue(String v) {
        for (DettaglioEsitoEnum b : DettaglioEsitoEnum.values()) {
            if (String.valueOf(b.value).equals(v)) {
                return b;
            }
        }
        return null;
    }
}
  
  @Schema(example = "ok", description = "")
  private DettaglioEsitoEnum dettaglioEsito = null;
@XmlType(name="ContestoEnum")
@XmlEnum(String.class)
public enum ContestoEnum {

@XmlEnumValue("applicativo") APPLICATIVO(String.valueOf("applicativo")), @XmlEnumValue("sistema") SISTEMA(String.valueOf("sistema"));


    private String value;

    ContestoEnum (String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public static ContestoEnum fromValue(String v) {
        for (ContestoEnum b : ContestoEnum.values()) {
            if (String.valueOf(b.value).equals(v)) {
                return b;
            }
        }
        return null;
    }
}
  
  @Schema(example = "applicativo", description = "")
  private ContestoEnum contesto = null;
  
  @Schema(example = "evento1", description = "")
  private String evento = null;
 /**
   * Get protocollo
   * @return protocollo
  **/
  @JsonProperty("protocollo")
  public String getProtocollo() {
    if (protocollo == null) {
      return null;
    }
    return protocollo.value();
  }

  public void setProtocollo(ProtocolloEnum protocollo) {
    this.protocollo = protocollo;
  }

  public RicercaIntervalloTemporale protocollo(ProtocolloEnum protocollo) {
    this.protocollo = protocollo;
    return this;
  }

 /**
   * Get soggettoLocale
   * @return soggettoLocale
  **/
  @JsonProperty("soggettoLocale")
  public String getSoggettoLocale() {
    return soggettoLocale;
  }

  public void setSoggettoLocale(String soggettoLocale) {
    this.soggettoLocale = soggettoLocale;
  }

  public RicercaIntervalloTemporale soggettoLocale(String soggettoLocale) {
    this.soggettoLocale = soggettoLocale;
    return this;
  }

 /**
   * Get soggettoRemoto
   * @return soggettoRemoto
  **/
  @JsonProperty("soggettoRemoto")
  public String getSoggettoRemoto() {
    return soggettoRemoto;
  }

  public void setSoggettoRemoto(String soggettoRemoto) {
    this.soggettoRemoto = soggettoRemoto;
  }

  public RicercaIntervalloTemporale soggettoRemoto(String soggettoRemoto) {
    this.soggettoRemoto = soggettoRemoto;
    return this;
  }

 /**
   * Get applicativo
   * @return applicativo
  **/
  @JsonProperty("applicativo")
  public String getApplicativo() {
    return applicativo;
  }

  public void setApplicativo(String applicativo) {
    this.applicativo = applicativo;
  }

  public RicercaIntervalloTemporale applicativo(String applicativo) {
    this.applicativo = applicativo;
    return this;
  }

 /**
   * Get servizio
   * @return servizio
  **/
  @JsonProperty("servizio")
  public String getServizio() {
    return servizio;
  }

  public void setServizio(String servizio) {
    this.servizio = servizio;
  }

  public RicercaIntervalloTemporale servizio(String servizio) {
    this.servizio = servizio;
    return this;
  }

 /**
   * Get azione
   * @return azione
  **/
  @JsonProperty("azione")
  public String getAzione() {
    return azione;
  }

  public void setAzione(String azione) {
    this.azione = azione;
  }

  public RicercaIntervalloTemporale azione(String azione) {
    this.azione = azione;
    return this;
  }

 /**
   * Get tipologia
   * @return tipologia
  **/
  @JsonProperty("tipologia")
  public String getTipologia() {
    if (tipologia == null) {
      return null;
    }
    return tipologia.value();
  }

  public void setTipologia(TipologiaEnum tipologia) {
    this.tipologia = tipologia;
  }

  public RicercaIntervalloTemporale tipologia(TipologiaEnum tipologia) {
    this.tipologia = tipologia;
    return this;
  }

 /**
   * Get esito
   * @return esito
  **/
  @JsonProperty("esito")
  public String getEsito() {
    if (esito == null) {
      return null;
    }
    return esito.value();
  }

  public void setEsito(EsitoEnum esito) {
    this.esito = esito;
  }

  public RicercaIntervalloTemporale esito(EsitoEnum esito) {
    this.esito = esito;
    return this;
  }

 /**
   * Get dettaglioEsito
   * @return dettaglioEsito
  **/
  @JsonProperty("dettaglioEsito")
  public String getDettaglioEsito() {
    if (dettaglioEsito == null) {
      return null;
    }
    return dettaglioEsito.value();
  }

  public void setDettaglioEsito(DettaglioEsitoEnum dettaglioEsito) {
    this.dettaglioEsito = dettaglioEsito;
  }

  public RicercaIntervalloTemporale dettaglioEsito(DettaglioEsitoEnum dettaglioEsito) {
    this.dettaglioEsito = dettaglioEsito;
    return this;
  }

 /**
   * Get contesto
   * @return contesto
  **/
  @JsonProperty("contesto")
  public String getContesto() {
    if (contesto == null) {
      return null;
    }
    return contesto.value();
  }

  public void setContesto(ContestoEnum contesto) {
    this.contesto = contesto;
  }

  public RicercaIntervalloTemporale contesto(ContestoEnum contesto) {
    this.contesto = contesto;
    return this;
  }

 /**
   * Get evento
   * @return evento
  **/
  @JsonProperty("evento")
  public String getEvento() {
    return evento;
  }

  public void setEvento(String evento) {
    this.evento = evento;
  }

  public RicercaIntervalloTemporale evento(String evento) {
    this.evento = evento;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RicercaIntervalloTemporale {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    protocollo: ").append(toIndentedString(protocollo)).append("\n");
    sb.append("    soggettoLocale: ").append(toIndentedString(soggettoLocale)).append("\n");
    sb.append("    soggettoRemoto: ").append(toIndentedString(soggettoRemoto)).append("\n");
    sb.append("    applicativo: ").append(toIndentedString(applicativo)).append("\n");
    sb.append("    servizio: ").append(toIndentedString(servizio)).append("\n");
    sb.append("    azione: ").append(toIndentedString(azione)).append("\n");
    sb.append("    tipologia: ").append(toIndentedString(tipologia)).append("\n");
    sb.append("    esito: ").append(toIndentedString(esito)).append("\n");
    sb.append("    dettaglioEsito: ").append(toIndentedString(dettaglioEsito)).append("\n");
    sb.append("    contesto: ").append(toIndentedString(contesto)).append("\n");
    sb.append("    evento: ").append(toIndentedString(evento)).append("\n");
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
