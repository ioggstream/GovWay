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
package org.openspcoop2.monitor.engine.config.base.dao.jdbc.converter;

import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.expression.impl.sql.AbstractSQLFieldConverter;
import org.openspcoop2.utils.TipiDatabase;

import org.openspcoop2.monitor.engine.config.base.Plugin;


/**     
 * PluginFieldConverter
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PluginFieldConverter extends AbstractSQLFieldConverter {

	private TipiDatabase databaseType;
	
	public PluginFieldConverter(String databaseType){
		this.databaseType = TipiDatabase.toEnumConstant(databaseType);
	}
	public PluginFieldConverter(TipiDatabase databaseType){
		this.databaseType = databaseType;
	}


	@Override
	public IModel<?> getRootModel() throws ExpressionException {
		return Plugin.model();
	}
	
	@Override
	public TipiDatabase getDatabaseType() throws ExpressionException {
		return this.databaseType;
	}
	


	@Override
	public String toColumn(IField field,boolean returnAlias,boolean appendTablePrefix) throws ExpressionException {
		
		// In the case of columns with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the column containing the alias
		
		if(field.equals(Plugin.model().TIPO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo";
			}else{
				return "tipo";
			}
		}
		if(field.equals(Plugin.model().CLASS_NAME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".class_name";
			}else{
				return "class_name";
			}
		}
		if(field.equals(Plugin.model().DESCRIZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".descrizione";
			}else{
				return "descrizione";
			}
		}
		if(field.equals(Plugin.model().LABEL)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".label";
			}else{
				return "label";
			}
		}
		if(field.equals(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.URI_ACCORDO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".uri_accordo";
			}else{
				return "uri_accordo";
			}
		}
		if(field.equals(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.SERVIZIO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".servizio";
			}else{
				return "servizio";
			}
		}
		if(field.equals(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.PLUGIN_SERVIZIO_AZIONE_COMPATIBILITA.AZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".azione";
			}else{
				return "azione";
			}
		}
		if(field.equals(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.TIPO_MITTENTE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo_mittente";
			}else{
				return "tipo_mittente";
			}
		}
		if(field.equals(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.NOME_MITTENTE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome_mittente";
			}else{
				return "nome_mittente";
			}
		}
		if(field.equals(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.IDPORTA_MITTENTE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".idporta_mittente";
			}else{
				return "idporta_mittente";
			}
		}
		if(field.equals(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.TIPO_DESTINATARIO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo_destinatario";
			}else{
				return "tipo_destinatario";
			}
		}
		if(field.equals(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.NOME_DESTINATARIO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome_destinatario";
			}else{
				return "nome_destinatario";
			}
		}
		if(field.equals(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.IDPORTA_DESTINATARIO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".idporta_destinatario";
			}else{
				return "idporta_destinatario";
			}
		}
		if(field.equals(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.TIPO_SERVIZIO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo_servizio";
			}else{
				return "tipo_servizio";
			}
		}
		if(field.equals(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.NOME_SERVIZIO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome_servizio";
			}else{
				return "nome_servizio";
			}
		}
		if(field.equals(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.VERSIONE_SERVIZIO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".versione_servizio";
			}else{
				return "versione_servizio";
			}
		}
		if(field.equals(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.AZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".azione";
			}else{
				return "azione";
			}
		}


		return super.toColumn(field,returnAlias,appendTablePrefix);
		
	}
	
	@Override
	public String toTable(IField field,boolean returnAlias) throws ExpressionException {
		
		// In the case of table with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the table containing the alias
		
		if(field.equals(Plugin.model().TIPO)){
			return this.toTable(Plugin.model(), returnAlias);
		}
		if(field.equals(Plugin.model().CLASS_NAME)){
			return this.toTable(Plugin.model(), returnAlias);
		}
		if(field.equals(Plugin.model().DESCRIZIONE)){
			return this.toTable(Plugin.model(), returnAlias);
		}
		if(field.equals(Plugin.model().LABEL)){
			return this.toTable(Plugin.model(), returnAlias);
		}
		if(field.equals(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.URI_ACCORDO)){
			return this.toTable(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA, returnAlias);
		}
		if(field.equals(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.SERVIZIO)){
			return this.toTable(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA, returnAlias);
		}
		if(field.equals(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.PLUGIN_SERVIZIO_AZIONE_COMPATIBILITA.AZIONE)){
			return this.toTable(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.PLUGIN_SERVIZIO_AZIONE_COMPATIBILITA, returnAlias);
		}
		if(field.equals(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.TIPO_MITTENTE)){
			return this.toTable(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA, returnAlias);
		}
		if(field.equals(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.NOME_MITTENTE)){
			return this.toTable(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA, returnAlias);
		}
		if(field.equals(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.IDPORTA_MITTENTE)){
			return this.toTable(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA, returnAlias);
		}
		if(field.equals(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.TIPO_DESTINATARIO)){
			return this.toTable(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA, returnAlias);
		}
		if(field.equals(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.NOME_DESTINATARIO)){
			return this.toTable(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA, returnAlias);
		}
		if(field.equals(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.IDPORTA_DESTINATARIO)){
			return this.toTable(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA, returnAlias);
		}
		if(field.equals(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.TIPO_SERVIZIO)){
			return this.toTable(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA, returnAlias);
		}
		if(field.equals(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.NOME_SERVIZIO)){
			return this.toTable(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA, returnAlias);
		}
		if(field.equals(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.VERSIONE_SERVIZIO)){
			return this.toTable(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA, returnAlias);
		}
		if(field.equals(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.AZIONE)){
			return this.toTable(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA, returnAlias);
		}


		return super.toTable(field,returnAlias);
		
	}

	@Override
	public String toTable(IModel<?> model,boolean returnAlias) throws ExpressionException {
		
		// In the case of table with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the table containing the alias
		
		if(model.equals(Plugin.model())){
			return "plugins";
		}
		if(model.equals(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA)){
			return "plugins_servizi_comp";
		}
		if(model.equals(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.PLUGIN_SERVIZIO_AZIONE_COMPATIBILITA)){
			return "plugins_azioni_comp";
		}
		if(model.equals(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA)){
			return "plugins_filtro_comp";
		}


		return super.toTable(model,returnAlias);
		
	}

}
