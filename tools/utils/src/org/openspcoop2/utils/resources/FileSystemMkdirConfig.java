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


package org.openspcoop2.utils.resources;

/**
 * Classe utilizzabile per raccogliere la configurazione dell'operazione mkdir
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author: apoli $
 * @version $Rev: 13574 $, $Date: 2018-01-26 12:24:34 +0100 (Fri, 26 Jan 2018) $
 */
public class FileSystemMkdirConfig {

	boolean checkCanRead = true;
	boolean checkCanWrite = true;
	boolean checkCanExecute = false;
	boolean crateParentIfNotExists = true;
	
	public boolean isCheckCanRead() {
		return this.checkCanRead;
	}
	public void setCheckCanRead(boolean checkCanRead) {
		this.checkCanRead = checkCanRead;
	}
	public boolean isCheckCanWrite() {
		return this.checkCanWrite;
	}
	public void setCheckCanWrite(boolean checkCanWrite) {
		this.checkCanWrite = checkCanWrite;
	}
	public boolean isCrateParentIfNotExists() {
		return this.crateParentIfNotExists;
	}
	public void setCrateParentIfNotExists(boolean crateParentIfNotExists) {
		this.crateParentIfNotExists = crateParentIfNotExists;
	}
	public boolean isCheckCanExecute() {
		return this.checkCanExecute;
	}
	public void setCheckCanExecute(boolean checkCanExecute) {
		this.checkCanExecute = checkCanExecute;
	}
}