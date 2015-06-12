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
package org.openspcoop2.generic_project.web.impl.jsf2.output.impl;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.generic_project.web.output.OutputField;
import org.openspcoop2.generic_project.web.output.OutputGroup;

/***
 * 
 * Implementazione di un elemento di tipo Group.
 * 
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *
 */
public class OutputGroupImpl implements OutputGroup{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id = null;
	
	private Integer columns = null;
		
	private List<OutputField<?>> fields = null;
	
	private boolean rendered = true;
	
	private String label = null;
	
	private String columnClasses = null;
	
	private String styleClass = null;
	
	public OutputGroupImpl(){
		this.fields = new ArrayList<OutputField<?>>();
		this.columns = 2;
		this.columnClasses = ""; //"gridContent verticalAlignTop";
		this.styleClass = "";
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public void setId(String idGroup) {
		this.id = idGroup;
	}

	@Override
	public Integer getColumns() {
		return this.columns;
	}

	@Override
	public void setColumns(Integer columns) {
		this.columns = columns;
	}

	@Override
	public List<OutputField<?>> getFields() {
		return this.fields;
	}

	@Override
	public void setFields(List<OutputField<?>> listaOutput) {
		this.fields = listaOutput;
	}

	@Override
	public boolean isRendered() {
		return this.rendered;
	}

	@Override
	public void setRendered(boolean rendered) {
		this.rendered = rendered;
	}

	@Override
	public String getLabel() {
		return this.label;
	}

	@Override
	public void setLabel(String label) {
		this.label = label;
	}
	
	@Override
	public void addField(OutputField<?> field){
		this.fields.add(field);
	}

	@Override
	public String getColumnClasses() {
		return this.columnClasses;
	}

	@Override
	public void setColumnClasses(String columnClasses) {
		this.columnClasses = columnClasses;
	}

	@Override
	public String getStyleClass() {
		return this.styleClass;
	}

	@Override
	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}
}