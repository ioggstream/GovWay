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


package org.openspcoop2.web.ctrlstat.servlet.config;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.config.AccessoConfigurazione;
import org.openspcoop2.core.config.AccessoDatiAutenticazione;
import org.openspcoop2.core.config.AccessoDatiAutorizzazione;
import org.openspcoop2.core.config.AccessoDatiGestioneToken;
import org.openspcoop2.core.config.Attachments;
import org.openspcoop2.core.config.Cache;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.ConfigurazioneMultitenant;
import org.openspcoop2.core.config.ConfigurazioneProtocolli;
import org.openspcoop2.core.config.ConfigurazioneProtocollo;
import org.openspcoop2.core.config.Dump;
import org.openspcoop2.core.config.IndirizzoRisposta;
import org.openspcoop2.core.config.InoltroBusteNonRiscontrate;
import org.openspcoop2.core.config.IntegrationManager;
import org.openspcoop2.core.config.MessaggiDiagnostici;
import org.openspcoop2.core.config.Risposte;
import org.openspcoop2.core.config.Tracciamento;
import org.openspcoop2.core.config.ValidazioneBuste;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativi;
import org.openspcoop2.core.config.constants.AlgoritmoCache;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.PortaApplicativaSoggettiFruitori;
import org.openspcoop2.core.config.constants.PortaDelegataSoggettiErogatori;
import org.openspcoop2.core.config.constants.Severita;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.config.constants.TipoConnessioneRisposte;
import org.openspcoop2.core.config.constants.ValidazioneBusteTipoControllo;
import org.openspcoop2.core.config.constants.ValidazioneContenutiApplicativiTipo;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.utils.resources.MapReader;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.DBManager;
import org.openspcoop2.web.ctrlstat.costanti.MultitenantSoggettiErogazioni;
import org.openspcoop2.web.ctrlstat.costanti.MultitenantSoggettiFruizioni;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedBean;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedFormServlet;
import org.openspcoop2.web.ctrlstat.plugins.WrapperExtendedBean;
import org.openspcoop2.web.ctrlstat.plugins.servlet.AbstractServletNewWindowChangeExtended;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * configurazione
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ConfigurazioneGenerale extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = ServletUtils.getUserLoginFromSession(session);	

		try {


			ConfigurazioneHelper confHelper = new ConfigurazioneHelper(request, pd, session);
			ConfigurazioneCore confCore = new ConfigurazioneCore();

			List<IExtendedFormServlet> extendedServletList = confCore.getExtendedServletConfigurazione();
			
			String inoltromin = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_INOLTRO_MIN);
			String stato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO);
			String controllo = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO);
			String severita = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA);
			String severita_log4j = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA_LOG4J);
			String integman = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_INTEGMAN);
			String nomeintegman = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_NOME_INTEGMAN);
			String profcoll = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROFILO_COLLABORAZIONE);
			String connessione = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONNESSIONE);
			String utilizzo = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_UTILIZZO);
			String validman = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_VALIDMAN);
			String gestman = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_GESTMAN);
			String registrazioneTracce = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_TRACCE);
			String dumpPD = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DUMP_CONNETTORE_PD);
			String dumpPA = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DUMP_CONNETTORE_PA);
			String xsd = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_XSD);
			String tipoValidazione = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO_VALIDAZIONE);
			String applicaMTOM = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_APPLICA_MTOM);

			String statocache_config = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_CONFIG);
			String dimensionecache_config = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_CONFIG);
			String algoritmocache_config = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_CONFIG);
			String idlecache_config = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_CONFIG);
			String lifecache_config = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_CONFIG);
			
			String statocache_authz = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_AUTHZ);
			String dimensionecache_authz = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_AUTHZ);
			String algoritmocache_authz = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_AUTHZ);
			String idlecache_authz = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_AUTHZ);
			String lifecache_authz = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_AUTHZ);
			
			String statocache_authn = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_AUTHN);
			String dimensionecache_authn = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_AUTHN);
			String algoritmocache_authn = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_AUTHN);
			String idlecache_authn = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_AUTHN);
			String lifecache_authn = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_AUTHN);
			
			String statocache_token = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_TOKEN);
			String dimensionecache_token = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_TOKEN);
			String algoritmocache_token = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_TOKEN);
			String idlecache_token = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_TOKEN);
			String lifecache_token = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_TOKEN);
			
			String multitenantStatoTmp = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_MULTITENANT_STATO);
			boolean multitenantEnabled = CostantiConfigurazione.ABILITATO.getValue().equals(multitenantStatoTmp);
			String multitenantSoggettiFruizioni = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_MULTITENANT_FRUIZIONI_SOGGETTO_EROGATORE);
			String multitenantSoggettiErogazioni = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_MULTITENANT_EROGAZIONI_SOGGETTI_FRUITORI);
			
			Boolean confPersB = ServletUtils.getConfigurazioniPersonalizzateFromSession(session); 
			String confPers = confPersB ? "true" : "false";
			Configurazione configurazione = confCore.getConfigurazioneGenerale();

			List<ExtendedInfo> extendedBeanList = new ArrayList<ExtendedInfo>();
			if(extendedServletList!=null && extendedServletList.size()>0){
				for (IExtendedFormServlet extendedServlet : extendedServletList) {
					
					ExtendedInfo ei = new ExtendedInfo();
					ei.extendedServlet = extendedServlet;
					DBManager dbManager = null;
					Connection con = null;
					try{
						dbManager = DBManager.getInstance();
						con = dbManager.getConnection();
						ei.extendedBean = extendedServlet.getExtendedBean(con, "SingleInstance");
					}finally{
						dbManager.releaseConnection(con);
					}
					
					ei.extended = false;
					ei.extendedToNewWindow = false;
					if(extendedServlet!=null && extendedServlet.showExtendedInfoUpdate(request, session)){
						ei.extendedBean = extendedServlet.readHttpParameters(configurazione, TipoOperazione.CHANGE,  ei.extendedBean, request);
						ei.extended = true;
						ei.extendedToNewWindow = extendedServlet.extendedUpdateToNewWindow(request, session);
					}
					
					extendedBeanList.add(ei);
					
				}
			}
			
			
			
			// Preparo il menu
			confHelper.makeMenu();

			// setto la barra del titolo
			ServletUtils.setPageDataTitle_ServletFirst(pd, ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_GENERALE, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE);

			// Se idhid != null, modifico i dati della porta di dominio nel
			// db
			if (!confHelper.isEditModeInProgress()) {
				
				// Controlli sui campi immessi
				boolean isOk = confHelper.configurazioneCheckData();
				if (isOk) {
					if(extendedBeanList!=null && extendedBeanList.size()>0){
						for (ExtendedInfo ei : extendedBeanList) {
							if(ei.extended && !ei.extendedToNewWindow){
								try{
									ei.extendedServlet.checkDati(TipoOperazione.CHANGE, confHelper, confCore, configurazione, ei.extendedBean);
								}catch(Exception e){
									isOk = false;
									pd.setMessage(e.getMessage());
								}
							}
						}
					}
				}
				if (!isOk) {
					// preparo i campi
					Vector<DataElement> dati = new Vector<DataElement>();

					dati.addElement(ServletUtils.getDataElementForEditModeFinished());

					dati = confHelper.addConfigurazioneToDati(  inoltromin, stato, controllo, severita, severita_log4j, integman, nomeintegman, profcoll, 
							connessione, utilizzo, validman, gestman, registrazioneTracce, dumpPD, dumpPA, 
							xsd, tipoValidazione, confPers, configurazione, dati, applicaMTOM,
							configurazione.getProtocolli(),
							multitenantEnabled, multitenantSoggettiFruizioni, multitenantSoggettiErogazioni,
							true);

					confHelper.setDataElementCache(dati,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_CONFIG,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_CONFIG,statocache_config,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_CONFIG,dimensionecache_config,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_CONFIG,algoritmocache_config,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_CONFIG,idlecache_config,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_CONFIG,lifecache_config);
					
					confHelper.setDataElementCache(dati,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_AUTHZ,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_AUTHZ,statocache_authz,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_AUTHZ,dimensionecache_authz,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_AUTHZ,algoritmocache_authz,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_AUTHZ,idlecache_authz,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_AUTHZ,lifecache_authz);
					
					confHelper.setDataElementCache(dati,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_AUTHN,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_AUTHN,statocache_authn,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_AUTHN,dimensionecache_authn,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_AUTHN,algoritmocache_authn,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_AUTHN,idlecache_authn,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_AUTHN,lifecache_authn);
					
					confHelper.setDataElementCache(dati,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_TOKEN,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_TOKEN,statocache_token,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_TOKEN,dimensionecache_token,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_TOKEN,algoritmocache_token,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_TOKEN,idlecache_token,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_TOKEN,lifecache_token);
					
					if(extendedBeanList!=null && extendedBeanList.size()>0){
						for (ExtendedInfo ei : extendedBeanList) {
							if(ei.extended){
								if(ei.extendedToNewWindow){
									AbstractServletNewWindowChangeExtended.addToDatiNewWindow(ei.extendedServlet, 
											dati, confHelper, confCore, configurazione, ei.extendedBean, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE_EXTENDED);
								}else{
									ei.extendedServlet.addToDati(dati, TipoOperazione.CHANGE, confHelper, confCore, configurazione, ei.extendedBean);
								}
							}
						}
					}
					
					pd.setDati(dati);

					ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

					return ServletUtils.getStrutsForwardEditModeCheckError(mapping, 
							ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_GENERALE,
							ConfigurazioneCostanti.TIPO_OPERAZIONE_CONFIGURAZIONE_GENERALE);
				}

				// Modifico i dati della porta di dominio nel db
				Configurazione newConfigurazione = confCore.getConfigurazioneGenerale();

				if (newConfigurazione.getInoltroBusteNonRiscontrate() != null) {
					newConfigurazione.getInoltroBusteNonRiscontrate().setCadenza(inoltromin);
				} else {
					InoltroBusteNonRiscontrate ibnr = new InoltroBusteNonRiscontrate();
					ibnr.setCadenza(inoltromin);
					newConfigurazione.setInoltroBusteNonRiscontrate(ibnr);
				}

				if (newConfigurazione.getValidazioneBuste() != null) {
					newConfigurazione.getValidazioneBuste().setStato(StatoFunzionalitaConWarning.toEnumConstant(stato));
					newConfigurazione.getValidazioneBuste().setControllo(ValidazioneBusteTipoControllo.toEnumConstant(controllo));
					newConfigurazione.getValidazioneBuste().setProfiloCollaborazione(StatoFunzionalita.toEnumConstant(profcoll));
					newConfigurazione.getValidazioneBuste().setManifestAttachments(StatoFunzionalita.toEnumConstant(validman));
				} else {
					ValidazioneBuste vbe = new ValidazioneBuste();
					vbe.setStato(StatoFunzionalitaConWarning.toEnumConstant(stato));
					vbe.setControllo(ValidazioneBusteTipoControllo.toEnumConstant(controllo));
					vbe.setProfiloCollaborazione(StatoFunzionalita.toEnumConstant(profcoll));
					vbe.setManifestAttachments(StatoFunzionalita.toEnumConstant(validman));
					newConfigurazione.setValidazioneBuste(vbe);
				}

				if (newConfigurazione.getMessaggiDiagnostici() != null) {
					newConfigurazione.getMessaggiDiagnostici().setSeverita(Severita.toEnumConstant(severita));
					newConfigurazione.getMessaggiDiagnostici().setSeveritaLog4j(Severita.toEnumConstant(severita_log4j));
				} else {
					MessaggiDiagnostici md = new MessaggiDiagnostici();
					md.setSeverita(Severita.toEnumConstant(severita));
					md.setSeveritaLog4j(Severita.toEnumConstant(severita_log4j));
					newConfigurazione.setMessaggiDiagnostici(md);
				}

				if (newConfigurazione.getIntegrationManager() != null) {
					if (integman == null || !integman.equals(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_IM_CUSTOM))
						newConfigurazione.getIntegrationManager().setAutenticazione(integman);
					else
						newConfigurazione.getIntegrationManager().setAutenticazione(nomeintegman);
				} else {
					IntegrationManager im = new IntegrationManager();
					if (integman == null || !integman.equals(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_IM_CUSTOM))
						im.setAutenticazione(integman);
					else
						im.setAutenticazione(nomeintegman);
					newConfigurazione.setIntegrationManager(im);
				}

				if (newConfigurazione.getRisposte() != null) {
					newConfigurazione.getRisposte().setConnessione(TipoConnessioneRisposte.toEnumConstant(connessione));
				} else {
					Risposte rs = new Risposte();
					rs.setConnessione(TipoConnessioneRisposte.toEnumConstant(connessione));
					newConfigurazione.setRisposte(rs);
				}

				if (newConfigurazione.getIndirizzoRisposta() != null) {
					newConfigurazione.getIndirizzoRisposta().setUtilizzo(StatoFunzionalita.toEnumConstant(utilizzo));
				} else {
					IndirizzoRisposta it = new IndirizzoRisposta();
					it.setUtilizzo(StatoFunzionalita.toEnumConstant(utilizzo));
					newConfigurazione.setIndirizzoRisposta(it);
				}

				if (newConfigurazione.getAttachments() != null) {
					newConfigurazione.getAttachments().setGestioneManifest(StatoFunzionalita.toEnumConstant(gestman));
				} else {
					Attachments a = new Attachments();
					a.setGestioneManifest(StatoFunzionalita.toEnumConstant(gestman));
					newConfigurazione.setAttachments(a);
				}

				if (newConfigurazione.getTracciamento() != null) {
					newConfigurazione.getTracciamento().setStato(StatoFunzionalita.toEnumConstant(registrazioneTracce));
				} else {
					Tracciamento t = new Tracciamento();
					t.setStato(StatoFunzionalita.toEnumConstant(registrazioneTracce));
					newConfigurazione.setTracciamento(t);
				}
				
				if (newConfigurazione.getDump() != null) {
					newConfigurazione.getDump().setDumpBinarioPortaDelegata(StatoFunzionalita.toEnumConstant(dumpPD));
					newConfigurazione.getDump().setDumpBinarioPortaApplicativa(StatoFunzionalita.toEnumConstant(dumpPA));
				} else {
					Dump d = new Dump();
					d.setStato(StatoFunzionalita.DISABILITATO);
					d.setDumpBinarioPortaDelegata(StatoFunzionalita.toEnumConstant(dumpPD));
					d.setDumpBinarioPortaApplicativa(StatoFunzionalita.toEnumConstant(dumpPA));
					newConfigurazione.setDump(d);
				}

				if (newConfigurazione.getValidazioneContenutiApplicativi() != null) {
					newConfigurazione.getValidazioneContenutiApplicativi().setStato(StatoFunzionalitaConWarning.toEnumConstant(xsd));
					newConfigurazione.getValidazioneContenutiApplicativi().setTipo(ValidazioneContenutiApplicativiTipo.toEnumConstant(tipoValidazione));
					if(applicaMTOM != null){
						if(ServletUtils.isCheckBoxEnabled(applicaMTOM))
							newConfigurazione.getValidazioneContenutiApplicativi().setAcceptMtomMessage(StatoFunzionalita.ABILITATO);
						else 
							newConfigurazione.getValidazioneContenutiApplicativi().setAcceptMtomMessage(StatoFunzionalita.DISABILITATO);
					} else 
						newConfigurazione.getValidazioneContenutiApplicativi().setAcceptMtomMessage(null);
				} else {
					ValidazioneContenutiApplicativi vca = new ValidazioneContenutiApplicativi();
					vca.setStato(StatoFunzionalitaConWarning.toEnumConstant(xsd));
					vca.setTipo(ValidazioneContenutiApplicativiTipo.toEnumConstant(tipoValidazione));
					vca.setAcceptMtomMessage(null);
					newConfigurazione.setValidazioneContenutiApplicativi(vca);
				}
				
				if(newConfigurazione.getAccessoConfigurazione()==null){
					newConfigurazione.setAccessoConfigurazione(new AccessoConfigurazione());
				}
				if(statocache_config.equals(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO)){
					newConfigurazione.getAccessoConfigurazione().setCache(new Cache());
					newConfigurazione.getAccessoConfigurazione().getCache().setDimensione(dimensionecache_config);
					newConfigurazione.getAccessoConfigurazione().getCache().setAlgoritmo(AlgoritmoCache.toEnumConstant(algoritmocache_config));
					newConfigurazione.getAccessoConfigurazione().getCache().setItemIdleTime(idlecache_config);
					newConfigurazione.getAccessoConfigurazione().getCache().setItemLifeSecond(lifecache_config);
				}
				else{
					newConfigurazione.getAccessoConfigurazione().setCache(null);
				}
				
				if(newConfigurazione.getAccessoDatiAutorizzazione()==null){
					newConfigurazione.setAccessoDatiAutorizzazione(new AccessoDatiAutorizzazione());
				}
				if(statocache_authz.equals(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO)){
					newConfigurazione.getAccessoDatiAutorizzazione().setCache(new Cache());
					newConfigurazione.getAccessoDatiAutorizzazione().getCache().setDimensione(dimensionecache_authz);
					newConfigurazione.getAccessoDatiAutorizzazione().getCache().setAlgoritmo(AlgoritmoCache.toEnumConstant(algoritmocache_authz));
					newConfigurazione.getAccessoDatiAutorizzazione().getCache().setItemIdleTime(idlecache_authz);
					newConfigurazione.getAccessoDatiAutorizzazione().getCache().setItemLifeSecond(lifecache_authz);
				}
				else{
					newConfigurazione.getAccessoDatiAutorizzazione().setCache(null);
				}
				
				if(newConfigurazione.getAccessoDatiAutenticazione()==null){
					newConfigurazione.setAccessoDatiAutenticazione(new AccessoDatiAutenticazione());
				}
				if(statocache_authn.equals(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO)){
					newConfigurazione.getAccessoDatiAutenticazione().setCache(new Cache());
					newConfigurazione.getAccessoDatiAutenticazione().getCache().setDimensione(dimensionecache_authn);
					newConfigurazione.getAccessoDatiAutenticazione().getCache().setAlgoritmo(AlgoritmoCache.toEnumConstant(algoritmocache_authn));
					newConfigurazione.getAccessoDatiAutenticazione().getCache().setItemIdleTime(idlecache_authn);
					newConfigurazione.getAccessoDatiAutenticazione().getCache().setItemLifeSecond(lifecache_authn);
				}
				else{
					newConfigurazione.getAccessoDatiAutenticazione().setCache(null);
				}
				
				if(newConfigurazione.getAccessoDatiGestioneToken()==null){
					newConfigurazione.setAccessoDatiGestioneToken(new AccessoDatiGestioneToken());
				}
				if(statocache_token.equals(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO)){
					newConfigurazione.getAccessoDatiGestioneToken().setCache(new Cache());
					newConfigurazione.getAccessoDatiGestioneToken().getCache().setDimensione(dimensionecache_token);
					newConfigurazione.getAccessoDatiGestioneToken().getCache().setAlgoritmo(AlgoritmoCache.toEnumConstant(algoritmocache_token));
					newConfigurazione.getAccessoDatiGestioneToken().getCache().setItemIdleTime(idlecache_token);
					newConfigurazione.getAccessoDatiGestioneToken().getCache().setItemLifeSecond(lifecache_token);
				}
				else{
					newConfigurazione.getAccessoDatiGestioneToken().setCache(null);
				}
				
				newConfigurazione.setMultitenant(new ConfigurazioneMultitenant());
				newConfigurazione.getMultitenant().setStato(multitenantEnabled ? StatoFunzionalita.ABILITATO : StatoFunzionalita.DISABILITATO);
				
				MultitenantSoggettiFruizioni multitenantSoggettiFruizioniEnum = null;
				if(multitenantSoggettiFruizioni!=null && !"".equals(multitenantSoggettiFruizioni)) {
					multitenantSoggettiFruizioniEnum = MultitenantSoggettiFruizioni.valueOf(multitenantSoggettiFruizioni);
				}
				if(multitenantSoggettiFruizioniEnum!=null) {
					switch (multitenantSoggettiFruizioniEnum) {
					case SOLO_SOGGETTI_ESTERNI:
						newConfigurazione.getMultitenant().setFruizioneSceltaSoggettiErogatori(PortaDelegataSoggettiErogatori.SOGGETTI_ESTERNI);
						break;
					case ESCLUDI_SOGGETTO_FRUITORE:
						newConfigurazione.getMultitenant().setFruizioneSceltaSoggettiErogatori(PortaDelegataSoggettiErogatori.ESCLUDI_SOGGETTO_FRUITORE);
						break;
					case TUTTI:
						newConfigurazione.getMultitenant().setFruizioneSceltaSoggettiErogatori(PortaDelegataSoggettiErogatori.TUTTI);
						break;
					}
				}
				else {
					newConfigurazione.getMultitenant().setFruizioneSceltaSoggettiErogatori(PortaDelegataSoggettiErogatori.SOGGETTI_ESTERNI); // default
				}
				

				MultitenantSoggettiErogazioni multitenantSoggettiErogazioniEnum = null;
				if(multitenantSoggettiErogazioni!=null && !"".equals(multitenantSoggettiErogazioni)) {
					multitenantSoggettiErogazioniEnum = MultitenantSoggettiErogazioni.valueOf(multitenantSoggettiErogazioni);
				}
				if(multitenantSoggettiErogazioniEnum!=null) {
					switch (multitenantSoggettiErogazioniEnum) {
					case SOLO_SOGGETTI_ESTERNI:
						newConfigurazione.getMultitenant().setErogazioneSceltaSoggettiFruitori(PortaApplicativaSoggettiFruitori.SOGGETTI_ESTERNI);
						break;
					case ESCLUDI_SOGGETTO_EROGATORE:
						newConfigurazione.getMultitenant().setErogazioneSceltaSoggettiFruitori(PortaApplicativaSoggettiFruitori.ESCLUDI_SOGGETTO_EROGATORE);
						break;
					case TUTTI:
						newConfigurazione.getMultitenant().setErogazioneSceltaSoggettiFruitori(PortaApplicativaSoggettiFruitori.TUTTI);
						break;
					}
				}
				else {
					newConfigurazione.getMultitenant().setErogazioneSceltaSoggettiFruitori(PortaApplicativaSoggettiFruitori.SOGGETTI_ESTERNI); // default
				}
				
				newConfigurazione.setProtocolli(null); // aggiorno i protocolli
				
				ProtocolFactoryManager pManager = ProtocolFactoryManager.getInstance();
				MapReader<String, IProtocolFactory<?>> mapPFactory = pManager.getProtocolFactories();
				Enumeration<String> protocolName = mapPFactory.keys();
				while (protocolName.hasMoreElements()) {
					String protocollo = (String) protocolName.nextElement();
										
					String nameP = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROTOCOLLO_PREFIX_NAME+protocollo);
					String urlInvocazionePD = null;
					String urlInvocazionePA = null;
					if(nameP!=null) {
						urlInvocazionePD = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROTOCOLLO_PREFIX_URL_INVOCAZIONE_PD+protocollo);
						urlInvocazionePA = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROTOCOLLO_PREFIX_URL_INVOCAZIONE_PA+protocollo);
						
						if(newConfigurazione.getProtocolli()==null) {
							newConfigurazione.setProtocolli(new ConfigurazioneProtocolli());	
						}
						
						ConfigurazioneProtocollo configProtocollo = new ConfigurazioneProtocollo();
						configProtocollo.setNome(nameP);
						configProtocollo.setUrlInvocazioneServizioPD(urlInvocazionePD);
						configProtocollo.setUrlInvocazioneServizioPA(urlInvocazionePA);
						newConfigurazione.getProtocolli().addProtocollo(configProtocollo);
					}
				}
				
				confCore.performUpdateOperation(userLogin, confHelper.smista(), newConfigurazione);
				
				if(extendedBeanList!=null && extendedBeanList.size()>0){
					for (ExtendedInfo ei : extendedBeanList) {
						if(ei.extended && !ei.extendedToNewWindow){
							WrapperExtendedBean wrapperExtendedBean = new WrapperExtendedBean();
							wrapperExtendedBean.setExtendedBean(ei.extendedBean);
							wrapperExtendedBean.setExtendedServlet(ei.extendedServlet);
							wrapperExtendedBean.setOriginalBean(newConfigurazione);
							wrapperExtendedBean.setManageOriginalBean(false);
							confCore.performUpdateOperation(userLogin, confHelper.smista(), wrapperExtendedBean);
						}
					}
				}

				Vector<DataElement> dati = new Vector<DataElement>();

				dati = confHelper.addConfigurazioneToDati(  inoltromin, stato, controllo, severita, severita_log4j, integman, nomeintegman, profcoll, 
						connessione, utilizzo, validman, gestman, registrazioneTracce, dumpPD, dumpPA, 
						xsd, tipoValidazione, confPers, configurazione, dati, applicaMTOM,
						configurazione.getProtocolli(),
						multitenantEnabled, multitenantSoggettiFruizioni, multitenantSoggettiErogazioni,
						false);

				confHelper.setDataElementCache(dati,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_CONFIG,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_CONFIG,statocache_config,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_CONFIG,dimensionecache_config,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_CONFIG,algoritmocache_config,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_CONFIG,idlecache_config,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_CONFIG,lifecache_config);
				
				confHelper.setDataElementCache(dati,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_AUTHZ,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_AUTHZ,statocache_authz,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_AUTHZ,dimensionecache_authz,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_AUTHZ,algoritmocache_authz,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_AUTHZ,idlecache_authz,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_AUTHZ,lifecache_authz);
				
				confHelper.setDataElementCache(dati,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_AUTHN,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_AUTHN,statocache_authn,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_AUTHN,dimensionecache_authn,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_AUTHN,algoritmocache_authn,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_AUTHN,idlecache_authn,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_AUTHN,lifecache_authn);
				
				confHelper.setDataElementCache(dati,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_TOKEN,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_TOKEN,statocache_token,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_TOKEN,dimensionecache_token,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_TOKEN,algoritmocache_token,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_TOKEN,idlecache_token,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_TOKEN,lifecache_token);
				
				if(extendedBeanList!=null && extendedBeanList.size()>0){
					for (ExtendedInfo ei : extendedBeanList) {
						if(ei.extended){
							if(ei.extendedToNewWindow){
								AbstractServletNewWindowChangeExtended.addToDatiNewWindow(ei.extendedServlet, 
										dati, confHelper, confCore, newConfigurazione, ei.extendedBean, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE_EXTENDED);
							}else{
								ei.extendedServlet.addToDati(dati, TipoOperazione.CHANGE, confHelper, confCore, newConfigurazione, ei.extendedBean);
							}
						}
					}
				}

				pd.setDati(dati);

				if(confHelper.isModalitaStandard()) {
					pd.setMessage(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_GENERALE_MODIFICATA_CON_SUCCESSO_SOLO_DATI_CONSOLE, Costanti.MESSAGE_TYPE_INFO);
				}
				else {
					pd.setMessage(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_GENERALE_MODIFICATA_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);
				}

				pd.disableEditMode();
				
				generalHelper = new GeneralHelper(session);
				gd = generalHelper.initGeneralData(request); // re-inizializzo per ricalcolare il menu in alto a destra
				
				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeFinished(mapping,
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_GENERALE,
						ConfigurazioneCostanti.TIPO_OPERAZIONE_CONFIGURAZIONE_GENERALE);
			}

			if (inoltromin == null) {
				inoltromin = configurazione.getInoltroBusteNonRiscontrate().getCadenza();
				if(configurazione.getValidazioneBuste().getStato()!=null)
					stato = configurazione.getValidazioneBuste().getStato().toString();
				if(configurazione.getValidazioneBuste().getControllo()!=null)
					controllo = configurazione.getValidazioneBuste().getControllo().toString();
				if(configurazione.getMessaggiDiagnostici().getSeverita()!=null)
					severita = configurazione.getMessaggiDiagnostici().getSeverita().toString();
				if(configurazione.getMessaggiDiagnostici().getSeveritaLog4j()!=null)
					severita_log4j = configurazione.getMessaggiDiagnostici().getSeveritaLog4j().toString();
				integman = configurazione.getIntegrationManager().getAutenticazione();
				boolean foundIM = false;
				if(integman!=null){
					for (int i = 0; i < ConfigurazioneCostanti.PARAMETRI_CONFIGURAZIONE_IM.length; i++) {
						if(ConfigurazioneCostanti.PARAMETRI_CONFIGURAZIONE_IM[i].equals(integman)){
							foundIM = true;
							break;
						}
					}
				}
				if (foundIM == false) {
					nomeintegman = integman;
					integman = ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_IM_CUSTOM  ;
				}
				if(configurazione.getValidazioneBuste().getProfiloCollaborazione()!=null)
					profcoll = configurazione.getValidazioneBuste().getProfiloCollaborazione().toString();
				if(configurazione.getRisposte().getConnessione()!=null)
					connessione = configurazione.getRisposte().getConnessione().toString();
				if(configurazione.getIndirizzoRisposta().getUtilizzo()!=null)
					utilizzo = configurazione.getIndirizzoRisposta().getUtilizzo().toString();
				if(configurazione.getValidazioneBuste().getManifestAttachments()!=null)
					validman = configurazione.getValidazioneBuste().getManifestAttachments().toString();
				if(configurazione.getAttachments().getGestioneManifest()!=null)
					gestman = configurazione.getAttachments().getGestioneManifest().toString();
				if(configurazione.getTracciamento().getStato()!=null)
					registrazioneTracce = configurazione.getTracciamento().getStato().toString();
				if(configurazione.getDump().getDumpBinarioPortaDelegata()!=null)
					dumpPD = configurazione.getDump().getDumpBinarioPortaDelegata().toString();
				if(configurazione.getDump().getDumpBinarioPortaApplicativa()!=null)
					dumpPA = configurazione.getDump().getDumpBinarioPortaApplicativa().toString();
				if (configurazione.getValidazioneContenutiApplicativi() != null) {
					if(configurazione.getValidazioneContenutiApplicativi().getStato()!=null)
						xsd = configurazione.getValidazioneContenutiApplicativi().getStato().toString();
					if(configurazione.getValidazioneContenutiApplicativi().getTipo()!=null)
						tipoValidazione = configurazione.getValidazioneContenutiApplicativi().getTipo().toString();
					if(configurazione.getValidazioneContenutiApplicativi().getAcceptMtomMessage()!=null){
						if(StatoFunzionalita.ABILITATO.equals(configurazione.getValidazioneContenutiApplicativi().getAcceptMtomMessage())){
							applicaMTOM = Costanti.CHECK_BOX_ENABLED_ABILITATO;
						}
						else{
							applicaMTOM = Costanti.CHECK_BOX_DISABLED;
						}
					}
				}
				if(configurazione.getAccessoConfigurazione()!=null && configurazione.getAccessoConfigurazione().getCache()!=null){
					statocache_config = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
					dimensionecache_config = configurazione.getAccessoConfigurazione().getCache().getDimensione();
					if(configurazione.getAccessoConfigurazione().getCache().getAlgoritmo()!=null){
						algoritmocache_config = configurazione.getAccessoConfigurazione().getCache().getAlgoritmo().getValue();
					}
					idlecache_config = configurazione.getAccessoConfigurazione().getCache().getItemIdleTime();
					lifecache_config = configurazione.getAccessoConfigurazione().getCache().getItemLifeSecond();
				}
				else{
					statocache_config = ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
				}
				if(configurazione.getAccessoDatiAutorizzazione()!=null && configurazione.getAccessoDatiAutorizzazione().getCache()!=null){
					statocache_authz = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
					dimensionecache_authz = configurazione.getAccessoDatiAutorizzazione().getCache().getDimensione();
					if(configurazione.getAccessoDatiAutorizzazione().getCache().getAlgoritmo()!=null){
						algoritmocache_authz = configurazione.getAccessoDatiAutorizzazione().getCache().getAlgoritmo().getValue();
					}
					idlecache_authz = configurazione.getAccessoDatiAutorizzazione().getCache().getItemIdleTime();
					lifecache_authz = configurazione.getAccessoDatiAutorizzazione().getCache().getItemLifeSecond();
				}
				else{
					statocache_authz = ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
				}
				if(configurazione.getAccessoDatiAutenticazione()!=null && configurazione.getAccessoDatiAutenticazione().getCache()!=null){
					statocache_authn = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
					dimensionecache_authn = configurazione.getAccessoDatiAutenticazione().getCache().getDimensione();
					if(configurazione.getAccessoDatiAutenticazione().getCache().getAlgoritmo()!=null){
						algoritmocache_authn = configurazione.getAccessoDatiAutenticazione().getCache().getAlgoritmo().getValue();
					}
					idlecache_authn = configurazione.getAccessoDatiAutenticazione().getCache().getItemIdleTime();
					lifecache_authn = configurazione.getAccessoDatiAutenticazione().getCache().getItemLifeSecond();
				}
				else{
					statocache_authn = ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
				}
				if(configurazione.getAccessoDatiGestioneToken()!=null && configurazione.getAccessoDatiGestioneToken().getCache()!=null){
					statocache_token = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
					dimensionecache_token = configurazione.getAccessoDatiGestioneToken().getCache().getDimensione();
					if(configurazione.getAccessoDatiGestioneToken().getCache().getAlgoritmo()!=null){
						algoritmocache_token = configurazione.getAccessoDatiGestioneToken().getCache().getAlgoritmo().getValue();
					}
					idlecache_token = configurazione.getAccessoDatiGestioneToken().getCache().getItemIdleTime();
					lifecache_token = configurazione.getAccessoDatiGestioneToken().getCache().getItemLifeSecond();
				}
				else{
					statocache_token = ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
				}
				if(configurazione.getMultitenant()!=null) {
					if(configurazione.getMultitenant().getStato()!=null) {
						multitenantEnabled = StatoFunzionalita.ABILITATO.equals(configurazione.getMultitenant().getStato());
					}
					if(configurazione.getMultitenant().getFruizioneSceltaSoggettiErogatori()!=null) {
						switch (configurazione.getMultitenant().getFruizioneSceltaSoggettiErogatori()) {
						case SOGGETTI_ESTERNI:
							multitenantSoggettiFruizioni = MultitenantSoggettiFruizioni.SOLO_SOGGETTI_ESTERNI.name();
							break;
						case ESCLUDI_SOGGETTO_FRUITORE:
							multitenantSoggettiFruizioni = MultitenantSoggettiFruizioni.ESCLUDI_SOGGETTO_FRUITORE.name();
							break;
						case TUTTI:
							multitenantSoggettiFruizioni = MultitenantSoggettiFruizioni.TUTTI.name();
							break;
						}
					}
					if(configurazione.getMultitenant().getErogazioneSceltaSoggettiFruitori()!=null) {
						switch (configurazione.getMultitenant().getErogazioneSceltaSoggettiFruitori()) {
						case SOGGETTI_ESTERNI:
							multitenantSoggettiErogazioni = MultitenantSoggettiErogazioni.SOLO_SOGGETTI_ESTERNI.name();
							break;
						case ESCLUDI_SOGGETTO_EROGATORE:
							multitenantSoggettiErogazioni = MultitenantSoggettiErogazioni.ESCLUDI_SOGGETTO_EROGATORE.name();
							break;
						case TUTTI:
							multitenantSoggettiErogazioni = MultitenantSoggettiErogazioni.TUTTI.name();
							break;
						}
					}
				}
			}

			// preparo i campi
			Vector<DataElement> dati = new Vector<DataElement>();

			dati.add(ServletUtils.getDataElementForEditModeFinished());

			dati = confHelper.addConfigurazioneToDati(  inoltromin, stato, controllo, severita, severita_log4j, integman, nomeintegman, profcoll, 
					connessione, utilizzo, validman, gestman, registrazioneTracce, dumpPD, dumpPA, 
					xsd, tipoValidazione, confPers, configurazione, dati, applicaMTOM,
					configurazione.getProtocolli(),
					multitenantEnabled, multitenantSoggettiFruizioni, multitenantSoggettiErogazioni,
					true);

			confHelper.setDataElementCache(dati,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_CONFIG,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_CONFIG,statocache_config,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_CONFIG,dimensionecache_config,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_CONFIG,algoritmocache_config,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_CONFIG,idlecache_config,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_CONFIG,lifecache_config);
			
			confHelper.setDataElementCache(dati,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_AUTHZ,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_AUTHZ,statocache_authz,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_AUTHZ,dimensionecache_authz,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_AUTHZ,algoritmocache_authz,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_AUTHZ,idlecache_authz,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_AUTHZ,lifecache_authz);
			
			confHelper.setDataElementCache(dati,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_AUTHN,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_AUTHN,statocache_authn,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_AUTHN,dimensionecache_authn,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_AUTHN,algoritmocache_authn,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_AUTHN,idlecache_authn,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_AUTHN,lifecache_authn);
			
			confHelper.setDataElementCache(dati,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_TOKEN,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_TOKEN,statocache_token,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_TOKEN,dimensionecache_token,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_TOKEN,algoritmocache_token,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_TOKEN,idlecache_token,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_TOKEN,lifecache_token);
			
			if(extendedBeanList!=null && extendedBeanList.size()>0){
				for (ExtendedInfo ei : extendedBeanList) {
					if(ei.extended){
						if(ei.extendedToNewWindow){
							AbstractServletNewWindowChangeExtended.addToDatiNewWindow(ei.extendedServlet, 
									dati, confHelper, confCore, configurazione, ei.extendedBean, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE_EXTENDED);
						}else{
							ei.extendedServlet.addToDati(dati, TipoOperazione.CHANGE, confHelper, confCore, configurazione, ei.extendedBean);
						}
					}
				}
			}

			pd.setDati(dati);

			String msg = confHelper.getParameter(Costanti.PARAMETER_NAME_MSG_ERROR_EXPORT);
			if(msg!=null && !"".equals(msg)){
				pd.setMessage("Errore durante esportazione: "+msg);
			}
			
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_GENERALE,
					ConfigurazioneCostanti.TIPO_OPERAZIONE_CONFIGURAZIONE_GENERALE);
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_GENERALE, ConfigurazioneCostanti.TIPO_OPERAZIONE_CONFIGURAZIONE_GENERALE);
		}
	}


}


class ExtendedInfo{
	
	IExtendedBean extendedBean = null;
	boolean extended = false;
	boolean extendedToNewWindow = false;
	IExtendedFormServlet extendedServlet = null;
	
}