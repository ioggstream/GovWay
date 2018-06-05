package org.openspcoop2.pdd.core.handlers.notifier.engine;

import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.io.notifier.unblocked.AbstractStreamingHandler;
import org.openspcoop2.utils.io.notifier.unblocked.ResultStreamingHandler;

public class NotifierStreamingHandler extends AbstractStreamingHandler {

	private Throwable exception = null;
	private String error = null;
	
	private String idTransazione;
	private TipoMessaggio tipoMessaggio;
	private Properties headerTrasporto;
	private long idDumpConfigurazione;
	private String contentType;
	private NotifierCallback notifierCallback;
	private IDSoggetto dominio;
	
	public NotifierStreamingHandler(NotifierCallback notifierCallback, String idTransazione, TipoMessaggio tipoMessaggio,
			Properties headerTrasporto,
			long idDumpConfigurazione,
			String contentType, Logger log,
			IDSoggetto dominio) throws Exception{
		super(log, OpenSPCoop2Properties.getInstance().getDumpNonRealtime_inMemoryThreshold());
		this.notifierCallback = notifierCallback; // Per i log
		this.idTransazione = idTransazione;
		this.tipoMessaggio = tipoMessaggio;
		this.headerTrasporto = headerTrasporto;
		this.contentType = contentType;
		this.idDumpConfigurazione = idDumpConfigurazione;
		this.dominio = dominio;
	}
	
	@Override
	public boolean isPrematureEnd() throws UtilsException {
		return (this.exception!=null);
	}

	@Override
	public String getError() {
		return this.error;
	}

	@Override
	public Throwable getException() {
		return this.exception;
	}

	@Override
	public ResultStreamingHandler call() throws UtilsException {
		try{
			NotifierResultStreamingHandler result = new NotifierResultStreamingHandler();
			
			OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
			
			if(op2Properties.isDumpNonRealtime_databaseMode()){
				
				this.notifierCallback.debug("Save on database.....");
				
				// save on database
				NotifierDump notifierDump = NotifierDump.getInstance();
				
				this.notifierCallback.debug("Get Instance.....");
				
				int executeUpdate = notifierDump.saveOnDatabase(this.notifierCallback, this.idTransazione, this.tipoMessaggio,
						this.headerTrasporto,
						this.idDumpConfigurazione, this.contentType, this, this.dominio);
				
				this.notifierCallback.debug("Execute: "+executeUpdate);
				
				result.setSaveOnFileSystem(false);
				result.setExecuteUpdateRow(executeUpdate);
				
				this.notifierCallback.debug("Save on database execute with row: "+executeUpdate);
				
			}
			else{
				
				this.notifierCallback.debug("Save on fs.....");
				
				// save on fs
				
				// directory
				File fDir =op2Properties.getDumpNonRealtime_repository();
				if(fDir.exists()==false){
					throw new Exception("Directory ["+fDir.getAbsolutePath()+"] not exists");
				}
				if(fDir.canRead()==false){
					throw new Exception("Directory ["+fDir.getAbsolutePath()+"] not readable");
				}
				if(fDir.canWrite()==false){
					throw new Exception("Directory ["+fDir.getAbsolutePath()+"] not writable");
				}
				
				// messaggio
				File f = new File(fDir, this.idTransazione+"_"+this.tipoMessaggio.toString()+".bin");
				FileOutputStream fout = null;
				//org.apache.commons.io.output.NullOutputStream fout = null;
				try{
					fout = new FileOutputStream(f);
					//fout = new org.apache.commons.io.output.NullOutputStream();
					
					// lettura e scrittura su file
					byte[]buffer = new byte[4096];
					int letti = 0;
					while( (letti=this.read(buffer)) != -1 ){
						fout.write(buffer, 0, letti);
					}
					
				}finally{
					try{
						if(fout!=null){
							fout.flush();
							fout.close();
						}
					}catch(Exception eClose){}
				}
				
				// Risposta
				result.setSaveOnFileSystem(true);
				result.setFile(f);
				
				this.notifierCallback.debug("Save on fs execute: "+f.getAbsolutePath());
			}
			
			return result;
		}catch(Throwable e){
			
			//this.notifierCallback.error("ERRORE HANDLER STREAMING :"+e.getMessage(),e);
			
			this.exception = e;
			this.error = e.getMessage();
			throw new UtilsException(this.error,this.exception);
		}
	}



}