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

package org.openspcoop2.utils.id.serial;

import java.sql.Connection;

import org.apache.log4j.Logger;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.UtilsException;

/**
 * IDSerialGenerator
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IDSerialGenerator {

	private InfoStatistics infoStatistics;
	public IDSerialGenerator(InfoStatistics infoStatistics){
		this.infoStatistics = infoStatistics;
	}
	public IDSerialGenerator(){
	}
	
	public long buildIDAsNumber(IDSerialGeneratorParameter param, Connection con, TipiDatabase tipoDatabase, Logger log) throws UtilsException {
		try{
			if(IDSerialGeneratorType.ALFANUMERICO.equals(param)){
				throw new UtilsException("IDSerialGeneratorType["+param.getTipo()+"] prevede anche caratteri alfanumerici");
			}
			return Long.parseLong(this.buildID(param,con,tipoDatabase,log));
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public String buildID(IDSerialGeneratorParameter param, Connection con, TipiDatabase tipoDatabase, Logger log) throws UtilsException {

		IDSerialGeneratorType tipo = param.getTipo();

		// Check connessione
		if(con == null){
			throw new UtilsException("Connessione non fornita");
		}
		try{
			if(con.isClosed()){
				throw new UtilsException("Connessione risulta già chiusa");
			}
		}catch(Exception e){
			throw new UtilsException("Test Connessione non riuscito: "+e.getMessage(),e);
		}

		String identificativoUnivoco = null;
		int oldTransactionIsolation = -1;
		boolean oldAutoCommit = false;
		try{

			if ( IDSerialGeneratorType.MYSQL.equals(tipo) ) {

				try{
					oldTransactionIsolation = con.getTransactionIsolation();
					oldAutoCommit = con.getAutoCommit();
					con.setAutoCommit(true);
				} catch(Exception er) {
					log.error("Creazione serial ["+tipo.name()+"] non riuscita (impostazione transazione): "+er.getMessage());
					throw new UtilsException("Creazione serial ["+tipo.name()+"] non riuscita (impostazione transazione): "+er.getMessage());		
				}

				identificativoUnivoco = IDSerialGenerator_mysql.generate(con, param, log, this.infoStatistics);
			} 

			else if ( IDSerialGeneratorType.ALFANUMERICO.equals(tipo) || IDSerialGeneratorType.NUMERIC.equals(tipo) || IDSerialGeneratorType.DEFAULT.equals(tipo) ) {

				// DEFAULT: numeric
				try{				
					oldTransactionIsolation = con.getTransactionIsolation();
					oldAutoCommit = con.getAutoCommit();
					//System.out.println("SET TRANSACTION_SERIALIZABLE ("+conDB.getTransactionIsolation()+","+conDB.getAutoCommit()+")");
					// Il rollback, non servirebbe, pero le WrappedConnection di JBoss hanno un bug, per cui alcune risorse non vengono rilasciate.
					// Con il rollback tali risorse vengono rilasciate, e poi effettivamente la ConnectionSottostante emette una eccezione.
					try{
						con.rollback();
					}catch(Exception e){
						//System.out.println("ROLLBACK ERROR: "+e.getMessage());
					}
					if(TipiDatabase.SQLSERVER.equals(tipoDatabase)){
						con.setTransactionIsolation(4096); //4096 corresponds to SQLServerConnection.TRANSACTION_SNAPSHOT
					}else{
						con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
					}
					con.setAutoCommit(false);
				} catch(Exception er) {
					log.error("Creazione serial non riuscita (impostazione transazione): "+er.getMessage(),er);
					throw new UtilsException("Creazione serial non riuscita (impostazione transazione): "+er.getMessage(),er);		
				}

				if ( IDSerialGeneratorType.NUMERIC.equals(tipo) || IDSerialGeneratorType.DEFAULT.equals(tipo) )
					identificativoUnivoco = IDSerialGenerator_numeric.generate(con, tipoDatabase, param, log, this.infoStatistics);
				else
					identificativoUnivoco = IDSerialGenerator_alphanumeric.generate(con, tipoDatabase, param, log, this.infoStatistics);

			}

			else {

				throw new UtilsException("Tipo di generazione ["+tipo+"] non supportata");

			}

			return identificativoUnivoco;
		}
		finally{

			// Ripristino Transazione
			try{
				con.setTransactionIsolation(oldTransactionIsolation);
				con.setAutoCommit(oldAutoCommit);
			} catch(Exception er) {
				//System.out.println("ERROR UNSET:"+er.getMessage());
				log.error("Creazione serial non riuscita (ripristino transazione): "+er.getMessage());
				throw new UtilsException("Creazione serial non riuscita (ripristino transazione): "+er.getMessage());
			}
		}

	}



}