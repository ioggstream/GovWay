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
package org.openspcoop2.protocol.information_missing.constants;

import org.openspcoop2.protocol.information_missing.utils.ProjectInfo;

/**
 * Costanti
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Costanti {

	public final static String ROOT_LOCAL_NAME_INFORMATION_MISSING = "openspcoop2";
	public final static String LOCAL_NAME_SOGGETTO = "soggetto";
	public final static String LOCAL_NAME_SERVIZIO_APPLICATIVO = "servizio-applicativo";
	public final static String TARGET_NAMESPACE = (new ProjectInfo()).getProjectNamespace();
	
	// sinonimi
	public final static String TIPO_SOGGETTO = "@OPENSPCOOP2_TIPO_SOGGETTO@";
	public final static String NOME_SOGGETTO = "@OPENSPCOOP2_NOME_SOGGETTO@";
	public final static String TIPO_SOGGETTO_PROPRIETARIO = "@OPENSPCOOP2_TIPO_SOGGETTO_PROPRIETARIO@";
	public final static String NOME_SOGGETTO_PROPRIETARIO = "@OPENSPCOOP2_NOME_SOGGETTO_PROPRIETARIO@";
	
	public final static String TIPO_SOGGETTO_EROGATORE = "@OPENSPCOOP2_TIPO_SOGGETTO_EROGATORE@";
	public final static String NOME_SOGGETTO_EROGATORE = "@OPENSPCOOP2_NOME_SOGGETTO_EROGATORE@";
	
	public final static String TIPO_SERVIZIO = "@OPENSPCOOP2_TIPO_SERVIZIO@";
	public final static String NOME_SERVIZIO = "@OPENSPCOOP2_NOME_SERVIZIO@";
	public final static String AZIONE = "@OPENSPCOOP2_AZIONE@";
	
	public final static String TIPO_FRUITORE = "@OPENSPCOOP2_TIPO_FRUITORE@";
	public final static String NOME_FRUITORE = "@OPENSPCOOP2_NOME_FRUITORE@";
	
}
