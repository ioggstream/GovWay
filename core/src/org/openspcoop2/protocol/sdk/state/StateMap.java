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
package org.openspcoop2.protocol.sdk.state;

import java.sql.PreparedStatement;
import java.util.Enumeration;
import java.util.Hashtable;

import org.slf4j.Logger;

/**
 * StateMap
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StateMap {

	/** Tabella Hash contenente le PreparedStatement da eseguire */
	private Hashtable<String,PreparedStatement> tablePstmt;
	
	/** Logger */
	private Logger log = null;
	
	public StateMap(Logger log) {
		this.tablePstmt = new Hashtable<String,PreparedStatement>();
		this.log = log;
	}
	public StateMap(Hashtable<String, PreparedStatement> preparedStatement, Logger log){
		this.tablePstmt = preparedStatement;
		this.log = log;
	}
	
	public Hashtable<String,PreparedStatement> getPreparedStatement(){
		return this.tablePstmt;
	}
	public void setPreparedStatement(Hashtable<String,PreparedStatement> pstm){
		this.tablePstmt = pstm;
	}
	
	public void put(String key, PreparedStatement pstmt){
		if(this.tablePstmt.containsKey(key)){
			// prima di aggiornare chiudo la preparedStatement
			try{
				this.tablePstmt.get(key).close();
			}catch(Exception e){
				this.log.debug("ClosePreparedStatement already exists error: Riscontrato errore durante la chiusura della PreparedStatement ["+key+"]: "+e);
			}
		}
		this.tablePstmt.put(key, pstmt);
	}
	
	public int size(){
		return this.tablePstmt.size();
	}
	
	public Enumeration<String> keys(){
		return this.tablePstmt.keys();
	}
	
	public boolean containsKey(String key){
		return this.tablePstmt.containsKey(key);
	}
}
