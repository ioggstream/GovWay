/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
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
package org.openspcoop2.utils.logger.beans;

import java.io.Serializable;
import java.util.Date;

import org.openspcoop2.utils.logger.IEventSearchContext;
import org.openspcoop2.utils.logger.constants.Severity;

/**
 * EventSearchContext
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EventSearchContext implements IEventSearchContext,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Date leftIntervalDate;
	private Date rightIntervalDate;
	private String source;
	private String code;
	private Severity severity;
	private String correlationIdentifier;
	private String clusterId;
	private String configurationId;
		
	@Override
	public Date getLeftIntervalDate() {
		return this.leftIntervalDate;
	}
	@Override
	public void setLeftIntervalDate(Date leftIntervalDate) {
		this.leftIntervalDate = leftIntervalDate;
	}
	@Override
	public Date getRightIntervalDate() {
		return this.rightIntervalDate;
	}
	@Override
	public void setRightIntervalDate(Date rightIntervalDate) {
		this.rightIntervalDate = rightIntervalDate;
	}
	
	@Override
	public String getConfigurationId() {
		return this.configurationId;
	}
	@Override
	public void setConfigurationId(String configurationId) {
		this.configurationId = configurationId;
	}
	
	@Override
	public String getSource() {
		return this.source;
	}
	@Override
	public void setSource(String source) {
		this.source = source;
	}
	
	@Override
	public String getCode() {
		return this.code;
	}
	@Override
	public void setCode(String code) {
		this.code = code;
	}
	
	@Override
	public Severity getSeverity() {
		return this.severity;
	}
	@Override
	public void setSeverity(Severity severity) {
		this.severity = severity;
	}

	@Override
	public String getCorrelationIdentifier() {
		return this.correlationIdentifier;
	}
	@Override
	public void setCorrelationIdentifier(String correlationIdentifier) {
		this.correlationIdentifier = correlationIdentifier;
	}
	
	@Override
	public String getClusterId() {
		return this.clusterId;
	}
	@Override
	public void setClusterId(String clusterId) {
		this.clusterId = clusterId;
	}

}