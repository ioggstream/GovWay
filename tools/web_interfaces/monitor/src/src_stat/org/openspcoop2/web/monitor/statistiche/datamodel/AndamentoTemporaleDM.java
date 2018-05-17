package org.openspcoop2.web.monitor.statistiche.datamodel;

import org.openspcoop2.web.monitor.core.datamodel.BaseDataModel;
import org.openspcoop2.web.monitor.core.datamodel.Res;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.statistiche.bean.StatsSearchForm;
import org.openspcoop2.web.monitor.statistiche.dao.IStatisticheGiornaliere;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import org.ajax4jsf.model.DataVisitor;
import org.ajax4jsf.model.Range;
import org.ajax4jsf.model.SequenceRange;
import org.slf4j.Logger;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.SortOrder;

public class AndamentoTemporaleDM extends BaseDataModel<Long, Res, IStatisticheGiornaliere> {

	private static final long serialVersionUID = 500153520162806619L;
	private static Logger log =  LoggerManager.getPddMonitorCoreLogger();
	
	private StatsSearchForm search;
	private boolean visualizzaComandiExport = false;
	
	public void setSearch(StatsSearchForm search) {
		this.search = search;
	}
	
	@Override
	public int getRowCount() {
		try {
			this.visualizzaComandiExport = false;
			int count = this.getDataProvider().countAllAndamentoTemporale();
			
			if(count > 0)
				this.visualizzaComandiExport = true;
			
			return count;
		} catch (ServiceException e) {
			AndamentoTemporaleDM.log.error(e.getMessage(), e);
		}
		return 0;
	}
	
	@Override
	public void walk(FacesContext context, DataVisitor visitor, Range range,
			Object argument) throws IOException {
		try{	
			if(this.detached){
				for (Long key : this.wrappedKeys) {
					setRowKey(key);
					visitor.process(context, key, argument);
				}
			}else{
				int start = ((SequenceRange)range).getFirstRow();
				int limit = ((SequenceRange)range).getRows();

				this.wrappedKeys = new ArrayList<Long>();
				this.search.setSortOrder(SortOrder.DESC); 
				
				List<Res> list = new ArrayList<Res>();
				
				try {
					list =  this.getDataProvider().findAllAndamentoTemporale(start, limit);
				} catch (ServiceException e) {
					AndamentoTemporaleDM.log.error(e.getMessage(), e);
				}
				
				for (Res r : list) {
					this.wrappedData.put(r.getId(), r);
					this.wrappedKeys.add(r.getId());
					visitor.process(context, r.getId(), argument);
				}
			}
		} catch (Exception e) {
			AndamentoTemporaleDM.log.error(e.getMessage(), e);
		}

	}
	
	public boolean isVisualizzaComandiExport() {
		return this.visualizzaComandiExport;
	}

	public void setVisualizzaComandiExport(boolean visualizzaComandiExport) {
		this.visualizzaComandiExport = visualizzaComandiExport;
	}
	
}