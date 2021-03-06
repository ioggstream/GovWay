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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * serviziPorteAppDel
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteSpecificaPorteApplicativeDel extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Salvo il vecchio PageData
		PageData pdold = ServletUtils.getPageDataFromSession(session);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);
			/*
			 * Validate the request parameters specified by the user Note: Basic
			 * field validation done in porteDomForm.java Business logic
			 * validation done in porteDomAdd.java
			 */
			String id = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID);
			long idServizioLong = Long.parseLong(id);
			String objToRemove = apsHelper.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);
			// gli id contenuti nell'array sono i nomi delle porte applicative
			// da rimuovere
			ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);
			// prendo l'id del soggetto erogatore lo propago
			// lo metto nel pd come campo hidden
			// PageData oldPD = (PageData) session.getAttribute("PageData");
			pd.setHidden(pdold.getHidden());

			// Preparo il menu
			apsHelper.makeMenu();

			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore();
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore(apsCore);
			ServiziApplicativiCore saCore = new ServiziApplicativiCore(apsCore);
			ConfigurazioneCore confCore = new ConfigurazioneCore(apsCore);

			// Elimino le porte applicative del servizio dal db
			// StringTokenizer objTok = new StringTokenizer(objToRemove, ",");
			// int[] idToRemove = new int[objTok.countTokens()];
			//
			// int k = 0;
			// while (objTok.hasMoreElements()) {
			// idToRemove[k++] = Integer.parseInt(objTok.nextToken());
			// }

			// Prendo l'id del soggetto erogatore del servizio
			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(idServizioLong);
			IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps); 

			String superUser   = ServletUtils.getUserLoginFromSession(session);

			String errMsg = null;
			for (int i = 0; i < idsToRemove.size(); i++) {

				List<Object> listaOggettiDaEliminare = new ArrayList<Object>();
				// ricevo come parametro l'id della pa associata al mapping da cancellare
				IDPortaApplicativa idPortaApplicativa = new IDPortaApplicativa();
				idPortaApplicativa.setNome(idsToRemove.get(i)); 
				
				// leggo la pa
				PortaApplicativa tmpPA = porteApplicativeCore.getPortaApplicativa(idPortaApplicativa);
				// controllo se il mapping e' di default, se lo e' salto questo elemento
				
				boolean isDefault = apsCore.isDefaultMappingErogazione(idServizio, idPortaApplicativa );
				
				if(!isDefault) {
					//cancello il mapping
					MappingErogazionePortaApplicativa mappingErogazione = new MappingErogazionePortaApplicativa();
					mappingErogazione.setIdServizio(idServizio);
					mappingErogazione.setIdPortaApplicativa(idPortaApplicativa);
					listaOggettiDaEliminare.add(mappingErogazione);
					
					// cancello per policy associate alla porta se esistono
					List<AttivazionePolicy> listAttivazione = confCore.attivazionePolicyList(new Search(true), RuoloPolicy.APPLICATIVA, tmpPA.getNome());
					if(listAttivazione!=null && !listAttivazione.isEmpty()) {
						listaOggettiDaEliminare.addAll(listAttivazione);
					}
					
					// cancello la porta associata
					listaOggettiDaEliminare.add(tmpPA);
					
					for (PortaApplicativaServizioApplicativo paSA : tmpPA.getServizioApplicativoList()) {
						if(paSA.getNome().equals(tmpPA.getNome())) {
							IDServizioApplicativo idSA = new IDServizioApplicativo();
							idSA.setIdSoggettoProprietario(new IDSoggetto(tmpPA.getTipoSoggettoProprietario(), tmpPA.getNomeSoggettoProprietario()));
							idSA.setNome(paSA.getNome());
							ServizioApplicativo saGeneratoAutomaticamente = saCore.getServizioApplicativo(idSA);
							listaOggettiDaEliminare.add(saGeneratoAutomaticamente);
						}
					}
					
					// Elimino entrambi gli oggetti
					apsCore.performDeleteOperation(superUser, apsHelper.smista(), listaOggettiDaEliminare.toArray(new Object[1]));
				} else {
					errMsg = AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_IMPOSSIBILE_ELIMINARE_LA_CONFIGURAZIONE_DI_DEFAULT_EROGAZIONE;
				}
			}// for
			
			if(errMsg != null)
				pd.setMessage(errMsg);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			int idLista = Liste.CONFIGURAZIONE_EROGAZIONE;

			ricerca = apsHelper.checkSearchParameters(idLista, ricerca);

			
			List<MappingErogazionePortaApplicativa> lista = apsCore.mappingServiziPorteAppList(idServizio,asps.getId(), ricerca);
			apsHelper.prepareServiziConfigurazioneList(lista, id, null, ricerca);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_PORTE_APPLICATIVE, 
					ForwardParams.DEL());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_PORTE_APPLICATIVE,
					ForwardParams.DEL());
		}  
	}
}
