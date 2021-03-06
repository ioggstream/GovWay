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
package org.openspcoop2.utils.transport.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * WrappedHttpServletRequest
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class WrappedHttpServletRequest implements HttpServletRequest {
	
	protected HttpServletRequest httpServletRequest;

	public WrappedHttpServletRequest(HttpServletRequest httpServletRequest) {
		this.httpServletRequest = httpServletRequest;
		
	}

	@Override
	public int getContentLength() {
		return this.httpServletRequest.getContentLength();
	}
	
	@Override
	public String getContentType() {
		return this.httpServletRequest.getContentType();
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		return this.httpServletRequest.getInputStream();
	}
	
	@Override
	public BufferedReader getReader() throws IOException {
		return this.httpServletRequest.getReader();
	}
	
	@Override
	public String getParameter(String key) {
		return this.httpServletRequest.getParameter(key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<java.lang.String,java.lang.String[]> getParameterMap() {
		return this.httpServletRequest.getParameterMap();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Enumeration<String> getParameterNames() {
		return this.httpServletRequest.getParameterNames();
	}

	@Override
	public String[] getParameterValues(String arg0) {
		return this.httpServletRequest.getParameterValues(arg0);
	}

	@Override
	public String getRequestURI() {
		return this.httpServletRequest.getRequestURI();
	}
		
	@Override
	public String getContextPath() {
		return this.httpServletRequest.getContextPath();
	}
	
	@Override
	public String getPathInfo() {
		return this.httpServletRequest.getPathInfo();
	}
	
	@Override
	public String getPathTranslated() {
		return this.httpServletRequest.getPathTranslated();
	}

	@Override
	public String getQueryString() {
		return this.httpServletRequest.getQueryString();
	}
	
	@Override
	public StringBuffer getRequestURL() {
		return this.httpServletRequest.getRequestURL();
	}
	
	@Override
	public String getServletPath() {
		return this.httpServletRequest.getServletPath();
	}

	@SuppressWarnings("deprecation")
	@Override
	@Deprecated
	public String getRealPath(String arg0) {
		return this.httpServletRequest.getRealPath(arg0);
	}
	
	@Override
	public Object getAttribute(String arg0) {
		return this.httpServletRequest.getAttribute(arg0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Enumeration<String> getAttributeNames() {
		return this.httpServletRequest.getAttributeNames();
	}

	@Override
	public String getCharacterEncoding() {
		return this.httpServletRequest.getCharacterEncoding();
	}

	@Override
	public String getLocalAddr() {
		return this.httpServletRequest.getLocalAddr();
	}

	@Override
	public String getLocalName() {
		return this.httpServletRequest.getLocalName();
	}

	@Override
	public int getLocalPort() {
		return this.httpServletRequest.getLocalPort();
	}

	@Override
	public Locale getLocale() {
		return this.httpServletRequest.getLocale();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Enumeration<java.util.Locale> getLocales() {
		return this.httpServletRequest.getLocales();
	}

	@Override
	public String getProtocol() {
		return this.httpServletRequest.getProtocol();
	}

	@Override
	public String getRemoteAddr() {
		return this.httpServletRequest.getRemoteAddr();
	}

	@Override
	public String getRemoteHost() {
		return this.httpServletRequest.getRemoteHost();
	}

	@Override
	public int getRemotePort() {
		return this.httpServletRequest.getRemotePort();
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String arg0) {
		return this.httpServletRequest.getRequestDispatcher(arg0);
	}

	@Override
	public String getScheme() {
		return this.httpServletRequest.getScheme();
	}

	@Override
	public String getServerName() {
		return this.httpServletRequest.getServerName();
	}

	@Override
	public int getServerPort() {
		return this.httpServletRequest.getServerPort();
	}

	@Override
	public boolean isSecure() {
		return this.httpServletRequest.isSecure();
	}

	@Override
	public void removeAttribute(String arg0) {
		this.httpServletRequest.removeAttribute(arg0);
	}

	@Override
	public void setAttribute(String arg0, Object arg1) {
		this.httpServletRequest.setAttribute(arg0,arg1);
	}

	@Override
	public void setCharacterEncoding(String arg0)
			throws UnsupportedEncodingException {
		this.httpServletRequest.setCharacterEncoding(arg0);
	}

	@Override
	public String getAuthType() {
		return this.httpServletRequest.getAuthType();
	}

	@Override
	public Cookie[] getCookies() {
		return this.httpServletRequest.getCookies();
	}

	@Override
	public long getDateHeader(String arg0) {
		return this.httpServletRequest.getDateHeader(arg0);
	}

	@Override
	public String getHeader(String arg0) {
		return this.httpServletRequest.getHeader(arg0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Enumeration<String> getHeaderNames() {
		return this.httpServletRequest.getHeaderNames();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Enumeration<String> getHeaders(String arg0) {
		return this.httpServletRequest.getHeaders(arg0);
	}

	@Override
	public int getIntHeader(String arg0) {
		return this.httpServletRequest.getIntHeader(arg0);
	}

	@Override
	public String getMethod() {
		return this.httpServletRequest.getMethod();
	}

	@Override
	public String getRemoteUser() {
		return this.httpServletRequest.getRemoteUser();
	}

	@Override
	public String getRequestedSessionId() {
		return this.httpServletRequest.getRequestedSessionId();
	}

	@Override
	public HttpSession getSession() {
		return this.httpServletRequest.getSession();
	}

	@Override
	public HttpSession getSession(boolean arg0) {
		return this.httpServletRequest.getSession(arg0);
	}

	@Override
	public Principal getUserPrincipal() {
		return this.httpServletRequest.getUserPrincipal();
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		return this.httpServletRequest.isRequestedSessionIdFromCookie();
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		return this.httpServletRequest.isRequestedSessionIdFromURL();
	}

	@SuppressWarnings("deprecation")
	@Override
	@Deprecated
	public boolean isRequestedSessionIdFromUrl() {
		return this.httpServletRequest.isRequestedSessionIdFromUrl();
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		return this.httpServletRequest.isRequestedSessionIdValid();
	}

	@Override
	public boolean isUserInRole(String arg0) {
		return this.httpServletRequest.isUserInRole(arg0);
	}
	
}
