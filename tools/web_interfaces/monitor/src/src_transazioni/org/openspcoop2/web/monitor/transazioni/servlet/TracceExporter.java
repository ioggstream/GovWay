package org.openspcoop2.web.monitor.transazioni.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.XMLRootElement;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.constants.TipoSerializzazione;
import org.openspcoop2.protocol.sdk.tracciamento.DriverTracciamentoException;
import org.openspcoop2.protocol.sdk.tracciamento.DriverTracciamentoNotFoundException;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaDriver;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaSerializer;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.web.monitor.core.bean.LoginBean;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioneBean;
import org.openspcoop2.web.monitor.transazioni.core.UtilityTransazioni;
import org.openspcoop2.web.monitor.transazioni.dao.ITransazioniService;
import org.openspcoop2.web.monitor.transazioni.exporter.CostantiExport;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class TracceExporter extends HttpServlet{

	private static final long serialVersionUID = 1272767433184676700L;
	private static Logger log =  LoggerManager.getPddMonitorCoreLogger();
	private transient ITracciaDriver tracciamentoService = null;
	
	private static Boolean enableHeaderInfo = false;
	
	@Override
	public void init() throws ServletException {
		try{
			PddMonitorProperties govwayMonitorProperties = PddMonitorProperties.getInstance(TracceExporter.log);
			TracceExporter.enableHeaderInfo = govwayMonitorProperties.isAttivoTransazioniExportHeader();
			
			this.tracciamentoService = govwayMonitorProperties.getDriverTracciamento();
		}catch(Exception e){
			TracceExporter.log.warn("Inizializzazione servlet fallita, setto enableHeaderInfo=false",e);
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.processRequest(req,resp);		
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		this.processRequest(req,resp);		
	}
	
	
	private void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException,IOException{
		try{
			ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
			
			ITransazioniService service = (ITransazioniService)context.getBean("transazioniService");
			
			// Then we have to get the Response where to write our file
			HttpServletResponse response = resp;

			String isAllString = req.getParameter(CostantiExport.PARAMETER_IS_ALL);
			Boolean isAll = Boolean.parseBoolean(isAllString);
			String idtransazioni=req.getParameter(CostantiExport.PARAMETER_IDS);
			String[] ids = StringUtils.split(idtransazioni, ",");
			
			
			//Check Parametri di export
			HttpSession sessione = req.getSession();

			// Prelevo i parametri necessari
			Boolean isAllFromSession = (Boolean) sessione.getAttribute(CostantiExport.PARAMETER_IS_ALL_ORIGINALE);
			String idTransazioniFromSession = (String) sessione.getAttribute(CostantiExport.PARAMETER_ID_TRANSAZIONI_ORIGINALI);

			//Rimuovo i parametri utilizzati dalla sessione
			sessione.removeAttribute(CostantiExport.PARAMETER_IS_ALL_ORIGINALE);
			sessione.removeAttribute(CostantiExport.PARAMETER_ID_TRANSAZIONI_ORIGINALI);

			String[] idsFromSession = StringUtils.split(idTransazioniFromSession, ",");

			//I parametri in sessione devono coincidere con quelli della request
			boolean exportConsentito = DiagnosticiExporter.checkParametri(isAll,ids,isAllFromSession,idsFromSession);

			if(!exportConsentito){
				
				String msg_errore = "L'utente non dispone dei permessi necessari per effettuare l'export delle tracce.";
				String redirectUrl = req.getContextPath()+"/public/error.jsf?msg_errore=" + msg_errore;
				
				response.sendRedirect(redirectUrl);
				return;
				
//				throw new ExportException("Errore durante l'export dei messaggi diagnostici: i parametri indicati non sono validi!");
			}
			
			// Be sure to retrieve the absolute path to the file with the required
			// method
			// filePath = pathToTheFile;

			// This is another important attribute for the header of the response
			// Here fileName, is a String with the name that you will suggest as a
			// name to save as
			// I use the same name as it is stored in the file system of the server.

			String fileName = "Tracce.zip";

			// Setto Proprietà Export File
			HttpUtilities.setOutputFile(response, true, fileName);
				        
	        // committing status and headers
	        response.setStatus(200);
	        response.flushBuffer();
			
			
			int start = 0;
			int limit = 25;
			List<TransazioneBean> transazioni = new ArrayList<TransazioneBean>();
			//TransazioniSearchForm search = (TransazioniSearchForm)context.getBean("searchFormTransazioni");
			
			Utility.setLoginMBean((LoginBean)context.getBean("loginBean"));
			
			
			if(isAll)
				transazioni = service.findAll(start, limit);
			else{
				for (int j = 0; j < ids.length; j++) {
					transazioni.add(service.findByIdTransazione(ids[j]));					
				}
			}
				
			if(transazioni.size()>0){
				//int i = 0;// progressivo per evitare entry duplicate nel file zip
				// Create a buffer for reading the files
				byte[] buf = new byte[1024];
				ZipOutputStream zip = new ZipOutputStream(response.getOutputStream());
				InputStream in = null;
			
				while(transazioni.size()>0){

					for(TransazioneBean t: transazioni){
						//recupero i diagnostici per questa transazione


						//devo impostare solo l'idtransazione
						//filter.setIdEgov(this.diagnosticiBean.getIdEgov());	
						Hashtable<String, String> properties = new Hashtable<String, String>();
						properties.put("id_transazione", t.getIdTransazione());

						Traccia tracciaRichiesta = null;
						Traccia tracciaRisposta  = null;
						ArrayList<Traccia> tracce = new ArrayList<Traccia>();
						try{
							tracciaRichiesta=this.tracciamentoService.getTraccia(RuoloMessaggio.RICHIESTA,properties);
							tracce.add(tracciaRichiesta);
						}catch(DriverTracciamentoException e){
							//ignore
						}catch(DriverTracciamentoNotFoundException e){
							//ignore
						}
						try{
							tracciaRisposta = this.tracciamentoService.getTraccia(RuoloMessaggio.RISPOSTA,properties);
							tracce.add(tracciaRisposta);
						}catch(DriverTracciamentoException e){
							//ignore
						}catch(DriverTracciamentoNotFoundException e){
							//ignore
						}
						if(tracce.size()>0){
							// Add ZIP entry to output stream.
							zip.putNextEntry(new ZipEntry(/*++i + "_" + */t.getIdTransazione() + " (" + tracce.size() + " entries)" + ".xml"));
							if(TracceExporter.enableHeaderInfo){
								zip.write(UtilityTransazioni.getHeaderTransazione(t).getBytes());
							}	
							
							String tail = null;			
							for (int j = 0; j < tracce.size(); j++) {
								Traccia tr = tracce.get(j);
								String newLine = j > 0 ? "\n\n" : "";
								
								IProtocolFactory<?> pf = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(tr.getProtocollo());
								ITracciaSerializer tracciaBuilder = pf.createTracciaSerializer();
								
								if(j==0){
									XMLRootElement xmlRootElement = tracciaBuilder.getXMLRootElement();
									if(xmlRootElement!=null){
										String head = xmlRootElement.getAsStringStartTag();
										if(head!=null && !"".equals(head)){
											head = head +"\n\n";
											zip.write(head.getBytes(), 0, head.length());
											tail = xmlRootElement.getAsStringEndTag();
			    							if(tail!=null && !"".equals(tail)){
			    								tail = "\n\n" + tail;
			    							}
										}
									}
								}
								
								String traccia = tracciaBuilder.toString(tr, TipoSerializzazione.DEFAULT);
								in = new ByteArrayInputStream((newLine + traccia).getBytes());
								// Transfer bytes from the input stream to the ZIP file
								int len;
								while ((len = in.read(buf)) > 0) {
									zip.write(buf, 0, len);
								}

							}
							if(tail!=null && !"".equals(tail)){
								zip.write(tail.getBytes(), 0, tail.length());
							}

							// Complete the entry
							zip.closeEntry();
							zip.flush();
							in.close();
						}
					}

					start+=limit;

					response.flushBuffer();

					if(!isAll)
						break;
					else
						transazioni = service.findAll(start, limit);
				}
				
				
				zip.flush();
				zip.close();
			}
			
			
		}catch(Throwable e){
			TracceExporter.log.error(e.getMessage(),e);
			throw new ServletException(e.getMessage(),e);
		}
	}
	
	
}
