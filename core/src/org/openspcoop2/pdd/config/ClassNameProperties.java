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



package org.openspcoop2.pdd.config;


import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;


/**
 * Contiene un lettore del file di proprieta' di OpenSPCoop.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class ClassNameProperties {	

	/** Logger utilizzato per errori eventuali. */
	private Logger log = null;



	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Reader delle proprieta' impostate nel file 'govway.classRegistry.properties' */
	private ClassNameInstanceProperties reader;

	/** Copia Statica */
	private static ClassNameProperties classNameProperties = null;


	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Viene chiamato in causa per istanziare il properties reader
	 *
	 * 
	 */
	public ClassNameProperties() throws Exception {

		if(OpenSPCoop2Startup.initialize)
			this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		else
			this.log = LoggerWrapperFactory.getLogger(ClassNameProperties.class);
		
		/* ---- Lettura del cammino del file di configurazione ---- */
		Properties propertiesReader = new Properties();
		java.io.InputStream properties = null;
		try{  
		    properties = ClassNameProperties.class.getResourceAsStream("/govway.classRegistry.properties");
			if(properties==null){
				throw new Exception("File '/govway.classRegistry.properties' not found");
			}
		    propertiesReader.load(properties);
		}catch(Exception e) {
			this.log.error("Riscontrato errore durante la lettura del file 'govway.classRegistry.properties': \n\n"+e.getMessage());
		    throw new Exception("ClassName initialize error: "+e.getMessage());
		}finally{
		    try{
		    	if(properties!=null)
		    		properties.close();
		    }catch(Exception er){}
		}

		this.reader = new ClassNameInstanceProperties(propertiesReader, this.log);
	}
	
	public void refreshLocalProperties(Properties localProp,String confDir){
		
		this.reader.searchLocalFileImplementation(confDir);
		
		if(localProp!=null){
			this.reader.setLocalObjectImplementation(localProp);
		}
		
	}


	/**
	 * Il Metodo si occupa di inizializzare il propertiesReader 
	 *
	 * 
	 */
	public static boolean initialize(){

		try {
		    ClassNameProperties.classNameProperties = new ClassNameProperties();	
		    return true;
		}
		catch(Exception e) {
		    return false;
		}
	}
    
	/**
	 * Ritorna l'istanza di questa classe
	 *
	 * @return Istanza di ClassNameProperties
	 * 
	 */
	public static ClassNameProperties getInstance(){
	    if(ClassNameProperties.classNameProperties==null)
	    	ClassNameProperties.initialize();
	    return ClassNameProperties.classNameProperties;
	}
    
	/*
	public static void updateLocalImplementation(Properties prop){
		ClassNameProperties.classNameProperties.reader.setLocalImplementation(prop);
	}*/








	/* ********  M E T O D I  ******** */

	/**
	 * Ritorna la classe di un connector se questo e' stato precedentemente registrata
	 *
	 * 
	 */
	public String getConnettore(String nome){
		return this.getValue("org.openspcoop2.connettore.", nome);
	}
	public String[] getConnettore() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.connettore.",CostantiConfigurazione.DISABILITATO.toString());
	}
		
	/**
	 * Ritorna una classe 'IAutorizzazioneSecurityContainer' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getRealmContainerCustom(String nome){
		return this.getValue("org.openspcoop2.realmContainer.custom.", nome);
	}
	public String[] getRealmContainerCustom() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.realmContainer.custom.");
	}
	
	/**
	 * Ritorna una classe 'IAutenticazionePortaDelegata' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getAutenticazionePortaDelegata(String nome){
		return this.getValue("org.openspcoop2.autenticazione.pd.", nome);
	}
	public String[] getAutenticazionePortaDelegata() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.autenticazione.pd.",CostantiConfigurazione.AUTENTICAZIONE_NONE.toString());
	}

	/**
	 * Ritorna una classe 'IAutenticazionePortaApplicativa' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getAutenticazionePortaApplicativa(String nome){
		return this.getValue("org.openspcoop2.autenticazione.pa.", nome);
	}
	public String[] getAutenticazionePortaApplicativa() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.autenticazione.pa.",CostantiConfigurazione.AUTENTICAZIONE_NONE.toString());
	}
	
	/**
	 * Ritorna una classe 'IAutorizzazionePortaDelegata' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getAutorizzazionePortaDelegata(String nome){
		return this.getValue("org.openspcoop2.autorizzazione.pd.", nome);
	}
	public String[] getAutorizzazionePortaDelegata() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.autorizzazione.pd.",CostantiConfigurazione.AUTORIZZAZIONE_NONE);
	}
	
	/**
	 * Ritorna una classe 'IAutorizzazionePortaApplicativa' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getAutorizzazionePortaApplicativa(String nome){
		return this.getValue("org.openspcoop2.autorizzazione.pa.", nome);
	}
	public String[] getAutorizzazionePortaApplicativa() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.autorizzazione.pa.",CostantiConfigurazione.AUTORIZZAZIONE_NONE);
	}
	
	/**
	 * Ritorna una classe 'IAutorizzazioneContenutoPortaDelegata' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getAutorizzazioneContenutoPortaDelegata(String nome){
		return this.getValue("org.openspcoop2.autorizzazioneContenuto.pd.", nome);
	}
	public String[] getAutorizzazioneContenutoPortaDelegata() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.autorizzazioneContenuto.pd.",CostantiConfigurazione.AUTORIZZAZIONE_NONE);
	}
	
	/**
	 * Ritorna una classe 'IAutorizzazioneContenutoPortaApplicativa' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getAutorizzazioneContenutoPortaApplicativa(String nome){
		return this.getValue("org.openspcoop2.autorizzazioneContenuto.pa.", nome);
	}
	public String[] getAutorizzazioneContenutoPortaApplicativa() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.autorizzazioneContenuto.pa.",CostantiConfigurazione.AUTORIZZAZIONE_NONE);
	}
	
	/**
	 * Ritorna una classe 'IGestoreIntegrazionePD' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getIntegrazionePortaDelegata(String nome){
		return this.getValue("org.openspcoop2.integrazione.pd.", nome);
	}
	public String[] getIntegrazionePortaDelegata() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.integrazione.pd.");
	}
	
	/**
	 * Ritorna una classe 'IGestoreIntegrazionePA' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getIntegrazionePortaApplicativa(String nome){
		return this.getValue("org.openspcoop2.integrazione.pa.", nome);
	}
	public String[] getIntegrazionePortaApplicativa() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.integrazione.pa.");
	}
	
	/**
	 * Ritorna una classe 'IJDBCAdapter' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getJDBCAdapter(String nome){
		return this.getValue("org.openspcoop2.jdbcAdapter.", nome);
	}
	public String[] getJDBCAdapter() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.jdbcAdapter.");
	}
	
	/**
	 * Ritorna una classe 'IThreshold' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getThreshold(String nome){
		return this.getValue("org.openspcoop2.threshold.", nome);
	}
	public String[] getThreshold() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.threshold.");
	}

	/**
	 * Ritorna una classe 'IMsgDiagnosticoOpenSPCoopAppender' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getMsgDiagnosticoOpenSPCoopAppender(String nome){
		return this.getValue("org.openspcoop2.msgdiagnosticoAppender.", nome);
	}
	public String[] getMsgDiagnosticoOpenSPCoopAppender() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.msgdiagnosticoAppender.");
	}
	
	/**
	 * Ritorna una classe 'ITracciamentoOpenSPCoopAppender' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getTracciamentoOpenSPCoopAppender(String nome){
		return this.getValue("org.openspcoop2.tracciamentoAppender.", nome);
	}
	public String[] getTracciamentoOpenSPCoopAppender() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.tracciamentoAppender.");
	}
	
	/**
	 * Ritorna una classe 'IDumpOpenSPCoopAppender' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getDumpOpenSPCoopAppender(String nome){
		return this.getValue("org.openspcoop2.dumpAppender.", nome);
	}
	public String[] getDumpOpenSPCoopAppender() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.dumpAppender.");
	}
		
	/**
	 * Ritorna una classe 'INodeReceiver' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getNodeReceiver(String nome){
		return this.getValue("org.openspcoop2.nodeReceiver.", nome);
	}
	public String[] getNodeReceiver() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.nodeReceiver.");
	}
	
	/**
	 * Ritorna una classe 'INodeSender' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getNodeSender(String nome){
		return this.getValue("org.openspcoop2.nodeSender.", nome);
	}
	public String[] getNodeSender() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.nodeSender.");
	}
	
	/**
	 * Ritorna una classe 'IGestoreRepositoryBuste' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getRepositoryBuste(String nome){
		return this.getValue("org.openspcoop2.repositoryBuste.", nome);
	}
	public String[] getRepositoryBuste() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.repositoryBuste.");
	}
	
	/**
	 * Ritorna una classe 'ISQLQueryObject' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getSQLQueryObject(String nome){
		return this.getValue("org.openspcoop2.sqlQueryObject.", nome);
	}
	public String[] getSQLQueryObject() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.sqlQueryObject.");
	}
	
	/**
	 * Ritorna una classe 'IDate' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getDateManager(String nome){
		return this.getValue("org.openspcoop2.date.", nome);
	}
	public String[] getDateManager() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.date.");
	}
	
	
	
	/**
	 * Ritorna una classe 'IUniqueIdentifier' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getUniqueIdentifier(String nome){
		return this.getValue("org.openspcoop2.id.", nome);
	}
	public String[] getUniqueIdentifier() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.id.");
	}
	
	
	
	/**
	 * Ritorna una classe 'IFiltroDuplicati' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getFiltroDuplicati(String nome){
		return this.getValue("org.openspcoop2.protocol.filtroDuplicati.", nome);
	}
	public String[] getFiltroDuplicati() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.protocol.filtroDuplicati.");
	}
	
	
	
	
	
	// Handler BuiltIn
	
	
	/**
	 * Ritorna una classe 'InitHandlerBuiltIn' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getInitHandlerBuiltIn(String nome){
		return this.getValue("org.openspcoop2.pdd.handler.built-in.init.", nome);
	}
	public String[] getInitHandlerBuiltIn() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.pdd.handler.built-in.init.");
	}
	
	
	
	/**
	 * Ritorna una classe 'ExitHandlerBuiltIn' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getExitHandlerBuiltIn(String nome){
		return this.getValue("org.openspcoop2.pdd.handler.built-in.exit.", nome);
	}
	public String[] getExitHandlerBuiltIn() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.pdd.handler.built-in.exit.");
	}
	
	
	
	/**
	 * Ritorna una classe 'PreInRequestHandlerBuiltIn' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getPreInRequestHandlerBuiltIn(String nome){
		return this.getValue("org.openspcoop2.pdd.handler.built-in.pre-in-request.", nome);
	}
	public String[] getPreInRequestHandlerBuiltIn() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.pdd.handler.built-in.pre-in-request.");
	}
	
	/**
	 * Ritorna una classe 'InRequestHandlerBuiltIn' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getInRequestHandlerBuiltIn(String nome){
		return this.getValue("org.openspcoop2.pdd.handler.built-in.in-request.", nome);
	}
	public String[] getInRequestHandlerBuiltIn() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.pdd.handler.built-in.in-request.");
	}
	
	/**
	 * Ritorna una classe 'InProtocolRequestHandlerBuiltIn' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getInRequestProtocolHandlerBuiltIn(String nome){
		return this.getValue("org.openspcoop2.pdd.handler.built-in.in-protocol-request.", nome);
	}
	public String[] getInRequestProtocolHandlerBuiltIn() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.pdd.handler.built-in.in-protocol-request.");
	}
	
	/**
	 * Ritorna una classe 'OutRequestHandlerBuiltIn' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getOutRequestHandlerBuiltIn(String nome){
		return this.getValue("org.openspcoop2.pdd.handler.built-in.out-request.", nome);
	}
	public String[] getOutRequestHandlerBuiltIn() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.pdd.handler.built-in.out-request.");
	}
	
	/**
	 * Ritorna una classe 'PostOutRequestHandlerBuiltIn' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getPostOutRequestHandlerBuiltIn(String nome){
		return this.getValue("org.openspcoop2.pdd.handler.built-in.post-out-request.", nome);
	}
	public String[] getPostOutRequestHandlerBuiltIn() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.pdd.handler.built-in.post-out-request.");
	}
	
	/**
	 * Ritorna una classe 'PreInResponseHandlerBuiltIn' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getPreInResponseHandlerBuiltIn(String nome){
		return this.getValue("org.openspcoop2.pdd.handler.built-in.pre-in-response.", nome);
	}
	public String[] getPreInResponseHandlerBuiltIn() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.pdd.handler.built-in.pre-in-response.");
	}
	
	/**
	 * Ritorna una classe 'InResponseHandlerBuiltIn' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getInResponseHandlerBuiltIn(String nome){
		return this.getValue("org.openspcoop2.pdd.handler.built-in.in-response.", nome);
	}
	public String[] getInResponseHandlerBuiltIn() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.pdd.handler.built-in.in-response.");
	}
	
	/**
	 * Ritorna una classe 'OutResponseHandlerBuiltIn' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getOutResponseHandlerBuiltIn(String nome){
		return this.getValue("org.openspcoop2.pdd.handler.built-in.out-response.", nome);
	}
	public String[] getOutResponseHandlerBuiltIn() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.pdd.handler.built-in.out-response.");
	}
	
	/**
	 * Ritorna una classe 'PostOutResponseHandlerBuiltIn' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getPostOutResponseHandlerBuiltIn(String nome){
		return this.getValue("org.openspcoop2.pdd.handler.built-in.post-out-response.", nome);
	}
	public String[] getPostOutResponseHandlerBuiltIn() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.pdd.handler.built-in.post-out-response.");
	}
	
	/**
	 * Ritorna una classe 'IntegrationManagerRequestHandlerBuiltIn' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getIntegrationManagerRequestHandlerBuiltIn(String nome){
		return this.getValue("org.openspcoop2.pdd.integrationManager.handler.built-in.request.", nome);
	}
	public String[] getIntegrationManagerRequestHandlerBuiltIn() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.pdd.integrationManager.handler.built-in.request.");
	}
	
	/**
	 * Ritorna una classe 'IntegrationManagerResponseHandlerBuiltIn' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getIntegrationManagerResponseHandlerBuiltIn(String nome){
		return this.getValue("org.openspcoop2.pdd.integrationManager.handler.built-in.response.", nome);
	}
	public String[] getIntegrationManagerResponseHandlerBuiltIn() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.pdd.integrationManager.handler.built-in.response.");
	}
	
	
	
	
	// Handler 
	
	/**
	 * Ritorna una classe 'InitHandler' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getInitHandler(String nome){
		return this.getValue("org.openspcoop2.pdd.handler.init.", nome);
	}
	public String[] getInitHandler() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.pdd.handler.init.");
	}
	
	
	
	/**
	 * Ritorna una classe 'ExitHandler' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getExitHandler(String nome){
		return this.getValue("org.openspcoop2.pdd.handler.exit.", nome);
	}
	public String[] getExitHandler() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.pdd.handler.exit.");
	}
	
	
	
	/**
	 * Ritorna una classe 'PreInRequestHandler' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getPreInRequestHandler(String nome){
		return this.getValue("org.openspcoop2.pdd.handler.pre-in-request.", nome);
	}
	public String[] getPreInRequestHandler() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.pdd.handler.pre-in-request.");
	}
	
	/**
	 * Ritorna una classe 'InRequestHandler' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getInRequestHandler(String nome){
		return this.getValue("org.openspcoop2.pdd.handler.in-request.", nome);
	}
	public String[] getInRequestHandler() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.pdd.handler.in-request.");
	}
	
	/**
	 * Ritorna una classe 'InProtocolRequestHandler' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getInRequestProtocolHandler(String nome){
		return this.getValue("org.openspcoop2.pdd.handler.in-protocol-request.", nome);
	}
	public String[] getInRequestProtocolHandler() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.pdd.handler.in-protocol-request.");
	}
	
	/**
	 * Ritorna una classe 'OutRequestHandler' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getOutRequestHandler(String nome){
		return this.getValue("org.openspcoop2.pdd.handler.out-request.", nome);
	}
	public String[] getOutRequestHandler() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.pdd.handler.out-request.");
	}
	
	/**
	 * Ritorna una classe 'PostOutRequestHandler' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getPostOutRequestHandler(String nome){
		return this.getValue("org.openspcoop2.pdd.handler.post-out-request.", nome);
	}
	public String[] getPostOutRequestHandler() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.pdd.handler.post-out-request.");
	}
	
	/**
	 * Ritorna una classe 'PreInResponseHandler' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getPreInResponseHandler(String nome){
		return this.getValue("org.openspcoop2.pdd.handler.pre-in-response.", nome);
	}
	public String[] getPreInResponseHandler() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.pdd.handler.pre-in-response.");
	}
	
	/**
	 * Ritorna una classe 'InResponseHandler' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getInResponseHandler(String nome){
		return this.getValue("org.openspcoop2.pdd.handler.in-response.", nome);
	}
	public String[] getInResponseHandler() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.pdd.handler.in-response.");
	}
	
	/**
	 * Ritorna una classe 'OutResponseHandler' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getOutResponseHandler(String nome){
		return this.getValue("org.openspcoop2.pdd.handler.out-response.", nome);
	}
	public String[] getOutResponseHandler() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.pdd.handler.out-response.");
	}
	
	/**
	 * Ritorna una classe 'PostOutResponseHandler' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getPostOutResponseHandler(String nome){
		return this.getValue("org.openspcoop2.pdd.handler.post-out-response.", nome);
	}
	public String[] getPostOutResponseHandler() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.pdd.handler.post-out-response.");
	}
	
	/**
	 * Ritorna una classe 'IntegrationManagerRequestHandler' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getIntegrationManagerRequestHandler(String nome){
		return this.getValue("org.openspcoop2.pdd.integrationManager.handler.request.", nome);
	}
	public String[] getIntegrationManagerRequestHandler() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.pdd.integrationManager.handler.request.");
	}
	
	/**
	 * Ritorna una classe 'IntegrationManagerResponseHandler' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getIntegrationManagerResponseHandler(String nome){
		return this.getValue("org.openspcoop2.pdd.integrationManager.handler.response.", nome);
	}
	public String[] getIntegrationManagerResponseHandler() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.pdd.integrationManager.handler.response.");
	}
	
	
	/**
	 * Ritorna una classe 'OpenSPCoop2MessageFactory' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getOpenSPCoop2MessageFactory(String nome){
		if(nome == null) return null;
		return this.getValue("org.openspcoop2.pdd.messagefactory.", nome);
	}
	public String[] getOpenSPCoop2MessageFactory() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.pdd.messagefactory.");
	}
	
	
	/**
	 * Ritorna una classe 'MessageSecurityContext' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getMessageSecurityContext(String nome){
		if(nome == null) return null;
		return this.getValue("org.openspcoop2.pdd.messageSecurity.context.", nome);
	}
	public String[] getMessageSecurityContext() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.pdd.messageSecurity.context.");
	}
	
	
	/**
	 * Ritorna una classe 'MessageSecurityDigestReader' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getMessageSecurityDigestReader(String nome){
		if(nome == null) return null;
		return this.getValue("org.openspcoop2.pdd.messageSecurity.digestReader.", nome);
	}
	public String[] getMessageSecurityDigestReader() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.pdd.messageSecurity.digestReader.");
	}
	
	
	/**
	 * Ritorna una classe 'IGestoreCredenziali' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getGestoreCredenziali(String nome){
		return this.getValue("org.openspcoop2.pdd.gestoreCredenziali.", nome);
	}
	public String[] getGestoreCredenziali() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.pdd.gestoreCredenziali.");
	}
	
	/**
	 * Ritorna una classe 'IGestoreCredenzialiIM' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getGestoreCredenzialiIM(String nome){
		return this.getValue("org.openspcoop2.integrationManager.gestoreCredenziali.", nome);
	}
	public String[] getGestoreCredenzialiIM() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.integrationManager.gestoreCredenziali.");
	}
	
	/**
	 * Ritorna la classe che implementa la IProtocolFactory per la servletcontext invocata
	 * 
	 * 
	 */
	public String getProtocol(String nome){
		return this.getValue("org.openspcoop2.protocol.", nome);
	}
	public String[] getProtocol() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.protocol.");
	}
	
	
	/**
	 * Ritorna una classe 'NotifierCallback' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getNotifierCallback(String nome){
		return this.getValue("org.openspcoop2.notifierCallback.", nome);
	}
	public String[] getNotifierCallback() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.notifierCallback.");
	}
	
	
	
	
	/**
	 * Ritorna una classe 'IBehaviour' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getBehaviour(String nome){
		return this.getValue("org.openspcoop2.behaviour.", nome);
	}
	public String[] getBehaviour() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.behaviour.");
	}
	
	
	
	/**
	 * Ritorna una classe 'ISalvataggioTracceManager' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getSalvataggioTracceManager(String nome){
		return this.getValue("org.openspcoop2.pdd.transazioni.tracce.salvataggio.", nome);
	}
	public String[] getSalvataggioTracceManager() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.pdd.transazioni.tracce.salvataggio.");
	}
	
	
	
	/**
	 * Ritorna una classe 'ISalvataggioDiagnosticiManager' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getSalvataggioDiagnosticiManager(String nome){
		return this.getValue("org.openspcoop2.pdd.transazioni.diagnostici.salvataggio.", nome);
	}
	public String[] getSalvataggioDiagnosticiManager() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.pdd.transazioni.diagnostici.salvataggio.");
	}
	
	
	
	/**
	 * Ritorna una classe 'IRateLimiting' se questa e' stata precedentemente registrata
	 *
	 * 
	 */
	public String getRateLimiting(String nome){
		return this.getValue("org.openspcoop2.pdd.controlloTraffico.rateLimiting.", nome);
	}
	public String[] getRateLimiting() throws Exception{
		return this.getTipiGestiti("org.openspcoop2.pdd.controlloTraffico.rateLimiting.");
	}
	
	
	
	/**
	 * Ritorna una classe estesa la cui interfaccia dipende da quella indicata nel primo, parametro
	 *
	 * 
	 */
	public String getExtended(String tipologia, String nome){
		return this.getValue("org.openspcoop2.pdd.extended.", tipologia+"."+nome);
	}
	public String[] getExtended(String tipologia) throws Exception{
		return this.getTipiGestiti("org.openspcoop2.pdd.extended."+tipologia+".");
	}
	
	
	

	private String getValue(String prop,String nome){
		try{
			String value = this.reader.getValue(prop+nome);
			if(value!=null){
				value = value.trim();
			}
			else{
				// provo con lowerCase
				value = this.reader.getValue(prop+(nome.toLowerCase()));
				if(value!=null){
					value = value.trim();
				}
				else{
					// provo con upperCase
					value = this.reader.getValue(prop+(nome.toUpperCase()));
					if(value!=null){
						value = value.trim();
					}
				}
			}
				
			return value;
		}catch(Exception e){
			this.log.error("Errore durante la lettura della proprieta ["+prop+nome+"]: "+e.getMessage(),e);
			return null;
		}
	}
	private String[] getTipiGestiti(String prefix,String ... defaults) throws Exception{
		Properties prop = this.reader.readProperties(prefix);
		Enumeration<?> en = prop.keys();
		List<String> tipi = new ArrayList<String>();
		if(defaults!=null && defaults.length>0){
			for(int i=0; i<defaults.length; i++){
				tipi.add(defaults[i]);
			}
		}
		while(en.hasMoreElements()){
			Object o = en.nextElement();
			if(o!=null){
				String v = (String)o;
				tipi.add(v.trim());
			}
		}
		if(tipi.size()>0){
			return tipi.toArray(new String[1]);
		}else{
			return null;
		}
	}
}
