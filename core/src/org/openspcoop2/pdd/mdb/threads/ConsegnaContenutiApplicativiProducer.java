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


package org.openspcoop2.pdd.mdb.threads;


import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * @author Fabio Tronci (tronci@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConsegnaContenutiApplicativiProducer extends ModuloAlternativoProducer {

	private static String idModulo = "ConsegnaContenutiApplicativi";
	
	public ConsegnaContenutiApplicativiProducer(BlockingQueue<IWorker> coda) {
		super(ConsegnaContenutiApplicativiProducer.idModulo,coda);
	}

	@Override
	public void creaWorkers(List<MessageIde> messaggiTrovati) {
		for (int i=0;i<messaggiTrovati.size();i++){
			MessageIde ide = messaggiTrovati.get(i);
			System.out.println(this.ID_MODULO+ "Producer: trovato messaggio, creo task...");
			ConsegnaContenutiApplicativiWorker task = new ConsegnaContenutiApplicativiWorker(ide);
			try {
				this.coda.put(task);
			} catch (InterruptedException e) {
				System.out.println(this.ID_MODULO+" :errore "+e);
			}
		}
			
	}
	
}
