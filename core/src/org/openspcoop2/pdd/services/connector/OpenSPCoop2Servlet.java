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

package org.openspcoop2.pdd.services.connector;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.engine.FunctionContextsCustom;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.engine.constants.IDService;
import org.openspcoop2.protocol.manifest.constants.Costanti;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.slf4j.Logger;

/**
 * OpenSPCoop2Servlet
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@SuppressWarnings("serial")
public class OpenSPCoop2Servlet extends HttpServlet {

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		HttpRequestMethod m = HttpRequestMethod.valueOf(req.getMethod().toUpperCase());
		switch (m) {
		
		// Standard
		
		case DELETE:
			this.doDelete(req, resp);
			break;
		case GET:
			this.doGet(req, resp);
			break;
		case HEAD:
			this.doHead(req, resp);
			break;
		case OPTIONS:
			this.doOptions(req, resp);
			break;
		case POST:
			this.doPost(req, resp);
			break;
		case PUT:
			this.doPut(req, resp);
			break;
		case TRACE:
			this.doTrace(req, resp);
			break;
			
		// Additionals
		case PATCH:
		case LINK:
		case UNLINK:
			boolean enabled = true;
			OpenSPCoop2Properties op2Properties = null;
			try {
				op2Properties = OpenSPCoop2Properties.getInstance();
			}catch(Throwable t) { 
				//come default si lasciano abilitati
			}
			if(HttpRequestMethod.PATCH.equals(m)) {
				enabled = op2Properties.isServiceRequestHttpMethodPatchEnabled();
			}
			else if(HttpRequestMethod.LINK.equals(m)) {
				enabled = op2Properties.isServiceRequestHttpMethodLinkEnabled();
			}
			else if(HttpRequestMethod.UNLINK.equals(m)) {
				enabled = op2Properties.isServiceRequestHttpMethodUnlinkEnabled();
			}
			if(enabled) {
				dispatch(req, resp, m);
			}
			else {
				super.service(req, resp); // richiamo implementazione originale che genera errore: Method XXX is not defined in RFC 2068 and is not supported by the Servlet API
			}
			break;
			
		default:
			super.service(req, resp); // richiamo implementazione originale che genera errore: Method XXX is not defined in RFC 2068 and is not supported by the Servlet API
			break;
		}
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		dispatch(req, resp, HttpRequestMethod.DELETE);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		dispatch(req, resp, HttpRequestMethod.GET);
	}

	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		dispatch(req, resp, HttpRequestMethod.HEAD);
	}

	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		dispatch(req, resp, HttpRequestMethod.OPTIONS);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		dispatch(req, resp, HttpRequestMethod.POST);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		dispatch(req, resp, HttpRequestMethod.PUT);
	}

	@Override
	protected void doTrace(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		dispatch(req, resp, HttpRequestMethod.TRACE);
	}

	
	private void dispatch(HttpServletRequest req, HttpServletResponse res,HttpRequestMethod method) throws ServletException, IOException {
		
		Logger logCore = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		Logger logOpenSPCoop2Servlet = LoggerWrapperFactory.getLogger("govway.startup");
		
		OpenSPCoop2Properties op2Properties = null;
		try {
			
			op2Properties = OpenSPCoop2Properties.getInstance();
			
			boolean printCertificate = false;
			FunctionContextsCustom customContexts = null;
			if(op2Properties!=null) {
				printCertificate = op2Properties.isPrintInfoCertificate();
				customContexts = op2Properties.getCustomContexts();
			}
			
			URLProtocolContext protocolContext = new URLProtocolContext(req, logCore, printCertificate, customContexts);
			String function = protocolContext.getFunction();
			IDService idServiceCustom = protocolContext.getIdServiceCustom();
			
			IProtocolFactory<?> pf = ProtocolFactoryManager.getInstance().getProtocolFactoryByServletContext(protocolContext.getProtocolWebContext());
			if(pf==null){
				if(!Costanti.CONTEXT_EMPTY.equals(protocolContext.getProtocolWebContext()))
					throw new Exception("Non risulta registrato un protocollo con contesto ["+protocolContext.getProtocolWebContext()+"]");
				else
					throw new Exception("Non risulta registrato un protocollo con contesto speciale 'vuoto'");
			}
						
			if( 
					(function.equals(URLProtocolContext.PD_FUNCTION) && op2Properties.isEnabledFunctionPD()) 
					|| 
					(idServiceCustom!=null && IDService.PORTA_DELEGATA.equals(idServiceCustom))
				){
				
				RicezioneContenutiApplicativiConnector r = new RicezioneContenutiApplicativiConnector();
				r.doEngine(ConnectorUtils.getRequestInfo(pf, protocolContext), req, res, method);
				
			}
			else if(
					(function.equals(URLProtocolContext.PDtoSOAP_FUNCTION) && op2Properties.isEnabledFunctionPDtoSOAP()) 
					|| 
					(idServiceCustom!=null && IDService.PORTA_DELEGATA_XML_TO_SOAP.equals(idServiceCustom))
				){
				
				RicezioneContenutiApplicativiHTTPtoSOAPConnector r = new RicezioneContenutiApplicativiHTTPtoSOAPConnector();
				r.doEngine(ConnectorUtils.getRequestInfo(pf, protocolContext), req, res, method);
				
			}
			else if(
					(function.equals(URLProtocolContext.PA_FUNCTION) && op2Properties.isEnabledFunctionPA()) 
					|| 
					(idServiceCustom!=null && IDService.PORTA_APPLICATIVA.equals(idServiceCustom))
				){
				
				RicezioneBusteConnector r = new RicezioneBusteConnector();
				r.doEngine(ConnectorUtils.getRequestInfo(pf, protocolContext), req, res, method);
			}
			
			else if(function.equals(URLProtocolContext.IntegrationManager_FUNCTION) || (idServiceCustom!=null && IDService.INTEGRATION_MANAGER_SOAP.equals(idServiceCustom))){
				
				if(op2Properties.isIntegrationManagerEnabled()==false) {
					throw new Exception("Service ["+function+"] not active");
				}
				
				boolean wsdl = false;
				if(HttpRequestMethod.GET.equals(method)){
					Enumeration<?> parameters = req.getParameterNames();
					while(parameters.hasMoreElements()){
						String key = (String) parameters.nextElement();
						String value = req.getParameter(key);
						if("wsdl".equalsIgnoreCase(key) && (value==null || "".equals(value)) ){
							// richiesta del wsdl
							if(op2Properties!=null && op2Properties.isGenerazioneWsdlIntegrationManagerEnabled()==false){
								res.sendError(404, ConnectorUtils.generateError404Message(ConnectorUtils.getFullCodeWsdlUnsupported(IDService.INTEGRATION_MANAGER_SOAP)));
								return;
							}
							else{
								wsdl = true;
								break;
							}
						}
					}
				}
				
				if(!HttpRequestMethod.POST.equals(method) && !wsdl){
					// messaggio di errore
					boolean errore404 = false;
					if(op2Properties!=null && !op2Properties.isGenerazioneErroreHttpMethodUnsupportedIntegrationManagerEnabled()){
						errore404 = true;
					}
					
					if(errore404){
						res.sendError(404,ConnectorUtils.generateError404Message(ConnectorUtils.getFullCodeHttpMethodNotSupported(IDService.INTEGRATION_MANAGER_SOAP, method)));
						return;
					}
					else{
					
						res.setStatus(500);
						
						ConnectorUtils.generateErrorMessage(IDService.INTEGRATION_MANAGER_SOAP, method, req, res, ConnectorUtils.getMessageHttpMethodNotSupported(method), false, true);
								
						try{
							res.getOutputStream().flush();
						}catch(Exception eClose){}
						try{
							res.getOutputStream().close();
						}catch(Exception eClose){}
						
						return;
					}
				}
								
				// Dispatching al servizio di IntegrationManager implementato tramite CXF
				String serviceIM = protocolContext.getFunctionParameters();
				if(URLProtocolContext.IntegrationManager_SERVICE_PD_GOVWAY.equals(serviceIM) || 
						(URLProtocolContext.IntegrationManager_SERVICE_PD_GOVWAY+"/").equals(serviceIM)) {
					serviceIM = URLProtocolContext.IntegrationManager_SERVICE_PD;
				}
				String forwardUrl = "/"+URLProtocolContext.IntegrationManager_ENGINE+"/"+serviceIM;
				req.setAttribute(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME, protocolContext.getProtocolName());
				req.setAttribute(org.openspcoop2.core.constants.Costanti.PROTOCOL_WEB_CONTEXT, protocolContext.getProtocolWebContext());
				req.setAttribute(org.openspcoop2.core.constants.Costanti.INTEGRATION_MANAGER_ENGINE_AUTHORIZED, true);
				RequestDispatcher dispatcher = req.getRequestDispatcher(forwardUrl);
				dispatcher.forward(req, res);
				
			}
			else if(function.equals(URLProtocolContext.Check_FUNCTION)){
				
				if(HttpRequestMethod.GET.equals(method)==false){
					// messaggio di errore
					boolean errore404 = false;
					if(op2Properties!=null && !op2Properties.isGenerazioneErroreHttpMethodUnsupportedCheckEnabled()){
						errore404 = true;
					}
					
					if(errore404){
						res.sendError(404,ConnectorUtils.generateError404Message(ConnectorUtils.getFullCodeHttpMethodNotSupported(IDService.CHECK_PDD, method)));
						return;
					}
					else{
						
						res.setStatus(500);
						
						ConnectorUtils.generateErrorMessage(IDService.CHECK_PDD,method,req,res, ConnectorUtils.getMessageHttpMethodNotSupported(method), false, true);
								
						try{
							res.getOutputStream().flush();
						}catch(Exception eClose){}
						try{
							res.getOutputStream().close();
						}catch(Exception eClose){}
						
						return;
					}
				}
				
				// Dispatching al servizio 
				CheckStatoPdD checkStatoPdD = new CheckStatoPdD();
				req.setAttribute(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME, protocolContext.getProtocolName());
				checkStatoPdD.doGet(req, res);
				
			}
			else{
				throw new Exception("Service ["+function+"] not supported");
			}
			
		} catch (Exception e) {
			
			StringBuffer bf = new StringBuffer();
			bf.append("RemoteAddr["+req.getRemoteAddr()+"] ");
			bf.append("RemoteHost["+req.getRemoteHost()+"] ");
			bf.append("RemotePort["+req.getRemotePort()+"] ");
			bf.append("RemoteUser["+req.getRemoteUser()+"] ");
			bf.append("LocalAddr["+req.getLocalAddr()+"] ");
			bf.append("LocalHost["+req.getLocalName()+"] ");
			bf.append("LocalPort["+req.getLocalPort()+"] ");
			bf.append("ServerName["+req.getServerName()+"] ");
			bf.append("ServerPort["+req.getServerPort()+"] ");
						
			if(logCore!=null){
				logCore.error(e.getMessage(),e);
				logCore.error("Detail: "+bf.toString());
			}
			else{
				logOpenSPCoop2Servlet.error(e.getMessage(),e);
				logOpenSPCoop2Servlet.error("Detail: "+bf.toString());
			}
			
			// log su file core
			StringBuffer bfLogError = new StringBuffer();
			ConnectorUtils.generateErrorMessage(IDService.OPENSPCOOP2_SERVLET,method,req,bfLogError, e.getMessage(), true, false);
			if(logCore!=null){
				logCore.error(bfLogError.toString());
			}
			else{
				logOpenSPCoop2Servlet.error(bfLogError.toString());
			}
			
			// messaggio di errore
			boolean errore404 = true;
			if(op2Properties!=null && op2Properties.isGenerazioneErroreProtocolloNonSupportato()){
				errore404 = false;
			}
			
			if(errore404){
				res.sendError(404,ConnectorUtils.generateError404Message(ConnectorUtils.getFullCodeProtocolUnsupported(IDService.OPENSPCOOP2_SERVLET)));
			}
			else{
				res.setStatus(500);

				ConnectorUtils.generateErrorMessage(IDService.OPENSPCOOP2_SERVLET,method,req,res, e.getMessage(), true, true);
				
				try{
					res.getOutputStream().flush();
				}catch(Exception eClose){}
				try{
					res.getOutputStream().close();
				}catch(Exception eClose){}
				
			}
			
		}
		

		
	}
	

	

}
