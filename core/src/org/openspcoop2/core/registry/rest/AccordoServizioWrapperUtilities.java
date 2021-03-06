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


package org.openspcoop2.core.registry.rest;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.message.xml.XMLUtils;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.rest.ApiFactory;
import org.openspcoop2.utils.rest.ApiFormats;
import org.openspcoop2.utils.rest.ApiReaderConfig;
import org.openspcoop2.utils.rest.IApiReader;
import org.openspcoop2.utils.rest.api.Api;
import org.openspcoop2.utils.rest.api.ApiSchema;
import org.openspcoop2.utils.rest.api.ApiSchemaType;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.openspcoop2.utils.xml.XSDUtils;
import org.slf4j.Logger;


/**
 * Classe utilizzata per leggere/modificare i wsdl che formano un accordo di un servizio
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class AccordoServizioWrapperUtilities {

	private Logger logger = null;
	private AbstractXMLUtils xmlUtils = null;
	private XSDUtils xsdUtils = null;
	
	/** Accordo Servizio */
	public AccordoServizioWrapperUtilities(Logger log, AccordoServizioWrapper accordoServizio){
		if(log!=null)
			this.logger = log;
		else
			this.logger = LoggerWrapperFactory.getLogger(AccordoServizioWrapperUtilities.class);
		this.accordoServizioWrapper = accordoServizio;
		this.xmlUtils = XMLUtils.getInstance();
		this.xsdUtils = new XSDUtils(this.xmlUtils);
	}
	private AccordoServizioWrapper accordoServizioWrapper = null;
	public AccordoServizioWrapper getAccordoServizioWrapper() {
		return this.accordoServizioWrapper;
	}
	public void setAccordoServizio(AccordoServizioWrapper accordoServizio) {
		this.accordoServizioWrapper = accordoServizio;
	}
	
	
	
	
	
	
	
	public void buildApiFromSpecific(boolean fromBytes,boolean buildSchemi) throws DriverRegistroServiziException {
		
		if(this.accordoServizioWrapper==null) {
			throw new DriverRegistroServiziException("AccordoWrapper non fornito");
		}
		if(this.accordoServizioWrapper.getAccordoServizio()==null) {
			throw new DriverRegistroServiziException("API non fornita");
		}
		AccordoServizioParteComune as = this.accordoServizioWrapper.getAccordoServizio();
		
		IApiReader apiReader = null;
		switch (as.getFormatoSpecifica()) {
		case WADL:
			try {
				apiReader = ApiFactory.newApiReader(ApiFormats.WADL);
			}catch(Exception e) {
				throw new DriverRegistroServiziException("Inizializzazione Reader Specifica ["+this.accordoServizioWrapper.getAccordoServizio().getFormatoSpecifica()+"] fallito: "+e.getMessage(),e);
			}
			break;
		case SWAGGER_2:
			try {
				apiReader = ApiFactory.newApiReader(ApiFormats.SWAGGER_2);
			}catch(Exception e) {
				throw new DriverRegistroServiziException("Inizializzazione Reader Specifica ["+this.accordoServizioWrapper.getAccordoServizio().getFormatoSpecifica()+"] fallito: "+e.getMessage(),e);
			}
			break;
		case OPEN_API_3:
			try {
				apiReader = ApiFactory.newApiReader(ApiFormats.OPEN_API_3);
			}catch(Exception e) {
				throw new DriverRegistroServiziException("Inizializzazione Reader Specifica ["+this.accordoServizioWrapper.getAccordoServizio().getFormatoSpecifica()+"] fallito: "+e.getMessage(),e);
			}
			break;
		case WSDL_11:
			throw new DriverRegistroServiziException("Tipo specifica ["+this.accordoServizioWrapper.getAccordoServizio().getFormatoSpecifica()+"] non supportata");
		}
		
		
		
		byte [] specifica = null;
		if(fromBytes){
			if(as.getByteWsdlConcettuale()!=null){
				specifica = as.getByteWsdlConcettuale();
			}
		}
		else{
			if(as.getWsdlConcettuale()!=null){
				try{
					String location = as.getWsdlConcettuale();
					if(location.startsWith("http://") || location.startsWith("file://")){
						URL url = new URL(location);
						specifica = HttpUtilities.requestHTTPFile(url.toString());
					}
					else{
						File f = new File(location);
						specifica = FileSystemUtilities.readBytesFromFile(f);
					}
				}catch(Exception e){
					throw new DriverRegistroServiziException("L'API contiene una specifica corrotta: "+e.getMessage(),e);
				}
			}
		}
		if(specifica==null) {
			throw new DriverRegistroServiziException("L'API non contiene una specifica");
		}
		
		
		
		ApiSchema [] s = null;
		ApiReaderConfig config = new ApiReaderConfig();
		if(buildSchemi) {
			config.setProcessInclude(true);
			config.setProcessInlineSchema(true);
			s = this.buildSchemas(as, fromBytes);
		}
		else {
			config.setProcessInclude(false);
			config.setProcessInlineSchema(false);
		}
		try {
			if(s!=null) {
				apiReader.init(this.logger, specifica, config, s);
			}
			else {
				apiReader.init(this.logger, specifica, config);
			}
			Api api = apiReader.read();
			this.accordoServizioWrapper.setApi(api);
		}catch(Exception e) {
			throw new DriverRegistroServiziException("Inizializzazione Specifica ["+this.accordoServizioWrapper.getAccordoServizio().getFormatoSpecifica()+"] fallita: "+e.getMessage(),e);
		}
		
		
	}
	
	
	public void buildApiFromRegistry(boolean fromBytes,boolean buildSchemi) throws DriverRegistroServiziException {
			
		if(this.accordoServizioWrapper==null) {
			throw new DriverRegistroServiziException("AccordoWrapper non fornito");
		}
		if(this.accordoServizioWrapper.getAccordoServizio()==null) {
			throw new DriverRegistroServiziException("API non fornita");
		}
		AccordoServizioParteComune as = this.accordoServizioWrapper.getAccordoServizio();
		
		try {
			RegistryAPI api = new RegistryAPI(as, "http://TODO");
			
			ApiSchema [] s = null;
			if(buildSchemi) {
				s = this.buildSchemas(as, fromBytes);
				if(s!=null && s.length>0) {
					for (int i = 0; i < s.length; i++) {
						api.addSchema(s[i]);
					}
				}
			}
			
			this.accordoServizioWrapper.setApi(api);
		}catch(Exception e) {
			throw new DriverRegistroServiziException("Inizializzazione Specifica ["+this.accordoServizioWrapper.getAccordoServizio().getFormatoSpecifica()+"] fallita: "+e.getMessage(),e);
		}
	}
	
	
	private ApiSchema [] buildSchemas(AccordoServizioParteComune as, boolean fromBytes) throws DriverRegistroServiziException{
		Hashtable<String, byte[]> resourcesXSD = new Hashtable<String, byte[]>();
		
		// --------- ALLEGATI --------- 
		if(as.sizeAllegatoList()>0){
			for(int i=0; i<as.sizeAllegatoList();i++){

				Documento allegato = as.getAllegato(i);
				byte[] resource = null;
				String systemId = null;
				//String location = null;
				if(fromBytes){
					//location = allegato.getFile();
					systemId = allegato.getFile();
					resource = allegato.getByteContenuto();
					if(resource==null){
						throw new DriverRegistroServiziException("Allegato ["+systemId+"] non contiene i bytes che ne definiscono il contenuto");
					}
				}
				else{
					String location = allegato.getFile();
					try{
						if(location.startsWith("http://") || location.startsWith("file://")){
							URL url = new URL(location);
							resource = HttpUtilities.requestHTTPFile(url.toString());
							systemId = (new File(url.getFile())).getName();
						}
						else{
							File f = new File(location);
							resource = FileSystemUtilities.readBytesFromFile(f);
							systemId = f.getName();
						}	
					}catch(Exception e){
						throw new DriverRegistroServiziException("Allegato ["+location+"] indirizza un documento non esistente");
					}
				}

				try{
					if(this.xsdUtils.isXSDSchema(resource)){

						if(resourcesXSD.containsKey(systemId)){
							throw new Exception("Esiste più di un documento xsd, registrato tra allegati e specifiche semiformali, con nome ["+systemId+"] (La validazione xsd di OpenSPCoop richiede l'utilizzo di nomi diversi)");
						}
						resourcesXSD.put(systemId,resource); 

					}
				}catch(Exception e){
					throw new DriverRegistroServiziException("La lettura dell'allegato ["+systemId+"] ha causato un errore: "+e.getMessage(),e);
				}

			}
		}


		// --------- SPECIFICHE SEMIFORMALI --------- 
		if(as.sizeSpecificaSemiformaleList()>0){
			for(int i=0; i<as.sizeSpecificaSemiformaleList();i++){

				Documento specificaSemiformale = as.getSpecificaSemiformale(i);
				byte[] resource = null;
				String systemId = null;
				//String location = null;
				if(fromBytes){
					//location = specificaSemiformale.getFile();
					systemId = specificaSemiformale.getFile();
					resource = specificaSemiformale.getByteContenuto();
					if(resource==null){
						throw new DriverRegistroServiziException("Specifica Semiformale ["+systemId+"] non contiene i bytes che ne definiscono il contenuto");
					}
				}
				else{
					String location = specificaSemiformale.getFile();
					try{
						if(location.startsWith("http://") || location.startsWith("file://")){
							URL url = new URL(location);
							resource = HttpUtilities.requestHTTPFile(url.toString());
							systemId = (new File(url.getFile())).getName();
						}
						else{
							File f = new File(location);
							resource = FileSystemUtilities.readBytesFromFile(f);
							systemId = f.getName();
						}	
					}catch(Exception e){
						throw new DriverRegistroServiziException("Specifica Semiformale ["+location+"] indirizza un documento non esistente");
					}
				}

				try{
					if(this.xsdUtils.isXSDSchema(resource)){

						if(resourcesXSD.containsKey(systemId)){
							throw new Exception("Esiste più di un documento xsd, registrato tra allegati e specifiche semiformali, con nome ["+systemId+"] (La validazione xsd di OpenSPCoop richiede l'utilizzo di nomi diversi)");
						}
						resourcesXSD.put(systemId,resource); 

					}
				}catch(Exception e){
					throw new DriverRegistroServiziException("La lettura della specifica semiformale ["+systemId+"] ha causato un errore: "+e.getMessage(),e);
				}

			}
		}

		List<ApiSchema> schemas = new ArrayList<ApiSchema>();
		if(resourcesXSD!=null && resourcesXSD.size()>0) {
			Enumeration<String> enKeys = resourcesXSD.keys();
			while (enKeys.hasMoreElements()) {
				String key = (String) enKeys.nextElement();
				byte[] value = resourcesXSD.get(key);
				schemas.add(new ApiSchema(key, value, ApiSchemaType.XSD));
			}
		}
		ApiSchema [] s = null;
		if(schemas.size()>0) {
			s = schemas.toArray(new ApiSchema[1]);
		}
		
		return s;
	}
}
