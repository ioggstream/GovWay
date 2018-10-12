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
package org.openspcoop2.web.ctrlstat.servlet.pd;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.ConfigurazioneProtocollo;
import org.openspcoop2.core.config.CorrelazioneApplicativaElemento;
import org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento;
import org.openspcoop2.core.config.MessageSecurity;
import org.openspcoop2.core.config.MessageSecurityFlowParameter;
import org.openspcoop2.core.config.MtomProcessorFlowParameter;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataServizioApplicativo;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import org.openspcoop2.core.config.constants.MTOMProcessorType;
import org.openspcoop2.core.config.constants.PortaDelegataAzioneIdentificazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.core.config.constants.TipoAutorizzazione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.FunzionalitaProtocollo;
import org.openspcoop2.protocol.utils.PorteNamingUtils;
import org.openspcoop2.web.ctrlstat.core.AutorizzazioneUtilities;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationNotFound;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedListServlet;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni.ErogazioniCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriHelper;
import org.openspcoop2.web.ctrlstat.servlet.protocol_properties.ProtocolPropertiesUtilities;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * PorteDelegateHelper
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PorteDelegateHelper extends ConnettoriHelper {
	
	
	public PorteDelegateHelper(HttpServletRequest request, PageData pd, 
			HttpSession session) throws Exception {
		super(request, pd,  session);
	}
	
	public Vector<DataElement> addPorteDelegateToDati(TipoOperazione tipoOp, String idsogg,
			String nomePorta, Vector<DataElement> dati, String idPorta,
			String descr, String autenticazione,
			String autorizzazione, String soggid,
			String[] soggettiList, String[] soggettiListLabel, String sp,
			String tiposp, String patternErogatore, 
			String servid, String[] serviziList, String[] serviziListLabel,
			String servizio, String tiposervizio,String versioneServizio, String patternServizio,
			String modeaz, String azid, String[] azioniListLabel,
			String[] azioniList, String azione, String patternAzione,
			long totAzioni,  String stateless, String localForward, String paLocalForward, String ricsim,
			String ricasim, String statoValidazione, String tipoValidazione,
			int numCorrApp, String scadcorr, String gestBody,
			String gestManifest, String integrazione, String autenticazioneOpzionale, String autenticazioneCustom,
			String autorizzazioneCustom,String autorizzazioneAutenticati,String autorizzazioneRuoli,String autorizzazioneRuoliTipologia,String autorizzazioneContenuti, String idsogg2, String protocollo,
			int numSA, int numRuoli, String ruoloMatch, String statoMessageSecurity,String statoMTOM ,int numCorrelazioneReq , 
			int numCorrelazioneRes,String forceWsdlBased, String applicaMTOM,
			boolean riusoId,
			AccordoServizioParteSpecifica asps, AccordoServizioParteComune aspc,ServiceBinding serviceBinding,
			String statoPorta, boolean usataInConfigurazioni, boolean usataInConfigurazioneDefault,
			boolean ricercaPortaAzioneDelegata, String nomePortaDelegante, String gestioneToken, String[] gestioneTokenPolicyLabels, String[] gestioneTokenPolicyValues,
			String gestioneTokenPolicy, String gestioneTokenOpzionale, 
			String gestioneTokenValidazioneInput, String gestioneTokenIntrospection, String gestioneTokenUserInfo, String gestioneTokenForward,
			String autenticazioneTokenIssuer,String autenticazioneTokenClientId,String autenticazioneTokenSubject,String autenticazioneTokenUsername,String autenticazioneTokenEMail,
			String autorizzazione_token, String autorizzazione_tokenOptions,
			String autorizzazioneScope, int numScope, String autorizzazioneScopeMatch, BinaryParameter allegatoXacmlPolicy) throws Exception {

		boolean multitenant = this.pddCore.isMultitenant();

//		Boolean confPers = (Boolean) this.session.getAttribute(CostantiControlStation.SESSION_PARAMETRO_GESTIONE_CONFIGURAZIONI_PERSONALIZZATE);
		Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);
		
		// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
		Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, this.session);
		if(parentPD == null) parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;
		
		boolean isConfigurazione = parentPD.intValue() == PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE; 
		
		boolean datiInvocazione = false;
		boolean datiAltro = false;
		if(isConfigurazione) {
			if(usataInConfigurazioneDefault) {
				datiInvocazione = ServletUtils.isCheckBoxEnabled(this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_CONFIGURAZIONE_DATI_INVOCAZIONE));
			}
			datiAltro = ServletUtils.isCheckBoxEnabled(this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_CONFIGURAZIONE_ALTRO));
			
			DataElement de = new DataElement();
			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_CONFIGURAZIONE_DATI_INVOCAZIONE);
			de.setType(DataElementType.HIDDEN);
			de.setValue(datiInvocazione+"");
			dati.addElement(de);
			
			de = new DataElement();
			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_CONFIGURAZIONE_ALTRO);
			de.setType(DataElementType.HIDDEN);
			de.setValue(datiAltro+"");
			dati.addElement(de);
		}
		

		int alternativeSize = 80;
		
		@SuppressWarnings("unused")
		Parameter pIdSogg = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg);
		@SuppressWarnings("unused")
		Parameter pIdPorta = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, idPorta);
		String idAsps = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
		if(idAsps == null) idAsps = "";
		@SuppressWarnings("unused")
		Parameter pIdAsps = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS, idAsps);
		String idFruizione = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE);
		if(idFruizione == null) idFruizione = "";
		@SuppressWarnings("unused")
		Parameter pIdFruizione = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE, idFruizione);
		
		DataElement de = null;
		
		de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
		de.setValue(idsogg2);
		de.setType(DataElementType.HIDDEN);
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_ID);
		de.setValue(idPorta);
		de.setType(DataElementType.HIDDEN);
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
		dati.addElement(de);
		
		// servicebinding hidden
		dati.add(this.getServiceBindingDataElement(serviceBinding));
		
		// *************** Dati Generali: Nome/Descrizione *********************
		
		de = new DataElement();
		if(datiInvocazione) {
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_TITOLO_PORTE_DELEGATE_DATI_INVOCAZIONE);
		}
		else {
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_TITOLO_PORTE_DELEGATE_DATI_GENERALI);
		}
		de.setType(DataElementType.TITLE);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME);
		de.setValue(nomePorta);
		
		if(isConfigurazione) {
			de.setType(DataElementType.HIDDEN);
		}
		else {				
			de.setType(DataElementType.TEXT_EDIT);
			de.setRequired(true);
		}
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_PORTA);
		de.setSize(alternativeSize);
		dati.addElement(de);
		
		if(datiInvocazione) {
			
			ConfigurazioneProtocollo configProt = this.confCore.getConfigurazioneProtocollo(protocollo);
			
			boolean useInterfaceNameInInvocationURL = this.useInterfaceNameInSubscriptionInvocationURL(protocollo, serviceBinding);
			
			String prefix = configProt.getUrlInvocazioneServizioPD();
			prefix = prefix.trim();
			if(useInterfaceNameInInvocationURL) {
				if(prefix.endsWith("/")==false) {
					prefix = prefix + "/";
				}
			}
			
			String urlInvocazione = prefix;
			if(useInterfaceNameInInvocationURL) {
				PorteNamingUtils utils = new PorteNamingUtils(ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo));
				urlInvocazione = urlInvocazione + utils.normalizePD(nomePorta);
			}
			
			de = new DataElement();
			if(ServiceBinding.SOAP.equals(serviceBinding)) {
				de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_URL_INVOCAZIONE);
			}
			else {
				de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_BASE_URL_INVOCAZIONE);
			}
			de.setValue(urlInvocazione);
			de.setType(DataElementType.TEXT);
			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_PORTA+"___LABEL");
			dati.addElement(de);
		}

		de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_DESCRIZIONE);
		de.setValue(descr);
		if(isConfigurazione) {
			de.setType(DataElementType.HIDDEN);
		} else {
			de.setType(DataElementType.TEXT_EDIT);
		}
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_DESCRIZIONE);
		de.setSize(alternativeSize);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_STATO_PORTA);
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_STATO_PORTA);
		if(!isConfigurazione || datiAltro) {
			List<String> statoValues = new ArrayList<>();
			statoValues.add(CostantiConfigurazione.ABILITATO.toString());
			statoValues.add(CostantiConfigurazione.DISABILITATO.toString());
			de.setValues(statoValues);
			if(statoPorta==null || "".equals(statoPorta)){
				statoPorta = CostantiConfigurazione.ABILITATO.toString();
			}
			de.setSelected(statoPorta);
			de.setType(DataElementType.SELECT);
		}
		else {
			de.setValue(statoPorta);
			de.setType(DataElementType.HIDDEN);
		}
		dati.addElement(de);

		
		// *************** Dati Servizio *********************
		
		if(!isConfigurazione) {
			de = new DataElement();
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_TITOLO_PORTE_DELEGATE_DATI_SERVIZIO);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
		}
		
		
		
		// *************** Soggetto Erogatore *********************
		
		if(!isConfigurazione) {
			de = new DataElement();
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SOGGETTO_EROGATORE);
			de.setType(DataElementType.SUBTITLE);
			dati.addElement(de);
		}

		de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME);
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SOGGETTO_ID);
		if(!usataInConfigurazioni) {
			de.setPostBack(true);
			de.setType(DataElementType.SELECT);
			de.setValues(soggettiList);
			de.setLabels(soggettiListLabel);
			de.setSelected(soggid);
		} else {
			de.setType(DataElementType.HIDDEN);
			de.setValue(soggid); 
			dati.addElement(de);
			
			if(this.isModalitaCompleta()) {
				de = new DataElement();
				de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME);
				de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_SOGGETTO);
				de.setType(DataElementType.TEXT);
				String tipoSoggetto = soggid.split("/")[0];
				String nomeSoggetto = soggid.split("/")[1];
				de.setValue(this.getLabelNomeSoggetto(protocollo, tipoSoggetto, nomeSoggetto));
			}
		}
		dati.addElement(de);


		// *************** Servizio *********************
		
		if(!isConfigurazione) {
			de = new DataElement();
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SERVIZIO);
			de.setType(DataElementType.SUBTITLE);
			dati.addElement(de);
		}

		de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME);
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SERVIZIO_ID);
		if(!usataInConfigurazioni) {
			de.setPostBack(true);
			de.setType(DataElementType.SELECT);
			de.setValues(serviziList);
			de.setLabels(serviziListLabel);
			de.setSelected(servid);
		} else {
			de.setType(DataElementType.HIDDEN);
			de.setValue(servid); 
			dati.addElement(de);
			
			if(this.isModalitaCompleta()) {
				de = new DataElement();
				de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME);
				de.setType(DataElementType.TEXT);
				for (int i = 0; i < serviziList.length; i++) {
					if(serviziList[i]!=null && serviziList[i].equals(servid)){
						de.setValue(serviziListLabel[i]);
						break;
					}
				}
			}
		}
		dati.addElement(de);
		
		// *************** Azione *********************
		
		List<PortaDelegataAzioneIdentificazione> allSubscriptionIdentificationResourceModes = getModalitaIdentificazionePorta(protocollo, serviceBinding);
		
		
		// se servizio register-input e azioneList==null e
		// mode_azione=register-input allora nn c'e' azione
		List<String> azTmp = new ArrayList<>();
		String[] tipoModeAzione = null;
		String[] tipoModeAzioneLabel = null;
		
		if(tipoOp.equals(TipoOperazione.ADD) || (tipoOp.equals(TipoOperazione.CHANGE) && !usataInConfigurazioni))
			azTmp.add(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT);
		
		if(allSubscriptionIdentificationResourceModes != null && allSubscriptionIdentificationResourceModes.size() >0) {
			
			
			for (int i = 0; i < allSubscriptionIdentificationResourceModes.size(); i++) {
				PortaDelegataAzioneIdentificazione pdAi = allSubscriptionIdentificationResourceModes.get(i);
				azTmp.add(pdAi.toString());
			}
			
			Collections.sort(azTmp);
						
			tipoModeAzione = new String [azTmp.size()];
			tipoModeAzioneLabel = new String [azTmp.size()];
			
			for (int i = 0; i < azTmp.size(); i++) {
				String azMod = azTmp.get(i);
				tipoModeAzione[i] = azMod;
				tipoModeAzioneLabel[i] = this.getPortaDelegataAzioneIdentificazioneLabel(azMod);
			}
		}
		
		boolean disableSaveButtonForDatiInvocazione = true;
		
		if(!isConfigurazione || datiInvocazione) {
			de = new DataElement();
			if(datiInvocazione) {
				if(ServiceBinding.REST.equals(serviceBinding)) {
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTA_RISORSA_MODALITA);
				}
				else {
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTA_AZIONE_MODALITA);
				}
			}
			else {
				if(ServiceBinding.REST.equals(serviceBinding)) {
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_RISORSA);
				}
				else {
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_AZIONE);
				}
			}
			de.setType(DataElementType.SUBTITLE);
			dati.addElement(de);
		}

		boolean viewOnlyModeDatiAzione = datiInvocazione && modeaz!=null && !"".equals(modeaz) && this.isModalitaStandard() &&
				!azTmp.contains(modeaz);
		// se true viewOnlyModeDatiAzione e' stato usato un valore modificato in avanzato e non supportato in standard
		
		// Fix per standard visualizzazione anche su soap
		// fornisco interfaceMode direttamente come per rest se cmq è abilitato sia il riconoscimento per url che anche per forceInterface.
		boolean visualizzazioneSpecialeSoapPerEssereUgualeARest = false;
		if(this.isModalitaStandard() && ServiceBinding.SOAP.equals(serviceBinding) &&
				PortaDelegataAzioneIdentificazione.URL_BASED.getValue().equals(modeaz) &&
				(ServletUtils.isCheckBoxEnabled(forceWsdlBased) || CostantiRegistroServizi.ABILITATO.equals(forceWsdlBased)) 
				&& (aspc.getByteWsdlConcettuale()!=null)
				) {
			visualizzazioneSpecialeSoapPerEssereUgualeARest = true;
		}
		
		de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MODALITA_IDENTIFICAZIONE);
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_AZIONE);
		if(!usataInConfigurazioni || datiInvocazione) {
			if(viewOnlyModeDatiAzione || (tipoModeAzione!=null && tipoModeAzione.length==1) || visualizzazioneSpecialeSoapPerEssereUgualeARest) {
				de.setType(DataElementType.HIDDEN);
				de.setValue(modeaz);
				dati.addElement(de);
				
				de = new DataElement();
				de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MODALITA_IDENTIFICAZIONE);
				de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_AZIONE+"__LABEL");
				de.setType(DataElementType.TEXT);
				if(visualizzazioneSpecialeSoapPerEssereUgualeARest) {
					de.setValue(this.getPortaDelegataAzioneIdentificazioneLabel(PortaDelegataAzioneIdentificazione.INTERFACE_BASED.getValue()));
				}
				else {
					de.setValue(this.getPortaDelegataAzioneIdentificazioneLabel(modeaz));
				}
			}
			else {
				de.setType(DataElementType.SELECT);
				de.setValues(tipoModeAzione);
				de.setLabels(tipoModeAzioneLabel); 
				de.setSelected(modeaz);
				de.setPostBack(true);
				
				disableSaveButtonForDatiInvocazione = false;
			}
		} else {
			de.setType(DataElementType.HIDDEN);
			de.setValue(modeaz); 
		}
		
		dati.addElement(de);
		
		boolean addHiddenAzione = false;
		
		if(!usataInConfigurazioni || datiInvocazione) {
	
			if(!usataInConfigurazioni && PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_DELEGATED_BY.equals(modeaz)) {
				
				// azione non modificabile, metto la lista delle azioni
				de = new DataElement();
				de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME);
				de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AZIONE);
				de.setValue(azione);
				dati.addElement(de);
				
				addHiddenAzione = true;
				
			}
			else {
			
				if ( (!visualizzazioneSpecialeSoapPerEssereUgualeARest) &&
						(modeaz != null) && 
						modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
					de = new DataElement();
					de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME);
					de.setType(DataElementType.SELECT);
					de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AZIONE_ID);
					de.setValues(azioniList);
					de.setLabels(azioniListLabel);
					de.setSelected(azid);
					dati.addElement(de);
					
					disableSaveButtonForDatiInvocazione = false;
					
				} else {
		
					de = new DataElement();
					if ((modeaz != null) && (modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_URL_BASED) 
							|| modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_CONTENT_BASED))) {
						de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PATTERN);
						de.setValue(patternAzione);
						de.setRequired(true);
					}
					else if ((modeaz != null) && (modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_HEADER_BASED))) {
						de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME);
						de.setValue(patternAzione);
						de.setRequired(true);
					} 
					else {
						de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME);
						de.setValue(azione);
					}
		
					if ( (!visualizzazioneSpecialeSoapPerEssereUgualeARest) &&
							!PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_INPUT_BASED.equals(modeaz) && 
							!PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_SOAP_ACTION_BASED.equals(modeaz) && 
							!PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_INTERFACE_BASED.equals(modeaz) ){
						if(viewOnlyModeDatiAzione) {
							de.setType(DataElementType.TEXT);
							de.setRequired(false);
						}
						else {
							if(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_URL_BASED.equals(modeaz) ||
									PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_CONTENT_BASED.equals(modeaz) ) {
								de.setType(DataElementType.TEXT_AREA);
							}
							else {
								de.setType(DataElementType.TEXT_EDIT);
							}
							disableSaveButtonForDatiInvocazione = false;
						}
					}else
						de.setType(DataElementType.HIDDEN);
					de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AZIONE);
					de.setSize(alternativeSize);
					dati.addElement(de);
				}
		
				// se non e' selezionata la modalita userInput / wsdlbased / registerInput faccio vedere il check box forceWsdlbased
				de = new DataElement();
				de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_FORCE_INTERFACE_BASED);
				if( (!visualizzazioneSpecialeSoapPerEssereUgualeARest) &&
						modeaz!= null && 
						(
							!modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT) &&
							!modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_INTERFACE_BASED)
						)
					){
		
					if(viewOnlyModeDatiAzione) {
						de.setType(DataElementType.TEXT);
						if( ServletUtils.isCheckBoxEnabled(forceWsdlBased) || CostantiRegistroServizi.ABILITATO.equals(forceWsdlBased) ){
							de.setValue(CostantiConfigurazione.ABILITATO.getValue());
						}
						else {
							de.setValue(CostantiConfigurazione.DISABILITATO.getValue());
						}
					}
					else {
						de.setType(DataElementType.CHECKBOX);
						if( ServletUtils.isCheckBoxEnabled(forceWsdlBased) || CostantiRegistroServizi.ABILITATO.equals(forceWsdlBased) ){
							de.setSelected(true);
						}
						disableSaveButtonForDatiInvocazione = false;
					}
				}
				else{
					de.setType(DataElementType.HIDDEN);
					de.setValue(forceWsdlBased);
				}
				de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_FORCE_INTERFACE_BASED);
				dati.addElement(de);
				
				if( (!visualizzazioneSpecialeSoapPerEssereUgualeARest) &&
						modeaz!= null && 
						(
							!modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT) &&
							!modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_INTERFACE_BASED)
						)
				){
					de = new DataElement();
					de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_LIST_AZIONI_READ_ONLY);
					de.setLabel(this.getLabelAzioni(serviceBinding));
					Map<String,String> azioni = this.porteDelegateCore.getAzioniConLabel(asps, aspc, false, true, new ArrayList<String>());
					StringBuffer bf = new StringBuffer();
					for (String az : azioni.keySet()) {
						if(bf.length()>0) {
							bf.append("\n");
						}
						bf.append(azioni.get(az));
					}
					de.setType(DataElementType.TEXT_AREA_NO_EDIT);
					if(azioni.size()<=5) {
						de.setRows(azioni.size());
					}
					else {
						de.setRows(5);
					}
					de.setValue(bf.toString());
					dati.addElement(de);
				}
				
			}
		
		} else {
			
			addHiddenAzione = true;
			
		}
		
		if(addHiddenAzione) {
			
			de = new DataElement();
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME);
			de.setType(DataElementType.HIDDEN);
			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AZIONE_ID);
			de.setValue(azid);
			dati.addElement(de);
			
			if(this.isModalitaCompleta()) {
				DataElement deLabel = new DataElement();
				deLabel.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MODALITA_IDENTIFICAZIONE);
				deLabel.setType(DataElementType.TEXT);
				deLabel.setValue(modeaz);
				dati.addElement(deLabel);
			}
			
			de = new DataElement();
			if ((modeaz != null) && (modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_URL_BASED) 
					|| modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_CONTENT_BASED))) {
				de.setValue(patternAzione);
			} 
			else if ((modeaz != null) && modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_HEADER_BASED)
					) {
				de.setValue(patternAzione);
			} 
			else {
				de.setValue(azione);
			}
			de.setType(DataElementType.HIDDEN);
			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AZIONE);
			dati.addElement(de);
			
			if(this.isModalitaCompleta()) {
				DataElement deLabel = new DataElement();
				deLabel.setType(DataElementType.TEXT);
				if ((modeaz != null) && (modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_URL_BASED) 
						|| modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_CONTENT_BASED))) {
					deLabel.setType(DataElementType.TEXT_AREA_NO_EDIT);
					deLabel.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PATTERN);
					deLabel.setValue(patternAzione);
				} 
				else if ((modeaz != null) && modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_HEADER_BASED)
						) {
					deLabel.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME);
					deLabel.setValue(patternAzione);
				} 
				else {
					deLabel.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME);
					deLabel.setValue(azione);
				}
				dati.addElement(deLabel);
			}
			
			if(this.isModalitaCompleta()) {
				if ((modeaz != null) && (modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_DELEGATED_BY))){
					de = new DataElement();
					de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_RICERCA_PORTA_AZIONE_DELEGATA);
					de.setType(DataElementType.TEXT);
					de.setValue(nomePortaDelegante);
					dati.addElement(de);
				}
				else {
					DataElement deLabel = new DataElement();
					deLabel.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_RICERCA_PORTA_AZIONE_DELEGATA);
					deLabel.setType(DataElementType.TEXT);
					deLabel.setValue(ricercaPortaAzioneDelegata ? CostantiConfigurazione.ABILITATO.getValue() : CostantiConfigurazione.DISABILITATO.getValue() );
					dati.addElement(deLabel);
				}
			}
			
			de = new DataElement();
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_FORCE_INTERFACE_BASED);
			de.setType(DataElementType.HIDDEN);
			de.setValue(forceWsdlBased);
			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_FORCE_INTERFACE_BASED);
			dati.addElement(de);
			
			if(this.isModalitaCompleta()) {
				if( modeaz!= null && (
						!modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT) &&
						!modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_INTERFACE_BASED))
				){
					DataElement deLabel = new DataElement();
					deLabel.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_FORCE_INTERFACE_BASED);
					deLabel.setType(DataElementType.TEXT);
					deLabel.setValue(ServletUtils.isCheckBoxEnabled(forceWsdlBased) ? CostantiConfigurazione.ABILITATO.getValue() : CostantiConfigurazione.DISABILITATO.getValue() );
					dati.addElement(deLabel);
				}
			}
			
		}
		
		if(datiInvocazione && disableSaveButtonForDatiInvocazione) {
			this.pd.disableEditMode();
		}
		
		
		
		
		
		// *************** Controllo degli Accessi *********************
		
		
		
		// Pintori 11/12/2017 Gestione Accessi spostata nella servlet PorteDelegateControlloAccessi,  in ADD devo mostrare comunque la form.
		if(!tipoOp.equals(TipoOperazione.ADD)) {
			if(!isConfigurazione) {
				
				// Il link richiede ulteriori parametri.
				
//				this.controlloAccessi(dati);
//				// controllo accessi
//				de = new DataElement();
//				de.setType(DataElementType.LINK);
//				de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CONTROLLO_ACCESSI, pIdSogg, pIdPorta, pIdAsps, pIdFruizione);
//				String statoControlloAccessi = this.getLabelStatoControlloAccessi(gestioneToken,autenticazione, autenticazioneOpzionale, autenticazioneCustom, autorizzazione, autorizzazioneContenuti,autorizzazioneCustom);
//				ServletUtils.setDataElementCustomLabel(de, PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONTROLLO_ACCESSI, statoControlloAccessi);
//				dati.addElement(de);
			}
		}else {
			
			boolean isSupportatoAutenticazioneSoggetti = true; // sempre nelle porte delegate
			Boolean confPers = (Boolean) this.session.getAttribute(CostantiControlStation.SESSION_PARAMETRO_GESTIONE_CONFIGURAZIONI_PERSONALIZZATE);
			
			this.controlloAccessiGestioneToken(dati, tipoOp, gestioneToken, gestioneTokenPolicyLabels, gestioneTokenPolicyValues, 
					gestioneTokenPolicy, gestioneTokenOpzionale,
					gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenForward, null,true);
			
			this.controlloAccessiAutenticazione(dati, tipoOp, autenticazione, autenticazioneCustom, autenticazioneOpzionale, confPers , isSupportatoAutenticazioneSoggetti,true,
					gestioneToken, gestioneTokenPolicy, autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail);
			
			String urlAutorizzazioneAutenticati = null;
			String urlAutorizzazioneRuoli = null;
			String urlAutorizzazioneScope = null;
			String servletChiamante = PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_ADD;
			
			this.controlloAccessiAutorizzazione(dati, tipoOp, servletChiamante,null,
					autenticazione, autorizzazione, autorizzazioneCustom, 
					autorizzazioneAutenticati, urlAutorizzazioneAutenticati, numSA, null, null,
					autorizzazioneRuoli,  urlAutorizzazioneRuoli, numRuoli, null,
					autorizzazioneRuoliTipologia, ruoloMatch,
					confPers, isSupportatoAutenticazioneSoggetti, contaListe, true, false,autorizzazioneScope,urlAutorizzazioneScope,numScope,null,autorizzazioneScopeMatch,
					gestioneToken, gestioneTokenPolicy, 
					autorizzazione_token, autorizzazione_tokenOptions,allegatoXacmlPolicy,
					null, 0);
			
				
			this.controlloAccessiAutorizzazioneContenuti(dati, autorizzazioneContenuti);
		}
		
		
		// *************** Validazione Contenuti *********************
		
		
		// Pintori 08/02/2018 Validazione Contenuti spostata nella servlet PorteDelegateValidazione, in ADD devo mostrare comunque la form.
		
		if(!tipoOp.equals(TipoOperazione.ADD)) {
			if(!isConfigurazione) {
				
				// Il link richiede ulteriori parametri.
			
//				de = new DataElement();
//				de.setType(DataElementType.TITLE);
//				de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI);
//				dati.addElement(de);
//				
//				// validazione contenuti
//				de = new DataElement();
//				de.setType(DataElementType.LINK);
//				de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI, pIdSogg, pIdPorta, pIdAsps, pIdFruizione);
//				ServletUtils.setDataElementCustomLabel(de, PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI, statoValidazione);
//				dati.addElement(de);
			}
		} else {
				this.validazioneContenuti(tipoOp, dati, true, statoValidazione, tipoValidazione, applicaMTOM,
						serviceBinding, aspc.getFormatoSpecifica());
		}
		
		
		// *************** Integrazione *********************
		
		Vector<DataElement> deIntegrazione = new Vector<DataElement>();
		
		if (tipoOp.equals(TipoOperazione.CHANGE)) {

			de = new DataElement();
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_METADATI);
			de.setValue(integrazione);
			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_INTEGRAZIONE);
			de.setSize(alternativeSize);
			if(this.isModalitaStandard() || (isConfigurazione && !datiAltro)){
				de.setType(DataElementType.HIDDEN);
				dati.addElement(de);
			}else{
				de.setType(DataElementType.TEXT_EDIT);
				deIntegrazione.addElement(de);
			}
			
		}

		String[] tipoStateless = { PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_STATELESS_DEFAULT,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_STATELESS_ABILITATO, 
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_STATELESS_DISABILITATO };
		de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_STATELESS);
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_STATELESS);
		if(!this.core.isShowJ2eeOptions() || (isConfigurazione && !datiAltro)){
			de.setType(DataElementType.HIDDEN);
			de.setValue(stateless);
			dati.addElement(de);
		}else {
			de.setType(DataElementType.SELECT);
			de.setValues(tipoStateless);
			de.setSelected(stateless);
			deIntegrazione.addElement(de);
		}
		

		// LocalForward
		boolean localForwardShow = true;
		Soggetto soggettoErogatoreLocalForward  = null;
		try{
			if(soggid!=null && soggid.contains("/")){
				soggettoErogatoreLocalForward = this.soggettiCore.getSoggettoRegistro(new IDSoggetto(soggid.split("/")[0], soggid.split("/")[1]));
			}
			else if(soggid!=null && !"".equals(soggid)){
				soggettoErogatoreLocalForward = this.soggettiCore.getSoggettoRegistro(Long.parseLong(soggid));
			}
		}catch(DriverRegistroServiziNotFound dNot){}
		
		if(soggettoErogatoreLocalForward!=null){
			if(this.pddCore.isRegistroServiziLocale()){
				if(soggettoErogatoreLocalForward.getPortaDominio()!=null){
					try{
						if(this.pddCore.getPdDControlStation(soggettoErogatoreLocalForward.getPortaDominio()).getTipo().equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_ESTERNO)){
							localForwardShow = false;
						}
					}catch(DriverControlStationNotFound dNot){}
				}
			}
			else{
				// se il soggetto erogatore non e' locale non puo' esistere il localForward.
				// Comunque sia lo devo far vedere lo stesso poiche' magari e' una configurazione errata indicata nella console centrale.
//				if(soggettoErogatoreLocalForward.getTipo()!=null && soggettoErogatoreLocalForward.getNome()!=null){
//					try{
//						IDSoggetto idSoggetto = new IDSoggetto(soggettoErogatoreLocalForward.getTipo(), soggettoErogatoreLocalForward.getNome());
//						if(!this.soggettiCore.existsSoggetto(idSoggetto)){
//							localForwardShow = false; 
//						}
//					}catch(Exception dNot){
//						this.log.error(dNot.getMessage(), dNot);
//					}
//				}
			}
		}			
		
		if(deIntegrazione.size()>0){
			
			if(!isConfigurazione || datiAltro) {
				de = new DataElement();
				de.setType(DataElementType.TITLE);
				de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_INTEGRAZIONE);
				dati.addElement(de);
				
				for (int i = 0; i < deIntegrazione.size(); i++) {
					dati.addElement(deIntegrazione.get(i));
				}
			}
		}
		
		
		boolean localForwardDisable = !this.porteDelegateCore.isShowPortaDelegataLocalForward() || this.isModalitaStandard() || !multitenant || localForwardShow==false || (isConfigurazione && !datiAltro);
		
		if(!localForwardDisable) {
			de = new DataElement();
			de.setType(DataElementType.TITLE);
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_LOCAL_FORWARD);
			dati.addElement(de);
		}
		
		de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_LOCAL_FORWARD_STATO);
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_LOCAL_FORWARD);
		de.setSize(alternativeSize);
		if (localForwardDisable) {
			de.setType(DataElementType.HIDDEN);
		}else{
			String[] tipoLocalForward = { PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_LOCAL_FORWARD_ABILITATO,
					PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_LOCAL_FORWARD_DISABILITATO };
			de.setType(DataElementType.SELECT);
			de.setValues(tipoLocalForward);
			de.setSelected(localForward);
			de.setPostBack(true);
		}
		de.setValue(localForward);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_LOCAL_FORWARD_PA);
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_LOCAL_FORWARD_PA);
		de.setValue(paLocalForward);
		if (localForwardDisable || !PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_LOCAL_FORWARD_ABILITATO.equals(localForward)) {
			de.setType(DataElementType.HIDDEN);
		}
		else {
			de.setType(DataElementType.TEXT_EDIT);
		}
		dati.addElement(de);
		
		
		
		
		// *************** CorrelazioneApplicativa *********************
		boolean show = false; // aggiunto link in trattamento messaggio		
		if (show &&
				!idsogg.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_NEW_ID) 
				&& !idPorta.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_NEW_ID)) {

			de = new DataElement();
			de.setType(DataElementType.TITLE);
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA);
			dati.addElement(de);
			
			if (tipoOp == TipoOperazione.CHANGE) {

				if (riusoId && numCorrApp != 0) {
					de = new DataElement();
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_SCADENZA_CORRELAZIONE_APPLICATIVA_LABEL);
					de.setNote(CostantiControlStation.LABEL_PARAMETRO_SCADENZA_CORRELAZIONE_APPLICATIVA_NOTE);
					de.setValue(scadcorr);
					de.setType(DataElementType.TEXT_EDIT);
					de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SCADENZA_CORRELAZIONE_APPLICATIVA);
					de.setSize(alternativeSize);
					dati.addElement(de);
				}
			}
			
			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(
					PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_REQUEST_LIST,
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg),
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, ""+idPorta),
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME,nomePorta));

			if (contaListe) {
				ServletUtils.setDataElementCustomLabel(de,PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_RICHIESTA,Long.valueOf(numCorrelazioneReq));
			} else
				ServletUtils.setDataElementCustomLabel(de,PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_RICHIESTA);

			dati.addElement(de);

			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(
					PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_RESPONSE_LIST,
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg),
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, ""+idPorta),
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME,nomePorta));

			if (contaListe) {
				ServletUtils.setDataElementCustomLabel(de,PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_RISPOSTA,Long.valueOf(numCorrelazioneRes));
			} else
				ServletUtils.setDataElementCustomLabel(de,PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_RISPOSTA);

			dati.addElement(de);
		}
		
		
		
		
		
		
		
		
		
		// *************** Gestione Messaggio *********************
		
		if (!idsogg.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_NEW_ID) 
				&& !idPorta.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_NEW_ID)) {
			if(!isConfigurazione) {
				
				// Il link richiede ulteriori parametri.
				
//				de = new DataElement();
//				de.setType(DataElementType.TITLE);
//				de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_GESTIONE_MESSAGGIO);
//				dati.addElement(de);
//				
//				de = new DataElement();
//				de.setType(DataElementType.LINK);
//				de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA +"?" + 
//						PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO + "=" + idsogg + "&"
//						+ PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID + "=" + idPorta+ "&"
//						+ PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME + "=" + nomePorta);
//				String statoCorrelazioneApplicativa = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_DISABILITATA;
//				if(numCorrelazioneReq>0 || numCorrelazioneRes>0){
//					statoCorrelazioneApplicativa = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_ABILITATA;
//				}
//				ServletUtils.setDataElementCustomLabel(de, PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA, statoCorrelazioneApplicativa);
//				dati.addElement(de);
//				
//				de = new DataElement();
//				de.setType(DataElementType.LINK);
//				de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MESSAGE_SECURITY +"?" + 
//						PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO + "=" + idsogg + "&"
//						+ PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID + "=" + idPorta);
//				ServletUtils.setDataElementCustomLabel(de, PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY, statoMessageSecurity);
//				dati.addElement(de);
//	
//				//if(InterfaceType.AVANZATA.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())){
//				de = new DataElement();
//				de.setType(DataElementType.LINK);
//				de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MTOM +"?" + 
//						PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO + "=" + idsogg + "&"
//						+ PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID + "=" + idPorta);
//				ServletUtils.setDataElementCustomLabel(de, PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM, statoMTOM);
//				dati.addElement(de);
//				//}
			}	
		}
		
		
		// *************** Asincroni *********************
		
		boolean supportoAsincroni = this.core.isProfiloDiCollaborazioneAsincronoSupportatoDalProtocollo(protocollo,serviceBinding);
		
		if (this.isModalitaStandard() || !supportoAsincroni || datiInvocazione) {

			de = new DataElement();
			de.setType(DataElementType.HIDDEN);
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_GESTIONE_ASINCRONA);
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_RICEVUTA_ASINCRONA_SIMMETRICA);
			de.setType(DataElementType.HIDDEN);
			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_RICEVUTA_ASINCRONA_SIMMETRICA);
			de.setValue(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_RICEVUTA_ASINCRONA_SIMMETRICA_ABILITATO);
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_RICEVUTA_ASINCRONA_ASIMMETRICA);
			de.setType(DataElementType.HIDDEN);
			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_RICEVUTA_ASINCRONA_ASIMMETRICA);
			de.setValue(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_RICEVUTA_ASINCRONA_ASIMMETRICA_ABILITATO);
			dati.addElement(de);
		} else {

			de = new DataElement();
			de.setType(DataElementType.TITLE);
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_GESTIONE_ASINCRONA);
			dati.addElement(de);

			String[] tipoRicsim = {PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_RICEVUTA_ASINCRONA_SIMMETRICA_ABILITATO
					, PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_RICEVUTA_ASINCRONA_SIMMETRICA_DISABILITATO};
			de = new DataElement();
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_RICEVUTA_ASINCRONA_SIMMETRICA);
			de.setType(DataElementType.SELECT);
			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_RICEVUTA_ASINCRONA_SIMMETRICA);
			de.setValues(tipoRicsim);
			de.setSelected(ricsim);
			dati.addElement(de);

			String[] tipoRicasim = { PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_RICEVUTA_ASINCRONA_ASIMMETRICA_ABILITATO
					, PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_RICEVUTA_ASINCRONA_ASIMMETRICA_DISABILITATO};
			de = new DataElement();
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_RICEVUTA_ASINCRONA_ASIMMETRICA);
			de.setType(DataElementType.SELECT);
			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_RICEVUTA_ASINCRONA_ASIMMETRICA);
			de.setValues(tipoRicasim);
			de.setSelected(ricasim);
			dati.addElement(de);
		}

		
		
		
		
		
		// ***************  SOAP With Attachments *********************

		if (this.isModalitaAvanzata() && (!isConfigurazione || datiAltro) && ServiceBinding.SOAP.equals(serviceBinding) ) {
		
			de = new DataElement();
			de.setType(DataElementType.TITLE);
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SOAP_WITH_ATTACHMENTS);
			dati.addElement(de);
	
			String[] tipoGestBody = {PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_GEST_BODY_NONE ,
					PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_GEST_BODY_ALLEGA, 
					PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_GEST_BODY_SCARTA };
			de = new DataElement();
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_GESTIONE_BODY);
			de.setType(DataElementType.SELECT);
			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_GESTIONE_BODY);
			de.setValues(tipoGestBody);
			de.setSelected(gestBody);
			dati.addElement(de);
	
			String[] tipoGestManifest = {
					PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_GEST_MANIFEST_DEFAULT,
					PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_GEST_MANIFEST_ABILITATO,
					PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_GEST_MANIFEST_DISABILITATO };
			de = new DataElement();
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_GESTIONE_MANIFEST);
			if(isFunzionalitaProtocolloSupportataDalProtocollo(protocollo, serviceBinding, FunzionalitaProtocollo.MANIFEST_ATTACHMENTS)){
				de.setType(DataElementType.SELECT);
				de.setValues(tipoGestManifest);
				de.setSelected(gestManifest);
			}else {
				de.setType(DataElementType.HIDDEN);
				de.setValue(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_GEST_MANIFEST_DISABILITATO );
			}
			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_GESTIONE_MANIFEST);
			dati.addElement(de);
		
		}
	
//		if(configurazioneStandardNonApplicabile){
//			this.pd.setMessage(CostantiControlStation.LABEL_CONFIGURAZIONE_IMPOSTATA_MODALITA_AVANZATA_LONG_MESSAGE, Costanti.MESSAGE_TYPE_INFO);
//			this.pd.disableEditMode();
//		}
		
		return dati;
	}

	public boolean isFunzionalitaProtocolloSupportataDalProtocollo(String protocollo, ServiceBinding serviceBinding,FunzionalitaProtocollo funzionalitaProtocollo)
			throws DriverRegistroServiziNotFound, DriverRegistroServiziException, DriverConfigurazioneException {
		if(serviceBinding == null) {
			List<ServiceBinding> serviceBindingListProtocollo = this.core.getServiceBindingListProtocollo(protocollo);
			
			boolean supportato = true;
			if(serviceBindingListProtocollo != null && serviceBindingListProtocollo.size() > 0) {
				for (ServiceBinding serviceBinding2 : serviceBindingListProtocollo) {
					boolean supportatoTmp = this.core.isFunzionalitaProtocolloSupportataDalProtocollo(protocollo, serviceBinding2, funzionalitaProtocollo);
					supportato = supportato || supportatoTmp;
				}
			}
			return supportato;
		} else {
			return this.core.isFunzionalitaProtocolloSupportataDalProtocollo(protocollo, serviceBinding, funzionalitaProtocollo);
		}
	}

	public List<PortaDelegataAzioneIdentificazione> getModalitaIdentificazionePorta(String protocollo, ServiceBinding serviceBinding)
			throws ProtocolException, DriverConfigurazioneException { 
		
		if(serviceBinding == null) {
			List<ServiceBinding> serviceBindingListProtocollo = this.core.getServiceBindingListProtocollo(protocollo);
			
			List<PortaDelegataAzioneIdentificazione> listaModalita = new ArrayList<PortaDelegataAzioneIdentificazione>();
			if(serviceBindingListProtocollo != null && serviceBindingListProtocollo.size() > 0) {
				for (ServiceBinding serviceBinding2 : serviceBindingListProtocollo) {
					List<PortaDelegataAzioneIdentificazione> listaModalitaTmp = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo).
							createProtocolIntegrationConfiguration().getAllSubscriptionIdentificationResourceModes(serviceBinding2,
									ProtocolPropertiesUtilities.getTipoInterfaccia(this));
					
					for (PortaDelegataAzioneIdentificazione tipoTmp : listaModalitaTmp) {
						if(!listaModalita.contains(tipoTmp))
							listaModalita.add(tipoTmp);
					}
				}
			}
			return listaModalita;
		} else {
			return ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo).
					createProtocolIntegrationConfiguration().getAllSubscriptionIdentificationResourceModes(serviceBinding,
							ProtocolPropertiesUtilities.getTipoInterfaccia(this));
		}
	}
	
	public boolean reuseIdProtocol(String protocollo) throws ProtocolException{
		return ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo).
				createProtocolConfiguration().isAbilitatoRiusoIdCorrelazioneApplicativa();
	}


	public Vector<DataElement> addPorteDelegateCorrelazioneApplicativaRequestToDati(TipoOperazione tipoOp,
			PageData pd,   String elemxml, String mode,
			String pattern, String gif, String riusoIdMessaggio, Vector<DataElement> dati, String idcorr,
			String protocollo) throws ProtocolException {

		DataElement de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		dati.addElement(this.getDataElementNotCorrelazioneApplicativa());
		de = new DataElement();
		de.setLabel("");
		de.setValue("");
		de.setType(DataElementType.NOTE);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_ELEMENTO_XML);
		de.setNote(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_ELEMENTO_XML_NOTE);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ELEMENTO_XML);
		de.setSize(80);
		if (elemxml == null || CostantiControlStation.LABEL_PORTE_CORRELAZIONE_APPLICATIVA_QUALSIASI.equals(elemxml)) {
			de.setValue("");
		} else {
			de.setValue(elemxml);
		}
		dati.addElement(de);

		String[] tipoMode = { 
				PorteDelegateCostanti.VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_URL_BASED, 
				PorteDelegateCostanti.VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_HEADER_BASED, 
				PorteDelegateCostanti.VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_CONTENT_BASED,
				PorteDelegateCostanti.VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_INPUT_BASED ,
				PorteDelegateCostanti.VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_DISABILITATO 
		};
		//String[] tipoMode = { "contentBased", "disabilitato" };
		de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MODALITA_IDENTIFICAZIONE);
		de.setType(DataElementType.SELECT);
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE);
		de.setValues(tipoMode);
		de.setSelected(mode);
		//				de.setOnChange("CambiaModeCorrApp('add','')");
		de.setPostBack(true);
		dati.addElement(de);

		if (mode.equals(PorteDelegateCostanti.VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_URL_BASED) ||
				mode.equals(PorteDelegateCostanti.VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_HEADER_BASED) ||
				mode.equals(PorteDelegateCostanti.VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_CONTENT_BASED)) {
			de = new DataElement();
			if(mode.equals(PorteDelegateCostanti.VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_HEADER_BASED)) {
				de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME);
				de.setType(DataElementType.TEXT_EDIT);
			}
			else {
				de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PATTERN);
				de.setType(DataElementType.TEXT_AREA);
			}
			if (pattern == null) {
				de.setValue("");
			} else {
				de.setValue(pattern);
			}
			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_PATTERN);
			de.setRequired(true);
			dati.addElement(de);
		}

		if(!PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_DISABILITATO.equals(mode)){
			String[] tipiGIF = { CostantiConfigurazione.BLOCCA.toString(), CostantiConfigurazione.ACCETTA.toString()};
			de = new DataElement();
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_GESTIONE_IDENTIFICAZIONE_FALLITA);
			de.setType(DataElementType.SELECT);
			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_GESTIONE_IDENTIFICAZIONE_FALLITA);
			de.setValues(tipiGIF);
			de.setSelected(gif);
			dati.addElement(de);

			String[] tipiRiusoIdMessaggio = { CostantiConfigurazione.DISABILITATO.toString(), CostantiConfigurazione.ABILITATO.toString()};
			de = new DataElement();
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_RIUSO_ID_MESSAGGIO);
			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_RIUSO_ID_MESSAGGIO);
			if(this.reuseIdProtocol(protocollo)) {
				de.setType(DataElementType.SELECT);
				de.setValues(tipiRiusoIdMessaggio);
				de.setSelected(riusoIdMessaggio);
			}
			else {
				de.setType(DataElementType.HIDDEN);
				de.setValue(CostantiConfigurazione.DISABILITATO.toString());
			}
			dati.addElement(de);

		}

		if(idcorr != null){
			de = new DataElement();
			de.setValue(idcorr);
			de.setType(DataElementType.HIDDEN);
			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_CORRELAZIONE);
			dati.addElement(de);
		}

		return dati;

	}

	public Vector<DataElement> addPorteDelegateCorrelazioneApplicativaResponseToDati(TipoOperazione tipoOp,
			PageData pd, String elemxml, String mode,
			String pattern, String gif,
			//			String riuso,
			Vector<DataElement> dati, String idcorr) {

		DataElement de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		dati.addElement(this.getDataElementNotCorrelazioneApplicativa());
		de = new DataElement();
		de.setLabel("");
		de.setValue("");
		de.setType(DataElementType.NOTE);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_ELEMENTO_XML);
		de.setNote(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_ELEMENTO_XML_NOTE);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ELEMENTO_XML);
		de.setSize(80);
		if (elemxml == null || CostantiControlStation.LABEL_PORTE_CORRELAZIONE_APPLICATIVA_QUALSIASI.equals(elemxml)) {
			de.setValue("");
		} else {
			de.setValue(elemxml);
		}
		dati.addElement(de);

		String[] tipoMode = { 
				PorteDelegateCostanti.VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_HEADER_BASED, 
				PorteDelegateCostanti.VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_CONTENT_BASED,
				PorteDelegateCostanti.VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_INPUT_BASED ,
				PorteDelegateCostanti.VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_DISABILITATO 
		};
		//String[] tipoMode = { "contentBased", "disabilitato" };
		de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MODALITA_IDENTIFICAZIONE);
		de.setType(DataElementType.SELECT);
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE);
		de.setValues(tipoMode);
		de.setSelected(mode);
		//				de.setOnChange("CambiaModeCorrApp('add','')");
		de.setPostBack(true);
		dati.addElement(de);

		if (mode.equals(PorteDelegateCostanti.VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_URL_BASED) ||
				mode.equals(PorteDelegateCostanti.VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_HEADER_BASED) ||
				mode.equals(PorteDelegateCostanti.VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_CONTENT_BASED)) {
			de = new DataElement();
			if(mode.equals(PorteDelegateCostanti.VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_HEADER_BASED)) {
				de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME);
				de.setType(DataElementType.TEXT_EDIT);
			}
			else {
				de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PATTERN);
				de.setType(DataElementType.TEXT_AREA);
			}
			if (pattern == null) {
				de.setValue("");
			} else {
				de.setValue(pattern);
			}
			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_PATTERN);
			de.setRequired(true);
			dati.addElement(de);
		}

		if(!PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_DISABILITATO.equals(mode)){
			String[] tipiGIF = { CostantiConfigurazione.BLOCCA.toString(), CostantiConfigurazione.ACCETTA.toString()};
			de = new DataElement();
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_GESTIONE_IDENTIFICAZIONE_FALLITA);
			de.setType(DataElementType.SELECT);
			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_GESTIONE_IDENTIFICAZIONE_FALLITA);
			de.setValues(tipiGIF);
			de.setSelected(gif);
			dati.addElement(de);

			//			String[] tipiRiusoID = { CostantiConfigurazione.DISABILITATO, CostantiConfigurazione.ABILITATO};
			//			de = new DataElement();
			//			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_RIUSO_ID_MESSAGGIO);
			//			de.setType(DataElementType.SELECT);
			//			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_RIUSO_ID_MESSAGGIO);
			//			de.setValues(tipiRiusoID);
			//			de.setSelected(riuso);
			//			dati.addElement(de);

		}

		if(idcorr != null){
			de = new DataElement();
			de.setValue(idcorr);
			de.setType(DataElementType.HIDDEN);
			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_CORRELAZIONE);
			dati.addElement(de);
		}


		return dati;

	}

	// Controlla i dati del message-security response-flow della porta delegata
	public boolean porteDelegateMessageSecurityResponseCheckData(TipoOperazione tipoOp)
			throws Exception {
		try {
			String id = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			int idInt = Integer.parseInt(id);
			String nome = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME);
			String valore = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_VALORE);

			// Campi obbligatori
			if (nome.equals("") || valore.equals("")) {
				String tmpElenco = "";
				if (nome.equals("")) {
					tmpElenco = "Nome";
				}
				if (valore.equals("")) {
					if (tmpElenco.equals("")) {
						tmpElenco = "Valore";
					} else {
						tmpElenco = tmpElenco + ", Valore";
					}
				}
				this.pd.setMessage(MessageFormat.format(PorteDelegateCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX, tmpElenco));
				return false;
			}

			// Controllo che non ci siano spazi nei campi di testo
			//if ((nome.indexOf(" ") != -1) || (valore.indexOf(" ") != -1)) {
			if ((nome.indexOf(" ") != -1) ) {
				this.pd.setMessage(PorteDelegateCostanti.MESSAGGIO_ERRORE_NON_INSERIRE_SPAZI_NEI_NOMI);
				return false;
			}
			if(valore.startsWith(" ") || valore.endsWith(" ")){
				this.pd.setMessage(PorteDelegateCostanti.MESSAGGIO_ERRORE_NON_INSERIRE_SPAZI_ALL_INIZIO_O_ALLA_FINE_DEI_VALORI);
				return false;
			}
			if(this.checkLength255(nome, PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME)==false) {
				return false;
			}

			// Se tipoOp = add, controllo che il message-security non sia gia' stato
			// registrato per la porta delegata
			if (tipoOp.equals(TipoOperazione.ADD)) {
				boolean giaRegistrato = false;
				PortaDelegata pde = this.porteDelegateCore.getPortaDelegata(idInt);
				String nomeporta = pde.getNome();
				MessageSecurity messageSecurity = pde.getMessageSecurity();

				if(messageSecurity!=null){
					if(messageSecurity.getResponseFlow()!=null){
						for (int i = 0; i < messageSecurity.getResponseFlow().sizeParameterList(); i++) {
							MessageSecurityFlowParameter tmpMessageSecurity =messageSecurity.getResponseFlow().getParameter(i);
							if (nome.equals(tmpMessageSecurity.getNome())) {
								giaRegistrato = true;
								break;
							}
						}
					}
				}

				if (giaRegistrato) {
					this.pd.setMessage(MessageFormat.format(PorteDelegateCostanti.MESSAGGIO_ERRORE_LA_PROPRIETA_DI_MESSAGE_SECURITY_XX_E_GIA_STATO_ASSOCIATA_ALLA_PORTA_DELEGATA_YY,	nome, nomeporta));
					return false;
				}
			}

			return true;

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}






	// Controlla i dati della porta delegata
	public boolean porteDelegateCheckData(TipoOperazione tipoOp, String oldNomePD)
			throws Exception {
		try {
			String id = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			String nomePD = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_PORTA);
			String idsogg = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			int soggInt = Integer.parseInt(idsogg);
			// String descr = this.getParameter("descr");
			String autenticazione = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE);
			String autenticazioneCustom = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM);
			String autenticazioneOpzionale = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_OPZIONALE);
			String autorizzazione = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE);
			String autorizzazioneCustom = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM);
			String autorizzazioneAutenticati = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_AUTENTICAZIONE);
			String autorizzazioneRuoli = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_RUOLI);
			String autorizzazioneRuoliTipologia = this.getParameter(CostantiControlStation.PARAMETRO_RUOLO_TIPOLOGIA);
			String ruoloMatch = this.getParameter(CostantiControlStation.PARAMETRO_RUOLO_MATCH);
			String soggid = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SOGGETTO_ID);
			String servid = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SERVIZIO_ID);
			String azid = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AZIONE_ID);
			String modeaz = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_AZIONE);
			String azione = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AZIONE);
			String statoValidazione = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_XSD);

			String autorizzazioneContenuti = this.getParameter(CostantiControlStation.PARAMETRO_AUTORIZZAZIONE_CONTENUTI);
			
			String integrazione = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_INTEGRAZIONE);
			
			// Campi obbligatori
			if (nomePD==null || nomePD.equals("")) {
				this.pd.setMessage(PorteDelegateCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_EGRAVE_NECESSARIO_INDICARE_IL_NOME);
				return false;
			}

			if (soggid == null) {
				this.pd.setMessage(PorteDelegateCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_NON_E_STATO_TROVATO_NESSUN_SOGGETTO_EROGATORE_SCEGLIERE_UNA_DELLE_ALTRE_MODALITA);
				return false;
			}

			if (servid == null) {
				this.pd.setMessage(PorteDelegateCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_NON_E_STATO_TROVATO_NESSUN_SERVIZIO_ASSOCIATO_AL_SOGGETTO_EROGATORE_SCEGLIERE_UNA_DELLE_ALTRE_MODALITA);
				return false;
			}
			
			if (modeaz == null) {
				this.pd.setMessage(PorteDelegateCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_NON_E_STATA_TROVATA_NESSUNA_MODALITA_AZIONE);
				return false;
			}

			if (modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT) && (azid == null)) {
				this.pd.setMessage(PorteDelegateCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_NON_E_STATA_TROVATA_NESSUNA_AZIONE_ASSOCIATA_AL_SERVIZIO_SCEGLIERE_UNA_DELLE_ALTRE_MODALITA);
				return false;
			}
			if ((modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_URL_BASED) || 
					modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_HEADER_BASED) ||
					modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_CONTENT_BASED)) && (azione==null || azione.equals(""))) {
				this.pd.setMessage(PorteDelegateCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_PATTERN_AZIONE);
				return false;
			}

			// Controllo che non ci siano spazi nei campi di testo
			if ((nomePD.indexOf(" ") != -1) ) {
				this.pd.setMessage(PorteDelegateCostanti.MESSAGGIO_ERRORE_NON_INSERIRE_SPAZI_NEI_CAMPI_DI_TESTO);
				return false;
			}
			if(this.checkIntegrationEntityName(nomePD,PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME)==false){
				return false;
			}
			if ((modeaz != null) && !modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT) && 
					!modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_INPUT_BASED) &&
					!modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_SOAP_ACTION_BASED) &&
					!modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_DELEGATED_BY) &&
					(azione.indexOf(" ") != -1)) {
				this.pd.setMessage(PorteDelegateCostanti.MESSAGGIO_ERRORE_NON_INSERIRE_SPAZI_NEI_CAMPI_DI_TESTO);
				return false;
			}

			// length
			if(integrazione!=null && !"".equals(integrazione)){
				if(this.checkLength255(integrazione, PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_METADATI)==false) {
					return false;
				}
			}
			
//			// Controllo che i campi "select" abbiano uno dei valori ammessi
//			if ((modeaz != null) && !modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_HEADER_BASED) && 
//					!modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT) && 
//					!modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_URL_BASED) && 
//					!modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_CONTENT_BASED) && 
//					!modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_INPUT_BASED) && 
//					!modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_SOAP_ACTION_BASED) && 
//					!modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_WSDL_BASED)) {
//				this.pd.setMessage(PorteDelegateCostanti.MESSAGGIO_ERRORE_MODE_AZIONE_DEV_ESSERE_USER_INPUT_REGISTER_INPUT_URL_BASED_CONTENT_BASED_INPUT_BASED_SOAP_ACTION_BASED_O_WSDL_BASED);
//				return false;
//			}
			if(tipoOp.equals(TipoOperazione.ADD)) {
				if (!statoValidazione.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_ABILITATO) &&
						!statoValidazione.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_DISABILITATO) &&
						!statoValidazione.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_WARNING_ONLY)) {
					this.pd.setMessage(PorteDelegateCostanti.MESSAGGIO_ERRORE_VALIDAZIONE_XSD_DEV_ESSERE_ABILITATO_DISABILITATO_O_WARNING_ONLY);
					return false;
				}
			}

			// Se autenticazione = custom, nomeauth dev'essere specificato
			if (CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM.equals(autenticazione) && 
					(autenticazioneCustom == null || autenticazioneCustom.equals(""))) {
				this.pd.setMessage(MessageFormat.format(PorteDelegateCostanti.MESSAGGIO_ERRORE_INDICARE_UN_NOME_PER_L_AUTENTICAZIONE_XX, CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM));
				return false;
			}

			// Se autorizzazione = custom, nomeautor dev'essere specificato
			if (CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM.equals(autorizzazione) && 
					(autorizzazioneCustom == null || autorizzazioneCustom.equals(""))) {
				this.pd.setMessage(MessageFormat.format(PorteDelegateCostanti.MESSAGGIO_ERRORE_INDICARE_UN_NOME_PER_L_AUTORIZZAZIONE_XX, CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM));
				return false;
			}
			
			PortaDelegata pdDatabase = null;
			if (TipoOperazione.CHANGE.equals(tipoOp)){
				pdDatabase = this.porteDelegateCore.getPortaDelegata(Long.parseLong(id)); 
			}
			
			List<String> ruoli = new ArrayList<>();
			if(pdDatabase!=null && pdDatabase.getRuoli()!=null && pdDatabase.getRuoli().sizeRuoloList()>0){
				for (int i = 0; i < pdDatabase.getRuoli().sizeRuoloList(); i++) {
					ruoli.add(pdDatabase.getRuoli().getRuolo(i).getNome());
				}
			}
			
			if(tipoOp.equals(TipoOperazione.ADD)) {
				String gestioneToken = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN);
				String gestioneTokenPolicy = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_POLICY);
				String gestioneTokenValidazioneInput = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_VALIDAZIONE_INPUT);
				String gestioneTokenIntrospection = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_INTROSPECTION);
				String gestioneTokenUserInfo = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_USERINFO);
				String gestioneTokenTokenForward = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_TOKEN_FORWARD);
				String autorizzazione_token = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN);
				String autorizzazione_tokenOptions = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN_OPTIONS);
				String autorizzazioneScope = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_SCOPE);
				String autorizzazioneScopeMatch = this.getParameter(CostantiControlStation.PARAMETRO_SCOPE_MATCH);
				BinaryParameter allegatoXacmlPolicy = this.getBinaryParameter(CostantiControlStation.PARAMETRO_DOCUMENTO_SICUREZZA_XACML_POLICY);
				
				String idAsps = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
				if(idAsps == null)
					idAsps = "";
				
				AccordoServizioParteSpecifica asps = this.apsCore.getAccordoServizioParteSpecifica(Long.parseLong(idAsps)); 
				
				String protocollo = ProtocolFactoryManager.getInstance().getProtocolByServiceType(asps.getTipo());
				
				if(this.controlloAccessiCheck(tipoOp, autenticazione, autenticazioneOpzionale, 
						autorizzazione, autorizzazioneAutenticati, autorizzazioneRuoli, 
						autorizzazioneRuoliTipologia, ruoloMatch, 
						true, true, null, ruoli,gestioneToken, gestioneTokenPolicy, 
						gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward,
						autorizzazione_token,autorizzazione_tokenOptions,
						autorizzazioneScope,autorizzazioneScopeMatch,allegatoXacmlPolicy,
						autorizzazioneContenuti,
						protocollo)==false){
					return false;
				}
			}

			IDSoggetto idSoggettoFruitore = null; 
			if(this.core.isRegistroServiziLocale()){
				// Soggetto Fruitore
				Soggetto soggettoFruitore = null;
				soggettoFruitore = this.soggettiCore.getSoggettoRegistro(soggInt);
				idSoggettoFruitore = new IDSoggetto(soggettoFruitore.getTipo(), soggettoFruitore.getNome());
			}
			else{
				// Soggetto Fruitore
				org.openspcoop2.core.config.Soggetto soggettoFruitore = null;
				soggettoFruitore = this.soggettiCore.getSoggetto(soggInt);
				idSoggettoFruitore = new IDSoggetto(soggettoFruitore.getTipo(), soggettoFruitore.getNome());
			}
			
			IDPortaDelegata idPD = new IDPortaDelegata();
			idPD.setNome(nomePD);

			// Se tipoOp = add, controllo che la porta delegata non sia gia'
			// stata registrata
			if (TipoOperazione.ADD.equals(tipoOp)) {
				boolean giaRegistrato = false;
				long idPDGiaRegistrata = -1;
				try {
					// controllo puntuale su nome
					idPDGiaRegistrata = this.porteDelegateCore.getIdPortaDelegata(nomePD);
					giaRegistrato = idPDGiaRegistrata > 0;
					// controllo su location e nome
					if (!giaRegistrato)
						giaRegistrato = this.porteDelegateCore.getPortaDelegata(idPD) != null;
				} catch (DriverConfigurazioneNotFound e) {
					giaRegistrato = false;
				}

				if (giaRegistrato) {
					PortaDelegata pd = this.porteDelegateCore.getPortaDelegata(idPDGiaRegistrata);
					String soggettoProprietarioMessaggio = pd.getTipoSoggettoProprietario()+"/"+pd.getNomeSoggettoProprietario();
					this.pd.setMessage(MessageFormat.format(
							PorteDelegateCostanti.MESSAGGIO_ERRORE_ESISTE_GIA_UNA_PORTA_DELEGATA_CON_NOME_XX_ASSOCIATA_AL_SOGGETTO_YY, nomePD,
							soggettoProprietarioMessaggio));
					return false;
				}
			} else if (TipoOperazione.CHANGE.equals(tipoOp)) {
				PortaDelegata portaDelegata = null;
				try {
					// controllo su nome (non possono esistere 2 pd con stesso
					// nome dello stesso fruitore)
					if (!nomePD.equals(oldNomePD)) {
						long curID = this.porteDelegateCore.getIdPortaDelegata(nomePD);
						if (curID > 0) {
							PortaDelegata pd = this.porteDelegateCore.getPortaDelegata(curID);
							String soggettoProprietarioMessaggio = pd.getTipoSoggettoProprietario()+"/"+pd.getNomeSoggettoProprietario();
							this.pd.setMessage(MessageFormat.format(
									PorteDelegateCostanti.MESSAGGIO_ERRORE_ESISTE_GIA_UNA_PORTA_DELEGATA_CON_NOME_XX_ASSOCIATA_AL_SOGGETTO_YY,
									nomePD, soggettoProprietarioMessaggio));
							return false;
						}
					}

					// controllo porta delegata per location (questo controlla
					// anche il nome in caso di location non presente)
					portaDelegata = this.porteDelegateCore.getPortaDelegata(idPD);

				} catch (DriverConfigurazioneNotFound e) {
					// ok non esiste un altra porta delegata del fruitore con
					// questa location
				}

				// controllo se la pdd che ho ottenuto e' quella che sto
				// modificando
				// in tal caso procedo con l update altrimenti non posso fare
				// update in quanto pdd gia esistente
				long oldIDpd =  this.porteDelegateCore.getIdPortaDelegata(oldNomePD);
				if (portaDelegata != null) {
					if (oldIDpd != portaDelegata.getId()) {
						PortaDelegata pd = this.porteDelegateCore.getPortaDelegata(oldIDpd);
						String soggettoProprietarioMessaggio = pd.getTipoSoggettoProprietario()+"/"+pd.getNomeSoggettoProprietario();
						this.pd.setMessage(MessageFormat.format(
								PorteDelegateCostanti.MESSAGGIO_ERRORE_ESISTE_GIA_UNA_PORTA_DELEGATA_CON_NOME_XX_ASSOCIATA_AL_SOGGETTO_YY,
								portaDelegata.getNome(), soggettoProprietarioMessaggio));
						return false;
					}
				}
				
				// Controllo che se e' stato cambiato il tipo di autenticazione, non devono essere presenti serviziApplicativi incompatibili
				if(portaDelegata==null){
					// la prelevo con il vecchio nome
					portaDelegata = this.porteDelegateCore.getPortaDelegata(oldIDpd);
				}
				if(autenticazione!=null && autenticazione.equals(portaDelegata.getAutenticazione())==false){
					CredenzialeTipo c = CredenzialeTipo.toEnumConstant(autenticazione);
					if(c!=null){
						if(portaDelegata.sizeServizioApplicativoList()>0){
							
							boolean saCompatibili = true;
							for (int i = 0; i < portaDelegata.sizeServizioApplicativoList(); i++) {
								
								IDServizioApplicativo idServizioApplicativo = new IDServizioApplicativo();
								idServizioApplicativo.setNome(portaDelegata.getServizioApplicativo(i).getNome());
								idServizioApplicativo.setIdSoggettoProprietario(idSoggettoFruitore);
								ServizioApplicativo saTmp = this.saCore.getServizioApplicativo(idServizioApplicativo);
								
								if(saTmp.getInvocazionePorta()==null){
									saCompatibili=false;
									break;
								}
								if(saTmp.getInvocazionePorta().sizeCredenzialiList()<=0){
									saCompatibili=false;
									break;
								}
								
								boolean ok = false;
								for (int j = 0; j < saTmp.getInvocazionePorta().sizeCredenzialiList(); j++) {
									CredenzialeTipo cSA =saTmp.getInvocazionePorta().getCredenziali(j).getTipo();
									if( c.equals(cSA) ){
										ok = true;
										break;
									}
								}
								if(!ok){
									saCompatibili=false;
									break;
								}
							}
							
							if(saCompatibili==false){
								this.pd.setMessage(MessageFormat.format(
										PorteDelegateCostanti.MESSAGGIO_ERRORE_NON_E_POSSIBILE_MODIFICARE_IL_TIPO_DI_AUTENTICAZIONE_DA_XX_A_YY_POICHÈ_RISULTANO_ASSOCIATI_ALLA_PORTA_DELEGATA_DEI_SERVIZI_APPLICATIVI_NON_COMPATIBILI_NELLA_MODALITA_DI_ACCESSO_CON_IL_NUOVO_TIPO_DI_AUTENTICAZIONE,
										portaDelegata.getAutenticazione(), autenticazione));
								return false;
							}
						}
					}
				}
			}

			return true;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	// Controlla i dati del servizioApplicativo della porta delegata
	public boolean porteDelegateServizioApplicativoCheckData(TipoOperazione tipoOp)
			throws Exception {
		try {
			String id = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			int idInt = Integer.parseInt(id);
			String idsogg = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			int soggInt = Integer.parseInt(idsogg);
			String servizioApplicativo = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SERVIZIO_APPLICATIVO);

			// Campi obbligatori
			if (servizioApplicativo.equals("")) {
				this.pd.setMessage("Dati incompleti. E' necessario indicare un Servizio Applicativo");
				return false;
			}

			// Controllo che il servizioApplicativo appartenga alla lista di
			// servizioApplicativo disponibili per il soggetto
			boolean trovatoServizioApplicativo = false;

			// Prendo il nome e il tipo del soggetto
			String nomeprov = null;
			String tipoprov = null;
			if(this.core.isRegistroServiziLocale()){
				Soggetto mySogg = this.soggettiCore.getSoggettoRegistro(soggInt);
				nomeprov = mySogg.getNome();
				tipoprov = mySogg.getTipo();
			}else{
				org.openspcoop2.core.config.Soggetto mySogg = this.soggettiCore.getSoggetto(soggInt);
				nomeprov = mySogg.getNome();
				tipoprov = mySogg.getTipo();
			}

			IDSoggetto ids = new IDSoggetto(tipoprov, nomeprov);
			IDServizioApplicativo idSA = new IDServizioApplicativo();
			idSA.setIdSoggettoProprietario(ids);
			idSA.setNome(servizioApplicativo);
			trovatoServizioApplicativo = this.saCore.existsServizioApplicativo(idSA);
			if (!trovatoServizioApplicativo) {
				this.pd.setMessage("Il Servizio Applicativo dev'essere scelto tra quelli definiti nel pannello Servizi Applicativi ed associati al soggetto " + tipoprov + "/" + nomeprov);
				return false;
			}

			// Se tipoOp = add, controllo che il servizioApplicativo non sia
			// gia'
			// stato
			// registrato per la porta delegata
			if (tipoOp.equals(TipoOperazione.ADD)) {
				boolean giaRegistrato = false;

				// Prendo il nome della porta delegata
				PortaDelegata pde = this.porteDelegateCore.getPortaDelegata(idInt);
				String nomeporta = pde.getNome();

				for (int i = 0; i < pde.sizeServizioApplicativoList(); i++) {
					PortaDelegataServizioApplicativo tmpSA = pde.getServizioApplicativo(i);
					if (servizioApplicativo.equals(tmpSA.getNome())) {
						giaRegistrato = true;
						break;
					}
				}

				if (giaRegistrato) {
					this.pd.setMessage("Il Servizio Applicativo " + servizioApplicativo + " &egrave; gi&agrave; stato associato alla porta delegata " + nomeporta);
					return false;
				}
			}

			return true;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	// Prepara la lista di porte delegate
	public void preparePorteDelegateList(ISearch ricerca, List<PortaDelegata> lista,int idLista)
			throws Exception {
		try {
			Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);
			
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, this.session);
			if(parentPD == null) parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;
			
			IExtendedListServlet extendedServletList = this.core.getExtendedServletPortaDelegata();
			
			String idAsps = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";
			String id = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);

			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);
			
			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			List<Parameter> lstParam = new ArrayList<Parameter>();
			boolean useIdSogg = false;
			
			switch (parentPD) {
			case PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE:
				// In teoria non dovrei mai trovarmi qui
				break;
			case PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_SOGGETTO:
				ServletUtils.addListElementIntoSession(this.session, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE,
						new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, id));
				
				String soggettoTitle = null;
				if(this.core.isRegistroServiziLocale()){
					org.openspcoop2.core.registry.Soggetto soggetto = this.soggettiCore.getSoggettoRegistro(Integer.parseInt(id));
					String protocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(soggetto.getTipo());
					soggettoTitle = this.getLabelNomeSoggetto(protocollo, soggetto.getTipo() , soggetto.getNome());
				}else{
					org.openspcoop2.core.config.Soggetto soggetto = this.soggettiCore.getSoggetto(Integer.parseInt(id));
					String protocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(soggetto.getTipo());
					soggettoTitle = this.getLabelNomeSoggetto(protocollo, soggetto.getTipo() , soggetto.getNome());
				}
				
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SOGGETTI, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST));

				if(search.equals("")){
					this.pd.setSearchDescription("");
					lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PORTE_DELEGATE_DI + soggettoTitle,null));
				}
				else{
					lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PORTE_DELEGATE_DI + soggettoTitle, PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST + "?"
							+ PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO + "=" + id
							));
					lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_RISULTATI_RICERCA, null));

				}
				useIdSogg = true;
				break;
			case PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE:
			default:
				ServletUtils.addListElementIntoSession(this.session, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE);
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PORTE_DELEGATE, PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST));
				if(search.equals("")){
					this.pd.setSearchDescription("");
				}
				else{
					lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_RISULTATI_RICERCA, null));

				}
				break;
			}

			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			if(useIdSogg==false){
				addFilterProtocol(ricerca, idLista);
			}
						
			// controllo eventuali risultati ricerca
			this.pd.setSearchLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME);
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, PorteDelegateCostanti.LABEL_PORTE_DELEGATE, search);
			}

			boolean showProtocolli = this.core.countProtocolli(this.session)>1;
			
			// setto le label delle colonne
			List<String> labelsList= new ArrayList<String>();

			labelsList.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME); 
			if(useIdSogg==false){
				labelsList.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SOGGETTO);
				if( showProtocolli ) {
					labelsList.add(CostantiControlStation.LABEL_PARAMETRO_PROTOCOLLO);
				}
			}

			
			labelsList.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONTROLLO_ACCESSI); 
			
			labelsList.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY); 

			labelsList.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM);

			labelsList.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA);
			
			if(this.isModalitaAvanzata())
				labelsList.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PROTOCOL_PROPERTIES);
			
			if(extendedServletList!=null && extendedServletList.showExtendedInfo(this.request, this.session)){
				labelsList.add(extendedServletList.getListTitle(this));
			}
			
			labelsList.add(PorteDelegateCostanti.LABEL_COLUMN_PORTE_DELEGATE_STATO_PORTA);
			
			String[] labels = labelsList.toArray(new String[labelsList.size()]);

			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<PortaDelegata> it = lista.iterator();



				while (it.hasNext()) {
					PortaDelegata pd = it.next();

					Parameter pIdPD = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, "" + pd.getId());
					Parameter pNomePD = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_PORTA, pd.getNome());
					Parameter pIdSoggPD = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, pd.getIdSoggetto() + "");
					
					IDServizio idServizioObject = IDServizioFactory.getInstance().getIDServizioFromValues(pd.getServizio().getTipo(), pd.getServizio().getNome(), 
							pd.getSoggettoErogatore().getTipo(), pd.getSoggettoErogatore().getNome(), 
							pd.getServizio().getVersione());
					AccordoServizioParteSpecifica asps = this.apsCore.getServizio(idServizioObject);
					
					Parameter pIdAsps = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS, asps.getId()+"");
					@SuppressWarnings("unused")
					Parameter pIdSoggettoErogatore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, asps.getIdSoggetto()+"");
					
					long idFruzione = -1;
					for (Fruitore fruitore : asps.getFruitoreList()) {
						if(fruitore.getTipo().equals(pd.getTipoSoggettoProprietario()) &&
								fruitore.getNome().equals(pd.getNomeSoggettoProprietario())) {
							idFruzione = fruitore.getId();
							break;
						}
					}
					Parameter pIdFruitore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MY_ID, idFruzione+ "");
					
					String protocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(pd.getTipoSoggettoProprietario());
					
					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					de.setType(DataElementType.HIDDEN);
					de.setValue("" + pd.getId());
					e.addElement(de);

					de = new DataElement();
					de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CHANGE,
							pIdSoggPD,
							pNomePD,
							pIdPD
							);
					de.setValue(pd.getNome());
					de.setIdToRemove(pd.getId().toString());
					de.setToolTip(pd.getDescrizione());
					e.addElement(de);

					if(useIdSogg==false){
						de = new DataElement();
						de.setValue(this.getLabelNomeSoggetto(protocollo, pd.getTipoSoggettoProprietario() , pd.getNomeSoggettoProprietario()));
						e.addElement(de);
											
						if( showProtocolli ) {
							de = new DataElement();
							de.setValue(this.getLabelProtocollo(protocollo));
							e.addElement(de);
						}
					}
					
					// Controllo Accessi
					de = new DataElement();
					de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CONTROLLO_ACCESSI, pIdPD, pNomePD, pIdSoggPD, pIdAsps, pIdFruitore);
					
					String gestioneToken = null;
					if(pd.getGestioneToken()!=null && pd.getGestioneToken().getPolicy()!=null &&
							!"".equals(pd.getGestioneToken().getPolicy()) &&
							!"-".equals(pd.getGestioneToken().getPolicy())) {
						gestioneToken = StatoFunzionalita.ABILITATO.getValue();
					}
					
					String autenticazione = pd.getAutenticazione();
					String autenticazioneCustom = null;
					if (autenticazione != null && !TipoAutenticazione.getValues().contains(autenticazione)) {
						autenticazioneCustom = autenticazione;
						autenticazione = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM;
					}
					String autenticazioneOpzionale = "";
					if(pd.getAutenticazioneOpzionale()!=null){
						if (pd.getAutenticazioneOpzionale().equals(StatoFunzionalita.ABILITATO)) {
							autenticazioneOpzionale = Costanti.CHECK_BOX_ENABLED;
						}
					}
					String autorizzazioneContenuti = pd.getAutorizzazioneContenuto();
					
					String autorizzazione= null, autorizzazioneCustom = null;
					if (pd.getAutorizzazione() != null &&
							!TipoAutorizzazione.getAllValues().contains(pd.getAutorizzazione())) {
						autorizzazioneCustom = pd.getAutorizzazione();
						autorizzazione = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM;
					}
					else{
						autorizzazione = AutorizzazioneUtilities.convertToStato(pd.getAutorizzazione());
					}
					
					String statoControlloAccessi = this.getLabelStatoControlloAccessi(gestioneToken, autenticazione, autenticazioneOpzionale, autenticazioneCustom, autorizzazione, autorizzazioneContenuti,autorizzazioneCustom); 
					de.setValue(statoControlloAccessi);
					e.addElement(de);
					

					de = new DataElement();
					de.setUrl( 
							PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MESSAGE_SECURITY,
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, "" + pd.getIdSoggetto()),
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, ""+pd.getId()),
							pIdAsps, pIdFruitore
							);
					de.setValue(pd.getStatoMessageSecurity());
					e.addElement(de);

					//if(InterfaceType.AVANZATA.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())){
					de = new DataElement();
					de.setUrl( 
							PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MTOM,
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, "" + pd.getIdSoggetto()),
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, ""+pd.getId()),
							pIdAsps, pIdFruitore
							);

					boolean isMTOMAbilitatoReq = false;
					boolean isMTOMAbilitatoRes= false;
					if(pd.getMtomProcessor()!= null){
						if(pd.getMtomProcessor().getRequestFlow() != null){
							if(pd.getMtomProcessor().getRequestFlow().getMode() != null){
								MTOMProcessorType mode = pd.getMtomProcessor().getRequestFlow().getMode();
								if(!mode.equals(MTOMProcessorType.DISABLE))
									isMTOMAbilitatoReq = true;
							}
						}

						if(pd.getMtomProcessor().getResponseFlow() != null){
							if(pd.getMtomProcessor().getResponseFlow().getMode() != null){
								MTOMProcessorType mode = pd.getMtomProcessor().getResponseFlow().getMode();
								if(!mode.equals(MTOMProcessorType.DISABLE))
									isMTOMAbilitatoRes = true;
							}
						}
					}

					if(isMTOMAbilitatoReq || isMTOMAbilitatoRes)
						de.setValue(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM_ABILITATO);
					else 
						de.setValue(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM_DISABILITATO);
					e.addElement(de);
					//}

					de = new DataElement();
					de.setUrl(
							PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA,
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, "" + pd.getIdSoggetto()),
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, ""+pd.getId()),
							pIdAsps, pIdFruitore );
					
					boolean isCorrelazioneApplicativaAbilitataReq = false;
					boolean isCorrelazioneApplicativaAbilitataRes = false;
					
					if (pd.getCorrelazioneApplicativa() != null)
						isCorrelazioneApplicativaAbilitataReq = pd.getCorrelazioneApplicativa().sizeElementoList() > 0;

					if (pd.getCorrelazioneApplicativaRisposta() != null)
						isCorrelazioneApplicativaAbilitataRes = pd.getCorrelazioneApplicativaRisposta().sizeElementoList() > 0;
						
					if(isCorrelazioneApplicativaAbilitataReq || isCorrelazioneApplicativaAbilitataRes)
						de.setValue(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_ABILITATA);
					else 
						de.setValue(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_DISABILITATA);
					e.addElement(de);
					
					// Protocol Properties
					if(this.isModalitaAvanzata()){
						de = new DataElement();
						//fix: idsogg e' il soggetto proprietario della porta applicativa, e nn il soggetto virtuale
						de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_PROPRIETA_PROTOCOLLO_LIST, pIdSoggPD, pIdPD,
								pIdAsps, pIdFruitore);
						if (contaListe) {
							int numProp = pd.sizeProprietaList();
							ServletUtils.setDataElementVisualizzaLabel(de, (long) numProp );
						} else
							ServletUtils.setDataElementVisualizzaLabel(de);
						e.addElement(de);
					}

					if(extendedServletList!=null && extendedServletList.showExtendedInfo(this.request, this.session)){
						de = new DataElement();
						de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_EXTENDED_LIST,
								new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, "" + pd.getIdSoggetto()),
								new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, ""+pd.getId()),
								new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME,pd.getNome(),
								pIdAsps, pIdFruitore)
								);
						if (contaListe) {
							int numExtended = extendedServletList.sizeList(pd);
							ServletUtils.setDataElementVisualizzaLabel(de,Long.valueOf(numExtended));
						} else
							ServletUtils.setDataElementVisualizzaLabel(de);
						e.addElement(de);
					}
					
					de = new DataElement();
					boolean abilitatoPorta = pd.getStato()!=null ? CostantiConfigurazione.ABILITATO.equals(pd.getStato()) : true;
					de.setType(DataElementType.CHECKBOX);
					de.setSelected(abilitatoPorta);
					de.setToolTip(abilitatoPorta?CostantiConfigurazione.ABILITATO.getValue():CostantiConfigurazione.DISABILITATO.getValue());
					de.setValue(abilitatoPorta+"");
					e.addElement(de);
					
					dati.addElement(e);
				}
			}

			this.pd.setDati(dati);
			// le porte delegate non si possono piu' creare dalle liste PD e PD di un soggetto
			if(!this.isModalitaCompleta() || !useIdSogg) {
				this.pd.setAddButton(false);
			}
			
			if (useIdSogg){ 
				if(!this.isModalitaAvanzata()){
					this.pd.setRemoveButton(false);
					this.pd.setSelect(false);
				}
			} 
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}


	// Prepara la lista di sil delle porte delegate
	public void preparePorteDelegateServizioApplicativoList(String nomePorta, ISearch ricerca, List<ServizioApplicativo> lista)
			throws Exception {
		try {
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, this.session);
			if(parentPD == null) parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;
			
			String id = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			String idsogg = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			String idAsps = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";
			
			String idFruizione = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE);
			if(idFruizione == null)
				idFruizione = "";
			
			Parameter pId = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, id);
			Parameter pIdSoggetto = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg);
			Parameter pIdAsps = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS, idAsps);
			Parameter pIdFrizione = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE, idFruizione);

			ServletUtils.addListElementIntoSession(this.session, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_SERVIZIO_APPLICATIVO, pId, pIdSoggetto, pIdAsps, pIdFrizione);

			int idLista = Liste.PORTE_DELEGATE_SERVIZIO_APPLICATIVO;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			PortaDelegata myPD = this.porteDelegateCore.getPortaDelegata(Integer.parseInt(id));
			String idporta = myPD.getNome();

			List<Parameter> lstParam = this.getTitoloPD(parentPD, idsogg, idAsps, idFruizione);
			
			String labelPerPorta = null;
			if(parentPD!=null && (parentPD.intValue() == PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = this.porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONTROLLO_ACCESSI_CONFIG_DI,
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONTROLLO_ACCESSI,
						myPD);
			}
			else {
				labelPerPorta = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONTROLLO_ACCESSI_CONFIG_DI+idporta;
			}
			
			lstParam.add(new Parameter(labelPerPorta, PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CONTROLLO_ACCESSI, 
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, "" + myPD.getId()),
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_PORTA, myPD.getNome()),
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, myPD.getIdSoggetto() + ""),
					pIdAsps, new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE, idFruizione+ "")));
			
			String labelPagLista = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SERVIZIO_APPLICATIVO_CONFIG;
			if(!this.isModalitaCompleta()) {
				labelPagLista = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_APPLICATIVO_CONFIG;
			}
			
			this.pd.setSearchLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME);
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(labelPagLista,null));
			}
			else{
				lstParam.add(new Parameter(labelPagLista,
						PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_SERVIZIO_APPLICATIVO_LIST, pId, pIdSoggetto, pIdAsps, pIdFrizione	));
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_RISULTATI_RICERCA, null));
			}

			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SERVIZI_APPLICATIVI, search);
			}

			// setto le label delle colonne
			String[] labels = new String[1];
			if(this.isModalitaCompleta()) {
				labels[0] = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SERVIZIO_APPLICATIVO;
			}
			else {
				labels[0] = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_APPLICATIVO;
			}
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<ServizioApplicativo> it = lista.iterator();
				while (it.hasNext()) {
					ServizioApplicativo sa = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					if(this.isModalitaCompleta()) {
						de.setUrl(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE,
								new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID, sa.getId() + ""),
								new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_PROVIDER, idsogg));
					}
					de.setValue(sa.getNome());
					de.setIdToRemove(sa.getNome());
					e.addElement(de);

					dati.addElement(e);
				}
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	
	
	public void preparePorteDelegateRuoliList(String nomePorta, ISearch ricerca, List<String> lista)
			throws Exception {
		try {
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, this.session);
			if(parentPD == null) parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;
			
			String id = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			String idsogg = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			String idAsps = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";
			
			String idFruizione = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE);
			if(idFruizione == null)
				idFruizione = "";
			
			Parameter pId = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, id);
			Parameter pIdSoggetto = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg);
			Parameter pIdAsps = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS, idAsps);
			Parameter pIdFrizione = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE, idFruizione);

			ServletUtils.addListElementIntoSession(this.session, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_RUOLI, pId, pIdSoggetto, pIdAsps, pIdFrizione);

			int idLista = Liste.PORTE_DELEGATE_RUOLI;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			PortaDelegata myPD = this.porteDelegateCore.getPortaDelegata(Integer.parseInt(id));
			String idporta = myPD.getNome();

			List<Parameter> lstParam = this.getTitoloPD(parentPD, idsogg, idAsps, idFruizione);
			
			String labelPerPorta = null;
			if(parentPD!=null && (parentPD.intValue() == PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = this.porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONTROLLO_ACCESSI_CONFIG_DI,
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONTROLLO_ACCESSI,
						myPD);
			}
			else {
				labelPerPorta = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONTROLLO_ACCESSI_CONFIG_DI+idporta;
			}
			
			lstParam.add(new Parameter(labelPerPorta, PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CONTROLLO_ACCESSI, 
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, "" + myPD.getId()),
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_PORTA, myPD.getNome()),
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, myPD.getIdSoggetto() + ""),
					pIdAsps, new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE, idFruizione+ "")));
			
			String labelPagLista = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_RUOLI_CONFIG;

			Parameter pNomePorta = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME, idporta);

			this.pd.setSearchLabel(CostantiControlStation.LABEL_PARAMETRO_RUOLO);
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(labelPagLista,null));
			}
			else{
				lstParam.add(new Parameter(labelPagLista,
						PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_RUOLI_LIST, pId, pIdSoggetto, pNomePorta, pIdAsps, pIdFrizione	));
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_RISULTATI_RICERCA, null));

			}

			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_RUOLI, search);
			}

			// setto le label delle colonne
			String[] labels = {CostantiControlStation.LABEL_PARAMETRO_RUOLO };
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<String> it = lista.iterator();
				while (it.hasNext()) {
					String ruolo = it.next();
		
					Vector<DataElement> e = new Vector<DataElement>();
		
					DataElement de = new DataElement();
					de.setValue(ruolo);
					de.setIdToRemove(ruolo);
					e.addElement(de);
		
					dati.addElement(e);
				}
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public void preparePorteDelegateScopeList(String nomePorta, ISearch ricerca, List<String> lista)
			throws Exception {
		try {
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, this.session);
			if(parentPD == null) parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;
			
			String id = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			String idsogg = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			String idAsps = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";
			
			String idFruizione = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE);
			if(idFruizione == null)
				idFruizione = "";
			
			Parameter pId = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, id);
			Parameter pIdSoggetto = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg);
			Parameter pIdAsps = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS, idAsps);
			Parameter pIdFrizione = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE, idFruizione);

			ServletUtils.addListElementIntoSession(this.session, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_SCOPE, pId, pIdSoggetto, pIdAsps, pIdFrizione);

			int idLista = Liste.PORTE_DELEGATE_SCOPE;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			PortaDelegata myPD = this.porteDelegateCore.getPortaDelegata(Integer.parseInt(id));
			String idporta = myPD.getNome();

			List<Parameter> lstParam = this.getTitoloPD(parentPD, idsogg, idAsps, idFruizione);
			
			String labelPerPorta = null;
			if(parentPD!=null && (parentPD.intValue() == PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = this.porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONTROLLO_ACCESSI_CONFIG_DI,
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONTROLLO_ACCESSI,
						myPD);
			}
			else {
				labelPerPorta = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONTROLLO_ACCESSI_CONFIG_DI+idporta;
			}
			
			lstParam.add(new Parameter(labelPerPorta, PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CONTROLLO_ACCESSI, 
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, "" + myPD.getId()),
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_PORTA, myPD.getNome()),
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, myPD.getIdSoggetto() + ""),
					pIdAsps, new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE, idFruizione+ "")));
			
			String labelPagLista = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SCOPE_CONFIG;

			Parameter pNomePorta = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME, idporta);

			this.pd.setSearchLabel(CostantiControlStation.LABEL_PARAMETRO_SCOPE);
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(labelPagLista,null));
			}
			else{
				lstParam.add(new Parameter(labelPagLista,
						PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_SCOPE_LIST, pId, pIdSoggetto, pNomePorta, pIdAsps, pIdFrizione	));
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_RISULTATI_RICERCA, null));

			}

			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SCOPE, search);
			}

			// setto le label delle colonne
			String[] labels = {CostantiControlStation.LABEL_PARAMETRO_SCOPE };
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<String> it = lista.iterator();
				while (it.hasNext()) {
					String scope = it.next();
		
					Vector<DataElement> e = new Vector<DataElement>();
		
					DataElement de = new DataElement();
					de.setValue(scope);
					de.setIdToRemove(scope);
					e.addElement(de);
		
					dati.addElement(e);
				}
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}


	// Prepara la lista di Message-Security response-flow delle porte delegate
	public void preparePorteDelegateMessageSecurityResponseList(String nomePorta, ISearch ricerca, List<MessageSecurityFlowParameter> lista)
			throws Exception {
		try {
			String id = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			String idsogg = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			String idAsps = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";
			
			String idFruizione = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE);
			if(idFruizione == null)
				idFruizione = "";
			
			Parameter pId = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, id);
			Parameter pIdSoggetto = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg);
			Parameter pIdAsps = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS, idAsps);
			Parameter pIdFrizione = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE, idFruizione);

			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, this.session);
			if(parentPD == null) parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;

			ServletUtils.addListElementIntoSession(this.session, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE, pId, pIdSoggetto, pIdAsps, pIdFrizione); 

			int idLista = Liste.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			PortaDelegata myPD = this.porteDelegateCore.getPortaDelegata(Integer.parseInt(id));
			String idporta = myPD.getNome();

			// setto la barra del titolo
			List<Parameter> lstParam = this.getTitoloPD(parentPD, idsogg, idAsps, idFruizione);
			
			Parameter pNomePorta = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME, idporta);
			
			String labelPerPorta = null;
			if(parentPD!=null && (parentPD.intValue() == PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = this.porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_CONFIG_DI,
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY,
						myPD);
			}
			else {
				labelPerPorta = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_CONFIG_DI+idporta;
			}
			lstParam.add(new Parameter(labelPerPorta,
					PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MESSAGE_SECURITY, pId, pIdSoggetto, pIdAsps, pIdFrizione	));

			this.pd.setSearchLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME);
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE_FLOW_DI, // + idporta,
						null));
			}
			else{
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE_FLOW_DI, // + idporta,
						PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE_LIST, pId, pIdSoggetto, pNomePorta, pIdAsps, pIdFrizione	));
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_RISULTATI_RICERCA, null));

			}

			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE_FLOW_DI, search);
			}

			// setto le label delle colonne
			String[] labels = { 
					PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME,
					PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_VALORE
			};
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<MessageSecurityFlowParameter> it = lista.iterator();
				while (it.hasNext()) {
					MessageSecurityFlowParameter wsrfp = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE_CHANGE,
							pId, pIdSoggetto, pIdAsps, pIdFrizione,
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME, wsrfp.getNome())
							);
					de.setValue(wsrfp.getNome());
					de.setIdToRemove(wsrfp.getNome());
					e.addElement(de);

					de = new DataElement();
					de.setValue(wsrfp.getValore());
					e.addElement(de);

					dati.addElement(e);
				}
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}


	// Prepara la lista di correlazioni applicative delle porte delegate
	public void preparePorteDelegateCorrAppList(String nomePorta, ISearch ricerca, List<CorrelazioneApplicativaElemento> lista)
			throws Exception {
		try {
			String id = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			String idsogg = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			String idAsps = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";
			
			String idFruizione = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE);
			if(idFruizione == null)
				idFruizione = "";

			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, this.session);
			if(parentPD == null) parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;

			Parameter pId = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, id);
			Parameter pIdSoggetto = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg);
			Parameter pIdAsps = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS, idAsps);
			Parameter pIdFrizione = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE, idFruizione);
			ServletUtils.addListElementIntoSession(this.session, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_REQUEST, pId, pIdSoggetto, pIdAsps, pIdFrizione);

			int idLista = Liste.PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			PortaDelegata myPD = this.porteDelegateCore.getPortaDelegata(Integer.parseInt(id));
			String idporta = myPD.getNome();

			// setto la barra del titolo
			List<Parameter> lstParam = this.getTitoloPD(parentPD, idsogg, idAsps, idFruizione);
			
			Parameter pNomePorta = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME, idporta);
			String labelPerPorta = null;
			if(parentPD!=null && (parentPD.intValue() == PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = this.porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONI_APPLICATIVE_CONFIG_DI,
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TRACCIAMENTO,
						myPD);
			}
			else {
				labelPerPorta = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONI_APPLICATIVE_CONFIG_DI+idporta;
			}
			lstParam.add(new Parameter(labelPerPorta,
					PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA, pIdSoggetto, pId, pNomePorta, pIdAsps, pIdFrizione));

			this.pd.setSearchLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_ELEMENTO_XML);
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONI_APPLICATIVE_RICHIESTA_DI, // + idporta,
						null));
			}
			else{
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONI_APPLICATIVE_RICHIESTA_DI, // + idporta,
						PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_REQUEST_LIST, pId, pIdSoggetto, pNomePorta, pIdAsps, pIdFrizione	));
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_RISULTATI_RICERCA, null));
			}

			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONI_APPLICATIVE_RICHIESTA_DI, search);
			}

			// setto le label delle colonne
			String[] labels = {
					PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_ELEMENTO_XML,
					PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MODALITA_IDENTIFICAZIONE};
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<CorrelazioneApplicativaElemento> it = lista.iterator();
				while (it.hasNext()) {
					CorrelazioneApplicativaElemento cae = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					de.setType(DataElementType.HIDDEN);
					de.setValue("" + cae.getId());
					e.addElement(de);

					de = new DataElement();
					de.setUrl(
							PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_REQUEST_CHANGE,
							pId,
							pIdSoggetto,	
							pIdAsps,
							pIdFrizione,
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_CORRELAZIONE, ""+ cae.getId())
							);
					//String nomeElemento = "(*)";
					String nomeElemento = CostantiControlStation.LABEL_PORTE_CORRELAZIONE_APPLICATIVA_QUALSIASI;
					if (cae.getNome() != null && !"".equals(cae.getNome()))
						nomeElemento = cae.getNome();
					de.setValue(nomeElemento);
					de.setIdToRemove("" + cae.getId());
					e.addElement(de);

					de = new DataElement();
					if(cae.getIdentificazione()!=null)
						de.setValue(cae.getIdentificazione().toString());
					e.addElement(de);

					dati.addElement(e);
				}
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	// Controlla i dati del message-security request-flow della porta delegata
	public boolean porteDelegateMessageSecurityRequestCheckData(TipoOperazione tipoOp) throws Exception {
		try {
			String id = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			int idInt = Integer.parseInt(id);
			String nome = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME);
			String valore = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_VALORE);

			// Campi obbligatori
			if (nome.equals("") || valore.equals("")) {
				String tmpElenco = "";
				if (nome.equals("")) {
					tmpElenco = "Nome";
				}
				if (valore.equals("")) {
					if (tmpElenco.equals("")) {
						tmpElenco = "Valore";
					} else {
						tmpElenco = tmpElenco + ", Valore";
					}
				}
				this.pd.setMessage("Dati incompleti. E' necessario indicare: " + tmpElenco);
				return false;
			}

			// Controllo che non ci siano spazi nei campi di testo
			//if ((nome.indexOf(" ") != -1) || (valore.indexOf(" ") != -1)) {
			if ((nome.indexOf(" ") != -1) ) {
				this.pd.setMessage("Non inserire spazi nei nomi");
				return false;
			}
			if(valore.startsWith(" ") || valore.endsWith(" ")){
				this.pd.setMessage("Non inserire spazi all'inizio o alla fine dei valori");
				return false;
			}
			if(this.checkLength255(nome, PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME)==false) {
				return false;
			}

			// Se tipoOp = add, controllo che il message-security non sia gia' stato
			// registrato per la porta delegata
			if (tipoOp.equals(TipoOperazione.ADD)) {
				boolean giaRegistrato = false;
				PortaDelegata pde = this.porteDelegateCore.getPortaDelegata(idInt);
				String nomeporta = pde.getNome();
				MessageSecurity messageSecurity = pde.getMessageSecurity();

				if(messageSecurity!=null){
					if(messageSecurity.getRequestFlow()!=null){
						for (int i = 0; i < messageSecurity.getRequestFlow().sizeParameterList(); i++) {
							MessageSecurityFlowParameter tmpMessageSecurity =messageSecurity.getRequestFlow().getParameter(i);
							if (nome.equals(tmpMessageSecurity.getNome())) {
								giaRegistrato = true;
								break;
							}
						}
					}
				}

				if (giaRegistrato) {
					this.pd.setMessage("La proprieta' di message-security " + nome + " &egrave; gi&agrave; stato associata alla porta delegata " + nomeporta);
					return false;
				}
			}

			return true;

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	// Prepara la lista di Message-Security request-flow delle porte delegate
	public void preparePorteDelegateMessageSecurityRequestList(String nomePorta, ISearch ricerca, List<MessageSecurityFlowParameter> lista)
			throws Exception {
		try {

			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, this.session);
			if(parentPD == null) parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;

			String id = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			String idsogg = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			String idAsps = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";
			
			String idFruizione = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE);
			if(idFruizione == null)
				idFruizione = "";
			
			Parameter pId = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, id);
			Parameter pIdSoggetto = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg);
			Parameter pIdAsps = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS, idAsps);
			Parameter pIdFrizione = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE, idFruizione);

			ServletUtils.addListElementIntoSession(this.session, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST, pId, pIdSoggetto, pIdAsps, pIdFrizione);

			int idLista = Liste.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			PortaDelegata myPD = this.porteDelegateCore.getPortaDelegata(Integer.parseInt(id));
			String idporta = myPD.getNome();

			// setto la barra del titolo
			List<Parameter> lstParam = this.getTitoloPD(parentPD, idsogg, idAsps, idFruizione);
			
			Parameter pNomePorta = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME, idporta);

			String labelPerPorta = null;
			if(parentPD!=null && (parentPD.intValue() == PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = this.porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_CONFIG_DI,
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY,
						myPD);
			}
			else {
				labelPerPorta = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_CONFIG_DI+idporta;
			}
			lstParam.add(new Parameter(labelPerPorta,
					PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MESSAGE_SECURITY, pId, pIdSoggetto, pNomePorta, pIdAsps, pIdFrizione	));

			this.pd.setSearchLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME);
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_FLOW_DI, // + idporta,
						null));
			}
			else{
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_FLOW_DI, // + idporta,
						PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_LIST, pId, pIdSoggetto, pNomePorta, pIdAsps, pIdFrizione	));
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_RISULTATI_RICERCA, null));
			}


			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_FLOW_DI, search);
			}

			// setto le label delle colonne
			String[] labels = {
					PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME,
					PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_VALORE		
			};
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<MessageSecurityFlowParameter> it = lista.iterator();
				while (it.hasNext()) {
					MessageSecurityFlowParameter wsrfp = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_CHANGE ,pId, pIdSoggetto, pIdAsps, pIdFrizione,
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME, wsrfp.getNome())
							);
					de.setValue(wsrfp.getNome());
					de.setIdToRemove(wsrfp.getNome());
					e.addElement(de);

					de = new DataElement();
					de.setValue(wsrfp.getValore());
					e.addElement(de);

					dati.addElement(e);
				}
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	public void preparePorteDelegateCorrAppRispostaList(String nomePorta, ISearch ricerca, List<CorrelazioneApplicativaRispostaElemento> lista)
			throws Exception {
		try {

			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, this.session);
			if(parentPD == null) parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;
			
			String id = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			String idsogg = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			String idAsps = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";
			
			String idFruizione = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE);
			if(idFruizione == null)
				idFruizione = "";
			
			Parameter pId = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, id);
			Parameter pIdSoggetto = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg);
			Parameter pIdAsps = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS, idAsps);
			Parameter pIdFrizione = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE, idFruizione);

			ServletUtils.addListElementIntoSession(this.session, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_RESPONSE, pId, pIdSoggetto, pIdAsps, pIdFrizione);

			int idLista = Liste.PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_RISPOSTA;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			PortaDelegata myPD = this.porteDelegateCore.getPortaDelegata(Integer.parseInt(id));
			String idporta = myPD.getNome();

			// setto la barra del titolo
			List<Parameter> lstParam = this.getTitoloPD(parentPD, idsogg, idAsps, idFruizione);
			
			Parameter pNomePorta = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME, idporta);
			String labelPerPorta = null;
			if(parentPD!=null && (parentPD.intValue() == PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = this.porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONI_APPLICATIVE_CONFIG_DI,
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TRACCIAMENTO,
						myPD);
			}
			else {
				labelPerPorta = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONI_APPLICATIVE_CONFIG_DI+idporta;
			}
			lstParam.add(new Parameter(labelPerPorta,
						PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA, pId, pIdSoggetto, pNomePorta, pIdAsps, pIdFrizione, pIdFrizione	));

			this.pd.setSearchLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_ELEMENTO_XML);
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONI_APPLICATIVE_RISPOSTA_DI, // + idporta,
						null));
			}
			else{
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONI_APPLICATIVE_RISPOSTA_DI, // + idporta,
						PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_RESPONSE_LIST, pId, pIdSoggetto, pNomePorta, pIdAsps, pIdFrizione	));
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_RISULTATI_RICERCA, null));
			}

			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONI_APPLICATIVE_RISPOSTA_DI, search);
			}

			// setto le label delle colonne
			String[] labels = { 
					PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_ELEMENTO_XML,
					PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MODALITA_IDENTIFICAZIONE
			};
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<CorrelazioneApplicativaRispostaElemento> it = lista.iterator();
				while (it.hasNext()) {
					CorrelazioneApplicativaRispostaElemento cae = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					de.setType(DataElementType.HIDDEN);
					de.setValue("" + cae.getId());
					e.addElement(de);

					de = new DataElement();
					de.setUrl(
							PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_RESPONSE_CHANGE, pId, pIdSoggetto, pIdAsps, pIdFrizione,
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_CORRELAZIONE, cae.getId() + "")
							);
					//String nomeElemento = "(*)";
					String nomeElemento = CostantiControlStation.LABEL_PORTE_CORRELAZIONE_APPLICATIVA_QUALSIASI;
					if (cae.getNome() != null && !"".equals(cae.getNome()))
						nomeElemento = cae.getNome();
					de.setValue(nomeElemento);
					de.setIdToRemove("" + cae.getId());
					e.addElement(de);

					de = new DataElement();
					if(cae.getIdentificazione()!=null)
						de.setValue(cae.getIdentificazione().toString());
					e.addElement(de);

					dati.addElement(e);
				}
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	// Prepara la lista di MTOM request-flow delle porte delegate
	public void preparePorteDelegateMTOMRequestList(String nomePorta, ISearch ricerca, List<MtomProcessorFlowParameter> lista)	throws Exception {
		try {

			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, this.session);
			if(parentPD == null) parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;
			
			String id = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			String idsogg = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			String idAsps = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";
			
			String idFruizione = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE);
			if(idFruizione == null)
				idFruizione = "";
			
			Parameter pId = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, id);
			Parameter pIdSoggetto = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg);
			Parameter pIdAsps = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS, idAsps);
			Parameter pIdFrizione = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE, idFruizione);

			ServletUtils.addListElementIntoSession(this.session, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_MTOM_REQUEST, pId, pIdSoggetto, pIdAsps, pIdFrizione);

			int idLista = Liste.PORTE_DELEGATE_MTOM_REQUEST;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			PortaDelegata myPD = this.porteDelegateCore.getPortaDelegata(Integer.parseInt(id));
			String idporta = myPD.getNome();

			// setto la barra del titolo
			List<Parameter> lstParam = this.getTitoloPD(parentPD, idsogg, idAsps, idFruizione);
			
			Parameter pNomePorta = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME, idporta);

			String labelPerPorta = null;
			if(parentPD!=null && (parentPD.intValue() == PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = this.porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM_CONFIG_DI,
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM_CONFIG,
						myPD);
			}
			else {
				labelPerPorta = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM_CONFIG_DI+idporta;
			}
			lstParam.add(new Parameter(labelPerPorta,
					PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MTOM,pId, pIdSoggetto, pNomePorta, pIdAsps, pIdFrizione));
			
			this.pd.setSearchLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME);
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM_REQUEST_FLOW_DI, // + idporta,
						null));
			}
			else{
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM_REQUEST_FLOW_DI, // + idporta,
						PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MTOM_REQUEST_LIST,pId, pIdSoggetto, pNomePorta, pIdAsps, pIdFrizione));
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_RISULTATI_RICERCA, null));
			}


			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM_REQUEST_FLOW_DI, search);
			}

			// setto le label delle colonne
			String[] labels = {
					PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME,
			};
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<MtomProcessorFlowParameter> it = lista.iterator();
				while (it.hasNext()) {
					MtomProcessorFlowParameter parametro = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MTOM_REQUEST_CHANGE ,pId, pIdSoggetto, pIdAsps, pIdFrizione,
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME, parametro.getNome())
							);
					de.setValue(parametro.getNome());
					de.setIdToRemove(parametro.getNome());
					e.addElement(de);
					dati.addElement(e);
				}
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}


	// Prepara la lista di MTOM response-flow delle porte delegate
	public void preparePorteDelegateMTOMResponseList(String nomePorta, ISearch ricerca,
			List<MtomProcessorFlowParameter> lista)
					throws Exception {
		try {
			String id = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			String idsogg = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			String idAsps = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";
			
			String idFruizione = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE);
			if(idFruizione == null)
				idFruizione = "";
			
			Parameter pId = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, id);
			Parameter pIdSoggetto = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg);
			Parameter pIdAsps = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS, idAsps);
			Parameter pIdFrizione = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE, idFruizione);

			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, this.session);
			if(parentPD == null) parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;
			
			ServletUtils.addListElementIntoSession(this.session, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_MTOM_RESPONSE, pId, pIdSoggetto, pIdAsps, pIdFrizione);

			int idLista = Liste.PORTE_DELEGATE_MTOM_RESPONSE;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			PortaDelegata myPD = this.porteDelegateCore.getPortaDelegata(Integer.parseInt(id));
			String idporta = myPD.getNome();

			// setto la barra del titolo
			List<Parameter> lstParam = this.getTitoloPD(parentPD, idsogg, idAsps, idFruizione);
			
			Parameter pNomePorta = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME, idporta);
			
			String labelPerPorta = null;
			if(parentPD!=null && (parentPD.intValue() == PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = this.porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM_CONFIG_DI,
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM_CONFIG,
						myPD);
			}
			else {
				labelPerPorta = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM_CONFIG_DI+idporta;
			}
			lstParam.add(new Parameter(labelPerPorta,
					PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MTOM,pId, pIdSoggetto, pNomePorta, pIdAsps, pIdFrizione));

			this.pd.setSearchLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME);
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM_RESPONSE_FLOW_DI, // + idporta,
						null));
			}
			else{
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM_RESPONSE_FLOW_DI, // + idporta,
						PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MTOM_RESPONSE_LIST,pId, pIdSoggetto, pNomePorta, pIdAsps, pIdFrizione));
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_RISULTATI_RICERCA, null));
			}

			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM_RESPONSE_FLOW_DI, search);
			}

			// setto le label delle colonne
			String[] labels = { 
					PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME,
			};
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<MtomProcessorFlowParameter> it = lista.iterator();
				while (it.hasNext()) {
					MtomProcessorFlowParameter wsrfp = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MTOM_RESPONSE_CHANGE,pId, pIdSoggetto, pIdAsps, pIdFrizione,
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME, wsrfp.getNome())
							);
					de.setValue(wsrfp.getNome());
					de.setIdToRemove(wsrfp.getNome());
					e.addElement(de);

					dati.addElement(e);
				}
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public List<Parameter> getTitoloPD(Integer parentPD, String idSoggettoFruitore, String idAsps, String idFruizione)	throws Exception, DriverRegistroServiziNotFound, DriverRegistroServiziException {
		List<Parameter> lstParam = new ArrayList<>();
		
		String tipoSoggettoFruitore = null;
		String nomeSoggettoFruitore = null;
		if(this.core.isRegistroServiziLocale()){
			org.openspcoop2.core.registry.Soggetto soggettoFruitore = this.soggettiCore.getSoggettoRegistro(Integer.parseInt(idSoggettoFruitore));
			tipoSoggettoFruitore = soggettoFruitore.getTipo();
			nomeSoggettoFruitore = soggettoFruitore.getNome();
		}else{
			org.openspcoop2.core.config.Soggetto soggettoFruitore = this.soggettiCore.getSoggetto(Integer.parseInt(idSoggettoFruitore));
			tipoSoggettoFruitore = soggettoFruitore.getTipo();
			nomeSoggettoFruitore = soggettoFruitore.getNome();
		}
		
		String protocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(tipoSoggettoFruitore);
		
		switch (parentPD) {
		case PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE:
			// Prendo il nome e il tipo del servizio
			AccordoServizioParteSpecifica asps = this.apsCore.getAccordoServizioParteSpecifica(Integer.parseInt(idAsps));
			String servizioTmpTile = this.getLabelServizioFruizione(protocollo, new IDSoggetto(tipoSoggettoFruitore, nomeSoggettoFruitore), asps);
			
			String tipologia = ServletUtils.getObjectFromSession(this.session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
			boolean gestioneFruitori = false;
			if(tipologia!=null) {
				if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE.equals(tipologia)) {
					gestioneFruitori = true;
				}
			}
			
			Parameter pIdServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, asps.getId()+ "");
			Parameter pIdFruizione = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MY_ID, idFruizione+ "");
			Parameter pIdSoggettoFruitore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO, idSoggettoFruitore);
			Parameter pNomeServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO, asps.getNome());
			Parameter pTipoServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO, asps.getTipo());
			Parameter pTipoSoggettoFruitore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SOGGETTO_FRUITORE, tipoSoggettoFruitore);
			Parameter pNomeSoggettoFruitore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SOGGETTO_FRUITORE, nomeSoggettoFruitore);
			
			if(gestioneFruitori) {
				Boolean vistaErogazioni = ServletUtils.getBooleanAttributeFromSession(ErogazioniCostanti.ASPS_EROGAZIONI_ATTRIBUTO_VISTA_EROGAZIONI, this.session);
				if(vistaErogazioni != null && vistaErogazioni.booleanValue()) {
					lstParam.add(new Parameter(ErogazioniCostanti.LABEL_ASPS_FRUIZIONI, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_LIST));
					lstParam.add(new Parameter(servizioTmpTile, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_CHANGE, 
							pIdServizio,pNomeServizio, pTipoServizio, pTipoSoggettoFruitore, pNomeSoggettoFruitore));
					boolean gestioneGruppi = true;
					String paramGestioneGruppi = ServletUtils.getObjectFromSession(this.session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_GESTIONE_GRUPPI);
					if(paramGestioneGruppi!=null && !"".equals(paramGestioneGruppi)) {
						gestioneGruppi = Boolean.valueOf(paramGestioneGruppi);
					}
					
					boolean gestioneConfigurazioni = true;
					String paramGestioneConfigurazioni = ServletUtils.getObjectFromSession(this.session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_GESTIONE_CONFIGURAZIONI);
					if(paramGestioneConfigurazioni!=null && !"".equals(paramGestioneConfigurazioni)) {
						gestioneConfigurazioni = Boolean.valueOf(paramGestioneConfigurazioni);
					}
					
					AccordoServizioParteComune as = this.apcCore.getAccordoServizio(asps.getIdAccordo());
					ServiceBinding serviceBinding = this.apcCore.toMessageServiceBinding(as.getServiceBinding());
					String labelConfigurazione = gestioneConfigurazioni ? ErogazioniCostanti.LABEL_ASPS_GESTIONE_CONFIGURAZIONI : 
						(gestioneGruppi ? MessageFormat.format(ErogazioniCostanti.LABEL_ASPS_GESTIONE_GRUPPI_CON_PARAMETRO, this.getLabelAzioni(serviceBinding)) : AccordiServizioParteSpecificaCostanti.LABEL_APS_PORTE_APPLICATIVE);
					
					lstParam.add(new Parameter(labelConfigurazione, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_PORTE_DELEGATE_LIST ,
							pIdFruizione,pIdServizio,pIdSoggettoFruitore, pTipoSoggettoFruitore, pNomeSoggettoFruitore));
					
				}else {
					lstParam.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_FRUITORI, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
					//	lstParam.add(new Parameter(servizioTmpTile, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE, pIdServizio,pNomeServizio, pTipoServizio));
					lstParam.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_CONFIGURAZIONI_DI + servizioTmpTile, 
							AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_PORTE_DELEGATE_LIST ,
							pIdFruizione,pIdServizio,pIdSoggettoFruitore, pTipoSoggettoFruitore, pNomeSoggettoFruitore));
				}
			}
			else {
			
				Parameter pIdSoggettoErogatore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, asps.getIdSoggetto()+"");
				//Parameter pIdProviderSoggettoFruitore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROVIDER_FRUITORE, idSoggettoFruitore);
				
				String fruizioneTmpTile = this.getLabelNomeSoggetto(protocollo, tipoSoggettoFruitore,nomeSoggettoFruitore);
				
				lstParam.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
				lstParam.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_FUITORI_DI  + servizioTmpTile, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_LIST , pIdServizio,pIdSoggettoErogatore));
				//lstParam.add(new Parameter(fruizioneTmpTile, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE, pIdServizio,pIdFruizione,pIdProviderSoggettoFruitore));
				lstParam.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_CONFIGURAZIONI_DI + fruizioneTmpTile, 
						AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_PORTE_DELEGATE_LIST ,pIdFruizione,pIdServizio,pIdSoggettoFruitore));
				
			}
				
			break;
		case PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_SOGGETTO:
			String soggettoTitle =  this.getLabelNomeSoggetto(protocollo, tipoSoggettoFruitore,nomeSoggettoFruitore);
			lstParam.add(new Parameter(SoggettiCostanti.LABEL_SOGGETTI, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST));
			lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PORTE_DELEGATE_DI + soggettoTitle, PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST ,
					new Parameter( PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idSoggettoFruitore)));
			break;
		case PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE:
		default:
			lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PORTE_DELEGATE, PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST));
			break;
		}
		return lstParam;
	}
	
	public String getPortaDelegataAzioneIdentificazioneLabel(String pdAiString) {
		if(pdAiString == null)
			return "";
		
		return getPortaDelegataAzioneIdentificazioneLabel(PortaDelegataAzioneIdentificazione.toEnumConstant(pdAiString));
	}
	

	public String getPortaDelegataAzioneIdentificazioneLabel(PortaDelegataAzioneIdentificazione pdAi) {
		if(pdAi == null)
			return "";
		switch (pdAi) {
		case CONTENT_BASED:
			return PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MODE_CONTENT_BASED;
		case HEADER_BASED:
			return PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MODE_HEADER_BASED;
		case INPUT_BASED:
			return PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MODE_INPUT_BASED;
		case INTERFACE_BASED:
			return PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MODE_WSDL_BASED;
		case SOAP_ACTION_BASED:
			return PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MODE_SOAP_ACTION_BASED;
		case STATIC:
			return PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT;
		case URL_BASED:
			return PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MODE_URL_BASED;
		case DELEGATED_BY:
		default:
			break;
		}
		
		return "";
	}

	public void preparePorteDelPropList(String nomePorta, Search ricerca, List<Proprieta> lista) throws Exception {
		try {
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, this.session);
			if(parentPD == null) parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;
			
			String id = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			String idsogg = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			String idAsps = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";
			
			String idFruizione = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE);
			if(idFruizione == null)
				idFruizione = "";
			
			Parameter pId = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, id);
			Parameter pIdSoggetto = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg);
			Parameter pIdAsps = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS, idAsps);
			Parameter pIdFruizione = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE, idFruizione);

			ServletUtils.addListElementIntoSession(this.session, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_PROPRIETA_PROTOCOLLO, pId, pIdSoggetto, pIdAsps, pIdFruizione);

			int idLista = Liste.PORTE_DELEGATE_PROP;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			PortaDelegata myPD = this.porteDelegateCore.getPortaDelegata(Integer.parseInt(id));
			String idporta = myPD.getNome();

			List<Parameter> lstParam = this.getTitoloPD(parentPD, idsogg, idAsps, idFruizione);
			
			String labelPerPorta = null;
			if(parentPD!=null && (parentPD.intValue() == PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = this.porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PROTOCOL_PROPERTIES_CONFIG_DI,
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PROTOCOL_PROPERTIES,
						myPD);
			}
			else {
				labelPerPorta = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PROTOCOL_PROPERTIES_CONFIG_DI+idporta;
			}
			
			Parameter pNomePorta = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME, idporta);

			this.pd.setSearchLabel(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME);
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(labelPerPorta,null));
			}
			else{
				lstParam.add(new Parameter(labelPerPorta,
						PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_PROPRIETA_PROTOCOLLO_LIST, pId, pIdSoggetto, pNomePorta, pIdAsps, pIdFruizione	));
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_RISULTATI_RICERCA, null));

			}

			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PROTOCOL_PROPERTIES, search);
			}
			
			// setto le label delle colonne
			String valueLabel = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_VALORE;
			String[] labels = { PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME, valueLabel };
			this.pd.setLabels(labels);
			
			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<Proprieta> it = lista.iterator();
				while (it.hasNext()) {
					Proprieta ssp = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_PROPRIETA_PROTOCOLLO_CHANGE, pId,pIdSoggetto, pIdAsps, pIdFruizione, new Parameter( PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME, ssp.getNome()));
					de.setValue(ssp.getNome());
					de.setIdToRemove(ssp.getNome());
					e.addElement(de);

					de = new DataElement();
					if(ssp.getValore()!=null)
						de.setValue(ssp.getValore().toString());
					e.addElement(de);

					dati.addElement(e);
				}
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	public Vector<DataElement> addProprietaProtocolloToDati(TipoOperazione tipoOp, int size, String nome, String valore,
			Vector<DataElement> dati) {
		DataElement de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PROTOCOL_PROPERTIES);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME);
		de.setValue(nome);
		if(TipoOperazione.ADD.equals(tipoOp)){
			de.setType(DataElementType.TEXT_EDIT);
			de.setRequired(true);
		}
		else{
			de.setType(DataElementType.TEXT);
		}
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME);
		de.setSize(size);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_VALORE);
		de.setType(DataElementType.TEXT_EDIT);
		de.setRequired(true);
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_VALORE);
		de.setValue(valore);
		de.setSize(size);
		dati.addElement(de);

		return dati;
		
	}

	public boolean porteAppPropCheckData(TipoOperazione tipoOp) throws Exception {
		try {
			String idPorta = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			int idInt = Integer.parseInt(idPorta);
			String nome = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME);
			String valore = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_VALORE);

			// Campi obbligatori
			if (nome.equals("") || valore.equals("")) {
				String tmpElenco = "";
				if (nome.equals("")) {
					tmpElenco = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME;
				}
				if (valore.equals("")) {
					if (tmpElenco.equals("")) {
						tmpElenco = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_VALORE;
					} else {
						tmpElenco = tmpElenco + ", " + PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_VALORE;
					}
				}
				this.pd.setMessage(MessageFormat.format(PorteDelegateCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX, tmpElenco));
				return false;
			}

			// Controllo che non ci siano spazi nei campi di testo
			if ((nome.indexOf(" ") != -1) || (valore.indexOf(" ") != -1)) {
				this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_NON_INSERIRE_SPAZI_NEI_CAMPI_DI_TESTO);
				return false;
			}
			
			// Check Lunghezza
			if(this.checkLength255(nome, PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME)==false) {
				return false;
			}
			if(this.checkLength255(valore, PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_VALORE)==false) {
				return false;
			}

			// Se tipoOp = add, controllo che la property non sia gia'
			// stata
			// registrata per la porta applicativa
			if (tipoOp.equals(TipoOperazione.ADD)) {
				boolean giaRegistrato = false;
				PortaDelegata portaDelegata = this.porteDelegateCore.getPortaDelegata(idInt);
				String nomeporta = portaDelegata.getNome();

				for (int i = 0; i < portaDelegata.sizeProprietaList(); i++) {
					Proprieta tmpProp = portaDelegata.getProprieta(i);
					if (nome.equals(tmpProp.getNome())) {
						giaRegistrato = true;
						break;
					}
				}

				if (giaRegistrato) {
					this.pd.setMessage(MessageFormat.format(
							PorteDelegateCostanti.MESSAGGIO_ERRORE_LA_PROPERTY_XX_E_GIA_STATA_ASSOCIATA_ALLA_PORTA_DELEGATA_YY, nome,
							nomeporta));
					return false;
				}
			}

			return true;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public String getMessaggioConfermaModificaRegolaMappingFruizionePortaDelegata(PortaDelegata pd, ServiceBinding serviceBinding, boolean abilitazione, boolean multiline,boolean listElement) throws DriverConfigurazioneException {
		MappingFruizionePortaDelegata mapping = this.porteDelegateCore.getMappingFruizionePortaDelegata(pd);
		List<String> listaAzioni = pd.getAzione()!= null ?  pd.getAzione().getAzioneDelegataList() : new ArrayList<String>();
		return this.getMessaggioConfermaModificaRegolaMapping(mapping.isDefault(), listaAzioni, serviceBinding, abilitazione, multiline, listElement);
	}
}
