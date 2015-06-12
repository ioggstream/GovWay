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
package org.openspcoop2.generic_project.web.table.factory;

import java.io.Serializable;

import org.openspcoop2.generic_project.web.table.PagedDataTable;
import org.openspcoop2.generic_project.web.table.Table;
import org.openspcoop2.generic_project.web.factory.FactoryException;
import org.openspcoop2.generic_project.web.form.Form;
import org.openspcoop2.generic_project.web.form.SearchForm;

/***
 * 
 * Interfaccia base che definisce la factory delle tabelle
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *
 */
public interface TableFactory extends Serializable{

	public <V> Table<V> createTable() throws FactoryException;

	public <V,FormType extends Form,SearchFormType extends SearchForm> PagedDataTable<V, FormType, SearchFormType> createPagedDataTable() throws FactoryException;
}