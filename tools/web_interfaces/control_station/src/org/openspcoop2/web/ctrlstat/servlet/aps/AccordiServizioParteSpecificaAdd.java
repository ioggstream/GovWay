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


package org.openspcoop2.web.ctrlstat.servlet.aps;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.RispostaAsincrona;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.InvocazioneServizioTipoAutenticazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.core.config.constants.TipoAutorizzazione;
import org.openspcoop2.core.config.constants.TipologiaErogazione;
import org.openspcoop2.core.config.constants.TipologiaFruizione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.ConfigurazioneServizio;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.CredenzialeTipo;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.constants.TipologiaServizio;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.registry.driver.ValidazioneStatoPackageException;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.config.Implementation;
import org.openspcoop2.protocol.sdk.config.Subscription;
import org.openspcoop2.protocol.sdk.constants.ConsoleInterfaceType;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.IConsoleDynamicConfiguration;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.web.ctrlstat.core.AutorizzazioneUtilities;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.ConnettoreServletType;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.plugins.servlet.ServletExtendedConnettoreUtils;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneUtilities;
import org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni.ErogazioniCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni.ErogazioniHelper;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriHelper;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
import org.openspcoop2.web.ctrlstat.servlet.protocol_properties.ProtocolPropertiesUtilities;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;
import org.openspcoop2.web.lib.users.dao.PermessiUtente;

/**
 * serviziAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteSpecificaAdd extends Action {


	private String connettoreDebug;
	private String   nomeservizio, tiposervizio, provider, accordo,
	servcorr, endpointtype, tipoconn, url, nome, tipo, user, password, initcont,
	urlpgk, provurl, connfact, sendas, 
	profilo, portType,descrizione,
	httpsurl, httpstipologia, httpspath,
	httpstipo, httpspwd, httpsalgoritmo,
	httpskeystore, httpspwdprivatekeytrust, httpspathkey,
	httpstipokey, httpspwdkey, httpspwdprivatekey,
	httpsalgoritmokey;
	private String httpshostverifyS, httpsstatoS;
	private boolean httpshostverify, httpsstato;
	private String nomeSoggettoErogatore = "";
	private String tipoSoggettoErogatore = "";
	String providerSoggettoFruitore = null;
	private String nomeSoggettoFruitore = "";
	private String tipoSoggettoFruitore = "";
	private boolean privato = false;
	private String statoPackage = "";
	private String versione;
	private boolean validazioneDocumenti = true;
	private boolean decodeRequestValidazioneDocumenti = false;
	private String editMode = null;
	private String nomeSA = null;
	private String oldPortType = null;
	private String autenticazioneHttp;
	private Properties parametersPOST;
	private ServiceBinding serviceBinding = null;
	private org.openspcoop2.protocol.manifest.constants.InterfaceType formatoSpecifica = null;

	private String proxy_enabled, proxy_hostname,proxy_port,proxy_username,proxy_password;
	
	private String tempiRisposta_enabled, tempiRisposta_connectionTimeout, tempiRisposta_readTimeout, tempiRisposta_tempoMedioRisposta;

	private String transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop, opzioniAvanzate;

	// file
	private String requestOutputFileName = null;
	private String requestOutputFileNameHeaders = null;
	private String requestOutputParentDirCreateIfNotExists = null;
	private String requestOutputOverwriteIfExists = null;
	private String responseInputMode = null;
	private String responseInputFileName = null;
	private String responseInputFileNameHeaders = null;
	private String responseInputDeleteAfterRead = null;
	private String responseInputWaitTime = null;
	
	// Protocol Properties
	private IConsoleDynamicConfiguration consoleDynamicConfiguration = null;
	private ConsoleConfiguration consoleConfiguration =null;
	private ProtocolProperties protocolProperties = null;
	private IProtocolFactory<?> protocolFactory= null;
	private IRegistryReader registryReader = null; 
	private IConfigIntegrationReader configRegistryReader = null; 
	private ConsoleOperationType consoleOperationType = null;
	private ConsoleInterfaceType consoleInterfaceType = null;
	
	private BinaryParameter wsdlimpler, wsdlimplfru;

	private String erogazioneRuolo;
	private String erogazioneAutenticazione;
	private String erogazioneAutenticazioneOpzionale;
	private String erogazioneAutorizzazione;
	private String erogazioneAutorizzazioneAutenticati, erogazioneAutorizzazioneRuoli, erogazioneAutorizzazioneRuoliTipologia, erogazioneAutorizzazioneRuoliMatch;
	private String erogazioneSoggettoAutenticato; 
	
	private String fruizioneServizioApplicativo;
	private String fruizioneRuolo;
	private String fruizioneAutenticazione;
	private String fruizioneAutenticazioneOpzionale;
	private String fruizioneAutorizzazione;
	private String fruizioneAutorizzazioneAutenticati, fruizioneAutorizzazioneRuoli, fruizioneAutorizzazioneRuoliTipologia, fruizioneAutorizzazioneRuoliMatch;
	
	private String tipoProtocollo;
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = ServletUtils.getUserLoginFromSession(session);	
		
		IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();

		// Parametri Protocol Properties relativi al tipo di operazione e al tipo di visualizzazione
		this.consoleOperationType = ConsoleOperationType.ADD;
		
		// Parametri relativi al tipo operazione
		TipoOperazione tipoOp = TipoOperazione.ADD;

		try {
			boolean multitenant = ServletUtils.getUserFromSession(session).isPermitMultiTenant(); 
			
			ErogazioniHelper apsHelper = new ErogazioniHelper(request, pd, session);
			this.consoleInterfaceType = ProtocolPropertiesUtilities.getTipoInterfaccia(apsHelper); 

			this.parametersPOST = null;

			this.editMode = apsHelper.getParameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);

			this.nomeservizio = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO);
			this.tiposervizio = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO);
			this.provider = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROVIDER_EROGATORE);
			this.accordo = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ACCORDO);
			this.servcorr = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_SERVIZIO_CORRELATO);
			// this.servpub = apsHelper.getParameter("servpub");
			//			this.endpointtype = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE );

			this.endpointtype = apsHelper.readEndPointType();
			this.tipoconn = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO );
			this.autenticazioneHttp = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP);

			this.connettoreDebug = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_DEBUG);

			this.erogazioneRuolo = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_RUOLO);
			this.erogazioneAutenticazione = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTENTICAZIONE);
			this.erogazioneAutenticazioneOpzionale = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTENTICAZIONE_OPZIONALE);
			this.erogazioneAutorizzazione = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE);
			this.erogazioneAutorizzazioneAutenticati = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_AUTENTICAZIONE);
			this.erogazioneAutorizzazioneRuoli = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_RUOLI);
			this.erogazioneAutorizzazioneRuoliTipologia = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_RUOLO_TIPOLOGIA);
			this.erogazioneAutorizzazioneRuoliMatch = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_RUOLO_MATCH);
			this.erogazioneSoggettoAutenticato = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_SOGGETTO_AUTENTICATO);
			
			// proxy
			this.proxy_enabled = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_ENABLED);
			this.proxy_hostname = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_HOSTNAME);
			this.proxy_port = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PORT);
			this.proxy_username = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_USERNAME);
			this.proxy_password = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PASSWORD);

			// tempi risposta
			this.tempiRisposta_enabled = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_REDEFINE);
			this.tempiRisposta_connectionTimeout = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_CONNECTION_TIMEOUT);
			this.tempiRisposta_readTimeout = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_READ_TIMEOUT);
			this.tempiRisposta_tempoMedioRisposta = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_TEMPO_MEDIO_RISPOSTA);
			
			// opzioni avanzate
			this.transfer_mode = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_MODE);
			this.transfer_mode_chunk_size = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_CHUNK_SIZE);
			this.redirect_mode = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MODE);
			this.redirect_max_hop = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MAX_HOP);
			this.opzioniAvanzate = ConnettoriHelper.getOpzioniAvanzate(apsHelper,this.transfer_mode, this.redirect_mode);

			// http
			this.url = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_URL  );
			if(TipiConnettore.HTTP.toString().equals(this.endpointtype)){
				this.user = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				this.password = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			}

			// jms
			this.nome = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_NOME_CODA);
			this.tipo = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_CODA);
			this.initcont = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_INIT_CTX);
			this.urlpgk = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_URL_PKG);
			this.provurl = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PROVIDER_URL);
			this.connfact = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_CONNECTION_FACTORY);
			this.sendas = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_OGGETTO_JMS);
			if(TipiConnettore.JMS.toString().equals(this.endpointtype)){
				this.user = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_USERNAME);
				this.password = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PASSWORD);
			}

			// https
			this.httpsurl = this.url;
			this.httpstipologia = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_SSL_TYPE );
			this.httpshostverifyS = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_HOST_VERIFY);
			this.httpspath = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION );
			this.httpstipo = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_TYPE);
			this.httpspwd = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
			this.httpsalgoritmo = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
			this.httpsstatoS = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO);
			this.httpskeystore = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE);
			this.httpspwdprivatekeytrust = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_STORE);
			this.httpspathkey = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_LOCATION);
			this.httpstipokey = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_TYPE);
			this.httpspwdkey = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
			this.httpspwdprivatekey = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_KEYSTORE);
			this.httpsalgoritmokey = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM);
			if(TipiConnettore.HTTPS.toString().equals(this.endpointtype)){
				this.user = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				this.password = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			}
			
			// file
			this.requestOutputFileName = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME);
			this.requestOutputFileNameHeaders = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS);
			this.requestOutputParentDirCreateIfNotExists = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR);
			this.requestOutputOverwriteIfExists = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE_NAME);
			this.responseInputMode = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_MODE);
			this.responseInputFileName = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME);
			this.responseInputFileNameHeaders = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_HEADERS);
			this.responseInputDeleteAfterRead = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_DELETE_AFTER_READ);
			this.responseInputWaitTime = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME);




			this.profilo = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROFILO);

			this.wsdlimpler = apsHelper.getBinaryParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_EROGATORE);
			this.wsdlimplfru = apsHelper.getBinaryParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_FRUITORE);
			this.portType = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PORT_TYPE);

			String priv = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PRIVATO);
			this.privato = ServletUtils.isCheckBoxEnabled(priv);

			this.descrizione = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_DESCRIZIONE);
			this.statoPackage = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO_PACKAGE);

			this.versione = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VERSIONE);
			this.nomeSA = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SA);
			
			
			this.providerSoggettoFruitore = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROVIDER_FRUITORE);
			
			this.fruizioneServizioApplicativo = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_FRUIZIONE_NOME_SA);
			this.fruizioneRuolo = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_RUOLO);
			this.fruizioneAutenticazione = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTENTICAZIONE);
			this.fruizioneAutenticazioneOpzionale = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTENTICAZIONE_OPZIONALE);
			this.fruizioneAutorizzazione = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE);
			this.fruizioneAutorizzazioneAutenticati = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_AUTENTICAZIONE);
			this.fruizioneAutorizzazioneRuoli = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_RUOLI);
			this.fruizioneAutorizzazioneRuoliTipologia = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_RUOLO_TIPOLOGIA);
			this.fruizioneAutorizzazioneRuoliMatch = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_RUOLO_MATCH);
			
			String gestioneToken = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN);
			String gestioneTokenPolicy = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_POLICY);
			String gestioneTokenOpzionale = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_OPZIONALE);
			String gestioneTokenValidazioneInput = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_VALIDAZIONE_INPUT);
			String gestioneTokenIntrospection = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_INTROSPECTION);
			String gestioneTokenUserInfo = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_USERINFO);
			String gestioneTokenTokenForward = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_TOKEN_FORWARD);
			
			String autenticazioneTokenIssuer = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_ISSUER);
			String autenticazioneTokenClientId = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_CLIENT_ID);
			String autenticazioneTokenSubject = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_SUBJECT);
			String autenticazioneTokenUsername = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_USERNAME);
			String autenticazioneTokenEMail = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_MAIL);
			
			String autorizzazione_tokenOptions = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN_OPTIONS);
			String autorizzazioneScope = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_SCOPE);
			String autorizzazioneScopeMatch = apsHelper.getParameter(CostantiControlStation.PARAMETRO_SCOPE_MATCH);
			String scope = apsHelper.getParameter(CostantiControlStation.PARAMETRO_SCOPE);
			
			BinaryParameter allegatoXacmlPolicy = apsHelper.getBinaryParameter(CostantiControlStation.PARAMETRO_DOCUMENTO_SICUREZZA_XACML_POLICY);

			if(apsHelper.isMultipart()){
				this.decodeRequestValidazioneDocumenti = true;
			}

			// boolean decodeReq = false;
			//			String ct = request.getContentType();
			//			if ((ct != null) && (ct.indexOf(Costanti.MULTIPART) != -1)) {
			//				// decodeReq = true;
			//				this.decodeRequestValidazioneDocumenti = false; // init
			//				this.decodeRequest(request,apsHelper);
			//			}

			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore();
			SoggettiCore soggettiCore = new SoggettiCore(apsCore);
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore(apsCore);
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore(apsCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(apsCore);
			ServiziApplicativiCore saCore = new ServiziApplicativiCore(apsCore);
			PddCore pddCore = new PddCore(apsCore);
			ConfigurazioneCore confCore = new ConfigurazioneCore(apsCore);

			String tipologia = ServletUtils.getObjectFromSession(session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
			boolean gestioneFruitori = false;
			boolean gestioneErogatori = false;
			if(tipologia!=null) {
				if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_EROGAZIONE.equals(tipologia)) {
					gestioneErogatori = true;
				}
				else if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE.equals(tipologia)) {
					gestioneFruitori = true;
				}
			}
			
			PddTipologia pddTipologiaSoggettoAutenticati = null;
			if(gestioneErogatori) {
				pddTipologiaSoggettoAutenticati = PddTipologia.ESTERNO;
			}
			
			if(ServletUtils.isEditModeInProgress(this.editMode)){
				// primo accesso alla servlet
				this.validazioneDocumenti = true;
				if (apsHelper.isModalitaAvanzata()) {
					String tmpValidazioneDocumenti = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VALIDAZIONE_DOCUMENTI);
					if(tmpValidazioneDocumenti!=null){
						if(Costanti.CHECK_BOX_ENABLED_TRUE.equalsIgnoreCase(tmpValidazioneDocumenti) || Costanti.CHECK_BOX_ENABLED.equalsIgnoreCase(tmpValidazioneDocumenti)){
							this.validazioneDocumenti = true;
						}else{
							this.validazioneDocumenti = false;
						}
					}
				}
			}else{
				if(!this.decodeRequestValidazioneDocumenti){
					String tmpValidazioneDocumenti = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VALIDAZIONE_DOCUMENTI);
					if(Costanti.CHECK_BOX_ENABLED_TRUE.equalsIgnoreCase(tmpValidazioneDocumenti) || Costanti.CHECK_BOX_ENABLED.equalsIgnoreCase(tmpValidazioneDocumenti)){
						this.validazioneDocumenti = true;
					}else{
						this.validazioneDocumenti = false;
					}
				}
			}

			this.httpshostverify = false;
			if (this.httpshostverifyS != null && this.httpshostverifyS.equals(Costanti.CHECK_BOX_ENABLED))
				this.httpshostverify = true;
			this.httpsstato = false;
			if (this.httpsstatoS != null && this.httpsstatoS.equals(Costanti.CHECK_BOX_ENABLED))
				this.httpsstato = true;

			Boolean isConnettoreCustomUltimaImmagineSalvata = null;

			Connettore conTmp = null;
			List<ExtendedConnettore> listExtendedConnettore = 
					ServletExtendedConnettoreUtils.getExtendedConnettore(conTmp, ConnettoreServletType.ACCORDO_SERVIZIO_PARTE_SPECIFICA_ADD, apsHelper, 
							this.parametersPOST, (this.endpointtype==null), this.endpointtype); // uso endpointtype per capire se è la prima volta che entro

			// Tipi protocollo supportati
			List<String> listaTipiProtocollo = apcCore.getProtocolliByFilter(session, true, true);
			
			// Preparo il menu
			apsHelper.makeMenu();

			if(listaTipiProtocollo.size()<=0) {
				
				List<String> _listaTipiProtocolloSoloSoggetti = apcCore.getProtocolliByFilter(session, true, false);
				
				if(_listaTipiProtocolloSoloSoggetti.size()>0) {
					pd.setMessage("Non risultano registrate API", Costanti.MESSAGE_TYPE_INFO);
				}
				else {
					pd.setMessage("Non risultano registrati soggetti", Costanti.MESSAGE_TYPE_INFO);
				}
				pd.disableEditMode();

				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS, 
						ForwardParams.ADD());
			}
			
			this.tipoProtocollo = apsHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PROTOCOLLO);
			if(this.tipoProtocollo == null){
				this.tipoProtocollo = apsCore.getProtocolloDefault(session, listaTipiProtocollo);
			}
			
			boolean connettoreStatic = false;
			if(gestioneFruitori) {
				connettoreStatic = apsCore.isConnettoreStatic(this.tipoProtocollo);
			}
			
			String[] ptList = null;
			// Prendo la lista di soggetti e la metto in un array
			// Prendo la lista di accordi e la metto in un array
			String[] soggettiList = null;
			String[] soggettiListLabel = null;
			String[] accordiList = null;
			String[] accordiListLabel = null;
			String[] soggettiFruitoriList = null;
			String[] soggettiFruitoriListLabel = null;
			// int totSogg = 0, totAcc = 0;

			boolean generaPortaApplicativa = !gestioneFruitori;
			boolean generaPortaDelegata = gestioneFruitori;
			boolean accordoPrivato = false;
			String uriAccordo = null;
			IDSoggetto soggettoReferente = null;
			int idReferente = -1;
			// accordi
			//			if (InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(session).getInterfaceType())) {
			//
			//				List<String[]> lstAccordiList = apsCore.getAccordiListLabels(userLogin);
			//
			//				accordiList = lstAccordiList.get(0);
			//				accordiListLabel = lstAccordiList.get(1);
			//
			//			} else {

			PermessiUtente pu = ServletUtils.getUserFromSession(session).getPermessi();

			boolean [] permessi = new boolean[2];
			permessi[0] = pu.isServizi();
			permessi[1] = pu.isAccordiCooperazione();
			Search searchAccordi = new Search(true);
			searchAccordi.addFilter(Liste.ACCORDI, Filtri.FILTRO_PROTOCOLLO, this.tipoProtocollo);
			List<AccordoServizioParteComune> listaTmp =  
					AccordiServizioParteComuneUtilities.accordiListFromPermessiUtente(apcCore, userLogin, searchAccordi, permessi);
			List<AccordoServizioParteComune> lista = null;
			if(apsHelper.isModalitaCompleta()) {
				lista = listaTmp;
			}
			else {
				// filtro accordi senza risorse o senza pt/operation
				lista = new ArrayList<AccordoServizioParteComune>();
				for (AccordoServizioParteComune accordoServizioParteComune : listaTmp) {
					if(org.openspcoop2.core.registry.constants.ServiceBinding.REST.equals(accordoServizioParteComune.getServiceBinding())) {
						if(accordoServizioParteComune.sizeResourceList()>0) {
							lista.add(accordoServizioParteComune);	
						}
					}
					else {
						boolean ptValido = false;
						for (PortType pt : accordoServizioParteComune.getPortTypeList()) {
							if(pt.sizeAzioneList()>0) {
								ptValido = true;
								break;
							}
						}
						if(ptValido) {
							lista.add(accordoServizioParteComune);	
						}
					}
				}
			}

			int accordoPrimoAccesso = -1;

			if (lista.size() > 0) {
				accordiList = new String[lista.size()];
				accordiListLabel = new String[lista.size()];
				int i = 0;
				for (AccordoServizioParteComune as : lista) {
					accordiList[i] = as.getId().toString();
					soggettoReferente = null;
					idReferente = -1;
					if(as.getSoggettoReferente()!=null && as.getSoggettoReferente().getId()!=null)
						idReferente = as.getSoggettoReferente().getId().intValue();

					if(idReferente>0){
						Soggetto sRef = soggettiCore.getSoggettoRegistro(idReferente);
						soggettoReferente = new IDSoggetto();
						soggettoReferente.setTipo(sRef.getTipo());
						soggettoReferente.setNome(sRef.getNome());

						// se ancora non ho scelto l'accordo da mostrare quando entro
						if(accordoPrimoAccesso == -1){
							//mostro il primo accordo che ha tipo che corrisponde a quello di default
							if(apcCore.getProtocolloDefault(session,listaTipiProtocollo).equals(soggettiCore.getProtocolloAssociatoTipoSoggetto(sRef.getTipo()))){
								accordoPrimoAccesso = i;
							}
						}
					}
					accordiListLabel[i] = apsHelper.getLabelIdAccordo(this.tipoProtocollo, idAccordoFactory.getIDAccordoFromAccordo(as));
					i++;
				}
			}

			// se ancora non ho scelto l'accordo da mostrare quando entro
			if(accordoPrimoAccesso == -1 && lista.size() > 0){
				// Se entro in questo caso significa che tutti gli accordi di servizio parte comune esistente s
				// possiedono come soggetto referente un tipo di protocollo differente da quello di default.
				// in questo caso prendo il primo che trovo
				accordoPrimoAccesso = 0;
			}

			//			}


			String postBackElementName = apsHelper.getPostBackElementName();

			// Controllo se ho modificato l'accordo, se si allora suggerisco il referente dell'accordo
			if(postBackElementName != null ){
				if(postBackElementName.equalsIgnoreCase(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ACCORDO) ||
						postBackElementName.equalsIgnoreCase(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROTOCOLLO)){
					
					if(postBackElementName.equalsIgnoreCase(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROTOCOLLO)){
						this.accordo = null;
						this.versione = null;
					}
					
					this.provider = null;
					this.tiposervizio = null;

					this.providerSoggettoFruitore = null;
					
					// reset protocol properties
					apsHelper.deleteBinaryParameters(this.wsdlimpler,this.wsdlimplfru);
					apsHelper.deleteProtocolPropertiesBinaryParameters(this.wsdlimpler,this.wsdlimplfru);

					this.portType = null;
					this.nomeservizio = "";
				}  
				
				if(postBackElementName.equalsIgnoreCase(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PORT_TYPE)){
					this.nomeservizio = "";
				}
				
			}


			// Lista port-type associati all'accordo di servizio
			AccordoServizioParteComune as = null;
			if (this.accordo != null && !"".equals(this.accordo)) {
				as = apcCore.getAccordoServizio(Long.parseLong(this.accordo));
			} else {
				if (accordiList != null){
					if(accordoPrimoAccesso >= 0 && accordoPrimoAccesso < accordiList.length)
						as = apcCore.getAccordoServizio(Long.parseLong(accordiList[accordoPrimoAccesso]));
					if(as!=null)
						this.accordo = as.getId() + "";
				}
			}
			if(as!=null){
				// salvo il soggetto referente
				soggettoReferente = new IDSoggetto(as.getSoggettoReferente().getTipo(), as.getSoggettoReferente().getNome());

				this.serviceBinding = apcCore.toMessageServiceBinding(as.getServiceBinding());
				this.formatoSpecifica = apcCore.formatoSpecifica2InterfaceType(as.getFormatoSpecifica());
				

				accordoPrivato = as.getPrivato()!=null && as.getPrivato();
				uriAccordo = idAccordoFactory.getUriFromAccordo(as);

				List<PortType> portTypesTmp = apcCore.accordiPorttypeList(as.getId().intValue(), new Search(true));
				List<PortType> portTypes = null;
				
				if(apsHelper.isModalitaCompleta()) {
					portTypes = portTypesTmp;
				}
				else {
					// filtro pt senza op
					portTypes = new ArrayList<PortType>();
					for (PortType portType : portTypesTmp) {
						if(portType.sizeAzioneList()>0) {
							portTypes.add(portType);
						}
					}
				}
				
				if (portTypes.size() > 0) {
					ptList = new String[portTypes.size() + 1];
					ptList[0] = "-";
					int i = 1;
					for (Iterator<PortType> iterator = portTypes.iterator(); iterator.hasNext();) {
						PortType portType2 = iterator.next();
						ptList[i] = portType2.getNome();
						i++;
					}
				}

				if( apsCore.isShowCorrelazioneAsincronaInAccordi() ){
					if (this.portType != null && !"".equals(this.portType) && !"-".equals(this.portType)){
						PortType pt = null;
						for(int i=0; i<as.sizePortTypeList(); i++){
							if(this.portType.equals(as.getPortType(i).getNome())){
								pt = as.getPortType(i);
								break;
							}
						}
						boolean servizioCorrelato = false;
						if(pt!=null){
							for(int i=0; i<pt.sizeAzioneList(); i++){
								Operation op = pt.getAzione(i);
								if(op.getCorrelataServizio()!=null && !pt.getNome().equals(op.getCorrelataServizio()) && op.getCorrelata()!=null){
									servizioCorrelato = true;
									break;
								}
							}
						}
						if(servizioCorrelato){
							this.servcorr=Costanti.CHECK_BOX_ENABLED;
						}
						else{
							this.servcorr=Costanti.CHECK_BOX_DISABLED;
						}							
					}
				}

			}

			// Fix per bug che accadeva in modalita' standard quando si seleziona un servizio di un accordo operativo, poi si cambia idea e si seleziona un accordo bozza.
			// lo stato del package rimaneva operativo.
			if(this.statoPackage!=null && apsHelper.isModalitaStandard()){
				if(apsCore.isShowGestioneWorkflowStatoDocumenti()){
					if(StatiAccordo.operativo.toString().equals(this.statoPackage) || StatiAccordo.finale.toString().equals(this.statoPackage)){
						if(as!=null && as.getStatoPackage().equals(StatiAccordo.bozza.toString()) ){
							this.statoPackage = StatiAccordo.bozza.toString(); 
						}
					}					
				}
			}
			
			//String profiloValue = profiloSoggettoErogatore;
			//if(this.profilo!=null && !"".equals(this.profilo) && !"-".equals(this.profilo)){
			//	profiloValue = this.profilo;
			//}

			// Versione
			//String profiloReferente = core.getSoggettoRegistro(new IDSoggetto(as.getSoggettoReferente().getTipo(),as.getSoggettoReferente().getNome())).getProfilo();

			List<String> versioniProtocollo = apsCore.getVersioniProtocollo(this.tipoProtocollo);
			List<String> tipiSoggettiCompatibiliAccordo = soggettiCore.getTipiSoggettiGestitiProtocollo(this.tipoProtocollo);
			List<String> tipiServizioCompatibiliAccordo = apsCore.getTipiServiziGestitiProtocollo(this.tipoProtocollo,this.serviceBinding);
			boolean erogazioneIsSupportatoAutenticazioneSoggetti = soggettiCore.isSupportatoAutenticazioneSoggetti(this.tipoProtocollo);

			// calcolo soggetti compatibili con accordi
			List<Soggetto> list = null;
			Search searchSoggetti = new Search(true);
			searchSoggetti.addFilter(Liste.SOGGETTI, Filtri.FILTRO_PROTOCOLLO, this.tipoProtocollo);
			if(gestioneFruitori) {
				searchSoggetti.addFilter(Liste.SOGGETTI, Filtri.FILTRO_DOMINIO, SoggettiCostanti.SOGGETTO_DOMINIO_ESTERNO_VALUE);
			}
			if(gestioneErogatori) {
				searchSoggetti.addFilter(Liste.SOGGETTI, Filtri.FILTRO_DOMINIO, SoggettiCostanti.SOGGETTO_DOMINIO_OPERATIVO_VALUE);
			}
			if(apsCore.isVisioneOggettiGlobale(userLogin)){
				list = soggettiCore.soggettiRegistroList(null, searchSoggetti);
			}else{
				list = soggettiCore.soggettiRegistroList(userLogin, searchSoggetti);
			}

			if (list.size() > 0) {
				List<String> soggettiListTmp = new ArrayList<String>();
				List<String> soggettiListLabelTmp = new ArrayList<String>();
				for (Soggetto soggetto : list) {
					soggettiListTmp.add(soggetto.getId().toString());
					soggettiListLabelTmp.add(apsHelper.getLabelNomeSoggetto(this.tipoProtocollo, soggetto.getTipo() , soggetto.getNome()));
				}

				boolean existsAPCCompatibili = lista!=null && lista.size()>0;

				if(soggettiListTmp.size()>0 && existsAPCCompatibili){
					soggettiList = soggettiListTmp.toArray(new String[1]);
					soggettiListLabel = soggettiListLabelTmp.toArray(new String[1]);
				}
				else{
					if(lista.size()<=0){
						pd.setMessage("Non esistono accordi di servizio parte comune", Costanti.MESSAGE_TYPE_INFO);
						pd.disableEditMode();

						Vector<DataElement> dati = new Vector<DataElement>();

						dati.addElement(ServletUtils.getDataElementForEditModeFinished());

						pd.setDati(dati);

						ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

						return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS, 
								ForwardParams.ADD());
					}

					// refresh di tutte le infromazioni
					versioniProtocollo = apsCore.getVersioniProtocollo(this.tipoProtocollo);
					tipiSoggettiCompatibiliAccordo = soggettiCore.getTipiSoggettiGestitiProtocollo(this.tipoProtocollo);
					tipiServizioCompatibiliAccordo = apsCore.getTipiServiziGestitiProtocollo(this.tipoProtocollo,this.serviceBinding);

					searchSoggetti = new Search(true);
					searchSoggetti.addFilter(Liste.SOGGETTI, Filtri.FILTRO_PROTOCOLLO, this.tipoProtocollo);
					if(apsCore.isVisioneOggettiGlobale(userLogin)){
						list = soggettiCore.soggettiRegistroList(null, searchSoggetti);
					}else{
						list = soggettiCore.soggettiRegistroList(userLogin, searchSoggetti);
					}
					
					for (Soggetto soggetto : list) {
						soggettiListTmp.add(soggetto.getId().toString());
						soggettiListLabelTmp.add(apsHelper.getLabelNomeSoggetto(this.tipoProtocollo, soggetto.getTipo() , soggetto.getNome()));
					}
					soggettiList = soggettiListTmp.toArray(new String[1]);
					soggettiListLabel = soggettiListLabelTmp.toArray(new String[1]);

					if(lista.size()>0){
						this.accordo = lista.get(0).getId()+"";
					}
				}
			}

			//String profiloSoggettoErogatore = null;
			if ((this.provider != null) && !this.provider.equals("")) {
				long idErogatore = Long.parseLong(this.provider);
				Soggetto soggetto = soggettiCore.getSoggettoRegistro(idErogatore);
				this.nomeSoggettoErogatore = soggetto.getNome();
				this.tipoSoggettoErogatore = soggetto.getTipo();
				//profiloSoggettoErogatore = soggetto.getVersioneProtocollo();
			} else {
				if(soggettoReferente != null ){
					Soggetto soggetto = soggettiCore.getSoggettoRegistro(soggettoReferente);
					for (Soggetto soggettoCheck : list) {
						if(soggettoCheck.getTipo().equals(soggetto.getTipo()) && soggettoCheck.getNome().equals(soggetto.getNome())) {
							this.provider = soggetto.getId() + "";
							this.nomeSoggettoErogatore = soggetto.getNome();
							this.tipoSoggettoErogatore = soggetto.getTipo();
							break;
						}
					}
				}
				// Se ancora non l'ho trovato prendo il primo della lista nel caso di gestione erogazione
				if ((this.provider == null) || this.provider.equals("")) {
					if( (gestioneErogatori || gestioneFruitori) && list!=null && list.size()>0) {
						Soggetto soggetto = list.get(0);
						this.provider = soggetto.getId() + "";
						this.nomeSoggettoErogatore = soggetto.getNome();
						this.tipoSoggettoErogatore = soggetto.getTipo();
					}
				}
			}
			
			
			if(this.tiposervizio == null){
				this.tiposervizio = apsCore.getTipoServizioDefaultProtocollo(this.tipoProtocollo,this.serviceBinding);
			}


			// Lista dei servizi applicativi per la creazione automatica
			String [] saSoggetti = null;	
			if ((this.provider != null) && !this.provider.equals("")) {
				int idErogatore = Integer.parseInt(this.provider);

				List<ServizioApplicativo> listaSA = saCore.getServiziApplicativiByIdErogatore(Long.valueOf(idErogatore));

				// rif bug #45
				// I servizi applicativi da visualizzare sono quelli che hanno
				// -Integration Manager (getMessage abilitato)
				// -connettore != disabilitato
				ArrayList<ServizioApplicativo> validSA = new ArrayList<ServizioApplicativo>();
				for (ServizioApplicativo sa : listaSA) {
					InvocazioneServizio invServizio = sa.getInvocazioneServizio();
					org.openspcoop2.core.config.Connettore connettore = invServizio != null ? invServizio.getConnettore() : null;
					StatoFunzionalita getMessage = invServizio != null ? invServizio.getGetMessage() : null;

					if ((connettore != null && !TipiConnettore.DISABILITATO.getNome().equals(connettore.getTipo())) || CostantiConfigurazione.ABILITATO.equals(getMessage)) {
						// il connettore non e' disabilitato oppure il get
						// message e' abilitato
						// Lo aggiungo solo se gia' non esiste tra quelli
						// aggiunti
						validSA.add(sa);
					}
				}

				// Prendo la lista di servizioApplicativo associati al soggetto
				// e la metto in un array
				saSoggetti = new String[validSA.size()+1];
				saSoggetti[0] = "-"; // elemento nullo di default
				for (int i = 0; i < validSA.size(); i++) {
					ServizioApplicativo sa = validSA.get(i);
					saSoggetti[i+1] = sa.getNome();
				}
			}
			
			// calcolo soggetti fruitori
			List<Soggetto> listFruitori = null;
			if(gestioneFruitori) {
				Search searchSoggettiFruitori = new Search(true);
				searchSoggettiFruitori.addFilter(Liste.SOGGETTI, Filtri.FILTRO_PROTOCOLLO, this.tipoProtocollo);
				searchSoggettiFruitori.addFilter(Liste.SOGGETTI, Filtri.FILTRO_DOMINIO, SoggettiCostanti.SOGGETTO_DOMINIO_OPERATIVO_VALUE);
				if(apsCore.isVisioneOggettiGlobale(userLogin)){
					listFruitori = soggettiCore.soggettiRegistroList(null, searchSoggettiFruitori);
				}else{
					listFruitori = soggettiCore.soggettiRegistroList(userLogin, searchSoggettiFruitori);
				}
	
				if (listFruitori.size() > 0) {
					List<String> soggettiListTmp = new ArrayList<String>();
					List<String> soggettiListLabelTmp = new ArrayList<String>();
					for (Soggetto soggetto : listFruitori) {
						soggettiListTmp.add(soggetto.getId().toString());
						soggettiListLabelTmp.add(apsHelper.getLabelNomeSoggetto(this.tipoProtocollo, soggetto.getTipo() , soggetto.getNome()));
					}
	
					if(soggettiListTmp.size()>0){
						soggettiFruitoriList = soggettiListTmp.toArray(new String[1]);
						soggettiFruitoriListLabel = soggettiListLabelTmp.toArray(new String[1]);
					}
					else {
						pd.setMessage("Non esistono soggetti nel dominio interno", Costanti.MESSAGE_TYPE_INFO);
						pd.disableEditMode();

						Vector<DataElement> dati = new Vector<DataElement>();

						dati.addElement(ServletUtils.getDataElementForEditModeFinished());

						pd.setDati(dati);

						ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

						return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS, 
								ForwardParams.ADD());
					}
				}
			}
			
			if(gestioneFruitori) {
				if ((this.providerSoggettoFruitore != null) && !this.providerSoggettoFruitore.equals("")) {
					long idFruitore = Long.parseLong(this.providerSoggettoFruitore);
					Soggetto soggetto = soggettiCore.getSoggettoRegistro(idFruitore);
					this.nomeSoggettoFruitore = soggetto.getNome();
					this.tipoSoggettoFruitore = soggetto.getTipo();
					//profiloSoggettoErogatore = soggetto.getVersioneProtocollo();
				}
				else {
					Soggetto soggetto = listFruitori.get(0);
					this.providerSoggettoFruitore = soggetto.getId()+"";
					this.nomeSoggettoFruitore = soggetto.getNome();
					this.tipoSoggettoFruitore = soggetto.getTipo();
				}
			}
			
			// ServiziApplicativi
			List<String> saFruitoriList = new ArrayList<String>();
			saFruitoriList.add("-");
			if(gestioneFruitori && this.nomeSoggettoFruitore!=null && this.tipoSoggettoFruitore!=null){
				try{
					
					IDSoggetto idSoggettoSelected = new IDSoggetto(this.tipoSoggettoFruitore, this.nomeSoggettoFruitore);
					
					String auth = this.fruizioneAutenticazione;
					if(auth==null || "".equals(auth)){
						auth = apsCore.getAutenticazione_generazioneAutomaticaPorteDelegate();
					}
					List<ServizioApplicativo> oldSilList = null;
					if(apsCore.isVisioneOggettiGlobale(userLogin)){
						oldSilList = saCore.soggettiServizioApplicativoList(idSoggettoSelected,null,
								org.openspcoop2.core.config.constants.CredenzialeTipo.toEnumConstant(auth));
					}
					else {
						oldSilList = saCore.soggettiServizioApplicativoList(idSoggettoSelected,userLogin,
								org.openspcoop2.core.config.constants.CredenzialeTipo.toEnumConstant(auth));
					}
					if(oldSilList!=null && oldSilList.size()>0){
						for (int i = 0; i < oldSilList.size(); i++) {
							saFruitoriList.add(oldSilList.get(i).getNome());		
						}
					}
				}catch(DriverConfigurazioneNotFound dNotFound){}

			}
			
			List<String> soggettiAutenticati = new ArrayList<String>();
			List<String> soggettiAutenticatiLabel = new ArrayList<String>();
			// lista soggetti autenticati per la creazione automatica
			CredenzialeTipo credenziale =  null;
			if((this.erogazioneAutenticazione !=null && !"".equals(this.erogazioneAutenticazione)) && erogazioneIsSupportatoAutenticazioneSoggetti) {
				TipoAutenticazione tipoAutenticazione = TipoAutenticazione.toEnumConstant(this.erogazioneAutenticazione);
				credenziale = !tipoAutenticazione.equals(TipoAutenticazione.DISABILITATO) ? CredenzialeTipo.toEnumConstant(this.erogazioneAutenticazione) : null;
			}
			
			List<org.openspcoop2.core.registry.Soggetto> listSoggettiCompatibili = null;
			 
			if(apsCore.isVisioneOggettiGlobale(userLogin)){
				listSoggettiCompatibili = soggettiCore.getSoggettiFromTipoAutenticazione(tipiSoggettiCompatibiliAccordo, null, credenziale, pddTipologiaSoggettoAutenticati );
			}else{
				listSoggettiCompatibili = soggettiCore.getSoggettiFromTipoAutenticazione(tipiSoggettiCompatibiliAccordo, userLogin, credenziale, pddTipologiaSoggettoAutenticati);
			}
			
			if(listSoggettiCompatibili != null && listSoggettiCompatibili.size() >0 ) {
				
				soggettiAutenticati.add("-"); // elemento nullo di default
				soggettiAutenticatiLabel.add("-");
				for (Soggetto soggetto : listSoggettiCompatibili) {
					soggettiAutenticati.add(soggetto.getTipo() + "/"+ soggetto.getNome());
					soggettiAutenticatiLabel.add(apsHelper.getLabelNomeSoggetto(this.tipoProtocollo, soggetto.getTipo(), soggetto.getNome())); 
				}
			}
			

			// Controllo se il soggetto erogare appartiene ad una pdd di tipo operativo.
			if(this.tipoSoggettoErogatore!=null && !"".equals(this.tipoSoggettoErogatore) 
					&&  this.nomeSoggettoErogatore!=null && !"".equals(this.nomeSoggettoErogatore)){
				IDSoggetto idSoggettoEr = new IDSoggetto(this.tipoSoggettoErogatore, this.nomeSoggettoErogatore);
				Soggetto soggetto = soggettiCore.getSoggettoRegistro(idSoggettoEr );
				if(pddCore.isPddEsterna(soggetto.getPortaDominio())){
					generaPortaApplicativa = false;
				}
			}
			
			this.protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(this.tipoProtocollo);
			this.consoleDynamicConfiguration =  this.protocolFactory.createDynamicConfigurationConsole();
			this.registryReader = soggettiCore.getRegistryReader(this.protocolFactory); 
			this.configRegistryReader = soggettiCore.getConfigIntegrationReader(this.protocolFactory);
						
			// ID Accordo Null per default
			IDServizio idAps = null;
			this.consoleConfiguration = this.consoleDynamicConfiguration.getDynamicConfigAccordoServizioParteSpecifica(this.consoleOperationType, this.consoleInterfaceType, 
					this.registryReader, this.configRegistryReader, idAps );
			this.protocolProperties = apsHelper.estraiProtocolPropertiesDaRequest(this.consoleConfiguration, this.consoleOperationType);
			
			List<GenericProperties> gestorePolicyTokenList = confCore.gestorePolicyTokenList(null, ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPOLOGIA_GESTIONE_POLICY_TOKEN, null);
			String [] policyLabels = new String[gestorePolicyTokenList.size() + 1];
			String [] policyValues = new String[gestorePolicyTokenList.size() + 1];
			
			policyLabels[0] = CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO;
			policyValues[0] = CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO;
			
			for (int i = 0; i < gestorePolicyTokenList.size(); i++) {
			GenericProperties genericProperties = gestorePolicyTokenList.get(i);
				policyLabels[(i+1)] = genericProperties.getNome();
				policyValues[(i+1)] = genericProperties.getNome();
			}
			
			String servletList = null;
			String labelList = null;
			
			Boolean vistaErogazioni = ServletUtils.getBooleanAttributeFromSession(ErogazioniCostanti.ASPS_EROGAZIONI_ATTRIBUTO_VISTA_EROGAZIONI, session);
			if(vistaErogazioni != null && vistaErogazioni.booleanValue()) {
				servletList = ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_LIST;
				if(gestioneFruitori) {
					labelList = ErogazioniCostanti.LABEL_ASPS_FRUIZIONI;
				}
				else {
					labelList = ErogazioniCostanti.LABEL_ASPS_EROGAZIONI;
				}
			} else {
				servletList = AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST;
				if(gestioneFruitori) {
					labelList = AccordiServizioParteSpecificaCostanti.LABEL_APS_FRUITORI;
				}
				else {
					labelList = AccordiServizioParteSpecificaCostanti.LABEL_APS;
				}
			}

			// Se nomehid = null, devo visualizzare la pagina per l'inserimento dati
			if(ServletUtils.isEditModeInProgress(this.editMode)){
				// setto la barra del titolo
				ServletUtils.setPageDataTitle_ServletAdd(pd, labelList,servletList);
				

				if(apsCore.isShowGestioneWorkflowStatoDocumenti()){
					if(this.nomeservizio==null || "".equals(this.nomeservizio)){
						this.statoPackage=StatiAccordo.bozza.toString();
					}

					// Se l'accordo Comune che si riferisce ha stato operativo o finale modifico lo stato in operativo
					// Per visualizzare immediatamente all'utente
					if(as!=null && 
							(as.getStatoPackage().equals(StatiAccordo.operativo.toString()) || as.getStatoPackage().equals(StatiAccordo.finale.toString()))
							){
						this.statoPackage = StatiAccordo.operativo.toString(); 
					}

				}else{
					this.statoPackage=StatiAccordo.finale.toString();
				}

				if (this.nomeservizio == null) {
					this.nomeservizio = "";
					//					this.provider = "";
					//					this.accordo = "";
					this.servcorr = "";
					// this.servpub = "";
//					if(this.wsdlimpler.getValue() == null)
//						this.wsdlimpler.setValue(new byte[1]);
//					if(this.wsdlimplfru.getValue() == null)
//						this.wsdlimplfru.setValue(new byte[1]); 
					this.tipoconn = "";
					this.url = "";
					this.nome = "";
					this.tipo = ConnettoriCostanti.TIPI_CODE_JMS[0];
					this.user = "";
					this.password = "";
					this.initcont = "";
					this.urlpgk = "";
					this.provurl = "";
					this.connfact = "";
					this.sendas = ConnettoriCostanti.TIPO_SEND_AS[0];
					this.profilo = "-";
					if(ServiceBinding.SOAP.equals(this.serviceBinding)) {
						if(ptList!=null && ptList.length==2){
							this.portType = ptList[1]; // al posto 0 è presente '-'
							this.nomeservizio = this.portType;
						}
						else {
							this.portType = "-";
						}
					}else {
						this.portType = "-";
					}
					this.descrizione = "";
					this.httpsurl = "";
					this.httpstipologia = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TYPE;
					this.httpshostverifyS = Costanti.CHECK_BOX_ENABLED_TRUE;
					this.httpshostverify = true;
					this.httpspath = "";
					this.httpstipo = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TIPOLOGIA_KEYSTORE_TYPE;
					this.httpspwd = "";
					this.httpsstato = false;
					this.httpskeystore = AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_DEFAULT;
					this.httpspwdprivatekeytrust = "";
					this.httpspathkey = "";
					this.httpstipokey =ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TIPOLOGIA_KEYSTORE_TYPE;
					this.httpspwdkey = "";
					this.httpspwdprivatekey = "";
					this.versione="1";
				}
				
				if(this.endpointtype==null) {
					if(apsHelper.isModalitaCompleta()==false) {
						this.endpointtype = TipiConnettore.HTTP.getNome();
					}
					else {
						this.endpointtype = AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_DISABILITATO;
					}
				}
				
				switch (this.serviceBinding) {
					case REST:
						if(this.nomeservizio==null || "".equals(this.nomeservizio)){
							this.nomeservizio = as.getNome();
						}
						
						break;
					case SOAP:
					default:
						if(this.portType!=null && !"".equals(this.portType) && !"-".equals(this.portType)){
		
							boolean ptValid = true;
		
							if(ptList!=null && ptList.length>0){
								// controllo che l'attuale port Type sia tra quelli presenti nell'accordo.
								boolean found = false;
								for (String portType : ptList) {
									if(portType.equals(this.portType)){
										found = true;
										break;
									}
								}
								if(!found){
									ptValid = false;
								}
		
							}
		
							if(ptValid){
		
								if(this.nomeservizio==null || "".equals(this.nomeservizio)){
									this.nomeservizio = this.portType;
								}
								else if(this.nomeservizio.equals(this.oldPortType)){
									this.nomeservizio = this.portType;
								}
		
								this.oldPortType = this.portType;
		
							}
							else{
		
								this.nomeservizio = null;
								this.portType = null;
								this.oldPortType = null;
		
							}
						}  else {
							if(ptList ==null || ptList.length < 1){
								this.nomeservizio = as.getNome();
							}
							else if(ptList!=null && ptList.length==2){
								this.portType = ptList[1]; // al posto 0 è presente '-'
								this.nomeservizio = this.portType;
							}
						}
		
					
					break;
				}


				if(this.erogazioneRuolo==null || "".equals(this.erogazioneRuolo))
					this.erogazioneRuolo = "-";
				if(this.erogazioneAutenticazione==null || "".equals(this.erogazioneAutenticazione)) {
					this.erogazioneAutenticazione = apsCore.getAutenticazione_generazioneAutomaticaPorteApplicative();
					
					soggettiAutenticati = new ArrayList<String>();
					soggettiAutenticatiLabel = new ArrayList<String>();
					if(erogazioneIsSupportatoAutenticazioneSoggetti) {
						TipoAutenticazione tipoAutenticazione = TipoAutenticazione.toEnumConstant(this.erogazioneAutenticazione);
						credenziale = !tipoAutenticazione.equals(TipoAutenticazione.DISABILITATO) ? CredenzialeTipo.toEnumConstant(this.erogazioneAutenticazione) : null;
					}
					 
					if(apsCore.isVisioneOggettiGlobale(userLogin)){
						listSoggettiCompatibili = soggettiCore.getSoggettiFromTipoAutenticazione(tipiSoggettiCompatibiliAccordo, null, credenziale, pddTipologiaSoggettoAutenticati );
					}else{
						listSoggettiCompatibili = soggettiCore.getSoggettiFromTipoAutenticazione(tipiSoggettiCompatibiliAccordo, userLogin, credenziale, pddTipologiaSoggettoAutenticati);
					}
					
					if(listSoggettiCompatibili != null && listSoggettiCompatibili.size() >0 ) {
						soggettiAutenticati.add("-"); // elemento nullo di default
						soggettiAutenticatiLabel.add("-");
						for (Soggetto soggetto : listSoggettiCompatibili) {
							soggettiAutenticati.add(soggetto.getTipo() + "/"+ soggetto.getNome());
							soggettiAutenticatiLabel.add(apsHelper.getLabelNomeSoggetto(this.tipoProtocollo, soggetto.getTipo(), soggetto.getNome())); 
						}
					}
					
				}
				if(this.erogazioneAutorizzazione==null || "".equals(this.erogazioneAutorizzazione)){
					String tipoAutorizzazione = apsCore.getAutorizzazione_generazioneAutomaticaPorteApplicative();
					this.erogazioneAutorizzazione = AutorizzazioneUtilities.convertToStato(tipoAutorizzazione);
					if(TipoAutorizzazione.isAuthenticationRequired(tipoAutorizzazione))
						this.erogazioneAutorizzazioneAutenticati = Costanti.CHECK_BOX_ENABLED;
					if(TipoAutorizzazione.isRolesRequired(tipoAutorizzazione))
						this.erogazioneAutorizzazioneRuoli = Costanti.CHECK_BOX_ENABLED;
					this.erogazioneAutorizzazioneRuoliTipologia = AutorizzazioneUtilities.convertToRuoloTipologia(tipoAutorizzazione).getValue();
				}
				
				if(gestioneFruitori) {
					if(this.fruizioneServizioApplicativo==null || "".equals(this.fruizioneServizioApplicativo))
						this.fruizioneServizioApplicativo = "-";
					if(this.fruizioneRuolo==null || "".equals(this.fruizioneRuolo))
						this.fruizioneRuolo = "-";
					if(this.fruizioneAutenticazione==null || "".equals(this.fruizioneAutenticazione))
						this.fruizioneAutenticazione = apsCore.getAutenticazione_generazioneAutomaticaPorteDelegate();
					if(this.fruizioneAutorizzazione==null || "".equals(this.fruizioneAutorizzazione)){
						String tipoAutorizzazione = apsCore.getAutorizzazione_generazioneAutomaticaPorteDelegate();
						this.fruizioneAutorizzazione = AutorizzazioneUtilities.convertToStato(tipoAutorizzazione);
						if(TipoAutorizzazione.isAuthenticationRequired(tipoAutorizzazione))
							this.fruizioneAutorizzazioneAutenticati = Costanti.CHECK_BOX_ENABLED;
						if(TipoAutorizzazione.isRolesRequired(tipoAutorizzazione))
							this.fruizioneAutorizzazioneRuoli = Costanti.CHECK_BOX_ENABLED;
						this.fruizioneAutorizzazioneRuoliTipologia = AutorizzazioneUtilities.convertToRuoloTipologia(tipoAutorizzazione).getValue();
					}
				}
				
				// default
				if(this.httpsalgoritmo==null || "".equals(this.httpsalgoritmo)){
					this.httpsalgoritmo = TrustManagerFactory.getDefaultAlgorithm();
				}
				if(this.httpsalgoritmokey==null || "".equals(this.httpsalgoritmokey)){
					this.httpsalgoritmokey = KeyManagerFactory.getDefaultAlgorithm();
				}
				if(this.httpstipologia==null || "".equals(this.httpstipologia)){
					this.httpstipologia = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TYPE;
				}
				if(this.httpshostverifyS==null || "".equals(this.httpshostverifyS)){
					this.httpshostverifyS = Costanti.CHECK_BOX_ENABLED_TRUE;
					this.httpshostverify = true;
				}

				String tipoSendas = ConnettoriCostanti.TIPO_SEND_AS[0];
				String tipoJms = ConnettoriCostanti.TIPI_CODE_JMS[0];

				this.autenticazioneHttp = apsHelper.getAutenticazioneHttp(this.autenticazioneHttp, this.endpointtype, this.user);

				if(this.tempiRisposta_connectionTimeout==null || "".equals(this.tempiRisposta_connectionTimeout) 
						|| 
						this.tempiRisposta_readTimeout==null || "".equals(this.tempiRisposta_readTimeout) 
						|| 
						this.tempiRisposta_tempoMedioRisposta==null || "".equals(this.tempiRisposta_tempoMedioRisposta) ){
					
					ConfigurazioneCore configCore = new ConfigurazioneCore(soggettiCore);
					ConfigurazioneGenerale configGenerale = configCore.getConfigurazioneControlloTraffico();
					
					if(this.tempiRisposta_connectionTimeout==null || "".equals(this.tempiRisposta_connectionTimeout) ) {
						this.tempiRisposta_connectionTimeout = configGenerale.getTempiRispostaFruizione().getConnectionTimeout().intValue()+"";
					}
					if(this.tempiRisposta_readTimeout==null || "".equals(this.tempiRisposta_readTimeout) ) {
						this.tempiRisposta_readTimeout = configGenerale.getTempiRispostaFruizione().getReadTimeout().intValue()+"";
					}
					if(this.tempiRisposta_tempoMedioRisposta==null || "".equals(this.tempiRisposta_tempoMedioRisposta) ) {
						this.tempiRisposta_tempoMedioRisposta = configGenerale.getTempiRispostaFruizione().getTempoMedioRisposta().intValue()+"";
					}
					
				}
				if(gestioneToken == null) {
					gestioneToken = StatoFunzionalita.DISABILITATO.getValue();
					gestioneTokenPolicy = CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO;
					gestioneTokenOpzionale = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_OPZIONALE;
					gestioneTokenValidazioneInput = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_VALIDAZIONE_INPUT;
					gestioneTokenIntrospection = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_INTROSPECTION;
					gestioneTokenUserInfo = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_USER_INFO;
					gestioneTokenTokenForward = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TOKEN_FORWARD;
					autenticazioneTokenIssuer = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_ISSUER;
					autenticazioneTokenClientId = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_CLIENT_ID;
					autenticazioneTokenSubject = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_SUBJECT;
					autenticazioneTokenUsername = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_USERNAME;
					autenticazioneTokenEMail = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_EMAIL;
				}
				
				if(scope ==null || "".equals(scope))
					scope = "-";
				if(autorizzazioneScope ==null)
					autorizzazioneScope = "";
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				// update della configurazione 
				this.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioParteSpecifica(this.consoleConfiguration, this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties, 
						this.registryReader, this.configRegistryReader, idAps);

				dati = apsHelper.addServiziToDati(dati, this.nomeservizio, this.tiposervizio,  null, null, 
						this.provider, null, null, 
						soggettiList, soggettiListLabel, this.accordo, this.serviceBinding, this.formatoSpecifica, accordiList, accordiListLabel, this.servcorr, 
						this.wsdlimpler, this.wsdlimplfru, tipoOp, "0", tipiServizioCompatibiliAccordo, 
						this.profilo, this.portType, ptList, this.privato,uriAccordo,this.descrizione,-1l,this.statoPackage,this.statoPackage,
						this.versione,versioniProtocollo,this.validazioneDocumenti,
						saSoggetti,this.nomeSA,generaPortaApplicativa,null,
						this.erogazioneRuolo,this.erogazioneAutenticazione,this.erogazioneAutenticazioneOpzionale,this.erogazioneAutorizzazione,erogazioneIsSupportatoAutenticazioneSoggetti,
						this.erogazioneAutorizzazioneAutenticati, this.erogazioneAutorizzazioneRuoli, this.erogazioneAutorizzazioneRuoliTipologia, this.erogazioneAutorizzazioneRuoliMatch,
						soggettiAutenticati,soggettiAutenticatiLabel, this.erogazioneSoggettoAutenticato,
						this.tipoProtocollo, listaTipiProtocollo,
						soggettiFruitoriList, soggettiFruitoriListLabel, this.providerSoggettoFruitore, this.tipoSoggettoFruitore, this.nomeSoggettoFruitore,
						this.fruizioneServizioApplicativo,this.fruizioneRuolo,this.fruizioneAutenticazione,this.fruizioneAutenticazioneOpzionale,this.fruizioneAutorizzazione,
						this.fruizioneAutorizzazioneAutenticati, this.fruizioneAutorizzazioneRuoli, this.fruizioneAutorizzazioneRuoliTipologia, this.fruizioneAutorizzazioneRuoliMatch,
						saFruitoriList,gestioneToken, policyLabels, policyValues, 
						gestioneTokenPolicy, gestioneTokenOpzionale,
						gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward,
						autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
						autorizzazione_tokenOptions,
						autorizzazioneScope,scope,autorizzazioneScopeMatch,allegatoXacmlPolicy);

				// Controllo se richiedere il connettore
				
				if(!connettoreStatic) {
					boolean forceEnableConnettore = false;
					if( gestioneFruitori || generaPortaApplicativa ) {
						forceEnableConnettore = true;
					}
					
					dati = apsHelper.addEndPointToDati(dati, this.connettoreDebug, this.endpointtype, this.autenticazioneHttp, 
							(apsHelper.isModalitaCompleta() || !multitenant)?null:
								(generaPortaApplicativa?AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_INTERNO_PREFIX : AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_ESTERNO_PREFIX), 
							this.url, this.nome,
							tipoJms, this.user,
							this.password, this.initcont, this.urlpgk,
							this.provurl, this.connfact, tipoSendas,
							AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,tipoOp, this.httpsurl, this.httpstipologia,
							this.httpshostverify, this.httpspath, this.httpstipo, this.httpspwd,
							this.httpsalgoritmo, this.httpsstato, this.httpskeystore,
							this.httpspwdprivatekeytrust, this.httpspathkey,
							this.httpstipokey, this.httpspwdkey, this.httpspwdprivatekey,
							this.httpsalgoritmokey, this.tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_ADD, null, null,
							null, null, null, null, null, null, true,
							isConnettoreCustomUltimaImmagineSalvata, 
							this.proxy_enabled, this.proxy_hostname, this.proxy_port, this.proxy_username, this.proxy_password,
							this.tempiRisposta_enabled, this.tempiRisposta_connectionTimeout, this.tempiRisposta_readTimeout, this.tempiRisposta_tempoMedioRisposta,
							this.opzioniAvanzate, this.transfer_mode, this.transfer_mode_chunk_size, this.redirect_mode, this.redirect_max_hop,
							this.requestOutputFileName,this.requestOutputFileNameHeaders,this.requestOutputParentDirCreateIfNotExists,this.requestOutputOverwriteIfExists,
							this.responseInputMode, this.responseInputFileName, this.responseInputFileNameHeaders, this.responseInputDeleteAfterRead, this.responseInputWaitTime,
							listExtendedConnettore, forceEnableConnettore);
				}
					
				// aggiunta campi custom
				dati = apsHelper.addProtocolPropertiesToDati(dati, this.consoleConfiguration,this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,
						ForwardParams.ADD());
			}

			if (apsHelper.isModalitaStandard()) {
				switch (this.serviceBinding) {
				case REST:
					// il nome del servizio e' quello dell'accordo
					this.nomeservizio = as.getNome();
					break;
				case SOAP:
				default:
					// il nome del servizio e' quello del porttype selezionato
					this.nomeservizio = this.portType;
					break;
				}
			}

			// Controlli sui campi immessi
			boolean isOk = apsHelper.serviziCheckData(tipoOp, soggettiList,
					accordiList, this.nomeservizio, this.tiposervizio, 
					(this.versione!=null && !"".equals(this.versione)) ? Integer.parseInt(this.versione) : 1,
					this.nomeservizio, this.tiposervizio, this.provider,
					this.nomeSoggettoErogatore, this.tipoSoggettoErogatore,
					this.accordo, this.serviceBinding, this.servcorr, this.endpointtype,
					this.url, this.nome, this.tipo, this.user,
					this.password, this.initcont, this.urlpgk, this.provurl,
					this.connfact, this.sendas, this.wsdlimpler,
					this.wsdlimplfru, "0", this.profilo, this.portType, ptList,
					accordoPrivato,this.privato, this.httpsurl, this.httpstipologia,
					this.httpshostverify, this.httpspath, this.httpstipo,
					this.httpspwd, this.httpsalgoritmo, this.httpsstato,
					this.httpskeystore, this.httpspwdprivatekeytrust,
					this.httpspathkey, this.httpstipokey,
					this.httpspwdkey, this.httpspwdprivatekey,
					this.httpsalgoritmokey, this.tipoconn,this.versione,this.validazioneDocumenti,null,this.autenticazioneHttp,
					this.proxy_enabled, this.proxy_hostname, this.proxy_port, this.proxy_username, this.proxy_password,
					this.tempiRisposta_enabled, this.tempiRisposta_connectionTimeout, this.tempiRisposta_readTimeout, this.tempiRisposta_tempoMedioRisposta,
					this.opzioniAvanzate, this.transfer_mode, this.transfer_mode_chunk_size, this.redirect_mode, this.redirect_max_hop,
					this.requestOutputFileName,this.requestOutputFileNameHeaders,this.requestOutputParentDirCreateIfNotExists,this.requestOutputOverwriteIfExists,
					this.responseInputMode, this.responseInputFileName, this.responseInputFileNameHeaders, this.responseInputDeleteAfterRead, this.responseInputWaitTime,
					null,this.erogazioneRuolo,this.erogazioneAutenticazione,this.erogazioneAutenticazioneOpzionale,this.erogazioneAutorizzazione,
					this.erogazioneAutorizzazioneAutenticati, this.erogazioneAutorizzazioneRuoli, this.erogazioneAutorizzazioneRuoliTipologia, this.erogazioneAutorizzazioneRuoliMatch,erogazioneIsSupportatoAutenticazioneSoggetti,
					generaPortaApplicativa, listExtendedConnettore,
					this.fruizioneServizioApplicativo,this.fruizioneRuolo,this.fruizioneAutenticazione,this.fruizioneAutenticazioneOpzionale,this.fruizioneAutorizzazione,
					this.fruizioneAutorizzazioneAutenticati, this.fruizioneAutorizzazioneRuoli, this.fruizioneAutorizzazioneRuoliTipologia, this.fruizioneAutorizzazioneRuoliMatch,
					this.tipoProtocollo, allegatoXacmlPolicy);

			if(isOk){
				if(generaPortaApplicativa && apsHelper.isModalitaCompleta() && (this.nomeSA==null || "".equals(this.nomeSA) || "-".equals(this.nomeSA))){
					if(saSoggetti==null || saSoggetti.length==0 || (saSoggetti.length==1 && "-".equals(saSoggetti[0]))){
						pd.setMessage(MessageFormat.format(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_PRIMA_DI_POTER_DEFINIRE_UN_ACCORDO_PARTE_SPECIFICA_DEVE_ESSERE_CREATO_UN_SERVIZIO_APPLICATIVO_EROGATO_DAL_SOGGETTO_X_Y,
								this.tipoSoggettoErogatore, this.nomeSoggettoErogatore));
					}
					else{
						pd.setMessage(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_NON_E_POSSIBILE_CREARE_L_ACCORDO_PARTE_SPECIFICA_SENZA_SELEZIONARE_UN_SERVIZIO_APPLICATIVO_EROGATORE);
					}
					isOk = false;
				}
			}

			// Validazione base dei parametri custom 
			if(isOk){
				try{
					apsHelper.validaProtocolProperties(this.consoleConfiguration, this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties);
				}catch(ProtocolException e){
					ControlStationCore.getLog().error(e.getMessage(),e);
					pd.setMessage(e.getMessage());
					isOk = false;
				}
			}

			// Valido i parametri custom se ho gia' passato tutta la validazione prevista
			if(isOk){
				try{
					idAps = apsHelper.getIDServizioFromValues(this.tiposervizio, this.nomeservizio, this.provider, this.versione);
					//validazione campi dinamici
					this.consoleDynamicConfiguration.validateDynamicConfigAccordoServizioParteSpecifica(this.consoleConfiguration, this.consoleOperationType, this.protocolProperties, 
							this.registryReader, this.configRegistryReader, idAps);
				}catch(ProtocolException e){
					ControlStationCore.getLog().error(e.getMessage(),e);
					pd.setMessage(e.getMessage());
					isOk = false;
				}
			}

			if (!isOk) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle_ServletAdd(pd, labelList, servletList);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				// update della configurazione 
				this.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioParteSpecifica(this.consoleConfiguration, this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties, 
						this.registryReader, this.configRegistryReader, idAps);

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = apsHelper.addServiziToDati(dati, this.nomeservizio, this.tiposervizio, null, null,  
						this.provider, null, null, 
						soggettiList, soggettiListLabel, this.accordo, this.serviceBinding, this.formatoSpecifica, accordiList, accordiListLabel,
						this.servcorr, this.wsdlimpler, this.wsdlimplfru, tipoOp, "0", tipiServizioCompatibiliAccordo, 
						this.profilo, this.portType, ptList, this.privato,uriAccordo,this.descrizione,-1l,this.statoPackage,
						this.statoPackage,this.versione,versioniProtocollo,this.validazioneDocumenti,
						saSoggetti,this.nomeSA,generaPortaApplicativa,null,
						this.erogazioneRuolo,this.erogazioneAutenticazione,this.erogazioneAutenticazioneOpzionale,this.erogazioneAutorizzazione,erogazioneIsSupportatoAutenticazioneSoggetti,
						this.erogazioneAutorizzazioneAutenticati, this.erogazioneAutorizzazioneRuoli, this.erogazioneAutorizzazioneRuoliTipologia, this.erogazioneAutorizzazioneRuoliMatch,
						soggettiAutenticati, soggettiAutenticatiLabel, this.erogazioneSoggettoAutenticato,
						this.tipoProtocollo, listaTipiProtocollo,
						soggettiFruitoriList, soggettiFruitoriListLabel, this.providerSoggettoFruitore, this.tipoSoggettoFruitore, this.nomeSoggettoFruitore,
						this.fruizioneServizioApplicativo,this.fruizioneRuolo,this.fruizioneAutenticazione,this.fruizioneAutenticazioneOpzionale,this.fruizioneAutorizzazione,
						this.fruizioneAutorizzazioneAutenticati, this.fruizioneAutorizzazioneRuoli, this.fruizioneAutorizzazioneRuoliTipologia, this.fruizioneAutorizzazioneRuoliMatch,
						saFruitoriList,gestioneToken, policyLabels, policyValues, 
						gestioneTokenPolicy,  gestioneTokenOpzionale,
						gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward,
						autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
						autorizzazione_tokenOptions,
						autorizzazioneScope,scope,autorizzazioneScopeMatch,allegatoXacmlPolicy);

				if(!connettoreStatic) {
					boolean forceEnableConnettore = false;
					if( gestioneFruitori || generaPortaApplicativa ) {
						forceEnableConnettore = true;
					}
					
					dati = apsHelper.addEndPointToDati(dati, this.connettoreDebug, this.endpointtype, this.autenticazioneHttp, 
							(apsHelper.isModalitaCompleta() || !multitenant)?null:
								(generaPortaApplicativa?AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_INTERNO_PREFIX : AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_ESTERNO_PREFIX), 
							this.url, this.nome, this.tipo, this.user,
							this.password, this.initcont, this.urlpgk,
							this.provurl, this.connfact, this.sendas,
							AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,tipoOp, this.httpsurl, this.httpstipologia,
							this.httpshostverify, this.httpspath, this.httpstipo,
							this.httpspwd, this.httpsalgoritmo, this.httpsstato,
							this.httpskeystore, this.httpspwdprivatekeytrust,
							this.httpspathkey, this.httpstipokey,
							this.httpspwdkey, this.httpspwdprivatekey,
							this.httpsalgoritmokey, this.tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_ADD, null, null,
							null, null, null, null, null, null, true,
							isConnettoreCustomUltimaImmagineSalvata, 
							this.proxy_enabled, this.proxy_hostname, this.proxy_port, this.proxy_username, this.proxy_password,
							this.tempiRisposta_enabled, this.tempiRisposta_connectionTimeout, this.tempiRisposta_readTimeout, this.tempiRisposta_tempoMedioRisposta,
							this.opzioniAvanzate, this.transfer_mode, this.transfer_mode_chunk_size, this.redirect_mode, this.redirect_max_hop,
							this.requestOutputFileName,this.requestOutputFileNameHeaders,this.requestOutputParentDirCreateIfNotExists,this.requestOutputOverwriteIfExists,
							this.responseInputMode, this.responseInputFileName, this.responseInputFileNameHeaders, this.responseInputDeleteAfterRead, this.responseInputWaitTime,
							listExtendedConnettore, forceEnableConnettore);
				}

				// aggiunta campi custom
				dati = apsHelper.addProtocolPropertiesToDati(dati, this.consoleConfiguration,this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS, 
						ForwardParams.ADD());
			}

			// Inserisco il servizio nel db
			long idProv = Long.parseLong(this.provider);
			long idAcc = Long.parseLong(this.accordo);

			AccordoServizioParteSpecifica asps = new AccordoServizioParteSpecifica();
			asps.setNome(this.nomeservizio);
			asps.setTipo(this.tiposervizio);
			asps.setDescrizione(this.descrizione);
			asps.setIdAccordo(idAcc);
			// nome accordo
			as = apcCore.getAccordoServizio(idAcc);
			asps.setAccordoServizioParteComune(idAccordoFactory.getUriFromAccordo(as));
			asps.setIdSoggetto(idProv);
			asps.setNomeSoggettoErogatore(this.nomeSoggettoErogatore);
			asps.setTipoSoggettoErogatore(this.tipoSoggettoErogatore);
			asps.setTipologiaServizio(((this.servcorr != null) && this.servcorr.equals(Costanti.CHECK_BOX_ENABLED)) ? TipologiaServizio.CORRELATO : TipologiaServizio.NORMALE);
			asps.setSuperUser(ServletUtils.getUserLoginFromSession(session));
			if ("-".equals(this.profilo) == false)
				asps.setVersioneProtocollo(this.profilo);
			else
				asps.setVersioneProtocollo(null);

			asps.setPrivato(this.privato);

			String wsdlimplerS = this.wsdlimpler.getValue() != null ? new String(this.wsdlimpler.getValue()) : null; 
			asps.setByteWsdlImplementativoErogatore(((wsdlimplerS != null) && !wsdlimplerS.trim().replaceAll("\n", "").equals("")) ? wsdlimplerS.trim().getBytes() : null);
			String wsdlimplfruS = this.wsdlimplfru.getValue() != null ? new String(this.wsdlimplfru.getValue()) : null; 
			asps.setByteWsdlImplementativoFruitore(((wsdlimplfruS != null) && !wsdlimplfruS.trim().replaceAll("\n", "").equals("")) ? wsdlimplfruS.trim().getBytes() : null);
			
			if (this.portType != null && !"".equals(this.portType) && !"-".equals(this.portType))
				asps.setPortType(this.portType);

			// Connettore
			Connettore connettore = null;
			if(!connettoreStatic) {
				connettore = new Connettore();
				// this.nomeservizio);
				if (this.endpointtype.equals(ConnettoriCostanti.DEFAULT_CONNETTORE_TYPE_CUSTOM))
					connettore.setTipo(this.tipoconn);
				else
					connettore.setTipo(this.endpointtype);
	
				apsHelper.fillConnettore(connettore, this.connettoreDebug, this.endpointtype, this.endpointtype, this.tipoconn, this.url,
						this.nome, this.tipo, this.user, this.password,
						this.initcont, this.urlpgk, this.url, this.connfact,
						this.sendas, this.httpsurl, this.httpstipologia,
						this.httpshostverify, this.httpspath, this.httpstipo,
						this.httpspwd, this.httpsalgoritmo, this.httpsstato,
						this.httpskeystore, this.httpspwdprivatekeytrust,
						this.httpspathkey, this.httpstipokey,
						this.httpspwdkey, this.httpspwdprivatekey,
						this.httpsalgoritmokey,
						this.proxy_enabled, this.proxy_hostname, this.proxy_port, this.proxy_username, this.proxy_password,
						this.tempiRisposta_enabled, this.tempiRisposta_connectionTimeout, this.tempiRisposta_readTimeout, this.tempiRisposta_tempoMedioRisposta,
						this.opzioniAvanzate, this.transfer_mode, this.transfer_mode_chunk_size, this.redirect_mode, this.redirect_max_hop,
						this.requestOutputFileName,this.requestOutputFileNameHeaders,this.requestOutputParentDirCreateIfNotExists,this.requestOutputOverwriteIfExists,
						this.responseInputMode, this.responseInputFileName, this.responseInputFileNameHeaders, this.responseInputDeleteAfterRead, this.responseInputWaitTime,
						listExtendedConnettore);
			}

			if(asps.getConfigurazioneServizio()==null)
				asps.setConfigurazioneServizio(new ConfigurazioneServizio());
			if(apsHelper.isModalitaCompleta() || (!generaPortaApplicativa && !gestioneFruitori)) {
				asps.getConfigurazioneServizio().setConnettore(connettore);
			}

			// Versione
			if(apsCore.isSupportatoVersionamentoAccordiServizioParteSpecifica(this.tipoProtocollo)){
				if(this.versione!=null && !"".equals(this.versione))
					asps.setVersione(Integer.parseInt(this.versione));
				else
					asps.setVersione(1);
			}else{
				asps.setVersione(1);
			}

			// stato
			asps.setStatoPackage(this.statoPackage);

			if(gestioneFruitori) {
				Fruitore fruitore = new Fruitore();
				fruitore.setTipo(this.tipoSoggettoFruitore);
				fruitore.setNome(this.nomeSoggettoFruitore);
				fruitore.setStatoPackage(this.statoPackage);
				fruitore.setConnettore(connettore);
				asps.addFruitore(fruitore);
			}
			
			//			Spostato sopra a livello di edit in progress			
			//			// Se l'accordo Comune che si riferisce ha stato operativo o finale modifico lo stato in operativo
			//			if(as.getStatoPackage().equals(StatiAccordo.operativo.toString()) || as.getStatoPackage().equals(StatiAccordo.finale.toString())){
			//				asps.setStatoPackage(StatiAccordo.operativo.toString()); 
			//			}

			// Check stato
			if(apsCore.isShowGestioneWorkflowStatoDocumenti()){

				ValidazioneStatoPackageException validazione = null;
				try{
					boolean gestioneWsdlImplementativo = apcCore.showPortiAccesso(this.tipoProtocollo, this.serviceBinding, this.formatoSpecifica);
					boolean checkConnettore = !gestioneFruitori && !gestioneErogatori;
					apsCore.validaStatoAccordoServizioParteSpecifica(asps, gestioneWsdlImplementativo, checkConnettore);
				}catch(ValidazioneStatoPackageException validazioneException){
					validazione = validazioneException;
				}
				if(validazione==null && gestioneFruitori) {
					try{
						apsCore.validaStatoFruitoreAccordoServizioParteSpecifica(asps.getFruitore(0), asps);
					}catch(ValidazioneStatoPackageException validazioneException){
					}
				}
				if(validazione!=null) {

					// Setto messaggio di errore
					pd.setMessage(validazione.toString());

					// setto la barra del titolo
					ServletUtils.setPageDataTitle_ServletAdd(pd, labelList,servletList);

					// preparo i campi
					Vector<DataElement> dati = new Vector<DataElement>();

					// update della configurazione 
					this.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioParteSpecifica(this.consoleConfiguration, this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties, 
							this.registryReader, this.configRegistryReader, idAps);

					dati.addElement(ServletUtils.getDataElementForEditModeFinished());
					
					dati = apsHelper.addServiziToDati(dati, this.nomeservizio, this.tiposervizio, null, null,  
							this.provider, null, null, 
							soggettiList, soggettiListLabel, this.accordo, this.serviceBinding, this.formatoSpecifica, accordiList, accordiListLabel, this.servcorr, 
							this.wsdlimpler, this.wsdlimplfru, tipoOp, "0", tipiServizioCompatibiliAccordo, 
							this.profilo, this.portType, ptList, this.privato,uriAccordo,this.descrizione,-1l,this.statoPackage,
							this.statoPackage,this.versione,versioniProtocollo,this.validazioneDocumenti,
							saSoggetti,this.nomeSA,generaPortaApplicativa,null,
							this.erogazioneRuolo,this.erogazioneAutenticazione,this.erogazioneAutenticazioneOpzionale,this.erogazioneAutorizzazione,erogazioneIsSupportatoAutenticazioneSoggetti,
							this.erogazioneAutorizzazioneAutenticati, this.erogazioneAutorizzazioneRuoli, this.erogazioneAutorizzazioneRuoliTipologia, this.erogazioneAutorizzazioneRuoliMatch,
							soggettiAutenticati, soggettiAutenticatiLabel, this.erogazioneSoggettoAutenticato,
							this.tipoProtocollo, listaTipiProtocollo,
							soggettiFruitoriList, soggettiFruitoriListLabel, this.providerSoggettoFruitore, this.tipoSoggettoFruitore, this.nomeSoggettoFruitore,
							this.fruizioneServizioApplicativo,this.fruizioneRuolo,this.fruizioneAutenticazione,this.fruizioneAutenticazioneOpzionale,this.fruizioneAutorizzazione,
							this.fruizioneAutorizzazioneAutenticati, this.fruizioneAutorizzazioneRuoli, this.fruizioneAutorizzazioneRuoliTipologia, this.fruizioneAutorizzazioneRuoliMatch,
							saFruitoriList,gestioneToken, policyLabels, policyValues, 
							gestioneTokenPolicy,  gestioneTokenOpzionale,
							gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward,
							autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
							autorizzazione_tokenOptions,
							autorizzazioneScope,scope,autorizzazioneScopeMatch,allegatoXacmlPolicy);

					if(!connettoreStatic) {
					
						boolean forceEnableConnettore = false;
						if( gestioneFruitori || generaPortaApplicativa ) {
							forceEnableConnettore = true;
						}
						
						dati = apsHelper.addEndPointToDati(dati, this.connettoreDebug, this.endpointtype, this.autenticazioneHttp, 
								(apsHelper.isModalitaCompleta() || !multitenant)?null:
									(generaPortaApplicativa?AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_INTERNO_PREFIX : AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_ESTERNO_PREFIX), 
								this.url, this.nome, this.tipo, this.user,
								this.password, this.initcont, this.urlpgk,
								this.provurl, this.connfact, this.sendas,
								AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,tipoOp, this.httpsurl, this.httpstipologia,
								this.httpshostverify, this.httpspath, this.httpstipo,
								this.httpspwd, this.httpsalgoritmo, this.httpsstato,
								this.httpskeystore, this.httpspwdprivatekeytrust,
								this.httpspathkey, this.httpstipokey,
								this.httpspwdkey, this.httpspwdprivatekey,
								this.httpsalgoritmokey, this.tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_ADD, null, null,
								null, null, null, null, null, null, true,
								isConnettoreCustomUltimaImmagineSalvata, 
								this.proxy_enabled, this.proxy_hostname, this.proxy_port, this.proxy_username, this.proxy_password,
								this.tempiRisposta_enabled, this.tempiRisposta_connectionTimeout, this.tempiRisposta_readTimeout, this.tempiRisposta_tempoMedioRisposta,
								this.opzioniAvanzate, this.transfer_mode, this.transfer_mode_chunk_size, this.redirect_mode, this.redirect_max_hop,
								this.requestOutputFileName,this.requestOutputFileNameHeaders,this.requestOutputParentDirCreateIfNotExists,this.requestOutputOverwriteIfExists,
								this.responseInputMode, this.responseInputFileName, this.responseInputFileNameHeaders, this.responseInputDeleteAfterRead, this.responseInputWaitTime,
								listExtendedConnettore, forceEnableConnettore);
						
					}

					// aggiunta campi custom
					dati = apsHelper.addProtocolPropertiesToDati(dati, this.consoleConfiguration,this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties);

					pd.setDati(dati);


					ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

					return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS, 
							ForwardParams.ADD());

				}
			}

			List<Object> listaOggettiDaCreare = new ArrayList<Object>();
			listaOggettiDaCreare.add(asps);


			// Creo Porta Applicativa (opzione??)
			if(generaPortaApplicativa){

				IDSoggetto soggettoErogatore = new IDSoggetto(this.tipoSoggettoErogatore,this.nomeSoggettoErogatore);
				IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(this.tiposervizio, this.nomeservizio, soggettoErogatore, 
						(this.versione==null || "".equals(this.versione))? 1 : Integer.parseInt(this.versione));
				Implementation implementationDefault = this.protocolFactory.createProtocolIntegrationConfiguration().
						createDefaultImplementation(this.serviceBinding, idServizio);
				
				PortaApplicativa portaApplicativa = implementationDefault.getPortaApplicativa();
				MappingErogazionePortaApplicativa mappingErogazione = implementationDefault.getMapping();
				portaApplicativa.setIdSoggetto((long) idProv);
				
				IDSoggetto idSoggettoAutenticatoErogazione = null;
				if(this.erogazioneSoggettoAutenticato != null && !"".equals(this.erogazioneSoggettoAutenticato) && !"-".equals(this.erogazioneSoggettoAutenticato)) {
					String [] splitSoggetto = this.erogazioneSoggettoAutenticato.split("/");
					if(splitSoggetto != null) {
						idSoggettoAutenticatoErogazione = new IDSoggetto();
						if(splitSoggetto.length == 2) {
							idSoggettoAutenticatoErogazione.setTipo(splitSoggetto[0]);
							idSoggettoAutenticatoErogazione.setNome(splitSoggetto[1]);
						} else {
							idSoggettoAutenticatoErogazione.setNome(splitSoggetto[0]);
						}
					}
				}
				
				String nomeServizioApplicativoErogatore = this.nomeSA;
				ServizioApplicativo sa = null;
				
				if(apsHelper.isModalitaCompleta()==false) {
					// Creo il servizio applicativo
					
					nomeServizioApplicativoErogatore = portaApplicativa.getNome();
					
					sa = new ServizioApplicativo();
					sa.setNome(nomeServizioApplicativoErogatore);
					sa.setTipologiaFruizione(TipologiaFruizione.DISABILITATO.getValue());
					sa.setTipologiaErogazione(TipologiaErogazione.TRASPARENTE.getValue());
					sa.setIdSoggetto((long) idProv);
					sa.setTipoSoggettoProprietario(portaApplicativa.getTipoSoggettoProprietario());
					sa.setNomeSoggettoProprietario(portaApplicativa.getNomeSoggettoProprietario());
					
					RispostaAsincrona rispostaAsinc = new RispostaAsincrona();
					rispostaAsinc.setAutenticazione(InvocazioneServizioTipoAutenticazione.NONE);
					rispostaAsinc.setGetMessage(CostantiConfigurazione.DISABILITATO);
					sa.setRispostaAsincrona(rispostaAsinc);
					
					InvocazioneServizio invServizio = new InvocazioneServizio();
					invServizio.setAutenticazione(InvocazioneServizioTipoAutenticazione.NONE);
					invServizio.setGetMessage(CostantiConfigurazione.DISABILITATO);
					invServizio.setConnettore(connettore.mappingIntoConnettoreConfigurazione());
					sa.setInvocazioneServizio(invServizio);
					
					listaOggettiDaCreare.add(sa);
				}
				
				porteApplicativeCore.configureControlloAccessiPortaApplicativa(portaApplicativa,
						this.erogazioneAutenticazione, this.erogazioneAutenticazioneOpzionale,
						this.erogazioneAutorizzazione, this.erogazioneAutorizzazioneAutenticati, this.erogazioneAutorizzazioneRuoli, this.erogazioneAutorizzazioneRuoliTipologia, this.erogazioneAutorizzazioneRuoliMatch,
						nomeServizioApplicativoErogatore, this.erogazioneRuolo,idSoggettoAutenticatoErogazione,
						autorizzazione_tokenOptions,
						autorizzazioneScope,scope,autorizzazioneScopeMatch,allegatoXacmlPolicy);
				
				porteApplicativeCore.configureControlloAccessiGestioneToken(portaApplicativa, gestioneToken, 
						gestioneTokenPolicy,  gestioneTokenOpzionale,
						gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward,
						autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
						autorizzazione_tokenOptions
						);
				
				listaOggettiDaCreare.add(portaApplicativa);						
				listaOggettiDaCreare.add(mappingErogazione);
			}
			
			if(generaPortaDelegata){
				
				IDSoggetto idFruitore = new IDSoggetto(this.tipoSoggettoFruitore, this.nomeSoggettoFruitore);
				IDSoggetto soggettoErogatore = new IDSoggetto(this.tipoSoggettoErogatore,this.nomeSoggettoErogatore);
				IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(this.tiposervizio, this.nomeservizio, soggettoErogatore, 
						(this.versione==null || "".equals(this.versione))? 1 : Integer.parseInt(this.versione));
				ServiceBinding serviceBinding = org.openspcoop2.core.registry.constants.ServiceBinding.REST.equals(as.getServiceBinding()) ?
						ServiceBinding.REST : ServiceBinding.SOAP;
				Subscription subscriptionDefault = this.protocolFactory.createProtocolIntegrationConfiguration().
						createDefaultSubscription(serviceBinding, idFruitore, idServizio);
				
				PortaDelegata portaDelegata = subscriptionDefault.getPortaDelegata();
				MappingFruizionePortaDelegata mappingFruizione = subscriptionDefault.getMapping();
				portaDelegata.setIdSoggetto((long) idProv);

				porteDelegateCore.configureControlloAccessiPortaDelegata(portaDelegata, 
						this.fruizioneAutenticazione, this.fruizioneAutenticazioneOpzionale,
						this.fruizioneAutorizzazione, this.fruizioneAutorizzazioneAutenticati, this.fruizioneAutorizzazioneRuoli, this.fruizioneAutorizzazioneRuoliTipologia, this.fruizioneAutorizzazioneRuoliMatch,
						this.fruizioneServizioApplicativo, this.fruizioneRuolo,
						autorizzazione_tokenOptions,
						autorizzazioneScope,scope,autorizzazioneScopeMatch,allegatoXacmlPolicy);
				
				porteDelegateCore.configureControlloAccessiGestioneToken(portaDelegata, gestioneToken, 
						gestioneTokenPolicy,  gestioneTokenOpzionale,
						gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward,
						autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
						autorizzazione_tokenOptions
						);
							
				// Verifico prima che la porta delegata non esista già
				if (!porteDelegateCore.existsPortaDelegata(mappingFruizione.getIdPortaDelegata())){
					listaOggettiDaCreare.add(portaDelegata);
				}
				listaOggettiDaCreare.add(mappingFruizione);
			}

			//imposto properties custom
			asps.setProtocolPropertyList(ProtocolPropertiesUtils.toProtocolProperties(this.protocolProperties, this.consoleOperationType,null));

			apsCore.performCreateOperation(asps.getSuperUser(), apsHelper.smista(), listaOggettiDaCreare.toArray());

			// cancello i file temporanei
			apsHelper.deleteBinaryParameters(this.wsdlimpler,this.wsdlimplfru);
			apsHelper.deleteBinaryProtocolPropertiesTmpFiles(this.protocolProperties);
			
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
			List<AccordoServizioParteSpecifica> listaAccordi = null;
			if(apsCore.isVisioneOggettiGlobale(userLogin)){
				listaAccordi = apsCore.soggettiServizioList(null, ricerca,permessi, gestioneFruitori);
			}else{
				listaAccordi = apsCore.soggettiServizioList(userLogin, ricerca, permessi, gestioneFruitori);
			}
			
			if(vistaErogazioni != null && vistaErogazioni.booleanValue()) {
				apsHelper.prepareErogazioniList(ricerca, listaAccordi);
				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				return ServletUtils.getStrutsForwardEditModeFinished(mapping, ErogazioniCostanti.OBJECT_NAME_ASPS_EROGAZIONI, ForwardParams.ADD());
			}

			apsHelper.prepareServiziList(ricerca, listaAccordi);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS, ForwardParams.ADD());



		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,
					ForwardParams.ADD());
		}  
	}
	
}
