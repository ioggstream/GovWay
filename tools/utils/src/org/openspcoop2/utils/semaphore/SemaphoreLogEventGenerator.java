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

package org.openspcoop2.utils.semaphore;

import java.sql.Connection;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;

/**
 * SemaphoreLogEventGenerator
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SemaphoreLogEventGenerator implements ISemaphoreEventGenerator {

	private static final String format = "yyyy-MM-dd_HH:mm:ss.SSS";
	
	private Logger log;
	
	public SemaphoreLogEventGenerator(Logger log) {
		this.log = log;
	}
	
	@Override
	public void emitEvent(Connection con, SemaphoreEvent event) {
		SimpleDateFormat dateformat = new SimpleDateFormat (format); // SimpleDateFormat non e' thread-safe
		String msg = 
				"["+event.getSeverity()+"] ["+event.getOperationType()+"] Date["+dateformat.format(event.getDate())+"] IdNode["+event.getIdNode()+"] [Lock:"+event.isLock()+"]: "+event.getDetails();
		switch (event.getSeverity()) {
		case DEBUG:
			this.log.debug(msg);
			break;
		case INFO:
			this.log.info(msg);
			break;
		case WARN:
			this.log.warn(msg);
			break;
		case ERROR:
			this.log.error(msg);
			break;
		}
	}
}
