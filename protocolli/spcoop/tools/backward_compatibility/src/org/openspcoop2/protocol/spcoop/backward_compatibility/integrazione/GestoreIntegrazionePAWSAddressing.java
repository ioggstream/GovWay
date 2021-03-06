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

package org.openspcoop2.protocol.spcoop.backward_compatibility.integrazione;

import javax.xml.soap.SOAPHeaderElement;

import org.slf4j.Logger;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.integrazione.HeaderIntegrazione;
import org.openspcoop2.pdd.core.integrazione.HeaderIntegrazioneBusta;
import org.openspcoop2.pdd.core.integrazione.HeaderIntegrazioneException;
import org.openspcoop2.pdd.core.integrazione.IGestoreIntegrazionePASoap;
import org.openspcoop2.pdd.core.integrazione.InRequestPAMessage;
import org.openspcoop2.pdd.core.integrazione.InResponsePAMessage;
import org.openspcoop2.pdd.core.integrazione.OutRequestPAMessage;
import org.openspcoop2.pdd.core.integrazione.OutResponsePAMessage;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.spcoop.backward_compatibility.config.BackwardCompatibilityProperties;
import org.openspcoop2.protocol.spcoop.backward_compatibility.config.Costanti;
import org.openspcoop2.protocol.spcoop.backward_compatibility.services.BackwardCompatibilityStartup;
import org.openspcoop2.utils.LoggerWrapperFactory;


/**
 * Classe utilizzata per la spedizione di informazioni di integrazione 
 * dalla porta di dominio verso i servizi applicativi.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreIntegrazionePAWSAddressing extends AbstractCore implements IGestoreIntegrazionePASoap{

	/** Utility per l'integrazione */
	private UtilitiesIntegrazioneWSAddressing utilities = null;
	
	/** BackwardCompatibilityProperties */
	private BackwardCompatibilityProperties backwardCompatibilityProperties = null;
	
	/** Logger utilizzato per debug. */
	private Logger log = null;

	/** GestoreIntegrazionePA Originale */
	private org.openspcoop2.pdd.core.integrazione.GestoreIntegrazionePAWSAddressing gestoreIntegrazioneOpenSPCoopV2 = null;
	
	
	public GestoreIntegrazionePAWSAddressing(){
		this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		if(this.log==null){
			this.log = LoggerWrapperFactory.getLogger(GestoreIntegrazionePAWSAddressing.class);
		}
		try{
			this.backwardCompatibilityProperties = BackwardCompatibilityProperties.getInstance(true);
			this.utilities = UtilitiesIntegrazioneWSAddressing.getInstance(this.log);
		}catch(Exception e){
			this.log.error("Errore durante l'inizializzazione delle UtilitiesIntegrazioneWSAddressing: "+e.getMessage(),e);
		}
		try{
			this.gestoreIntegrazioneOpenSPCoopV2 = new org.openspcoop2.pdd.core.integrazione.GestoreIntegrazionePAWSAddressing();
		}catch(Exception e){
			this.log.error("Errore durante l'instanziazione del gestoreIntegrazioneOpenSPCoopV2: "+e.getMessage(),e);
		}
	}
	@Override
	public void init(PdDContext pddContext, IProtocolFactory<?> protocolFactory,
			Object... args) {
		super.init(pddContext, protocolFactory, args);
		try{
			if(this.gestoreIntegrazioneOpenSPCoopV2!=null){
				this.gestoreIntegrazioneOpenSPCoopV2.init(this.getPddContext(), this.getProtocolFactory(), this.getArgs());
			}
		}catch(Exception e){
			this.log.error("Errore durante l'instanziazione del gestoreIntegrazioneOpenSPCoopV2: "+e.getMessage(),e);
		}
	}
	
	
	// IN - Request
	
	@Override
	public void readInRequestHeader(HeaderIntegrazione integrazione,
			InRequestPAMessage inRequestPAMessage) throws HeaderIntegrazioneException{
		try{
			if(
					BackwardCompatibilityStartup.initialized &&
					(
						(!this.backwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaApplicativa())
						||
						(this.backwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaApplicativa() && this.getPddContext().containsKey(Costanti.OPENSPCOOP2_BACKWARD_COMPATIBILITY))
					)
				){
				this.utilities.readHeader(inRequestPAMessage.getMessage().castAsSoap(), integrazione, UtilitiesIntegrazioneWSAddressing.INTERPRETA_COME_ID_BUSTA, this.backwardCompatibilityProperties.getHeaderSoapActorIntegrazione());
			}
			else{
				this.gestoreIntegrazioneOpenSPCoopV2.readInRequestHeader(integrazione, inRequestPAMessage);
			}
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePASoap, "+e.getMessage(),e);
		}
	}
	
	@Override
	public void deleteInRequestHeader(InRequestPAMessage inRequestPAMessage) throws HeaderIntegrazioneException{
		try{
			if(
					BackwardCompatibilityStartup.initialized &&
					(
						(!this.backwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaApplicativa())
						||
						(this.backwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaApplicativa() && this.getPddContext().containsKey(Costanti.OPENSPCOOP2_BACKWARD_COMPATIBILITY))
					)
				){
				this.utilities.deleteHeader(inRequestPAMessage.getMessage().castAsSoap(), this.backwardCompatibilityProperties.getHeaderSoapActorIntegrazione());
			}
			else{
				this.gestoreIntegrazioneOpenSPCoopV2.deleteInRequestHeader(inRequestPAMessage);
			}
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePASoap, "+e.getMessage(),e);
		}
	}
	
	@Override
	public void updateInRequestHeader(InRequestPAMessage inRequestPAMessage,
			String idMessaggio,String servizioApplicativo,String correlazioneApplicativa) throws HeaderIntegrazioneException{
		try{
			if(
					BackwardCompatibilityStartup.initialized &&
					(
						(!this.backwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaApplicativa())
						||
						(this.backwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaApplicativa() && this.getPddContext().containsKey(Costanti.OPENSPCOOP2_BACKWARD_COMPATIBILITY))
					)
				){
				this.utilities.updateHeader(inRequestPAMessage.getMessage().castAsSoap(), 
						inRequestPAMessage.getSoggettoMittente(),
						inRequestPAMessage.getServizio(),
						idMessaggio, servizioApplicativo, correlazioneApplicativa, 
						org.openspcoop2.pdd.core.integrazione.UtilitiesIntegrazione.getIdTransazione(this.getPddContext()),
						this.backwardCompatibilityProperties.getHeaderSoapActorIntegrazione());
			}
			else{
				this.gestoreIntegrazioneOpenSPCoopV2.updateInRequestHeader(inRequestPAMessage, idMessaggio, servizioApplicativo, correlazioneApplicativa);
			}
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePASoap, "+e.getMessage(),e);
		}
	}
	
	
	// OUT - Request
	
	@Override
	public void setOutRequestHeader(HeaderIntegrazione integrazione,
			OutRequestPAMessage outRequestPAMessage) throws HeaderIntegrazioneException{
		try{
			
			if(
					BackwardCompatibilityStartup.initialized &&
					(
						(!this.backwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaApplicativa())
						||
						(this.backwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaApplicativa() && this.getPddContext().containsKey(Costanti.OPENSPCOOP2_BACKWARD_COMPATIBILITY))
					)
				){
			
				OpenSPCoop2Message msgParam = outRequestPAMessage.getMessage();
				OpenSPCoop2SoapMessage msg = null;
				if(msgParam!=null){
					msg = msgParam.castAsSoap();
				}
				
				if(msg.getSOAPHeader() == null){
					msg.getSOAPPart().getEnvelope().addHeader();
				}
				
				if(integrazione.getBusta()!=null){
					
					HeaderIntegrazioneBusta hBusta = integrazione.getBusta();
						
					if(hBusta.getDestinatario()!=null && hBusta.getServizio()!=null){
						
						// To
						SOAPHeaderElement wsaTO = UtilitiesIntegrazioneWSAddressing.buildWSATo(msg,this.backwardCompatibilityProperties.getHeaderSoapActorIntegrazione(),hBusta.getTipoDestinatario(),hBusta.getDestinatario(), hBusta.getTipoServizio(), hBusta.getServizio());
						msg.addHeaderElement(msg.getSOAPHeader(), wsaTO);
						
						// Action
						if(hBusta.getAzione()!=null){
							SOAPHeaderElement wsaAction = UtilitiesIntegrazioneWSAddressing.buildWSAAction(msg,this.backwardCompatibilityProperties.getHeaderSoapActorIntegrazione(),hBusta.getTipoDestinatario(),hBusta.getDestinatario(), hBusta.getTipoServizio(), hBusta.getServizio(),hBusta.getAzione());
							msg.addHeaderElement(msg.getSOAPHeader(), wsaAction);
						}
					}
					
					if(hBusta.getMittente()!=null){
						SOAPHeaderElement wsaFROM = UtilitiesIntegrazioneWSAddressing.buildWSAFrom(msg,this.backwardCompatibilityProperties.getHeaderSoapActorIntegrazione(),integrazione.getServizioApplicativo(),hBusta.getTipoMittente(),hBusta.getMittente());
						msg.addHeaderElement(msg.getSOAPHeader(), wsaFROM);
					}
						
					if(hBusta.getID()!=null){
						SOAPHeaderElement wsaID = UtilitiesIntegrazioneWSAddressing.buildWSAID(msg,this.backwardCompatibilityProperties.getHeaderSoapActorIntegrazione(),hBusta.getID());
						msg.addHeaderElement(msg.getSOAPHeader(), wsaID);
					}
					
					if(hBusta.getRiferimentoMessaggio()!=null || hBusta.getIdCollaborazione()!=null){
						String rif = hBusta.getRiferimentoMessaggio();
						if(rif==null){
							rif = hBusta.getIdCollaborazione();
						}
						SOAPHeaderElement wsaRelatesTo = UtilitiesIntegrazioneWSAddressing.buildWSARelatesTo(msg,this.backwardCompatibilityProperties.getHeaderSoapActorIntegrazione(),rif);
						msg.addHeaderElement(msg.getSOAPHeader(), wsaRelatesTo);
					}
				}
				
			}
			else{
				this.gestoreIntegrazioneOpenSPCoopV2.setOutRequestHeader(integrazione, outRequestPAMessage);
			}
			
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePASoap, "+e.getMessage(),e);
		}
	}

	
	// IN - Response
	
	@Override
	public void readInResponseHeader(HeaderIntegrazione integrazione,
			InResponsePAMessage inResponsePAMessage) throws HeaderIntegrazioneException{
		try{
			if(
					BackwardCompatibilityStartup.initialized &&
					(
						(!this.backwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaApplicativa())
						||
						(this.backwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaApplicativa() && this.getPddContext().containsKey(Costanti.OPENSPCOOP2_BACKWARD_COMPATIBILITY))
					)
				){
				this.utilities.readHeader(inResponsePAMessage.getMessage().castAsSoap(), integrazione, UtilitiesIntegrazioneWSAddressing.INTERPRETA_COME_ID_APPLICATIVO, this.backwardCompatibilityProperties.getHeaderSoapActorIntegrazione());
			}
			else{
				this.gestoreIntegrazioneOpenSPCoopV2.readInResponseHeader(integrazione, inResponsePAMessage);
			}
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePASoap, "+e.getMessage(),e);
		}
	}
	
	@Override
	public void deleteInResponseHeader(InResponsePAMessage inResponsePAMessage) throws HeaderIntegrazioneException{
		try{
			if(
					BackwardCompatibilityStartup.initialized &&
					(
						(!this.backwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaApplicativa())
						||
						(this.backwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaApplicativa() && this.getPddContext().containsKey(Costanti.OPENSPCOOP2_BACKWARD_COMPATIBILITY))
					)
				){
				this.utilities.deleteHeader(inResponsePAMessage.getMessage().castAsSoap(), this.backwardCompatibilityProperties.getHeaderSoapActorIntegrazione());
			}
			else{
				this.gestoreIntegrazioneOpenSPCoopV2.deleteInResponseHeader(inResponsePAMessage);
			}
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePASoap, "+e.getMessage(),e);
		}
	}
	
	@Override
	public void updateInResponseHeader(InResponsePAMessage inResponsePAMessage,
			String idMessageRequest,String idMessageResponse,String servizioApplicativo,String correlazioneApplicativa,String riferimentoCorrelazioneApplicativaRichiesta) throws HeaderIntegrazioneException{
		try{
			if(
					BackwardCompatibilityStartup.initialized &&
					(
						(!this.backwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaApplicativa())
						||
						(this.backwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaApplicativa() && this.getPddContext().containsKey(Costanti.OPENSPCOOP2_BACKWARD_COMPATIBILITY))
					)
				){
				this.utilities.updateHeader(inResponsePAMessage.getMessage().castAsSoap(), 
						inResponsePAMessage.getSoggettoMittente(),
						inResponsePAMessage.getServizio(),
						idMessageRequest, idMessageResponse, servizioApplicativo, correlazioneApplicativa, this.backwardCompatibilityProperties.getHeaderSoapActorIntegrazione());
			}
			else{
				this.gestoreIntegrazioneOpenSPCoopV2.updateInResponseHeader(inResponsePAMessage, idMessageRequest, idMessageResponse, servizioApplicativo, correlazioneApplicativa, riferimentoCorrelazioneApplicativaRichiesta);
			}
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePASoap, "+e.getMessage(),e);
		}
	}
	
	// OUT - Response
	
	@Override
	public void setOutResponseHeader(HeaderIntegrazione integrazione,
			OutResponsePAMessage outResponsePAMessage) throws HeaderIntegrazioneException{
		
		// nop;
		
	}
		

}