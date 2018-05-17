package org.openspcoop2.monitor.engine.dynamic;

import org.openspcoop2.monitor.sdk.condition.Context;
import org.openspcoop2.monitor.sdk.constants.StatisticType;
import org.openspcoop2.monitor.sdk.exceptions.SearchException;
import org.openspcoop2.monitor.sdk.parameters.Parameter;
import org.openspcoop2.monitor.sdk.plugins.ISearchArguments;
import org.openspcoop2.monitor.sdk.plugins.ISearchProcessing;
import org.openspcoop2.monitor.sdk.plugins.IStatisticProcessing;

import java.util.List;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;

/**
 * BasicLoader
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BasicLoader implements IDynamicLoader{

	private static Logger log = LoggerWrapperFactory.getLogger(BasicLoader.class);

	private String className;
	private Class<?> c;

	protected BasicLoader(String className, Class<?> c) {
		this.className = className;
		this.c = c;
	}
	
	@Override
	public String getClassName() throws SearchException{
		if(this.c!=null){
			return this.c.getName();
		}
		throw new SearchException("Class undefined");
	}
	
	@Override
	public String getClassSimpleName() throws SearchException{
		if(this.c!=null){
			return this.c.getSimpleName();
		}
		throw new SearchException("Class undefined");
	}
	
	@Override
	public Object newInstance() throws SearchException{
		try{
			return this.c.newInstance();
		}catch (Exception e) {
			throw new SearchException(e.getMessage(),e);
		}
	}
	
	@Override
	public List<Parameter<?>> getParameters(Context context) throws SearchException{
		try{
			
			Object obj = this.c.newInstance();

			if(obj instanceof ISearchProcessing){				
				return ((ISearchProcessing) obj).getParameters(context);
			}else if(obj instanceof IStatisticProcessing){
				return ((IStatisticProcessing) obj).getParameters(context);
			}else if(obj instanceof ISearchArguments){
				return ((ISearchArguments) obj).getParameters(context);
			}else{
				String iface = ISearchArguments.class.getName();
				throw new SearchException("La classe ["+this.className+"] non implementa l'interfaccia ["+iface+"]");
			}
			
		}
//		catch(ClassNotFoundException cnfe){
//			BasicLoader.log.error("Impossibile caricare il plugin. La classe indicata ["+this.className+"] non esiste.",cnfe);
//			throw new SearchException("Impossibile caricare il plugin. La classe indicata ["+this.className+"] non esiste.");
//		}
		catch (SearchException re) {
			throw re;
		}catch (Exception e) {
			BasicLoader.log.error("Impossibile ottenere informazioni per il rendereng dal Loader: "+e.getMessage(),e);
			throw new SearchException(e.getMessage(),e);
		}
	}
	
	@Override
	public void updateRendering(Parameter<?> parameter, Context context) throws SearchException{
		try{
			Object obj = this.c.newInstance();

			if(obj instanceof ISearchProcessing){				
				((ISearchProcessing) obj).updateRendering(parameter, context);
			}else if(obj instanceof IStatisticProcessing){
				((IStatisticProcessing) obj).updateRendering(parameter, context);
			}else if(obj instanceof ISearchArguments){
				((ISearchArguments) obj).updateRendering(parameter, context);
			}else{
				String iface = ISearchArguments.class.getName();
				throw new SearchException("La classe ["+this.className+"] non implementa l'interfaccia ["+iface+"]");
			}
		}
//		catch(ClassNotFoundException cnfe){
//			BasicLoader.log.error("Impossibile caricare il plugin. La classe indicata ["+this.className+"] non esiste.",cnfe);
//			throw new SearchException("Impossibile caricare il plugin. La classe indicata ["+this.className+"] non esiste.");
//		}
		catch (SearchException re) {
			throw re;
		}catch (Exception e) {
			BasicLoader.log.error("Impossibile ottenere informazioni per il rendereng dal Loader: "+e.getMessage(),e);
			throw new SearchException(e.getMessage(),e);
		}
	}

	@Override
	public void valueSelectedListener(Parameter<?> parameter, Context context) {
		try{
			Object obj = this.c.newInstance();

			if(obj instanceof ISearchProcessing){
				((ISearchProcessing) obj).onChangeValue(parameter, context);
			}else if(obj instanceof IStatisticProcessing){
				((IStatisticProcessing) obj).onChangeValue(parameter, context);
			}else if(obj instanceof ISearchArguments){
				((ISearchArguments) obj).onChangeValue(parameter, context);
			}else{
				String iface = ISearchArguments.class.getName();
				throw new SearchException("La classe ["+this.className+"] non implementa l'interfaccia ["+iface+"]");
			}
		}
//		catch(ClassNotFoundException cnfe){
//			BasicLoader.log.error("Impossibile caricare il plugin. La classe indicata ["+this.className+"] non esiste.",cnfe);
//			throw new RuntimeException("Impossibile caricare il plugin. La classe indicata ["+this.className+"] non esiste.");
//		}
		catch (Exception e) {
			BasicLoader.log.error(e.getMessage(),e);
		}

	}

	@Override
	public List<StatisticType> getEnabledStatisticType(Context context)
			throws SearchException {
		try{
			Object obj = this.c.newInstance();

			if(obj instanceof IStatisticProcessing){
				return ((IStatisticProcessing) obj).getEnabledStatisticType();
			}else{
				String iface = ISearchArguments.class.getName();
				throw new SearchException("La classe ["+this.className+"] non implementa l'interfaccia ["+iface+"]");
			}
		}
//		catch(ClassNotFoundException cnfe){
//			BasicLoader.log.error("Impossibile caricare il plugin. La classe indicata ["+this.className+"] non esiste.",cnfe);
//			throw new SearchException("Impossibile caricare il plugin. La classe indicata ["+this.className+"] non esiste.");
//		}
		catch (SearchException re) {
			throw re;
		}catch (Exception e) {
			BasicLoader.log.error(e.getMessage(),e);
			throw new SearchException(e.getMessage(),e);
		}

	}

}