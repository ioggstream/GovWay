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
package org.openspcoop2.generic_project.expression.impl.formatter;

import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.utils.TipiDatabase;

/**
 * BooleanTypeFormatter
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BooleanTypeFormatter implements ITypeFormatter<Boolean> {

	@Override
	public String toString(Boolean o) throws ExpressionException {
		if(o == null){
			throw new ExpressionException("Object parameter is null");
		}
		if(o)
			return "t";
		else
			return "f";
	}
	
	@Override
	public String toSQLString(Boolean o) throws ExpressionException {
		return this.toSQLString(o, TipiDatabase.DEFAULT);
	}
	
	@Override
	public String toSQLString(Boolean o, TipiDatabase databaseType) throws ExpressionException {
		switch (databaseType) {
		case ORACLE:
			if(o!=null && o){
				return "1";
			}
			else{
				return "0";
			}
		case POSTGRESQL:
		case MYSQL:
		case HSQL:
			if(o!=null && o){
				return "true";
			}
			else{
				return "false";
			}
		case SQLSERVER:
			if(o!=null && o){
				return "'true'";
			}
			else{
				return "'false'";
			}
		case DEFAULT:
		default:
			return "'"+toString(o)+"'";
		}

	}

	@Override
	public Boolean toObject(String o,Class<?> c) throws ExpressionException {
		if(o == null){
			throw new ExpressionException("Object parameter is null");
		}
		try{
			if("t".equalsIgnoreCase(o)){
				return true;
			}else if("f".equalsIgnoreCase(o)){
				return false;
			}else{
				throw new Exception("String format error (expect 't' or 'f')");
			}
			
		}catch(Exception e){
			throw new ExpressionException("Conversion failure: "+e.getMessage(),e);
		}
	}

	@Override
	public Class<Boolean> getTypeSupported() {
		return Boolean.class;
	}

}
