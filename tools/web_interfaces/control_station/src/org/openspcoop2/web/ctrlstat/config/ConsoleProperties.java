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



package org.openspcoop2.web.ctrlstat.config;


import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.openspcoop2.core.mvc.properties.utils.PropertiesSourceConfiguration;
import org.openspcoop2.pdd.config.OpenSPCoop2ConfigurationException;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.io.ZipUtilities;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.slf4j.Logger;



/**
* ConsoleProperties
*
* @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
*/


public class ConsoleProperties {	

	/** Logger utilizzato per errori eventuali. */
	private Logger log = null;



	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Reader delle proprieta' impostate nel file 'console.properties' */
	private ConsoleInstanceProperties reader;

	/** Copia Statica */
	private static ConsoleProperties consoleProperties = null;


	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Viene chiamato in causa per istanziare il properties reader
	 *
	 * 
	 */
	public ConsoleProperties(String confDir, String confPropertyName, String confLocalPathPrefix,Logger log) throws Exception {

		if(log!=null)
			this.log = log;
		else
			this.log = LoggerWrapperFactory.getLogger(ConsoleProperties.class);
		
		/* ---- Lettura del cammino del file di configurazione ---- */
		Properties propertiesReader = new Properties();
		java.io.InputStream properties = null;
		try{  
			properties = ConsoleProperties.class.getResourceAsStream("/console.properties");
			if(properties==null){
				throw new Exception("File '/console.properties' not found");
			}
			propertiesReader.load(properties);
		}catch(Exception e) {
			this.log.error("Riscontrato errore durante la lettura del file 'console.properties': \n\n"+e.getMessage());
		    throw new Exception("ConsoleProperties initialize error: "+e.getMessage());
		}finally{
		    try{
				if(properties!=null)
				    properties.close();
		    }catch(Exception er){}
		}

		this.reader = new ConsoleInstanceProperties(propertiesReader, this.log, confDir, confPropertyName, confLocalPathPrefix);
	}


	/**
	 * Il Metodo si occupa di inizializzare il propertiesReader 
	 *
	 * 
	 */
	public static boolean initialize(String confDir, String confPropertyName, String confLocalPathPrefix,Logger log){

		try {
		    ConsoleProperties.consoleProperties = new ConsoleProperties(confDir,confPropertyName,confLocalPathPrefix,log);	
		    return true;
		}
		catch(Exception e) {
			log.error("Inizializzazione fallita: "+e.getMessage(),e);
		    return false;
		}
	}
    
	/**
	 * Ritorna l'istanza di questa classe
	 *
	 * @return Istanza di ClassNameProperties
	 * 
	 */
	public static ConsoleProperties getInstance() throws OpenSPCoop2ConfigurationException{
		if(ConsoleProperties.consoleProperties==null){
	    	throw new OpenSPCoop2ConfigurationException("ConsoleProperties non inizializzato");
	    }
	    return ConsoleProperties.consoleProperties;
	}
    
	public static void updateLocalImplementation(Properties prop){
		ConsoleProperties.consoleProperties.reader.setLocalObjectImplementation(prop);
	}








	/* ********  M E T O D I  ******** */

	private String readProperty(boolean required,String property) throws UtilsException{
		String tmp = this.reader.getValue_convertEnvProperties(property);
		if(tmp==null){
			if(required){
				throw new UtilsException("Property ["+property+"] not found");
			}
			else{
				return null;
			}
		}else{
			return tmp.trim();
		}
	}
	private Boolean readBooleanProperty(boolean required,String property) throws UtilsException{
		String tmp = this.readProperty(required, property);
		if("true".equalsIgnoreCase(tmp)==false && "false".equalsIgnoreCase(tmp)==false){
			throw new UtilsException("Property ["+property+"] with uncorrect value ["+tmp+"] (true/value expected)");
		}
		return Boolean.parseBoolean(tmp);
	}
	private Integer readIntegerProperty(boolean required,String property) throws UtilsException{
		String tmp = this.readProperty(required, property);
		try{
			return Integer.parseInt(tmp);
		}catch(Exception e){
			throw new UtilsException("Property ["+property+"] with uncorrect value ["+tmp+"] (int value expected)");
		}
	}
	private Long readLongProperty(boolean required,String property) throws UtilsException{
		String tmp = this.readProperty(required, property);
		try{
			return Long.parseLong(tmp);
		}catch(Exception e){
			throw new UtilsException("Property ["+property+"] with uncorrect value ["+tmp+"] (int value expected)");
		}
	}
	

	
	/* ----- Funzionalità Generiche -------- */
	
	public String getConfDirectory() throws UtilsException{
		return this.readProperty(false, "confDirectory");
	}
	
	public String getProtocolloDefault() throws UtilsException{
		return this.readProperty(false, "protocolloDefault");
	}
	
	public long getGestioneSerializableDB_AttesaAttiva() throws UtilsException {	
		return this.readLongProperty(false, "jdbc.serializable.attesaAttiva");
	}
	
	public int getGestioneSerializableDB_CheckInterval() throws UtilsException {	
		return this.readIntegerProperty(false, "jdbc.serializable.check");
	}
	
	public Boolean isSinglePdD() throws UtilsException{
		return this.readBooleanProperty(true, "singlePdD");
	}
	
	public Boolean isToken_GenerazioneAutomaticaPorteDelegate_enabled() throws UtilsException{
		return this.readBooleanProperty(true, "generazioneAutomaticaPorteDelegate.token.enabled");
	}
	
	public Boolean isAutenticazione_GenerazioneAutomaticaPorteDelegate_enabled() throws UtilsException{
		return this.readBooleanProperty(true, "generazioneAutomaticaPorteDelegate.autenticazione.enabled");
	}
	public String getAutenticazione_GenerazioneAutomaticaPorteDelegate() throws UtilsException{
		return this.readProperty(true, "generazioneAutomaticaPorteDelegate.autenticazione");
	}
	
	public Boolean isAutorizzazione_GenerazioneAutomaticaPorteDelegate_enabled() throws UtilsException{
		return this.readBooleanProperty(true, "generazioneAutomaticaPorteDelegate.autorizzazione.enabled");
	}
	public String getAutorizzazione_GenerazioneAutomaticaPorteDelegate() throws UtilsException{
		return this.readProperty(true, "generazioneAutomaticaPorteDelegate.autorizzazione");
	}
	
	public Boolean isToken_GenerazioneAutomaticaPorteApplicative_enabled() throws UtilsException{
		return this.readBooleanProperty(true, "generazioneAutomaticaPorteApplicative.token.enabled");
	}
	
	public Boolean isAutenticazione_GenerazioneAutomaticaPorteApplicative_enabled() throws UtilsException{
		return this.readBooleanProperty(true, "generazioneAutomaticaPorteApplicative.autenticazione.enabled");
	}
	public String getAutenticazione_GenerazioneAutomaticaPorteApplicative() throws UtilsException{
		return this.readProperty(true, "generazioneAutomaticaPorteApplicative.autenticazione");
	}
	
	public Boolean isAutorizzazione_GenerazioneAutomaticaPorteApplicative_enabled() throws UtilsException{
		return this.readBooleanProperty(true, "generazioneAutomaticaPorteApplicative.autorizzazione.enabled");
	}
	public String getAutorizzazione_GenerazioneAutomaticaPorteApplicative() throws UtilsException{
		return this.readProperty(true, "generazioneAutomaticaPorteApplicative.autorizzazione");
	}
	
	public Boolean isAbilitatoControlloUnicitaImplementazionePortTypePerSoggetto() throws UtilsException{
		return this.readBooleanProperty(true, "accordi.portType.implementazioneUnicaPerSoggetto");
	}
	
	public Boolean isAbilitatoControlloUnicitaImplementazioneAccordoPerSoggetto() throws UtilsException{
		return this.readBooleanProperty(true, "accordi.implementazioneUnicaPerSoggetto");
	}
	
	public String getImportArchive_tipoPdD() throws UtilsException{
		return this.readProperty(true, "importArchive.tipoPdD");
	}
	
	public boolean isExportArchive_configurazione_soloDumpCompleto() throws UtilsException{
		return this.readBooleanProperty(true, "exportArchive.configurazione.soloDumpCompleto");
	}
	
	public boolean isExportArchive_servizi_standard() throws UtilsException{
		return this.readBooleanProperty(true, "exportArchive.servizi.standard");
	}
	
	public boolean isGestoreConsistenzaDatiEnabled() throws UtilsException{
		return this.readBooleanProperty(true, "gestoreConsistenzaDati");
	}
	
	public boolean isGestoreConsistenzaDati_forceCheckMapping() throws UtilsException{
		return this.readBooleanProperty(true, "gestoreConsistenzaDati.forceCheckMapping");
	}
	
	public String getConsolePasswordVerifier() throws UtilsException{
		return this.readProperty(false, "console.passwordVerifier");
	}
	
	public PropertiesSourceConfiguration getMessageSecurityPropertiesSourceConfiguration() throws UtilsException {
		return _getSourceConfiguration("messageSecurity", 
				"messageSecurity.dir", "messageSecurity.dir.refresh", 
				"messageSecurity.builtIn", "messageSecurity.builtIn.refresh");
	}
	
	public PropertiesSourceConfiguration getPolicyGestioneTokenPropertiesSourceConfiguration() throws UtilsException {
		return _getSourceConfiguration("policyGestioneToken", 
				"policyGestioneToken.dir", "policyGestioneToken.dir.refresh", 
				"policyGestioneToken.builtIn", "policyGestioneToken.builtIn.refresh");
	}
	
	public boolean isAuditingRegistrazioneElementiBinari() throws UtilsException{
		return this.readBooleanProperty(true, "auditing.registrazioneElementiBinari");
	}
	
	public boolean isIntegrationManagerEnabled() throws UtilsException{
		return this.readBooleanProperty(true, "integrationManager.enabled");
	}
	
	public boolean isAccordiCooperazioneEnabled() throws UtilsException{
		return this.readBooleanProperty(true, "accordiCooperazione.enabled");
	}
	
	
	/* ----- Impostazioni grafiche ------- */
	
	public String getConsoleNomeSintesi() throws UtilsException{
		return this.readProperty(true, "console.nome.sintesi");
	}
	
	public String getConsoleNomeEsteso() throws UtilsException{
		return this.readProperty(true, "console.nome.esteso");
	}
	
	public String getConsoleNomeEstesoSuffix() throws UtilsException{
		if(_consoleReadSuffix==null){
			ConsoleProperties.initConsoleNomeEstesoSuffix(this);
		}
		return _consoleSuffix;
	}
	
	private static String _consoleSuffix = null;
	private static Boolean _consoleReadSuffix = null;
	private static synchronized void initConsoleNomeEstesoSuffix(ConsoleProperties p) throws UtilsException{
		if(_consoleReadSuffix==null){
			
			_consoleReadSuffix = true;
			
			String classLicenseValidator = p.readProperty(false, "console.licenseValidator");
			String classLicenseValidatorFactory = p.readProperty(false, "console.licenseValidator.factory");
			if(classLicenseValidator == null || classLicenseValidatorFactory==null){
				return;
			}
			else{
				Class<?> cFactory = null;
				try{
					cFactory = Class.forName(classLicenseValidatorFactory);
				}catch(Exception e){
					throw new UtilsException("[ClassForNameFactory] "+e.getMessage(),e);
				}
				Method mFactory = null;
				try{
					Method [] ms =cFactory.getMethods();
					if(ms==null || ms.length<=0){
						throw new Exception("Non esistono metodi [Factory]");
					}
					for (int i = 0; i < ms.length; i++) {
						
						if("getInstance".equals(ms[i].getName())==false){
							continue;
						}
						
						Type[]types = ms[i].getGenericParameterTypes();
	
						if(types!=null && types.length==1){
							if( (types[0].toString().equals("class org.slf4j.Logger") || types[0].toString().equals("interface org.slf4j.Logger")) ){
								mFactory = ms[i];
								break;
							}
						}
	
					}
	
					if(mFactory==null){
						throw new Exception("Metodo Factory non trovato");
					}
	
				}catch(Exception e){
					throw new UtilsException("[getMethodFactory] "+e.getMessage(),e);
				}
				Object oFactory = null;
				try{
					oFactory = mFactory.invoke(null, p.log);
				}catch(Exception e){
					throw new UtilsException("[invokeFactory] "+e.getMessage(),e);
				}
				if(oFactory==null){
					throw new UtilsException("Factory not found");
				}
				Class<?> c = null;
				try{
					c = Class.forName(classLicenseValidator);
				}catch(Exception e){
					throw new UtilsException("[ClassForName] "+e.getMessage(),e);
				}
				Method m = null;
				try{
					Method [] ms =c.getMethods();
					if(ms==null || ms.length<=0){
						throw new Exception("Non esistono metodi");
					}
					for (int i = 0; i < ms.length; i++) {
						Type[]types = ms[i].getGenericParameterTypes();
	
						if(types!=null && types.length==3){
							if( (types[0].toString().equals("class org.slf4j.Logger") || types[0].toString().equals("interface org.slf4j.Logger")) && 
									(types[1].toString().equals("class "+classLicenseValidatorFactory)) && 
									(types[2].toString().equals("boolean")) ){
								m = ms[i];
								break;
							}
						}
	
					}
	
					if(m==null){
						throw new Exception("Metodo non trovato");
					}
	
				}catch(Exception e){
					throw new UtilsException("[getMethod] "+e.getMessage(),e);
				}
	
				Object o = null;
				try{
					o = m.invoke(null, p.log, oFactory,true);
				}catch(Exception e){
					throw new UtilsException("[invoke] "+e.getMessage(),e);
				}
				if(o==null){
					throw new UtilsException("License not found");
				}
				// Recupero titolo
				Method mTitolo = null;
				try{
					mTitolo = o.getClass().getMethod("getTitlePddMonitor");
				}catch(Exception e){
					throw new UtilsException("[getMethod_TitoloPddMonitor] "+e.getMessage(),e);
				}
				Object oTitoloPddMonitor = null;
				try{
					oTitoloPddMonitor = mTitolo.invoke(o);
				}catch(Exception e){
					throw new UtilsException("[invoke_TitoloPddMonitor] "+e.getMessage(),e);
				}
				if(oTitoloPddMonitor==null){
					throw new UtilsException("TitoloPddMonitor not found");
				}
				if( !(oTitoloPddMonitor instanceof String) ){
					throw new UtilsException("TitoloPddMonitor with wrong type ["+oTitoloPddMonitor.getClass().getName()+"], excepected: "+String.class.getName());
				}
				_consoleSuffix = (String) oTitoloPddMonitor;
				return;
			}
		}
	}
	
	public String getConsoleCSS() throws UtilsException{
		return this.readProperty(true, "console.css");
	}
	
	public String getConsoleLanguage() throws UtilsException{
		return this.readProperty(true, "console.language");
	}
	
	public int getConsoleLunghezzaLabel() throws UtilsException{
		String lunghezzaS = this.readProperty(true, "console.lunghezzaLabel");
		return Integer.parseInt(lunghezzaS); 
	}
	
	public int getConsoleNumeroColonneDefaultTextArea() throws UtilsException{
		String lunghezzaS = this.readProperty(true, "console.colonneTextArea.default");
		return Integer.parseInt(lunghezzaS); 
	}
	
	public String getLogoHeaderImage() throws Exception{
		return this.readProperty(false,"console.header.logo.image");
	}

	public String getLogoHeaderTitolo() throws Exception{
		return this.readProperty(false,"console.header.logo.titolo");
	}

	public String getLogoHeaderLink() throws Exception{
		return this.readProperty(false,"console.header.logo.link");
	}
	
	/* ----- Opzioni Accesso JMX della PdD ------- */
	
	public List<String> getJmxPdD_aliases() throws UtilsException {
		List<String> list = new ArrayList<String>();
		String tipo = this.readProperty(false, "risorseJmxPdd.aliases");
		if(tipo!=null && !"".equals(tipo)){
			String [] tmp = tipo.split(",");
			for (int i = 0; i < tmp.length; i++) {
				list.add(tmp[i].trim());
			}
		}
		return list;
	}
	
	public String getJmxPdD_descrizione(String alias) throws UtilsException {
		return this.readProperty(false, alias+".risorseJmxPdd.descrizione");
	}
	
	private String _getJmxPdD_value(boolean required, String alias, String prop) throws UtilsException{
		String tmp = this.readProperty(false, alias+"."+prop);
		if(tmp==null || "".equals(tmp)){
			tmp = this.readProperty(required, prop);
		}
		return tmp;
	}
	
	public String getJmxPdD_tipoAccesso(String alias) throws UtilsException {
		String tipo = _getJmxPdD_value(true, alias, "risorseJmxPdd.tipoAccesso");
		if(!CostantiControlStation.RESOURCE_JMX_PDD_TIPOLOGIA_ACCESSO_JMX.equals(tipo) && !CostantiControlStation.RESOURCE_JMX_PDD_TIPOLOGIA_ACCESSO_OPENSPCOOP.equals(tipo)){
			throw new UtilsException("Tipo ["+tipo+"] non supportato per la proprieta' 'risorseJmxPdd.tipoAccesso'");
		}
		return tipo;
	}
	public String getJmxPdD_remoteAccess_username(String alias) throws UtilsException {
		return _getJmxPdD_value(false, alias, "risorseJmxPdd.remoteAccess.username");
	}
	public String getJmxPdD_remoteAccess_password(String alias) throws UtilsException {
		return _getJmxPdD_value(false, alias, "risorseJmxPdd.remoteAccess.password");
	}
	public String getJmxPdD_remoteAccess_applicationServer(String alias) throws UtilsException {
		return _getJmxPdD_value(false, alias, "risorseJmxPdd.remoteAccess.as");
	}
	public String getJmxPdD_remoteAccess_factory(String alias) throws UtilsException {
		return _getJmxPdD_value(false, alias, "risorseJmxPdd.remoteAccess.factory");
	}
	public String getJmxPdD_remoteAccess_url(String alias) throws UtilsException {
		return _getJmxPdD_value(false, alias, "risorseJmxPdd.remoteAccess.url");
	}
	public String getJmxPdD_dominio(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.dominio");
	}
	public String getJmxPdD_configurazioneSistema_type(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.tipo");
	}
	public String getJmxPdD_configurazioneSistema_nomeRisorsa(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeRisorsa");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_versionePdD(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.versionePdD");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_versioneBaseDati(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.versioneBaseDati");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_versioneJava(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.versioneJava");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_vendorJava(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.vendorJava");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_tipoDatabase(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.tipoDatabase");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_informazioniDatabase(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.infoDatabase");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_informazioniSSL(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.infoSSL");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_informazioniCompleteSSL(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.infoSSLComplete");
	}
	public boolean isJmxPdD_configurazioneSistema_showInformazioniCryptographyKeyLength() throws UtilsException {
		String tmp = this.readProperty(false, "risorseJmxPdd.configurazioneSistema.infoCryptographyKeyLength.show");
		if(tmp==null || "".equals(tmp)){
			return false;
		}
		return "true".equalsIgnoreCase(tmp.trim());
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_informazioniCryptographyKeyLength(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.infoCryptographyKeyLength");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_informazioniInternazionalizzazione(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.infoInternazionalizzazione");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_informazioniCompleteInternazionalizzazione(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.infoInternazionalizzazioneComplete");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_informazioniTimeZone(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.infoTimeZone");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_informazioniCompleteTimeZone(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.infoTimeZoneComplete");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_informazioniProprietaJavaNetworking(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.infoProprietaJavaNetworking");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_informazioniCompleteProprietaJavaNetworking(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.infoProprietaJavaNetworkingComplete");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_informazioniProprietaJavaAltro(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.infoProprietaJavaAltro");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_informazioniProprietaSistema(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.infoProprietaSistema");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_messageFactory(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.messageFactory");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_directoryConfigurazione(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.confDir");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_pluginProtocols(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.pluginProtocols");
	}
	public String getJmxPdD_configurazioneSistema_nomeRisorsaMonitoraggio(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeRisorsaMonitoraggio");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_connessioniDB(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.connessioniDB");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_connessioniJMS(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.connessioniJMS");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_idTransazioniAttive(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.transazioniID");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_idProtocolloTransazioniAttive(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.transazioniIDProtocollo");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_connessioniPD(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.connessioniPD");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_connessioniPA(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.connessioniPA");
	}
	public String getJmxPdD_configurazioneSistema_nomeRisorsaConfigurazionePdD(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeRisorsaConfigurazionePdD");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_severitaDiagnostici(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.severitaDiagnostici");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_severitaDiagnosticiLog4j(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.severitaDiagnosticiLog4j");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_tracciamento(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.tracciamento");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_dumpPD(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.dumpBinarioPD");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_dumpPA(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.dumpBinarioPA");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_log4jDiagnostica(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.log4jDiagnostica");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_log4jOpenspcoop(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.log4jOpenspcoop");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_log4jIntegrationManager(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.log4jIntegrationManager");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_log4jTracciamento(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.log4jTracciamento");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_log4jDump(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.log4jDump");
	}
	public String getJmxPdD_configurazioneSistema_nomeRisorsaStatoServiziPdD(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeRisorsaStatoServiziPdD");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaDelegata(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.statoServizioPortaDelegata");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaDelegataAbilitazioniPuntuali(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.statoServizioPortaDelegataAbilitazioniPuntuali");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaDelegataDisabilitazioniPuntuali(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.statoServizioPortaDelegataDisabilitazioniPuntuali");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaApplicativa(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.statoServizioPortaApplicativa");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaApplicativaAbilitazioniPuntuali(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.statoServizioPortaApplicativaAbilitazioniPuntuali");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaApplicativaDisabilitazioniPuntuali(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.statoServizioPortaApplicativaDisabilitazioniPuntuali");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_statoServizioIntegrationManager(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.statoServizioIntegrationManager");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_abilitaServizioPortaDelegata(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.abilitaServizioPortaDelegata");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_disabilitaServizioPortaDelegata(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.disabilitaServizioPortaDelegata");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_abilitaServizioPortaApplicativa(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.abilitaServizioPortaApplicativa");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_disabilitaServizioPortaApplicativa(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.disabilitaServizioPortaApplicativa");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_abilitaServizioIntegrationManager(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.abilitaServizioIntegrationManager");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_disabilitaServizioIntegrationManager(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.disabilitaServizioIntegrationManager");
	}
	public List<String> getJmxPdD_caches(String alias) throws UtilsException {
		return this.read_jmx_caches(alias, "risorseJmxPdd.caches");
	}
	public List<String> getJmxPdD_caches_prefill(String alias) throws UtilsException {
		return this.read_jmx_caches(alias, "risorseJmxPdd.caches.prefill");
	}
	private List<String> read_jmx_caches(String alias,String property) throws UtilsException {
		List<String> list = new ArrayList<String>();
		String tipo = _getJmxPdD_value(false, alias, property);
		if(tipo!=null && !"".equals(tipo)){
			String [] tmp = tipo.split(",");
			for (int i = 0; i < tmp.length; i++) {
				list.add(tmp[i].trim());
			}
		}
		return list;
	}
	public String getJmxPdD_cache_type(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.cache.tipo");
	}
	public String getJmxPdD_cache_nomeAttributo_cacheAbilitata(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.cache.nomeAttributo.cacheAbilitata");
	}
	public String getJmxPdD_cache_nomeMetodo_statoCache(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.cache.nomeMetodo.statoCache");
	}
	public String getJmxPdD_cache_nomeMetodo_resetCache(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.cache.nomeMetodo.resetCache");
	}
	public String getJmxPdD_cache_nomeMetodo_prefillCache(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.cache.nomeMetodo.prefillCache");
	}
	
	
	/* ----- Opzioni di Visualizzazione ----- */
	
	public Boolean isShowJ2eeOptions() throws UtilsException{
		String tmp = this.readProperty(true, "server.tipo");
		if("web".equals(tmp)){
			return false;
		}
		else{
			return true;
		}
	}
	
	public Boolean isConsoleConfigurazioniPersonalizzate() throws UtilsException{
		return this.readBooleanProperty(true, "console.configurazioniPersonalizzate");
	}
	
	public Boolean isConsoleGestioneSoggettiVirtuali() throws UtilsException{
		return this.readBooleanProperty(true, "console.gestioneSoggettiVirtuali");
	}
	
	public Boolean isConsoleGestioneSoggettiRouter() throws UtilsException{
		return this.readBooleanProperty(true, "console.gestioneSoggettiRouter");
	}
	
	public Boolean isConsoleGestioneWorkflowStatoDocumenti() throws UtilsException{
		return this.readBooleanProperty(true, "console.gestioneWorkflowStatoDocumenti");
	}
	
	public Boolean isConsoleGestioneWorkflowStatoDocumenti_visualizzaStatoLista() throws UtilsException{
		return this.readBooleanProperty(true, "console.gestioneWorkflowStatoDocumenti.visualizzaStatoLista");
	}
	
	public Boolean isConsoleGestioneWorkflowStatoDocumenti_ripristinoStatoOperativoDaFinale() throws UtilsException{
		return this.readBooleanProperty(true, "console.gestioneWorkflowStatoDocumenti.finale.ripristinoStatoOperativo");
	}
	
	public Boolean isConsoleInterfacceAPI_visualizza() throws UtilsException{
		return this.readBooleanProperty(true, "console.interfacceAPI.visualizza");
	}
	
	public Boolean isConsoleAllegati_visualizza() throws UtilsException{
		return this.readBooleanProperty(true, "console.allegati.visualizza");
	}
	
	public Boolean isEnableAutoMappingWsdlIntoAccordo() throws UtilsException{
		return this.readBooleanProperty(true, "console.gestioneWsdl.autoMappingInAccordo");
	}
	
	public Boolean isEnableAutoMappingWsdlIntoAccordo_estrazioneSchemiInWsdlTypes() throws UtilsException{
		return this.readBooleanProperty(true, "console.gestioneWsdl.autoMappingInAccordo.estrazioneSchemiInWsdlTypes");
	}
	
	public Boolean isMenuVisualizzaFlagPrivato() throws UtilsException{
		return this.readBooleanProperty(true, "menu.visualizzaFlagPrivato");
	}
	
	public Boolean isMenuVisualizzaListaCompletaConnettori() throws UtilsException{
		return this.readBooleanProperty(true, "menu.visualizzaListaCompletaConnettori");
	}
	
	public Boolean isMenuVisualizzaOpzioneDebugConnettore() throws UtilsException{
		return this.readBooleanProperty(true, "menu.visualizzaOpzioneDebugConnettore");
	}
	
	public Boolean isMenuAccordiVisualizzaCorrelazioneAsincrona() throws UtilsException{
		return this.readBooleanProperty(true, "menu.accordi.visualizzaCorrelazioneAsincrona");
	}
	
	public Boolean isMenuAccordiVisualizzazioneGestioneInformazioniProtocollo() throws UtilsException{
		return this.readBooleanProperty(true, "menu.accordi.visualizzazioneGestioneInformazioniProtocollo");
	}
	
	public Boolean isMenuMTOMVisualizzazioneCompleta() throws UtilsException{
		return this.readBooleanProperty(true, "menu.mtom.visualizzazioneCompleta");
	}
	
	public Integer getPortaCorrelazioneApplicativaMaxLength() throws UtilsException{
		return this.readIntegerProperty(true, "menu.porte.correlazioneApplicativa.maxLength");
	}
	
	public Boolean isMenuPortaDelegataLocalForward() throws UtilsException{
		return this.readBooleanProperty(true, "menu.porte.localForward");
	}
	
	public Boolean isMenuConfigurazioneVisualizzazioneDiagnosticaTracciatura() throws UtilsException{
		String p = "menu.configurazione.visualizzazioneDiagnosticaTracciatura.standard";
		String tmp = this.readProperty(false, p);
		if(tmp==null){
			return true; // standard per default
		}
		if("true".equalsIgnoreCase(tmp)==false && "false".equalsIgnoreCase(tmp)==false){
			throw new UtilsException("Property ["+p+"] with uncorrect value ["+tmp+"] (true/value expected)");
		}
		return Boolean.parseBoolean(tmp);
	}
	
	public Boolean isElenchiVisualizzaCountElementi() throws UtilsException{
		return this.readBooleanProperty(true, "elenchi.visualizzaCountElementi");
	}
	
	public Boolean isElenchiRicercaConservaCriteri() throws UtilsException{
		return this.readBooleanProperty(true, "elenchi.ricerca.conservaCriteri");
	}
	
	public Boolean isElenchiAccordiVisualizzaColonnaAzioni() throws UtilsException{
		return this.readBooleanProperty(true, "elenchi.accordi.visualizzaColonnaAzioni");
	}
	
	public Boolean isElenchiSA_asincroniNonSupportati_VisualizzaRispostaAsincrona() throws UtilsException{
		return this.readBooleanProperty(true, "elenchi.serviziApplicativi.asincroniNonSupportati.visualizzazioneRispostaAsincrona");
	}
	
	public Boolean isElenchiMenuVisualizzazionePulsantiImportExportPackage() throws UtilsException{
		return this.readBooleanProperty(true, "elenchi_menu.visualizzazionePulsantiImportExportPackage");
	}
	
	public String getTokenPolicyForceId() throws UtilsException{
		return this.readProperty(false, "console.tokenPolicy.forceId");
	}
	
	public Boolean isEnableServiziVisualizzaModalitaElenco() throws UtilsException{
		return this.readBooleanProperty(true, "console.servizi.visualizzaModalitaElenco");
	}
	
	public Integer getNumeroMassimoSoggettiOperativiMenuUtente() throws UtilsException{
		return this.readIntegerProperty(true, "console.selectListSoggettiOperativi.numeroMassimoSoggettiVisualizzati");
	}
	
	public Integer getLunghezzaMassimaLabelSoggettiOperativiMenuUtente() throws Exception{
		return this.readIntegerProperty(true, "console.selectListSoggettiOperativi.lunghezzaMassimaLabel");
	}
	
	/* ---------------- Gestione govwayConsole centralizzata ----------------------- */

	public Boolean isGestioneCentralizzata_SincronizzazionePdd() throws UtilsException{
		return this.readBooleanProperty(true, "sincronizzazionePdd");
	}
	
	public Boolean isGestioneCentralizzata_SincronizzazioneRegistro() throws UtilsException{
		return this.readBooleanProperty(true, "sincronizzazioneRegistro");
	}
	
	public Boolean isGestioneCentralizzata_SincronizzazioneGestoreEventi() throws UtilsException{
		return this.readBooleanProperty(true, "sincronizzazioneGE");
	}
	
	public String getGestioneCentralizzata_NomeCodaSmistatore() throws UtilsException{
		return this.readProperty(true, "SmistatoreQueue");
	}
	
	public String getGestioneCentralizzata_NomeCodaRegistroServizi() throws UtilsException{
		return this.readProperty(true, "RegistroServiziQueue");
	}
	
	public String getGestioneCentralizzata_WSRegistroServizi_endpointPdd() throws UtilsException{
		return this.readProperty(true, "RegistroServiziWS.endpoint.portaDominio");
	}
	
	public String getGestioneCentralizzata_WSRegistroServizi_endpointSoggetto() throws UtilsException{
		return this.readProperty(true, "RegistroServiziWS.endpoint.soggetto");
	}
	
	public String getGestioneCentralizzata_WSRegistroServizi_endpointAccordoCooperazione() throws UtilsException{
		return this.readProperty(true, "RegistroServiziWS.endpoint.accordoCooperazione");
	}
	
	public String getGestioneCentralizzata_WSRegistroServizi_endpointAccordoServizioParteComune() throws UtilsException{
		return this.readProperty(true, "RegistroServiziWS.endpoint.accordoServizioParteComune");
	}
	
	public String getGestioneCentralizzata_WSRegistroServizi_endpointAccordoServizioParteSpecifica() throws UtilsException{
		return this.readProperty(true, "RegistroServiziWS.endpoint.accordoServizioParteSpecifica");
	}
	
	public String getGestioneCentralizzata_WSRegistroServizi_credenzialiBasic_username() throws UtilsException{
		return this.readProperty(false, "RegistroServiziWS.username");
	}
	
	public String getGestioneCentralizzata_WSRegistroServizi_credenzialiBasic_password() throws UtilsException{
		return this.readProperty(false, "RegistroServiziWS.password");
	}
	
	public String getGestioneCentralizzata_PrefissoNomeCodaConfigurazionePdd() throws UtilsException{
		return this.readProperty(true, "PdDQueuePrefix");
	}
	
	public String getGestioneCentralizzata_GestorePddd_ScriptShell_Path() throws UtilsException{
		return this.readProperty(false, "GestorePdD.script.path");
	}
	
	public String getGestioneCentralizzata_GestorePddd_ScriptShell_Args() throws UtilsException{
		return this.readProperty(false, "GestorePdD.script.args");
	}
	
	public String getGestioneCentralizzata_WSConfigurazione_endpointSuffixPortaApplicativa() throws UtilsException{
		return this.readProperty(true, "ConfigurazioneWS.endpoint.suffix.portaApplicativa");
	}
	
	public String getGestioneCentralizzata_WSConfigurazione_endpointSuffixPortaDelegata() throws UtilsException{
		return this.readProperty(true, "ConfigurazioneWS.endpoint.suffix.portaDelegata");
	}
	
	public String getGestioneCentralizzata_WSConfigurazione_endpointSuffixServizioApplicativo() throws UtilsException{
		return this.readProperty(true, "ConfigurazioneWS.endpoint.suffix.servizioApplicativo");
	}
	
	public String getGestioneCentralizzata_WSConfigurazione_endpointSuffixSoggetto() throws UtilsException{
		return this.readProperty(true, "ConfigurazioneWS.endpoint.suffix.soggetto");
	}
	
	public String getGestioneCentralizzata_WSConfigurazione_credenzialiBasic_username() throws UtilsException{
		return this.readProperty(false, "ConfigurazioneWS.username");
	}
	
	public String getGestioneCentralizzata_WSConfigurazione_credenzialiBasic_password() throws UtilsException{
		return this.readProperty(false, "ConfigurazioneWS.password");
	}
	
	public String getGestioneCentralizzata_NomeCodaGestoreEventi() throws UtilsException{
		return this.readProperty(true, "GestoreEventiQueue");
	}
	
	public String getGestioneCentralizzata_PrefissoWSGestoreEventi() throws UtilsException{
		return this.readProperty(true, "UrlWebServiceGestoreEventi");
	}
	
	public String getGestioneCentralizzata_GestoreEventiTipoSoggetto() throws UtilsException{
		return this.readProperty(true, "gestoreEventi.tipo_soggetto");
	}
	
	public String getGestioneCentralizzata_GestoreEventiNomeSoggetto() throws UtilsException{
		return this.readProperty(true, "gestoreEventi.nome_soggetto");
	}
	
	public String getGestioneCentralizzata_GestoreEventiNomeServizioApplicativo() throws UtilsException{
		return this.readProperty(true, "gestoreEventi.nome_servizio_applicativo");
	}
	
	public String getGestioneCentralizzata_WSMonitor_pddDefault() throws UtilsException{
		return this.readProperty(true, "MonitoraggioWS.pdd.default");
	}
	
	public String getGestioneCentralizzata_WSMonitor_endpointSuffixStatoPdd() throws UtilsException{
		return this.readProperty(true, "MonitoraggioWS.endpoint.suffix.statoPdd");
	}
	
	public String getGestioneCentralizzata_WSMonitor_endpointSuffixMessaggio() throws UtilsException{
		return this.readProperty(true, "MonitoraggioWS.endpoint.suffix.messaggio");
	}
	
	public String getGestioneCentralizzata_WSMonitor_credenzialiBasic_username() throws UtilsException{
		return this.readProperty(false, "MonitoraggioWS.username");
	}
	
	public String getGestioneCentralizzata_WSMonitor_credenzialiBasic_password() throws UtilsException{
		return this.readProperty(false, "MonitoraggioWS.password");
	}
	
	public String getGestioneCentralizzata_URLContextCreazioneAutomaticaSoggetto() throws UtilsException{
		return this.readProperty(true, "UrlConnettoreSoggetto");
	}
	
	public String getGestioneCentralizzata_PddIndirizzoIpPubblico() throws UtilsException{
		return this.readProperty(true, "pdd.indirizzoIP.pubblico");
	}
	
	public Integer getGestioneCentralizzata_PddPortaPubblica() throws UtilsException{
		return this.readIntegerProperty(true, "pdd.porta.pubblica");
	}
	
	public String getGestioneCentralizzata_PddIndirizzoIpGestione() throws UtilsException{
		return this.readProperty(true, "pdd.indirizzoIP.gestione");
	}
	
	public Integer getGestioneCentralizzata_PddPortaGestione() throws UtilsException{
		return this.readIntegerProperty(true, "pdd.porta.gestione");
	}
	
	
	
	/* ---------------- Gestione govwayConsole locale ----------------------- */

	public Boolean isSinglePdD_GestionePdd() throws UtilsException{
		return this.readBooleanProperty(true, "singlePdD.pdd.enabled");
	}
	
	public Boolean isSinglePdD_RegistroServiziLocale() throws UtilsException{
		return this.readBooleanProperty(true, "singlePdD.registroServizi.locale");
	}
	
	public Boolean isSinglePdD_TracceConfigurazioneCustomAppender() throws UtilsException{
		return this.readBooleanProperty(true, "tracce.configurazioneCustomAppender");
	}
	
	public Boolean isSinglePdD_TracceGestioneSorgentiDatiPrelevataDaDatabase() throws UtilsException{
		return this.readBooleanProperty(true, "tracce.sorgentiDati.database");
	}
	
	public Boolean isSinglePdD_MessaggiDiagnosticiConfigurazioneCustomAppender() throws UtilsException{
		return this.readBooleanProperty(true, "msgDiagnostici.configurazioneCustomAppender");
	}
	
	public Boolean isSinglePdD_MessaggiDiagnosticiGestioneSorgentiDatiPrelevataDaDatabase() throws UtilsException{
		return this.readBooleanProperty(true, "msgDiagnostici.sorgentiDati.database");
	}
	
	public Boolean isSinglePdD_DumpConfigurazioneCustomAppender() throws UtilsException{
		return this.readBooleanProperty(true, "dump.configurazioneCustomAppender");
	}
	
	public Boolean isSinglePdD_DumpConfigurazioneRealtime() throws UtilsException{
		return this.readBooleanProperty(true, "dump.configurazioneRealtime");
	}
	
	
	/* ---- Plugins ------ */
	
	public String getExtendedInfoDriverConfigurazione() throws UtilsException{
		return this.readProperty(false, "extendedInfo.configurazione");
	}
	public String getExtendedInfoDriverPortaDelegata() throws UtilsException{
		return this.readProperty(false, "extendedInfo.portaDelegata");
	}
	public String getExtendedInfoDriverPortaApplicativa() throws UtilsException{
		return this.readProperty(false, "extendedInfo.portaApplicativa");
	}
	
	public String[] getPlugins_Menu() throws UtilsException{
		String p = this.readProperty(false, "plugins.menu");
		if(p!=null && !"".equals(p.trim())){
			String [] tmp = p.trim().split(",");
			for (int i = 0; i < tmp.length; i++) {
				tmp[i] = tmp[i].trim();
			}
			return tmp;
		}
		return null;
	}
	
	public String[] getPlugins_Configurazione() throws UtilsException{
		String p = this.readProperty(false, "plugins.configurazione");
		if(p!=null && !"".equals(p.trim())){
			String [] tmp = p.trim().split(",");
			for (int i = 0; i < tmp.length; i++) {
				tmp[i] = tmp[i].trim();
			}
			return tmp;
		}
		return null;
	}
	
	public String getPlugins_ConfigurazioneList() throws UtilsException{
		return this.readProperty(false, "plugins.configurazione.list");
	}
	
	public String[] getPlugins_Connettore() throws UtilsException{
		String p = this.readProperty(false, "plugins.connettore");
		if(p!=null && !"".equals(p.trim())){
			String [] tmp = p.trim().split(",");
			for (int i = 0; i < tmp.length; i++) {
				tmp[i] = tmp[i].trim();
			}
			return tmp;
		}
		return null;
	}
	
	public String getPlugins_PortaDelegata() throws UtilsException{
		return this.readProperty(false, "plugins.portaDelegata");
	}
	
	public String getPlugins_PortaApplicativa() throws UtilsException{
		return this.readProperty(false, "plugins.portaApplicativa");
	}
	
	
	
	/* ---- Gestione Visibilità utenti ------ */

	public Boolean isVisibilitaOggettiGlobale() throws UtilsException{
		String tmp = this.readProperty(true, "visibilitaOggetti");
		if("locale".equals(tmp)){
			return false;
		}else{
			return true;
		}
	}
	
	public List<String> getUtentiConVisibilitaGlobale() throws UtilsException{
		String utenti = this.readProperty(false, "utentiConVisibilitaGlobale");
		List<String> lista = new ArrayList<String>();
		if(utenti!=null){
			String [] tmp = utenti.trim().split(",");
			if(tmp!=null){
				for(int i=0; i<tmp.length;i++){
					lista.add(tmp[i].trim());
				}
			}
		}
		return lista;
	}


	/* ---- Utiltiies interne ------ */
	
	public PropertiesSourceConfiguration _getSourceConfiguration(String id, 
			String propertyExternalDir, String propertyExternalDirRefresh,
			String propertyBuiltIn, String propertyBuiltInRefresh) throws UtilsException {
		
		PropertiesSourceConfiguration config = new PropertiesSourceConfiguration();
		config.setId(id);
		
		String dir = this.readProperty(false, propertyExternalDir);
		if(dir!=null) {
			File f = new File(dir);
			if(f.exists()) {
				config.setDirectory(f.getAbsolutePath());
			}
		}
		
		String dir_refresh = this.readProperty(false, propertyExternalDirRefresh);
		if(dir_refresh!=null) {
			config.setUpdate("true".equalsIgnoreCase(dir_refresh.trim()));
		}
		
		String buildInt = this.readProperty(false, propertyBuiltIn);
		if(buildInt!=null) {
			try {
				//System.out.println("BASE: "+buildInt);
				InputStream is = ConsoleProperties.class.getResourceAsStream(buildInt);
				try {
					if(is!=null) {
						byte [] zipContent = Utilities.getAsByteArray(is);
						is.close();
						is = null;
						File fTmp = File.createTempFile("propertiesSourceConfiguration", ".zip");
						//System.out.println("TMP: "+fTmp.getAbsolutePath());
						try {
							FileSystemUtilities.writeFile(fTmp, zipContent);
							ZipFile zip = new ZipFile(fTmp);
							Iterator<ZipEntry> itZip = ZipUtilities.entries(zip, true);
							List<byte[]> builtIdList = new ArrayList<>();
							while (itZip.hasNext()) {
								ZipEntry zipEntry = (ZipEntry) itZip.next();
								if(zipEntry.isDirectory() == false) {
									InputStream isZip = zip.getInputStream(zipEntry);
									try {
										//System.out.println("LEGGO ["+zipEntry.getName()+"]");
										byte [] bytes = Utilities.getAsByteArray(isZip);
										builtIdList.add(bytes);
									}finally {
										try {
											if(isZip!=null) {
												isZip.close();
											}
										}catch(Exception e) {}
									}
								}
							}
							config.setBuiltIn(builtIdList);
						}finally {
							fTmp.delete();
						}
					}
				}finally {
					try {
						if(is!=null) {
							is.close();
						}
					}catch(Exception e) {}
				}
			}catch(Exception e) {
				throw new UtilsException(e.getMessage(),e);
			}
		}
		
		String builtIn_refresh = this.readProperty(false, propertyBuiltInRefresh);
		if(builtIn_refresh!=null) {
			config.setUpdate("true".equalsIgnoreCase(builtIn_refresh.trim()));
		}
		
		return config;

	}
	
	// propertiy per la gestione del console.font
	private String consoleFontName = null;
	private String consoleFontFamilyName = null;
	private int consoleFontStyle = -1;
	
	public String getConsoleFont() throws Exception{
		return this.readProperty(true,"console.font");
	}
	
	public String getConsoleFontName() {
		return this.consoleFontName;
	}

	public void setConsoleFontName(String consoleFontName) {
		this.consoleFontName = consoleFontName;
	}
	public String getConsoleFontFamilyName() {
		return this.consoleFontFamilyName;
	}

	public void setConsoleFontFamilyName(String consoleFontFamilyName) {
		this.consoleFontFamilyName = consoleFontFamilyName;
	}

	public int getConsoleFontStyle() {
		return this.consoleFontStyle;
	}

	public void setConsoleFontStyle(int consoleFontStyle) {
		this.consoleFontStyle = consoleFontStyle;
	}
}
