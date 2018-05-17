package org.openspcoop2.web.monitor.core.core;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.xml.PrettyPrintXMLUtils;
import org.openspcoop2.utils.xml.XMLUtils;


public class Utils {


	private static Logger log = LoggerWrapperFactory.getLogger(Utils.class);

	protected static ClassLoader getCurrentClassLoader(Object defaultObject){

		ClassLoader loader = Thread.currentThread().getContextClassLoader();

		if(loader == null){
			loader = defaultObject.getClass().getClassLoader();
		}

		return loader;
	}

	public static String getMessageFromResourceBundle(
			String bundleName, 
			String key, 
			Object params[], 
			Locale locale){

		String text = null;

		ResourceBundle bundle = 
				ResourceBundle.getBundle(bundleName, locale, 
						Utils.getCurrentClassLoader(params));

		try{
			text = bundle.getString(key);
		} catch(MissingResourceException e){
			text = "?? key " + key + " not found ??";
		}

		if(params != null){
			MessageFormat mf = new MessageFormat(text, locale);
			text = mf.format(params, new StringBuffer(), null).toString();
		}

		return text;
	}

	public static String getMessageFromJSFBundle(String bundleName,String key) {
		ResourceBundle rb = ResourceBundle.getBundle(bundleName);
		return rb.getString(key);
		//return (String)resolveExpression("#{"+bundleName+"['" + key + "']}");
	}

	// from JSFUtils in Oracle ADF 11g Storefront Demo
	public static Object resolveExpression(String expression) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		Application app = facesContext.getApplication();
		ExpressionFactory elFactory = app.getExpressionFactory();
		ELContext elContext = facesContext.getELContext();
		ValueExpression valueExp =
				elFactory.createValueExpression(elContext, expression,
						Object.class);
		return valueExp.getValue(elContext);
	}

	public static String prettifyXml(String xml) {
		if (xml == null || "".equals(xml))
			return "";
		try {
			return PrettyPrintXMLUtils.prettyPrintWithTrAX(XMLUtils.getInstance().newDocument(xml.getBytes()));
		} catch (Exception e) {
			// non sono riuscito a formattare il messaggio
			log.error(e.getMessage(),e);
		}
		return xml;
	}

	public static String prettifyXml(byte[] xml) {
		if (xml == null)
			return "";
		String res = "";
		try {
			return PrettyPrintXMLUtils.prettyPrintWithTrAX(XMLUtils.getInstance().newDocument(xml));
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return res;
	} 

	/**
	 * Legge le proprieta' dal fileProperties passato come parametro
	 * 
	 * @param fileProperties
	 *            Il path del file properties
	 * @return
	 * @throws Exception
	 */
	public static Properties readProperties(String fileProperties) throws Exception {
		Properties prop = new Properties();
		InputStream inProp = Utils.class.getResourceAsStream(fileProperties);

		try {
			prop.load(inProp);

			return new Properties(prop);

		} catch (Exception e) {

			throw new Exception("Impossibile leggere il file di proprieta [" + fileProperties + "]", e);

		} finally {
			try {
				inProp.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Controlla che la pagina richiesta sia tra quelle che non necessitano di filtro sui contenuti,
	 * sono "libere" le pagine di login e timeout, e i path delle risorse richiesta dinamicamente dal framework 
	 *	
	 */
	public static boolean isContentAuthorizationRequiredForThisResource(HttpServletRequest httpServletRequest, List<String> excludedPaths) {
		String requestPath = httpServletRequest.getRequestURI();

		String contextPath = httpServletRequest.getContextPath(); // '/pddMonitor'

		// caso limite, pddMonitor/
		if(StringUtils.equals(requestPath, (contextPath + "/")))
			return false;

		boolean controlRequired = true;
		if(excludedPaths.size() > 0){
			for (String page : excludedPaths) {
				if(StringUtils.contains(requestPath, (contextPath + page))){
					controlRequired = false;
					break;
				}
			}
		}


		return controlRequired;
	}

}