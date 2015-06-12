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
package org.openspcoop2.generic_project.web.impl.jsf1.mbean.exception;

import org.openspcoop2.generic_project.web.impl.jsf1.exception.BaseException;

/***
 * 
 * Eccezione che viene lanciata quando c'&egrave; un errore durante l'esecuzione della funzionalit&agrave; menu action.
 * 
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * 
 */
public class MenuActionException  extends BaseException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public MenuActionException(){
		super();
		this.setResourceBundleKey("MENU_ACTION_ERROR");
	}

	public MenuActionException(String msg){
		super(msg);
		this.setResourceBundleKey("MENU_ACTION_ERROR");
	}
	public MenuActionException(Throwable t){
		super(t);
		this.setResourceBundleKey("MENU_ACTION_ERROR");
	}
	public MenuActionException(String msg,Throwable t){ 
		super(msg, t);
		this.setResourceBundleKey("MENU_ACTION_ERROR");
	}

}