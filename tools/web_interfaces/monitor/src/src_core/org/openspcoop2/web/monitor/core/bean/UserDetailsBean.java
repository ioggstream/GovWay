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
package org.openspcoop2.web.monitor.core.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.web.lib.users.dao.PermessiUtente;
import org.openspcoop2.web.lib.users.dao.User;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.exception.UserInvalidException;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.utils.ParseUtility;

/****
 * UserDetailsBean
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class UserDetailsBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5504135605630617383L;
	
	public static final String RUOLO_USER = "ROLE_USER";
	public static final String RUOLO_OPERATORE = "ROLE_OPERATORE";
	public static final String RUOLO_CONFIGURATORE = "ROLE_CONFIG";
	public static final String RUOLO_AMMINISTRATORE = "ROLE_ADMIN";

	private String username;
	private String password;
	private List<RuoloBean> authorities;
	private List<IDSoggetto> utenteSoggettoList;
	private List<IDServizio> utenteServizioList;

	private User utente;
	private boolean ruoloConfiguratoreEnabled = false;
	
	public UserDetailsBean() {
		this.authorities = new ArrayList<RuoloBean>();
		this.authorities.add(new RuoloBean( UserDetailsBean.RUOLO_USER));
		
		try {
			this.ruoloConfiguratoreEnabled = PddMonitorProperties.getInstance(LoggerManager.getPddMonitorCoreLogger()).isRuoloConfiguratoreAttivo();
		}catch(Exception e) {
			
		}
	}

	public void setUtente(User u) throws UserInvalidException{
		this.username = u.getLogin();
		this.password = u.getPassword();
		
		this.utenteSoggettoList = u.getSoggetti();
		this.utenteServizioList = u.getServizi();
		
		int foundSoggetti = this.utenteSoggettoList != null ? this.utenteSoggettoList.size() : 0;
		int foundServizi = this.utenteServizioList !=  null ? this.utenteServizioList.size() : 0;
		
		boolean admin = (foundServizi + foundSoggetti) == 0;
		
		PermessiUtente permessi = u.getPermessi();
		
		if(permessi.isDiagnostica() || permessi.isReportistica()) {
			this.authorities.add(new RuoloBean(UserDetailsBean.RUOLO_OPERATORE));

			if(admin)
				this.authorities.add(new RuoloBean(UserDetailsBean.RUOLO_AMMINISTRATORE));
		}
		
		if(permessi.isSistema() && this.ruoloConfiguratoreEnabled) {
			this.authorities.add(new RuoloBean(UserDetailsBean.RUOLO_CONFIGURATORE));
		}
		
		if(this.authorities.size() == 1) {
			throw new UserInvalidException("Utente non dispone di alcun ruolo necessario per accedere alla console");
		}
		
		if(!u.isConfigurazioneValidaAbilitazioni()) {
			throw new UserInvalidException("L'utente non è abilitato ad utilizzare la console: configurazione incompleta");
		}
		
		this.utente = u;
	}
	public User getUtente(){
		return this.utente;
	}
	
	
	public boolean isAdmin(){
		for (RuoloBean auth : this.authorities) {
			if(UserDetailsBean.RUOLO_AMMINISTRATORE.equals(auth.getAuthority()))
				return true;
		}
		
		return false;
	}
	
	public boolean isOperatore(){
		for (RuoloBean auth : this.authorities) {
			if(UserDetailsBean.RUOLO_OPERATORE.equals(auth.getAuthority()))
				return true;
		}
		
		return false;
	}
	
	public List<IDSoggetto> getUtenteSoggettoList(){
		return this.utenteSoggettoList;
	}
	
	
	public int getSizeSoggetti(){
		return this.utenteSoggettoList!=null ? this.utenteSoggettoList.size() : 0;
	}
	
	public List<IDServizio> getUtenteServizioList(){
		return this.utenteServizioList;
	}
	
	
	public int getSizeServizio(){
		return this.utenteServizioList!=null ? this.utenteServizioList.size() : 0;
	}
	
	public String getLabelUnicoSoggettoServizioAssociato(){
		boolean foundSoggetti = this.utenteSoggettoList != null && this.utenteSoggettoList.size() > 0;
		boolean foundServizi = this.utenteServizioList !=  null && this.utenteServizioList.size() > 0;
		
		if(foundSoggetti){
			return "Soggetto Locale: ";
		}
		else if(foundServizi){
			return "Servizio: ";
		}
		else{
			return null;
		}
	}
	
	@SuppressWarnings("deprecation")
	public String getValueUnicoSoggettoServizioAssociato(){
		boolean foundSoggetti = this.utenteSoggettoList != null && this.utenteSoggettoList.size() > 0;
		boolean foundServizi = this.utenteServizioList !=  null && this.utenteServizioList.size() > 0;
		
		if(foundSoggetti){
			IDServizio idServizio = new IDServizio();
			idServizio.setSoggettoErogatore(new IDSoggetto(this.utenteServizioList.get(0).getTipo(), this.utenteSoggettoList.get(0).getNome())); 
			return ParseUtility.convertToSoggettoServizio(idServizio);
		}
		else if(foundServizi){
			return ParseUtility.convertToSoggettoServizio(this.utenteServizioList.get(0));
		}
		else{
			return null;
		}
	}

	public List<String> getTipiNomiSoggettiAssociati(){
		List<String> lst = new ArrayList<String>();
		for (IDSoggetto utenteSoggetto : this.utenteSoggettoList) {
			String tipoNome = utenteSoggetto.getTipo() + "/" + utenteSoggetto.getNome();
			if(lst.contains(tipoNome)==false){
				lst.add(tipoNome);
			}
		}
		return lst;
	}
	
	public String getLabelTipiNomiSoggettiServiziAssociati(){
		boolean foundSoggetti = this.utenteSoggettoList != null && this.utenteSoggettoList.size() > 0;
		boolean foundServizi = this.utenteServizioList !=  null && this.utenteServizioList.size() > 0;

		if(foundSoggetti && foundServizi){
			return "Soggetto Locale / Servizio";
		}
		else if(foundServizi){
			return "Servizio";
		}
		else{
			return "Soggetto Locale";
		}
	}
	
	@SuppressWarnings("deprecation")
	public List<String> getTipiNomiSoggettiServiziAssociati(){
		List<String> lst = new ArrayList<String>();
		for (IDSoggetto utenteSoggetto : this.utenteSoggettoList) {
			IDServizio idServizio = new IDServizio();
			idServizio.setSoggettoErogatore(new IDSoggetto(utenteSoggetto.getTipo(), utenteSoggetto.getNome())); 
			lst.add(ParseUtility.convertToSoggettoServizio(idServizio));
		}
		for (IDServizio idServizio : this.utenteServizioList) {
			lst.add(ParseUtility.convertToSoggettoServizio(idServizio));
		}
		return lst;
	}


	public String getPassword() {
		return this.password;
	}

	public String getUsername() {
		return this.username;
	}

	public List<RuoloBean> getAuthorities() {
		return this.authorities;
	}
	
	public class RuoloBean implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private String authority;
		
		public RuoloBean (String r){
			this.authority = r;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof RuoloBean) {
				return this.getAuthority().equals(
						(((RuoloBean) o).getAuthority()));
			}
			return false;
		}

		public String getAuthority() {
			return this.authority;
		}

	}
}
