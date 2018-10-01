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




package org.openspcoop2.web.lib.mvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;



/**
 * DataElement
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class DataElement {
	
	private static Map<String, String> escapeMap = null;
	
	private static int DATA_ELEMENT_SIZE = 50;
	private static int DATA_ELEMENT_COLS = 15;
	private static int DATA_ELEMENT_ROWS = 5;
	public static void initialize(DataElementParameter p){
		if(p.getSize()!= null && p.getSize().intValue() > 0) {
			DataElement.DATA_ELEMENT_SIZE = p.getSize();
		}
		
		if(p.getCols()!= null && p.getCols().intValue() > 0) {
			DataElement.DATA_ELEMENT_COLS = p.getCols();
		}
		
		if(p.getRows()!= null && p.getRows().intValue() > 0) {
			DataElement.DATA_ELEMENT_ROWS = p.getRows();
		}
	}
	
	
	static{
		DataElement.escapeMap = new HashMap<String,String>();
		
		// carico le stringhe da sostituire
		DataElement.escapeMap.put("&lt;BR&gt;", "<BR>");
		DataElement.escapeMap.put("&lt;BR/&gt;", "<BR/>");
		DataElement.escapeMap.put("&lt;/BR&gt;", "</BR>");
		DataElement.escapeMap.put("&lt;br&gt;", "<br>");
		DataElement.escapeMap.put("&lt;br/&gt;", "<br/>");
		DataElement.escapeMap.put("&lt;/br&gt;", "</br>");
	}

	String label, labelRight, value, type, name, onClick, onChange, selected;
	String [] values = null;
	String [] labels = null;
	int size, cols, rows, id;
	boolean affiancato; // serve a gestire il successivo elemento se disegnarlo accanto o in verticale (default)
	boolean labelAffiancata=true; // indica se la label e poi l'elemento sono disegnati uno accanto all'altro in orizzontale (default) oppure in verticale (default per le text-area)
	String idToRemove;
	boolean required=false;
	boolean bold=false;
	boolean postBack=false;
	
	String note = null;
	String styleClass = null;
	String labelStyleClass = null;
	
	String [] selezionati = null; // serve per gestire i valori selezionati in una multiselect
	
	private String style = null;
	private String width = null;
	
	private Integer minValue = null, maxValue= null;
	
	private List<String> icon, url,toolTip, target = null;

	public String getIdToRemove() {
		return this.idToRemove;
	}

	public void setIdToRemove(String idToRemove) {
		this.idToRemove = idToRemove;
	}

	public DataElement() {
		//int id = -1;
		this.label = "";
		this.value = "";
		this.type = "text";
		this.url = new ArrayList<>();
		this.target = new ArrayList<>();
		this.name = "";
		this.onClick = "";
		this.onChange = "";
		this.selected = "";
		this.toolTip = new ArrayList<>();
		this.icon = new ArrayList<>();
		this.size = DataElement.DATA_ELEMENT_SIZE;
		this.cols = DataElement.DATA_ELEMENT_COLS;
		this.rows = DataElement.DATA_ELEMENT_ROWS;
		this.affiancato = false;
		this.labelAffiancata = true;
		this.note = "";
		this.styleClass = Costanti.INPUT_LONG_CSS_CLASS;
		this.labelStyleClass = null;
		this.labelRight = null;
		
	}

	public void setId(int i) {
		this.id = i;
	}
	public int getId() {
		return this.id;
	}

	public void setLabel(String s) {
		this.label = s;
	}
	public String getLabel() {
		return this.getLabel(true);
	}
	public String getLabel(boolean elementsRequiredEnabled) {
		StringBuffer bf = new StringBuffer();
		if(this.bold){
			bf.append("<B>");
		}
		
		bf.append(DataElement.getEscapedValue(DataElement.checkNull(this.label)));
		if(elementsRequiredEnabled && this.required){
			//	bf.append(" (*)");
			bf.append(" <em>*</em>");
		}
		if(this.bold){
			bf.append("</B>");
		}
		return bf.toString();
	}

	public void setValue(String s) {
		this.value = s;
	}
	public String getValue() {
		return DataElement.checkNull(this.value);
	}

	public void setType(DataElementType s) {
		this.setType(s.toString());
	}
	public void setType(String s) {
		this.type = s;
		/*if("hidden".equals(this.type)){
			this.required = false;
			this.bold = false;
		}
		if("text".equals(this.type)){
			this.required = false;
		}*/
		if(DataElementType.TEXT_AREA.toString().equals(s) || DataElementType.TEXT_AREA_NO_EDIT.toString().equals(s)){
			this.setLabelAffiancata(false);
		}
	}
	public String getType() {
		return DataElement.checkNull(this.type);
	}
	
	public boolean isRequired() {
		return this.required;
	}

	public void setRequired(boolean required) {
		this.required = required;
		/*if(!"hidden".equals(this.type)){
			this.bold = required;
			if(!"text".equals(this.type)){
				this.required = required;
			}
		}*/
	}

	public void setUrl(String s) {
		this.url.clear();
		this.url.add(s);
	}
	public void addUrl(String s) {
		this.url.add(s);
	}
	public void setUrl(String servletName,Parameter ... parameter) {
		String urValue = _getUrlValue(servletName, parameter);
		this.url.clear();
		this.url.add(urValue);
		
	}

	private String _getUrlValue(String servletName, Parameter... parameter) {
		StringBuilder sb = new StringBuilder();
		sb.append(servletName);
		if(parameter!=null && parameter.length>0){
			sb.append("?");
			for (int i = 0; i < parameter.length; i++) {
				if(i>0){
					sb.append("&");
				}
				sb.append(parameter[i].toString());
			}
		}
		
		String urValue = sb.toString();
		return urValue;
	}
	
	public void addUrl(String servletName,Parameter ... parameter) {
		String urValue = _getUrlValue(servletName, parameter);
		this.url.add(urValue);
	}
	public String getUrl() {
		if(this.url.isEmpty())
			return "";
		return this.url.get(0);
	}
	
	public List<String> getListaUrl() {
		return this.url;
	}

	public void setTarget(TargetType s) {
		this.target.clear();
		this.target.add(s.toString());
	}
	public void addTarget(TargetType s) {
		this.target.add(s.toString());
	}
	public String getTarget() {
		if(this.target.isEmpty())
			return "";
		return this.target.get(0);
	}
	
	public List<String> getListaTarget() {
		return this.target;
	}

	public void setName(String s) {
		this.name = s;
	}
	public String getName() {
		return DataElement.checkNull(this.name);
	}

	public void setOnClick(String s) {
		this.onClick = s;
	}
	public String getOnClick() {
		return DataElement.checkNull(this.onClick);
	}

	@Deprecated
	public void setOnChange(String s) {
		this.onChange = s;
	}
	public void setOnChangeAlternativePostBack(String s) {
		this.onChange = s;
	}
	public String getOnChange() {
		return DataElement.checkNull(this.onChange);
	}

	public void setSelected(String s) {
		this.selected = s;
	}
	public void setSelectedAsNull() {
		this.selected = null;
	}
	public void setSelected(boolean isFlag) {
		if(isFlag)
			this.selected = Costanti.CHECK_BOX_ENABLED;
		else
			this.selected = Costanti.CHECK_BOX_DISABLED;
	}
	public void setSelected(CheckboxStatusType status) {
		switch (status) {
		case ABILITATO:
			this.selected = Costanti.CHECK_BOX_ENABLED;
			break;
		case DISABILITATO:
			this.selected = Costanti.CHECK_BOX_DISABLED;
			break;
		case WARNING_ONLY:
			this.selected = Costanti.CHECK_BOX_WARN;
			break;
		}
	}
	public String getSelected() {
		return DataElement.checkNull(this.selected);
	}

	public void setSize(int i) {
		this.size = i;
	}
	public int getSize() {
		return this.size;
	}

	public void setCols(int i) {
		this.cols = i;
	}
	public int getCols() {
		return this.cols;
	}

	public void setRows(int i) {
		this.rows = i;
	}
	public int getRows() {
		return this.rows;
	}

	public void setAffiancato(boolean b) {
		this.affiancato = b;
	}
	public boolean getAffiancato() {
		return this.affiancato;
	}

	public void setValues(String [] s) {
		this.values = s;
	}
	public void setValues(List<String> s) {
		if(s==null || s.size()<=0){
			return;
		}
		this.setValues(s.toArray(new String[1]));
	}
	public String[] getValues() {
		return this.values;
	}

	public void setLabels(String [] s) {
		if( s != null && s.length > 0){
			this.labels = new String[ s.length];
			for (int i = 0; i < s.length; i++) {
				this.labels[i] = DataElement.getEscapedValue( s[i]);
			}
		}else {
			this.labels = s;
		}
	}
	public void setLabels(List<String> s) {
		if(s==null || s.size()<=0){
			return;
		}
		this.setLabels(s.toArray(new String[1]));
	}
	public String[] getLabels() {
		return this.labels;
	}
	
	private static String checkNull(String toCheck)
	{
		return (toCheck==null ? "" : toCheck);
	}

	public String getToolTip() {
		if(this.toolTip.isEmpty())
			return "";
		return this.toolTip.get(0);
	}
	public List<String> getListaToolTip() {
		return this.toolTip;
	}
	/**
	 * Il tooltip da visualizzare quando si passa con il mouse
	 * su di un link, se il tooltip non e' impostato e il valore del campo value
	 * e' &gt; size allora il link viene troncato e viene 
	 * impostato come tooltip il valore originale di value
	 * @param toolTip
	 */
	public void setToolTip(String toolTip) {
		this.toolTip.clear();
		this.toolTip.add(toolTip);
	}
	public void addToolTip(String s) {
		this.toolTip.add(s);
	}
	
	public boolean isBold() {
		return this.bold;
	}

	public void setBold(boolean bold) {
		this.bold = bold;
	}
	
	public boolean isLabelAffiancata() {
		return this.labelAffiancata;
	}

	public void setLabelAffiancata(boolean labelAffiancata) {
		this.labelAffiancata = labelAffiancata;
	}
	
	public boolean isPostBack() {
		return this.postBack;
	}
	public void setPostBack(boolean postBack) {
		this.setPostBack(postBack, true);
	}
	public void setPostBack(boolean postBack,boolean setElementName) {
		this.setPostBack(postBack, setElementName, false);
	}
	public void setPostBack_viaPOST(boolean postBack) {
		this.setPostBack(postBack, true, true); // obbligatorio il nome
	}
	private void setPostBack(boolean postBack,boolean setElementName, boolean viaPOST) {
		this.postBack = postBack;
		if (this.postBack) {
			if(setElementName){
				if(this.name==null || "".equals(this.name)){
					throw new RuntimeException("Per poter impostare il nome dell'element che scaturira' il postBack deve prima essere indicato tramite il metodo setName");
				}
				String prefix = "";
				if(viaPOST) {
					prefix=Costanti.POSTBACK_VIA_POST_FUNCTION_PREFIX;
				}
				prefix+=Costanti.POSTBACK_FUNCTION_WITH_PARAMETER_START;
				this.setOnClick(prefix+this.name+Costanti.POSTBACK_FUNCTION_WITH_PARAMETER_END);
				this.setOnChange(prefix+this.name+Costanti.POSTBACK_FUNCTION_WITH_PARAMETER_END);
			}
			else{
				this.setOnClick(Costanti.POSTBACK_FUNCTION);
				this.setOnChange(Costanti.POSTBACK_FUNCTION);
			}
		}
		else{
			this.setOnClick(null);
			this.setOnChange(null);
		}
	}
	
	public static String getEscapedValue(String value){
		String escaped = StringEscapeUtils.escapeHtml(StringEscapeUtils.unescapeHtml(DataElement.checkNull(value)));
		
		// ripristino evenutali caratteri html
		for (String key : DataElement.escapeMap.keySet()) {
			if(escaped.contains(key)){
				escaped = escaped.replaceAll(key, DataElement.escapeMap.get(key));
			}
		}
		
		return escaped;
	}

	public String getNote() {
		return DataElement.checkNull(this.note);
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getStyleClass() {
		return DataElement.checkNull(this.styleClass);
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}
	
	public void setSelezionati(String [] s) {
			this.selezionati = s;
	}
	
	public void setSelezionati(List<String> s) {
		if(s==null || s.size()<=0){
			return;
		}
		this.setSelezionati(s.toArray(new String[1]));
	}
	public String[] getSelezionati() {
		return this.selezionati;
	}
	
	public String getSelezionatiAsString() {
		if(this.selezionati ==null || this.selezionati.length <=0){
			return "";
		}
		
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < this.selezionati.length; i++) {
			if(sb.length() > 0)
				sb.append(", ");
			
			sb.append(DataElement.checkNull(this.selezionati[i]));
		}
		
		return sb.toString();
	}
	
	public boolean isSelected(String value) {
		if(this.selezionati ==null || this.selezionati.length <=0){
			return false;
		}
		
		for (int i = 0; i < this.selezionati.length; i++) {
			if(value.equals(DataElement.checkNull(this.selezionati[i])))
				return true;
		}
		
		return false;
	}

	public Integer getMinValue() {
		return this.minValue;
	}

	public void setMinValue(Integer minValue) {
		this.minValue = minValue;
	}

	public Integer getMaxValue() {
		return this.maxValue;
	}

	public void setMaxValue(Integer maxValue) {
		this.maxValue = maxValue;
	}

	public String getStyle() {
		String width = this.getWidth();
		
		if(StringUtils.isNotEmpty(this.style)) {
			if(width != null)
				return DataElement.checkNull(this.style) + " " + width;
		} else {
			if(width != null)
				return width;
		}
			
		return DataElement.checkNull(this.style);
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getWidth() {
		if(StringUtils.isNotEmpty(this.width)) {
			return " width: " + this.width + ";";
		} else 
			return null;
	}

	/**
	 * Imposta la larghezza dell'elemento
	 * Nota: Indicare solo il valore, il nome proprieta': "width" viene generato automaticamente.
	 * Valori ammessi (Decrizione):
	 * 
	 * auto	(Default value. The browser calculates the width)
	 * length	(Defines the width in px, cm, etc.) 
	 * %	(Defines the width in percent of the containing block)
	 * initial	(Sets this property to its default value.)
	 * inherit	(Inherits this property from its parent element.)
	 * 
	 * @param width
	 */
	public void setWidth(String width) {
		this.width = width;
	}
	
	public void setWidthPx(int width) {
		this.setWidth(width + "px");
	}
	public void setWidthCm(int width) {
		this.setWidth(width + "cm");
	}
	public void setWidthAuto() {
		this.setWidth("auto");
	}
	public void setWidthPercentuale(int width) {
		this.setWidth(width + "%");
	}

	public String getLabelStyleClass() {
		return DataElement.checkNull(this.labelStyleClass);
	}

	public void setLabelStyleClass(String labelStyleClass) {
		this.labelStyleClass = labelStyleClass;
	}
	
	public String getLabelRight() {
		return DataElement.checkNull(this.labelRight);
	}

	public void setLabelRight(String labelRight) {
		this.labelRight = labelRight;
	}

	public String getIcon() {
		if(this.icon.isEmpty())
			return "";
		return this.icon.get(0);
	}
	public List<String> getListaIcon() {
		return this.icon;
	}

	public void setIcon(String icon) {
		this.icon.clear();
		this.icon.add(icon);
	}
	
	public void addIcon(String icon) {
		this.icon.add(icon);
	}
}
