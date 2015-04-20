/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it). 
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



package org.openspcoop2.pdd.core.autorizzazione.pd;

import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.core.autorizzazione.AutorizzazioneException;

/**
 * Classe che implementa una autorizzazione OpenSPCoop.
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author: mergefairy $
 * @version $Rev: 10491 $, $Date: 2015-01-13 10:33:50 +0100 (Tue, 13 Jan 2015) $
 */

public class AutorizzazioneTestEccezioneProcessamento extends AbstractCore implements IAutorizzazionePortaDelegata {


    @Override
	public EsitoAutorizzazioneIntegrazione process(DatiInvocazionePortaDelegata datiInvocazione) throws AutorizzazioneException{

    	Throwable t1 = new Throwable("Eccezione processamento Test Livello 3");
    	Throwable t2 = new Throwable("Eccezione processamento Test Livello 2",t1);
    	Exception e = new Exception("Eccezione processamento Test Livello 1",t2);
    	throw new AutorizzazioneException("Autorizzazione fallita per verifica Errore Processamento (TestSuiteOpenSPCoop)", e);
    
    	
    }
   
}
