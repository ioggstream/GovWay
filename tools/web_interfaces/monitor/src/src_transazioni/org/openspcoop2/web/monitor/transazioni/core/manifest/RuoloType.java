//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.08.03 at 04:21:16 PM CEST 
//


package org.openspcoop2.web.monitor.transazioni.core.manifest;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ruolo_type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ruolo_type">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="sconosciuto"/>
 *     &lt;enumeration value="invocazioneOneway"/>
 *     &lt;enumeration value="invocazioneSincrona"/>
 *     &lt;enumeration value="invocazioneAsincronaSimmetrica"/>
 *     &lt;enumeration value="rispostaAsincronaSimmetrica"/>
 *     &lt;enumeration value="invocazioneAsincronaAsimmetrica"/>
 *     &lt;enumeration value="richiestaStatoAsincronaAsimmetrica"/>
 *     &lt;enumeration value="integrationManager"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ruolo_type", namespace = "http://www.link.it/pdd/monitor/web/pddmonitor/transazioni/core/manifest")
@XmlEnum
public enum RuoloType {

    @XmlEnumValue("sconosciuto")
    SCONOSCIUTO("sconosciuto"),
    @XmlEnumValue("invocazioneOneway")
    INVOCAZIONE_ONEWAY("invocazioneOneway"),
    @XmlEnumValue("invocazioneSincrona")
    INVOCAZIONE_SINCRONA("invocazioneSincrona"),
    @XmlEnumValue("invocazioneAsincronaSimmetrica")
    INVOCAZIONE_ASINCRONA_SIMMETRICA("invocazioneAsincronaSimmetrica"),
    @XmlEnumValue("rispostaAsincronaSimmetrica")
    RISPOSTA_ASINCRONA_SIMMETRICA("rispostaAsincronaSimmetrica"),
    @XmlEnumValue("invocazioneAsincronaAsimmetrica")
    INVOCAZIONE_ASINCRONA_ASIMMETRICA("invocazioneAsincronaAsimmetrica"),
    @XmlEnumValue("richiestaStatoAsincronaAsimmetrica")
    RICHIESTA_STATO_ASINCRONA_ASIMMETRICA("richiestaStatoAsincronaAsimmetrica"),
    @XmlEnumValue("integrationManager")
    INTEGRATION_MANAGER("integrationManager");
    private final String value;

    RuoloType(String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    public static RuoloType fromValue(String v) {
        for (RuoloType c: RuoloType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}