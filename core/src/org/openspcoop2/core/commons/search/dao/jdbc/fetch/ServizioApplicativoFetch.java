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
package org.openspcoop2.core.commons.search.dao.jdbc.fetch;

import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.dao.jdbc.utils.AbstractJDBCFetch;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCParameterUtilities;
import org.openspcoop2.generic_project.exception.ServiceException;

import java.sql.ResultSet;
import java.util.Map;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.jdbc.IKeyGeneratorObject;

import org.openspcoop2.core.commons.search.ServizioApplicativo;


/**     
 * ServizioApplicativoFetch
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServizioApplicativoFetch extends AbstractJDBCFetch {

	@Override
	public Object fetch(TipiDatabase tipoDatabase, IModel<?> model , ResultSet rs) throws ServiceException {
		
		try{
			JDBCParameterUtilities jdbcParameterUtilities =  
					new JDBCParameterUtilities(tipoDatabase);

			if(model.equals(ServizioApplicativo.model())){
				ServizioApplicativo object = new ServizioApplicativo();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setNome", ServizioApplicativo.model().NOME.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "nome", ServizioApplicativo.model().NOME.getFieldType()));
				setParameter(object, "setTipologiaFruizione", ServizioApplicativo.model().TIPOLOGIA_FRUIZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "tipologia_fruizione", ServizioApplicativo.model().TIPOLOGIA_FRUIZIONE.getFieldType()));
				setParameter(object, "setTipologiaErogazione", ServizioApplicativo.model().TIPOLOGIA_EROGAZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "tipologia_erogazione", ServizioApplicativo.model().TIPOLOGIA_EROGAZIONE.getFieldType()));
				return object;
			}
			
			else{
				throw new ServiceException("Model ["+model.toString()+"] not supported by fetch: "+this.getClass().getName());
			}	
					
		}catch(Exception e){
			throw new ServiceException("Model ["+model.toString()+"] occurs error in fetch: "+e.getMessage(),e);
		}
		
	}
	
	@Override
	public Object fetch(TipiDatabase tipoDatabase, IModel<?> model , Map<String,Object> map ) throws ServiceException {
		
		try{

			if(model.equals(ServizioApplicativo.model())){
				ServizioApplicativo object = new ServizioApplicativo();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"id"));
				setParameter(object, "setNome", ServizioApplicativo.model().NOME.getFieldType(),
					this.getObjectFromMap(map,"nome"));
				setParameter(object, "setTipologiaFruizione", ServizioApplicativo.model().TIPOLOGIA_FRUIZIONE.getFieldType(),
					this.getObjectFromMap(map,"tipologia_fruizione"));
				setParameter(object, "setTipologiaErogazione", ServizioApplicativo.model().TIPOLOGIA_EROGAZIONE.getFieldType(),
					this.getObjectFromMap(map,"tipologia_erogazione"));
				return object;
			}
			
			else{
				throw new ServiceException("Model ["+model.toString()+"] not supported by fetch: "+this.getClass().getName());
			}	
					
		}catch(Exception e){
			throw new ServiceException("Model ["+model.toString()+"] occurs error in fetch: "+e.getMessage(),e);
		}
		
	}
	
	
	@Override
	public IKeyGeneratorObject getKeyGeneratorObject( IModel<?> model )  throws ServiceException {
		
		try{

			if(model.equals(ServizioApplicativo.model())){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject("servizi_applicativi","id","seq_servizi_applicativi","servizi_applicativi_init_seq");
			}
			
			else{
				throw new ServiceException("Model ["+model.toString()+"] not supported by getKeyGeneratorObject: "+this.getClass().getName());
			}

		}catch(Exception e){
			throw new ServiceException("Model ["+model.toString()+"] occurs error in getKeyGeneratorObject: "+e.getMessage(),e);
		}
		
	}

}
