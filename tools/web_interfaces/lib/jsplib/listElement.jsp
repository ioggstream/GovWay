<%--
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
--%>
<!DOCTYPE html>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page session="true" import="java.util.*, org.openspcoop2.web.lib.mvc.*" %>
<html>
<%
String iddati = request.getParameter("iddati");
String params = (String) request.getAttribute("params");

if (params == null)
	 params="";

ListElement listElement = 
	  (org.openspcoop2.web.lib.mvc.ListElement) session.getAttribute("ListElement");

String nomeServlet = listElement.getOggetto();
String nomeServletAdd = nomeServlet+"Add.do";
String nomeServletDel = nomeServlet+"Del.do";
String nomeServletList = nomeServlet+"List.do";
	  
String formatPar = listElement.formatParametersURL();

String gdString = "GeneralData";
String pdString = "PageData";
if (iddati != null && !iddati.equals("notdefined")) {
  gdString += iddati;
  pdString += iddati;
}
else {
  iddati = "notdefined";
}

GeneralData gd = (GeneralData) session.getAttribute(gdString);
PageData pd = (PageData) session.getAttribute(pdString);

Vector v = pd.getDati();
int n = v.size();
String search = request.getParameter("search");
if (search == null)
  search = "";

String customListViewName = pd.getCustomListViewName();
%>

<head>
<meta charset="UTF-8">
<jsp:include page="/jsplib/browserUtils.jsp" flush="true" />
<title><%= gd.getTitle() %></title>
<link rel="stylesheet" href="css/roboto/roboto-fontface.css" type="text/css">
<link rel="stylesheet" href="css/materialIcons/material-icons-fontface.css" type="text/css">
<link rel="stylesheet" href="css/<%= gd.getCss() %>" type="text/css">
<link rel="stylesheet" href="css/materialIcons.css" type="text/css">
<link rel="stylesheet" href="css/ui.core.css" type="text/css">
<link rel="stylesheet" href="css/ui.theme.css" type="text/css">
<link rel="stylesheet" href="css/ui.dialog.css" type="text/css">
<script type="text/javascript" src="js/webapps.js"></script>


<SCRIPT>
var nomeServletAdd_Custom = '<%= nomeServletAdd %>';
var nomeServletDel_Custom = '<%= nomeServletDel %>';
var nomeServletList_Custom = '<%= nomeServletList %>';
</SCRIPT>
<jsp:include page="/jsp/listElementCustom.jsp" flush="true" />
<SCRIPT type="text/javascript">
var iddati = '<%= iddati %>';
var params = '<%= params %>';
var nomeServletAdd = nomeServletAdd_Custom;
var nomeServletDel = nomeServletDel_Custom;
var nomeServletList = nomeServletList_Custom;
var formatPar = '<%= formatPar %>';
var n = <%= n %>;
var index = <%= pd.getIndex() %>;
var pageSize = <%= pd.getPageSize() %>;
var nr = 0;

function checkAll(){
	if(n > 0){
		var chkAll = $("#chkAll:checked").length;
		
		if(chkAll > 0) {
			SelectAll();
		} else {
			DeselectAll();
		}
	}
}

function SelectAll() {
  if (n > 1) {
    for (var c = 0; c < document.form.selectcheckbox.length; c++)
      document.form.selectcheckbox[c].checked = true;
  } else {
    document.form.selectcheckbox.checked = true;
  }
};

function DeselectAll() {
  if (n > 1) {
    for (var c = 0; c < document.form.selectcheckbox.length; c++)
      document.form.selectcheckbox[c].checked = false;
  } else {
    document.form.selectcheckbox.checked = false;
  }
};

function RemoveEntries() {
  if (nr != 0) {
    return false;
  }
  var elemToRemove = '';
  if (n > 1) {
    for (var j = 0; j < n; j++) {
      if (document.form.selectcheckbox[j].checked) {
        if (elemToRemove != '') {
          elemToRemove += ',';
        }
        //elemToRemove += j;
        elemToRemove +=document.form.selectcheckbox[j].value;
      }
    }
  } else {
    if (document.form.selectcheckbox.checked)
      elemToRemove += document.form.selectcheckbox.value;
  }
  if (elemToRemove != '') {
    nr = 1;
    if (formatPar != null && formatPar != "")
        document.location='<%= request.getContextPath() %>/'+nomeServletDel+'?'+formatPar+'&obj='+elemToRemove+'&iddati='+iddati+params;
      else
        document.location='<%= request.getContextPath() %>/'+nomeServletDel+'?obj='+elemToRemove+'&iddati='+iddati+params;
  }
};

function AddEntry() {
  if (nr != 0) {
    return false;
  }
  nr = 1;
  if (formatPar != null && formatPar != "")
 	  document.location='<%= request.getContextPath() %>/'+nomeServletAdd+'?'+formatPar+'&iddati='+iddati+params;
  else
	  document.location='<%= request.getContextPath() %>/'+nomeServletAdd+'?iddati='+iddati+params;
};

function CambiaVisualizzazione(newPageSize) {
  if (nr != 0) {
    return false;
  }
  nr = 1;
  if (newPageSize == '0') {
    index = 0; 
  }
  if (formatPar != null && formatPar != "")
    document.location='<%= request.getContextPath() %>/'+nomeServletList+'?'+formatPar+'&pageSize='+newPageSize+'&index='+index+'&iddati='+iddati+params;
  else
    document.location='<%= request.getContextPath() %>/'+nomeServletList+'?pageSize='+newPageSize+'&index='+index+'&iddati='+iddati+params;
};

function NextPage() {
  if (nr != 0) {
    return false;
  }
  nr = 1;
  index += pageSize;
  if (formatPar != null && formatPar != "")
    document.location='<%= request.getContextPath() %>/'+nomeServletList+'?'+formatPar+'&pageSize='+pageSize+'&index='+index+'&iddati='+iddati+params;
  else
    document.location='<%= request.getContextPath() %>/'+nomeServletList+'?pageSize='+pageSize+'&index='+index+'&iddati='+iddati+params;
};

function PrevPage(pageSize) {
  if (nr != 0) {
    return false;
  }
  nr = 1;
  index -= pageSize;
  if (index < 0) {
    index = 0;
  }
  if (formatPar != null && formatPar != "")
    document.location='<%= request.getContextPath() %>/'+nomeServletList+'?'+formatPar+'&pageSize='+pageSize+'&index='+index+'&iddati='+iddati+params;
  else
    document.location='<%= request.getContextPath() %>/'+nomeServletList+'?pageSize='+pageSize+'&index='+index+'&iddati='+iddati+params;
};

function Search(form) {
  if (nr != 0) {
    return false;
  }
  nr = 1;

  addHidden(form, 'index' , 0);
  addHidden(form, 'iddati' , iddati);
  addHidden(form, 'pageSize' , pageSize);

  // formatParams
  
   if (formatPar != null && formatPar != ""){
  	var pairs = ((formatPar[0] === '?' || formatPar[0] === '&') ? formatPar.substr(1) : formatPar).split('&');
  	for (var i = 0; i < pairs.length; i++) {
      	var pair = pairs[i].split('=');
      	addHidden(form, pair[0] , pair[1]);
  	}
   }
   if (params != null && params != ""){
	   var pairs = ((params[0] === '?' || params[0] === '&') ? params.substr(1) : params).split('&');
	   for (var i = 0; i < pairs.length; i++) {
	       var pair = pairs[i].split('=');
	       addHidden(form, pair[0] , pair[1]);
	   }
   }

  // imposto la destinazione
  document.form.action = nomeServletList;
      
  // form submit
  document.form.submit();
 
};

function Reset(form) {
	  if (nr != 0) {
	    return false;
	  }
	  nr = 1;
	  
	  document.form.reset();
 	  for (var k=0; k<document.form.elements.length; k++) {
		var name = document.form.elements[k].name;
		if (name == "search"){
			document.form.elements[k].value="";
		}
		var tipo = document.form.elements[k].type;
		if (tipo == "select-one" || tipo == "select-multiple") {
			document.form.elements[k].selectedIndex = 0;
		}
	  }

	  addHidden(form, 'index' , 0);
	  addHidden(form, 'iddati' , iddati);
	  addHidden(form, 'pageSize' , pageSize);

	  // formatParams
	  
	   if (formatPar != null && formatPar != ""){
	  	var pairs = ((formatPar[0] === '?' || formatPar[0] === '&') ? formatPar.substr(1) : formatPar).split('&');
	  	for (var i = 0; i < pairs.length; i++) {
	      	var pair = pairs[i].split('=');
	      	addHidden(form, pair[0] , pair[1]);
	  	}
	   }
	   if (params != null && params != ""){
		   var pairs = ((params[0] === '?' || params[0] === '&') ? params.substr(1) : params).split('&');
		   for (var i = 0; i < pairs.length; i++) {
		       var pair = pairs[i].split('=');
		       addHidden(form, pair[0] , pair[1]);
		   }
	  }
	  
	   // imposto la destinazione
	   document.form.action = nomeServletList;
	   
	  // form submit
	  document.form.submit();
	 
	};

function Export(url){
	document.location='<%= request.getContextPath() %>/'+url
};

function Esporta(tipo) {

	 var elemToExport = '';
	  if (n > 1) {
	    for (var j = 0; j < n; j++) {
	      if (document.form.selectcheckbox[j].checked) {
	        if (elemToExport != '') {
	        	elemToExport += ',';
	        }
	        //elemToRemove += j;
	        elemToExport +=document.form.selectcheckbox[j].value;
	      }
	    }
	  } else {
	    if (document.form.selectcheckbox.checked)
	    	elemToExport +=document.form.selectcheckbox.value;
	  }


	if(elemToExport !== '')
		document.location = "<%= request.getContextPath() %>/export.do?tipoExport="+tipo+"&obj="+elemToExport;  
		  
};

function Change(form,dataElementName) {
    
    //aggiungo parametro per indicare che si tratta di postback e azzero idhid
    addHidden(form, 'isPostBack' , true);
    if(dataElementName!=null)
    	addHidden(document.form, 'postBackElementName' , dataElementName);
    addHidden(form, 'index' , 0);
    addHidden(form, 'iddati' , iddati);
  
    // formatParams
    
     if (formatPar != null && formatPar != ""){
    	var pairs = ((formatPar[0] === '?' || formatPar[0] === '&') ? formatPar.substr(1) : formatPar).split('&');
    	for (var i = 0; i < pairs.length; i++) {
        	var pair = pairs[i].split('=');
        	addHidden(form, pair[0] , pair[1]);
    	}
     }
     if (params != null && params != ""){
	   var pairs = ((params[0] === '?' || params[0] === '&') ? params.substr(1) : params).split('&');
	   for (var i = 0; i < pairs.length; i++) {
	       var pair = pairs[i].split('=');
	       addHidden(form, pair[0] , pair[1]);
	   }
     }
        
    // form submit
    document.form.submit();
}

function addHidden(theForm, name, value) {
    // Create a hidden input element, and append it to the form:
    var input = document.createElement('input');
    input.type = 'hidden';
    input.name = name;
    input.value = value;
    theForm.appendChild(input);
}

var panelListaRicercaOpen = false; // controlla l'aperture del pannello di ricerca.
<%
if ( 
	(
		pd.getSearch().equals("on") || 
		(pd.getSearch().equals("auto") && (pd.getNumEntries() > pd.getSearchNumEntries()))
	) || 
	(
		pd.getFilterNames() != null &&
		pd.getFilterValues().size()>0
	)
) {

	String searchDescription = pd.getSearchDescription();
	if (!searchDescription.equals("") || (pd.getFilterNames() != null && pd.hasAlmostOneFilterDefined())){
	%>	panelListaRicercaOpen = true; <% 
	} 
}%>

</SCRIPT>

<!-- JQuery lib-->
<script type="text/javascript" src="js/jquery-latest.js"></script>
<!--Funzioni di utilita -->
<script type="text/javascript" src="js/ui.core.js"></script>
<script type="text/javascript" src="js/ui.dialog.js"></script>
<script type="text/javascript">
function togglePanelListaRicerca(panelListaRicercaOpen){
	if(panelListaRicercaOpen) {
    	$("#searchForm").removeClass('searchFormOff');
    	$("#searchForm").addClass('searchFormOn');
    	
//     	$("#iconaPanelLista").removeClass('icon-down-white');
//     	$("#iconaPanelLista").addClass('icon-up-white');
    } else {
    	$("#searchForm").removeClass('searchFormOn');
    	$("#searchForm").addClass('searchFormOff');
    	
//     	$("#iconaPanelLista").removeClass('icon-up-white');
//     	$("#iconaPanelLista").addClass('icon-down-white');
    }
}

	$(document).ready(function(){
		// pannello ricerca che si apre e chiude iconaPanelLista
		$("#panelListaRicercaHeader").click(function(){
			panelListaRicercaOpen = !panelListaRicercaOpen;
			togglePanelListaRicerca(panelListaRicercaOpen);
		});
		
		togglePanelListaRicerca(panelListaRicercaOpen);
		
		$("tr[class='even']").hover(function() {
		    $(this).addClass('active');
		}, function() {
		    $(this).removeClass('active');
		});
		
		$("tr[class='odd']").hover(function() {
		    $(this).addClass('active');
		}, function() {
		    $(this).removeClass('active');
		});
	 });
</script>
<script type="text/javascript" src="js/utils.js"></script>
<jsp:include page="/jsplib/menuUtente.jsp" flush="true" />
<link rel="shortcut icon" href="images/favicon.ico" type="image/x-icon" />
</head>
<body marginwidth=0 marginheight=0 onLoad="focusText(document.form);">
	<table class="bodyWrapper">
		<tbody>
			<jsp:include page="/jsplib/templateHeader.jsp" flush="true" />
			<tr class="trPageBody">
				<jsp:include page="/jsplib/menu.jsp" flush="true" />
				<% if(customListViewName == null || "".equals(customListViewName)){ %>
					<jsp:include page="/jsplib/full-list.jsp" flush="true" />
				<% } else {%>	
					<jsp:include page="/jsplib/full-list-noTable.jsp" flush="true" />
				<% } %>
			</tr>
			<jsp:include page="/jsplib/templateFooter.jsp" flush="true" />
		</tbody>
	</table>
	<div id="confermaEliminazioneModal" title="Conferma Operazione">
 		<p class="contenutoEliminazioneModal">
 			<img src="images/tema_link/alert_orange.png"/>
 			<span>Eliminare gli elementi selezionati?</span>
 		</p>
	</div>
<jsp:include page="/jsplib/conferma.jsp" flush="true" />
</body>
</html>
