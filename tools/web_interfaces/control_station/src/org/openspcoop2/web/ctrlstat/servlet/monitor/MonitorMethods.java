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


package org.openspcoop2.web.ctrlstat.servlet.monitor;

import java.util.ArrayList;

/**
 * MonitorMethods
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public enum MonitorMethods {

	LISTA_RICHIESTE_PENDENTI("Lista"), STATO_RICHIESTE("Stato"), ELIMINAZIONE_RICHIESTE_PENDENTI("Eliminazione");

	private String nome;

	private static ArrayList<String> methodNames;

	// inizializzo la lista dei nomi
	static {
		MonitorMethods.methodNames = new ArrayList<String>();
		MonitorMethods[] methods = MonitorMethods.values();
		for (MonitorMethods method : methods) {
			MonitorMethods.methodNames.add(method.getNome());
		}
	}

	MonitorMethods(String nome) {
		this.nome = nome;
	}

	public String getNome() {
		return this.nome;
	}

	public static String[] getMethodsNames() {
		return MonitorMethods.methodNames.toArray(new String[MonitorMethods.values().length]);
	}

	public static ArrayList<String> getMethodNames() {
		return new ArrayList<String>(MonitorMethods.methodNames);
	}
}
