/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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



package org.openspcoop2.pdd.core.autorizzazione;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.core.ICore;

/**
 * Interfaccia che definisce un processo di autorizzazione dei contenuti
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author: apoli $
 * @version $Rev: 12942 $, $Date: 2017-05-02 13:01:02 +0200 (Tue, 02 May 2017) $
 */

public interface IAutorizzazioneContenuto extends ICore {

    public default void cleanPostAuth(OpenSPCoop2Message message) {
    	// nop
    }
    
}
