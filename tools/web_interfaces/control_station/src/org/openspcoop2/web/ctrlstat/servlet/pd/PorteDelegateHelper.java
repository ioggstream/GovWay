/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.CorrelazioneApplicativaElemento;
import org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento;
import org.openspcoop2.core.config.MessageSecurity;
import org.openspcoop2.core.config.MessageSecurityFlowParameter;
import org.openspcoop2.core.config.MtomProcessorFlowParameter;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataServizioApplicativo;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import org.openspcoop2.core.config.constants.MTOMProcessorType;
import org.openspcoop2.core.config.constants.TipoAutorizzazione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.sdk.constants.FunzionalitaProtocollo;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.costanti.IdentificazioneView;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationNotFound;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedListServlet;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneUtilities;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
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
public class PorteDelegateHelper extends ConsoleHelper {

	public PorteDelegateHelper(HttpServletRequest request, PageData pd, 
			HttpSession session) throws Exception {
		super(request, pd,  session);
	}
	
	public Vector<DataElement> addPorteDelegateToDati(TipoOperazione tipoOp, String idsogg,
			String nomePorta, Vector<DataElement> dati, String idPorta,
			String descr, String autenticazione,
			String autorizzazione, String modesp, String soggid,
			String[] soggettiList, String[] soggettiListLabel, String sp,
			String tiposp, String patternErogatore, String modeservizio,
			String servid, String[] serviziList, String[] serviziListLabel,
			String servizio, String tiposervizio,String versioneServizio, String patternServizio,
			String modeaz, String azid, String[] azioniListLabel,
			String[] azioniList, String azione, String patternAzione,
			long totAzioni,  String stateless, String localForward, String ricsim,
			String ricasim, String xsd, String tipoValidazione,
			int numCorrApp, String scadcorr, String gestBody,
			String gestManifest, String integrazione, String autenticazioneOpzionale, String autenticazioneCustom,
			String autorizzazioneCustom,String autorizzazioneAutenticati,String autorizzazioneRuoli,String autorizzazioneRuoliTipologia,String autorizzazioneContenuti, String idsogg2, String protocollo,
			int numSA, int numRuoli, String ruoloMatch, String statoMessageSecurity,String statoMTOM ,int numCorrelazioneReq , 
			int numCorrelazioneRes,String forceWsdlBased, String applicaMTOM,
			boolean riusoId,
			AccordoServizioParteSpecifica asps, AccordoServizioParteComune aspc,ServiceBinding serviceBinding,
			String statoPorta) throws Exception {



//		Boolean confPers = (Boolean) this.session.getAttribute(CostantiControlStation.SESSION_PARAMETRO_GESTIONE_CONFIGURAZIONI_PERSONALIZZATE);
		Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);

		boolean configurazioneStandardNonApplicabile = false;
		
		int alternativeSize = 80;
		
		Parameter pIdSogg = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg);
		Parameter pIdPorta = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, idPorta);
		String idAsps = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
		if(idAsps == null) idAsps = "";
		Parameter pIdAsps = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS, idAsps);
		String idFruizione = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE);
		if(idFruizione == null) idFruizione = "";
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
		

		

		
		// *************** Dati Generali: Nome/Descrizione *********************
		
		de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_TITOLO_PORTE_DELEGATE_DATI_GENERALI);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME);
		de.setValue(nomePorta);
		if(this.isModalitaStandard() && TipoOperazione.CHANGE.equals(tipoOp) ){
			de.setType(DataElementType.TEXT);
		}
		else{
			de.setType(DataElementType.TEXT_EDIT);
			de.setRequired(true);
		}
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_PORTA);
		de.setSize(alternativeSize);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_DESCRIZIONE);
		de.setValue(descr);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_DESCRIZIONE);
		de.setSize(alternativeSize);
		dati.addElement(de);
		
		List<String> statoValues = new ArrayList<>();
		statoValues.add(CostantiConfigurazione.ABILITATO.toString());
		statoValues.add(CostantiConfigurazione.DISABILITATO.toString());
		de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_STATO_PORTA);
		de.setValues(statoValues);
		if(statoPorta==null || "".equals(statoPorta)){
			statoPorta = CostantiConfigurazione.ABILITATO.toString();
		}
		de.setSelected(statoPorta);
		de.setType(DataElementType.SELECT);
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_STATO_PORTA);
		dati.addElement(de);

		
		// *************** Dati Servizio *********************
		
		de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_TITOLO_PORTE_DELEGATE_DATI_SERVIZIO);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		
		
		// *************** Soggetto Erogatore *********************
		
		boolean configurazioneStandardSoggettoErogatore = false;
		
		de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SOGGETTO_EROGATORE);
		de.setType(DataElementType.SUBTITLE);
		dati.addElement(de);

		String[] tipoMode = {
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_USER_INPUT,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_URL_BASED,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_CONTENT_BASED,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_INPUT_BASED
		};
		String[] tipoModeSimple = { 
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_USER_INPUT,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_URL_BASED,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_CONTENT_BASED,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_INPUT_BASED
		};
		de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MODALITA_IDENTIFICAZIONE);
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_SP);
		if(TipoOperazione.CHANGE.equals(tipoOp)){
			if (this.isModalitaStandard()) {
				if(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT.equals(modesp) ){
					de.setType(DataElementType.HIDDEN);
					de.setValue(modesp);
					configurazioneStandardSoggettoErogatore = true;
				}
				else{
					de.setLabel(null);
					de.setValue(CostantiControlStation.LABEL_CONFIGURAZIONE_IMPOSTATA_MODALITA_AVANZATA_SHORT_MESSAGE);
					de.setType(DataElementType.TEXT);
					configurazioneStandardNonApplicabile = true;
				}
			}
			else{
				de.setType(DataElementType.SELECT);
				de.setValues(tipoMode);
				de.setSelected(modesp);
			}
		}
		else{
			de.setType(DataElementType.SELECT);
			de.setValues(tipoMode);
			de.setSelected(modesp);
		}
		//		de.setOnChange("CambiaModeSP('" + tipoOp + "', " + numCorrApp + ")");
		de.setPostBack(true);
		dati.addElement(de);

		if(!configurazioneStandardNonApplicabile){
			if (modesp != null && modesp.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
				de = new DataElement();
				de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME);
				de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SOGGETTO_ID);
				de.setPostBack(true);
				if(configurazioneStandardSoggettoErogatore){
					de.setType(DataElementType.HIDDEN);
					de.setValue(soggid);
					dati.addElement(de);
					
					de = new DataElement();
					de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME);
					de.setValue(soggid);
					de.setType(DataElementType.TEXT);
				}else{
					de.setType(DataElementType.SELECT);
					de.setValues(soggettiList);
					de.setLabels(soggettiListLabel);
					de.setSelected(soggid);
				}
				//			de.setOnChange("CambiaSoggPD('" + tipoOp + "', " + numCorrApp + ")");
				dati.addElement(de);
			} else {
	
				if (this.isModalitaStandard()) {
					de = new DataElement();
					de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TIPO);
					de.setValue(this.soggettiCore.getTipoSoggettoDefault());
					de.setType(DataElementType.HIDDEN);
					de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TIPO_SP);
					dati.addElement(de);
				} else {
					
					List<String> tipiSoggetto = this.soggettiCore.getTipiSoggettiGestitiProtocollo(protocollo);
					
					de = new DataElement();
					de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TIPO);
					de.setValues(tipiSoggetto);
					de.setValue(tiposp);
					de.setSelected(tiposp);
					de.setType(DataElementType.SELECT);
					de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TIPO_SP);
					de.setSize(alternativeSize);
					dati.addElement(de);
				}
	
				de = new DataElement();
				if (modesp != null && (modesp.equals(IdentificazioneView.URL_BASED.toString()) || modesp.equals(IdentificazioneView.CONTENT_BASED.toString()))) {
					de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PATTERN);
					de.setValue(patternErogatore);
				} else {
					de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME);
					de.setValue(sp);
				}
				if (modesp != null && !modesp.equals(IdentificazioneView.INPUT_BASED.toString())){
					de.setType(DataElementType.TEXT_EDIT);
					de.setRequired(true);
				}else
					de.setType(DataElementType.HIDDEN);
				de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SP);
				de.setSize(alternativeSize);
				dati.addElement(de);
			}
		}

		
		
		
		
		
		
		
		
		// *************** Servizio *********************
		
		boolean configurazioneStandardServizio = false;
		
		de = new DataElement();
		//if(this.core.isTerminologiaSICA_RegistroServizi()){
		//	de.setLabel("Accordo Servizio Parte Specifica");
		//}else{
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SERVIZIO);
		//}
		de.setType(DataElementType.SUBTITLE);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MODALITA_IDENTIFICAZIONE);
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_SERVIZIO);
		if(TipoOperazione.CHANGE.equals(tipoOp)){
			if (this.isModalitaStandard()) {
				if(!configurazioneStandardNonApplicabile && PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT.equals(modeservizio) ){
					de.setType(DataElementType.HIDDEN);
					de.setValue(modeservizio);
					configurazioneStandardServizio = true;
				}
				else{
					de.setLabel(null);
					de.setValue(CostantiControlStation.LABEL_CONFIGURAZIONE_IMPOSTATA_MODALITA_AVANZATA_SHORT_MESSAGE);
					de.setType(DataElementType.TEXT);
					configurazioneStandardNonApplicabile = true;
				}
			}
			else{
				de.setType(DataElementType.SELECT);
				if (modesp != null && modesp.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
					de.setValues(tipoMode);
				} else {
					de.setValues(tipoModeSimple);
				}
				de.setSelected(modeservizio);
			}
		}else{
			de.setType(DataElementType.SELECT);
			if (modesp != null && modesp.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
				de.setValues(tipoMode);
			} else {
				de.setValues(tipoModeSimple);
			}
			de.setSelected(modeservizio);
		}
		//if (modesp.equals("register-input")) {
		//			de.setOnChange("CambiaModeServizio('" + tipoOp + "', " + numCorrApp + ")");
		de.setPostBack(true);
		//}
		dati.addElement(de);

		if(!configurazioneStandardNonApplicabile){
			if (modeservizio!= null && modeservizio.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
				de = new DataElement();
				de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME);
				de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SERVIZIO_ID);
				de.setPostBack(true);
				if(configurazioneStandardServizio){
					de.setType(DataElementType.HIDDEN);
					de.setValue(servid);
					dati.addElement(de);
					
					de = new DataElement();
					de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME);
					de.setValue(servid);
					de.setType(DataElementType.TEXT);
				}else{
					de.setType(DataElementType.SELECT);
					de.setValues(serviziList);
					de.setLabels(serviziListLabel);
					de.setSelected(servid);
				}
				//			de.setOnChange("CambiaServPD('" + tipoOp + "', " + numCorrApp + ")");
				dati.addElement(de);
			} else {
	
				if (this.isModalitaStandard()) {
					de = new DataElement();
					de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TIPO);
					de.setValue(this.apsCore.getTipoServizioDefault(serviceBinding));
					de.setType(DataElementType.HIDDEN);
					de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TIPO_SERVIZIO);
					dati.addElement(de);
				} else {
					
					List<String> tipiServizio = this.apsCore.getTipiServiziGestitiProtocollo(protocollo,serviceBinding);
					
					de = new DataElement();
					de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TIPO_SERVIZIO);
					de.setValues(tipiServizio);
					de.setSelected(tiposervizio);
					de.setValue(tiposervizio);
					de.setType(DataElementType.SELECT);
					de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TIPO_SERVIZIO);
					de.setSize(alternativeSize);
					dati.addElement(de);
				}
	
				de = new DataElement();
				if (modeservizio!= null && (modeservizio.equals(IdentificazioneView.URL_BASED.toString()) || modeservizio.equals(IdentificazioneView.CONTENT_BASED.toString()))) {
					de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PATTERN);
					de.setValue(patternServizio);
				} else {
					de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME);
					de.setValue(servizio);
				}
				if (modeservizio!= null && !modeservizio.equals(IdentificazioneView.INPUT_BASED.toString())){
					de.setType(DataElementType.TEXT_EDIT);
					de.setRequired(true);
				}else
					de.setType(DataElementType.HIDDEN);
				de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SERVIZIO);
				de.setSize(alternativeSize);
				dati.addElement(de);
				
				de = new DataElement();
				de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_VERSIONE_SERVIZIO);
				de.setValue(tiposervizio);
				de.setType(DataElementType.TEXT_EDIT);
				de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_VERSIONE_SERVIZIO);
				de.setSize(alternativeSize);
				de.setRequired(true);
				dati.addElement(de);
			}
		}
		
		// *************** Azione *********************
		
		boolean configurazioneStandardAzione = false;
		
		// se servizio register-input e azioneList==null e
		// mode_azione=register-input allora nn c'e' azione
		String[] tipoModeAzione = {
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_USER_INPUT,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_URL_BASED,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_CONTENT_BASED,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_INPUT_BASED,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_SOAP_ACTION_BASED,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_WSDL_BASED
		};
		String[] tipoModeSimpleAzione = {
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_USER_INPUT,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_URL_BASED,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_CONTENT_BASED,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_INPUT_BASED,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_SOAP_ACTION_BASED,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_WSDL_BASED
		};
		if(TipoOperazione.CHANGE.equals(tipoOp) && this.isModalitaStandard() ){
			if ( !configurazioneStandardNonApplicabile && 
					(modeaz != null) && 
					(
					 modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_URL_BASED) ||
					 modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_USER_INPUT) ||
					 modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)
					)
				) {
				if( modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_URL_BASED) ){
					if( ServletUtils.isCheckBoxEnabled(forceWsdlBased) || CostantiRegistroServizi.ABILITATO.equals(forceWsdlBased) ){
						configurazioneStandardAzione = true;
					}
					else{
						configurazioneStandardNonApplicabile = true;
					}
				}
				else{
					configurazioneStandardAzione = true;
				}
			}
			else{
				configurazioneStandardNonApplicabile = true;
			}
		}
		if(configurazioneStandardNonApplicabile){
			
			de = new DataElement();
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_AZIONE);
			de.setType(DataElementType.SUBTITLE);
			dati.addElement(de);
			
			de = new DataElement();
			de.setValue(CostantiControlStation.LABEL_CONFIGURAZIONE_IMPOSTATA_MODALITA_AVANZATA_SHORT_MESSAGE);
			de.setType(DataElementType.TEXT);
			dati.addElement(de);
			
		}
		else{

			if(configurazioneStandardAzione){
				de = new DataElement();
				de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MODALITA_IDENTIFICAZIONE);
				de.setType(DataElementType.HIDDEN);
				de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_AZIONE);
				de.setValue(modeaz);
				dati.addElement(de);
				
				if( modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_URL_BASED) ){
					de = new DataElement();
					de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PATTERN);
					de.setValue(patternAzione);
					de.setType(DataElementType.HIDDEN);
					de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AZIONE);
					de.setSize(alternativeSize);
					dati.addElement(de);
				}
				else{
					if ((modeaz != null) && modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
						de = new DataElement();
						de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME);
						de.setType(DataElementType.HIDDEN);
						de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AZIONE_ID);
						de.setValue(azid);
						dati.addElement(de);
					}
					else{
						de = new DataElement();
						de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME);
						de.setValue(azione);
						de.setType(DataElementType.HIDDEN);
						de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AZIONE);
						de.setSize(alternativeSize);
						dati.addElement(de);
					}
				}
				
				de = new DataElement();
				de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_FORCE_WSDL_BASED);
				de.setType(DataElementType.HIDDEN);
				de.setValue(forceWsdlBased);
				de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_FORCE_WSDL_BASED);
				dati.addElement(de);
				
				de = new DataElement();
				de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_AZIONE);
				de.setType(DataElementType.SUBTITLE);
				dati.addElement(de);
				
				de = new DataElement();
				de.setType(DataElementType.TEXT);
				boolean tutteAzioni = false;
				if ((modeaz != null) && (modeaz.equals(IdentificazioneView.URL_BASED.toString()))){
					de.setValue(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_QUALSIASI_AZIONE);
					tutteAzioni = true;
				}
				else{
					// static o registry
					if(azione==null || "".equals(azione) || "-".equals(azione)){
						de.setValue(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_QUALSIASI_AZIONE);
						tutteAzioni = true;
					}else{
						de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME);
						de.setValue(azione);
					}
				}
				dati.addElement(de);
				
				if(TipoOperazione.CHANGE.equals(tipoOp)){
					if(tutteAzioni && asps!=null && aspc!=null){
						de = new DataElement();
						de.setType(DataElementType.LINK);
						String tipoAccordo = AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_PARTE_COMUNE;
						if(aspc.getServizioComposto()!=null){
							tipoAccordo = AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_SERVIZIO_COMPOSTO;
						}
						if(asps.getPortType()!=null && !"".equals(asps.getPortType()) && !"-".equals(asps.getPortType())){
							de.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_LIST, 
									new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, aspc.getId()+""),
									new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_NOME, asps.getPortType()),
									AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo)
									);
							org.openspcoop2.core.registry.PortType pt = null;
							for (org.openspcoop2.core.registry.PortType ptCheck : aspc.getPortTypeList()) {
								if(ptCheck.getNome().equals(asps.getPortType())){
									pt = ptCheck;
									break;
								}
							}
							if (contaListe) {
								ServletUtils.setDataElementVisualizzaLabel(de,(long)pt.sizeAzioneList());
							} else
								ServletUtils.setDataElementVisualizzaLabel(de);
						}
						else{
							de.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_AZIONI_LIST,
									new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, aspc.getId()+""),
									new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME, aspc.getNome()),
									AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo));
							if (contaListe) {
								ServletUtils.setDataElementVisualizzaLabel(de,(long)aspc.sizeAzioneList());
							} else
								ServletUtils.setDataElementVisualizzaLabel(de);
						}
						dati.addElement(de);
					}
				}
				
			}
			else{
				if (modeservizio!= null && !(modeservizio.equals(IdentificazioneView.REGISTER_INPUT.toString()) && ((modeaz != null) && modeaz.equals(IdentificazioneView.REGISTER_INPUT.toString()) && ((azioniList == null) || (azioniList.length == 0))))) {
					de = new DataElement();
					de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_AZIONE);
					de.setType(DataElementType.SUBTITLE);
					dati.addElement(de);
		
					de = new DataElement();
					de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MODALITA_IDENTIFICAZIONE);
					de.setType(DataElementType.SELECT);
					de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_AZIONE);
					if (modeservizio!= null && modeservizio.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
						de.setValues(tipoModeAzione);
					} else {
						de.setValues(tipoModeSimpleAzione);
					}
					de.setSelected(modeaz);
					//if (modeservizio.equals("register-input")) {
					//				de.setOnChange("CambiaModeAzione('" + tipoOp + "', " + numCorrApp + ")");
					de.setPostBack(true);
					//}
					dati.addElement(de);
		
					if ((modeaz != null) && modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
						de = new DataElement();
						de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME);
						de.setType(DataElementType.SELECT);
						de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AZIONE_ID);
						de.setValues(azioniList);
						de.setLabels(azioniListLabel);
						de.setSelected(azid);
						dati.addElement(de);
					} else {
		
						de = new DataElement();
						if ((modeaz != null) && (modeaz.equals(IdentificazioneView.URL_BASED.toString()) || modeaz.equals(IdentificazioneView.CONTENT_BASED.toString()))) {
							de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PATTERN);
							de.setValue(patternAzione);
							de.setRequired(true);
						} else {
							de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME);
							de.setValue(azione);
						}
		
						if (!IdentificazioneView.INPUT_BASED.toString().equals(modeaz) && 
								!IdentificazioneView.SOAP_ACTION_BASED.toString().equals(modeaz) && 
								!IdentificazioneView.INTERFACE_BASED.toString().equals(modeaz) ){
							de.setType(DataElementType.TEXT_EDIT);
						}else
							de.setType(DataElementType.HIDDEN);
						de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AZIONE);
						de.setSize(alternativeSize);
						dati.addElement(de);
					}
		
					// se non e' selezionata la modalita userInput / wsdlbased / registerInput faccio vedere il check box forceWsdlbased
					de = new DataElement();
					de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_FORCE_WSDL_BASED);
					if( this.isModalitaAvanzata() &&
							modeaz!= null && (
								!modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_USER_INPUT) &&
								!modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT) &&
								!modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_WSDL_BASED))
						){
		
						de.setType(DataElementType.CHECKBOX);
						if( ServletUtils.isCheckBoxEnabled(forceWsdlBased) || CostantiRegistroServizi.ABILITATO.equals(forceWsdlBased) ){
							de.setSelected(true);
						}
					}
					else{
						de.setType(DataElementType.HIDDEN);
						de.setValue(forceWsdlBased);
					}
					de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_FORCE_WSDL_BASED);
					dati.addElement(de);
		
				}
			}
		}
		
		
		
		
		
		
		
		
		// *************** Controllo degli Accessi *********************
		
		this.controlloAccessi(dati);
		
		// controllo accessi
		de = new DataElement();
		de.setType(DataElementType.LINK);
		de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CONTROLLO_ACCESSI, pIdSogg, pIdPorta, pIdAsps, pIdFruizione);
		String statoControlloAccessi = this.getLabelStatoControlloAccessi(autenticazioneCustom, autenticazioneOpzionale, autenticazioneCustom, autorizzazione, autorizzazioneContenuti,autorizzazioneCustom);
		ServletUtils.setDataElementCustomLabel(de, PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONTROLLO_ACCESSI, statoControlloAccessi);
		dati.addElement(de);
		
		// Pintori 11/12/2017 Gestione Accessi spostata nella servlet PorteDelegateControlloAccessi, lascio questo codice per eventuali ripensamenti.
//		this.controlloAccessi(dati);
//		
//		boolean isSupportatoAutenticazioneSoggetti = true; // sempre nelle porte delegate
//		
//		this.controlloAccessiAutenticazione(dati, autenticazione, autenticazioneCustom, autenticazioneOpzionale, confPers, isSupportatoAutenticazioneSoggetti);
//		
//		String urlAutorizzazioneAutenticati = null;
//		String urlAutorizzazioneRuoli = null;
//		if(TipoOperazione.CHANGE.equals(tipoOp)){
//			urlAutorizzazioneAutenticati = PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_SERVIZIO_APPLICATIVO_LIST +"?" + 
//					PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO + "=" + idsogg + "&" +
//					PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID + "=" + idPorta;
//			
//			urlAutorizzazioneRuoli = PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_RUOLI_LIST +"?" + 
//					PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO + "=" + idsogg + "&" +
//					PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID + "=" + idPorta;
//		}
//		
//		String servletChiamante = PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_ADD;
//		if (tipoOp == TipoOperazione.CHANGE) {
//			servletChiamante = PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CHANGE;
//		}
//		
//		this.controlloAccessiAutorizzazione(dati, tipoOp, servletChiamante,
//				autenticazione, autorizzazione, autorizzazioneCustom, 
//				autorizzazioneAutenticati, urlAutorizzazioneAutenticati, numSA, null, null,
//				autorizzazioneRuoli,  urlAutorizzazioneRuoli, numRuoli, null,
//				autorizzazioneRuoliTipologia, ruoloMatch,
//				confPers, isSupportatoAutenticazioneSoggetti, contaListe, true, false);
//		
//		this.controlloAccessiAutorizzazioneContenuti(dati, autorizzazioneContenuti);
		
		
		
		
		
		
		// *************** Integrazione *********************
		
		Vector<DataElement> deIntegrazione = new Vector<DataElement>();
		
		if (tipoOp == TipoOperazione.CHANGE) {

			de = new DataElement();
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_METADATI);
			de.setValue(integrazione);
			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_INTEGRAZIONE);
			de.setSize(alternativeSize);
			if(this.isModalitaStandard()){
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
		if(this.core.isShowJ2eeOptions()){
			if(configurazioneStandardNonApplicabile){
				de.setType(DataElementType.TEXT);
				if(stateless!=null && !"".equals(stateless)){
					de.setValue(stateless);
				}
				else{
					de.setValue(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_STATELESS_DEFAULT);
				}
			}else{
				de.setType(DataElementType.SELECT);
				de.setValues(tipoStateless);
				de.setSelected(stateless);
			}
			deIntegrazione.addElement(de);
		}else{
			de.setType(DataElementType.HIDDEN);
			de.setValue(stateless);
			dati.addElement(de);
		}
		

		// LocalForward
		boolean localForwardShow = true;
		Soggetto soggettoErogatoreLocalForward  = null;
		if (modesp != null && modesp.equals(IdentificazioneView.REGISTER_INPUT.toString()) ) {
			try{
				if(soggid!=null && soggid.contains("/")){
					soggettoErogatoreLocalForward = this.soggettiCore.getSoggettoRegistro(new IDSoggetto(soggid.split("/")[0], soggid.split("/")[1]));
				}
				else if(soggid!=null && !"".equals(soggid)){
					soggettoErogatoreLocalForward = this.soggettiCore.getSoggettoRegistro(Long.parseLong(soggid));
				}
			}catch(DriverRegistroServiziNotFound dNot){}
		}
		else if (modesp != null && modesp.equals(IdentificazioneView.USER_INPUT.toString()) ) {
			try{
				String tipoSoggetto = null;
				if (this.isModalitaStandard()) {
					tipoSoggetto = this.soggettiCore.getTipoSoggettoDefault();
				} else {
					tipoSoggetto = tiposp;
				}
				if(tipoSoggetto!=null && !"".equals(tipoSoggetto) && 
						sp!=null && !"".equals(sp)){
					soggettoErogatoreLocalForward = this.soggettiCore.getSoggettoRegistro(new IDSoggetto(tipoSoggetto, sp));
				}
			}catch(DriverRegistroServiziNotFound dNot){}
		}
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
		String[] tipoLocalForward = { PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_LOCAL_FORWARD_ABILITATO,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_LOCAL_FORWARD_DISABILITATO };
		de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_LOCAL_FORWARD);
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_LOCAL_FORWARD);
		de.setSize(alternativeSize);
		if (this.isModalitaStandard() || localForwardShow==false) {
			de.setType(DataElementType.HIDDEN);
			de.setValue(localForward);
			dati.addElement(de);
		}else{
			de.setType(DataElementType.SELECT);
			de.setValues(tipoLocalForward);
			de.setSelected(localForward);
			deIntegrazione.addElement(de);
		}
		

		if(deIntegrazione.size()>0){
			
			de = new DataElement();
			de.setType(DataElementType.TITLE);
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_INTEGRAZIONE);
			dati.addElement(de);
			
			for (int i = 0; i < deIntegrazione.size(); i++) {
				dati.addElement(deIntegrazione.get(i));
			}
		}
		
		
		
		
		
		
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
				ServletUtils.setDataElementCustomLabel(de,PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_RICHIESTA,new Long(numCorrelazioneReq));
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
				ServletUtils.setDataElementCustomLabel(de,PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_RISPOSTA,new Long(numCorrelazioneRes));
			} else
				ServletUtils.setDataElementCustomLabel(de,PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_RISPOSTA);

			dati.addElement(de);
		}
		
		
		
		
		
		
		
		
		
		// *************** Gestione Messaggio *********************
		
		if (!idsogg.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_NEW_ID) 
				&& !idPorta.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_NEW_ID)) {

			de = new DataElement();
			de.setType(DataElementType.TITLE);
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_GESTIONE_MESSAGGIO);
			dati.addElement(de);
			
			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA +"?" + 
					PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO + "=" + idsogg + "&"
					+ PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID + "=" + idPorta+ "&"
					+ PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME + "=" + nomePorta);
			String statoCorrelazioneApplicativa = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_DISABILITATA;
			if(numCorrelazioneReq>0 || numCorrelazioneRes>0){
				statoCorrelazioneApplicativa = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_ABILITATA;
			}
			ServletUtils.setDataElementCustomLabel(de, PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA, statoCorrelazioneApplicativa);
			dati.addElement(de);
			
			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MESSAGE_SECURITY +"?" + 
					PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO + "=" + idsogg + "&"
					+ PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID + "=" + idPorta);
			ServletUtils.setDataElementCustomLabel(de, PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY, statoMessageSecurity);
			dati.addElement(de);

			//if(InterfaceType.AVANZATA.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())){
			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MTOM +"?" + 
					PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO + "=" + idsogg + "&"
					+ PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID + "=" + idPorta);
			ServletUtils.setDataElementCustomLabel(de, PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM, statoMTOM);
			dati.addElement(de);
			//}
			
		}
		
		
		
		
		
		
		
		// *************** Validazione Contenuti *********************

		de = new DataElement();
		de.setType(DataElementType.TITLE);
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI);
		dati.addElement(de);

		String[] tipoXsd = { PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_XSD_ABILITATO,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_XSD_DISABILITATO,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_XSD_WARNING_ONLY };
		de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_STATO);
		de.setType(DataElementType.SELECT);
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_XSD);
		de.setValues(tipoXsd);
		//		de.setOnChange("CambiaModePD('" + tipoOp + "', " + numCorrApp + ")");
		de.setPostBack(true);
		de.setSelected(xsd);
		dati.addElement(de);

		if (PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_XSD_ABILITATO.equals(xsd) || 
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_XSD_WARNING_ONLY.equals(xsd)) {
			String[] tipi_validazione = { PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_VALIDAZIONE_XSD,
					PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_VALIDAZIONE_WSDL,
					PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_VALIDAZIONE_OPENSPCOOP };
			//String[] tipi_validazione = { "xsd", "wsdl" };
			de = new DataElement();
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TIPO);
			de.setType(DataElementType.SELECT);
			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TIPO_VALIDAZIONE);
			de.setValues(tipi_validazione);
			de.setSelected(tipoValidazione);
			dati.addElement(de);


			// Applica MTOM 
			de = new DataElement();
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_ACCETTA_MTOM);

			
			//if(InterfaceType.AVANZATA.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())){
			de.setType(DataElementType.CHECKBOX);
			if( ServletUtils.isCheckBoxEnabled(applicaMTOM) || CostantiRegistroServizi.ABILITATO.equals(applicaMTOM) ){
				de.setSelected(true);
			}
//			}
//			else{
//				de.setType(DataElementType.HIDDEN);
//				de.setValue(applicaMTOM);
//			}
		 
			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_APPLICA_MTOM);
			dati.addElement(de);

		}




		
		// *************** Asincroni *********************
		
		if (this.isModalitaStandard()) {

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

		if (this.isModalitaAvanzata()) {
		
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
			if(this.core.isFunzionalitaProtocolloSupportataDalProtocollo(protocollo, serviceBinding, FunzionalitaProtocollo.MANIFEST_ATTACHMENTS)){
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
		
		

	
		if(configurazioneStandardNonApplicabile){
			this.pd.setMessage(CostantiControlStation.LABEL_CONFIGURAZIONE_IMPOSTATA_MODALITA_AVANZATA_LONG_MESSAGE, Costanti.MESSAGE_TYPE_INFO);
			this.pd.disableEditMode();
		}
		
		
		
		return dati;
	}


	public Vector<DataElement> addPorteDelegateCorrelazioneApplicativaRequestToDati(TipoOperazione tipoOp,
			PageData pd,   String elemxml, String mode,
			String pattern, String gif, String riusoIdMessaggio, Vector<DataElement> dati, String idcorr) {

		DataElement de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_ELEMENTO_XML);
		de.setNote(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_ELEMENTO_XML_NOTE);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ELEMENTO_XML);
		de.setSize(80);
		if (elemxml == null) {
			de.setValue("");
		} else {
			de.setValue(elemxml);
		}
		dati.addElement(de);

		String[] tipoMode = { 
				PorteDelegateCostanti.VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_URL_BASED, 
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
				mode.equals(PorteDelegateCostanti.VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_CONTENT_BASED)) {
			de = new DataElement();
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PATTERN);
			if (pattern == null) {
				de.setValue("");
			} else {
				de.setValue(pattern);
			}
			de.setType(DataElementType.TEXT_EDIT);
			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_PATTERN);
			de.setSize(80);
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
			de.setType(DataElementType.SELECT);
			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_RIUSO_ID_MESSAGGIO);
			de.setValues(tipiRiusoIdMessaggio);
			de.setSelected(riusoIdMessaggio);
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
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_ELEMENTO_XML);
		de.setNote(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_ELEMENTO_XML_NOTE);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ELEMENTO_XML);
		de.setSize(80);
		if (elemxml == null) {
			de.setValue("");
		} else {
			de.setValue(elemxml);
		}
		dati.addElement(de);

		String[] tipoMode = { 
				//				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_URL_BASED, 
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
				mode.equals(PorteDelegateCostanti.VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_CONTENT_BASED)) {
			de = new DataElement();
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PATTERN);
			if (pattern == null) {
				de.setValue("");
			} else {
				de.setValue(pattern);
			}
			de.setType(DataElementType.TEXT_EDIT);
			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_PATTERN);
			de.setSize(80);
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
			String tiposp = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TIPO_SP);
			String modesp = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_SP);
			String sp = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SP);
			String servid = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SERVIZIO_ID);
			String tiposervizio = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TIPO_SERVIZIO);
			String modeservizio = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_SERVIZIO);
			String servizio = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SERVIZIO);
			String azid = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AZIONE_ID);
			String modeaz = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_AZIONE);
			String azione = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AZIONE);
			String xsd = this.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_XSD);

			// Campi obbligatori
			if (nomePD==null || nomePD.equals("")) {
				this.pd.setMessage("Dati incompleti. E' necessario indicare il Nome");
				return false;
			}

			if (!modesp.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT) && 
					!modesp.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_INPUT_BASED) &&
					(tiposp.equals("") || sp.equals(""))) {
				String tmpElenco = "";
				if (tiposp.equals("")) {
					tmpElenco = "Tipo soggetto erogatore";
				}
				if (sp.equals("")) {
					if(modesp.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_URL_BASED)
							|| modesp.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_CONTENT_BASED)){
						if (tmpElenco.equals("")) {
							tmpElenco = "Pattern soggetto erogatore";
						} else {
							tmpElenco = tmpElenco + ", Pattern soggetto erogatore";
						}
					}else{
						if (tmpElenco.equals("")) {
							tmpElenco = "Nome soggetto erogatore";
						} else {
							tmpElenco = tmpElenco + ", Nome soggetto erogatore";
						}
					}
				}
				this.pd.setMessage("Dati incompleti. E' necessario indicare: " + tmpElenco);
				return false;
			}
			if (modesp.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT) && (soggid == null)) {
				this.pd.setMessage("Dati incompleti. Non &egrave; stato trovato nessun soggetto erogatore. Scegliere una delle altre modalit&agrave");
				return false;
			}
			if (modesp.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_INPUT_BASED) && (tiposp.equals(""))) {
				this.pd.setMessage("Dati incompleti. E' necessario indicare il Tipo soggetto erogatore");
				return false;
			}

			if (!modeservizio.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT) && 
					!modeservizio.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_INPUT_BASED) &&
					(tiposervizio.equals("") || servizio.equals(""))) {
				String tmpElenco = "";
				if (tiposervizio.equals("")) {
					tmpElenco = "Tipo servizio";
				}
				if (servizio.equals("")) {
					if(modeservizio.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_URL_BASED) ||
							modeservizio.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_CONTENT_BASED)){
						if (tmpElenco.equals("")) {
							tmpElenco = "Pattern servizio";
						} else {
							tmpElenco = tmpElenco + ", Pattern servizio";
						}
					}else{
						if (tmpElenco.equals("")) {
							tmpElenco = "Nome servizio";
						} else {
							tmpElenco = tmpElenco + ", Nome servizio";
						}
					}
				}
				this.pd.setMessage("Dati incompleti. E' necessario indicare: " + tmpElenco);
				return false;
			}
			if (modeservizio.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT) && (servid == null)) {
				this.pd.setMessage("Dati incompleti. Non &egrave; stato trovato nessun servizio associato al soggetto erogatore. Scegliere una delle altre modalit&agrave");
				return false;
			}
			if (modeservizio.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_INPUT_BASED) && (tiposervizio.equals(""))) {
				this.pd.setMessage("Dati incompleti. E' necessario indicare il Tipo servizio");
				return false;
			}

			if ((modeaz != null) && modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT) && (azid == null)) {
				this.pd.setMessage("Dati incompleti. Non &egrave; stata trovata nessuna azione associata al servizio. Scegliere una delle altre modalit&agrave");
				return false;
			}
			if ( modeaz!=null && (modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_URL_BASED) || 
					modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_CONTENT_BASED)) && (azione==null || azione.equals(""))) {
				String tmpElenco = "";
				if (tmpElenco.equals("")) {
					tmpElenco = "Pattern azione";
				} else {
					tmpElenco = tmpElenco + ", Pattern azione";
				}
				this.pd.setMessage("Dati incompleti. E' necessario indicare: " + tmpElenco);
				return false;
			}

			// Controllo che non ci siano spazi nei campi di testo
			if ((nomePD.indexOf(" ") != -1) ) {
				this.pd.setMessage("Non inserire spazi nei campi di testo");
				return false;
			}
			if(this.checkIntegrationEntityName(nomePD,PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME)==false){
				return false;
			}
			if (!modesp.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
				if (modesp.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_INPUT_BASED)) {
					if (tiposp.indexOf(" ") != -1) {
						this.pd.setMessage("Non inserire spazi nei campi di testo");
						return false;
					}
				} else {
					if ((tiposp.indexOf(" ") != -1) || (sp.indexOf(" ") != -1)) {
						this.pd.setMessage("Non inserire spazi nei campi di testo");
						return false;
					}
				}
			}
			if (!modeservizio.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
				if (modeservizio.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_INPUT_BASED)) {
					if (tiposervizio.indexOf(" ") != -1) {
						this.pd.setMessage("Non inserire spazi nei campi di testo");
						return false;
					}
				} else {
					if ((tiposervizio.indexOf(" ") != -1) || (servizio.indexOf(" ") != -1)) {
						this.pd.setMessage("Non inserire spazi nei campi di testo");
						return false;
					}
				}
			}
			if ((modeaz != null) && !modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT) && 
					!modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_INPUT_BASED) &&
					!modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_SOAP_ACTION_BASED) &&
					(azione.indexOf(" ") != -1)) {
				this.pd.setMessage("Non inserire spazi nei campi di testo");
				return false;
			}

			// Controllo che i campi "select" abbiano uno dei valori ammessi
			if (!modesp.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_USER_INPUT) &&
					!modesp.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT) 
					&& !modesp.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_URL_BASED) && 
					!modesp.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_CONTENT_BASED) &&
					!modesp.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_INPUT_BASED)) {
				this.pd.setMessage("Mode SP dev'essere user-input, register-input, url-based, content-based o input-based");
				return false;
			}
			if (!modeservizio.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_USER_INPUT) && 
					!modeservizio.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT) && 
					!modeservizio.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_URL_BASED) &&
					!modeservizio.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_CONTENT_BASED) &&
					!modeservizio.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_INPUT_BASED)) {
				this.pd.setMessage("Mode Servizio dev'essere user-input, register-input, url-based, content-based o input-based");
				return false;
			}
			if ((modeaz != null) && !modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_USER_INPUT) && 
					!modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT) && 
					!modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_URL_BASED) && 
					!modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_CONTENT_BASED) && 
					!modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_INPUT_BASED) && 
					!modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_SOAP_ACTION_BASED) && 
					!modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_WSDL_BASED)) {
				this.pd.setMessage("Mode Azione dev'essere user-input, register-input, url-based, content-based, input-based, soap-action-based o wsdl-based");
				return false;
			}
			if (!xsd.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_XSD_ABILITATO) &&
					!xsd.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_XSD_DISABILITATO) &&
					!xsd.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_XSD_WARNING_ONLY)) {
				this.pd.setMessage("Validazione XSD dev'essere abilitato, disabilitato o warningOnly");
				return false;
			}

			// Se autenticazione = custom, nomeauth dev'essere specificato
			if (CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM.equals(autenticazione) && 
					(autenticazioneCustom == null || autenticazioneCustom.equals(""))) {
				this.pd.setMessage("Indicare un nome per l'autenticazione '"+CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM+"'");
				return false;
			}

			// Se autorizzazione = custom, nomeautor dev'essere specificato
			if (CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM.equals(autorizzazione) && 
					(autorizzazioneCustom == null || autorizzazioneCustom.equals(""))) {
				this.pd.setMessage("Indicare un nome per l'autorizzazione '"+CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM+"'");
				return false;
			}
			
			PortaDelegata pdDatabase = null;
			if (TipoOperazione.CHANGE == tipoOp){
				pdDatabase = this.porteDelegateCore.getPortaDelegata(Long.parseLong(id)); 
			}
			
			List<String> ruoli = new ArrayList<>();
			if(pdDatabase!=null && pdDatabase.getRuoli()!=null && pdDatabase.getRuoli().sizeRuoloList()>0){
				for (int i = 0; i < pdDatabase.getRuoli().sizeRuoloList(); i++) {
					ruoli.add(pdDatabase.getRuoli().getRuolo(i).getNome());
				}
			}
			
			if(this.controlloAccessiCheck(tipoOp, autenticazione, autenticazioneOpzionale, 
					autorizzazione, autorizzazioneAutenticati, autorizzazioneRuoli, 
					autorizzazioneRuoliTipologia, ruoloMatch, 
					true, true, ruoli)==false){
				return false;
			}

			// Se modesp = register-input, controllo che il soggetto sia uno di
			// quelli disponibili
			// Se modeservizio = register-input, controllo che il servizio sia
			// uno
			// di quelli disponibili
			// Se modeaz = register-input, controllo che l'azione sia una di
			// quelle
			// disponibili
			if (modesp.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
				/*IDSoggetto idSoggetto = new IDSoggetto(soggid.split("/")[0], soggid.split("/")[1]);
					boolean soggEsiste = this.core.existsSoggetto(idSoggetto);
					if (!soggEsiste) {
						this.pd.setMessage("Il soggetto erogatore specificato non esiste");
						return false;
					}*/ // Questo controllo serve??? Il valore è stato preso da una select list!!!
			}

			if (modeservizio.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
				/*IDSoggetto idSoggetto = new IDSoggetto(soggid.split("/")[0], soggid.split("/")[1]);
					IDServizio idServizio = new IDServizio(idSoggetto, servid.split("/")[0], servid.split("/")[1]);
					boolean servEsiste = this.core.existsAccordoServizioParteSpecifica(idServizio);
					if (!servEsiste) {
						this.pd.setMessage("Il servizio specificato non esiste");
						return false;
					}*/ // Questo controllo serve??? Il valore è stato preso da una select list!!!
			}

			if ((modeaz != null) && modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {

				/*int servInt = Integer.parseInt(servid);
					AccordoServizioParteSpecifica myServ = this.core.getAccordoServizioParteSpecifica(servInt);
					boolean trovataAz = false;
					if(myServ.getPortType()!=null){
						trovataAz = this.core.existsAccordoServizioOperation(Integer.parseInt(azid)); 
						if (!trovataAz) {
							this.pd.setMessage("L'Azione dev'essere scelta tra quelle associate al servizio "+myServ.getPortType()+" dell'accordo di servizio "+myServ.getAccordoServizio());
							return false;
						}
					}
					else{
						trovataAz = this.core.existsAccordoServizioAzione(Integer.parseInt(azid));
						if (!trovataAz) {
							this.pd.setMessage("L'Azione dev'essere scelta tra quelle associate all'accordo di servizio "+myServ.getAccordoServizio());
							return false;
						}
					}*/ // Questo controllo serve??? Il valore è stato preso da una select list!!!
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
			if (tipoOp == TipoOperazione.ADD) {
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
					this.pd.setMessage("Esiste gia' una Porta Delegata con nome [" + nomePD + "] associata al Soggetto [" + pd.getTipoSoggettoProprietario()+"/"+pd.getNomeSoggettoProprietario() + "]");
					return false;
				}
			} else if (TipoOperazione.CHANGE == tipoOp) {
				PortaDelegata portaDelegata = null;
				try {
					// controllo su nome (non possono esistere 2 pd con stesso
					// nome dello stesso fruitore)
					if (!nomePD.equals(oldNomePD)) {
						long curID = this.porteDelegateCore.getIdPortaDelegata(nomePD);
						if (curID > 0) {
							PortaDelegata pd = this.porteDelegateCore.getPortaDelegata(curID);
							this.pd.setMessage("Esiste gia' una Porta Delegata con nome [" + nomePD + "] associata al Soggetto [" + pd.getTipoSoggettoProprietario()+"/"+pd.getNomeSoggettoProprietario() + "]");
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
						this.pd.setMessage("Esiste gia' una Porta Delegata con nome [" + portaDelegata.getNome() + "] associata al Soggetto [" + pd.getTipoSoggettoProprietario()+"/"+pd.getNomeSoggettoProprietario() + "]");
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
								this.pd.setMessage("Non è possibile modificare il tipo di autenticazione da ["+portaDelegata.getAutenticazione()+"] a ["+autenticazione+
										"], poichè risultano associati alla porta delegata dei servizi applicativi non compatibili, nella modalita' di accesso, con il nuovo tipo di autenticazione");
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
					soggettoTitle = soggetto.getTipo() + "/" + soggetto.getNome();
				}else{
					org.openspcoop2.core.config.Soggetto soggetto = this.soggettiCore.getSoggetto(Integer.parseInt(id));
					soggettoTitle = soggetto.getTipo() + "/" + soggetto.getNome();
				}
				
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SOGGETTI, null));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST));

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
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PORTE_DELEGATE, null));
				if(search.equals("")){
					this.pd.setSearchDescription("");
					lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO,null));
				}
				else{
					lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST));
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
			//labelsList.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_DESCRIZIONE); 
			labelsList.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SERVIZI_APPLICATIVI); 
			labelsList.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_RUOLI); 
			labelsList.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY); 
			//if(InterfaceType.AVANZATA.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())){
			labelsList.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM);
			//}
			labelsList.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA);
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

					String protocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(pd.getTipoSoggettoProprietario());
					
					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					de.setType(DataElementType.HIDDEN);
					de.setValue("" + pd.getId());
					e.addElement(de);

					de = new DataElement();
					de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CHANGE,
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, "" + pd.getIdSoggetto()),
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_PORTA,pd.getNome()),
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, "" + pd.getId())
							);
					de.setValue(pd.getNome());
					de.setIdToRemove(pd.getId().toString());
					de.setToolTip(pd.getDescrizione());
					e.addElement(de);

					if(useIdSogg==false){
						de = new DataElement();
						de.setValue(pd.getTipoSoggettoProprietario()+"/"+pd.getNomeSoggettoProprietario());
						e.addElement(de);
											
						if( showProtocolli ) {
							de = new DataElement();
							de.setValue(this.getLabelProtocollo(protocollo));
							e.addElement(de);
						}
					}
					
//					de = new DataElement();
//					de.setValue(pd.getDescrizione());
//					e.addElement(de);
					
					de = new DataElement();
					if(TipoAutorizzazione.isAuthenticationRequired(pd.getAutorizzazione()) 
							|| 
							!TipoAutorizzazione.getAllValues().contains(pd.getAutorizzazione()) // custom 
							){
						de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_SERVIZIO_APPLICATIVO_LIST,
								new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, "" + pd.getIdSoggetto()),
								new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, ""+pd.getId())
								);
						if (contaListe) {
							int numSA = pd.sizeServizioApplicativoList();
							ServletUtils.setDataElementVisualizzaLabel(de,new Long(numSA));
						} else
							ServletUtils.setDataElementVisualizzaLabel(de);
					}
					else{
						de.setValue("-");
					}
					e.addElement(de);
					
					de = new DataElement();
					if(TipoAutorizzazione.isRolesRequired(pd.getAutorizzazione()) 
							|| 
							!TipoAutorizzazione.getAllValues().contains(pd.getAutorizzazione()) // custom 
							){
						de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_RUOLI_LIST,
								new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, "" + pd.getIdSoggetto()),
								new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, ""+pd.getId())
								);
						if (contaListe) {
							int numSA = 0;
							if(pd.getRuoli()!=null){
								numSA= pd.getRuoli().sizeRuoloList();
							}
							ServletUtils.setDataElementVisualizzaLabel(de,new Long(numSA));
						} else
							ServletUtils.setDataElementVisualizzaLabel(de);
					}
					else{
						de.setValue("-");
					}
					e.addElement(de);

					de = new DataElement();
					de.setUrl( 
							PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MESSAGE_SECURITY,
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, "" + pd.getIdSoggetto()),
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, ""+pd.getId())
							);
					de.setValue(pd.getStatoMessageSecurity());
					e.addElement(de);

					//if(InterfaceType.AVANZATA.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())){
					de = new DataElement();
					de.setUrl( 
							PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MTOM,
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, "" + pd.getIdSoggetto()),
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, ""+pd.getId())
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
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, ""+pd.getId()) );
					
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

					if(extendedServletList!=null && extendedServletList.showExtendedInfo(this.request, this.session)){
						de = new DataElement();
						de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_EXTENDED_LIST,
								new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, "" + pd.getIdSoggetto()),
								new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, ""+pd.getId()),
								new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME,pd.getNome())
								);
						if (contaListe) {
							int numExtended = extendedServletList.sizeList(pd);
							ServletUtils.setDataElementVisualizzaLabel(de,new Long(numExtended));
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
			this.pd.setAddButton(false);
			
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
			
			this.pd.setSearchLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME);
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SERVIZIO_APPLICATIVO_DI + idporta,null));
			}
			else{
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SERVIZIO_APPLICATIVO_DI + idporta,
						PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_SERVIZIO_APPLICATIVO_LIST, pId, pIdSoggetto, pIdAsps, pIdFrizione	));
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_RISULTATI_RICERCA, null));

			}

			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SERVIZI_APPLICATIVI, search);
			}

			// setto le label delle colonne
			String[] labels = {PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SERVIZIO_APPLICATIVO };
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<ServizioApplicativo> it = lista.iterator();
				while (it.hasNext()) {
					ServizioApplicativo sa = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					de.setUrl(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE,
							new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID, sa.getId() + ""),
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_PROVIDER, idsogg));
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
			
			Parameter pNomePorta = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME, idporta);

			this.pd.setSearchLabel(CostantiControlStation.LABEL_PARAMETRO_RUOLO);
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_RUOLI_DI + idporta,null));
			}
			else{
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_RUOLI_DI + idporta,
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
			
			lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_DI + idporta,
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
			lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONI_APPLICATIVE_DI + idporta,
					PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA, pIdSoggetto, pId, pNomePorta));

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
					String nomeElemento = "(*)";
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

			lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_DI + idporta,
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

			lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONI_APPLICATIVE_DI + idporta,
						PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA, pId, pIdSoggetto, pNomePorta, pIdAsps, pIdFrizione	));

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
					String nomeElemento = "(*)";
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

			lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM_DI + idporta,
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
			
			lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM_DI + idporta,
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
		
		switch (parentPD) {
		case PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE:
			// Prendo il nome e il tipo del servizio
			AccordoServizioParteSpecifica asps = this.apsCore.getAccordoServizioParteSpecifica(Integer.parseInt(idAsps));
			String servizioTmpTile = asps.getTipoSoggettoErogatore() + "/" + asps.getNomeSoggettoErogatore() + "-" + asps.getTipo() + "/" + asps.getNome();
			Parameter pIdServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, asps.getId()+ "");
			Parameter pIdSoggettoErogatore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, asps.getIdSoggetto()+"");
			Parameter pIdFruizione = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MY_ID, idFruizione+ "");
			Parameter pIdSoggettoFruitore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO, idSoggettoFruitore);
			Parameter pIdProviderSoggettoFruitore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROVIDER_FRUITORE, idSoggettoFruitore);
			
			String fruizioneTmpTile = MessageFormat.format(PorteDelegateCostanti.LABEL_FRUIZIONE_TIPO_NOME_SOGGETTO, tipoSoggettoFruitore,nomeSoggettoFruitore);
			
			lstParam.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, null));
			lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
			lstParam.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_FUITORI_DI  + servizioTmpTile, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_LIST , pIdServizio,pIdSoggettoErogatore));
			lstParam.add(new Parameter(fruizioneTmpTile, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE, pIdServizio,pIdFruizione,pIdProviderSoggettoFruitore));
			lstParam.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_PORTE_DELEGATE, 
					AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_PORTE_DELEGATE_LIST ,pIdFruizione,pIdServizio,pIdSoggettoFruitore));
			break;
		case PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_SOGGETTO:
			String soggettoTitle =   MessageFormat.format(PorteDelegateCostanti.LABEL_TIPO_NOME_SOGGETTO, tipoSoggettoFruitore,nomeSoggettoFruitore);
			lstParam.add(new Parameter(SoggettiCostanti.LABEL_SOGGETTI, null));
			lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST));
			lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PORTE_DELEGATE_DI + soggettoTitle, PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST ,
					new Parameter( PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idSoggettoFruitore)));
			break;
		case PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE:
		default:
			lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PORTE_DELEGATE, null));
			lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST));
			break;
		}
		return lstParam;
	}

}
