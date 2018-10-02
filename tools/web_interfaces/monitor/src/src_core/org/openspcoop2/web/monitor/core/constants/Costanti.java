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
package org.openspcoop2.web.monitor.core.constants;

/**
 * Costanti
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class Costanti {

	public static final String LABEL_MENU_MODALITA_CORRENTE_WITH_PARAM = org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLO_COMPACT+": {0}";
	public static final String LABEL_MENU_SOGGETTO_CORRENTE_WITH_PARAM = "Soggetto: {0}";
	
	public final static String LABEL_PARAMETRO_SOGGETTI_OPERATIVI = "Soggetti Operativi";
	public final static String LABEL_PARAMETRO_SOGGETTO_OPERATIVO = "Soggetto Operativo";
	public final static String LABEL_PARAMETRO_SOGGETTO_COMPACT = "Soggetto Operativo";
	public final static String LABEL_PARAMETRO_SOGGETTI_COMPACT = "Soggetti Operativi";
	
	public static final String LABEL_PARAMETRO_MODALITA_ALL = "Tutti";
	public static final String VALUE_PARAMETRO_MODALITA_ALL = "qualsiasi";
	
	public static final String ICONS_BASE = "/images/tema_link/" ;
	
	public static final String ICONA_MENU_UTENTE_CHECKED = ICONS_BASE +"checkbox_checked_white.png";
	public static final String ICONA_MENU_UTENTE_UNCHECKED = ICONS_BASE + "checkbox_unchecked_white.png";
	
	public static final String DATASOURCE = "dataSource";
	
	public static final String TIPODATABASE ="tipoDatabase";
	
	public static final String SHOWSQL ="showSql";
	
	public static final String NON_SELEZIONATO = "--";
	
	public static final String SEPARATORE_TIPO_NOME = org.openspcoop2.web.monitor.core.utils.Costanti.SEPARATORE_TIPO_NOME;
	public static final String SOGGETTO_SERVIZIO_SEPARATORE_CON_PARENTISI_APERTA = org.openspcoop2.web.monitor.core.utils.Costanti.SOGGETTO_SERVIZIO_SEPARATORE_CON_PARENTISI_APERTA;
	public static final String SOGGETTO_SERVIZIO_SEPARATORE_CON_PARENTISI_CHIUSA = org.openspcoop2.web.monitor.core.utils.Costanti.SOGGETTO_SERVIZIO_SEPARATORE_CON_PARENTISI_CHIUSA;
	
	public static final String LABEL_PARAMETRO_RISORSA = "Risorsa";
	public static final String LABEL_PARAMETRO_AZIONE = "Azione";
	
	public static final String LABEL_INFORMAZIONE_NON_DISPONIBILE = "Informazione non disponibile";
	public static final String LABEL_INFORMAZIONE_NON_PIU_PRESENTE = "Informazione non piu' presente";
	
	public static final String VALUE_TIPO_RICONOSCIMENTO_TOKEN_INFO = "tokenInfo";
	public static final String VALUE_TIPO_RICONOSCIMENTO_IDENTIFICATIVO_AUTENTICATO = "identificativoAutenticato";
	public static final String VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO = "applicativo";
	
	public static final String VALUE_CREDENZIALE_TRASPORTO_PREFIX = "trasporto";
	
	public static final String ICONA_ANDAMENTO_TEMPORALE = "timelapse";
	public static final String ICONA_ID_APPLICATIVO = "apps";
	public static final String ICONA_ID_MESSAGGIO = "message";
	public static final String ICONA_ID_TRANSAZIONE = "swap_horiz";
	
	public static final String LABEL_STORICO_ANDAMENTO_TEMPORALE = "Intervallo Temporale";
	public static final String LABEL_STORICO_ID_APPLICATIVO = "Identificativo Applicativo";
	public static final String LABEL_STORICO_ID_MESSAGGIO = "Identificativo Messaggio";
	public static final String LABEL_STORICO_ID_TRANSAZIONE = "Identificativo Transazione";
}
