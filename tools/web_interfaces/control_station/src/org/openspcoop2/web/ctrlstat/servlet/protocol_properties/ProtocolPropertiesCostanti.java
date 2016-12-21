package org.openspcoop2.web.ctrlstat.servlet.protocol_properties;

import java.util.Vector;

import org.openspcoop2.web.lib.mvc.ForwardParams;

public class ProtocolPropertiesCostanti {

	public final static String OBJECT_NAME_PP = "protocolProperty";
	
	public final static ForwardParams TIPO_OPERAZIONE_BINARY_PROPERTY_CHANGE = ForwardParams.OTHER("BinaryPropertyChange");
	public static final String SERVLET_NAME_BINARY_PROPERTY_CHANGE = OBJECT_NAME_PP+"BinaryPropertyChange.do";
	
	public final static Vector<String> SERVLET_PP = new Vector<String>();
	static{
		SERVLET_PP.add(SERVLET_NAME_BINARY_PROPERTY_CHANGE);
	}
	
	/* PARAMETRI */
	
	public static final String PARAMETRO_PP_ID = "id";
	public static final String PARAMETRO_PP_NOME = "nome";
	public static final String PARAMETRO_PP_ID_PROPRIETARIO = "idProprietario";
	public static final String PARAMETRO_PP_TIPO_PROPRIETARIO = "tipoProprietario";
	public static final String PARAMETRO_PP_NOME_PROPRIETARIO = "nomeProprietario";
	public static final String PARAMETRO_PP_NOME_PARENT_PROPRIETARIO = "nomeParentProprietario";
	public static final String PARAMETRO_PP_URL_ORIGINALE_CHANGE = "urlOrigChange";
	public static final String PARAMETRO_PP_SET = "ppSet";
	public final static String PARAMETRO_PP_CONTENUTO_DOCUMENTO = "contenutoDocumento";
	public final static String PARAMETRO_PP_CONTENUTO_DOCUMENTO_WARN = "contenutoDocumentoWarn";
	public final static String PARAMETRO_PP_PROTOCOLLO = "protocollo";
	public static final String PARAMETRO_PP_TIPO_ACCORDO = "tipoAccordo";
	public final static String PARAMETRO_PP_ID_ALLEGATO = "idAllegato";
	
	/* LABEL PARAMETRI */
	
	public final static String LABEL_PARAMETRO_PP_ID = "Id Property";
	public final static String LABEL_PARAMETRO_PP_ID_PROPRIETARIO = "Id Proprietario";
	public final static String LABEL_PARAMETRO_PP_TIPO_PROPRIETARIO = "Tipo Proprietario";
	public final static String LABEL_GESTIONE_DOCUMENTO = "Gestione Documento";
	public final static String LABEL_GESTIONE = "Gestione ";

	public final static String LABEL_SOGGETTO = "Soggetto";
	public final static String LABEL_APC = "Accordo Parte Comune";
	public final static String LABEL_ASC = "Servizio Composto";
	public final static String LABEL_APS = "Accordo Parte Specifica";
	public final static String LABEL_PORT_TYPE= "Port Type";
	public final static String LABEL_AZIONE = "Azione";
	public final static String LABEL_AZIONE_ACCORDO = "Azione Accordo";
	public final static String LABEL_SERVIZIO = "Servizio";
	public final static String LABEL_AC = "Accordo Cooperazione";
	public final static String LABEL_OPERATION = "Operazione";
	public final static String LABEL_FRUITORE = "Fruitore";
	public final static String LABEL_DOCUMENTO_ATTUALE = "Attuale";
	public final static String LABEL_DOWNLOAD = "Download";
	public final static String LABEL_DOCUMENTO_AGGIORNAMENTO = "Aggiorna Documento";
	public final static String LABEL_AGGIORNAMENTO = "Aggiorna ";
	public final static String LABEL_DOCUMENTO_NOT_FOUND = "non fornito";
	public final static String LABEL_DOCUMENTO_NUOVO = "Nuovo Documento";
	public final static String LABEL_CONTENUTO_NUOVO = "Nuovo Contenuto";
	public final static String LABEL_DOCUMENTO_CHANGE_CLEAR_WARNING = "Warning: ";
	public final static String LABEL_DOCUMENTO_CHANGE_CLEAR = "Se si desidera eliminare un documento precedentemente caricato cliccare su 'Invia' senza selezionare alcun file"; //fornirne un'altra versione";

	/* VALORI PARAMETRI */
	public static final String PARAMETRO_PP_TIPO_PROPRIETARIO_VALUE_SOGGETTO = "SOGGETTO";
	public static final String PARAMETRO_PP_TIPO_PROPRIETARIO_VALUE_ACCORDO_COOPERAZIONE = "ACCORDO_COOPERAZIONE";
	public static final String PARAMETRO_PP_TIPO_PROPRIETARIO_VALUE_ACCORDO_SERVIZIO_PARTE_COMUNE = "ACCORDO_SERVIZIO_PARTE_COMUNE";
	public static final String PARAMETRO_PP_TIPO_PROPRIETARIO_VALUE_ACCORDO_SERVIZIO_COMPOSTO = "ACCORDO_SERVIZIO_COMPOSTO";
	public static final String PARAMETRO_PP_TIPO_PROPRIETARIO_VALUE_PORT_TYPE = "PORT_TYPE";
	public static final String PARAMETRO_PP_TIPO_PROPRIETARIO_VALUE_OPERATION = "OPERATION";
	public static final String PARAMETRO_PP_TIPO_PROPRIETARIO_VALUE_AZIONE_ACCORDO = "AZIONE_ACCORDO";
	public static final String PARAMETRO_PP_TIPO_PROPRIETARIO_VALUE_ACCORDO_SERVIZIO_PARTE_SPECIFICA = "ACCORDO_SERVIZIO_PARTE_SPECIFICA";
	public static final String PARAMETRO_PP_TIPO_PROPRIETARIO_VALUE_FRUITORE = "FRUITORE";
	
	public final static String PARAMETRO_VALORE_PP_TIPO_ACCORDO_PARTE_COMUNE = "apc";
	public final static String PARAMETRO_VALORE_PP_TIPO_ACCORDO_SERVIZIO_COMPOSTO = "asc";
 
}