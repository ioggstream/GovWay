/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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


package org.openspcoop2.web.ctrlstat.plugins.servlet;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.DBManager;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedBean;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedListServlet;
import org.openspcoop2.web.ctrlstat.plugins.WrapperExtendedBean;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * AbstractServletListExtendedDel
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author: mergefairy $
 * @version $Rev: 10491 $, $Date: 2015-01-13 10:33:50 +0100 (Tue, 13 Jan 2015) $
 * 
 */
public abstract class AbstractServletListExtendedDel extends AbstractServletListUtilities {

	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Salvo il vecchio PageData
		// PageData pdold = (PageData) session.getAttribute("PageData");


		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			ConsoleHelper consoleHelper = this.getConsoleHelper(request, pd, session);
			
			String objToRemove = request.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);
			ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);
			
			ControlStationCore consoleCore = this.getConsoleCore();
			
			IExtendedListServlet extendedServlet = this.getExtendedServlet(consoleCore);
			
			Object object = this.getObject(consoleCore,request);
			
			DBManager dbManager = null;
			Connection con = null;
			List<WrapperExtendedBean> listDati = new ArrayList<WrapperExtendedBean>();
			try{
				dbManager = DBManager.getInstance();
				con = dbManager.getConnection();
				
				for (int i = 0; i < idsToRemove.size(); i++) {

					IExtendedBean extendedBean = extendedServlet.getExtendedBean(con, idsToRemove.get(i));
					
					WrapperExtendedBean wrapper = new WrapperExtendedBean();
					wrapper.setExtendedBean(extendedBean);
					wrapper.setExtendedServlet(extendedServlet);
					wrapper.setOriginalBean(object);
					wrapper.setManageOriginalBean(false);
					listDati.add(wrapper);
					
				}
				
			}finally{
				dbManager.releaseConnection(con);
			}
			
			
			String userLogin = ServletUtils.getUserLoginFromSession(session);
			consoleCore.performDeleteOperation(userLogin, consoleHelper.smista(), (Object[]) listDati.toArray(new WrapperExtendedBean[1]));

			// Preparo il menu
			consoleHelper.makeMenu();

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
			int idLista = this.getIdList();
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ("undefined".equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));
			List<IExtendedBean> lista = extendedServlet.extendedBeanList(object, limit, offset, search);
			ricerca.setNumEntries(idLista,lista.size());

			this.prepareList(consoleHelper, ricerca, object, extendedServlet, lista, ControlStationCore.getLog(), request);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			// Forward control to the specified success URI
		 	return ServletUtils.getStrutsForward(mapping, this.getObjectName(),
		 			ForwardParams.DEL());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					this.getObjectName(), 
					ForwardParams.DEL());
		}  
	}
}
