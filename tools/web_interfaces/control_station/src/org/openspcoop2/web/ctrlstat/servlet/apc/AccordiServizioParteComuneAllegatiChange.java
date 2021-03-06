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

import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.constants.ProprietariDocumento;
import org.openspcoop2.core.registry.constants.RuoliDocumento;
import org.openspcoop2.core.registry.constants.TipiDocumentoCoordinamento;
import org.openspcoop2.core.registry.constants.TipiDocumentoSemiformale;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.FileUploadForm;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.api.ApiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCore;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviHelper;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * accordiAllegatiChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteComuneAllegatiChange extends Action {

	@SuppressWarnings("incomplete-switch")
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = (String) ServletUtils.getUserLoginFromSession(session);

		try {
			
			FileUploadForm fileUpload = (FileUploadForm) form;
			
			AccordiServizioParteComuneHelper apcHelper = new AccordiServizioParteComuneHelper(request, pd, session);
			ArchiviHelper archiviHelper = new ArchiviHelper(request, pd, session);

			String idAllegato = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_ID_ALLEGATO);
			String idAccordo = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_ID_ACCORDO);
			String nomeDocumento = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_NOME_DOCUMENTO);
			String tipoFile = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_TIPO_FILE);
			
			int idAllegatoInt = Integer.parseInt(idAllegato);
			int idAccordoInt = Integer.parseInt(idAccordo);
			
			String tipoAccordo = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO);
			if("".equals(tipoAccordo))
				tipoAccordo = null;
			
			FormFile ff = fileUpload.getTheFile();

			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore();
			ArchiviCore archiviCore = new ArchiviCore(apcCore);

			// Preparo il menu
			apcHelper.makeMenu();

			// Prendo il nome
			AccordoServizioParteComune as = apcCore.getAccordoServizio(Long.valueOf(idAccordoInt));
			String labelASTitle = apcHelper.getLabelIdAccordo(as); 
			
			Documento doc = archiviCore.getDocumento(idAllegatoInt,false);
			
			IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();
			String uri = idAccordoFactory.getUriFromAccordo(as);
			Parameter pTipoAccordo = AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo);
			Parameter pIdAccordo = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, idAccordo);
			Parameter pNomeAccordo = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME, uri);
			
			Boolean isModalitaVistaApiCustom = ServletUtils.getBooleanAttributeFromSession(ApiCostanti.SESSION_ATTRIBUTE_VISTA_APC_API, session, false);
			List<Parameter> listaParams = apcHelper.getTitoloApc(TipoOperazione.ADD, as, tipoAccordo, labelASTitle, null, false);
			
			String labelAllegati = isModalitaVistaApiCustom ? AccordiServizioParteComuneCostanti.LABEL_ALLEGATI : AccordiServizioParteComuneCostanti.LABEL_ALLEGATI + " di " + labelASTitle;
			 
			listaParams.add(new Parameter(labelAllegati, AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_ALLEGATI_LIST, pIdAccordo, pNomeAccordo, pTipoAccordo));
			listaParams.add(new Parameter(nomeDocumento, null));
			
			// Se idhid = null, devo visualizzare la pagina per la
			// modifica dati
			if(apcHelper.isEditModeInProgress()){
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, listaParams);
				
				// preparo i campi
				Vector<Object> dati = new Vector<Object>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				apcHelper.addAccordiAllegatiToDati(dati,TipoOperazione.CHANGE,idAccordo,
						null,null,null,null,tipoAccordo,
						idAllegato,doc,as,null,null);
							
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_ALLEGATI, ForwardParams.CHANGE());
			}

			Documento toCheck = new Documento();
			toCheck.setRuolo(RuoliDocumento.valueOf(doc.getRuolo()).toString());
			toCheck.setByteContenuto(ff.getFileData());
			toCheck.setFile(ff.getFileName());
			switch (RuoliDocumento.valueOf(doc.getRuolo())) {
				case allegato:
					toCheck.setTipo(ff.getFileName().substring(ff.getFileName().lastIndexOf('.')+1, ff.getFileName().length()));
					break;
				case specificaSemiformale:
					toCheck.setTipo(TipiDocumentoSemiformale.toEnumConstant(tipoFile).getNome());
					break;
				case specificaCoordinamento:
					toCheck.setTipo(TipiDocumentoCoordinamento.toEnumConstant(tipoFile).getNome());
					break;
			}
			toCheck.setIdProprietarioDocumento(as.getId());
			toCheck.setOraRegistrazione(new Date());
			toCheck.setId(doc.getId());
			
			// Controlli sui campi immessi
			boolean isOk = archiviHelper.accordiAllegatiCheckData(TipoOperazione.CHANGE,ff,toCheck,ProprietariDocumento.accordoServizio);
			if (!isOk) {
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, listaParams);
				
				// preparo i campi
				Vector<Object> dati = new Vector<Object>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				apcHelper.addAccordiAllegatiToDati(dati,TipoOperazione.CHANGE,idAccordo,
						null,null,null,null,tipoAccordo,
						idAllegato,doc,as,null,null);
								
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_ALLEGATI, ForwardParams.CHANGE());
			}

			

			switch (RuoliDocumento.valueOf(doc.getRuolo())) {
				case allegato:
					//rimuovo il vecchio doc dalla lista
					for (int i = 0; i < as.sizeAllegatoList(); i++) {
						Documento documento = as.getAllegato(i);						
						if(documento.getId().equals(doc.getId()))
							as.removeAllegato(i);
					}
					//aggiungo il nuovo
					as.addAllegato(toCheck);
					
					break;

				case specificaSemiformale:
					
					for (int i = 0; i < as.sizeSpecificaSemiformaleList(); i++) {
						Documento documento = as.getSpecificaSemiformale(i);						
						if(documento.getId().equals(doc.getId())){
							as.removeSpecificaSemiformale(i);
							break;
						}
					}
					//aggiungo il nuovo
					as.addSpecificaSemiformale(toCheck);
					break;
					
				case specificaCoordinamento:
					AccordoServizioParteComuneServizioComposto assc = as.getServizioComposto();
					for (int i = 0; i < assc.sizeSpecificaCoordinamentoList(); i++) {
						Documento documento = assc.getSpecificaCoordinamento(i);						
						if(documento.getId().equals(doc.getId())){
							assc.removeSpecificaCoordinamento(i);
							break;
						}
					}
					assc.addSpecificaCoordinamento(toCheck);
					as.setServizioComposto(assc);
					break;
			}
			

			// effettuo le operazioni
			apcCore.performUpdateOperation(userLogin, apcHelper.smista(), as);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
			
			List<Documento> lista = apcCore.accordiAllegatiList(as.getId().intValue(), ricerca);

			apcHelper.prepareAccordiAllegatiList(as, ricerca, lista, tipoAccordo);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_ALLEGATI, ForwardParams.CHANGE());
			
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_ALLEGATI, ForwardParams.CHANGE());
		} 
	}
}
