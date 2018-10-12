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


package org.openspcoop2.web.ctrlstat.servlet.apc;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * accordiPorttypeDel
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteComuneResourcesDel extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Salvo il vecchio PageData
		// PageData pdold = (PageData) session.getAttribute("PageData");

		// Inizializzo PageData
		PageData pd = new PageData();

		// Inizializzo GeneralData
		GeneralHelper generalHelper = new GeneralHelper(session);

		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = (String) ServletUtils.getUserLoginFromSession(session);

		IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();
		
		try {
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore();
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(apcCore);
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore(apcCore);
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore(apcCore);
			AccordiServizioParteComuneHelper apcHelper = new AccordiServizioParteComuneHelper(request, pd, session);

			String id = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
			int idInt = Integer.parseInt(id);
			String objToRemove = apcHelper.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);
			String tipoAccordo = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO);
			if("".equals(tipoAccordo))
				tipoAccordo = null;

			// Preparo il menu
			apcHelper.makeMenu();

			// Elimino i port-types dell'accordo dal db
			// StringTokenizer objTok = new StringTokenizer(objToRemove, ",");
			// int[] idToRemove = new int[objTok.countTokens()];
			//
			// int k = 0;
			// while (objTok.hasMoreElements()) {
			// idToRemove[k++] = Integer.parseInt(objTok.nextToken());
			// }
			ArrayList<String> resourcesToRemove = Utilities.parseIdsToRemove(objToRemove);
			AccordoServizioParteComune as = apcCore.getAccordoServizio(Long.valueOf(idInt));
//			IDResource idRisorsa = new IDResource();
//			idRisorsa.setIdAccordo(idAccordoFactory.getIDAccordoFromAccordo(as));
			
			List<IDServizio> idServiziWithAccordo = null;
			try{
				idServiziWithAccordo = apsCore.getIdServiziWithAccordo(idAccordoFactory.getIDAccordoFromAccordo(as),true);
			}catch(DriverRegistroServiziNotFound dNotF){}
			
			String nomeRisorsa = "";
			boolean modificaAS_effettuata = false;
			StringBuffer errori = new StringBuffer();
			for (int i = 0; i < resourcesToRemove.size(); i++) {
				nomeRisorsa = resourcesToRemove.get(i);
				
				// traduco nomeRisorsa in path
				String methodPath = null;
				for (int j = 0; j < as.sizeResourceList(); j++) {
					Resource risorsa = as.getResource(j);
					if (nomeRisorsa.equals(risorsa.getNome())) {
						methodPath = NamingUtils.getLabelResource(risorsa);
						break;
					}
				}
				if(methodPath==null) {
					methodPath = nomeRisorsa;
				}
				
				// Se esiste un mapping
				List<MappingErogazionePortaApplicativa> lPA = porteApplicativeCore.getMappingConGruppiPerAzione(nomeRisorsa, idServiziWithAccordo);
				if(lPA!=null && !lPA.isEmpty()) {
					if(errori.length()>0) {
						errori.append("<BR>");
					}
					errori.append("Risorsa '"+methodPath+"' non rimuovibile poichè riassegnata in un gruppo dell'erogazione del servizio: <BR>");
					for(int j=0;j<lPA.size();j++){
						errori.append("- "+lPA.get(j).getIdServizio()+" (gruppo: '"+lPA.get(j).getDescrizione()+"')<BR>");
					}
					continue;
				}
				List<MappingFruizionePortaDelegata> lPD = porteDelegateCore.getMappingConGruppiPerAzione(nomeRisorsa, idServiziWithAccordo);
				if(lPD!=null && !lPD.isEmpty()) {
					if(errori.length()>0) {
						errori.append("<BR>");
					}
					errori.append("Risorsa '"+methodPath+"' non rimuovibile poichè riassegnata in un gruppo della fruizione del servizio: <BR>");
					for(int j=0;j<lPD.size();j++){
						errori.append("- "+lPD.get(j).getIdServizio()+" (fruitore: "+lPD.get(j).getIdFruitore()+") (gruppo: '"+lPD.get(j).getDescrizione()+"')<BR>");
					}
					continue;
				}
				
				// Controllo che l'azione non sia in uso (se esistono servizi, allora poi saranno state create PD o PA)
				if(idServiziWithAccordo!=null && idServiziWithAccordo.size()>0){
					
					// Se esiste solo un'azione con tale identificativo, posso effettuare il controllo che non vi siano porteApplicative/porteDelegate esistenti.
					if (porteApplicativeCore.existsPortaApplicativaAzione(nomeRisorsa)) {
						List<IDPortaApplicativa> idPAs = porteApplicativeCore.getPortaApplicativaAzione(nomeRisorsa);
						if(errori.length()>0) {
							errori.append("<BR>");
						}
						errori.append("Risorsa '"+methodPath+"' non rimuovibile poichè in uso in porte applicative: <BR>");
						for(int j=0;j<idPAs.size();j++){
							errori.append("- "+idPAs.get(j).toString()+"<BR>");
						}
						continue;
					}
					if (porteDelegateCore.existsPortaDelegataAzione(nomeRisorsa)) {
						List<IDPortaDelegata> idPDs = porteDelegateCore.getPortaDelegataAzione(nomeRisorsa);
						if(errori.length()>0) {
							errori.append("<BR>");
						}
						errori.append("Risorsa '"+methodPath+"' non rimuovibile poichè in uso in porte delegate: <BR>");
						for(int j=0;j<idPDs.size();j++){
							errori.append("- "+idPDs.get(j).toString()+"<BR>");
						}
						continue;
					}
					
				}
				
				// Effettuo eliminazione
				for (int j = 0; j < as.sizeResourceList(); j++) {
					Resource risorsa = as.getResource(j);
					if (nomeRisorsa.equals(risorsa.getNome())) {
						modificaAS_effettuata = true;
						as.removeResource(j);
						break;
					}
				}
			}
			
			// imposto msg di errore se presente
			if(errori.length()>0)
				pd.setMessage(errori.toString());

			// effettuo le operazioni
			if(modificaAS_effettuata)
				apcCore.performUpdateOperation(userLogin, apcHelper.smista(), as);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			List<Resource> lista = apcCore.accordiResourceList(idInt, ricerca);

			// Devo rileggere l'accordo dal db, perche' altrimenti
			// manca l'id dei port-type
			as = apcCore.getAccordoServizio(Long.valueOf(idInt));

			// Verifico stato
			apcHelper.setMessageWarningStatoConsistenzaAccordo(false, as);
			
			apcHelper.prepareAccordiResourcesList(id,as, lista, ricerca, tipoAccordo);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			
			return ServletUtils.getStrutsForward(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_RESOURCES, ForwardParams.DEL());
			
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_RESOURCES, ForwardParams.DEL());
		} 
	}
}
