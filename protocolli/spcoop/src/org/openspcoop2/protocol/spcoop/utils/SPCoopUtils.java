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

package org.openspcoop2.protocol.spcoop.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utilities per il protocollo SPCoop
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SPCoopUtils {
	
	private static final String format = "yyyy-MM-dd_HH:mm:ss.SSS";
	
	/**
	 * Metodo che si occupa di costruire una stringa formata da una data
	 * conforme alla specifica eGov.
	 * La Data deve essere formato da :
	 * aaaa-mm-ggThh:mm:ss    (es. il 23/03/2005 alle 13:56:01 viene registrato come 2005-03-23T13:56:01)
	 * 
	 * @return un oggetto String contenente la data secondo specifica eGov.
	 * 
	 */
	public static String getDate_eGovFormat() {
		return getDate_eGovFormat(org.openspcoop2.utils.date.DateManager.getDate());
	}
		
	public static String getDate_eGovFormat(Date date) {
		SimpleDateFormat dateformat = new SimpleDateFormat (format); // SimpleDateFormat non e' thread-safe
		return dateformat.format(date).replace('_','T');
	}
	
	public static Date getDate_eGovFormat(String date) throws ParseException {
		SimpleDateFormat dateformat = new SimpleDateFormat (format); // SimpleDateFormat non e' thread-safe
		return dateformat.parse(date.replace('T','_'));
	}
}
