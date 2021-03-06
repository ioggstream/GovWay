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
package org.openspcoop2.web.monitor.statistiche.constants;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.statistiche.constants.TipoBanda;
import org.openspcoop2.core.statistiche.constants.TipoLatenza;
import org.openspcoop2.core.statistiche.constants.TipoReport;
import org.openspcoop2.core.statistiche.constants.TipoVisualizzazione;

/**
 * CostantiExporter
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class CostantiExporter {

	public final static int ERRORE_SERVER = 503;
	public final static int DATI_NON_CORRETTI = 404;
	public final static int DATI_NON_TROVATI = 404;
	public final static int CREDENZIALI_NON_FORNITE = 401;
	public final static int AUTENTICAZIONE_FALLITA = 403;
	
	public final static String USER = "user";
	public final static String PASSWORD = "password";
	
	public final static String TIPO_DISTRIBUZIONE = "distribuzione";
	public static final String TIPO_DISTRIBUZIONE_TEMPORALE = "temporale";
	public static final String TIPO_DISTRIBUZIONE_ESITI = "esiti";
	public static final String TIPO_DISTRIBUZIONE_SOGGETTO_REMOTO = "soggetto_remoto";
	public static final String TIPO_DISTRIBUZIONE_SOGGETTO_LOCALE = "soggetto_locale";
	public static final String TIPO_DISTRIBUZIONE_SERVIZIO = "servizio";
	public static final String TIPO_DISTRIBUZIONE_AZIONE = "azione";
	public static final String TIPO_DISTRIBUZIONE_SERVIZIO_APPLICATIVO = "servizio_applicativo";
	public static final String TIPO_DISTRIBUZIONE_PERSONALIZZATA = "personalizzata";
	public static final List<String> TIPI_DISTRIBUZIONE = new ArrayList<String> ();
	static{
		TIPI_DISTRIBUZIONE.add(TIPO_DISTRIBUZIONE_TEMPORALE);
		TIPI_DISTRIBUZIONE.add(TIPO_DISTRIBUZIONE_ESITI);
		TIPI_DISTRIBUZIONE.add(TIPO_DISTRIBUZIONE_SOGGETTO_REMOTO);
		TIPI_DISTRIBUZIONE.add(TIPO_DISTRIBUZIONE_SOGGETTO_LOCALE);
		TIPI_DISTRIBUZIONE.add(TIPO_DISTRIBUZIONE_SERVIZIO);
		TIPI_DISTRIBUZIONE.add(TIPO_DISTRIBUZIONE_AZIONE);
		TIPI_DISTRIBUZIONE.add(TIPO_DISTRIBUZIONE_SERVIZIO_APPLICATIVO);
		// TIPI_DISTRIBUZIONE.add(TIPO_DISTRIBUZIONE_PERSONALIZZATA); TODO
	}
	
	public final static String TIPO_FORMATO = "formato";
	public final static String TIPO_FORMATO_CSV = "csv";
	public final static String TIPO_FORMATO_XLS = "xls";
	public final static String TIPO_FORMATO_PDF = "pdf";
	public static final List<String> TIPI_FORMATO = new ArrayList<String> ();
	static{
		TIPI_FORMATO.add(TIPO_FORMATO_CSV);
		TIPI_FORMATO.add(TIPO_FORMATO_XLS);
		TIPI_FORMATO.add(TIPO_FORMATO_PDF);
	}
	
	public final static String DATA_INIZIO = "dataInizio";
	public final static String DATA_FINE = "dataFine";
	public static final String FORMAT_TIME = "yyyy-MM-dd_HH:mm:ss.SSS";
	public static final String FORMAT_DATE = "yyyy-MM-dd";
	
	public final static String PROTOCOLLO  = "protocollo";
	public final static String MITTENTE  = "mittente";
	public final static String DESTINATARIO  = "destinatario";
	public final static String SOGGETTO_LOCALE  = "soggettoLocale";
	public final static String TRAFFICO_PER_SOGGETTO  = "trafficoPerSoggetto";
	
	public final static String SERVIZIO  = "servizio";
	public final static String AZIONE  = "azione";
	
	public final static String TIPOLOGIA  = "tipologia";
	public final static String TIPOLOGIA_EROGAZIONE  = "Erogazione";
	public final static String TIPOLOGIA_FRUIZIONE  = "Fruizione";
	public final static String TIPOLOGIA_EROGAZIONE_FRUIZIONE  = "Erogazione/Fruizione";
	public static final List<String> TIPOLOGIE = new ArrayList<String> ();
	static{
		TIPOLOGIE.add(TIPOLOGIA_EROGAZIONE);
		TIPOLOGIE.add(TIPOLOGIA_FRUIZIONE);
		TIPOLOGIE.add(TIPOLOGIA_EROGAZIONE_FRUIZIONE);
	}
	
	public final static String RICERCA_ALL  = "all";
	public final static String RICERCA_INGRESSO  = "ingresso";
	public final static String RICERCA_USCITA  = "uscita";
		
	public final static String ESITO_GRUPPO  = "esitoGruppo";
	public final static String ESITO_GRUPPO_ERROR  = "ko";
	public final static String ESITO_GRUPPO_OK  = "ok";
	public final static String ESITO_GRUPPO_FAULT_APPLICATIVO  = "faultApplicativo";
	public static final List<String> ESITI_GRUPPO = new ArrayList<String> ();
	static{
		ESITI_GRUPPO.add(ESITO_GRUPPO_ERROR);
		ESITI_GRUPPO.add(ESITO_GRUPPO_OK);
		ESITI_GRUPPO.add(ESITO_GRUPPO_FAULT_APPLICATIVO);
	}
	
	public final static String ESITO  = "esito";
	
	public final static String ESITO_CONTESTO  = "contesto";
	
	
	public final static String TIPO_REPORT = "report";
	public final static TipoReport TIPO_REPORT_DISTRIBUZIONE_TEMPORALE_DEFAULT = TipoReport.LINE_CHART;
	public final static TipoReport TIPO_REPORT_DISTRIBUZIONE_OTHER_DEFAULT = TipoReport.BAR_CHART;
	
	
	public final static String TIPO_UNITA_TEMPORALE = "unitaTemporale";
	
	
	public final static String TIPO_INFORMAZIONE_VISUALIZZATA = "visualizza";
	public final static TipoVisualizzazione TIPO_INFORMAZIONE_VISUALIZZATA_DEFAULT = TipoVisualizzazione.NUMERO_TRANSAZIONI;
	public final static String TIPO_BANDA_VISUALIZZATA = "banda";
	public final static TipoBanda TIPO_BANDA_VISUALIZZATA_DEFAULT = TipoBanda.COMPLESSIVA;
	public final static String TIPO_LATENZA_VISUALIZZATA = "latenza";
	public final static TipoLatenza TIPO_LATENZA_VISUALIZZATA_DEFAULT = TipoLatenza.LATENZA_TOTALE;
	
	
	
	
	
	
}
