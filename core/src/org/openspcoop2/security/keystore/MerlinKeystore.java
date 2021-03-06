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

package org.openspcoop2.security.keystore;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.util.Properties;

import org.openspcoop2.security.SecurityException;

/**
 * MerlinKeystore
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MerlinKeystore {

	private KeyStore ks = null;
	private String tipoStore = null;
	private String pathStore = null;
	private String passwordStore = null;
	private String passwordPrivateKey = null;
	
	public MerlinKeystore(String propertyFilePath,String passwordPrivateKey) throws SecurityException{
		
		InputStream isStore = null;
		try{
			if(propertyFilePath==null){
				throw new Exception("PropertyFilePath per lo Store non indicato");
			}
			
			File fStore = new File(propertyFilePath);
			if(fStore.exists()){
				isStore = new FileInputStream(fStore);
			}else{
				isStore = MerlinKeystore.class.getResourceAsStream("/"+propertyFilePath);
				if(isStore==null){
					throw new Exception("Store ["+propertyFilePath+"] not found");
				}
			}
			Properties propStore = new Properties();
			propStore.load(isStore);
			isStore.close();
			
			this._initMerlinKeystore(propStore,passwordPrivateKey);
			
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}finally{
			try{
				if(isStore!=null){
					isStore.close();
				}
			}catch(Exception eClose){}
		}
		
	}
	
	public MerlinKeystore(Properties propStore,String passwordPrivateKey) throws SecurityException{
		this._initMerlinKeystore(propStore,passwordPrivateKey);
	}
	
	private void _initMerlinKeystore(Properties propStore,String passwordPrivateKey) throws SecurityException{
		
		try{
			if(propStore==null){
				throw new Exception("Properties per lo Store non indicato");
			}
			
			this.tipoStore = propStore.getProperty(KeystoreConstants.PROPERTY_KEYSTORE_TYPE);
			if(this.tipoStore!=null){
				this.tipoStore = this.tipoStore.trim();
			}else{
				this.tipoStore = KeyStore.getDefaultType();
			}
			
			this.pathStore = propStore.getProperty(KeystoreConstants.PROPERTY_KEYSTORE_PATH);
			
			this.passwordStore = propStore.getProperty(KeystoreConstants.PROPERTY_KEYSTORE_PASSWORD);
			
			init(passwordPrivateKey);
			
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
		
	}
		
	public MerlinKeystore(String pathStore,String tipoStore,String passwordStore,String passwordPrivateKey) throws SecurityException{
		
		this.pathStore = pathStore;
		this.tipoStore = tipoStore;
		this.passwordStore = passwordStore;
		
		init(passwordPrivateKey);
		
	}
	
	private void init(String passwordPrivateKey) throws SecurityException{
		try{
			if(this.pathStore==null){
				throw new Exception("Path per lo Store non indicato");
			}
			if(this.tipoStore==null){
				throw new Exception("Tipo dello Store non indicato");
			}
			if(this.passwordStore==null){
				throw new Exception("Password dello Store non indicata");
			}
			
			File fPathStore = new File(this.pathStore);
			if(fPathStore.exists()==false){
				throw new Exception("keystore ["+fPathStore.getAbsolutePath()+"] not found");
			}
			FileInputStream finStore = new FileInputStream(fPathStore);
			
			this.ks = KeyStore.getInstance(this.tipoStore);
			this.ks.load(finStore, this.passwordStore.toCharArray());
			finStore.close();
			FixTrustAnchorsNotEmpty.addCertificate(this.ks);
			
			if(passwordPrivateKey==null){
				throw new Exception("Password chiave private non indicata per lo Store ["+this.pathStore+"] ");
			}
			this.passwordPrivateKey = passwordPrivateKey;
			
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
	}
	
	
	public Key getKey(String alias) throws SecurityException {
		try{
			return this.ks.getKey(alias, this.passwordPrivateKey.toCharArray());
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
	}

	public KeyStore getKeyStore() throws SecurityException {
		try{
			return this.ks;
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
	}

}
