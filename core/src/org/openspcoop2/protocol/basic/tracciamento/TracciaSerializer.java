/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
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



package org.openspcoop2.protocol.basic.tracciamento;

import java.io.ByteArrayOutputStream;

import org.openspcoop2.core.tracciamento.Eccezione;
import org.openspcoop2.core.tracciamento.Riscontro;
import org.openspcoop2.core.tracciamento.Trasmissione;
import org.openspcoop2.core.tracciamento.constants.TipoCodificaEccezione;
import org.openspcoop2.core.tracciamento.constants.TipoInoltro;
import org.openspcoop2.core.tracciamento.constants.TipoProfiloCollaborazione;
import org.openspcoop2.core.tracciamento.constants.TipoRilevanzaEccezione;
import org.openspcoop2.core.tracciamento.constants.TipoTempo;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.config.ITraduttore;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.constants.SubCodiceErrore;
import org.openspcoop2.protocol.sdk.constants.TipoOraRegistrazione;
import org.openspcoop2.protocol.sdk.constants.TipoSerializzazione;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.slf4j.Logger;
import org.w3c.dom.Element;

/**
 * XMLTracciaBuilder
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author: apoli $
 * @version $Rev: 12359 $, $Date: 2016-11-18 17:24:57 +0100 (Fri, 18 Nov 2016) $
 */
public class TracciaSerializer implements org.openspcoop2.protocol.sdk.tracciamento.ITracciaSerializer {
	
	protected Logger log;
	protected IProtocolFactory<?> factory;
	protected OpenSPCoop2MessageFactory fac = null;
	protected AbstractXMLUtils xmlUtils;
	
	@Override
	public IProtocolFactory<?> getProtocolFactory() {
		return this.factory;
	}
	
	public TracciaSerializer(IProtocolFactory<?> factory) {
		this.log = factory.getLogger();
		this.factory = factory;
		this.fac = OpenSPCoop2MessageFactory.getMessageFactory();
		this.xmlUtils = org.openspcoop2.message.xml.XMLUtils.getInstance();
	}


	
	private org.openspcoop2.core.tracciamento.Traccia toTraccia(Traccia tracciaObject)
			throws ProtocolException {
		String tmpId = null;
		try{
						
			if(tracciaObject.sizeProperties()>0){
				tmpId = tracciaObject.removeProperty(TracciaDriver.IDTRACCIA); // non deve essere serializzato
			}		
			org.openspcoop2.core.tracciamento.Traccia tracciaBase = tracciaObject.getTraccia();
			
			// xml
			if(tracciaBase.getBustaXml()==null){
				if(tracciaObject.getBustaAsByteArray()!=null)
					tracciaBase.setBustaXml(new String(tracciaObject.getBustaAsByteArray()));
				else if(tracciaObject.getBustaAsRawContent()!=null){			
					try{
						tracciaBase.setBustaXml(tracciaObject.getBustaAsRawContent().toString(TipoSerializzazione.XML));
					}catch(Exception e){
						try{
							tracciaBase.setBustaXml(tracciaObject.getBustaAsRawContent().toString(TipoSerializzazione.JSON));
						}catch(Exception e2){
							throw new Exception("Serializzazione RawContent non riuscito. \nXmlError: "+e.getMessage()+"\njsonErro: "+e2.getMessage(),e);
						}
					}
				}
			}
			
			// Traduzioni da factory
			ITraduttore protocolTraduttore = this.factory.createTraduttore();
			if(tracciaBase!=null){
				if(tracciaBase.getBusta()!=null){
					if(tracciaBase.getBusta().getProfiloCollaborazione()!=null){
						if(tracciaBase.getBusta().getProfiloCollaborazione().getBase()==null && 
								tracciaBase.getBusta().getProfiloCollaborazione().getTipo()!=null){
							tracciaBase.getBusta().getProfiloCollaborazione().setBase(this.getBaseValueProfiloCollaborazione(protocolTraduttore,tracciaBase.getBusta().getProfiloCollaborazione().getTipo()));
						}
					}
					if(tracciaBase.getBusta().getProfiloTrasmissione()!=null){
						if(tracciaBase.getBusta().getProfiloTrasmissione().getInoltro()!=null){
							if(tracciaBase.getBusta().getProfiloTrasmissione().getInoltro().getBase()==null && 
									tracciaBase.getBusta().getProfiloTrasmissione().getInoltro().getTipo()!=null){
								tracciaBase.getBusta().getProfiloTrasmissione().getInoltro().setBase(this.getBaseValueInoltro(protocolTraduttore,tracciaBase.getBusta().getProfiloTrasmissione().getInoltro().getTipo()));
							}
						}
					}
					if(tracciaBase.getBusta().getOraRegistrazione()!=null){
						if(tracciaBase.getBusta().getOraRegistrazione().getSorgente()!=null){
							if(tracciaBase.getBusta().getOraRegistrazione().getSorgente().getBase()==null && 
									tracciaBase.getBusta().getOraRegistrazione().getSorgente().getTipo()!=null){
								tracciaBase.getBusta().getOraRegistrazione().getSorgente().setBase(this.getBaseValueTipoTempo(protocolTraduttore,tracciaBase.getBusta().getOraRegistrazione().getSorgente().getTipo()));
							}
						}
					}
					if(tracciaBase.getBusta().getTrasmissioni()!=null && tracciaBase.getBusta().getTrasmissioni().sizeTrasmissioneList()>0){
						for (Trasmissione trasmissione : tracciaBase.getBusta().getTrasmissioni().getTrasmissioneList()) {
							if(trasmissione.getOraRegistrazione()!=null){
								if(trasmissione.getOraRegistrazione().getSorgente()!=null){
									if(trasmissione.getOraRegistrazione().getSorgente().getBase()==null &&
											trasmissione.getOraRegistrazione().getSorgente().getTipo()!=null){
										trasmissione.getOraRegistrazione().getSorgente().setBase(this.getBaseValueTipoTempo(protocolTraduttore,trasmissione.getOraRegistrazione().getSorgente().getTipo()));
									}
								}
							}
						}
					}
					if(tracciaBase.getBusta().getRiscontri()!=null && tracciaBase.getBusta().getRiscontri().sizeRiscontroList()>0){
						for (Riscontro riscontro : tracciaBase.getBusta().getRiscontri().getRiscontroList()) {
							if(riscontro.getOraRegistrazione()!=null){
								if(riscontro.getOraRegistrazione().getSorgente()!=null){
									if(riscontro.getOraRegistrazione().getSorgente().getBase()==null &&
											riscontro.getOraRegistrazione().getSorgente().getTipo()!=null){
										riscontro.getOraRegistrazione().getSorgente().setBase(this.getBaseValueTipoTempo(protocolTraduttore,riscontro.getOraRegistrazione().getSorgente().getTipo()));
									}
								}
							}
						}
					}
					if(tracciaBase.getBusta().getEccezioni()!=null && tracciaBase.getBusta().getEccezioni().sizeEccezioneList()>0){
						for (Eccezione eccezione : tracciaBase.getBusta().getEccezioni().getEccezioneList()) {
							if(eccezione.getCodice()!=null){
								if(eccezione.getCodice().getBase()==null && eccezione.getCodice().getTipo()!=null){
									eccezione.getCodice().setBase(this.getBaseValueCodiceEccezione(protocolTraduttore,eccezione.getCodice().getTipo(), eccezione.getCodice().getSottotipo()));
								}
							}
							if(eccezione.getContestoCodifica()!=null){
								if(eccezione.getContestoCodifica().getBase()==null && 
										eccezione.getContestoCodifica().getTipo()!=null){
									eccezione.getContestoCodifica().setBase(this.getBaseValueContestoCodifica(protocolTraduttore,eccezione.getContestoCodifica().getTipo()));
								}
							}
							if(eccezione.getRilevanza()!=null){
								if(eccezione.getRilevanza().getBase()==null &&
										eccezione.getRilevanza().getTipo()!=null){
									eccezione.getRilevanza().setBase(this.getBaseValueRilevanzaEccezione(protocolTraduttore,eccezione.getRilevanza().getTipo()));
								}
							}
						}
					}
				}
			}
							
			return tracciaBase;

		} catch(Exception e) {
			this.log.error("XMLBuilder.buildElement_Tracciamento error: "+e.getMessage(),e);
			throw new ProtocolException("XMLBuilder.buildElement_Tracciamento error: "+e.getMessage(),e);
		}
		finally{
			if(tmpId!=null && tracciaObject!=null){
				tracciaObject.addProperty(TracciaDriver.IDTRACCIA, tmpId);
			}
		}
	}

	@Override
	public Element toElement(Traccia tracciaObject)
			throws ProtocolException {
		byte[] traccia = this.toByteArray(tracciaObject,TipoSerializzazione.XML);
		try{
			return this.xmlUtils.newElement(traccia);
		} catch(Exception e) {
			this.log.error("TracciaSerializer.toElement error: "+e.getMessage(),e);
			throw new ProtocolException("TracciaSerializer.toElement error: "+e.getMessage(),e);
		}
	}

	@Override
	public String toString(Traccia traccia, TipoSerializzazione tipoSerializzazione) throws ProtocolException {
		return this.toByteArrayOutputStream(traccia, tipoSerializzazione).toString();
	}

	@Override
	public byte[] toByteArray(Traccia traccia, TipoSerializzazione tipoSerializzazione) throws ProtocolException {
		return this.toByteArrayOutputStream(traccia, tipoSerializzazione).toByteArray();
	}
	
	protected ByteArrayOutputStream toByteArrayOutputStream(Traccia traccia, TipoSerializzazione tipoSerializzazione) throws ProtocolException {
		
		try{
		
			org.openspcoop2.core.tracciamento.Traccia tracciaBase = this.toTraccia(traccia);
			
			switch (tipoSerializzazione) {
				case XML:
					
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					org.openspcoop2.core.tracciamento.utils.XMLUtils.generateTraccia(tracciaBase,bout);
					bout.flush();
					bout.close();
					return bout;
	
				case JSON:
					
					bout = new ByteArrayOutputStream();
					String s = org.openspcoop2.core.tracciamento.utils.XMLUtils.generateTracciaAsJson(tracciaBase);
					bout.write(s.getBytes());
					bout.flush();
					bout.close();
					return bout;
			}
			
			throw new Exception("Tipo ["+tipoSerializzazione+"] Non gestito");
			
		} catch(Exception e) {
			this.log.error("TracciaSerializer.toString error: "+e.getMessage(),e);
			throw new ProtocolException("TracciaSerializer.toString error: "+e.getMessage(),e);
		}
	}

	
	
	// UTILITIES 
	
	private String getBaseValueProfiloCollaborazione(ITraduttore protocolTraduttore,TipoProfiloCollaborazione tipoProfiloCollaborazione){
		switch (tipoProfiloCollaborazione) {
		case ONEWAY:
			return protocolTraduttore.toString(ProfiloDiCollaborazione.ONEWAY);
		case SINCRONO:
			return protocolTraduttore.toString(ProfiloDiCollaborazione.SINCRONO);
		case ASINCRONO_ASIMMETRICO:
			return protocolTraduttore.toString(ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO);
		case ASINCRONO_SIMMETRICO:
			return protocolTraduttore.toString(ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO);
		case SCONOSCIUTO:
			return protocolTraduttore.toString(ProfiloDiCollaborazione.UNKNOWN);
		}
		return null;
	}
	
	private String getBaseValueInoltro(ITraduttore protocolTraduttore,TipoInoltro tipoInoltro){
		switch (tipoInoltro) {
		case INOLTRO_CON_DUPLICATI:
			return protocolTraduttore.toString(Inoltro.CON_DUPLICATI);
		case INOLTRO_SENZA_DUPLICATI:
			return protocolTraduttore.toString(Inoltro.SENZA_DUPLICATI);
		case SCONOSCIUTO:
			return protocolTraduttore.toString(Inoltro.UNKNOWN);
		}
		return null;
	}
	
	private String getBaseValueTipoTempo(ITraduttore protocolTraduttore,TipoTempo tipoTempo){
		switch (tipoTempo) {
		case LOCALE:
			return protocolTraduttore.toString(TipoOraRegistrazione.LOCALE);
		case SCONOSCIUTO:
			return protocolTraduttore.toString(TipoOraRegistrazione.UNKNOWN);
		case SINCRONIZZATO:
			return protocolTraduttore.toString(TipoOraRegistrazione.SINCRONIZZATO);
		}
		return null;
	}
	
	private String getBaseValueCodiceEccezione(ITraduttore protocolTraduttore,Integer codice, Integer subCodice){
		CodiceErroreCooperazione errore = CodiceErroreCooperazione.toCodiceErroreCooperazione(codice);
		if(subCodice==null){
			return protocolTraduttore.toString(errore);
		}
		else{
			SubCodiceErrore sub = new SubCodiceErrore();
			sub.setSubCodice(subCodice);
			return protocolTraduttore.toString(errore,sub);
		}
	}
	
	private String getBaseValueContestoCodifica(ITraduttore protocolTraduttore,TipoCodificaEccezione codifica){
		switch (codifica) {
		case ECCEZIONE_PROCESSAMENTO:
			return protocolTraduttore.toString(org.openspcoop2.protocol.sdk.constants.ContestoCodificaEccezione.PROCESSAMENTO);
		case ECCEZIONE_VALIDAZIONE_PROTOCOLLO:
			return protocolTraduttore.toString(org.openspcoop2.protocol.sdk.constants.ContestoCodificaEccezione.INTESTAZIONE);
		case SCONOSCIUTO:
			return null;
		}
		return null;
	}
	
	private String getBaseValueRilevanzaEccezione(ITraduttore protocolTraduttore,TipoRilevanzaEccezione codifica){
		switch (codifica) {
		case DEBUG:
			return protocolTraduttore.toString(org.openspcoop2.protocol.sdk.constants.LivelloRilevanza.DEBUG);
		case ERROR:
			return protocolTraduttore.toString(org.openspcoop2.protocol.sdk.constants.LivelloRilevanza.ERROR);
		case FATAL:
			return protocolTraduttore.toString(org.openspcoop2.protocol.sdk.constants.LivelloRilevanza.FATAL);
		case INFO:
			return protocolTraduttore.toString(org.openspcoop2.protocol.sdk.constants.LivelloRilevanza.INFO);
		case WARN:
			return protocolTraduttore.toString(org.openspcoop2.protocol.sdk.constants.LivelloRilevanza.WARN);
		case SCONOSCIUTO:
			return protocolTraduttore.toString(org.openspcoop2.protocol.sdk.constants.LivelloRilevanza.UNKNOWN);
		}
		return null;
	}
}