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



package org.openspcoop2.pdd.core.autorizzazione.pa;

import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziServizioNotFound;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.core.credenziali.Credenziali;
import org.openspcoop2.protocol.registry.EsitoAutorizzazioneRegistro;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroriCooperazione;

/**
 * Interfaccia che definisce un processo di autorizzazione per i soggetti.
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class AutorizzazioneAuthenticated extends AbstractAutorizzazioneBase {

    @Override
	public EsitoAutorizzazionePortaApplicativa process(DatiInvocazionePortaApplicativa datiInvocazione){
    	
    	EsitoAutorizzazionePortaApplicativa esito = new EsitoAutorizzazionePortaApplicativa();
    	
    	try{
    		RegistroServiziManager reg = RegistroServiziManager.getInstance(datiInvocazione.getState());
    		ConfigurazionePdDManager config = ConfigurazionePdDManager.getInstance(datiInvocazione.getState());
    		
    		Credenziali credenzialiPdDMittente = datiInvocazione.getCredenzialiPdDMittente();
    		String pdd = null;
    		if(credenzialiPdDMittente!=null){
    			if(credenzialiPdDMittente.getSubject()!=null){
    				pdd = credenzialiPdDMittente.getSubject();
    			}
    		}
    		
    		String identitaServizioApplicativoFruitore = null;
    		if(datiInvocazione.getIdentitaServizioApplicativoFruitore()!=null){
    			identitaServizioApplicativoFruitore = datiInvocazione.getIdentitaServizioApplicativoFruitore().getNome();
    		}
    		
    		IDSoggetto idSoggetto = datiInvocazione.getIdSoggettoFruitore();
    		IDServizio idServizio = datiInvocazione.getIdServizio();
    		
    		boolean autorizzazioneSoggettiMittenti = config.autorizzazione(datiInvocazione.getPa(), idSoggetto);
    		boolean autorizzazioneApplicativiMittenti = false;
    		if(this.getProtocolFactory().createProtocolConfiguration().isSupportoAutenticazioneApplicativiErogazioni()) {
    			autorizzazioneApplicativiMittenti = config.autorizzazione(datiInvocazione.getPa(), datiInvocazione.getIdentitaServizioApplicativoFruitore());
    		}
    		
    		if(!autorizzazioneSoggettiMittenti && !autorizzazioneApplicativiMittenti) {
    			String errore = this.getErrorString(idSoggetto, idServizio);
    			esito.setErroreCooperazione(ErroriCooperazione.AUTORIZZAZIONE_FALLITA.getErroreAutorizzazione(errore, CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA));
    			esito.setAutorizzato(false);
    		}
    		else {
    		
    			if(this.getProtocolFactory().createProtocolConfiguration().isSupportoAutenticazioneSoggetti()) {
    				
    				esito.setAutorizzato(true);
    			
    			}
    			else {
    				
    				// VERIFICARE ANTI-SPOOFING (Tramite PDD)
    				
		    		EsitoAutorizzazioneRegistro esitoAutorizzazione = reg.isFruitoreServizioAutorizzato(pdd, identitaServizioApplicativoFruitore, idSoggetto, idServizio);
		    		if(esitoAutorizzazione.isServizioAutorizzato()==false){
		    			String errore = this.getErrorString(idSoggetto, idServizio);
		    			if(esitoAutorizzazione.getDetails()!=null){
		    				errore = errore + " ("+esitoAutorizzazione.getDetails()+")";
		    			}
		    			esito.setErroreCooperazione(ErroriCooperazione.AUTORIZZAZIONE_FALLITA.getErroreAutorizzazione(errore, CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA));
		    			esito.setAutorizzato(false);
		    		}else{
		    			esito.setAutorizzato(true);
		    			if(esitoAutorizzazione.getDetails()!=null){
		    				esito.setDetails(esitoAutorizzazione.getDetails());
		    			}
		    		}
    			}
	    		
    		}
    	}catch(DriverRegistroServiziServizioNotFound e){
    		esito.setErroreCooperazione(ErroriCooperazione.SERVIZIO_SCONOSCIUTO.
    				getErroreCooperazione("Errore durante il processo di autorizzazione (ServizioNotFound): "+e.getMessage()));
    		esito.setAutorizzato(false);
    		esito.setEccezioneProcessamento(e);
    	}catch(Exception e){
    		String errore = "Errore durante il processo di autorizzazione: "+e.getMessage();
    		esito.setErroreCooperazione(ErroriCooperazione.AUTORIZZAZIONE_FALLITA.getErroreAutorizzazione(errore, CodiceErroreCooperazione.SICUREZZA));
    		esito.setAutorizzato(false);
    		esito.setEccezioneProcessamento(e);
    	}
    	
    	return esito;
    }
	
}
