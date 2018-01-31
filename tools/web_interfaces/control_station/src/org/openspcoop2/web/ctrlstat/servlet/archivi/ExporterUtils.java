/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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


package org.openspcoop2.web.ctrlstat.servlet.archivi;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.archive.ExportMode;
import org.openspcoop2.protocol.sdk.archive.IArchive;
import org.openspcoop2.protocol.sdk.constants.ArchiveType;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.servlet.ac.AccordiCooperazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;

/**
 * ExporterUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ExporterUtils {

	private ArchiviCore archiviCore;
	private SoggettiCore soggettiCore;
	private AccordiServizioParteComuneCore aspcCore;
	@SuppressWarnings("unused")
	private AccordiServizioParteSpecificaCore aspsCore;
	private AccordiCooperazioneCore acCore;
	
	
	public ExporterUtils(ArchiviCore archiviCore) throws Exception{
		this.archiviCore = archiviCore;
		this.soggettiCore = new SoggettiCore(archiviCore);
		this.aspcCore = new AccordiServizioParteComuneCore(archiviCore);
		this.aspsCore = new AccordiServizioParteSpecificaCore(archiviCore);
		this.acCore = new AccordiCooperazioneCore(archiviCore);
	}

	public List<ExportMode> getExportModesCompatibleWithAllProtocol(List<String> protocolli,ArchiveType archiveType) throws ProtocolException{
		
		List<ExportMode> exportModes = new ArrayList<ExportMode>();
		
		for (int i = 0; i < protocolli.size(); i++) {
			String protocolName = protocolli.get(i);
			IProtocolFactory<?> pf = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocolName);
			IArchive archiveEngine = pf.createArchive();
			List<ExportMode> exportModesByProtocol = archiveEngine.getExportModes(archiveType);
			for (ExportMode exportMode : exportModesByProtocol) {
				
				boolean found = true;
				
				for (int j = 0; j < protocolli.size(); j++) {
					if(j==i)
						continue;
					String protocolCheck = protocolli.get(j);
					IProtocolFactory<?> pfCheck = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocolCheck);
					IArchive archiveEngineCheck = pfCheck.createArchive();
					List<ExportMode> exportModesByProtocolCheck = archiveEngineCheck.getExportModes(archiveType);
					if(exportModesByProtocolCheck.contains(exportMode)==false){
						found = false;
						break;
					}
				}
				
				if(found){
					if(exportModes.contains(exportMode)==false)
						exportModes.add(exportMode);
				}
				
			}
		}
		
		return exportModes;
	}
	
	public Hashtable<ExportMode,String> getExportModesWithProtocol(List<String> protocolli,ArchiveType archiveType) throws ProtocolException{

		Hashtable<ExportMode,String> exportModes = new Hashtable<ExportMode,String>();
		for (int i = 0; i < protocolli.size(); i++) {
			String protocolName = protocolli.get(i);
			if(ArchiviCostanti.PARAMETRO_ARCHIVI_PROTOCOLLO_UNDEFINDED.equals(protocolName)==false){
				IProtocolFactory<?> pf = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocolName);
				IArchive archiveEngine = pf.createArchive();
				List<ExportMode> exportModesByProtocol = archiveEngine.getExportModes(archiveType);
				for (ExportMode exp : exportModesByProtocol) {
					if(exportModes.containsKey(exp)==false){
						exportModes.put(exp,protocolName);
					}
				}
			}
		}

		return exportModes;
	}

	
	public boolean existsAtLeastOneExportMpde(ArchiveType archiveType, HttpSession session) throws ProtocolException, DriverRegistroServiziException{
		List<String> protocolli = this.archiviCore.getProtocolli(session);
		return this.getExportModesWithProtocol(protocolli, archiveType).size()>0;
	}

	public List<?> getIdsSoggetti(String ids) throws DriverRegistroServiziNotFound, DriverRegistroServiziException{
		List<IDSoggetto> idsSoggetti = new ArrayList<IDSoggetto>();
		ArrayList<String> idsToExport = Utilities.parseIdsToRemove(ids);
		for (String id : idsToExport) {
			long idLong = Long.parseLong(id);
			idsSoggetti.add(this.soggettiCore.getIdSoggettoRegistro(idLong));
		}
		return idsSoggetti;
	}
	
	public List<?> getIdsAccordiServizioComposti(String ids) throws DriverRegistroServiziNotFound, DriverRegistroServiziException{
		return this.getIdsAccordiServizioParteComune(ids);
	}
	public List<?> getIdsAccordiServizioParteComune(String ids) throws DriverRegistroServiziNotFound, DriverRegistroServiziException{
		List<IDAccordo> idsAccordi = new ArrayList<IDAccordo>();
		ArrayList<String> idsToExport = Utilities.parseIdsToRemove(ids);
		for (String id : idsToExport) {
			long idLong = Long.parseLong(id);
			idsAccordi.add(this.aspcCore.getIdAccordoServizio(idLong));
		}
		return idsAccordi;
	}
	
	public List<?> getIdsAccordiServizioParteSpecifica(String ids) throws DriverRegistroServiziNotFound, DriverRegistroServiziException{
		List<IDServizio> idsAccordi = new ArrayList<IDServizio>();
		ArrayList<String> idsToExport = Utilities.parseIdsToRemove(ids);
		for (String id : idsToExport) {
			IDServizio idS = IDServizioFactory.getInstance().getIDServizioFromUri(id);
			idsAccordi.add(idS);
		}
		return idsAccordi;
	}
	
	public List<?> getIdsAccordiCooperazione(String ids) throws DriverRegistroServiziNotFound, DriverRegistroServiziException{
		List<IDAccordoCooperazione> idsAccordi = new ArrayList<IDAccordoCooperazione>();
		ArrayList<String> idsToExport = Utilities.parseIdsToRemove(ids);
		for (String id : idsToExport) {
			long idLong = Long.parseLong(id);
			idsAccordi.add(this.acCore.getIdAccordoCooperazione(idLong));
		}
		return idsAccordi;
	}
}
