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
package org.openspcoop2.pdd.core.controllo_traffico.policy;

import java.util.Enumeration;
import java.util.Properties;

import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.controllo_traffico.beans.DatiTransazione;
import org.openspcoop2.core.controllo_traffico.constants.TipoFiltroApplicativo;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.config.ClassNameProperties;
import org.openspcoop2.pdd.core.controllo_traffico.plugins.Dati;
import org.openspcoop2.pdd.core.controllo_traffico.plugins.IRateLimiting;
import org.openspcoop2.pdd.core.handlers.InRequestProtocolContext;
import org.openspcoop2.utils.json.JsonPathExpressionEngine;
import org.openspcoop2.utils.regexp.RegExpNotFoundException;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.utils.xml.AbstractXPathExpressionEngine;
import org.slf4j.Logger;
import org.w3c.dom.Element;

/**     
 * PolicyFiltroApplicativoUtilities
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PolicyFiltroApplicativoUtilities {

	public static String getValore(Logger log,String tipo, String nome, InRequestProtocolContext context, 
			DatiTransazione datiTransazione, boolean forFilter) throws Exception{
		
		
		TipoFiltroApplicativo tipoFiltro = TipoFiltroApplicativo.toEnumConstant(tipo);
		
		switch (tipoFiltro) {
		case CONTENT_BASED:
			AbstractXPathExpressionEngine xPathEngine = null;
			Element element = null;
			String elementJson = null;
			if(ServiceBinding.SOAP.equals(context.getMessaggio().getServiceBinding())){
				element = context.getMessaggio().castAsSoap().getSOAPPart().getEnvelope();
			}
			else{
				if(MessageType.XML.equals(context.getMessaggio().getMessageType())){
					element = context.getMessaggio().castAsRestXml().getContent();
				}
				else if(MessageType.JSON.equals(context.getMessaggio().getMessageType())){
					elementJson = context.getMessaggio().castAsRestJson().getContent();
				}
				else{
					throw new DriverConfigurazioneNotFound("Filtro '"+tipoFiltro.getValue()+"' non supportato per il message-type '"+context.getMessaggio().getMessageType()+"'");
				}
			}
			if(element!=null) {
				xPathEngine = new org.openspcoop2.message.xml.XPathExpressionEngine();
				return AbstractXPathExpressionEngine.extractAndConvertResultAsString(element, xPathEngine, nome,  log);
			}
			else {
				return JsonPathExpressionEngine.extractAndConvertResultAsString(elementJson, nome, log);
			}
			
		case URLBASED:
			
			String urlInvocazionePD = context.getConnettore().getUrlProtocolContext().getUrlInvocazione_formBased();
			try{
				return RegularExpressionEngine.getStringMatchPattern(urlInvocazionePD, nome);
			}catch(RegExpNotFoundException notFound){
				return null;
			}

		case FORM_BASED:
			
			Properties pForm = context.getConnettore().getUrlProtocolContext().getParametersFormBased();
			if(pForm.containsKey(nome)){
				return pForm.getProperty(nome);
			}
			else{
				return null;
			}
			
		case HEADER_BASED:
			
			Properties pTrasporto = context.getConnettore().getUrlProtocolContext().getParametersTrasporto();
		
			Enumeration<?> en = pTrasporto.keys();
			while (en.hasMoreElements()) {
				Object object = (Object) en.nextElement();
				if(object!=null && object instanceof String){
					String key = (String) object;
					if(key.equals(nome)){
						return pTrasporto.getProperty(key);
					}
					else if(key.equals(nome.toLowerCase())){
						return pTrasporto.getProperty(key);
					}
					else if(key.equals(nome.toUpperCase())){
						return pTrasporto.getProperty(key);
					}
				}
			}
			
			return null;
						
		case SOAPACTION_BASED:
			
			return context.getConnettore().getSoapAction();
			
		case PLUGIN_BASED:
			
			String className = ClassNameProperties.getInstance().getRateLimiting(nome);
			if(className==null){
				throw new Exception("Instance plugin ["+nome+"] error: il tipo non è stato registrato nel registro delle classi ('org.openspcoop2.pdd.controlloTraffico.rateLimiting."+
						nome+"' non esiste)");
			}
			try{
				Class<?> classPlugin = Class.forName(className);
				Object o = classPlugin.newInstance();
				if(o instanceof IRateLimiting){
					
					Dati datiRichiesta = new Dati();
					datiRichiesta.setConnettore(context.getConnettore());
					datiRichiesta.setDatiTransazione(datiTransazione);
					datiRichiesta.setMessaggio(context.getMessaggio());
					datiRichiesta.setPddContext(context.getPddContext());
					
					if(forFilter){
						return ((IRateLimiting)o).estraiValoreFiltro(log,datiRichiesta);
					}
					else{
						return ((IRateLimiting)o).estraiValoreCollezionamentoDati(log,datiRichiesta);
					}
				}else{
					throw new Exception("ClassType ["+o.getClass().getName()+"] unknown");
				}
			}catch(Exception e){
				throw new Exception("Instance plugin ["+nome+"] [class:"+className+"] error: "+e.getMessage(),e);
			}

		}
		
		throw new Exception("TipoFiltro ["+tipo+"] non gestito");
	}
	
}
