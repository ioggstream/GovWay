package org.openspcoop2.web.monitor.core.utils;

//import java.awt.Canvas;
import java.awt.Font;
//import java.awt.FontMetrics;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.search.AccordoServizioParteComune;
import org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica;
import org.openspcoop2.core.commons.search.PortaDelegata;
import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.core.commons.search.constants.TipoPdD;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.web.monitor.core.bean.UserDetailsBean;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.core.PermessiUtenteOperatore;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.dao.DynamicUtilsService;
import org.openspcoop2.web.monitor.core.dao.IDynamicUtilsService;
import org.slf4j.Logger;

public class DynamicPdDBeanUtils implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 

	private transient Logger log = null;

	private transient IDynamicUtilsService dynamicUtilsService = null;

	private static DynamicPdDBeanUtils instance = null;

	public static Integer maxSelectItemsWidth = 700;

	public static Integer defaultSelectItemsWidth = 412;

	//	private transient FontMetrics fm = null;
	private transient AffineTransform affineTransform = null;
	private transient FontRenderContext fontRenderContext = null;

	private transient Font defaultFont = null;

	public static DynamicPdDBeanUtils getInstance(Logger log) throws Exception{
		if(DynamicPdDBeanUtils.instance == null)
			init(log);

		return DynamicPdDBeanUtils.instance;
	}

	public static synchronized void init(Logger log) throws Exception{
		if(DynamicPdDBeanUtils.instance == null)
			DynamicPdDBeanUtils.instance = new DynamicPdDBeanUtils(log);
	}

	public DynamicPdDBeanUtils(Logger log) throws Exception{
		this.log = log;
		try{
			this.log.debug("Init Dynamic Utils in corso...");
			this.dynamicUtilsService = new DynamicUtilsService();
			String fontName = PddMonitorProperties.getInstance(log).getConsoleFontFamilyName();
			int fontStyle = PddMonitorProperties.getInstance(log).getConsoleFontStyle();
			this.defaultFont = new Font(fontName,fontStyle, 12);
			this.log.debug("Init Dynamic Utils in completato.");
		}catch(Exception e){
			this.log.error("Si e' verificato un errore durante la init: " + e.getMessage(),e);
		}
	}

	public boolean isTipoSoggettoCompatibile(String tipo1, String tipo2)  throws Exception{

		// se uno dei due non e' impostato allora sono compatibili
		if(StringUtils.isBlank(tipo1) || StringUtils.isBlank(tipo2))
			return true;

		List<String> tipiDisponibili1 = new ArrayList<String>();
		List<String> tipiDisponibili2 = new ArrayList<String>();

		if(!tipo1.equals("*") ){
			IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByOrganizationType(tipo1);
			tipiDisponibili1.add(protocolFactory.getProtocol());
		}

		if(!tipo2.equals("*")){
			IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByOrganizationType(tipo2);
			tipiDisponibili2.add(protocolFactory.getProtocol());
		}

		Map<String, String> mappaTipi = new HashMap<String, String>();
		if(tipiDisponibili1 != null && tipiDisponibili1.size() > 0)
			for (String tipo : tipiDisponibili1) {
				if(!mappaTipi.containsKey(tipo))
					mappaTipi.put(tipo, tipo);
			}

		if(tipiDisponibili2 != null && tipiDisponibili2.size() > 0)
			for (String tipo : tipiDisponibili2) {
				if(!mappaTipi.containsKey(tipo))
					mappaTipi.put(tipo, tipo);
			}

		// controllo di validita  i tipi dei soggetti che gestisce il protocollo devono essere gli stessi
		if(tipiDisponibili1.size() > 0 && tipiDisponibili1.size() != mappaTipi.keySet().size())
			return false;

		if(tipiDisponibili2.size() > 0 && tipiDisponibili2.size() != mappaTipi.keySet().size())
			return false;


		return true;
	}

	public boolean isTipoSoggettoCompatibileConProtocollo(String tipoSoggetto, String tipoProtocollo)  throws Exception{

		// se uno dei due non e' impostato allora sono compatibili
		if(StringUtils.isBlank(tipoSoggetto) || StringUtils.isBlank(tipoProtocollo))
			return true;

		if(!tipoSoggetto.equals("*") ){
			IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByOrganizationType(tipoSoggetto);

			if(protocolFactory == null || !protocolFactory.getProtocol().equals(tipoProtocollo))
				return false;
		}

		return true;
	}

	public boolean isTipoServizioCompatibileConProtocollo(String tipoServizio, String tipoProtocollo)  throws Exception{

		// se uno dei due non e' impostato allora sono compatibili
		if(StringUtils.isBlank(tipoServizio) || StringUtils.isBlank(tipoProtocollo))
			return true;

		if(!tipoServizio.equals("*") ){
			IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByServiceType(tipoServizio);

			if(protocolFactory == null || !protocolFactory.getProtocol().equals(tipoProtocollo))
				return false;
		}

		return true;
	}


	public boolean isAccordoCompatibileConProtocollo(String tipoErogatore, String nomeErogatore,String accordo, String tipoProtocollo)  throws Exception{

		// se uno dei due non e' impostato allora sono compatibili
		if(StringUtils.isBlank(accordo) || StringUtils.isBlank(tipoProtocollo))
			return true;

		List<String> tipiDisponibili1 = new ArrayList<String>();

		if(!accordo.equals("*") ){
			List<AccordoServizioParteSpecifica> servizi2 = this.dynamicUtilsService.getServizi(tipoProtocollo,accordo, tipoErogatore, nomeErogatore);

			if(servizi2 != null && servizi2.size() > 0){
				for (AccordoServizioParteSpecifica accordoServizioParteSpecifica : servizi2) {
					String tipo = accordoServizioParteSpecifica.getIdErogatore() != null ? accordoServizioParteSpecifica.getIdErogatore().getTipo() : null;
					if(tipo != null){
						IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByOrganizationType(tipo);

						if(!tipiDisponibili1 .contains(protocolFactory.getProtocol()))
							tipiDisponibili1.add(protocolFactory.getProtocol());
					}
				}
			}
		}

		if(!tipoProtocollo.equals("*")){
			if(tipiDisponibili1.size() > 0 && !tipiDisponibili1 .contains(tipoProtocollo))
				return false;
		}


		return true;
	}

	public List<Object> findElencoServiziApplicativiFromSoggettoErogatore(String tipoProtocollo,String tipoSoggetto,String nomeSoggetto){
		List<Object> list = new ArrayList<Object>();
		Soggetto erogatore = this.dynamicUtilsService.findSoggettoByTipoNome(tipoSoggetto, nomeSoggetto);

		if(erogatore != null)
			list = this.dynamicUtilsService.findElencoServiziApplicativi(tipoProtocollo,erogatore);


		return list;
	}

	public List<SelectItem> getListaSelectItemsServiziApplicativiFromSoggettoErogatore(String tipoProtocollo,String tipoSoggetto,String nomeSoggetto){
		List<SelectItem> sa = new ArrayList<SelectItem>();

		List<Object> list = this.findElencoServiziApplicativiFromSoggettoErogatore(tipoProtocollo,tipoSoggetto, nomeSoggetto);

		for (Object res : list) {
			sa.add(new SelectItem((String) res));
		}

		return sa;
	}



	public List<SelectItem> getSoggetti() {
		List<SelectItem> soggetti = new ArrayList<SelectItem>();
		String tipoProtocollo = null;
		String idPorta = null;
		List<Soggetto> list = this.dynamicUtilsService.findElencoSoggetti(tipoProtocollo ,idPorta);

		for (Soggetto soggetto : list) {
			soggetti.add(new SelectItem(soggetto.getTipoSoggetto() + "/" + soggetto.getNomeSoggetto()));
		}
		return soggetti;
	}

	public List<SelectItem> getIdPorte() {
		List<SelectItem> idPorte = new ArrayList<SelectItem>();

		String tipoProtocollo = null;
		String idPorta = null;
		List<Soggetto> list = this.dynamicUtilsService.findElencoSoggetti(tipoProtocollo ,idPorta);

		List<String> checkContains = new ArrayList<String>();
		for (Soggetto soggetto : list) {
			if(checkContains.contains(soggetto.getIdentificativoPorta())==false){
				idPorte.add(new SelectItem(soggetto.getIdentificativoPorta()));
				checkContains.add(soggetto.getIdentificativoPorta());
			}
		}
		return idPorte;
	}

	public String getIdPortaFromSoggetto(String tipoSoggetto,String nomeSoggetto) {
		String idPorta =null;
		Soggetto soggetto = this.dynamicUtilsService.findSoggettoByTipoNome(tipoSoggetto, nomeSoggetto);

		if(soggetto != null)
			idPorta = soggetto.getIdentificativoPorta();

		return idPorta;
	}

	public List<SelectItem> getSoggettiFromIdPorta(String idPorta) {
		List<SelectItem> soggetti = new ArrayList<SelectItem>();
		String tipoProtocollo = null;
		List<Soggetto> list = this.dynamicUtilsService.findElencoSoggetti(tipoProtocollo ,idPorta);

		for (Soggetto soggetto : list) {
			soggetti.add(new SelectItem(soggetto.getTipoSoggetto() + "/" + soggetto.getNomeSoggetto()));
		}
		return soggetti;
	}

	public String getServerFromSoggetto(String tipoSoggetto,String nomeSoggetto) {
		String idPorta =null;
		Soggetto soggetto = this.dynamicUtilsService.findSoggettoByTipoNome(tipoSoggetto, nomeSoggetto);

		if(soggetto != null)
			idPorta = soggetto.getServer();

		return idPorta;
	}

	public boolean checkTipoPdd(String nome,TipoPdD tipoPdD) {
		return this.dynamicUtilsService.checkTipoPdd(nome, tipoPdD);
	}

	/***
	 * 
	 * Restituisce l'elenco dei servizi associati al soggetto erogatore passato come parametro
	 * 
	 * La Mappa contiene
	 * Nome Azione
	 * 
	 * @param erogatore
	 * @return
	 */
	public List<Map<String, Object>> findAzioniFromServizio(String tipoProtocollo,String tipoServizio ,String nomeServizio, String nomeAzione){
		List<Map<String, Object>>  list = new ArrayList<Map<String,Object>>();

		this.log.debug("Get Lista Azioni from Servizio [Tipo: " + tipoServizio+ "], [Nome: " + nomeServizio + "]");
		try{
			list = this.dynamicUtilsService.findAzioniFromServizio(tipoProtocollo,tipoServizio, nomeServizio,nomeAzione);
		}catch(Exception e){
			this.log.error("Si e' verificato un errore durante la ricerca Azioni from Servizio [Tipo: " + tipoServizio+ "], [Nome: " + nomeServizio + "]", e);
		}
		return list;
	}



	/***
	 * 
	 * Restituisce la lista delle select items  per le azioni
	 * 
	 * @param idAccordo
	 * @param nomeServizio
	 * @return
	 */
	public List<SelectItem> getListaSelectItemsAzioniFromServizio(String tipoProtocollo,String tipoServizio ,String nomeServizio, String nomeAzione){
		List<SelectItem> azioni = new ArrayList<SelectItem>();

		try{
			List<Map<String, Object>> list = this.findAzioniFromServizio(tipoProtocollo,tipoServizio,nomeServizio,nomeAzione);
			for (Map<String,Object> azioneObj : list) {
				String azione = (String) azioneObj.get("nome");
				SelectItem item = new SelectItem(azione);
				azioni.add(item);

			}
		}catch(Exception e){

		}

		return azioni;
	}


	/***
	 * 
	 * Restituisce l'elenco dei servizi associati al soggetto erogatore passato come parametro
	 * 
	 * La Mappa contiene
	 * Nome Azione
	 * 
	 * @param erogatore
	 * @return
	 */
	public List<Map<String, Object>> getAzioniFromAccordoServizio(String tipoProtocollo,IDAccordo idAccordo,String tipoServizio,String nomeServizio,  String tipoErogatore , String nomeErogatore,String nomeAzione){
		List<Map<String, Object>>  list = new ArrayList<Map<String,Object>>();

		//		if(idAccordo != null && nomeServizio != null){
		this.log.debug("Get Lista Azioni from Accordo Servizio [Accordo: " + (idAccordo != null ? idAccordo.getNome() : "Non specificato") + "], [nome Servizio: " + nomeServizio + "]");
		try{
			list = this.dynamicUtilsService.getAzioniFromAccordoServizio(tipoProtocollo,idAccordo,tipoServizio, nomeServizio,tipoErogatore,nomeErogatore,nomeAzione);
		}catch(Exception e){
			this.log.error("Si e' verificato un errore durante la ricerca  Azioni per L'accordo [" + (idAccordo != null ? idAccordo.getNome() : "Null")+ "]",e);
			//			}
		}
		return list;
	}

	/***
	 * 
	 * Restituisce la lista delle select items  per le azioni
	 * 
	 * @param idAccordo
	 * @param nomeServizio
	 * @return
	 */
	public List<SelectItem> getListaSelectItemsAzioniFromAccordoServizio(String tipoProtocollo,IDAccordo idAccordo,String tipoServizio,String nomeServizio,  String tipoErogatore , String nomeErogatore,String nomeAzione){
		List<SelectItem> azioni = new ArrayList<SelectItem>();

		try{
			List<Map<String, Object>> list = this.getAzioniFromAccordoServizio(tipoProtocollo,idAccordo,tipoServizio,nomeServizio,tipoErogatore,nomeErogatore,nomeAzione);
			for (Map<String,Object> azioneObj : list) {

				String azione = (String) azioneObj.get("nome");

				if(azione == null){
					try {
						azione = (String) azioneObj.get(JDBCUtilities.getAlias(AccordoServizioParteComune.model().PORT_TYPE.OPERATION.NOME));
					} catch (ServiceException e) {
						//						this.log.debug(e,e);
					}
				}

				if(azione == null){
					try {
						azione = (String) azioneObj.get(JDBCUtilities.getAlias(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE.NOME));
					} catch (ServiceException e) {
						//						this.log.debug(e,e);
					}
				}

				SelectItem item = new SelectItem(azione);
				azioni.add(item);

			}
		}catch(Exception e){

		}

		return azioni;
	}


	/***
	 * 
	 * Restituisce l'elenco dei servizi associati al soggetto erogatore passato come parametro
	 * 
	 * La Mappa contiene
	 * Tipo Servizio  {@link AccordoServizioParteSpecifica.model().TIPO}
	 * Nome Servizio  {@link AccordoServizioParteSpecifica.model().NOME}
	 * AccordoServizioParteComune {@link AccordoServizioParteComune}
	 * 
	 * @param erogatore
	 * @return
	 */
	public List<Map<String, Object>> findElencoServiziSoggettoErogatore(String tipoProtocollo,Soggetto erogatore){
		List<Map<String, Object>>  list = null;
		this.log.debug("Find Lista Servizi per il Soggetto Erogatore [" + (erogatore != null ? erogatore.getNomeSoggetto() : "Null")+ "]");
		try{
			list = this.dynamicUtilsService.findElencoServizi(tipoProtocollo,erogatore);
		}catch(Exception e){
			this.log.error("Si e' verificato un errore durante la ricerca dei servizi per il Soggetto Erogatore [" + (erogatore != null ? erogatore.getNomeSoggetto() : "Null")+ "]");
		}
		return list;
	}

	/**
	 * 
	 * 
	 * @param erogatore
	 * @param showTipoServizio le option generate avranno label "TipoServizio/NomeServizio"
	 * @param showUriAccordo le option generate avranno label "NomeServizio UriAccordoServizio"
	 * @return
	 */
	public List<SelectItem> getListaSelectItemsElencoServiziSoggettoErogatore(String tipoProtocollo,Soggetto erogatore, boolean showTipoServizio, boolean showUriAccordo){
		List<SelectItem> servizi = new ArrayList<SelectItem>();

		try{
			List<Map<String, Object>> mapServizi = this.findElencoServiziSoggettoErogatore(tipoProtocollo,erogatore);

			if(mapServizi != null && mapServizi.size() > 0){
				for (Map<String, Object> res : mapServizi) {

					String label= null;
					// servizi.add(new
					// SelectItem(servizio.getAccordo().getNome()+"@"+servizio.getNome()));
					StringBuilder uri = new StringBuilder();

					Object obj = res.get(JDBCUtilities.getAlias(AccordoServizioParteSpecifica.model().NOME));

					String nomeAsps = (obj instanceof String) ? (String) obj : null;

					String tipoAsps = null;
					if(showTipoServizio){
						obj = res.get(JDBCUtilities.getAlias(AccordoServizioParteSpecifica.model().TIPO));
						tipoAsps = (obj instanceof String) ? (String) obj : null;
						if(tipoAsps != null)
							uri.append(tipoAsps).append("/");
					}

					uri.append(nomeAsps);

					if(showUriAccordo){
						obj = res.get("idAccordo");

						AccordoServizioParteComune aspc = (obj instanceof AccordoServizioParteComune) ? (AccordoServizioParteComune) obj : null;

						if(aspc != null){
							String nomeAspc = aspc.getNome();

							Integer versioneAspc = aspc.getVersione();

							String nomeReferenteAspc = (aspc.getIdReferente() != null) ? aspc.getIdReferente().getNome() : null;

							String tipoReferenteAspc= (aspc.getIdReferente() != null) ? aspc.getIdReferente().getTipo() : null;

							try {
								uri.append(" (").append(IDAccordoFactory
										.getInstance()
										.getUriFromValues(nomeAspc,tipoReferenteAspc,nomeReferenteAspc,versioneAspc)

										//								res.getIdAccordoServizioParteComune().getNome(),
										//								res.getIdAccordoServizioParteComune()
										//										.getIdSoggetto() != null ? res
										//										.getIdAccordoServizioParteComune()
										//										.getIdSoggetto().getTipo() : null,
										//								res.getIdAccordoServizioParteComune()
										//										.getIdSoggetto() != null ? res
										//										.getIdAccordoServizioParteComune()
										//										.getIdSoggetto().getNome() : null,
										//								res.getIdAccordoServizioParteComune()
										//										.getVersione())
										);

								uri.append(")");
							} catch (DriverRegistroServiziException e) {
								// ignore
								uri = new StringBuilder();
								uri.append("");
							}
						}
					}

					label = uri.toString();
					//compongo la label e la imposto
					servizi.add(new SelectItem(label));
				}
			}
		}catch(Exception e){
			this.log.error("Si e' verificato un errore durante la ricerca dei servizi per il Soggetto Erogatore [" + (erogatore != null ? erogatore.getNomeSoggetto() : "Null")+ "]");
		}
		return servizi;
	}


	/**
	 * 
	 * 
	 * @param erogatore
	 * @param showTipoServizio le option generate avranno label "TipoServizio/NomeServizio"
	 * @param showUriAccordo le option generate avranno label "NomeServizio UriAccordoServizio"
	 * @return
	 */
	public List<SelectItem> getListaSelectItemsAccordiServizio(String tipoProtocollo,
			String tipoSoggetto, String nomeSoggetto, boolean isReferente, boolean isErogatore){
		List<SelectItem> servizi = new ArrayList<SelectItem>();

		try{
			List<AccordoServizioParteComune> listaAccordi = this.dynamicUtilsService.getAccordiServizio(tipoProtocollo,tipoSoggetto, nomeSoggetto, isReferente, isErogatore);

			if(listaAccordi != null && listaAccordi.size() > 0){
				for (AccordoServizioParteComune aspc : listaAccordi) {

					StringBuilder uri = new StringBuilder();
					if(aspc != null){
						String nomeAspc = aspc.getNome();

						Integer versioneAspc = aspc.getVersione();

						String nomeReferenteAspc = (aspc.getIdReferente() != null) ? aspc.getIdReferente().getNome() : null;

						String tipoReferenteAspc= (aspc.getIdReferente() != null) ? aspc.getIdReferente().getTipo() : null;

						try {
							uri.append(IDAccordoFactory.getInstance().getUriFromValues(nomeAspc,tipoReferenteAspc,nomeReferenteAspc,versioneAspc));
						} catch (DriverRegistroServiziException e) {
							// ignore
							uri = new StringBuilder();
							uri.append("");
						}
					}


					String label = uri.toString();
					//compongo la label e la imposto
					servizi.add(new SelectItem(label));
				}
			}
		}catch(Exception e){
			this.log.error("Si e' verificato un errore durante la ricerca degli Accordi di servizio il Soggetto [" + tipoSoggetto + "/" + nomeSoggetto+ "] Referente ["+isReferente+"], Erogatore ["+isErogatore+"]");
		}
		return servizi;
	}

	/**
	 * 
	 * 
	 * @param erogatore
	 * @param showTipoServizio le option generate avranno label "TipoServizio/NomeServizio"
	 * @param showUriAccordo le option generate avranno label "NomeServizio UriAccordoServizio"
	 * @return
	 */
	public List<SelectItem> getListaSelectItemsElencoServiziFromAccordoAndSoggettoErogatore(String tipoProtocollo,String uriAccordoServizio, String tipoSoggetto , String nomeSoggetto, boolean showErogatore){
		return getListaSelectItemsElencoServiziFromAccordoAndSoggettoErogatore(tipoProtocollo, uriAccordoServizio, tipoSoggetto, nomeSoggetto, showErogatore, false);
	}

	/**
	 * 
	 * 
	 * @param erogatore
	 * @param showTipoServizio le option generate avranno label "TipoServizio/NomeServizio"
	 * @param showUriAccordo le option generate avranno label "NomeServizio UriAccordoServizio"
	 * @return
	 */
	public List<SelectItem> getListaSelectItemsElencoServiziFromAccordoAndSoggettoErogatore(String tipoProtocollo,String uriAccordoServizio, String tipoSoggetto , String nomeSoggetto, boolean showErogatore, boolean soloOperativi){
		List<SelectItem> servizi = new ArrayList<SelectItem>();

		try{

			UserDetailsBean user = Utility.getLoggedUser();

			List<AccordoServizioParteSpecifica> servizi2 = this.dynamicUtilsService.getServizi(tipoProtocollo,uriAccordoServizio, tipoSoggetto, nomeSoggetto);

			if(servizi2 != null && servizi2.size() > 0){
				for (AccordoServizioParteSpecifica res : servizi2) {
					boolean add= true;
					String label= null;
					// servizi.add(new
					// SelectItem(servizio.getAccordo().getNome()+"@"+servizio.getNome()));
					StringBuilder uri = new StringBuilder();

					String nomeAsps = res.getNome();

					String tipoAsps = res.getTipo();

					if(tipoAsps != null)
						uri.append(tipoAsps).append("/");

					uri.append(nomeAsps);

					if(showErogatore ){
						uri.append(" (").append(res.getIdErogatore().getTipo()).append("/").append(res.getIdErogatore().getNome()).append(")");
					}

					label = uri.toString();
					//compongo la label e la imposto
					if(soloOperativi){ // controllo se il soggetto e' associato ad una pdd operativa
						String nomePddFromSoggetto = this.getServerFromSoggetto(res.getIdErogatore().getTipo(), res.getIdErogatore().getNome());
						add = this.checkTipoPdd(nomePddFromSoggetto, TipoPdD.OPERATIVO);
					}

					if(add && !user.isAdmin()){

						// controllo sul soggetto
						boolean existsPermessoSoggetto = false;
						if(user.getSizeSoggetti()>0){
							for (IDSoggetto utenteSoggetto : user.getUtenteSoggettoList()) {
								if(res.getIdErogatore().getTipo().equals(utenteSoggetto.getTipo()) &&
										res.getIdErogatore().getNome().equals(utenteSoggetto.getNome())){
									existsPermessoSoggetto = true;
									break;
								}
							}
						}

						boolean existsPermessoServizio = false;
						if(!existsPermessoSoggetto){
							if(user.getSizeSoggetti()>0){
								for (IDServizio utenteSoggetto : user.getUtenteServizioList()) {
									if(res.getIdErogatore().getTipo().equals(utenteSoggetto.getSoggettoErogatore().getTipo()) &&
											res.getIdErogatore().getNome().equals(utenteSoggetto.getSoggettoErogatore().getNome()) &&
											res.getTipo().equals(utenteSoggetto.getTipo()) &&
											res.getNome().equals(utenteSoggetto.getNome())){
										existsPermessoServizio = true;
										break;
									}
								}
							}
						}

						add = (existsPermessoSoggetto || existsPermessoServizio);
					}

					if(add)
						servizi.add(new SelectItem(label));
				}
			}


		}catch(Exception e){
			this.log.error("Si e' verificato un errore durante la ricerca dei servizi per l'accordo ["+uriAccordoServizio+"] erogati dal Soggetto [" + tipoSoggetto + "/" + nomeSoggetto+ "]");
		}
		return servizi;
	}

	public Integer getLunghezzaSelectList(List<SelectItem> listaSelectItem){
		return getLunghezzaSelectList(listaSelectItem, this.getDefaultFont());
	}

	public Integer getLunghezzaSelectList(List<SelectItem> listaSelectItem, Font font){
		int lunghezza = 0;
		Integer lunghezzaToRet = 0;
		try{
			if(listaSelectItem != null && listaSelectItem.size() > 0){
				for (SelectItem selectItem : listaSelectItem) {
					Object value = selectItem.getValue();
					if(value instanceof String){
						String label = (String) value;
						lunghezza = getFontWidth(label,font);
						lunghezzaToRet = Math.max(lunghezza, lunghezzaToRet);
					}
				}
			}
		}catch(Throwable e){
			this.log.error("Si e' verificato un errore durante il calcolo della lunghezza delle select items");
		}
		return lunghezzaToRet;
	}

	/***
	 * 
	 * Controlla se il valore dato sta all'interno dei due estremi, nel caso in cui non lo sia viene riportato 
	 * ad uno dei due estremi.
	 * 
	 * @param minValue
	 * @param maxValue
	 * @param value
	 * @return 
	 */
	public static Integer checkLimits(Integer minValue, Integer maxValue , Integer value){
		// valore deve essere compreso minore del max ma almeno quanto la default
		Integer toRet = Math.max(minValue, value);
		toRet = Math.min(toRet, maxValue);
		return toRet;
	}


	// UTILITIES misurazione dimensione text
	public Integer getFontWidth(String text) throws Throwable{
		return getFontWidth(text, this.getDefaultFont());
	} 

	public Integer getFontWidth(String text, String fontName, int fontStyle, int fontSize) throws Throwable{
		Font fontToCheck = new Font(fontName,  fontStyle , fontSize);
		return getFontWidth(text, fontToCheck);
	} 


	public Integer getFontWidth(String text, Font fontToCheck) throws Throwable{
		if(this.fontRenderContext == null){
			if(this.affineTransform == null)
				this.affineTransform = new AffineTransform();

			this.fontRenderContext = new FontRenderContext(this.affineTransform,true,true);
		}

		Rectangle2D rectangle2d = fontToCheck.getStringBounds(text, this.fontRenderContext);
		return (int) rectangle2d.getWidth(); 
	}	
	//	public Integer getFontWidthWithFontMetrics(String text, String fontName, int fontStyle, int fontSize) throws Throwable{
	//		Font fontToCheck = new Font(fontName,  fontStyle , fontSize);
	//		return this.getFontWidthWithFontMetrics(text,fontToCheck);
	//	} 
	//	public Integer getFontWidthWithFontMetrics(String text, Font fontTocheck ) throws Throwable{
	//		if(this.fm == null){
	//			Canvas c = new Canvas();
	//			this.fm = c.getFontMetrics(fontTocheck);
	//		}
	//		return this.fm.stringWidth(text);
	//	}

	/***
	 * utilizzo Lucida sans come font di dafault poiche' e' generalmente presente nella jdk
	 * 
	 * @return
	 */
	public Font getDefaultFont() {
		if(this.defaultFont == null)
			this.defaultFont = new Font("Lucida Sans", Font.PLAIN , 11);

		return this.defaultFont;
	}
	public void setDefaultFont(Font defaultFont) {
		this.defaultFont = defaultFont;
	}


	public AccordoServizioParteSpecifica getAspsFromValues(String tipoServizio, String nomeServizio, String tipoErogatore, String nomeErogatore){
		return this.dynamicUtilsService.getAspsFromValues(tipoServizio, nomeServizio, tipoErogatore, nomeErogatore);
	}

	/***
	 * 
	 * Restituisce l'elenco delle porte delegate associate al soggetto fruitore passato come parametro
	 * 
	 * La Mappa contiene
	 * Nome Azione
	 * 
	 * @param erogatore
	 * @return
	 */
	public List<PortaDelegata> findPorteDelegate(String tipoProtocollo,String idAccordo,String tipoServizio,String nomeServizio,  String tipoErogatore , String nomeErogatore,String nomeAzione,String tipoFruitore, String nomeFruitore, PermessiUtenteOperatore permessiUtenteOperatore){
		List<PortaDelegata>  list = new ArrayList<PortaDelegata>();

		//		if(idAccordo != null && nomeServizio != null){
		this.log.debug("findPorteDelegate [Accordo: " + (idAccordo != null ? idAccordo : "Non specificato") + "], [nome Servizio: " + nomeServizio + "], [nome Erogarore: " + nomeErogatore + "], [nome Fruitore: " + nomeFruitore + "]");
		try{
			list = this.dynamicUtilsService.findPorteDelegate(tipoProtocollo, idAccordo, tipoFruitore, nomeFruitore, tipoServizio, nomeServizio, tipoErogatore, nomeErogatore, nomeAzione,permessiUtenteOperatore);
		}catch(Exception e){
			this.log.error("Si e' verificato un errore durante la ricerca  findPorteDelegate [" + (idAccordo != null ? idAccordo  : "Non specificato") + "], [nome Servizio: " + nomeServizio + "], [nome Erogarore: " + nomeErogatore + "], [nome Fruitore: " + nomeFruitore + "]",e);
			//			}
		}
		return list;
	}

	public List<SelectItem> getListaSelectItemsPorteDelegate(String tipoProtocollo,String idAccordo,String tipoServizio,String nomeServizio,  String tipoErogatore , String nomeErogatore,String nomeAzione,String tipoFruitore, String nomeFruitore, PermessiUtenteOperatore permessiUtenteOperatore){

		List<SelectItem> servizi = new ArrayList<SelectItem>();

		try{
			List<PortaDelegata> listaPD = this.findPorteDelegate(tipoProtocollo, idAccordo, tipoServizio, nomeServizio, tipoErogatore, nomeErogatore, nomeAzione, tipoFruitore, nomeFruitore,permessiUtenteOperatore);
			if(listaPD != null && listaPD.size() > 0){
				for (PortaDelegata pd : listaPD) {
					String label = pd.getNome();
					servizi.add(new SelectItem(label));
				}
			}
		}catch(Exception e){
			this.log.error("Si e' verificato un errore durante la ricerca  findPorteDelegate [" + (idAccordo != null ? idAccordo  : "Non specificato") + "], [nome Servizio: " + nomeServizio + "], [nome Erogarore: " + nomeErogatore + "], [nome Fruitore: " + nomeFruitore + "]",e);
		}
		return servizi;
	}


	public List<Soggetto> getListaSoggetti(String tipoProtocollo,TipoPdD tipoPdD){
		return this.dynamicUtilsService.findElencoSoggettiFromTipoPdD(tipoProtocollo, tipoPdD);
	}

	public List<Soggetto> getSoggettiErogatoreAutoComplete(String tipoProtocollo,String uriAccordoServizio, String input, boolean soloOperativi){
		List<Soggetto> list = this.dynamicUtilsService.getSoggettiErogatoreAutoComplete(tipoProtocollo, uriAccordoServizio, input);

		if(soloOperativi && list != null && list.size() >0) {
			List<Soggetto> lstOperativi = new ArrayList<Soggetto>();

			for (Soggetto soggetto : list) {
				if(this.checkTipoPdd(soggetto.getServer(), TipoPdD.OPERATIVO))
					lstOperativi.add(soggetto);
			}

			return lstOperativi;
		}


		return new ArrayList<Soggetto>();
	}

}