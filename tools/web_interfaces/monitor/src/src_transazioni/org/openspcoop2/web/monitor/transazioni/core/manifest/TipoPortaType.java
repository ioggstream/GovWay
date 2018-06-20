//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.06.20 at 04:38:17 PM CEST 
//


package org.openspcoop2.web.monitor.transazioni.core.manifest;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for tipo_porta_type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="tipo_porta_type">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="delegata"/>
 *     &lt;enumeration value="applicativa"/>
 *     &lt;enumeration value="router"/>
 *     &lt;enumeration value="integrationManager"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "tipo_porta_type", namespace = "http://www.openspcoop2.org/web/monitor/transazioni/core/manifest")
@XmlEnum
public enum TipoPortaType {

    @XmlEnumValue("delegata")
    DELEGATA("delegata"),
    @XmlEnumValue("applicativa")
    APPLICATIVA("applicativa"),
    @XmlEnumValue("router")
    ROUTER("router"),
    @XmlEnumValue("integrationManager")
    INTEGRATION_MANAGER("integrationManager");
    private final String value;

    TipoPortaType(String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    public static TipoPortaType fromValue(String v) {
        for (TipoPortaType c: TipoPortaType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
