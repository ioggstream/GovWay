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
package org.openspcoop2.core.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.id.IDPortaApplicativa;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for porta-applicativa complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="porta-applicativa">
 * 		&lt;sequence>
 * 			&lt;element name="soggetto-virtuale" type="{http://www.openspcoop2.org/core/config}porta-applicativa-soggetto-virtuale" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="servizio" type="{http://www.openspcoop2.org/core/config}porta-applicativa-servizio" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="azione" type="{http://www.openspcoop2.org/core/config}porta-applicativa-azione" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="servizio-applicativo" type="{http://www.openspcoop2.org/core/config}porta-applicativa-servizio-applicativo" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="xacml-policy" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="soggetti" type="{http://www.openspcoop2.org/core/config}porta-applicativa-autorizzazione-soggetti" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="servizi-applicativi-autorizzati" type="{http://www.openspcoop2.org/core/config}porta-applicativa-autorizzazione-servizi-applicativi" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="ruoli" type="{http://www.openspcoop2.org/core/config}autorizzazione-ruoli" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="scope" type="{http://www.openspcoop2.org/core/config}autorizzazione-scope" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="gestione-token" type="{http://www.openspcoop2.org/core/config}gestione-token" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="proprieta" type="{http://www.openspcoop2.org/core/config}proprieta" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="mtom-processor" type="{http://www.openspcoop2.org/core/config}mtom-processor" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="message-security" type="{http://www.openspcoop2.org/core/config}message-security" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="validazione-contenuti-applicativi" type="{http://www.openspcoop2.org/core/config}validazione-contenuti-applicativi" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="correlazione-applicativa" type="{http://www.openspcoop2.org/core/config}correlazione-applicativa" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="correlazione-applicativa-risposta" type="{http://www.openspcoop2.org/core/config}correlazione-applicativa-risposta" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="dump" type="{http://www.openspcoop2.org/core/config}dump-configurazione" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="tracciamento" type="{http://www.openspcoop2.org/core/config}porta-tracciamento" minOccurs="0" maxOccurs="1"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="id-soggetto" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" use="optional"/>
 * 		&lt;attribute name="id-accordo" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" use="optional"/>
 * 		&lt;attribute name="id-port-type" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" use="optional"/>
 * 		&lt;attribute name="tipo-soggetto-proprietario" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="nome-soggetto-proprietario" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="stato-message-security" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="ricevuta-asincrona-simmetrica" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="abilitato"/>
 * 		&lt;attribute name="ricevuta-asincrona-asimmetrica" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="abilitato"/>
 * 		&lt;attribute name="integrazione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="allega-body" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/>
 * 		&lt;attribute name="scarta-body" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/>
 * 		&lt;attribute name="gestione-manifest" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional"/>
 * 		&lt;attribute name="stateless" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional"/>
 * 		&lt;attribute name="behaviour" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="autenticazione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional" default="ssl"/>
 * 		&lt;attribute name="autenticazione-opzionale" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/>
 * 		&lt;attribute name="autorizzazione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional" default="authenticated"/>
 * 		&lt;attribute name="autorizzazione-contenuto" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="ricerca-porta-azione-delegata" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/>
 * 		&lt;attribute name="stato" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="abilitato"/>
 * 		&lt;attribute name="ora-registrazione" type="{http://www.w3.org/2001/XMLSchema}dateTime" use="optional"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "porta-applicativa", 
  propOrder = {
  	"soggettoVirtuale",
  	"servizio",
  	"azione",
  	"servizioApplicativo",
  	"xacmlPolicy",
  	"soggetti",
  	"serviziApplicativiAutorizzati",
  	"ruoli",
  	"scope",
  	"gestioneToken",
  	"proprieta",
  	"mtomProcessor",
  	"messageSecurity",
  	"validazioneContenutiApplicativi",
  	"correlazioneApplicativa",
  	"correlazioneApplicativaRisposta",
  	"dump",
  	"tracciamento"
  }
)

@XmlRootElement(name = "porta-applicativa")

public class PortaApplicativa extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public PortaApplicativa() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return Long.valueOf(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=Long.valueOf(-1);
  }

  public IDPortaApplicativa getOldIDPortaApplicativaForUpdate() {
    return this.oldIDPortaApplicativaForUpdate;
  }

  public void setOldIDPortaApplicativaForUpdate(IDPortaApplicativa oldIDPortaApplicativaForUpdate) {
    this.oldIDPortaApplicativaForUpdate=oldIDPortaApplicativaForUpdate;
  }

  public void addExtendedInfo(Object extendedInfo) {
    this.extendedInfo.add(extendedInfo);
  }

  public Object getExtendedInfo(int index) {
    return this.extendedInfo.get( index );
  }

  public Object removeExtendedInfo(int index) {
    return this.extendedInfo.remove( index );
  }

  public List<Object> getExtendedInfoList() {
    return this.extendedInfo;
  }

  public void setExtendedInfoList(List<Object> extendedInfo) {
    this.extendedInfo=extendedInfo;
  }

  public int sizeExtendedInfoList() {
    return this.extendedInfo.size();
  }

  public PortaApplicativaSoggettoVirtuale getSoggettoVirtuale() {
    return this.soggettoVirtuale;
  }

  public void setSoggettoVirtuale(PortaApplicativaSoggettoVirtuale soggettoVirtuale) {
    this.soggettoVirtuale = soggettoVirtuale;
  }

  public PortaApplicativaServizio getServizio() {
    return this.servizio;
  }

  public void setServizio(PortaApplicativaServizio servizio) {
    this.servizio = servizio;
  }

  public PortaApplicativaAzione getAzione() {
    return this.azione;
  }

  public void setAzione(PortaApplicativaAzione azione) {
    this.azione = azione;
  }

  public void addServizioApplicativo(PortaApplicativaServizioApplicativo servizioApplicativo) {
    this.servizioApplicativo.add(servizioApplicativo);
  }

  public PortaApplicativaServizioApplicativo getServizioApplicativo(int index) {
    return this.servizioApplicativo.get( index );
  }

  public PortaApplicativaServizioApplicativo removeServizioApplicativo(int index) {
    return this.servizioApplicativo.remove( index );
  }

  public List<PortaApplicativaServizioApplicativo> getServizioApplicativoList() {
    return this.servizioApplicativo;
  }

  public void setServizioApplicativoList(List<PortaApplicativaServizioApplicativo> servizioApplicativo) {
    this.servizioApplicativo=servizioApplicativo;
  }

  public int sizeServizioApplicativoList() {
    return this.servizioApplicativo.size();
  }

  public java.lang.String getXacmlPolicy() {
    return this.xacmlPolicy;
  }

  public void setXacmlPolicy(java.lang.String xacmlPolicy) {
    this.xacmlPolicy = xacmlPolicy;
  }

  public PortaApplicativaAutorizzazioneSoggetti getSoggetti() {
    return this.soggetti;
  }

  public void setSoggetti(PortaApplicativaAutorizzazioneSoggetti soggetti) {
    this.soggetti = soggetti;
  }

  public PortaApplicativaAutorizzazioneServiziApplicativi getServiziApplicativiAutorizzati() {
    return this.serviziApplicativiAutorizzati;
  }

  public void setServiziApplicativiAutorizzati(PortaApplicativaAutorizzazioneServiziApplicativi serviziApplicativiAutorizzati) {
    this.serviziApplicativiAutorizzati = serviziApplicativiAutorizzati;
  }

  public AutorizzazioneRuoli getRuoli() {
    return this.ruoli;
  }

  public void setRuoli(AutorizzazioneRuoli ruoli) {
    this.ruoli = ruoli;
  }

  public AutorizzazioneScope getScope() {
    return this.scope;
  }

  public void setScope(AutorizzazioneScope scope) {
    this.scope = scope;
  }

  public GestioneToken getGestioneToken() {
    return this.gestioneToken;
  }

  public void setGestioneToken(GestioneToken gestioneToken) {
    this.gestioneToken = gestioneToken;
  }

  public void addProprieta(Proprieta proprieta) {
    this.proprieta.add(proprieta);
  }

  public Proprieta getProprieta(int index) {
    return this.proprieta.get( index );
  }

  public Proprieta removeProprieta(int index) {
    return this.proprieta.remove( index );
  }

  public List<Proprieta> getProprietaList() {
    return this.proprieta;
  }

  public void setProprietaList(List<Proprieta> proprieta) {
    this.proprieta=proprieta;
  }

  public int sizeProprietaList() {
    return this.proprieta.size();
  }

  public MtomProcessor getMtomProcessor() {
    return this.mtomProcessor;
  }

  public void setMtomProcessor(MtomProcessor mtomProcessor) {
    this.mtomProcessor = mtomProcessor;
  }

  public MessageSecurity getMessageSecurity() {
    return this.messageSecurity;
  }

  public void setMessageSecurity(MessageSecurity messageSecurity) {
    this.messageSecurity = messageSecurity;
  }

  public ValidazioneContenutiApplicativi getValidazioneContenutiApplicativi() {
    return this.validazioneContenutiApplicativi;
  }

  public void setValidazioneContenutiApplicativi(ValidazioneContenutiApplicativi validazioneContenutiApplicativi) {
    this.validazioneContenutiApplicativi = validazioneContenutiApplicativi;
  }

  public CorrelazioneApplicativa getCorrelazioneApplicativa() {
    return this.correlazioneApplicativa;
  }

  public void setCorrelazioneApplicativa(CorrelazioneApplicativa correlazioneApplicativa) {
    this.correlazioneApplicativa = correlazioneApplicativa;
  }

  public CorrelazioneApplicativaRisposta getCorrelazioneApplicativaRisposta() {
    return this.correlazioneApplicativaRisposta;
  }

  public void setCorrelazioneApplicativaRisposta(CorrelazioneApplicativaRisposta correlazioneApplicativaRisposta) {
    this.correlazioneApplicativaRisposta = correlazioneApplicativaRisposta;
  }

  public DumpConfigurazione getDump() {
    return this.dump;
  }

  public void setDump(DumpConfigurazione dump) {
    this.dump = dump;
  }

  public PortaTracciamento getTracciamento() {
    return this.tracciamento;
  }

  public void setTracciamento(PortaTracciamento tracciamento) {
    this.tracciamento = tracciamento;
  }

  public java.lang.Long getIdSoggetto() {
    return this.idSoggetto;
  }

  public void setIdSoggetto(java.lang.Long idSoggetto) {
    this.idSoggetto = idSoggetto;
  }

  public java.lang.Long getIdAccordo() {
    return this.idAccordo;
  }

  public void setIdAccordo(java.lang.Long idAccordo) {
    this.idAccordo = idAccordo;
  }

  public java.lang.Long getIdPortType() {
    return this.idPortType;
  }

  public void setIdPortType(java.lang.Long idPortType) {
    this.idPortType = idPortType;
  }

  public java.lang.String getTipoSoggettoProprietario() {
    return this.tipoSoggettoProprietario;
  }

  public void setTipoSoggettoProprietario(java.lang.String tipoSoggettoProprietario) {
    this.tipoSoggettoProprietario = tipoSoggettoProprietario;
  }

  public java.lang.String getNomeSoggettoProprietario() {
    return this.nomeSoggettoProprietario;
  }

  public void setNomeSoggettoProprietario(java.lang.String nomeSoggettoProprietario) {
    this.nomeSoggettoProprietario = nomeSoggettoProprietario;
  }

  public java.lang.String getStatoMessageSecurity() {
    return this.statoMessageSecurity;
  }

  public void setStatoMessageSecurity(java.lang.String statoMessageSecurity) {
    this.statoMessageSecurity = statoMessageSecurity;
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public java.lang.String getDescrizione() {
    return this.descrizione;
  }

  public void setDescrizione(java.lang.String descrizione) {
    this.descrizione = descrizione;
  }

  public void set_value_ricevutaAsincronaSimmetrica(String value) {
    this.ricevutaAsincronaSimmetrica = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_ricevutaAsincronaSimmetrica() {
    if(this.ricevutaAsincronaSimmetrica == null){
    	return null;
    }else{
    	return this.ricevutaAsincronaSimmetrica.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getRicevutaAsincronaSimmetrica() {
    return this.ricevutaAsincronaSimmetrica;
  }

  public void setRicevutaAsincronaSimmetrica(org.openspcoop2.core.config.constants.StatoFunzionalita ricevutaAsincronaSimmetrica) {
    this.ricevutaAsincronaSimmetrica = ricevutaAsincronaSimmetrica;
  }

  public void set_value_ricevutaAsincronaAsimmetrica(String value) {
    this.ricevutaAsincronaAsimmetrica = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_ricevutaAsincronaAsimmetrica() {
    if(this.ricevutaAsincronaAsimmetrica == null){
    	return null;
    }else{
    	return this.ricevutaAsincronaAsimmetrica.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getRicevutaAsincronaAsimmetrica() {
    return this.ricevutaAsincronaAsimmetrica;
  }

  public void setRicevutaAsincronaAsimmetrica(org.openspcoop2.core.config.constants.StatoFunzionalita ricevutaAsincronaAsimmetrica) {
    this.ricevutaAsincronaAsimmetrica = ricevutaAsincronaAsimmetrica;
  }

  public java.lang.String getIntegrazione() {
    return this.integrazione;
  }

  public void setIntegrazione(java.lang.String integrazione) {
    this.integrazione = integrazione;
  }

  public void set_value_allegaBody(String value) {
    this.allegaBody = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_allegaBody() {
    if(this.allegaBody == null){
    	return null;
    }else{
    	return this.allegaBody.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getAllegaBody() {
    return this.allegaBody;
  }

  public void setAllegaBody(org.openspcoop2.core.config.constants.StatoFunzionalita allegaBody) {
    this.allegaBody = allegaBody;
  }

  public void set_value_scartaBody(String value) {
    this.scartaBody = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_scartaBody() {
    if(this.scartaBody == null){
    	return null;
    }else{
    	return this.scartaBody.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getScartaBody() {
    return this.scartaBody;
  }

  public void setScartaBody(org.openspcoop2.core.config.constants.StatoFunzionalita scartaBody) {
    this.scartaBody = scartaBody;
  }

  public void set_value_gestioneManifest(String value) {
    this.gestioneManifest = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_gestioneManifest() {
    if(this.gestioneManifest == null){
    	return null;
    }else{
    	return this.gestioneManifest.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getGestioneManifest() {
    return this.gestioneManifest;
  }

  public void setGestioneManifest(org.openspcoop2.core.config.constants.StatoFunzionalita gestioneManifest) {
    this.gestioneManifest = gestioneManifest;
  }

  public void set_value_stateless(String value) {
    this.stateless = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_stateless() {
    if(this.stateless == null){
    	return null;
    }else{
    	return this.stateless.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getStateless() {
    return this.stateless;
  }

  public void setStateless(org.openspcoop2.core.config.constants.StatoFunzionalita stateless) {
    this.stateless = stateless;
  }

  public java.lang.String getBehaviour() {
    return this.behaviour;
  }

  public void setBehaviour(java.lang.String behaviour) {
    this.behaviour = behaviour;
  }

  public java.lang.String getAutenticazione() {
    return this.autenticazione;
  }

  public void setAutenticazione(java.lang.String autenticazione) {
    this.autenticazione = autenticazione;
  }

  public void set_value_autenticazioneOpzionale(String value) {
    this.autenticazioneOpzionale = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_autenticazioneOpzionale() {
    if(this.autenticazioneOpzionale == null){
    	return null;
    }else{
    	return this.autenticazioneOpzionale.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getAutenticazioneOpzionale() {
    return this.autenticazioneOpzionale;
  }

  public void setAutenticazioneOpzionale(org.openspcoop2.core.config.constants.StatoFunzionalita autenticazioneOpzionale) {
    this.autenticazioneOpzionale = autenticazioneOpzionale;
  }

  public java.lang.String getAutorizzazione() {
    return this.autorizzazione;
  }

  public void setAutorizzazione(java.lang.String autorizzazione) {
    this.autorizzazione = autorizzazione;
  }

  public java.lang.String getAutorizzazioneContenuto() {
    return this.autorizzazioneContenuto;
  }

  public void setAutorizzazioneContenuto(java.lang.String autorizzazioneContenuto) {
    this.autorizzazioneContenuto = autorizzazioneContenuto;
  }

  public void set_value_ricercaPortaAzioneDelegata(String value) {
    this.ricercaPortaAzioneDelegata = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_ricercaPortaAzioneDelegata() {
    if(this.ricercaPortaAzioneDelegata == null){
    	return null;
    }else{
    	return this.ricercaPortaAzioneDelegata.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getRicercaPortaAzioneDelegata() {
    return this.ricercaPortaAzioneDelegata;
  }

  public void setRicercaPortaAzioneDelegata(org.openspcoop2.core.config.constants.StatoFunzionalita ricercaPortaAzioneDelegata) {
    this.ricercaPortaAzioneDelegata = ricercaPortaAzioneDelegata;
  }

  public void set_value_stato(String value) {
    this.stato = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_stato() {
    if(this.stato == null){
    	return null;
    }else{
    	return this.stato.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getStato() {
    return this.stato;
  }

  public void setStato(org.openspcoop2.core.config.constants.StatoFunzionalita stato) {
    this.stato = stato;
  }

  public java.util.Date getOraRegistrazione() {
    return this.oraRegistrazione;
  }

  public void setOraRegistrazione(java.util.Date oraRegistrazione) {
    this.oraRegistrazione = oraRegistrazione;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;

  private static org.openspcoop2.core.config.model.PortaApplicativaModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.config.PortaApplicativa.modelStaticInstance==null){
  			org.openspcoop2.core.config.PortaApplicativa.modelStaticInstance = new org.openspcoop2.core.config.model.PortaApplicativaModel();
	  }
  }
  public static org.openspcoop2.core.config.model.PortaApplicativaModel model(){
	  if(org.openspcoop2.core.config.PortaApplicativa.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.config.PortaApplicativa.modelStaticInstance;
  }


  @javax.xml.bind.annotation.XmlTransient
  protected IDPortaApplicativa oldIDPortaApplicativaForUpdate;

  @javax.xml.bind.annotation.XmlTransient
  protected List<Object> extendedInfo = new ArrayList<Object>();

  /**
   * @deprecated Use method getExtendedInfoList
   * @return List<Object>
  */
  @Deprecated
  public List<Object> getExtendedInfo() {
  	return this.extendedInfo;
  }

  /**
   * @deprecated Use method setExtendedInfoList
   * @param extendedInfo List<Object>
  */
  @Deprecated
  public void setExtendedInfo(List<Object> extendedInfo) {
  	this.extendedInfo=extendedInfo;
  }

  /**
   * @deprecated Use method sizeExtendedInfoList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeExtendedInfo() {
  	return this.extendedInfo.size();
  }

  @XmlElement(name="soggetto-virtuale",required=false,nillable=false)
  protected PortaApplicativaSoggettoVirtuale soggettoVirtuale;

  @XmlElement(name="servizio",required=true,nillable=false)
  protected PortaApplicativaServizio servizio;

  @XmlElement(name="azione",required=false,nillable=false)
  protected PortaApplicativaAzione azione;

  @XmlElement(name="servizio-applicativo",required=true,nillable=false)
  protected List<PortaApplicativaServizioApplicativo> servizioApplicativo = new ArrayList<PortaApplicativaServizioApplicativo>();

  /**
   * @deprecated Use method getServizioApplicativoList
   * @return List<PortaApplicativaServizioApplicativo>
  */
  @Deprecated
  public List<PortaApplicativaServizioApplicativo> getServizioApplicativo() {
  	return this.servizioApplicativo;
  }

  /**
   * @deprecated Use method setServizioApplicativoList
   * @param servizioApplicativo List<PortaApplicativaServizioApplicativo>
  */
  @Deprecated
  public void setServizioApplicativo(List<PortaApplicativaServizioApplicativo> servizioApplicativo) {
  	this.servizioApplicativo=servizioApplicativo;
  }

  /**
   * @deprecated Use method sizeServizioApplicativoList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeServizioApplicativo() {
  	return this.servizioApplicativo.size();
  }

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="xacml-policy",required=false,nillable=false)
  protected java.lang.String xacmlPolicy;

  @XmlElement(name="soggetti",required=false,nillable=false)
  protected PortaApplicativaAutorizzazioneSoggetti soggetti;

  @XmlElement(name="servizi-applicativi-autorizzati",required=false,nillable=false)
  protected PortaApplicativaAutorizzazioneServiziApplicativi serviziApplicativiAutorizzati;

  @XmlElement(name="ruoli",required=false,nillable=false)
  protected AutorizzazioneRuoli ruoli;

  @XmlElement(name="scope",required=false,nillable=false)
  protected AutorizzazioneScope scope;

  @XmlElement(name="gestione-token",required=false,nillable=false)
  protected GestioneToken gestioneToken;

  @XmlElement(name="proprieta",required=true,nillable=false)
  protected List<Proprieta> proprieta = new ArrayList<Proprieta>();

  /**
   * @deprecated Use method getProprietaList
   * @return List<Proprieta>
  */
  @Deprecated
  public List<Proprieta> getProprieta() {
  	return this.proprieta;
  }

  /**
   * @deprecated Use method setProprietaList
   * @param proprieta List<Proprieta>
  */
  @Deprecated
  public void setProprieta(List<Proprieta> proprieta) {
  	this.proprieta=proprieta;
  }

  /**
   * @deprecated Use method sizeProprietaList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeProprieta() {
  	return this.proprieta.size();
  }

  @XmlElement(name="mtom-processor",required=false,nillable=false)
  protected MtomProcessor mtomProcessor;

  @XmlElement(name="message-security",required=false,nillable=false)
  protected MessageSecurity messageSecurity;

  @XmlElement(name="validazione-contenuti-applicativi",required=false,nillable=false)
  protected ValidazioneContenutiApplicativi validazioneContenutiApplicativi;

  @XmlElement(name="correlazione-applicativa",required=false,nillable=false)
  protected CorrelazioneApplicativa correlazioneApplicativa;

  @XmlElement(name="correlazione-applicativa-risposta",required=false,nillable=false)
  protected CorrelazioneApplicativaRisposta correlazioneApplicativaRisposta;

  @XmlElement(name="dump",required=false,nillable=false)
  protected DumpConfigurazione dump;

  @XmlElement(name="tracciamento",required=false,nillable=false)
  protected PortaTracciamento tracciamento;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.Long idSoggetto;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.Long idAccordo;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.Long idPortType;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="tipo-soggetto-proprietario",required=false)
  protected java.lang.String tipoSoggettoProprietario;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome-soggetto-proprietario",required=false)
  protected java.lang.String nomeSoggettoProprietario;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="stato-message-security",required=false)
  protected java.lang.String statoMessageSecurity;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="descrizione",required=false)
  protected java.lang.String descrizione;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_ricevutaAsincronaSimmetrica;

  @XmlAttribute(name="ricevuta-asincrona-simmetrica",required=false)
  protected StatoFunzionalita ricevutaAsincronaSimmetrica = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato");

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_ricevutaAsincronaAsimmetrica;

  @XmlAttribute(name="ricevuta-asincrona-asimmetrica",required=false)
  protected StatoFunzionalita ricevutaAsincronaAsimmetrica = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato");

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="integrazione",required=false)
  protected java.lang.String integrazione;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_allegaBody;

  @XmlAttribute(name="allega-body",required=false)
  protected StatoFunzionalita allegaBody = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_scartaBody;

  @XmlAttribute(name="scarta-body",required=false)
  protected StatoFunzionalita scartaBody = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_gestioneManifest;

  @XmlAttribute(name="gestione-manifest",required=false)
  protected StatoFunzionalita gestioneManifest;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_stateless;

  @XmlAttribute(name="stateless",required=false)
  protected StatoFunzionalita stateless;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="behaviour",required=false)
  protected java.lang.String behaviour;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="autenticazione",required=false)
  protected java.lang.String autenticazione = "ssl";

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_autenticazioneOpzionale;

  @XmlAttribute(name="autenticazione-opzionale",required=false)
  protected StatoFunzionalita autenticazioneOpzionale = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="autorizzazione",required=false)
  protected java.lang.String autorizzazione = "authenticated";

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="autorizzazione-contenuto",required=false)
  protected java.lang.String autorizzazioneContenuto;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_ricercaPortaAzioneDelegata;

  @XmlAttribute(name="ricerca-porta-azione-delegata",required=false)
  protected StatoFunzionalita ricercaPortaAzioneDelegata = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_stato;

  @XmlAttribute(name="stato",required=false)
  protected StatoFunzionalita stato = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato");

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlAttribute(name="ora-registrazione",required=false)
  protected java.util.Date oraRegistrazione;

}
