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
package org.openspcoop2.web.ctrlstat.servlet.config;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.AccessoConfigurazione;
import org.openspcoop2.core.config.AccessoDatiAutorizzazione;
import org.openspcoop2.core.config.AccessoRegistro;
import org.openspcoop2.core.config.AccessoRegistroRegistro;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.ConfigurazioneProtocollo;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.GestioneErrore;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.RoutingTable;
import org.openspcoop2.core.config.RoutingTableDestinazione;
import org.openspcoop2.core.config.SystemProperties;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicyRaggruppamento;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy;
import org.openspcoop2.core.controllo_traffico.beans.InfoPolicy;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mvc.properties.utils.DBPropertiesUtils;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationDB;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationException;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationNotFound;
import org.openspcoop2.web.lib.audit.AuditException;
import org.openspcoop2.web.lib.audit.DriverAudit;
import org.openspcoop2.web.lib.audit.dao.Filtro;

/**
 * ConfigurazioneCore
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneCore extends ControlStationCore {


	public ConfigurazioneCore() throws Exception {
		super();
	}
	public ConfigurazioneCore(ControlStationCore core) throws Exception {
		super(core);
	}
	
	
	public List<RoutingTableDestinazione> routingList(ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "routingList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().routingList(ricerca);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	
	public List<AccessoRegistroRegistro> registriList(ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "registriList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().registriList(ricerca);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
		
	/**
	 * Restituisce la gestione dell'errore di default definita nella Porta di
	 * Dominio per il componente di cooperazione
	 * 
	 * @return La gestione dell'errore
	 * 
	 */
	public GestioneErrore getGestioneErroreComponenteCooperazione() throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		return this.getGestioneErrore(true);
	}

	/**
	 * Restituisce la gestione dell'errore di default definita nella Porta di
	 * Dominio per il componente di integrazione
	 * 
	 * @return La gestione dell'errore
	 * 
	 */
	public GestioneErrore getGestioneErroreComponenteIntegrazione() throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		return this.getGestioneErrore(false);
	}

	private GestioneErrore getGestioneErrore(boolean cooperazione) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		Connection con = null;
		String nomeMetodo = "getGestioneErrore";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			if (cooperazione)
				return driver.getDriverConfigurazioneDB().getGestioneErroreComponenteCooperazione();
			else
				return driver.getDriverConfigurazioneDB().getGestioneErroreComponenteIntegrazione();

		} catch (DriverConfigurazioneNotFound de) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage(),de);
			throw de;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public AccessoRegistro getAccessoRegistro() throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getAccessoRegistro";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().getAccessoRegistro();

		} catch (DriverConfigurazioneNotFound de) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage(),de);
			throw de;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public AccessoConfigurazione getAccessoConfigurazione() throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getAccessoConfigurazione";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().getAccessoConfigurazione();

		} catch (DriverConfigurazioneNotFound de) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage(),de);
			throw de;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public AccessoDatiAutorizzazione getAccessoDatiAutorizzazione() throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getAccessoDatiAutorizzazione";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().getAccessoDatiAutorizzazione();

		} catch (DriverConfigurazioneNotFound de) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage(),de);
			throw de;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public SystemProperties getSystemPropertiesPdD() throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getSystemPropertiesPdD";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().getSystemPropertiesPdD();

		} catch (DriverConfigurazioneNotFound de) {
			//ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage(),de);
			return new SystemProperties();
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public List<Property> systemPropertyList(ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "systemPropertyList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().systemPropertyList(ricerca);

		} 
		catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public RoutingTable getRoutingTable() throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getRoutingTable";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().getRoutingTable();
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	
	public List<Filtro> filtriList(ISearch ricerca) throws AuditException {
		Connection con = null;
		String nomeMetodo = "filtriList";
		DriverAudit driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverAudit(con, this.tipoDB);
			return driver.filtriList(ricerca, Liste.FILTRI);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new AuditException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public Filtro getFiltro(long idFiltro) throws AuditException {
		Connection con = null;
		String nomeMetodo = "getFiltro";
		DriverAudit driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverAudit(con, this.tipoDB);
			return driver.getFiltro(idFiltro);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new AuditException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	
	public ConfigurazioneProtocollo getConfigurazioneProtocollo(String protocollo) throws Exception {
		
		Configurazione config = this.getConfigurazioneGenerale();
		ConfigurazioneProtocollo configProtocollo = null;
		if(config.getProtocolli()!=null && config.getProtocolli().sizeProtocolloList()>0) {
			for (ConfigurazioneProtocollo check : config.getProtocolli().getProtocolloList()) {
				if(check.getNome().equals(protocollo)) {
					configProtocollo = check;
					break;
				}
			}
		}
		
		if(configProtocollo==null) {
			configProtocollo = new ConfigurazioneProtocollo();
			configProtocollo.setNome(protocollo);
		}
		if(configProtocollo.getUrlInvocazioneServizioPD()==null || configProtocollo.getUrlInvocazioneServizioPA()==null) {
			ProtocolFactoryManager pManager = ProtocolFactoryManager.getInstance();
			IProtocolFactory<?> pFactory = pManager.getProtocolFactoryByName(protocollo);
			String context = "";
			if(pFactory.getManifest().getWeb().sizeContextList()>0) {
				context = pFactory.getManifest().getWeb().getContext(0).getName();
			}
			
			if(configProtocollo.getUrlInvocazioneServizioPD()==null) {
				configProtocollo.setUrlInvocazioneServizioPD(ConfigurazioneCostanti.getDefaultValueParametroConfigurazioneProtocolloPrefixUrlInvocazionePd(context));
			}
			
			if(configProtocollo.getUrlInvocazioneServizioPA()==null) {
				configProtocollo.setUrlInvocazioneServizioPA(ConfigurazioneCostanti.getDefaultValueParametroConfigurazioneProtocolloPrefixUrlInvocazionePa(context));
			}
		}
		
		return configProtocollo;
	}
	
	
	/**
	 * Accesso alla configurazione di controllo del traffico
	 * 
	 * @return configurazione controllo del traffico della Pdd
	 * @throws DriverControlStationNotFound
	 * @throws DriverControlStationException
	 */
	public ConfigurazioneGenerale getConfigurazioneControlloTraffico() throws DriverControlStationNotFound, DriverControlStationException {
		Connection con = null;
		String nomeMetodo = "getConfigurazioneControlloTraffico";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			
			return driver.getConfigurazioneControlloTraffico();
			
		} catch (DriverControlStationNotFound de) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage(),de);
			throw de;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public long countConfigurazionePolicy(Search ricerca)  throws DriverControlStationNotFound, DriverControlStationException {
		String nomeMetodo = "countConfigurazionePolicy";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			
			return driver.countConfigurazioneControlloTrafficoConfigurazionePolicy(ricerca);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public long countAttivazionePolicy(Search ricerca, RuoloPolicy ruoloPorta, String nomePorta)  throws DriverControlStationNotFound, DriverControlStationException{
		String nomeMetodo = "countAttivazionePolicy";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			
			return driver.countConfigurazioneControlloTrafficoAttivazionePolicy(ricerca, ruoloPorta, nomePorta);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<ConfigurazionePolicy> configurazionePolicyList(Search ricerca)  throws DriverControlStationNotFound, DriverControlStationException{
		String nomeMetodo = "configurazionePolicyList";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			
			return driver.configurazioneControlloTrafficoConfigurazionePolicyList(ricerca);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<AttivazionePolicy> attivazionePolicyList(Search ricerca, RuoloPolicy ruoloPorta, String nomePorta)  throws DriverControlStationNotFound, DriverControlStationException{
		String nomeMetodo = "attivazionePolicyList";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);	
			
			return driver.configurazioneControlloTrafficoAttivazionePolicyList(ricerca, ruoloPorta, nomePorta);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public boolean checkConfigurazioneControlloTrafficoAttivazionePolicyListUsedAction(RuoloPolicy ruoloPorta, String nomePorta, String azione)  throws DriverControlStationNotFound, DriverControlStationException{
		String nomeMetodo = "checkConfigurazioneControlloTrafficoAttivazionePolicyListUsedAction";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);	
			
			return driver.checkConfigurazioneControlloTrafficoAttivazionePolicyListUsedAction(ruoloPorta, nomePorta, azione);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<InfoPolicy> infoPolicyList()  throws DriverControlStationNotFound, DriverControlStationException{
		String nomeMetodo = "infoPolicyList";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			
			return driver.getInfoPolicyList(null);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public InfoPolicy getInfoPolicy(String idPolicyParam)  throws DriverControlStationNotFound, DriverControlStationException{
		String nomeMetodo = "getInfoPolicy";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			
			List<InfoPolicy> lst = driver.getInfoPolicyList(idPolicyParam); 
			return (lst != null && lst.size() > 0) ? lst.get(0) : null;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<AttivazionePolicy> findInUseAttivazioni(String idPolicy)   throws DriverControlStationNotFound, DriverControlStationException{
		return findInUseAttivazioni(idPolicy, false);
	}
	
	public List<AttivazionePolicy> findInUseAttivazioni(String idPolicy, boolean escludiDisabilitate)   throws DriverControlStationNotFound, DriverControlStationException{
		String nomeMetodo = "findInUseAttivazioni";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);	
			
			return driver.findInUseAttivazioni(idPolicy, escludiDisabilitate);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public long countInUseAttivazioni(String idPolicy)   throws DriverControlStationNotFound, DriverControlStationException{
		return countInUseAttivazioni(idPolicy, false);
	}
	
	public long countInUseAttivazioni(String idPolicy, boolean escludiDisabilitate)   throws DriverControlStationNotFound, DriverControlStationException{
		String nomeMetodo = "countInUseAttivazioni";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);	
			
			return driver.countInUseAttivazioni(idPolicy, escludiDisabilitate);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	
	public ConfigurazionePolicy getConfigurazionePolicy(long id)  throws DriverControlStationNotFound, DriverControlStationException{
		String nomeMetodo = "getConfigurazionePolicy";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			
			return driver.getConfigurazionePolicy(id);
		} catch (DriverControlStationNotFound e) {
			ControlStationCore.log.debug("[ControlStationCore::" + nomeMetodo + "] Configurazione ["+id+"] non presente");
			throw e;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public ConfigurazionePolicy getConfigurazionePolicy(String nomePolicy)  throws DriverControlStationNotFound, DriverControlStationException{
		String nomeMetodo = "getConfigurazionePolicy";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			
			return driver.getConfigurazionePolicy(nomePolicy);
		} catch (DriverControlStationNotFound e) {
			ControlStationCore.log.debug("[ControlStationCore::" + nomeMetodo + "] Configurazione ["+nomePolicy+"] non presente");
			throw e;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public AttivazionePolicy getAttivazionePolicy(long id)  throws DriverControlStationNotFound, DriverControlStationException{
		String nomeMetodo = "getAttivazionePolicy";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			
			return driver.getAttivazionePolicy(id);
		} catch (DriverControlStationNotFound e) {
			ControlStationCore.log.debug("[ControlStationCore::" + nomeMetodo + "] AttivazionePolicy ["+id+"] non presente");
			throw e;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public AttivazionePolicy getAttivazionePolicy(String nomePolicy)  throws DriverControlStationNotFound, DriverControlStationException{
		String nomeMetodo = "getAttivazionePolicy";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			
			return driver.getAttivazionePolicy(nomePolicy);
		} catch (DriverControlStationNotFound e) {
			ControlStationCore.log.debug("[ControlStationCore::" + nomeMetodo + "] Configurazione ["+nomePolicy+"] non presente");
			throw e;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public Integer getFreeCounterForGlobalPolicy(String policyId) throws DriverControlStationException{
		String nomeMetodo = "getFreeCounterForGlobalPolicy";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			
			return driver.getFreeCounterForGlobalPolicy(policyId);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public AttivazionePolicy getGlobalPolicy(String policyId, AttivazionePolicyFiltro filtro, AttivazionePolicyRaggruppamento groupBy) throws DriverControlStationNotFound, DriverControlStationException{
		String nomeMetodo = "getGlobalPolicy";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			
			return driver.getGlobalPolicy(policyId, filtro, groupBy); 
		} catch (DriverControlStationNotFound e) {
			throw e;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public AttivazionePolicy getGlobalPolicyByAlias(String alias) throws DriverControlStationNotFound, DriverControlStationException{
		String nomeMetodo = "getGlobalPolicyByAlias";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			
			return driver.getGlobalPolicyByAlias(alias);
		} catch (DriverControlStationNotFound e) {
			throw e;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	
	public List<IDSoggetto> getSoggettiErogatori(String protocolloSelezionato,List<String> protocolliSupportati) throws DriverControlStationException{
		String nomeMetodo = "getSoggettiErogatori";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
		
			return driver.getSoggettiErogatori(protocolloSelezionato,protocolliSupportati);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	public List<IDServizio> getServizi(String protocolloSelezionato,List<String> protocolliSupportati, 
			String tipoErogatore, String nomeErogatore) throws DriverControlStationException{
		String nomeMetodo = "getServizi";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			
			return driver.getServizi(protocolloSelezionato,protocolliSupportati, tipoErogatore, nomeErogatore);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	public List<String> getAzioni(String protocolloSelezionato,List<String> protocolliSupportati, 
			String tipoErogatore, String nomeErogatore, String tipoServizio, String nomeServizio, Integer versioneServizio) throws DriverControlStationException{
		String nomeMetodo = "getAzioni";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
		
			return driver.getAzioni(protocolloSelezionato,protocolliSupportati, 
					tipoErogatore, nomeErogatore, tipoServizio, nomeServizio, versioneServizio); 
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	public List<IDServizioApplicativo> getServiziApplicativiErogatori(String protocolloSelezionato,List<String> protocolliSupportati, 
			String tipoErogatore, String nomeErogatore, String tipoServizio, String nomeServizio, Integer versioneServizio,
			String azione) throws DriverControlStationException {
		String nomeMetodo = "getServiziApplicativiErogatori";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
		
			return driver.getServiziApplicativiErogatori(protocolloSelezionato,protocolliSupportati, 
					tipoErogatore, nomeErogatore, tipoServizio, nomeServizio, versioneServizio, 
					azione);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	public List<IDSoggetto> getSoggetti(String protocolloSelezionato,List<String> protocolliSupportati) throws DriverControlStationException{
		String nomeMetodo = "getSoggetti";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
		
			return driver.getSoggetti(protocolloSelezionato,protocolliSupportati);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	public List<IDSoggetto> getSoggettiFruitori(String protocolloSelezionato,List<String> protocolliSupportati, 
			String tipoErogatore, String nomeErogatore, String tipoServizio, String nomeServizio, Integer versioneServizio) throws DriverControlStationException{
		String nomeMetodo = "getSoggettiFruitori";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
		
			return driver.getSoggettiFruitori(protocolloSelezionato,protocolliSupportati, 
					tipoErogatore, nomeErogatore, tipoServizio, nomeServizio, versioneServizio);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	public List<IDServizioApplicativo> getServiziApplicativiFruitore(String protocolloSelezionato,List<String> protocolliSupportati,
			String tipoFruitore, String nomeFruitore,	
			String tipoErogatore, String nomeErogatore, String tipoServizio, String nomeServizio, Integer versioneServizio,
			String azione) throws DriverControlStationException{
		String nomeMetodo = "getServiziApplicativiFruitore";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			
			return driver.getServiziApplicativiFruitore(protocolloSelezionato,protocolliSupportati, 
					tipoFruitore, nomeFruitore, 
					tipoErogatore, nomeErogatore, tipoServizio, nomeServizio, versioneServizio,
					azione);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	public List<IDServizioApplicativo> getServiziApplicativiFruitore(String protocolloSelezionato,List<String> protocolliSupportati,
			String tipoFruitore, String nomeFruitore) throws DriverControlStationException{
		String nomeMetodo = "getServiziApplicativiFruitore";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			
			return driver.getServiziApplicativiFruitore(protocolloSelezionato,protocolliSupportati, 
					tipoFruitore, nomeFruitore);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public Map<String, Properties> readGestorePolicyTokenPropertiesConfiguration(long idGenericProperties) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "readGestorePolicyTokenPropertiesConfiguration";
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			Map<String, String> readProperties = DBPropertiesUtils.readProperties(con, this.tipoDB, CostantiDB.CONFIG_GENERIC_PROPERTY, CostantiDB.CONFIG_GENERIC_PROPERTY_COLUMN_NOME, 
					CostantiDB.CONFIG_GENERIC_PROPERTY_COLUMN_VALORE, CostantiDB.CONFIG_GENERIC_PROPERTY_COLUMN_ID_PROPS, idGenericProperties);
			return DBPropertiesUtils.toMultiMap(readProperties);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}	
	}
	
	public List<GenericProperties> gestorePolicyTokenList(Integer idLista, String tipologia, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "gestorePolicyTokenList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().getGenericProperties(tipologia, idLista, ricerca,false);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	public GenericProperties getGenericProperties(long idGenericProperties) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		Connection con = null;
		String nomeMetodo = "getGenericProperties";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().getGenericProperties(idGenericProperties);
		}catch (DriverConfigurazioneNotFound e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw e;
		}catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public GenericProperties getGenericProperties(String nome, String tipologia) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		Connection con = null;
		String nomeMetodo = "getGenericProperties";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().getGenericProperties(tipologia,nome);
		} catch (DriverConfigurazioneNotFound e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	public List<PortaApplicativa> listaPorteApplicativeUtilizzateDaPolicyGestioneToken(String nome) throws DriverConfigurazioneException{
		Connection con = null;
		String nomeMetodo = "listaPorteApplicativeUtilizzateDaPolicyGestioneToken";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().getPorteApplicativeByPolicyGestioneToken(nome);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	public List<PortaDelegata> listaPorteDelegateUtilizzateDaPolicyGestioneToken(String nome) throws DriverConfigurazioneException{
		Connection con = null;
		String nomeMetodo = "listaPorteDelegateUtilizzateDaPolicyGestioneToken";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().getPorteDelegateByPolicyGestioneToken(nome);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
}
