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

package org.openspcoop2.utils.sonde;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Date;
import java.util.Properties;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.resources.ClassLoaderUtilities;
import org.openspcoop2.utils.sonde.Sonda.StatoSonda;
import org.openspcoop2.utils.sonde.impl.SondaBatch;
import org.openspcoop2.utils.sonde.impl.SondaCoda;
import org.openspcoop2.utils.sonde.impl.SondaInvocazione;

/**
 * TEST
 * 
 *
 * @author Bussu Giovanni (bussu@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ClientTest {

	public static void main(String[] args) throws Exception {

		TipiDatabase tipoDatabase = null;
		if(args.length>0){
			if(!"${tipoDatabase}".equals(args[0].trim())){
				tipoDatabase = TipiDatabase.toEnumConstant(args[0].trim());
			}
		}

		String url = null;
		String driver = null;
		String userName = null;
		String password = null;
		String sondaBatchName = "batch";
		String sondaCodaName = "coda";
		String sondaInvocazioneName = "invocazione";
		
		
		switch (tipoDatabase) {
		case POSTGRESQL:
			url = "jdbc:postgresql://localhost/prova";
			driver = "org.postgresql.Driver";
			userName = "openspcoop2";
			password = "openspcoop2";
			break;
		case MYSQL:
			url = "jdbc:mysql://localhost/prova";
			driver = "com.mysql.jdbc.Driver";
			userName = "openspcoop2";
			password = "openspcoop2";
			break;
		case ORACLE:
			url = "jdbc:oracle:thin:@localhost:1521:XE";
			driver = "oracle.jdbc.OracleDriver";
			userName = "prova";
			password = "prova";
			break;
		case HSQL:
			url = "jdbc:hsqldb:hsql://localhost:9001/";
			driver = "org.hsqldb.jdbcDriver";
			userName = "sa";
			password = "";
			break;
		case SQLSERVER:
			url = "jdbc:sqlserver://localhost:1433;databaseName=prova";
			driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
			userName = "openspcoop2";
			password = "openspcoop2";
			break;
		case DB2:
			url = "jdbc:db2://127.0.0.1:50000/prova";
			driver = "com.ibm.db2.jcc.DB2Driver";
			userName = "openspcoop2";
			password = "openspcoop2";
			break;
		default:
			break;
		}
		if(args.length>1){
			String urlCustom = args[1].trim();
			if(!"${url}".equals(urlCustom)){
				url = urlCustom;
			}
		}
		if(args.length>2){
			String usernameCustom = args[2].trim();
			if(!"${username}".equals(usernameCustom)){
				userName = usernameCustom;
			}
		}
		if(args.length>3){
			String passwordCustom = args[3].trim();
			if(!"${password}".equals(passwordCustom)){
				password = passwordCustom;
			}
		}
		if(args.length>4){
			String sondaBatchNameCustom = args[4].trim();
			if(!"${sondaBatchName}".equals(sondaBatchNameCustom)){
				sondaBatchName = sondaBatchNameCustom;
			}
		}
		if(args.length>5){
			String sondaCodaNameCustom = args[5].trim();
			if(!"${sondaCodaName}".equals(sondaCodaNameCustom)){
				sondaCodaName = sondaCodaNameCustom;
			}
		}


		ClassLoaderUtilities.newInstance(driver);
		Connection con = null;
		try{
			con = DriverManager.getConnection(url, userName, password);
//			testSondaError(tipoDatabase, con);
			testSondaBatch(sondaBatchName, tipoDatabase, con);
			testSondaCoda(sondaCodaName, tipoDatabase, con);
			testSondaInvocazione(sondaInvocazioneName, tipoDatabase, con);
		} catch(Exception e) {
			System.err.println("Errore durante il TestSonda: " + e.getMessage());
			e.printStackTrace(System.err);
		}
	}
	
	private static void checkStato(String nome, int expected, StatoSonda actualState) throws Exception {
		if(expected != actualState.getStato()) {
			throw new Exception("Stato della sonda "+nome+" errato. Expected: "+expected+". Actual: " + actualState.getStato() + ". Descrizione: " + actualState.getDescrizione());
		}
	}
	
	public static void testSondaError(TipiDatabase tipoDatabase, Connection con) throws Exception {
		Sonda sonda = SondaFactory.get("inesistente", con, tipoDatabase);
		if(sonda != null) throw new Exception("Sonda inesistente trovata sul DB");
		System.out.println("Test sonda inesistente ok");
		try {
			SondaFactory.get("malconfigurata", con, tipoDatabase);
		} catch(Exception e) {
			System.out.println("Test sonda malconfigurata ok");
			try {
				SondaFactory.get("sondaclasseacaso", con, tipoDatabase);
			} catch(Exception e1) {
				System.out.println("Test sonda sondaclasseacaso ok");
				return;
			}
			throw new Exception("Sonda classeacaso dovrebbe dare problemi di inizializzazione");

		}
		throw new Exception("Sonda malconfigurata dovrebbe dare problemi di inizializzazione");
		
	}
	
	public static void testSondaBatch(String sondaName, TipiDatabase tipoDatabase, Connection con) throws Exception {
		
		SondaFactory.updateConfSonda(sondaName, 1000, 2000, con, tipoDatabase);
		SondaBatch batch = (SondaBatch) SondaFactory.get(sondaName, con, tipoDatabase);
		
		if(batch == null) throw new Exception("Sonda ["+sondaName+"] non trovata sul db");

		{
			StatoSonda stato = batch.aggiornaStatoSonda(true, null, new Date(), "OK", con, tipoDatabase); //<<-- Aggiornare con uno stato ok
			
			checkStato(sondaName, 0, stato);
			System.out.println("Test 1 batch ok. Descrizione: " + stato.getDescrizione());
		}

		Utilities.sleep(1200);

		{
			StatoSonda stato = batch.getStatoSonda(); // <<-- l'ultima volta che ha girato il batch e' prima dell'intervallo di warn
			checkStato(sondaName, 1, stato);
			System.out.println("Test 2 batch ok. Descrizione: " + stato.getDescrizione());
		}
		Utilities.sleep(1200);

		{
			StatoSonda stato = batch.getStatoSonda(); // <<-- l'ultima volta che ha girato il batch e' prima dell'intervallo di error
			checkStato(sondaName, 2, stato);
			System.out.println("Test 3 batch ok. Descrizione: " + stato.getDescrizione());
		}

		{
			StatoSonda stato = batch.aggiornaStatoSonda(true, null, new Date(), null, con, tipoDatabase); //<<-- Aggiornare con uno stato ok
			checkStato(sondaName, 0, stato);
			System.out.println("Test 4 batch ok. Descrizione: " + stato.getDescrizione());
		}		

		{
			StatoSonda stato = batch.aggiornaStatoSonda(false, null, new Date(), "Errore durante l'esecuzione del batch\n\nin due righe", con, tipoDatabase); //<<-- Aggiornare con uno stato ko
			checkStato(sondaName, 2, stato);
			System.out.println("Test 5 batch ok. Descrizione: " + stato.getDescrizione());
		}

		{
			StatoSonda stato = batch.aggiornaStatoSonda(true, null, new Date(), null, con, tipoDatabase); // <<-- Aggiornare con uno stato ok
			checkStato(sondaName, 0, stato);
			System.out.println("Test 6 batch ok. Descrizione: " + stato.getDescrizione());
		}
		
	}
	
	public static void testSondaInvocazione(String sondaInvocazioneName, TipiDatabase tipoDatabase, Connection con) throws Exception {

		SondaInvocazione invocazione = (SondaInvocazione) SondaFactory.get(sondaInvocazioneName, con, tipoDatabase);
		if(invocazione == null) throw new Exception("Sonda ["+sondaInvocazioneName+"] non trovata sul db");
		
		{
			StatoSonda stato = invocazione.aggiornaStatoSonda(true, null, con, tipoDatabase);
			checkStato(sondaInvocazioneName, 0, stato);
			System.out.println("Test 1 invocazione ok. Descrizione: " + stato.getDescrizione());
		}

		{
			StatoSonda stato = invocazione.aggiornaStatoSonda(false, null, con, tipoDatabase);
			checkStato(sondaInvocazioneName, 2, stato);
			System.out.println("Test 2 invocazione ko. Descrizione: " + stato.getDescrizione());
		}

		{
			Properties props = new Properties();
			String key = "subCode";
			String value = "ERR_000\na";
			props.setProperty(key, value);
			StatoSonda stato = invocazione.aggiornaStatoSonda(true, props, con, tipoDatabase);
			checkStato(sondaInvocazioneName, 0, stato);
			System.out.println("Test 3 invocazione ok. Descrizione: " + stato.getDescrizione());
			
			SondaInvocazione invocazione2 = (SondaInvocazione) SondaFactory.get(sondaInvocazioneName, con, tipoDatabase);
			
			if(!invocazione2.getParam().getDatiCheck().containsKey(key)) {
				 throw new Exception("Property ["+key+"] non correttamente salvata");
			}
			String property = invocazione2.getParam().getDatiCheck().getProperty(key);
			
			if(!property.equals(value)) {
				 throw new Exception("Property ["+key+"] non correttamente salvata. Expected ["+value+"] found ["+property+"]");
			}
			
			System.out.println("Trovata property custom ["+key+"] ["+property+"]");
		}


	}
	
	public static void testSondaCoda(String sondaCodaName, TipiDatabase tipoDatabase, Connection con) throws Exception {

		SondaCoda coda = (SondaCoda) SondaFactory.get(sondaCodaName, con, tipoDatabase);
		if(coda == null) throw new Exception("Sonda ["+sondaCodaName+"] non trovata sul db");

		{
			StatoSonda stato = coda.aggiornaStatoSonda(3, null, con, tipoDatabase);
			checkStato(sondaCodaName, 0, stato);
			System.out.println("Test 1 coda ok. Descrizione: " + stato.getDescrizione());
		}

		{
			StatoSonda stato = coda.aggiornaStatoSonda(7, null, con, tipoDatabase); 
			checkStato(sondaCodaName, 1, stato);
			System.out.println("Test 2 coda ok. Descrizione: " + stato.getDescrizione());
		}


		{
			StatoSonda stato = coda.aggiornaStatoSonda(20, null, con, tipoDatabase);
			checkStato(sondaCodaName, 2, stato);
			System.out.println("Test 3 coda ok. Descrizione: " + stato.getDescrizione());
		}


		{
			StatoSonda stato = coda.aggiornaStatoSonda(3, null, con, tipoDatabase);
			checkStato(sondaCodaName, 0, stato);
			System.out.println("Test 4 coda ok. Descrizione: " + stato.getDescrizione());
		}

	}

}