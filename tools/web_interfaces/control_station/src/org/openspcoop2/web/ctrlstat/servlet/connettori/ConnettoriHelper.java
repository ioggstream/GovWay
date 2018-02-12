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
package org.openspcoop2.web.ctrlstat.servlet.connettori;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.RispostaAsincrona;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.constants.TransferLengthModes;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Property;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.utils.DynamicStringReplace;
import org.openspcoop2.utils.transport.http.SSLConstants;
import org.openspcoop2.utils.transport.http.SSLUtilities;
import org.openspcoop2.web.ctrlstat.core.Connettori;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.costanti.TipologiaConnettori;
import org.openspcoop2.web.ctrlstat.dao.SoggettoCtrlStat;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationException;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettoreConverter;
import org.openspcoop2.web.ctrlstat.plugins.servlet.ServletExtendedConnettoreUtils;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
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
 * ConnettoriHelper
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoriHelper extends ConsoleHelper {

	public ConnettoriHelper(HttpServletRequest request, PageData pd, 
			HttpSession session) throws Exception {
		super(request, pd,  session);
	}

	public String getAutenticazioneHttp(String autenticazioneHttp,String endpointtype, String user){
		if((endpointtype.equals(TipiConnettore.HTTP.toString()) || endpointtype.equals(TipiConnettore.HTTPS.toString()))){
			if ( autenticazioneHttp==null && user!=null && !"".equals(user) ){
				autenticazioneHttp =  Costanti.CHECK_BOX_ENABLED;
			}  
		}
		else{
			autenticazioneHttp = null;
		}
		return autenticazioneHttp;
	}
	
	public void setTitleProprietaConnettoriCustom(PageData pd,TipoOperazione tipoOperazione,
			String servlet, String id, String nomeprov, String tipoprov,String nomeservizio,String tiposervizio,
			String myId, String correlato, String idSoggErogatore, String nomeservizioApplicativo,String idsil,String tipoAccordo,
			String provider) throws DriverConfigurazioneNotFound, DriverConfigurazioneException, DriverRegistroServiziNotFound, DriverRegistroServiziException, DriverControlStationException{
				
		if (AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE.equals(servlet)) {
			int idServizioInt = Integer.parseInt(id);
			AccordoServizioParteSpecifica asps = this.apsCore.getAccordoServizioParteSpecifica(idServizioInt);
			
			ServletUtils.setPageDataTitle(pd, 
					// t1
					new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, null),
					// t2
					new Parameter(
						Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, 
						AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST), 
					// t3
					new Parameter(
						"Connettore del servizio "+IDServizioFactory.getInstance().getUriFromAccordo(asps), 
						AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE+
						"?"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID+"=" + id+
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO+"=" + nomeservizio + 
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SERVIZIO+"=" + tiposervizio
						)
					);

		}
		
		if (AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE.equals(servlet)) {
			int idServizioInt = Integer.parseInt(id);
			AccordoServizioParteSpecifica asps = this.apsCore.getAccordoServizioParteSpecifica(idServizioInt);
			int idServizioFruitoreInt = Integer.parseInt(myId);
			Fruitore servFru = this.apsCore.getServizioFruitore(idServizioFruitoreInt);
			String nomefru = servFru.getNome();
			String tipofru = servFru.getTipo();

			ServletUtils.setPageDataTitle(pd, 
					// t1
					new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS,null), 
					// t2
					new Parameter(
						Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, 
						AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST), 
					// t3
					new Parameter(
						"Fruitori del servizio " + IDServizioFactory.getInstance().getUriFromAccordo(asps),
						AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_LIST+
						"?"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID+"=" + id + 
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SOGGETTO_EROGATORE+"=" + idSoggErogatore),
					// t4
					new Parameter(
						"Connettore del fruitore "+tipofru + "/" + nomefru,
						AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE+
						"?"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID+"=" + id + 
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_MY_ID+"=" + myId + 
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SOGGETTO_EROGATORE+"=" + idSoggErogatore
						)
					);

		}
		
		if (ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT.equals(servlet)) {
			int idInt = Integer.parseInt(idsil);
			ServizioApplicativo sa = this.saCore.getServizioApplicativo(idInt);

			ServletUtils.setPageDataTitle(pd, 
					// t1
					new Parameter(ServiziApplicativiCostanti.LABEL_SERVIZIO_APPLICATIVO,null), 
					// t2
					new Parameter(
							Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, 
							ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST), 
					// t3
					new Parameter(
						"Connettore del servizio applicativo (InvocazioneServizio) " + nomeservizioApplicativo+" del soggetto "+sa.getTipoSoggettoProprietario()+"/"+sa.getNomeSoggettoProprietario(),
						ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT+
						"?"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO_APPLICATIVO+"=" + nomeservizioApplicativo + 
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SERVIZIO_APPLICATIVO+"=" + idsil +
						"&"+ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER+"=" + provider
						)
					);
		}
		
		if (ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT_RISPOSTA.equals(servlet)) {
			int idInt = Integer.parseInt(idsil);
			ServizioApplicativo sa = this.saCore.getServizioApplicativo(idInt);

			ServletUtils.setPageDataTitle(pd, 
					// t1
					new Parameter(ServiziApplicativiCostanti.LABEL_SERVIZIO_APPLICATIVO, null),
					// t2
					new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, 
								 ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST), 
					// t3
					new Parameter(			 
						"Connettore del servizio applicativo (RispostaAsincrona) " + nomeservizioApplicativo+" del soggetto "+sa.getTipoSoggettoProprietario()+"/"+sa.getNomeSoggettoProprietario(),
						ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT_RISPOSTA+
						"?"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO_APPLICATIVO+"=" + nomeservizioApplicativo + 
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SERVIZIO_APPLICATIVO+"=" + idsil +
						"&"+ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER+"=" + provider
					)
					);

		}
		
		if (SoggettiCostanti.SERVLET_NAME_SOGGETTI_ENDPOINT.equals(servlet)) {
			int idInt = Integer.parseInt(id);
			SoggettoCtrlStat scs = this.soggettiCore.getSoggettoCtrlStat(idInt);
			String nomeprov1 = scs.getNome();
			String tipoprov1 = scs.getTipo();

			ServletUtils.setPageDataTitle(pd, 
					// t1
					new Parameter(SoggettiCostanti.LABEL_SOGGETTI,null), 
					// t2
					new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, 
								  SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST), 
					// t3
					new Parameter(
						"Connettore del soggetto " + tipoprov1 + "/" + nomeprov1,
						SoggettiCostanti.SERVLET_NAME_SOGGETTI_ENDPOINT+
						"?"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID+"=" + id + 
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SOGGETTO+"=" + nomeprov + 
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SOGGETTO+"=" + tipoprov
						)
					);

		}

		if(!TipoOperazione.LIST.equals(tipoOperazione)){
			ServletUtils.appendPageDataTitle(pd,
					// t1
					new Parameter(
						ConnettoriCostanti.LABEL_CONNETTORE_PROPRIETA,
						ConnettoriCostanti.SERVLET_NAME_CONNETTORI_CUSTOM_PROPERTIES_LIST+
						"?"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_SERVLET+"=" + servlet + 
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID+"=" + id + 
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SOGGETTO+"=" + nomeprov + 
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SOGGETTO+"=" + tipoprov +
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO+"=" + nomeservizio + 
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SERVIZIO+"=" + tiposervizio + 
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_CORRELATO+"=" + correlato +
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_MY_ID+"=" + myId + 
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SOGGETTO_EROGATORE+"=" + idSoggErogatore +
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO_APPLICATIVO+"=" + nomeservizioApplicativo + 
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SERVIZIO_APPLICATIVO+"=" + idsil
							)
					);
		}
		ServletUtils.appendPageDataTitle(pd,
				new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI,null));
	}

	
	
	
	public void prepareConnettorePropList(List<?> lista, ISearch ricerca,
			int newMyId,String tipoAccordo)
	throws Exception {
		try {
			
			String servlet = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_SERVLET);
			String id = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID);
			String nomeprov = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SOGGETTO);
			String tipoprov = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SOGGETTO);
			String nomeservizio = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO);
			String tiposervizio = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SERVIZIO);
			String myId = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_MY_ID);
			if (newMyId != 0)
				myId = ""+newMyId;
			String correlato = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_CORRELATO);
			String idSoggErogatore = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SOGGETTO_EROGATORE);
			String nomeservizioApplicativo = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO_APPLICATIVO);
			String idsil = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SERVIZIO_APPLICATIVO);
			String provider = this.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
			
			ServletUtils.addListElementIntoSession(this.session, ConnettoriCostanti.OBJECT_NAME_CONNETTORI_CUSTOM_PROPERTIES,
					new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_SERVLET, servlet),
					new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID, id),
					new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SOGGETTO, nomeprov),
					new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SOGGETTO, tipoprov),
					new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO, nomeservizio),
					new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SERVIZIO, tiposervizio),
					new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_MY_ID, myId),
					new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_CORRELATO, correlato),
					new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SOGGETTO_EROGATORE, idSoggErogatore),
					new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO_APPLICATIVO, nomeservizioApplicativo),
					new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SERVIZIO_APPLICATIVO, idsil),
					new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER, provider));
			
//			Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);
//			
			//int idLista = Liste.SERVIZI_SERVIZIO_APPLICATIVO;
			//int limit = ricerca.getPageSize(idLista);
			//int offset = ricerca.getIndexIniziale(idLista);
			//String search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));

			//this.pd.setIndex(offset);
			//this.pd.setPageSize(limit);
			this.pd.setNumEntries(lista.size());

			// setto la barra del titolo
			setTitleProprietaConnettoriCustom(this.pd, TipoOperazione.LIST, servlet, id, nomeprov, tipoprov, nomeservizio, tiposervizio, 
					myId, correlato, idSoggErogatore, nomeservizioApplicativo, idsil, tipoAccordo, provider);
			
			ServletUtils.disabledPageDataSearch(this.pd);
			

			// setto le label delle colonne
			String[] labels = { ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_CUSTOM_NOME, ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_CUSTOM_VALORE };
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<?> it = lista.iterator();

				while (it.hasNext()) {
					String nome = "", valore = "";
					if (servlet.equals(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT) ||
							servlet.equals(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT_RISPOSTA)) {
						org.openspcoop2.core.config.Property cp =
							(org.openspcoop2.core.config.Property) it.next();
						nome = cp.getNome();
						valore = cp.getValore();
					} else {
						Property cp = (Property) it.next();
						nome = cp.getNome();
						valore = cp.getValore();
					}

					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					de.setValue(nome);
					de.setIdToRemove(nome);
					e.addElement(de);

					de = new DataElement();
					de.setValue(valore);
					e.addElement(de);

					dati.addElement(e);
				}
			}

			// inserisco i campi hidden
			Hashtable<String, String> hidden = new Hashtable<String, String>();
			hidden.put(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_SERVLET, servlet);
			hidden.put(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID, id != null ? id : "");
			hidden.put(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SOGGETTO, nomeprov != null ? nomeprov : "");
			hidden.put(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SOGGETTO, tipoprov != null ? tipoprov : "");
			hidden.put(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO, nomeservizio != null ? nomeservizio : "");
			hidden.put(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SERVIZIO, tiposervizio != null ? tiposervizio : "");
			hidden.put(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_MY_ID, myId != null ? myId : "");
			hidden.put(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_CORRELATO, correlato != null ? correlato : "");
			hidden.put(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SOGGETTO_EROGATORE, idSoggErogatore != null ? idSoggErogatore : "");
			hidden.put(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO_APPLICATIVO, nomeservizioApplicativo != null ? nomeservizioApplicativo : "");
			hidden.put(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SERVIZIO_APPLICATIVO, idsil != null ? idsil : "");

			this.pd.setHidden(hidden);

			this.pd.setDati(dati);
			this.pd.setAddButton(true);

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public Vector<DataElement> addEndPointToDati(Vector<DataElement> dati,String connettoreDebug,
			String endpointtype, String autenticazioneHttp, String prefix, String url, String nome, String tipo,
			String user, String password, String initcont, String urlpgk,
			String provurl, String connfact, String sendas, String objectName, TipoOperazione tipoOperazione,
			String httpsurl, String httpstipologia, boolean httpshostverify,
			String httpspath, String httpstipo, String httpspwd,
			String httpsalgoritmo, boolean httpsstato, String httpskeystore,
			String httpspwdprivatekeytrust, String httpspathkey,
			String httpstipokey, String httpspwdkey,
			String httpspwdprivatekey, String httpsalgoritmokey,
			String tipoconn, String servletChiamante, String elem1, String elem2, String elem3,
			String elem4, String elem5, String elem6, String elem7, String elem8,
			boolean showSectionTitle,
			Boolean isConnettoreCustomUltimaImmagineSalvata,
			String proxyEnabled, String proxyHost, String proxyPort, String proxyUsername, String proxyPassword,
			String opzioniAvanzate, String transfer_mode, String transfer_mode_chunk_size, String redirect_mode, String redirect_max_hop,
			String requestOutputFileName,String requestOutputFileNameHeaders,String requestOutputParentDirCreateIfNotExists,String requestOutputOverwriteIfExists,
			String responseInputMode, String responseInputFileName, String responseInputFileNameHeaders, String responseInputDeleteAfterRead, String responseInputWaitTime,
			List<ExtendedConnettore> listExtendedConnettore, boolean forceEnabled) {
		return addEndPointToDati(dati, connettoreDebug, endpointtype, autenticazioneHttp, prefix, url, nome, tipo, user,
				password, initcont, urlpgk, provurl, connfact, sendas,
				objectName,tipoOperazione, httpsurl, httpstipologia, httpshostverify,
				httpspath, httpstipo, httpspwd, httpsalgoritmo, httpsstato,
				httpskeystore, httpspwdprivatekeytrust, httpspathkey,
				httpstipokey, httpspwdkey, httpspwdprivatekey,
				httpsalgoritmokey, tipoconn, servletChiamante, elem1, elem2, elem3,
				elem4, elem5, elem6, elem7, elem8, null, showSectionTitle,
				isConnettoreCustomUltimaImmagineSalvata,
				proxyEnabled, proxyHost, proxyPort, proxyUsername, proxyPassword,
				opzioniAvanzate, transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop,
				requestOutputFileName,requestOutputFileNameHeaders,requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
				responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
				listExtendedConnettore, forceEnabled);
	}

	// Controlla i dati del connettore custom
	boolean connettorePropCheckData() throws Exception {
		try {
			String servlet = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_SERVLET);
			String id = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID);
			//String nomeprov = this.getParameter("nomeprov");
			//String tipoprov = this.getParameter("tipoprov");
			//String nomeservizio = this.getParameter("nomeservizio");
			//String tiposervizio = this.getParameter("tiposervizio");
			String myId = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_MY_ID);
			//String correlato = this.getParameter("correlato");
			//String idSoggErogatore = this.getParameter("idSoggErogatore");
			//String nomeservizioApplicativo = this.getParameter("nomeservizioApplicativo");
			String idsil = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SERVIZIO_APPLICATIVO);

			String nome = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME);
			String valore = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_VALORE);

			// Campi obbligatori
			if (nome.equals("") || valore.equals("")) {
				String tmpElenco = "";
				if (nome.equals("")) {
					tmpElenco = ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_CUSTOM_NOME;
				}
				if (valore.equals("")) {
					if (tmpElenco.equals("")) {
						tmpElenco = ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_CUSTOM_VALORE;
					} else {
						tmpElenco = tmpElenco + ", "+ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_CUSTOM_VALORE;
					}
				}
				this.pd.setMessage("Dati incompleti. E' necessario indicare: " + tmpElenco);
				return false;
			}

			// Controllo che non ci siano spazi nei campi di testo
			if ((nome.indexOf(" ") != -1) || (valore.indexOf(" ") != -1)) {
				this.pd.setMessage("Non inserire spazi nei campi di testo");
				return false;
			}

			// Controllo che la property non sia gia' stata
			// registrata
			boolean giaRegistratoProprietaNormale = false;
			boolean giaRegistratoProprietaDebug = false;
			if (servlet.equals(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE)) {
				AccordoServizioParteSpecifica asps = this.apsCore.getAccordoServizioParteSpecifica(Long.parseLong(id));
				org.openspcoop2.core.registry.Connettore connettore = asps.getConfigurazioneServizio().getConnettore();
				for (int j = 0; j < connettore.sizePropertyList(); j++) {
					Property tmpProp = connettore.getProperty(j);
					if (tmpProp.getNome().equals(nome)) {
						if(CostantiDB.CONNETTORE_DEBUG.equals(nome)){
							giaRegistratoProprietaDebug = true;
						}else{
							giaRegistratoProprietaNormale = true;
						}
						break;
					}
				}
			}
			if (servlet.equals(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE)) {
				int idServizioFruitoreInt = Integer.parseInt(myId);
				Fruitore servFru = this.apsCore.getServizioFruitore(idServizioFruitoreInt);
				org.openspcoop2.core.registry.Connettore connettore = servFru.getConnettore();
				for (int j = 0; j < connettore.sizePropertyList(); j++) {
					Property tmpProp = connettore.getProperty(j);
					if (tmpProp.getNome().equals(nome)) {
						if(CostantiDB.CONNETTORE_DEBUG.equals(nome)){
							giaRegistratoProprietaDebug = true;
						}else{
							giaRegistratoProprietaNormale = true;
						}
						break;
					}
				}
			}
			if (servlet.equals(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT)) {
				int idSilInt = Integer.parseInt(idsil);
				ServizioApplicativo sa = this.saCore.getServizioApplicativo(idSilInt);
				InvocazioneServizio is = sa.getInvocazioneServizio();
				org.openspcoop2.core.config.Connettore connettore = is.getConnettore();
				for (int j = 0; j < connettore.sizePropertyList(); j++) {
					org.openspcoop2.core.config.Property tmpProp = connettore.getProperty(j);
					if (tmpProp.getNome().equals(nome)) {
						if(CostantiDB.CONNETTORE_DEBUG.equals(nome)){
							giaRegistratoProprietaDebug = true;
						}else{
							giaRegistratoProprietaNormale = true;
						}
						break;
					}
				}
			}
			if (servlet.equals(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT_RISPOSTA)) {
				int idSilInt = Integer.parseInt(idsil);
				ServizioApplicativo sa = this.saCore.getServizioApplicativo(idSilInt);
				RispostaAsincrona ra = sa.getRispostaAsincrona();
				org.openspcoop2.core.config.Connettore connettore = ra.getConnettore();
				for (int j = 0; j < connettore.sizePropertyList(); j++) {
					org.openspcoop2.core.config.Property tmpProp = connettore.getProperty(j);
					if (tmpProp.getNome().equals(nome)) {
						if(CostantiDB.CONNETTORE_DEBUG.equals(nome)){
							giaRegistratoProprietaDebug = true;
						}else{
							giaRegistratoProprietaNormale = true;
						}
						break;
					}
				}
			}
			if (servlet.equals(SoggettiCostanti.SERVLET_NAME_SOGGETTI_ENDPOINT)) {
				int idInt = Integer.parseInt(id);
				SoggettoCtrlStat scs = this.soggettiCore.getSoggettoCtrlStat(idInt);
				Soggetto ss = scs.getSoggettoReg();
				org.openspcoop2.core.registry.Connettore connettore = ss.getConnettore();
				for (int j = 0; j < connettore.sizePropertyList(); j++) {
					Property tmpProp = connettore.getProperty(j);
					if (tmpProp.getNome().equals(nome)) {
						if(CostantiDB.CONNETTORE_DEBUG.equals(nome)){
							giaRegistratoProprietaDebug = true;
						}else{
							giaRegistratoProprietaNormale = true;
						}
						break;
					}
				}
			}

			if (giaRegistratoProprietaNormale) {
				this.pd.setMessage("La propriet&agrave; '" + nome + "' &egrave; gi&agrave; stata associata al connettore");
				return false;
			}
			if (giaRegistratoProprietaDebug) {
				this.pd.setMessage("La keyword '" + nome + "' non &egrave; associabile come nome ad una propriet&agrave; del connettore");
				return false;
			}

			return true;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	public String readEndPointType() throws Exception{
		String endpointtype = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE);
		String endpointtype_check = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_CHECK);
		String endpointtype_ssl = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTPS);
		return this.readEndPointType(endpointtype, endpointtype_check, endpointtype_ssl);
	}
	public String readEndPointType(String endpointtype,String endpointtype_check,String endpointtype_ssl){
				
		TipologiaConnettori tipologiaConnettori = null;
		try {
			tipologiaConnettori = Utilities.getTipologiaConnettori(this.core);
		} catch (Exception e) {
			// default
			tipologiaConnettori = TipologiaConnettori.TIPOLOGIA_CONNETTORI_ALL;
		}
		
		if(endpointtype_check!=null && !"".equals(endpointtype_check)){
			if (TipologiaConnettori.TIPOLOGIA_CONNETTORI_HTTP.equals(tipologiaConnettori)) {
				if(ServletUtils.isCheckBoxEnabled(endpointtype_check)){
					if(ServletUtils.isCheckBoxEnabled(endpointtype_ssl)){
						endpointtype = TipiConnettore.HTTPS.toString();
					}
					else{
						endpointtype = TipiConnettore.HTTP.toString();
					}
				}
				else{
					endpointtype = TipiConnettore.DISABILITATO.toString();
				}
			}
		}
		return endpointtype;
	}
	
	public Vector<DataElement> addOpzioniAvanzateHttpToDati(Vector<DataElement> dati,
			String opzioniAvanzate, String transfer_mode, String transfer_mode_chunk_size, String redirect_mode, String redirect_max_hop){
		
		boolean showOpzioniAvanzate = this.isModalitaAvanzata()
				&& ServletUtils.isCheckBoxEnabled(opzioniAvanzate);
		
		if(showOpzioniAvanzate){
			DataElement de = new DataElement();
			de.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_OPZIONI_AVANZATE);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
		}
		else{
			if(this.isModalitaAvanzata() &&
					!ServletUtils.isCheckBoxEnabled(opzioniAvanzate)){
				transfer_mode=null;
				transfer_mode_chunk_size=null;
				redirect_mode=null;
				redirect_max_hop=null;
			}
		}
		
		// DataTransferMode
		DataElement de = new DataElement();
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_MODE);
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_MODE);
		if(showOpzioniAvanzate){
			if(transfer_mode==null || "".equals(transfer_mode)){
				transfer_mode = ConnettoriCostanti.DEFAULT_TIPO_DATA_TRANSFER;
			}
			de.setType(DataElementType.SELECT);
			de.setValues(ConnettoriCostanti.TIPI_DATA_TRANSFER);
			de.setPostBack(true);
			de.setSelected(transfer_mode);
			de.setSize(this.getSize());
		}
		else{
			de.setType(DataElementType.HIDDEN);
		}
		de.setValue(transfer_mode);
		dati.addElement(de);
		
		de = new DataElement();
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_CHUNK_SIZE);
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_CHUNK_SIZE);
		if(showOpzioniAvanzate && TransferLengthModes.TRANSFER_ENCODING_CHUNKED.getNome().equals(transfer_mode)){
			de.setType(DataElementType.TEXT_EDIT);
			de.setSize(this.getSize());
		}
		else{
			de.setType(DataElementType.HIDDEN);
		}
		de.setValue(transfer_mode_chunk_size);
		dati.addElement(de);
		
		
		// Redirect
		de = new DataElement();
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MODE);
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MODE);
		if(showOpzioniAvanzate){
			if(redirect_mode==null || "".equals(redirect_mode)){
				redirect_mode = ConnettoriCostanti.DEFAULT_GESTIONE_REDIRECT;
			}
			de.setType(DataElementType.SELECT);
			de.setValues(ConnettoriCostanti.TIPI_GESTIONE_REDIRECT);
			de.setPostBack(true);
			de.setSelected(redirect_mode);
			de.setSize(this.getSize());
		}
		else{
			de.setType(DataElementType.HIDDEN);
		}
		de.setValue(redirect_mode);
		dati.addElement(de);
		
		de = new DataElement();
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MAX_HOP);
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MAX_HOP);
		if(showOpzioniAvanzate && CostantiConfigurazione.ABILITATO.getValue().equals(redirect_mode)){
			de.setType(DataElementType.TEXT_EDIT);
			de.setSize(this.getSize());
		}
		else{
			de.setType(DataElementType.HIDDEN);
		}
		de.setValue(redirect_max_hop);
		dati.addElement(de);
		
		return dati;
	}
	
	public Vector<DataElement> addProxyToDati(Vector<DataElement> dati,
			String proxyHostname, String proxyPort, String proxyUsername, String proxyPassword){
		
		DataElement de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_PROXY);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_PROXY_HOSTNAME);
		de.setValue(proxyHostname);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_HOSTNAME);
		de.setSize(this.getSize());
		de.setRequired(true);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_PROXY_PORT);
		de.setValue(proxyPort);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PORT);
		de.setSize(this.getSize());
		de.setRequired(true);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_PROXY_USERNAME);
		de.setValue(StringEscapeUtils.escapeHtml(proxyUsername));
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_USERNAME);
		de.setSize(this.getSize());
		de.setRequired(false);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_PROXY_PASSWORD);
		de.setValue(StringEscapeUtils.escapeHtml(proxyPassword));
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PASSWORD);
		de.setSize(this.getSize());
		de.setRequired(false);
		dati.addElement(de);
		
		return dati;
	}
	
	public Vector<DataElement> addCredenzialiToDati(Vector<DataElement> dati, String tipoauth, String utente, String password, String subject, String principal,
			String toCall, boolean showLabelCredenzialiAccesso, String endpointtype,boolean connettore,boolean visualizzaTipoAutenticazione,
			String prefix, boolean autenticazioneNessunaAbilitata) {

		DataElement de = null;

		if(prefix==null){
			prefix = "";
		}

		String[] tipoA = null;
		String[] labelTipoA = null;
		if(visualizzaTipoAutenticazione){
			if (ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT.equals(toCall) || 
					ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT_RISPOSTA.equals(toCall)) {
				if(TipiConnettore.HTTPS.toString().equals(endpointtype) || TipiConnettore.HTTP.toString().equals(endpointtype) || 
						TipiConnettore.JMS.toString().equals(endpointtype) || TipiConnettore.CUSTOM.toString().equals(endpointtype)){
					tipoA = new String[] { ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA, 
							ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC };
					if(TipiConnettore.HTTPS.toString().equals(endpointtype) || TipiConnettore.HTTP.toString().equals(endpointtype) ){
						labelTipoA = new String[] { CostantiConfigurazione.DISABILITATO.toString(), 
								ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC };
					}else{
						labelTipoA = new String[] { CostantiConfigurazione.DISABILITATO.toString(), 
								CostantiConfigurazione.ABILITATO.toString() };
					}
				}else{
					tipoA = new String[] { ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA };
					labelTipoA = new String[] { CostantiConfigurazione.DISABILITATO.toString() };
					tipoauth = ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA;
				}
			} else {
				boolean autenticazioneNessuna = autenticazioneNessunaAbilitata;
				if (! (SoggettiCostanti.SERVLET_NAME_SOGGETTI_ADD.equals(toCall) || 
						SoggettiCostanti.SERVLET_NAME_SOGGETTI_CHANGE.equals(toCall)) ) {
					if (this.isModalitaStandard()){
						autenticazioneNessuna = false;
					}
				}
				
				if (autenticazioneNessuna) {
					tipoA = new String[] { ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA,
							ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC, 
							ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL,
							ConnettoriCostanti.AUTENTICAZIONE_TIPO_PRINCIPAL };
					labelTipoA = new String[] { ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA,
							ConnettoriCostanti.LABEL_AUTENTICAZIONE_TIPO_BASIC, 
							ConnettoriCostanti.LABEL_AUTENTICAZIONE_TIPO_SSL,
							ConnettoriCostanti.LABEL_AUTENTICAZIONE_TIPO_PRINCIPAL };
				}
				else{
					tipoA = new String[] { 
							ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC, 
							ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL,
							ConnettoriCostanti.AUTENTICAZIONE_TIPO_PRINCIPAL };
					labelTipoA = new String[] { 
							ConnettoriCostanti.LABEL_AUTENTICAZIONE_TIPO_BASIC, 
							ConnettoriCostanti.LABEL_AUTENTICAZIONE_TIPO_SSL,
							ConnettoriCostanti.LABEL_AUTENTICAZIONE_TIPO_PRINCIPAL};
					if(tipoauth==null){
						tipoauth = ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL;
					}
				}
			}
		}

		boolean showSezioneCredenziali = true;
		if(visualizzaTipoAutenticazione==false){
			showSezioneCredenziali = true;
		}
		else if(tipoA.length == 1){
			showSezioneCredenziali = false;
		}

		if(showSezioneCredenziali) {
			if(showLabelCredenzialiAccesso){
				de = new DataElement();
				if(connettore){
					if(TipiConnettore.HTTPS.toString().equals(endpointtype) ||  TipiConnettore.HTTP.toString().equals(endpointtype) ){
						de.setLabel(prefix+ServiziApplicativiCostanti.LABEL_CREDENZIALI_ACCESSO_SERVIZIO_APPLICATIVO_HTTP);
					}
					else{
						de.setLabel(prefix+ServiziApplicativiCostanti.LABEL_CREDENZIALI_ACCESSO_SERVIZIO_APPLICATIVO);
					}
				}else{
					de.setLabel(prefix+ServiziApplicativiCostanti.LABEL_CREDENZIALI_ACCESSO_PORTA);
				}
				de.setType(DataElementType.TITLE);
				dati.addElement(de);
			}

			de = new DataElement();
			if(showLabelCredenzialiAccesso){
				de.setLabel(ServiziApplicativiCostanti.LABEL_TIPO_CREDENZIALE);
			}else{
				de.setLabel(ServiziApplicativiCostanti.LABEL_CREDENZIALE_ACCESSO);
			}
			if(connettore){
				de.setName(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_TIPO_AUTENTICAZIONE);
			}
			else{
				de.setName(ConnettoriCostanti.PARAMETRO_CREDENZIALI_TIPO_AUTENTICAZIONE);
			}
			if(visualizzaTipoAutenticazione){
				de.setType(DataElementType.SELECT);
				de.setLabels(labelTipoA);
				de.setValues(tipoA);
				de.setSelected(tipoauth);
				//		de.setOnChange("CambiaAuth('" + toCall + "')");
				de.setPostBack(true);
			}
			else{
				de.setType(DataElementType.HIDDEN);
				de.setValue(tipoauth);
			}
			dati.addElement(de);
			
			if (ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC.equals(tipoauth)) {
				de = new DataElement();
				de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				de.setValue(StringEscapeUtils.escapeHtml(utente));
				de.setType(DataElementType.TEXT_EDIT);
				if(connettore){
					de.setName(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				}
				else{
					de.setName(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				}
				de.setSize(this.getSize());
				de.setRequired(true);
				dati.addElement(de);

				de = new DataElement();
				de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
				de.setValue(StringEscapeUtils.escapeHtml(password));
				// de.setType("crypt");
				de.setType(DataElementType.TEXT_EDIT);
				if(connettore){
					de.setName(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
				}
				else{
					de.setName(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
				}
				de.setSize(this.getSize());
				de.setRequired(true);
				dati.addElement(de);

			}

			if (ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL.equals(tipoauth) && !connettore) {
				de = new DataElement();
				de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_SUBJECT);
				de.setValue(StringEscapeUtils.escapeHtml(subject));
				de.setType(DataElementType.TEXT_EDIT);
				de.setName(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_SUBJECT);
				de.setSize(this.getSize());
				de.setRequired(true);
				dati.addElement(de);
			}
			

			if (ConnettoriCostanti.AUTENTICAZIONE_TIPO_PRINCIPAL.equals(tipoauth)  && !connettore) {
				de = new DataElement();
				de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PRINCIPAL);
				de.setValue(StringEscapeUtils.escapeHtml(principal));
				de.setType(DataElementType.TEXT_EDIT);
				de.setName(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PRINCIPAL);
				de.setSize(this.getSize());
				de.setRequired(true);
				dati.addElement(de);
			}

		}  
		else{
			de = new DataElement();
			de.setType(DataElementType.HIDDEN);
			de.setName(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_TIPO_AUTENTICAZIONE);
			de.setValue(ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA);
			dati.addElement(de);
		}
		
		return dati;
	}
	
	public Vector<DataElement> addEndPointToDati(Vector<DataElement> dati,String connettoreDebug,
			String endpointtype, String autenticazioneHttp,String prefix, String url, String nome, String tipo,
			String user, String password, String initcont, String urlpgk,
			String provurl, String connfact, String sendas, String objectName, TipoOperazione tipoOperazione,
			String httpsurl, String httpstipologia, boolean httpshostverify,
			String httpspath, String httpstipo, String httpspwd,
			String httpsalgoritmo, boolean httpsstato, String httpskeystore,
			String httpspwdprivatekeytrust, String httpspathkey,
			String httpstipokey, String httpspwdkey,
			String httpspwdprivatekey, String httpsalgoritmokey,
			String tipoconn, String servletChiamante, String elem1, String elem2, String elem3,
			String elem4, String elem5, String elem6, String elem7, String elem8,
			String stato,
			boolean showSectionTitle,
			Boolean isConnettoreCustomUltimaImmagineSalvata,
			String proxyEnabled, String proxyHost, String proxyPort, String proxyUsername, String proxyPassword,
			String opzioniAvanzate, String transfer_mode, String transfer_mode_chunk_size, String redirect_mode, String redirect_max_hop,
			String requestOutputFileName,String requestOutputFileNameHeaders,String requestOutputParentDirCreateIfNotExists,String requestOutputOverwriteIfExists,
			String responseInputMode, String responseInputFileName, String responseInputFileNameHeaders, String responseInputDeleteAfterRead, String responseInputWaitTime,
			List<ExtendedConnettore> listExtendedConnettore, boolean forceEnabled) {


		Boolean confPers = (Boolean) this.session.getAttribute(CostantiControlStation.SESSION_PARAMETRO_GESTIONE_CONFIGURAZIONI_PERSONALIZZATE);

		TipologiaConnettori tipologiaConnettori = null;
		try {
			tipologiaConnettori = Utilities.getTipologiaConnettori(this.core);
		} catch (Exception e) {
			// default
			tipologiaConnettori = TipologiaConnettori.TIPOLOGIA_CONNETTORI_ALL;
		}

		// override tipologiaconnettori :
		// se standard allora la tipologia connettori e' sempre http
		// indipendentemente
		// dalla proprieta settata
		if (this.isModalitaStandard()) {
			tipologiaConnettori = TipologiaConnettori.TIPOLOGIA_CONNETTORI_HTTP;
		}

		if(prefix==null){
			prefix="";
		}
		
		if(showSectionTitle){
			DataElement de = new DataElement();
			de.setLabel(prefix+ConnettoriCostanti.LABEL_CONNETTORE);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
		}


		/** VISUALIZZAZIONE HTTP ONLY MODE */

		if (TipologiaConnettori.TIPOLOGIA_CONNETTORI_HTTP.equals(tipologiaConnettori)) {
			
			boolean configurazioneNonVisualizzabile = false;
			
			DataElement de = new DataElement();
			de.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_ABILITATO);

			de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_CHECK);
			if(!TipiConnettore.HTTP.toString().equals(endpointtype) &&
					!TipiConnettore.HTTPS.toString().equals(endpointtype) &&
					!TipiConnettore.DISABILITATO.toString().equals(endpointtype)){
				de.setLabel(null);
				de.setValue(CostantiControlStation.LABEL_CONFIGURAZIONE_IMPOSTATA_MODALITA_AVANZATA_SHORT_MESSAGE);
				de.setType(DataElementType.TEXT);
				configurazioneNonVisualizzabile = true;
				this.pd.disableEditMode();
				this.pd.setMessage(CostantiControlStation.LABEL_CONFIGURAZIONE_IMPOSTATA_MODALITA_AVANZATA_LONG_MESSAGE, Costanti.MESSAGE_TYPE_INFO);
			}
			else if( (  (AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS.equals(objectName) && TipoOperazione.CHANGE.equals(tipoOperazione))
					|| 
					(AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI.equals(objectName) && TipoOperazione.CHANGE.equals(tipoOperazione))
					)
					&& StatiAccordo.finale.toString().equals(stato) && this.core.isShowGestioneWorkflowStatoDocumenti() ){
				if (endpointtype.equals(TipiConnettore.HTTP.toString()) || endpointtype.equals(TipiConnettore.HTTPS.toString())) {
					de.setType(DataElementType.HIDDEN);
					de.setSelected(true);
				}
				else{
					de.setLabel(null);
					de.setType(DataElementType.TEXT);
					de.setValue(TipiConnettore.DISABILITATO.toString());
				}
			}else{
				if(forceEnabled) {
					de.setType(DataElementType.HIDDEN);
					de.setSelected(true);
				}
				else {
					de.setType(DataElementType.CHECKBOX);
				}
				if (endpointtype.equals(TipiConnettore.HTTP.toString()) || endpointtype.equals(TipiConnettore.HTTPS.toString())) {
					de.setSelected(true);
				}
				de.setPostBack(true);
				//				de.setOnClick("AbilitaEndPointHTTP(\"" + tipoOp + "\")");
			}
			dati.addElement(de);
			
			
			de = new DataElement();
			de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE);
			if (endpointtype.equals(TipiConnettore.HTTP.toString())) {
				de.setValue(TipiConnettore.HTTP.toString());
			}
			else if (endpointtype.equals(TipiConnettore.HTTP.toString())) {
				de.setValue(TipiConnettore.HTTPS.toString());
			} 
			else {
				de.setValue(TipiConnettore.DISABILITATO.toString());
			}
			de.setType(DataElementType.HIDDEN);
			dati.addElement(de);

			
			de = new DataElement();
			de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_DEBUG);
			de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_DEBUG);
//			if(this.core.isShowDebugOptionConnettore() && !TipiConnettore.DISABILITATO.toString().equals(endpointtype)){
//				de.setType(DataElementType.CHECKBOX);
//				if ( ServletUtils.isCheckBoxEnabled(connettoreDebug)) {
//					de.setSelected(true);
//				}
//			}
//			else{
			de.setType(DataElementType.HIDDEN);
			//}
			if ( ServletUtils.isCheckBoxEnabled(connettoreDebug)) {
				de.setValue("true");
			}
			else{
				de.setValue("false");
			}
			dati.addElement(de);
			
			
			de = new DataElement();
			de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_URL);
			String tmpUrl = url;
			if(url==null || "".equals(url) || "http://".equals(url) || "https://".equals(url) ){
				if (endpointtype.equals(TipiConnettore.HTTP.toString()) || endpointtype.equals(TipiConnettore.HTTPS.toString())) {
					tmpUrl =endpointtype+"://";
				}
			}
			de.setValue(tmpUrl);
			if (endpointtype.equals(TipiConnettore.HTTP.toString()) || endpointtype.equals(TipiConnettore.HTTPS.toString())) {
				if ( !this.core.isShowGestioneWorkflowStatoDocumenti() || !StatiAccordo.finale.toString().equals(stato)) {
					de.setType(DataElementType.TEXT_EDIT);
					de.setRequired(true);
				} else {
					de.setType(DataElementType.TEXT);
				}
			}else{
				de.setType(DataElementType.HIDDEN);
			}
			de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_URL);
			de.setSize(this.getSize());
			dati.addElement(de);
			
//			if (endpointtype.equals(TipiConnettore.HTTPS.toString())) {
//				de = new DataElement();
//				de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_URL);
//				de.setValue(tmpUrl);
//				de.setType(DataElementType.HIDDEN);
//				de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_URL);
//				dati.addElement(de);
//			}
			
			if (endpointtype.equals(TipiConnettore.HTTP.toString()) || endpointtype.equals(TipiConnettore.HTTPS.toString())) {
								
//				if(!ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT.equals(servletChiamante) &&
//						!ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT_RISPOSTA.equals(servletChiamante) ){
				de = new DataElement();
				de.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_HTTP);
				de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP);
				de.setType(DataElementType.CHECKBOX);
				if ( ServletUtils.isCheckBoxEnabled(autenticazioneHttp)) {
					de.setSelected(true);
				}
				de.setPostBack(true);
				dati.addElement(de);		
				//}
				
				de = new DataElement();
				de.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_HTTPS);
				de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTPS);
				de.setType(DataElementType.CHECKBOX);
				if (endpointtype.equals(TipiConnettore.HTTPS.toString())) {
					de.setSelected(true);
				}
				de.setPostBack(true);
				dati.addElement(de);
				
				// Proxy
				de = new DataElement();
				de.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_PROXY);
				de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_ENABLED);
				de.setType(DataElementType.CHECKBOX);
				if ( ServletUtils.isCheckBoxEnabled(proxyEnabled)) {
					de.setSelected(true);
				}
				de.setPostBack(true);
				dati.addElement(de);
				
			}	
			
			// Extended
			if(configurazioneNonVisualizzabile==false) {
				if(listExtendedConnettore!=null && listExtendedConnettore.size()>0){
					ServletExtendedConnettoreUtils.addToDatiEnabled(dati, listExtendedConnettore);
				}
			}
			
			// opzioni avanzate
			if (endpointtype.equals(TipiConnettore.HTTP.toString()) || endpointtype.equals(TipiConnettore.HTTPS.toString())) {
				de = new DataElement();
				de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_OPZIONI_AVANZATE);
				de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE);
				if (this.isModalitaAvanzata()) {
					de.setType(DataElementType.CHECKBOX);
					de.setValue(opzioniAvanzate);
					if ( ServletUtils.isCheckBoxEnabled(opzioniAvanzate)) {
						de.setSelected(true);
					}
				}else{
					de.setType(DataElementType.HIDDEN);
					de.setValue(Costanti.CHECK_BOX_DISABLED);
				}
				de.setPostBack(true);
				dati.addElement(de);
			}
			
			// Http Autenticazione
			if (ServletUtils.isCheckBoxEnabled(autenticazioneHttp)) {
				this.addCredenzialiToDati(dati, CostantiConfigurazione.CREDENZIALE_BASIC.getValue(), user, password, password, null,
						servletChiamante,true,endpointtype,true,false, prefix, true);
			}
			
			// Https
			if (endpointtype.equals(TipiConnettore.HTTPS.toString())) {
				ConnettoreHTTPSUtils.addHTTPSDati(dati, httpsurl, httpstipologia, httpshostverify, httpspath, httpstipo, 
						httpspwd, httpsalgoritmo, httpsstato, httpskeystore, httpspwdprivatekeytrust, httpspathkey, 
						httpstipokey, httpspwdkey, httpspwdprivatekey, httpsalgoritmokey, stato, this.core, this.getSize(), false, prefix);
			}
			
			// Proxy
			if (endpointtype.equals(TipiConnettore.HTTP.toString()) || endpointtype.equals(TipiConnettore.HTTPS.toString())){
				if (ServletUtils.isCheckBoxEnabled(proxyEnabled)) {
					this.addProxyToDati(dati, proxyHost, proxyPort, proxyUsername, proxyPassword);
				}
			}
			
			// Extended
			if(listExtendedConnettore!=null && listExtendedConnettore.size()>0){
				ServletExtendedConnettoreUtils.addToDatiExtendedInfo(dati, listExtendedConnettore);
			}
			
			// Opzioni Avanzate
			if (endpointtype.equals(TipiConnettore.HTTP.toString()) || endpointtype.equals(TipiConnettore.HTTPS.toString())){
				this.addOpzioniAvanzateHttpToDati(dati, opzioniAvanzate, transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop);
			}
			
		} else {

			/** VISUALIZZAZIONE COMPLETA CONNETTORI MODE */

			List<String> connettoriList = Connettori.getList();
			if(forceEnabled) {
				int indexDisabled = -1;
				for (int i = 0; i < connettoriList.size(); i++) {
					if(TipiConnettore.DISABILITATO.getNome().equals(connettoriList.get(i))) {
						indexDisabled = i;
						break;
					}
				}
				if(indexDisabled>=0) {
					connettoriList.remove(indexDisabled);
				}
			}
			int sizeEP = connettoriList.size();
			if (!connettoriList.contains(TipiConnettore.HTTPS.toString()))
				sizeEP++;
			if (confPers &&
					TipologiaConnettori.TIPOLOGIA_CONNETTORI_ALL.equals(tipologiaConnettori))
				sizeEP++;
			String[] tipoEP = new String[sizeEP];
			connettoriList.toArray(tipoEP);
			int newCount = connettoriList.size();
			if (!connettoriList.contains(TipiConnettore.HTTPS.toString())) {
				tipoEP[newCount] = TipiConnettore.HTTPS.toString();
				newCount++;
			}
			if (confPers &&
					TipologiaConnettori.TIPOLOGIA_CONNETTORI_ALL.equals(tipologiaConnettori))
				tipoEP[newCount] = TipiConnettore.CUSTOM.toString();
			//String[] tipoEP = { TipiConnettore.DISABILITATO.toString(), TipiConnettore.HTTP.toString(), TipiConnettore.JMS.toString(), TipiConnettore.NULL.toString(), TipiConnettore.NULL_ECHO.toString() };

			DataElement de = new DataElement();
			de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_ENDPOINT_TYPE);
			de.setType(DataElementType.SELECT);
			de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE);
			de.setValues(tipoEP);
			de.setSelected(endpointtype);
			//		    de.setOnChange("CambiaEndPoint('" + tipoOp + "')");
			de.setPostBack(true);
			dati.addElement(de);

			
			de = new DataElement();
			de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO);
			if (endpointtype == null || !endpointtype.equals(TipiConnettore.CUSTOM.toString()))
				de.setType(DataElementType.HIDDEN);
			else{
				de.setType(DataElementType.TEXT_EDIT);
				de.setRequired(true);
			}
			de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO);
			de.setValue(tipoconn);
			dati.addElement(de);
			
			
			
			de = new DataElement();
			de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_DEBUG);
			de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_DEBUG);
			if(this.core.isShowDebugOptionConnettore() && !TipiConnettore.DISABILITATO.toString().equals(endpointtype)){
				de.setType(DataElementType.CHECKBOX);
				if ( ServletUtils.isCheckBoxEnabled(connettoreDebug)) {
					de.setSelected(true);
				}
			}
			else{
				de.setType(DataElementType.HIDDEN);
			}
			if ( ServletUtils.isCheckBoxEnabled(connettoreDebug)) {
				de.setValue("true");
			}
			else{
				de.setValue("false");
			}
			dati.addElement(de);	
			
			
			de = new DataElement();
			de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_URL);
			String tmpUrl = url;
			if(url==null || "".equals(url) || "http://".equals(url) || "https://".equals(url) ){
				if (endpointtype.equals(TipiConnettore.HTTP.toString()) || endpointtype.equals(TipiConnettore.HTTPS.toString())) {
					tmpUrl =endpointtype+"://";
				}
			}
			de.setValue(tmpUrl);
			if (endpointtype.equals(TipiConnettore.HTTP.toString()) || endpointtype.equals(TipiConnettore.HTTPS.toString())){
				if (!this.core.isShowGestioneWorkflowStatoDocumenti() || !StatiAccordo.finale.toString().equals(stato)) {
					de.setType(DataElementType.TEXT_EDIT);
					de.setRequired(true);
				} else {
					de.setType(DataElementType.TEXT);
				}
			}
			else{
				de.setType(DataElementType.HIDDEN);
			}
			de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_URL);
			de.setSize(this.getSize());
			dati.addElement(de);
			
//			if (endpointtype.equals(TipiConnettore.HTTPS.toString())) {
//				de = new DataElement();
//				de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_URL);
//				de.setValue(tmpUrl);
//				de.setType(DataElementType.HIDDEN);
//				de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_URL);
//				dati.addElement(de);
//			}

			if (endpointtype.equals(TipiConnettore.HTTP.toString()) || endpointtype.equals(TipiConnettore.HTTPS.toString())){
//				if(!ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT.equals(servletChiamante) &&
//						!ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT_RISPOSTA.equals(servletChiamante) ){
								
				de = new DataElement();
				de.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_HTTP);
				de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP);
				de.setType(DataElementType.CHECKBOX);
				if ( ServletUtils.isCheckBoxEnabled(autenticazioneHttp)) {
					de.setSelected(true);
				}
				de.setPostBack(true);
				dati.addElement(de);		
				//}
				
				de = new DataElement();
				de.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_PROXY);
				de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_ENABLED);
				de.setType(DataElementType.CHECKBOX);
				if ( ServletUtils.isCheckBoxEnabled(proxyEnabled)) {
					de.setSelected(true);
				}
				de.setPostBack(true);
				dati.addElement(de);
			}
			
			// Extended
			if(listExtendedConnettore!=null && listExtendedConnettore.size()>0){
				ServletExtendedConnettoreUtils.addToDatiEnabled(dati, listExtendedConnettore);
			}
			
			// opzioni avanzate
			if (endpointtype.equals(TipiConnettore.HTTP.toString()) || endpointtype.equals(TipiConnettore.HTTPS.toString())) {
				de = new DataElement();
				de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_OPZIONI_AVANZATE);
				de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE);
				if (this.isModalitaAvanzata()) {
					de.setType(DataElementType.CHECKBOX);
					de.setValue(opzioniAvanzate);
					if ( ServletUtils.isCheckBoxEnabled(opzioniAvanzate)) {
						de.setSelected(true);
					}
				}else{
					de.setType(DataElementType.HIDDEN);
					de.setValue(Costanti.CHECK_BOX_DISABLED);
				}
				de.setPostBack(true);
				dati.addElement(de);
			}
			
			// Autenticazione http
			if (ServletUtils.isCheckBoxEnabled(autenticazioneHttp)) {
				this.addCredenzialiToDati(dati, CostantiConfigurazione.CREDENZIALE_BASIC.getValue(), user, password, password, null,
						servletChiamante,true,endpointtype,true,false, prefix, true);
			}
			
			// Custom
			if (endpointtype != null && endpointtype.equals(TipiConnettore.CUSTOM.toString()) &&
					!servletChiamante.equals(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_ADD) &&
					!servletChiamante.equals(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_ADD) &&
					(isConnettoreCustomUltimaImmagineSalvata!=null && isConnettoreCustomUltimaImmagineSalvata)) {
				de = new DataElement();
				de.setType(DataElementType.LINK);
				de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_PROPRIETA);
				int numProp = 0;
				try {
					if (servletChiamante.equals(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE)) {
						de.setUrl(ConnettoriCostanti.SERVLET_NAME_CONNETTORI_CUSTOM_PROPERTIES_LIST,
								new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_SERVLET, servletChiamante),
								new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID, elem1),
								new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO, elem2),
								new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SERVIZIO, elem3),
								new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_VERSIONE_SERVIZIO, elem4));
						AccordoServizioParteSpecifica asps = this.apsCore.getAccordoServizioParteSpecifica(Long.parseLong(elem1));
						org.openspcoop2.core.registry.Connettore connettore = asps.getConfigurazioneServizio().getConnettore();
						if (connettore != null && (connettore.getCustom()!=null && connettore.getCustom()) ){
							for (int i = 0; i < connettore.sizePropertyList(); i++) {
								if(CostantiDB.CONNETTORE_DEBUG.equals(connettore.getProperty(i).getNome())==false  &&
										connettore.getProperty(i).getNome().startsWith(CostantiConnettori.CONNETTORE_EXTENDED_PREFIX)==false){
									numProp++;
								}
							}
							// Non devo contare la proprietà debug: numProp = connettore.sizePropertyList();
						}
					}
					if (servletChiamante.equals(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE)) {
						de.setUrl(ConnettoriCostanti.SERVLET_NAME_CONNETTORI_CUSTOM_PROPERTIES_LIST,
								new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_SERVLET, servletChiamante),
								new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID, elem1),
								new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_MY_ID, elem2),
								new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SOGGETTO_EROGATORE, elem3));
						int idServizioFruitoreInt = Integer.parseInt(elem2);
						Fruitore servFru = this.apsCore.getServizioFruitore(idServizioFruitoreInt);
						org.openspcoop2.core.registry.Connettore connettore = servFru.getConnettore();
						if (connettore != null && (connettore.getCustom()!=null && connettore.getCustom()) ){
							for (int i = 0; i < connettore.sizePropertyList(); i++) {
								if(CostantiDB.CONNETTORE_DEBUG.equals(connettore.getProperty(i).getNome())==false  &&
										connettore.getProperty(i).getNome().startsWith(CostantiConnettori.CONNETTORE_EXTENDED_PREFIX)==false){
									numProp++;
								}
							}
							// Non devo contare la proprietà debug: numProp = connettore.sizePropertyList();
						}
					}
					if (servletChiamante.equals(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT) ||
							servletChiamante.equals(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT_RISPOSTA)) {
						int idSilInt = Integer.parseInt(elem2);
						ServizioApplicativo sa = this.saCore.getServizioApplicativo(idSilInt);
						Soggetto soggetto = this.soggettiCore.getSoggettoRegistro(new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()));
						de.setUrl(ConnettoriCostanti.SERVLET_NAME_CONNETTORI_CUSTOM_PROPERTIES_LIST,
								new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_SERVLET, servletChiamante),
								new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO_APPLICATIVO, elem1),
								new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SERVIZIO_APPLICATIVO, elem2),
								new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER, soggetto.getId()+""));
						
						
						if (servletChiamante.equals(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT)) {
							InvocazioneServizio is = sa.getInvocazioneServizio();
							Connettore connettore = is.getConnettore();
							if(connettore.getCustom()==null || !connettore.getCustom()){
								// è cambiato il tipo
								de.setType(DataElementType.HIDDEN);
							}
							if (connettore != null && connettore.getCustom()!=null && connettore.getCustom()){
								for (int i = 0; i < connettore.sizePropertyList(); i++) {
									if(CostantiDB.CONNETTORE_DEBUG.equals(connettore.getProperty(i).getNome())==false  &&
											connettore.getProperty(i).getNome().startsWith(CostantiConnettori.CONNETTORE_EXTENDED_PREFIX)==false){
										numProp++;
									}
								}
								// Non devo contare la proprietà debug: numProp = connettore.sizePropertyList();
							}
						} else {
							RispostaAsincrona ra = sa.getRispostaAsincrona();
							Connettore connettore = ra.getConnettore();
							if(connettore.getCustom()==null || !connettore.getCustom()){
								// è cambiato il tipo
								de.setType(DataElementType.HIDDEN);
							}
							if (connettore != null && connettore.getCustom()!=null && connettore.getCustom()){
								for (int i = 0; i < connettore.sizePropertyList(); i++) {
									if(CostantiDB.CONNETTORE_DEBUG.equals(connettore.getProperty(i).getNome())==false  &&
											connettore.getProperty(i).getNome().startsWith(CostantiConnettori.CONNETTORE_EXTENDED_PREFIX)==false){
										numProp++;
									}
								}
								// Non devo contare la proprietà debug: numProp = connettore.sizePropertyList();
							}
						}
					}
					if (servletChiamante.equals(SoggettiCostanti.SERVLET_NAME_SOGGETTI_ENDPOINT)) {
						de.setUrl(ConnettoriCostanti.SERVLET_NAME_CONNETTORI_CUSTOM_PROPERTIES_LIST,
								new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_SERVLET, servletChiamante),
								new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID, elem1),
								new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SOGGETTO, elem2),
								new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SOGGETTO, elem3));
						int idInt = Integer.parseInt(elem1);
						SoggettoCtrlStat scs = this.soggettiCore.getSoggettoCtrlStat(idInt);
						Soggetto ss = scs.getSoggettoReg();
						org.openspcoop2.core.registry.Connettore connettore = ss.getConnettore();
						if (connettore != null && (connettore.getCustom()!=null && connettore.getCustom()) ){
							for (int i = 0; i < connettore.sizePropertyList(); i++) {
								if(CostantiDB.CONNETTORE_DEBUG.equals(connettore.getProperty(i).getNome())==false  &&
										connettore.getProperty(i).getNome().startsWith(CostantiConnettori.CONNETTORE_EXTENDED_PREFIX)==false){
									numProp++;
								}
							}
							// Non devo contare la proprietà debug: numProp = connettore.sizePropertyList();
						}
					}
				} catch (Exception ex) {
					this.log.error("Exception: " + ex.getMessage(), ex);
				}
				de.setValue(ConnettoriCostanti.LABEL_CONNETTORE_PROPRIETA+"("+numProp+")");
				dati.addElement(de);
			}
			
			// Https
			if (endpointtype.equals(TipiConnettore.HTTPS.toString())) {
				ConnettoreHTTPSUtils.addHTTPSDati(dati, httpsurl, httpstipologia, httpshostverify, httpspath, httpstipo, httpspwd, httpsalgoritmo, 
						httpsstato, httpskeystore, httpspwdprivatekeytrust, httpspathkey, httpstipokey, httpspwdkey, httpspwdprivatekey, httpsalgoritmokey, stato,
						this.core,this.getSize(), false, prefix);
			}

			// Jms
			if (endpointtype.equals(TipiConnettore.JMS.getNome())) {
				ConnettoreJMSUtils.addJMSDati(dati, nome, tipoconn, user, password, initcont, urlpgk, 
						provurl, connfact, sendas, objectName, tipoOperazione, stato,
						this.core,this.getSize());
			}
			
			// FileSystem
			if (endpointtype.equals(TipiConnettore.FILE.toString())) {
				ConnettoreFileUtils.addFileDati(dati, this.getSize(),
						requestOutputFileName,requestOutputFileNameHeaders,requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
						responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime
						);
			}
			
			// Proxy
			if (endpointtype.equals(TipiConnettore.HTTP.toString()) || endpointtype.equals(TipiConnettore.HTTPS.toString())){
				if (ServletUtils.isCheckBoxEnabled(proxyEnabled)) {
					this.addProxyToDati(dati, proxyHost, proxyPort, proxyUsername, proxyPassword);
				}
			}
			
			// Extended
			if(listExtendedConnettore!=null && listExtendedConnettore.size()>0){
				ServletExtendedConnettoreUtils.addToDatiExtendedInfo(dati, listExtendedConnettore);
			}
			
			// Opzioni Avanzate
			if (endpointtype.equals(TipiConnettore.HTTP.toString()) || endpointtype.equals(TipiConnettore.HTTPS.toString())){
				this.addOpzioniAvanzateHttpToDati(dati, opzioniAvanzate, transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop);
			}

		}


		return dati;
	}
	


	public Vector<DataElement> addEndPointToDatiAsHidden(Vector<DataElement> dati,
			String endpointtype, String url, String nome, String tipo,
			String user, String password, String initcont, String urlpgk,
			String provurl, String connfact, String sendas, String objectName, TipoOperazione tipoOperazione,
			String httpsurl, String httpstipologia, boolean httpshostverify,
			String httpspath, String httpstipo, String httpspwd,
			String httpsalgoritmo, boolean httpsstato, String httpskeystore,
			String httpspwdprivatekeytrust, String httpspathkey,
			String httpstipokey, String httpspwdkey,
			String httpspwdprivatekey, String httpsalgoritmokey,
			String tipoconn, String servletChiamante, String elem1, String elem2, String elem3,
			String elem4, String elem5, String elem6, String elem7, String stato,
			String requestOutputFileName,String requestOutputFileNameHeaders,String requestOutputParentDirCreateIfNotExists,String requestOutputOverwriteIfExists,
			String responseInputMode, String responseInputFileName, String responseInputFileNameHeaders, String responseInputDeleteAfterRead, String responseInputWaitTime
			) {


		Boolean confPers = (Boolean) this.session.getAttribute(CostantiControlStation.SESSION_PARAMETRO_GESTIONE_CONFIGURAZIONI_PERSONALIZZATE);

		TipologiaConnettori tipologiaConnettori = null;
		try {
			tipologiaConnettori = Utilities.getTipologiaConnettori(this.core);
		} catch (Exception e) {
			// default
			tipologiaConnettori = TipologiaConnettori.TIPOLOGIA_CONNETTORI_ALL;
		}

		// override tipologiaconnettori :
		// se standard allora la tipologia connettori e' sempre http
		// indipendentemente
		// dalla proprieta settata
		if (this.isModalitaStandard()) {
			tipologiaConnettori = TipologiaConnettori.TIPOLOGIA_CONNETTORI_HTTP;
		}

		DataElement de = new DataElement();
//		de.setLabel(ConnettoriCostanti.LABEL_CONNETTORE);
//		de.setType(DataElementType.TITLE);
//		dati.addElement(de);


		/** VISUALIZZAZIONE HTTP ONLY MODE */

		if (TipologiaConnettori.TIPOLOGIA_CONNETTORI_HTTP.equals(tipologiaConnettori)) {
			
			de = new DataElement();
			de.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_ABILITATO);

			de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_CHECK);
			if(!TipiConnettore.HTTP.toString().equals(endpointtype) &&
					!TipiConnettore.DISABILITATO.toString().equals(endpointtype)){
//				de.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_CONNETTORE_IMPOSTATO_MODALITA_AVANZATA);
				de.setType(DataElementType.HIDDEN);
//				de.setValue(" ");
				de.setValue(Costanti.CHECK_BOX_DISABLED);
			}
			else if( (  (AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS.equals(objectName) && TipoOperazione.CHANGE.equals(tipoOperazione))
					|| 
					(AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI.equals(objectName) && TipoOperazione.CHANGE.equals(tipoOperazione))
					)
					&& StatiAccordo.finale.toString().equals(stato) && this.core.isShowGestioneWorkflowStatoDocumenti() ){
				if (endpointtype.equals(TipiConnettore.HTTP.toString())) {
					de.setType(DataElementType.HIDDEN);
//					de.setSelected(true);
					de.setValue(Costanti.CHECK_BOX_ENABLED);
				}else{
					de.setType(DataElementType.HIDDEN);
					de.setLabel(TipiConnettore.DISABILITATO.toString());
					de.setValue(Costanti.CHECK_BOX_DISABLED);
				}
			}else{
				de.setType(DataElementType.HIDDEN);
				if (endpointtype.equals(TipiConnettore.HTTP.toString())) {
//					de.setSelected(true);
					de.setValue(Costanti.CHECK_BOX_ENABLED);
				}
//				de.setPostBack(true);
				//				de.setOnClick("AbilitaEndPointHTTP(\"" + tipoOp + "\")");
			}
			dati.addElement(de);

			de = new DataElement();
			de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE);
			if (endpointtype.equals(TipiConnettore.HTTP.toString())) {
				de.setValue(TipiConnettore.HTTP.toString());
			} else {
				de.setValue(TipiConnettore.DISABILITATO.toString());
			}
			de.setType(DataElementType.HIDDEN);
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_URL);
			String defaultPrefixValue = "http://";
			if (endpointtype.equals(TipiConnettore.HTTPS.toString())) {
				defaultPrefixValue = "https://";
			}
			de.setValue((url != null) && !"".equals(url) && !"http://".equals(url) && !"https://".equals(url) ? url : defaultPrefixValue);
			de.setType(DataElementType.HIDDEN);
			de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_URL);
			dati.addElement(de);

		} else {

			/** VISUALIZZAZIONE COMPLETA CONNETTORI MODE */


			int sizeEP = Connettori.getList().size();
			if (!Connettori.getList().contains(TipiConnettore.HTTPS.toString()))
				sizeEP++;
			if (confPers &&
					TipologiaConnettori.TIPOLOGIA_CONNETTORI_ALL.equals(tipologiaConnettori))
				sizeEP++;
			String[] tipoEP = new String[sizeEP];
			Connettori.getList().toArray(tipoEP);
			int newCount = Connettori.getList().size();
			if (!Connettori.getList().contains(TipiConnettore.HTTPS.toString())) {
				tipoEP[newCount] = TipiConnettore.HTTPS.toString();
				newCount++;
			}
			if (confPers &&
					TipologiaConnettori.TIPOLOGIA_CONNETTORI_ALL.equals(tipologiaConnettori))
				tipoEP[newCount] = TipiConnettore.CUSTOM.toString();
			//String[] tipoEP = { TipiConnettore.DISABILITATO.toString(), TipiConnettore.HTTP.toString(), TipiConnettore.JMS.toString(), TipiConnettore.NULL.toString(), TipiConnettore.NULL_ECHO.toString() };

			de = new DataElement();
			de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_ENDPOINT_TYPE);
			de.setType(DataElementType.HIDDEN);
			de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE);
//			de.setValues(tipoEP);
			de.setValue(endpointtype);
			//		    de.setOnChange("CambiaEndPoint('" + tipoOp + "')");
//			de.setPostBack(true);
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_URL);
			de.setValue(url);
//			if (endpointtype.equals(TipiConnettore.HTTP.toString())){
//				if (!this.core.isShowGestioneWorkflowStatoDocumenti() || !StatiAccordo.finale.toString().equals(stato)) {
//					de.setType(DataElementType.TEXT_EDIT);
//					de.setRequired(true);
//				} else {
//					de.setType(DataElementType.TEXT);
//				}
//			}
//			else{
				de.setType(DataElementType.HIDDEN);
//		}
			de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_URL);
			de.setSize(this.getSize());
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO);
//			if (endpointtype == null || !endpointtype.equals(TipiConnettore.CUSTOM.toString()))
				de.setType(DataElementType.HIDDEN);
//			else{
//				de.setType(DataElementType.TEXT_EDIT);
//				de.setRequired(true);
//			}
			de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO);
			de.setValue(tipoconn);
			dati.addElement(de);

//			if (endpointtype != null && endpointtype.equals(TipiConnettore.CUSTOM.toString()) &&
//					!servletChiamante.equals(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_ADD) &&
//					!servletChiamante.equals(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_ADD)) {
//				 Eliminati Link non dovrebbero servire... eventualmente riportare da su
//			}
		}

		if (endpointtype.equals(TipiConnettore.HTTPS.toString())) {
			ConnettoreHTTPSUtils.addHTTPSDatiAsHidden(dati, httpsurl, httpstipologia, httpshostverify, httpspath, httpstipo, httpspwd, 
					httpsalgoritmo, httpsstato, httpskeystore, httpspwdprivatekeytrust, httpspathkey, httpstipokey, httpspwdkey, httpspwdprivatekey, httpsalgoritmokey, stato,
					this.core,this.getSize());
			
		}

		if (endpointtype.equals(TipiConnettore.JMS.getNome())) {
			ConnettoreJMSUtils.addJMSDatiAsHidden(dati, nome, tipoconn, user, password, initcont, urlpgk, 
					provurl, connfact, sendas, objectName, tipoOperazione, stato,
					this.core,this.getSize());
		}

		if (endpointtype.equals(TipiConnettore.FILE.toString())) {
			ConnettoreFileUtils.addFileDatiAsHidden(dati, 
					requestOutputFileName,requestOutputFileNameHeaders,requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
					responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime);
			
			
		}
		
		return dati;
	}




	// Controlla i dati dell'end-point
	public boolean endPointCheckData(List<ExtendedConnettore> listExtendedConnettore) throws Exception {
		try {

			//String endpointtype = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE);
			String endpointtype = this.readEndPointType();
			String tipoconn = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO);
			String autenticazioneHttp = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP);
			
			// proxy
			String proxy_enabled = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_ENABLED);
			String proxy_hostname = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_HOSTNAME);
			String proxy_port = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PORT);
			String proxy_username = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_USERNAME);
			String proxy_password = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PASSWORD);

			// opzioni avanzate
			String transfer_mode = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_MODE);
			String transfer_mode_chunk_size = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_CHUNK_SIZE);
			String redirect_mode = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MODE);
			String redirect_max_hop = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MAX_HOP);
			String opzioniAvanzate = getOpzioniAvanzate(this,transfer_mode, redirect_mode);
						
			// http
			String url = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_URL);

			// jms
			String nome = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_NOME_CODA);
			String tipo = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_CODA);
			String user = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_USERNAME);
			String password = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PASSWORD);
			String initcont = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_INIT_CTX);
			String urlpgk = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_URL_PKG);
			String provurl = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PROVIDER_URL);
			String connfact = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_CONNECTION_FACTORY);
			String sendas = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_OGGETTO_JMS);

			// https
			String httpsurl = url;
			String httpstipologia = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_SSL_TYPE);
			String httpshostverifyS = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_HOST_VERIFY);
			boolean httpshostverify = ServletUtils.isCheckBoxEnabled(httpshostverifyS);
			String httpspath = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION);
			String httpstipo = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_TYPE);
			String httpspwd = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
			String httpsalgoritmo = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
			String httpsstatoS = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO);
			boolean httpsstato = ServletUtils.isCheckBoxEnabled(httpsstatoS);
			String httpskeystore = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE);
			String httpspwdprivatekeytrust = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_STORE);
			String httpspathkey = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_LOCATION);
			String httpstipokey = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_TYPE);
			String httpspwdkey = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
			String httpspwdprivatekey = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_KEYSTORE);
			String httpsalgoritmokey = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM);

			if(ServletUtils.isCheckBoxEnabled(autenticazioneHttp)){
				user = this.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				password = this.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			}
			
			// file
			String requestOutputFileName = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME);
			String requestOutputFileNameHeaders = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS);
			String requestOutputParentDirCreateIfNotExists = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR);
			String requestOutputOverwriteIfExists = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE_NAME);
			String responseInputMode = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_MODE);
			String responseInputFileName = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME);
			String responseInputFileNameHeaders = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_HEADERS);
			String responseInputDeleteAfterRead = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_DELETE_AFTER_READ);
			String responseInputWaitTime = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME);
			
			
			return endPointCheckData(endpointtype, url, nome, tipo, user,
					password, initcont, urlpgk, provurl, connfact, sendas,
					httpsurl, httpstipologia, httpshostverify,
					httpspath, httpstipo, httpspwd, httpsalgoritmo, httpsstato,
					httpskeystore, httpspwdprivatekeytrust, httpspathkey,
					httpstipokey, httpspwdkey, httpspwdprivatekey,
					httpsalgoritmokey, tipoconn, autenticazioneHttp,
					proxy_enabled,proxy_hostname,proxy_port,proxy_username,proxy_password,
					opzioniAvanzate, transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop,
					requestOutputFileName,requestOutputFileNameHeaders,requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
					responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
					listExtendedConnettore);
			
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	// Controlla i dati dell'end-point
	public boolean endPointCheckData(String endpointtype, String url, String nome,
			String tipo, String user, String password, String initcont,
			String urlpgk, String provurl, String connfact, String sendas,
			String httpsurl, String httpstipologia, boolean httpshostverify,
			String httpspath, String httpstipo, String httpspwd,
			String httpsalgoritmo, boolean httpsstato, String httpskeystore,
			String httpspwdprivatekeytrust, String httpspathkey,
			String httpstipokey, String httpspwdkey,
			String httpspwdprivatekey, String httpsalgoritmokey,
			String tipoconn, String autenticazioneHttp,
			String proxy_enabled, String proxy_hostname, String proxy_port, String proxy_username, String proxy_password,
			String opzioniAvanzate, String transfer_mode, String transfer_mode_chunk_size, String redirect_mode, String redirect_max_hop,
			String requestOutputFileName,String requestOutputFileNameHeaders,String requestOutputParentDirCreateIfNotExists,String requestOutputOverwriteIfExists,
			String responseInputMode, String responseInputFileName, String responseInputFileNameHeaders, String responseInputDeleteAfterRead, String responseInputWaitTime,
			List<ExtendedConnettore> listExtendedConnettore)
					throws Exception {
		try{
			
			if (url == null)
				url = "";
			if (nome == null)
				nome = "";
			if (tipo == null)
				tipo = "";
			if (user == null)
				user = "";
			if (password == null)
				password = "";
			if (initcont == null)
				initcont = "";
			if (urlpgk == null)
				urlpgk = "";
			if (provurl == null)
				provurl = "";
			if (connfact == null)
				connfact = "";
			if (sendas == null)
				sendas = "";
			if (httpsurl == null)
				httpsurl = "";
			if (httpstipologia == null)
				httpstipologia = "";
			if (httpspath == null)
				httpspath = "";
			if (httpstipo == null)
				httpstipo = "";
			if (httpspwd == null)
				httpspwd = "";
			if (httpsalgoritmo == null)
				httpsalgoritmo = "";
			if (httpskeystore == null)
				httpskeystore = "";
			if (httpspwdprivatekeytrust == null)
				httpspwdprivatekeytrust = "";
			if (httpspathkey == null)
				httpspathkey = "";
			if (httpstipokey == null)
				httpstipokey = "";
			if (httpspwdkey == null)
				httpspwdkey = "";
			if (httpspwdprivatekey == null)
				httpspwdprivatekey = "";
			if (httpsalgoritmokey == null)
				httpsalgoritmokey = "";
			if (tipoconn == null)
				tipoconn = "";	
			if (proxy_enabled == null)
				proxy_enabled = "";
			if (proxy_hostname == null)
				proxy_hostname = "";
			if (proxy_port == null)
				proxy_port = "";
			if (proxy_username == null)
				proxy_username = "";
			if (proxy_password == null)
				proxy_password = "";
			if (transfer_mode == null)
				transfer_mode = "";
			if (transfer_mode_chunk_size == null)
				transfer_mode_chunk_size = "";
			if (redirect_mode == null)
				redirect_mode = "";
			if (redirect_max_hop == null)
				redirect_max_hop = "";			
			if (requestOutputFileName == null)
				requestOutputFileName = "";
			if (requestOutputFileNameHeaders == null)
				requestOutputFileNameHeaders = "";
			if (responseInputFileName == null)
				responseInputFileName = "";
			if (responseInputFileNameHeaders == null)
				responseInputFileNameHeaders = "";
			if (responseInputWaitTime == null)
				responseInputWaitTime = "";
			
			// Controllo che non ci siano spazi nei campi di testo
			if ((url.indexOf(" ") != -1) || 
					(nome.indexOf(" ") != -1) ||
					(user.indexOf(" ") != -1) || 
					(password.indexOf(" ") != -1) || 
					(initcont.indexOf(" ") != -1) || 
					(urlpgk.indexOf(" ") != -1) || 
					(provurl.indexOf(" ") != -1) || 
					(connfact.indexOf(" ") != -1) ||
					(httpsurl.indexOf(" ") != -1) ||
					(httpspath.indexOf(" ") != -1) ||
					(httpspwd.indexOf(" ") != -1) ||
					(httpsalgoritmo.indexOf(" ") != -1) ||
					(httpskeystore.indexOf(" ") != -1) ||
					(httpspwdprivatekeytrust.indexOf(" ") != -1) ||
					(httpspathkey.indexOf(" ") != -1) ||
					(httpspwdkey.indexOf(" ") != -1) ||
					(httpspwdprivatekey.indexOf(" ") != -1) ||
					(httpsalgoritmokey.indexOf(" ") != -1) ||
					(tipoconn.indexOf(" ") != -1) ||
					(proxy_hostname.indexOf(" ") != -1) ||
					(proxy_port.indexOf(" ") != -1) ||
					(proxy_username.indexOf(" ") != -1) ||
					(proxy_password.indexOf(" ") != -1) ||
					(transfer_mode.indexOf(" ") != -1) ||
					(transfer_mode_chunk_size.indexOf(" ") != -1) ||
					(redirect_mode.indexOf(" ") != -1) ||
					(redirect_max_hop.indexOf(" ") != -1) ||
					(requestOutputFileName.indexOf(" ") != -1) ||
					(requestOutputFileNameHeaders.indexOf(" ") != -1) ||
					(responseInputFileName.indexOf(" ") != -1) ||
					(responseInputFileNameHeaders.indexOf(" ") != -1) ||
					(responseInputWaitTime.indexOf(" ") != -1) 
					) {
				this.pd.setMessage("Non inserire spazi nei campi di testo");
				return false;
			}
			
			if(ServletUtils.isCheckBoxEnabled(proxy_enabled)){
				
				if (proxy_hostname == null || "".equals(proxy_hostname)) {
					this.pd.setMessage(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_PROXY_HOSTNAME+" obbligatorio se si seleziona la comunicazione tramite Proxy");
					return false;
				}
				if (proxy_port == null || "".equals(proxy_port)) {
					this.pd.setMessage(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_PROXY_PORT+" obbligatorio se si seleziona la comunicazione tramite Proxy");
					return false;
				}
				int value = -1;
				try{
					value = Integer.parseInt(proxy_port);
					if(value<1 || value>65535){
						this.pd.setMessage(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_PROXY_PORT+" indicata per il Proxy deve essere un intero compreso tra 1 e  65.535");
						return false;
					}
				}catch(Exception e){
					this.pd.setMessage(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_PROXY_PORT+" indicata per il Proxy deve essere un numero intero: "+e.getMessage());
					return false;
				}
				
				try{
					new InetSocketAddress(proxy_hostname,value);
				}catch(Exception e){
					this.pd.setMessage(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_PROXY_HOSTNAME+" e "+
							ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_PROXY_PORT+" indicati per il Proxy non sono corretti: "+e.getMessage());
					return false;
				}
				
			}
			
			if(ServletUtils.isCheckBoxEnabled(opzioniAvanzate)){
				
				if(TransferLengthModes.TRANSFER_ENCODING_CHUNKED.getNome().equals(transfer_mode)){
					if(transfer_mode_chunk_size!=null && !"".equals(transfer_mode_chunk_size)){
						int value = -1;
						try{
							value = Integer.parseInt(transfer_mode_chunk_size);
							if(value<1){
								this.pd.setMessage("Il valore indicato nel parametro '"+ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_CHUNK_SIZE+
										"' indicato per la modalità "+TransferLengthModes.TRANSFER_ENCODING_CHUNKED.getNome()+" deve essere un numero maggiore di zero.");
								return false;
							}
						}catch(Exception e){
							this.pd.setMessage("Il valore indicato nel parametro '"+ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_CHUNK_SIZE+
									"' indicato per la modalità "+TransferLengthModes.TRANSFER_ENCODING_CHUNKED.getNome()+" deve essere un numero intero: "+e.getMessage());
							return false;
						}
					}
				}
				
				if(CostantiConfigurazione.ABILITATO.getValue().equals(redirect_mode)){
					if(redirect_max_hop!=null && !"".equals(redirect_max_hop)){
						int value = -1;
						try{
							value = Integer.parseInt(redirect_max_hop);
							if(value<1){
								this.pd.setMessage("Il valore indicato nel parametro '"+ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MAX_HOP+
										"' deve essere un numero maggiore di zero.");
								return false;
							}
						}catch(Exception e){
							this.pd.setMessage("Il valore indicato nel parametro '"+ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MAX_HOP+
									"' deve essere un numero intero: "+e.getMessage());
							return false;
						}
					}
				}
				
				
			}
			
			if(ServletUtils.isCheckBoxEnabled(autenticazioneHttp)){
				if (user == null || "".equals(user)) {
					this.pd.setMessage("Username obbligatoria per l'autenticazione http");
					return false;
				}
				if (password == null || "".equals(password)) {
					this.pd.setMessage("Password obbligatoria per l'autenticazione http");
					return false;
				}
			}

			// Controllo che i campi "select" abbiano uno dei valori ammessi
			if (!Connettori.contains(endpointtype) && !endpointtype.equals(TipiConnettore.CUSTOM.toString())) {
				this.pd.setMessage("Tipo Connettore dev'essere uno tra : " + Connettori.getList());
				return false;
			}

			List<String> tipologie = null;
			try{
				tipologie = SSLUtilities.getSSLSupportedProtocols();
			}catch(Exception e){
				ControlStationCore.logError(e.getMessage(), e);
				tipologie = SSLUtilities.getAllSslProtocol();
			}
			if(!tipologie.contains(SSLConstants.PROTOCOL_TLS)){
				tipologie.add(SSLConstants.PROTOCOL_TLS); // retrocompatibilita'
			}
			if(!tipologie.contains(SSLConstants.PROTOCOL_SSL)){
				tipologie.add(SSLConstants.PROTOCOL_SSL); // retrocompatibilita'
			}
			if (!httpstipologia.equals("") &&
					!tipologie.contains(httpstipologia)) {
				this.pd.setMessage("Il campo Tipologia può assumere uno tra i seguenti valori: "+tipologie);
				return false;
			}

			if (!httpstipo.equals("") &&
					!Utilities.contains(httpstipo, ConnettoriCostanti.TIPOLOGIE_KEYSTORE)) {
				this.pd.setMessage("Il campo Tipo per l'Autenticazione Server può assumere uno tra i seguenti valori: "+Utilities.toString(ConnettoriCostanti.TIPOLOGIE_KEYSTORE, ","));
				return false;
			}
			if (!httpstipokey.equals("") &&
					!Utilities.contains(httpstipokey, ConnettoriCostanti.TIPOLOGIE_KEYSTORE)) {
				this.pd.setMessage("Il campo Tipo per l'Autenticazione Client può assumere uno tra i seguenti valori: "+Utilities.toString(ConnettoriCostanti.TIPOLOGIE_KEYSTORE, ","));
				return false;
			}

			// Controllo campi obbligatori per il tipo di connettore custom
			if (endpointtype.equals(TipiConnettore.CUSTOM.toString()) && (tipoconn == null || "".equals(tipoconn))) {
				this.pd.setMessage(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO+" obbligatorio per il tipo di connettore custom");
				return false;
			}

			// Controllo campi obbligatori per il tipo di connettore http
			if (endpointtype.equals(TipiConnettore.HTTP.toString()) && (url == null || "".equals(url))) {
				this.pd.setMessage("Url obbligatoria per il tipo di connettore http");
				return false;
			}

			// Se il tipo di connettore è custom, tipoconn non può essere
			if (endpointtype.equals(TipiConnettore.CUSTOM.toString()) && (tipoconn.equals(TipiConnettore.HTTP.toString()) || tipoconn.equals(TipiConnettore.HTTPS.toString()) || tipoconn.equals(TipiConnettore.JMS.toString()) || tipoconn.equals(TipiConnettore.NULL.toString()) || tipoconn.equals(TipiConnettore.NULLECHO.toString()) || tipoconn.equals(TipiConnettore.DISABILITATO.toString()) )) {
				this.pd.setMessage(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO+" non può assumere i valori: disabilitato,http,https,jms,null,nullEcho");
				return false;
			}

			// Se e' stata specificata la url, dev'essere valida
			if (endpointtype.equals(TipiConnettore.HTTP.toString()) && !url.equals("") ){
				try{
					org.openspcoop2.utils.regexp.RegExpUtilities.validateUrl(url);
				}catch(Exception e){
					this.pd.setMessage("Url non correttamente formata: "+e.getMessage());
					return false;
				}
			}

			// Controllo campi obbligatori per il tipo di connettore jms
			if (endpointtype.equals(TipiConnettore.JMS.toString())) {
				if (nome == null || "".equals(nome)) {
					this.pd.setMessage("Nome della coda/topic obbligatorio per il tipo di connettore jms");
					return false;
				}
				if (tipo == null || "".equals(tipo)) {
					this.pd.setMessage("Tipo di coda obbligatorio per il tipo di connettore jms");
					return false;
				}
				if (sendas == null || "".equals(sendas)) {
					this.pd.setMessage("Tipo di messaggio (SendAs) obbligatorio per il tipo di connettore jms");
					return false;
				}
				if (connfact == null || "".equals(connfact)) {
					this.pd.setMessage("Connection Factory obbligatoria per il tipo di connettore jms");
					return false;
				}
			}

			if (endpointtype.equals(TipiConnettore.JMS.toString()) && !Utilities.contains(tipo, ConnettoriCostanti.TIPI_CODE_JMS)) {
				this.pd.setMessage("Tipo Jms dev'essere: "+Utilities.toString(ConnettoriCostanti.TIPI_CODE_JMS, ","));
				return false;
			}
			if (endpointtype.equals(TipiConnettore.JMS.toString()) && !Utilities.contains(sendas, ConnettoriCostanti.TIPO_SEND_AS) ) {
				this.pd.setMessage("Send As dev'essere: "+Utilities.toString(ConnettoriCostanti.TIPO_SEND_AS, ","));
				return false;
			}

			// Controllo campi obbligatori per il tipo di connettore https
			if (endpointtype.equals(TipiConnettore.HTTPS.toString())) {
				if ("".equals(httpsurl)) {
					this.pd.setMessage("Url obbligatorio per il tipo di connettore https");
					return false;
				}else{
					try{
						org.openspcoop2.utils.regexp.RegExpUtilities.validateUrl(httpsurl);
					}catch(Exception e){
						this.pd.setMessage("Url non correttamente formata: "+e.getMessage());
						return false;
					}
				}
				if ("".equals(httpspath)) {
					this.pd.setMessage("Il campo 'Path' è obbligatorio per l'Autenticazione Server");
					return false;
				}else{
					try{
						File f = new File(httpspath);
						f.getAbsolutePath();
					}catch(Exception e){
						this.pd.setMessage("Il campo 'Path', obbligatorio per l'Autenticazione Server, non è correttamente definito: "+e.getMessage());
						return false;
					}
				}
				if ("".equals(httpspwd)) {
					this.pd.setMessage("La password del TrustStore è necessaria per l'Autenticazione Server");
					return false;
				}
				if ("".equals(httpsalgoritmo)) {
					this.pd.setMessage("Il campo 'Algoritmo' è obbligatorio per l'Autenticazione Server");
					return false;
				}
				if (httpsstato) {
					if (ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT.equals(httpskeystore)) {
						if ("".equals(httpspwdprivatekeytrust)) {
							this.pd.setMessage("La password della chiave privata è necessaria in caso di Autenticazione Client abilitata");
							return false;
						}
						if ("".equals(httpsalgoritmokey)) {
							this.pd.setMessage("Il campo 'Algoritmo' è obbligatorio in caso di Autenticazione Client abilitata");
							return false;
						}
					} else {
						if ("".equals(httpspathkey)) {
							this.pd.setMessage("Il campo 'Path' è obbligatorio per l'Autenticazione Client, in caso di dati di accesso al KeyStore ridefiniti");
							return false;
						}else{
							try{
								File f = new File(httpspathkey);
								f.getAbsolutePath();
							}catch(Exception e){
								this.pd.setMessage("Il campo 'Path', obbligatorio per l'Autenticazione Client in caso di dati di accesso al KeyStore ridefiniti, non è correttamente definito: "+e.getMessage());
								return false;
							}
						}
						if ("".equals(httpspwdkey)) {
							this.pd.setMessage("La password del KeyStore è necessaria per l'Autenticazione Client, in caso di dati di accesso al KeyStore ridefiniti");
							return false;
						}
						if ("".equals(httpspwdprivatekey)) {
							this.pd.setMessage("La password della chiave privata è necessaria in caso di Autenticazione Client abilitata");
							return false;
						}
						if ("".equals(httpsalgoritmokey)) {
							this.pd.setMessage("Il campo 'Algoritmo' è obbligatorio per l'Autenticazione Client, in caso di dati di accesso al KeyStore ridefiniti");
							return false;
						}
					}
				}
			}
			
			// Controllo campi obbligatori per il tipo di connettore file
			if (endpointtype.equals(TipiConnettore.FILE.toString())) {
				
				if ("".equals(requestOutputFileName)) {
					this.pd.setMessage("'"+ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME+"' ("+
							ConnettoriCostanti.LABEL_CONNETTORE_REQUEST_OUTPUT+") obbligatorio per il tipo di connettore file");
					return false;
				}else{
					try{
						DynamicStringReplace.validate(requestOutputFileName);
					}catch(Exception e){
						this.pd.setMessage("Il valore indicato nel parametro '"+ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME+"' ("+
								ConnettoriCostanti.LABEL_CONNETTORE_REQUEST_OUTPUT+") non risulta corretto: "+e.getMessage());
						return false;
					}
				}
				
				if(requestOutputFileNameHeaders!=null && !"".equals(requestOutputFileNameHeaders)){
					try{
						DynamicStringReplace.validate(requestOutputFileNameHeaders);
					}catch(Exception e){
						this.pd.setMessage("Il valore indicato nel parametro '"+ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS+"' ("+
								ConnettoriCostanti.LABEL_CONNETTORE_REQUEST_OUTPUT+") non risulta corretto: "+e.getMessage());
						return false;
					}
				}
				
				if(CostantiConfigurazione.ABILITATO.getValue().equals(responseInputMode)){
					
					if ("".equals(responseInputFileName)) {
						this.pd.setMessage("'"+ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME+"' ("+
								ConnettoriCostanti.LABEL_CONNETTORE_RESPONSE_INPUT+") obbligatorio per il tipo di connettore file");
						return false;
					}else{
						try{
							DynamicStringReplace.validate(responseInputFileName);
						}catch(Exception e){
							this.pd.setMessage("Il valore indicato nel parametro '"+ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME+"' ("+
									ConnettoriCostanti.LABEL_CONNETTORE_RESPONSE_INPUT+") non risulta corretto: "+e.getMessage());
							return false;
						}
					}
					
					if(responseInputFileNameHeaders!=null && !"".equals(responseInputFileNameHeaders)){
						try{
							DynamicStringReplace.validate(responseInputFileNameHeaders);
						}catch(Exception e){
							this.pd.setMessage("Il valore indicato nel parametro '"+ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_HEADERS+"' ("+
									ConnettoriCostanti.LABEL_CONNETTORE_RESPONSE_INPUT+") non risulta corretto: "+e.getMessage());
							return false;
						}
					}
					
					if(responseInputWaitTime!=null && !"".equals(responseInputWaitTime)){
						int value = -1;
						try{
							value = Integer.parseInt(responseInputWaitTime);
							if(value<1){
								this.pd.setMessage("Il valore indicato nel parametro '"+ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME+
										"' ("+
									ConnettoriCostanti.LABEL_CONNETTORE_RESPONSE_INPUT+") deve essere un numero maggiore di zero.");
								return false;
							}
						}catch(Exception e){
							this.pd.setMessage("Il valore indicato nel parametro '"+ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME+
									"' ("+
								ConnettoriCostanti.LABEL_CONNETTORE_RESPONSE_INPUT+") deve essere un numero intero: "+e.getMessage());
							return false;
						}
					}
					
				}
			}
			
			
			try{
				ServletExtendedConnettoreUtils.checkInfo(listExtendedConnettore);
			}catch(Exception e){
				this.pd.setMessage(e.getMessage());
				return false;
			}
			
			return true;
			
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}


	public void fillConnettore(org.openspcoop2.core.registry.Connettore connettore,
			String connettoreDebug,
			String tipoConnettore, String oldtipo, String tipoconn, String http_url, String jms_nome,
			String jms_tipo, String user, String pwd,
			String jms_nf_initial, String jms_nf_urlPkg, String jms_np_url,
			String jms_connection_factory, String jms_send_as,
			String httpsurl, String httpstipologia, boolean httpshostverify,
			String httpspath, String httpstipo, String httpspwd,
			String httpsalgoritmo, boolean httpsstato, String httpskeystore,
			String httpspwdprivatekeytrust, String httpspathkey,
			String httpstipokey, String httpspwdkey,
			String httpspwdprivatekey, String httpsalgoritmokey,
			String proxyEnabled, String proxyHost, String proxyPort, String proxyUsername, String proxyPassword,
			String opzioniAvanzate, String transfer_mode, String transfer_mode_chunk_size, String redirect_mode, String redirect_max_hop,
			String requestOutputFileName,String requestOutputFileNameHeaders,String requestOutputParentDirCreateIfNotExists,String requestOutputOverwriteIfExists,
			String responseInputMode, String responseInputFileName, String responseInputFileNameHeaders, String responseInputDeleteAfterRead, String responseInputWaitTime,
			List<ExtendedConnettore> listExtendedConnettore)
					throws Exception {
		try {
			
			// azzero proprieta esistenti precedentemente
			// (se il connettore è custom lo faccio solo se prima
			// non era custom)
			if (!tipoConnettore.equals(TipiConnettore.CUSTOM.toString()) ||
					!tipoConnettore.equals(oldtipo)) {
				while(connettore.sizePropertyList()>0)
					connettore.removeProperty(0);
			}
			
			String debugValue = null;
			if(ServletUtils.isCheckBoxEnabled(connettoreDebug)){
				debugValue = "true";
			}
			else{
				debugValue = "false";
			}
			boolean found = false;
			for (int i = 0; i < connettore.sizePropertyList(); i++) {
				Property pCheck = connettore.getProperty(i);
				if(CostantiDB.CONNETTORE_DEBUG.equals(pCheck.getNome())){
					pCheck.setValore(debugValue);
					found = true;
					break;
				}
			}
			if(!found){
				Property p = new Property();
				p.setNome(CostantiDB.CONNETTORE_DEBUG);
				p.setValore(debugValue);
				connettore.addProperty(p);
			}

			org.openspcoop2.core.registry.Property prop = null;

			if (tipoConnettore.equals(TipiConnettore.CUSTOM.toString()))
				connettore.setTipo(tipoconn);
			else
				connettore.setTipo(tipoConnettore);
			// Inizializzo a false... Poi eventualmente lo setto a true
			connettore.setCustom(false);
			if (tipoConnettore.equals(TipiConnettore.HTTP.getNome())) {
				
				prop = new org.openspcoop2.core.registry.Property();
				prop.setNome(CostantiDB.CONNETTORE_HTTP_LOCATION);
				prop.setValore(http_url);
				connettore.addProperty(prop);
				
				if(user!=null){
					prop = new org.openspcoop2.core.registry.Property();
					prop.setNome(CostantiDB.CONNETTORE_USER);
					prop.setValore(user);
					connettore.addProperty(prop);
				}
				
				if(pwd!=null){
					prop = new org.openspcoop2.core.registry.Property();
					prop.setNome(CostantiDB.CONNETTORE_PWD);
					prop.setValore(pwd);
					connettore.addProperty(prop);
				}

			} else if (tipoConnettore.equals(TipiConnettore.JMS.getNome())) {
				ConnettoreJMSUtils.fillConnettoreRegistry(connettore, jms_nome, jms_tipo, user, pwd, 
						jms_nf_initial, jms_nf_urlPkg, jms_np_url, jms_connection_factory, jms_send_as);
			} else if (tipoConnettore.equals(TipiConnettore.NULL.getNome())) {
				// nessuna proprieta per connettore null
			} else if (tipoConnettore.equals(TipiConnettore.NULLECHO.getNome())) {
				// nessuna proprieta per connettore nullEcho
			} else if (tipoConnettore.equals(TipiConnettore.HTTPS.getNome())) {
				ConnettoreHTTPSUtils.fillConnettoreRegistry(connettore, httpsurl, httpstipologia, httpshostverify, httpspath, 
						httpstipo, httpspwd, httpsalgoritmo, httpsstato, httpskeystore, httpspwdprivatekeytrust, 
						httpspathkey, httpstipokey, httpspwdkey, httpspwdprivatekey, httpsalgoritmokey,
						user, pwd);
			} else if (tipoConnettore.equals(TipiConnettore.FILE.getNome())) {
				ConnettoreFileUtils.fillConnettoreRegistry(connettore, 
						requestOutputFileName,requestOutputFileNameHeaders,requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
						responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime);
			} else if (tipoConnettore.equals(TipiConnettore.CUSTOM.toString())) {
				connettore.setCustom(true);
			} else if (!tipoConnettore.equals(TipiConnettore.DISABILITATO.getNome()) &&
					!tipoConnettore.equals(TipiConnettore.CUSTOM.toString())) {
				Property [] cp = this.connettoriCore.getPropertiesConnettore(tipoConnettore);
				List<Property> cps = new ArrayList<Property>();
				if(cp!=null){
					for (int i = 0; i < cp.length; i++) {
						cps.add(cp[i]);
					}
				}
				connettore.setPropertyList(cps);
			}
			
			// Proxy
			if(ServletUtils.isCheckBoxEnabled(proxyEnabled)){
				
				prop = new org.openspcoop2.core.registry.Property();
				prop.setNome(CostantiDB.CONNETTORE_PROXY_TYPE);
				prop.setValore(CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE_VALUE_HTTP);
				connettore.addProperty(prop);
				
				if(proxyHost!=null && !"".equals(proxyHost)){
					prop = new org.openspcoop2.core.registry.Property();
					prop.setNome(CostantiDB.CONNETTORE_PROXY_HOSTNAME);
					prop.setValore(proxyHost);
					connettore.addProperty(prop);
				}
				
				if(proxyPort!=null && !"".equals(proxyPort)){
					prop = new org.openspcoop2.core.registry.Property();
					prop.setNome(CostantiDB.CONNETTORE_PROXY_PORT);
					prop.setValore(proxyPort);
					connettore.addProperty(prop);
				}
				
				if(proxyUsername!=null && !"".equals(proxyUsername)){
					prop = new org.openspcoop2.core.registry.Property();
					prop.setNome(CostantiDB.CONNETTORE_PROXY_USERNAME);
					prop.setValore(proxyUsername);
					connettore.addProperty(prop);
				}
				
				if(proxyPassword!=null && !"".equals(proxyPassword)){
					prop = new org.openspcoop2.core.registry.Property();
					prop.setNome(CostantiDB.CONNETTORE_PROXY_PASSWORD);
					prop.setValore(proxyPassword);
					connettore.addProperty(prop);
				}
				
			}
			
			// OpzioniAvanzate
			//if(ServletUtils.isCheckBoxEnabled(opzioniAvanzate)){ li devo impostare anche in caso di HIDDEN
				
			if(TransferLengthModes.CONTENT_LENGTH.getNome().equals(transfer_mode) ||
					TransferLengthModes.TRANSFER_ENCODING_CHUNKED.getNome().equals(transfer_mode)){
				
				// nel caso di default non devo creare la proprietà
				prop = new org.openspcoop2.core.registry.Property();
				prop.setNome(CostantiDB.CONNETTORE_HTTP_DATA_TRANSFER_MODE);
				prop.setValore(transfer_mode);
				connettore.addProperty(prop);
				
			}
			
			if(TransferLengthModes.TRANSFER_ENCODING_CHUNKED.getNome().equals(transfer_mode)){
				if(transfer_mode_chunk_size!=null && !"".equals(transfer_mode_chunk_size)){
					prop = new org.openspcoop2.core.registry.Property();
					prop.setNome(CostantiDB.CONNETTORE_HTTP_DATA_TRANSFER_MODE_CHUNK_SIZE);
					prop.setValore(transfer_mode_chunk_size);
					connettore.addProperty(prop);
				}
			}
			
			if(CostantiConfigurazione.ABILITATO.getValue().equals(redirect_mode) ||
					CostantiConfigurazione.DISABILITATO.getValue().equals(redirect_mode)){
				
				// nel caso di default non devo creare la proprietà
				prop = new org.openspcoop2.core.registry.Property();
				prop.setNome(CostantiDB.CONNETTORE_HTTP_REDIRECT_FOLLOW);
				prop.setValore(redirect_mode);
				connettore.addProperty(prop);
				
			}
			
			if(CostantiConfigurazione.ABILITATO.getValue().equals(redirect_mode)){
				if(redirect_max_hop!=null && !"".equals(redirect_max_hop)){
					prop = new org.openspcoop2.core.registry.Property();
					prop.setNome(CostantiDB.CONNETTORE_HTTP_REDIRECT_MAX_HOP);
					prop.setValore(redirect_max_hop);
					connettore.addProperty(prop);
				}
			}
				
			
			// Extended
			ExtendedConnettoreConverter.fillExtendedInfoIntoConnettore(listExtendedConnettore, connettore);

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}

	}


	public void fillConnettore(org.openspcoop2.core.config.Connettore connettore,
			String connettoreDebug,
			String tipoConnettore, String oldtipo, String tipoconn, String http_url, String jms_nome,
			String jms_tipo, String jms_user, String jms_pwd,
			String jms_nf_initial, String jms_nf_urlPkg, String jms_np_url,
			String jms_connection_factory, String jms_send_as,
			String httpsurl, String httpstipologia, boolean httpshostverify,
			String httpspath, String httpstipo, String httpspwd,
			String httpsalgoritmo, boolean httpsstato, String httpskeystore,
			String httpspwdprivatekeytrust, String httpspathkey,
			String httpstipokey, String httpspwdkey,
			String httpspwdprivatekey, String httpsalgoritmokey,
			String proxyEnabled, String proxyHost, String proxyPort, String proxyUsername, String proxyPassword,
			String opzioniAvanzate, String transfer_mode, String transfer_mode_chunk_size, String redirect_mode, String redirect_max_hop,
			String requestOutputFileName,String requestOutputFileNameHeaders,String requestOutputParentDirCreateIfNotExists,String requestOutputOverwriteIfExists,
			String responseInputMode, String responseInputFileName, String responseInputFileNameHeaders, String responseInputDeleteAfterRead, String responseInputWaitTime,
			List<ExtendedConnettore> listExtendedConnettore)
					throws Exception {
		try {
			
			// azzero proprieta esistenti precedentemente
			// (se il connettore è custom lo faccio solo se prima
			// non era custom)
			if (!tipoConnettore.equals(TipiConnettore.CUSTOM.toString()) ||
					!tipoConnettore.equals(oldtipo)) {
				while(connettore.sizePropertyList()>0)
					connettore.removeProperty(0);
			}

			
			String debugValue = null;
			if(ServletUtils.isCheckBoxEnabled(connettoreDebug)){
				debugValue = "true";
			}
			else{
				debugValue = "false";
			}
			boolean found = false;
			for (int i = 0; i < connettore.sizePropertyList(); i++) {
				org.openspcoop2.core.config.Property pCheck = connettore.getProperty(i);
				if(CostantiDB.CONNETTORE_DEBUG.equals(pCheck.getNome())){
					pCheck.setValore(debugValue);
					found = true;
					break;
				}
			}
			if(!found){
				org.openspcoop2.core.config.Property p = new org.openspcoop2.core.config.Property();
				p.setNome(CostantiDB.CONNETTORE_DEBUG);
				p.setValore(debugValue);
				connettore.addProperty(p);
			}
			
			org.openspcoop2.core.config.Property prop = null;

			if (tipoConnettore.equals(TipiConnettore.CUSTOM.toString()))
				connettore.setTipo(tipoconn);
			else
				connettore.setTipo(tipoConnettore);
			// Inizializzo a false... Poi eventualmente lo setto a true
			connettore.setCustom(false);
			if (tipoConnettore.equals(TipiConnettore.HTTP.getNome())) {
				prop = new org.openspcoop2.core.config.Property();
				prop.setNome(CostantiDB.CONNETTORE_HTTP_LOCATION);
				prop.setValore(http_url);
				connettore.addProperty(prop);

			} else if (tipoConnettore.equals(TipiConnettore.JMS.getNome())) {
				ConnettoreJMSUtils.fillConnettoreConfig(connettore, jms_nome, jms_tipo, jms_user, jms_pwd, 
						jms_nf_initial, jms_nf_urlPkg, jms_np_url, jms_connection_factory, jms_send_as);
			} else if (tipoConnettore.equals(TipiConnettore.NULL.getNome())) {
				// nessuna proprieta per connettore null
			} else if (tipoConnettore.equals(TipiConnettore.NULLECHO.getNome())) {
				// nessuna proprieta per connettore nullEcho
			} else if (tipoConnettore.equals(TipiConnettore.HTTPS.getNome())) {
				ConnettoreHTTPSUtils.fillConnettoreConfig(connettore, httpsurl, httpstipologia, httpshostverify, httpspath, httpstipo, 
						httpspwd, httpsalgoritmo, httpsstato, httpskeystore, httpspwdprivatekeytrust, httpspathkey, httpstipokey, 
						httpspwdkey, httpspwdprivatekey, httpsalgoritmokey);
			} else if (tipoConnettore.equals(TipiConnettore.FILE.getNome())) {
				ConnettoreFileUtils.fillConnettoreConfig(connettore, 
						requestOutputFileName,requestOutputFileNameHeaders,requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
						responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime);
			} else if (tipoConnettore.equals(TipiConnettore.CUSTOM.toString())) {
				connettore.setCustom(true);
			}else if (!tipoConnettore.equals(TipiConnettore.DISABILITATO.getNome()) &&
					!tipoConnettore.equals(TipiConnettore.CUSTOM.toString())) {
				org.openspcoop2.core.config.Property [] cp = this.connettoriCore.getPropertiesConnettoreConfig(tipoConnettore);
				List<org.openspcoop2.core.config.Property> cps = new ArrayList<org.openspcoop2.core.config.Property>();
				if(cp!=null){
					for (int i = 0; i < cp.length; i++) {
						cps.add(cp[i]);
					}
				}
				connettore.setPropertyList(cps);
			}
			
			// Proxy
			if(ServletUtils.isCheckBoxEnabled(proxyEnabled)){
				
				prop = new org.openspcoop2.core.config.Property();
				prop.setNome(CostantiDB.CONNETTORE_PROXY_TYPE);
				prop.setValore(CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE_VALUE_HTTP);
				connettore.addProperty(prop);
				
				if(proxyHost!=null && !"".equals(proxyHost)){
					prop = new org.openspcoop2.core.config.Property();
					prop.setNome(CostantiDB.CONNETTORE_PROXY_HOSTNAME);
					prop.setValore(proxyHost);
					connettore.addProperty(prop);
				}
				
				if(proxyPort!=null && !"".equals(proxyPort)){
					prop = new org.openspcoop2.core.config.Property();
					prop.setNome(CostantiDB.CONNETTORE_PROXY_PORT);
					prop.setValore(proxyPort);
					connettore.addProperty(prop);
				}
				
				if(proxyUsername!=null && !"".equals(proxyUsername)){
					prop = new org.openspcoop2.core.config.Property();
					prop.setNome(CostantiDB.CONNETTORE_PROXY_USERNAME);
					prop.setValore(proxyUsername);
					connettore.addProperty(prop);
				}
				
				if(proxyPassword!=null && !"".equals(proxyPassword)){
					prop = new org.openspcoop2.core.config.Property();
					prop.setNome(CostantiDB.CONNETTORE_PROXY_PASSWORD);
					prop.setValore(proxyPassword);
					connettore.addProperty(prop);
				}
				
			}
			
			// OpzioniAvanzate
			//if(ServletUtils.isCheckBoxEnabled(opzioniAvanzate)){ li devo impostare anche in caso di HIDDEN
				
			if(TransferLengthModes.CONTENT_LENGTH.getNome().equals(transfer_mode) ||
					TransferLengthModes.TRANSFER_ENCODING_CHUNKED.getNome().equals(transfer_mode)){
				
				// nel caso di default non devo creare la proprietà
				prop = new org.openspcoop2.core.config.Property();
				prop.setNome(CostantiDB.CONNETTORE_HTTP_DATA_TRANSFER_MODE);
				prop.setValore(transfer_mode);
				connettore.addProperty(prop);
				
			}
			
			if(TransferLengthModes.TRANSFER_ENCODING_CHUNKED.getNome().equals(transfer_mode)){
				if(transfer_mode_chunk_size!=null && !"".equals(transfer_mode_chunk_size)){
					prop = new org.openspcoop2.core.config.Property();
					prop.setNome(CostantiDB.CONNETTORE_HTTP_DATA_TRANSFER_MODE_CHUNK_SIZE);
					prop.setValore(transfer_mode_chunk_size);
					connettore.addProperty(prop);
				}
			}
			
			if(CostantiConfigurazione.ABILITATO.getValue().equals(redirect_mode) ||
					CostantiConfigurazione.DISABILITATO.getValue().equals(redirect_mode)){
				
				// nel caso di default non devo creare la proprietà
				prop = new org.openspcoop2.core.config.Property();
				prop.setNome(CostantiDB.CONNETTORE_HTTP_REDIRECT_FOLLOW);
				prop.setValore(redirect_mode);
				connettore.addProperty(prop);
				
			}
			
			if(CostantiConfigurazione.ABILITATO.getValue().equals(redirect_mode)){
				if(redirect_max_hop!=null && !"".equals(redirect_max_hop)){
					prop = new org.openspcoop2.core.config.Property();
					prop.setNome(CostantiDB.CONNETTORE_HTTP_REDIRECT_MAX_HOP);
					prop.setValore(redirect_max_hop);
					connettore.addProperty(prop);
				}
			}
				
			
			// Extended
			ExtendedConnettoreConverter.fillExtendedInfoIntoConnettore(listExtendedConnettore, connettore);

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}

	}

	public static String getOpzioniAvanzate(ConsoleHelper helper,String transfer_mode, String redirect_mode) throws Exception{

		String opzioniAvanzate = helper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE);
		return getOpzioniAvanzate(opzioniAvanzate, transfer_mode, redirect_mode);
		
	}
	public static String getOpzioniAvanzate(String opzioniAvanzate,String transfer_mode, String redirect_mode){
		
		if(opzioniAvanzate!=null && !"".equals(opzioniAvanzate)){
			return opzioniAvanzate;
		}
		
		if(opzioniAvanzate==null || "".equals(opzioniAvanzate)){
			opzioniAvanzate = Costanti.CHECK_BOX_DISABLED;
		}
		if( (transfer_mode!=null && !"".equals(transfer_mode)) || (redirect_mode!=null && !"".equals(redirect_mode)) ){
			opzioniAvanzate = Costanti.CHECK_BOX_ENABLED;
		}
		return opzioniAvanzate;
	}
	
	public boolean credenzialiCheckData() throws Exception{
		String tipoauth = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_TIPO_AUTENTICAZIONE);
		if (tipoauth == null) {
			tipoauth = ConnettoriCostanti.DEFAULT_AUTENTICAZIONE_TIPO;
		}
		String utente = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_USERNAME);
		String password = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
		// String confpw = this.getParameter("confpw");
		String subject = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_SUBJECT);
		String principal = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PRINCIPAL);
		
		if (tipoauth.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC) && (utente.equals("") || password.equals("") /*
		 * ||
		 * confpw
		 * .
		 * equals
		 * (
		 * ""
		 * )
		 */)) {
			String tmpElenco = "";
			if (utente.equals("")) {
				tmpElenco = ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_USERNAME;
			}
			if (password.equals("")) {
				if (tmpElenco.equals("")) {
					tmpElenco = ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD;
				} else {
					tmpElenco = tmpElenco + ", "+ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD;
				}
			}
			/*
			 * if (confpw.equals("")) { if (tmpElenco.equals("")) {
			 * tmpElenco = "Conferma password"; } else { tmpElenco =
			 * tmpElenco + ", Conferma password"; } }
			 */
			this.pd.setMessage("Dati incompleti. E' necessario indicare: " + tmpElenco);
			return false;
		}
		if (tipoauth.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL)){
			if (subject.equals("")) {
				this.pd.setMessage("Dati incompleti. E' necessario indicare il "+
						ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_SUBJECT);
				return false;
			}else{
				try{
					org.openspcoop2.utils.Utilities.validaSubject(subject);
				}catch(Exception e){
					this.pd.setMessage("Le credenziali di tipo ssl  possiedono un subject non valido: "+e.getMessage());
					return false;
				}
			}
		}
		
		if (tipoauth.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_PRINCIPAL) && principal.equals("") ) {
			String tmpElenco = "";
			if (principal.equals("")) {
				tmpElenco = ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PRINCIPAL;
			}
			this.pd.setMessage("Dati incompleti. E' necessario indicare: " + tmpElenco);
			return false;
		}
		
		if (tipoauth.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC) && ((utente.indexOf(" ") != -1) || (password.indexOf(" ") != -1))) {
			this.pd.setMessage("Non inserire spazi nei campi di testo");
			return false;
		}
		
		if (!tipoauth.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC) && 
				!tipoauth.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL) && 
				!tipoauth.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_PRINCIPAL) && 
				!tipoauth.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA)) {
			this.pd.setMessage("Tipo dev'essere "+
					ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC+", "+
					ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL+" o "+
					ConnettoriCostanti.AUTENTICAZIONE_TIPO_PRINCIPAL+" o "+
					ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA);
			return false;
		}
		
		return true;
	}
}
