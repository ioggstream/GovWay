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


package org.openspcoop2.web.ctrlstat.servlet.soggetti;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.CredenzialiSoggetto;
import org.openspcoop2.core.registry.Property;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.CredenzialeTipo;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleInterfaceType;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.IConsoleDynamicConfiguration;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.dao.PdDControlStation;
import org.openspcoop2.web.ctrlstat.dao.SoggettoCtrlStat;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
import org.openspcoop2.web.ctrlstat.servlet.protocol_properties.ProtocolPropertiesUtilities;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * soggettiAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class SoggettiAdd extends Action {


	private String editMode = null;
	private String nomeprov , tipoprov, portadom, descr, versioneProtocollo,pdd, codiceIpa, pd_url_prefix_rewriter,pa_url_prefix_rewriter,protocollo,dominio;
	private boolean isRouter,privato; 
	private Boolean singlePdD = null;
	private String tipologia = null;
		
	// Protocol Properties
	private IConsoleDynamicConfiguration consoleDynamicConfiguration = null;
	private ConsoleConfiguration consoleConfiguration =null;
	private ProtocolProperties protocolProperties = null;
	private IProtocolFactory<?> protocolFactory= null;
	private IRegistryReader registryReader = null; 
	private IConfigIntegrationReader configRegistryReader = null; 
	private ConsoleOperationType consoleOperationType = null;
	private ConsoleInterfaceType consoleInterfaceType = null;

	private String tipoauthSoggetto = null;
	private String utenteSoggetto = null;
	private String passwordSoggetto = null;
	private String subjectSoggetto = null;
	private String principalSoggetto = null;
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		// Parametri Protocol Properties relativi al tipo di operazione e al tipo di visualizzazione
		this.consoleOperationType = ConsoleOperationType.ADD;
		
		// Parametri relativi al tipo operazione
		TipoOperazione tipoOp = TipoOperazione.ADD; 

		try {
			SoggettiHelper soggettiHelper = new SoggettiHelper(request, pd, session);
			this.consoleInterfaceType = ProtocolPropertiesUtilities.getTipoInterfaccia(soggettiHelper); 
			
			this.protocollo = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_PROTOCOLLO);
			this.nomeprov = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME);
			this.tipoprov = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO);
			this.tipologia = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPOLOGIA);
			this.portadom = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_CODICE_PORTA);
			this.descr = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_DESCRIZIONE);
			this.versioneProtocollo = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_VERSIONE_PROTOCOLLO);
			this.pdd = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_PDD);
			String is_router = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_IS_ROUTER);
			String is_privato = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_IS_PRIVATO);
			this.privato = ServletUtils.isCheckBoxEnabled(is_privato);
			this.codiceIpa = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_CODICE_IPA);
			this.pd_url_prefix_rewriter = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_PD_URL_PREFIX_REWRITER);
			this.pa_url_prefix_rewriter = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_PA_URL_PREFIX_REWRITER);
			this.isRouter = ServletUtils.isCheckBoxEnabled(is_router);
			this.dominio = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_DOMINIO);

			String userLogin = ServletUtils.getUserLoginFromSession(session);
			this.singlePdD = (Boolean) session.getAttribute(CostantiControlStation.SESSION_PARAMETRO_SINGLE_PDD);

			this.editMode = soggettiHelper.getParameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);
			
			this.tipoauthSoggetto = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_TIPO_AUTENTICAZIONE);
			this.utenteSoggetto = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_USERNAME);
			this.passwordSoggetto = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			this.subjectSoggetto = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_SUBJECT);
			this.principalSoggetto = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PRINCIPAL);
			
			boolean isRouter = ServletUtils.isCheckBoxEnabled(is_router);

			// Preparo il menu
			soggettiHelper.makeMenu();

			// Prendo la lista di pdd e la metto in un array
			String[] pddList = null;
			String[] pddEsterneList = null;
			List<String> tipiSoggetti = null;
			int totPdd = 1;
			String nomePddGestioneLocale = null;
			List<String> versioniProtocollo = null;

			SoggettiCore soggettiCore = new SoggettiCore();
			PddCore pddCore = new PddCore(soggettiCore);

			// Tipi protocollo supportati
			List<String> listaTipiProtocollo = soggettiCore.getProtocolli(session);
			// primo accesso
			if(this.protocollo == null){
				this.protocollo = soggettiCore.getProtocolloDefault(session, null);
			}
			
			if(soggettiCore.isRegistroServiziLocale()){
				List<PdDControlStation> lista = new ArrayList<PdDControlStation>();

				// aggiungo un elemento di comodo
				PdDControlStation tmp = new PdDControlStation();
				tmp.setNome("-");
				lista.add(tmp);

				// aggiungo gli altri elementi
				if(soggettiCore.isVisioneOggettiGlobale(userLogin)){
					lista.addAll(pddCore.pddList(null, new Search(true)));
				}else{
					lista.addAll(pddCore.pddList(userLogin, new Search(true)));
				}

				totPdd = lista.size();

				pddList = new String[lista.size()];

				int i = 0;
				List<String> pddEsterne = new ArrayList<>();
				pddEsterne.add("-");
				for (PdDControlStation pddTmp : lista) {
					pddList[i] = pddTmp.getNome();
					i++;
					if(this.singlePdD && (nomePddGestioneLocale==null) && (PddTipologia.OPERATIVO.toString().equals(pddTmp.getTipo())) ){
						nomePddGestioneLocale = pddTmp.getNome();
					}
					if(this.singlePdD && PddTipologia.ESTERNO.toString().equals(pddTmp.getTipo())){
						pddEsterne.add(pddTmp.getNome());
					}
				}
				pddEsterneList = pddEsterne.toArray(new String[1]);
				
				// Gestione pdd
				if(soggettiCore.isGestionePddAbilitata(soggettiHelper)==false) {
					
					if(nomePddGestioneLocale==null) {
						throw new Exception("Non è stata rilevata una pdd di tipologia 'operativo'");
					}
					
					if(SoggettiCostanti.SOGGETTO_DOMINIO_OPERATIVO_VALUE.equals(this.dominio)) {
						this.pdd = nomePddGestioneLocale;
					}
					else {
						this.pdd = null;
					}
				}
			}
			
			//Carico la lista dei tipi di soggetti gestiti dal protocollo
			tipiSoggetti = soggettiCore.getTipiSoggettiGestitiProtocollo(this.protocollo);

			// lista tipi
			//			tipiSoggetti = soggettiCore.getTipiSoggettiGestiti(); // all tipi soggetti gestiti
			if(this.tipoprov==null){
				this.tipoprov = soggettiCore.getTipoSoggettoDefaultProtocollo(this.protocollo);
			}

			String postBackElementName = soggettiHelper.getPostBackElementName();

			// Controllo se ho modificato il protocollo, ricalcolo il default della versione del protocollo
			if(postBackElementName != null ){
				if(postBackElementName.equalsIgnoreCase(SoggettiCostanti.PARAMETRO_SOGGETTO_PROTOCOLLO)){
					this.versioneProtocollo = null;
					// cancello file temporanei
					soggettiHelper.deleteProtocolPropertiesBinaryParameters();
				}  
			}


			//			String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(tipoprov);
			if(this.versioneProtocollo == null){
				this.versioneProtocollo = soggettiCore.getVersioneDefaultProtocollo(this.protocollo);
			}

			if(soggettiHelper.isModalitaAvanzata()){
				versioniProtocollo = soggettiCore.getVersioniProtocollo(this.protocollo);
			}else {
				versioniProtocollo = new ArrayList<String>();
				//				versioneProtocollo = soggettiCore.getVersioneDefaultProtocollo(protocollo);
				versioniProtocollo.add(this.versioneProtocollo);
			}
			boolean isSupportatoCodiceIPA = soggettiCore.isSupportatoCodiceIPA(this.protocollo); 
			boolean isSupportatoIdentificativoPorta = soggettiCore.isSupportatoIdentificativoPorta(this.protocollo);

			boolean isSupportatoAutenticazioneSoggetti = soggettiCore.isSupportatoAutenticazioneSoggetti(this.protocollo);
			boolean isPddEsterna = pddCore.isPddEsterna(this.pdd);
			if(isSupportatoAutenticazioneSoggetti){
				if(isPddEsterna){
					
					if(this.tipologia==null || "".equals(this.tipologia)){
						this.tipologia = SoggettiCostanti.SOGGETTO_RUOLO_EROGATORE;
					}
					
					if(ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA.equals(this.tipoauthSoggetto)){
						this.tipoauthSoggetto = null;
					}
				}
				if (this.tipoauthSoggetto == null) {
					if(isPddEsterna){
						
						if(SoggettiCostanti.SOGGETTO_RUOLO_FRUITORE.equals(this.tipologia) || SoggettiCostanti.SOGGETTO_RUOLO_ENTRAMBI.equals(this.tipologia)){
							this.tipoauthSoggetto = soggettiCore.getAutenticazione_generazioneAutomaticaPorteApplicative();
						}else{
							this.tipoauthSoggetto = ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA;
						}						
					}
					else{
						this.tipoauthSoggetto = ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA;
					}
				}
			}
			

			IDSoggetto idSoggetto = new IDSoggetto(this.tipoprov,this.nomeprov);
			this.protocolFactory  = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(this.protocollo); 
			this.consoleDynamicConfiguration =  this.protocolFactory.createDynamicConfigurationConsole();
			this.registryReader = soggettiCore.getRegistryReader(this.protocolFactory); 
			this.configRegistryReader = soggettiCore.getConfigIntegrationReader(this.protocolFactory);
			this.consoleConfiguration = this.consoleDynamicConfiguration.getDynamicConfigSoggetto(this.consoleOperationType, this.consoleInterfaceType, 
					this.registryReader, this.configRegistryReader, idSoggetto);
			this.protocolProperties = soggettiHelper.estraiProtocolPropertiesDaRequest(this.consoleConfiguration, this.consoleOperationType);

			// Se nomehid = null, devo visualizzare la pagina per l'inserimento dati
			if(ServletUtils.isEditModeInProgress(this.editMode)){

				// setto la barra del titolo
				ServletUtils.setPageDataTitle_ServletAdd(pd, SoggettiCostanti.LABEL_SOGGETTI, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				if(this.nomeprov==null){
					this.nomeprov = "";
					idSoggetto = new IDSoggetto(this.tipoprov,this.nomeprov);
				}
				if(this.portadom==null){
					this.portadom = "";
				}
				if(this.descr==null){
					this.descr = "";
				}

				// update della configurazione 
				this.consoleDynamicConfiguration.updateDynamicConfigSoggetto(this.consoleConfiguration, this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties, 
						this.registryReader, this.configRegistryReader, idSoggetto); 
				dati = soggettiHelper.addSoggettiToDati(TipoOperazione.ADD,dati, this.nomeprov, this.tipoprov, this.portadom, this.descr, 
						isRouter, tipiSoggetti, this.versioneProtocollo, this.privato,this.codiceIpa,versioniProtocollo,
						isSupportatoCodiceIPA, isSupportatoIdentificativoPorta,
						pddList,pddEsterneList,nomePddGestioneLocale, this.pdd, 
						listaTipiProtocollo, this.protocollo ,
						isSupportatoAutenticazioneSoggetti,this.utenteSoggetto,this.passwordSoggetto,this.subjectSoggetto,this.principalSoggetto,this.tipoauthSoggetto,
						isPddEsterna,this.tipologia,this.dominio);

				// aggiunta campi custom
				dati = soggettiHelper.addProtocolPropertiesToDati(dati, this.consoleConfiguration,this.consoleOperationType, this.consoleInterfaceType , this.protocolProperties);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, SoggettiCostanti.OBJECT_NAME_SOGGETTI, ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			boolean isOk = soggettiHelper.soggettiCheckData(tipoOp, null, this.tipoprov, this.nomeprov, this.codiceIpa, this.pd_url_prefix_rewriter, this.pa_url_prefix_rewriter,
					null, false, this.descr);

			if (isOk) {
				if(soggettiCore.isRegistroServiziLocale()){
					if (!this.singlePdD) {
						isOk = false;
						// Controllo che pdd appartenga alla lista di pdd
						// esistenti
						for (int i = 0; i < totPdd; i++) {
							String tmpPdd = pddList[i];
							if (tmpPdd.equals(this.pdd) && !this.pdd.equals("-")) {
								isOk = true;
							}
						}
						if (!isOk) {
							pd.setMessage("La Porta di Dominio dev'essere scelta tra quelle definite nel pannello Porte di Dominio");
						}
					}
				}
			}
			
			// Validazione base dei parametri custom 
			if(isOk){
				try{
					soggettiHelper.validaProtocolProperties(this.consoleConfiguration, this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties);
				}catch(ProtocolException e){
					ControlStationCore.getLog().error(e.getMessage(),e);
					pd.setMessage(e.getMessage());
					isOk = false;
				}
			}

			// Valido i parametri custom se ho gia' passato tutta la validazione prevista
			if(isOk){
				try{
					//validazione campi dinamici
					this.consoleDynamicConfiguration.validateDynamicConfigSoggetto(this.consoleConfiguration, this.consoleOperationType, this.protocolProperties, 
							this.registryReader, this.configRegistryReader, idSoggetto); 
				}catch(ProtocolException e){
					ControlStationCore.getLog().error(e.getMessage(),e);
					pd.setMessage(e.getMessage());
					isOk = false;
				}
			}

			if (!isOk) {

				// setto la barra del titolo
				ServletUtils.setPageDataTitle_ServletAdd(pd, SoggettiCostanti.LABEL_SOGGETTI, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				// update della configurazione 
				this.consoleDynamicConfiguration.updateDynamicConfigSoggetto(this.consoleConfiguration, this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties, 
						this.registryReader, this.configRegistryReader, idSoggetto); 

				dati = soggettiHelper.addSoggettiToDati(TipoOperazione.ADD,dati, this.nomeprov, this.tipoprov, this.portadom, this.descr, 
						isRouter, tipiSoggetti, this.versioneProtocollo, this.privato,this.codiceIpa,versioniProtocollo,
						isSupportatoCodiceIPA, isSupportatoIdentificativoPorta,
						pddList,pddEsterneList,nomePddGestioneLocale, this.pdd,  
						listaTipiProtocollo, this.protocollo,
						isSupportatoAutenticazioneSoggetti,this.utenteSoggetto,this.passwordSoggetto,this.subjectSoggetto,this.principalSoggetto,this.tipoauthSoggetto,
						isPddEsterna,this.tipologia,this.dominio);

				// aggiunta campi custom
				dati = soggettiHelper.addProtocolPropertiesToDati(dati, this.consoleConfiguration,this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, SoggettiCostanti.OBJECT_NAME_SOGGETTI, ForwardParams.ADD());
			}

			// Inserisco il soggetto nel db
			if (this.codiceIpa==null || this.codiceIpa.equals("")) {
				this.codiceIpa = soggettiCore.getCodiceIPADefault(this.protocollo, idSoggetto, false);
			}

			if (this.portadom==null || this.portadom.equals("")) {
				this.portadom=soggettiCore.getIdentificativoPortaDefault(this.protocollo, idSoggetto);
			}

			// utilizzo il soggetto del registro che e' un
			// sovrainsieme di quello del config
			Soggetto soggettoRegistro = null;
			if(soggettiCore.isRegistroServiziLocale()){
				soggettoRegistro = new Soggetto();
			}
			org.openspcoop2.core.config.Soggetto soggettoConfig = new org.openspcoop2.core.config.Soggetto();

			// imposto soggettoRegistro
			if(soggettiCore.isRegistroServiziLocale()){
				soggettoRegistro.setNome(this.nomeprov);
				soggettoRegistro.setTipo(this.tipoprov);
				soggettoRegistro.setDescrizione(this.descr);
				soggettoRegistro.setVersioneProtocollo(this.versioneProtocollo);
				soggettoRegistro.setIdentificativoPorta(this.portadom);
				soggettoRegistro.setCodiceIpa(this.codiceIpa);
				
				if(pddCore.isGestionePddAbilitata(soggettiHelper)==false){
					if(SoggettiCostanti.SOGGETTO_DOMINIO_OPERATIVO_VALUE.equals(this.dominio)) {
						this.pdd = nomePddGestioneLocale;
					}
					else {
						this.pdd = null;
					}
				}
				
				if(soggettiCore.isSinglePdD()){
					if (this.pdd==null || this.pdd.equals("-"))
						soggettoRegistro.setPortaDominio(null);
					else
						soggettoRegistro.setPortaDominio(this.pdd);
				}else{
					soggettoRegistro.setPortaDominio(this.pdd);
				}
				soggettoRegistro.setSuperUser(userLogin);
				soggettoRegistro.setPrivato(this.privato);
				
				if(isSupportatoAutenticazioneSoggetti){
					if(this.tipoauthSoggetto!=null && !"".equals(this.tipoauthSoggetto) && !ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA.equals(this.tipoauthSoggetto)){
						CredenzialiSoggetto credenziali = new CredenzialiSoggetto();
						credenziali.setTipo(CredenzialeTipo.toEnumConstant(this.tipoauthSoggetto));
						credenziali.setUser(this.utenteSoggetto);
						if(this.principalSoggetto!=null && !"".equals(this.principalSoggetto)){
							credenziali.setUser(this.principalSoggetto); // al posto di user
						}
						credenziali.setPassword(this.passwordSoggetto);
						credenziali.setSubject(this.subjectSoggetto);
						soggettoRegistro.setCredenziali(credenziali);
					}
					else{
						soggettoRegistro.setCredenziali(null);
					}
				}
			}

			Connettore connettore = null;
			if(soggettiCore.isRegistroServiziLocale()){
				connettore = new Connettore();
				connettore.setTipo(CostantiDB.CONNETTORE_TIPO_DISABILITATO);
			}

			if ( !this.singlePdD && soggettiCore.isRegistroServiziLocale() && !this.pdd.equals("-")) {

				PdDControlStation aPdD = pddCore.getPdDControlStation(this.pdd);
				int porta = aPdD.getPorta() <= 0 ? 80 : aPdD.getPorta();

				// nel caso in cui e' stata selezionato un nal
				// e la PdD e' di tipo operativo oppure non-operativo
				// allora setto come default il tipo HTTP
				// altrimenti il connettore e' disabilitato
				String tipoPdD = aPdD.getTipo();
				if ((tipoPdD != null) && (!this.singlePdD) && (tipoPdD.equals(PddTipologia.OPERATIVO.toString()) || tipoPdD.equals(PddTipologia.NONOPERATIVO.toString()))) {
					String ipPdd = aPdD.getIp();

					String url = aPdD.getProtocollo() + "://" + ipPdd + ":" + porta + "/" + soggettiCore.getSuffissoConnettoreAutomatico();
					url = url.replace(CostantiControlStation.PLACEHOLDER_SOGGETTO_ENDPOINT_CREAZIONE_AUTOMATICA, 
							soggettiCore.getWebContextProtocolAssociatoTipoSoggetto(this.tipoprov));
					connettore.setTipo(CostantiDB.CONNETTORE_TIPO_HTTP);

					Property property = new Property();
					property.setNome(CostantiDB.CONNETTORE_HTTP_LOCATION);
					property.setValore(url);
					connettore.addProperty(property);
				}

			}

			if(soggettiCore.isRegistroServiziLocale()){
				soggettoRegistro.setConnettore(connettore);
			}

			// imposto soggettoConfig
			soggettoConfig.setNome(this.nomeprov);
			soggettoConfig.setTipo(this.tipoprov);
			soggettoConfig.setDescrizione(this.descr);
			soggettoConfig.setIdentificativoPorta(this.portadom);
			soggettoConfig.setRouter(this.isRouter);
			soggettoConfig.setSuperUser(userLogin);

			//imposto properties custom
			soggettoRegistro.setProtocolPropertyList(ProtocolPropertiesUtils.toProtocolProperties(this.protocolProperties, this.consoleOperationType,null)); 


			SoggettoCtrlStat sog = new SoggettoCtrlStat(soggettoRegistro, soggettoConfig);
			// eseguo le operazioni
			soggettiCore.performCreateOperation(userLogin, soggettiHelper.smista(), sog);

			// cancello file temporanei
			soggettiHelper.deleteBinaryProtocolPropertiesTmpFiles(this.protocolProperties); 

			// recupero la lista dei soggetti
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			// la lista dei soggetti del registro e' un sovrainsieme
			// di quella di config
			// cioe' ha piu informazioni, ma lo stesso numero di
			// soggetti.
			// quindi posso utilizzare solo quella
			if(soggettiCore.isRegistroServiziLocale()){
				List<Soggetto> listaSoggettiRegistro = null;
				if(soggettiCore.isVisioneOggettiGlobale(userLogin)){
					listaSoggettiRegistro = soggettiCore.soggettiRegistroList(null, ricerca);
				}else{
					listaSoggettiRegistro = soggettiCore.soggettiRegistroList(userLogin, ricerca);
				}

				soggettiHelper.prepareSoggettiList(listaSoggettiRegistro, ricerca);
			}
			else{
				List<org.openspcoop2.core.config.Soggetto> listaSoggettiConfig = null;
				if(soggettiCore.isVisioneOggettiGlobale(userLogin)){
					listaSoggettiConfig = soggettiCore.soggettiList(null, ricerca);
				}else{
					listaSoggettiConfig = soggettiCore.soggettiList(userLogin, ricerca);
				}

				soggettiHelper.prepareSoggettiConfigList(listaSoggettiConfig, ricerca);
			}

			if(!pddCore.isPddEsterna(this.pdd)) {
				generalHelper = new GeneralHelper(session);
				gd = generalHelper.initGeneralData(request); // re-inizializzo per ricalcolare il menu in alto a destra
			}

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, SoggettiCostanti.OBJECT_NAME_SOGGETTI, ForwardParams.ADD());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					SoggettiCostanti.OBJECT_NAME_SOGGETTI, ForwardParams.ADD());
		}
	}
}
