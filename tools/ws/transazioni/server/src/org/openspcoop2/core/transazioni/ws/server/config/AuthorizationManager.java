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

package org.openspcoop2.core.transazioni.ws.server.config;

import java.util.Properties;

import org.openspcoop2.generic_project.exception.ServiceException;

/**     
 * AuthorizationManager
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 13574 $, $Date: 2018-01-26 12:24:34 +0100 (Fri, 26 Jan 2018) $
 */

public class AuthorizationManager extends org.openspcoop2.generic_project.utils.AuthorizationManager {

	// ------- Static -------------
	
	private static AuthorizationManager authorizationManager = null;
	private static synchronized void initAuthorizationManager() throws ServiceException{
		if(AuthorizationManager.authorizationManager==null){
			AuthorizationManager.authorizationManager = new AuthorizationManager();
		}
	}
	public static AuthorizationManager getAuthorizationManager() throws ServiceException{
		if(AuthorizationManager.authorizationManager==null){
			AuthorizationManager.initAuthorizationManager();
		}
		return AuthorizationManager.authorizationManager;
	}
	
	
	
	// ------- Instance -------------
	
	
	public AuthorizationManager() throws ServiceException{
		super(AuthorizationManager.getProperties());
	}
	
	private static Properties getProperties() throws ServiceException{
		try{
			return ServerProperties.getInstance().getProperties();
		}catch(Exception e){
			throw new ServiceException(e.getMessage(),e);
		}
	}
	
}