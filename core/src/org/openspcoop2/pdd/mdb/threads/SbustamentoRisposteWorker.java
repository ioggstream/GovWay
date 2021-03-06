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


package org.openspcoop2.pdd.mdb.threads;


import org.openspcoop2.pdd.core.state.OpenSPCoopStateException;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateful;
import org.openspcoop2.pdd.mdb.EsitoLib;
import org.openspcoop2.pdd.mdb.GenericMessage;
import org.openspcoop2.pdd.mdb.SbustamentoRisposte;

/**
 * @author Fabio Tronci (tronci@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SbustamentoRisposteWorker extends ModuloAlternativoWorker
		implements IWorker {

	private final static String idModulo = "SbustamentoRisposte";

	public SbustamentoRisposteWorker(MessageIde ide) {
		super(SbustamentoRisposteWorker.idModulo , ide);
	}


	@Override
	protected EsitoLib onMessage(GenericMessage messaggio, String idMessaggio) throws OpenSPCoopStateException{
		EsitoLib esito = new EsitoLib();
		SbustamentoRisposte lib = null;
		try{
			lib = new SbustamentoRisposte(this.log);
		}catch(Exception e){
			throw new OpenSPCoopStateException(e.getMessage(),e);
		}
		OpenSPCoopStateful openspcoopstate = new OpenSPCoopStateful();
		openspcoopstate.setMessageLib( messaggio  );
		openspcoopstate.setIDMessaggioSessione(idMessaggio);
		esito = lib.onMessage(openspcoopstate);
		return esito;
	}
	
}
