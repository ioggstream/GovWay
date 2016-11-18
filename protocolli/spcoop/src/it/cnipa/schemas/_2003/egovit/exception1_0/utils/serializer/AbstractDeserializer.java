/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package it.cnipa.schemas._2003.egovit.exception1_0.utils.serializer;

import org.openspcoop2.generic_project.exception.DeserializerException;

import it.cnipa.schemas._2003.egovit.exception1_0.EccezioneBusta;
import it.cnipa.schemas._2003.egovit.exception1_0.Eccezione;
import it.cnipa.schemas._2003.egovit.exception1_0.EccezioneProcessamento;
import it.cnipa.schemas._2003.egovit.exception1_0.MessaggioDiErroreApplicativo;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.File;

/**     
 * XML Deserializer of beans
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public abstract class AbstractDeserializer {



	protected abstract Object _xmlToObj(InputStream is, Class<?> c) throws Exception;
	
	private Object xmlToObj(InputStream is,Class<?> c) throws DeserializerException{
		try{
			return this._xmlToObj(is, c);
		}catch(Exception e){
			throw new DeserializerException(e.getMessage(), e);
		}
	}
	private Object xmlToObj(String fileName,Class<?> c) throws DeserializerException{
		try{
			return this.xmlToObj(new File(fileName), c);
		}catch(Exception e){
			throw new DeserializerException(e.getMessage(), e);
		}
	}
	private Object xmlToObj(File file,Class<?> c) throws DeserializerException{
		FileInputStream fin = null;
		try{
			fin = new FileInputStream(file);
			return this._xmlToObj(fin, c);
		}catch(Exception e){
			throw new DeserializerException(e.getMessage(),e);
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
		}
	}
	private Object xmlToObj(byte[] file,Class<?> c) throws DeserializerException{
		ByteArrayInputStream fin = null;
		try{
			fin = new ByteArrayInputStream(file);
			return this._xmlToObj(fin, c);
		}catch(Exception e){
			throw new DeserializerException(e.getMessage(),e);
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
		}
	}






	/*
	 =================================================================================
	 Object: EccezioneBusta
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneBusta}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneBusta}
	 * @return Object type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneBusta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public EccezioneBusta readEccezioneBusta(String fileName) throws DeserializerException {
		return (EccezioneBusta) this.xmlToObj(fileName, EccezioneBusta.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneBusta}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneBusta}
	 * @return Object type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneBusta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public EccezioneBusta readEccezioneBusta(File file) throws DeserializerException {
		return (EccezioneBusta) this.xmlToObj(file, EccezioneBusta.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneBusta}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneBusta}
	 * @return Object type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneBusta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public EccezioneBusta readEccezioneBusta(InputStream in) throws DeserializerException {
		return (EccezioneBusta) this.xmlToObj(in, EccezioneBusta.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneBusta}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneBusta}
	 * @return Object type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneBusta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public EccezioneBusta readEccezioneBusta(byte[] in) throws DeserializerException {
		return (EccezioneBusta) this.xmlToObj(in, EccezioneBusta.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneBusta}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneBusta}
	 * @return Object type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneBusta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public EccezioneBusta readEccezioneBustaFromString(String in) throws DeserializerException {
		return (EccezioneBusta) this.xmlToObj(in.getBytes(), EccezioneBusta.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: Eccezione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.cnipa.schemas._2003.egovit.exception1_0.Eccezione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.cnipa.schemas._2003.egovit.exception1_0.Eccezione}
	 * @return Object type {@link it.cnipa.schemas._2003.egovit.exception1_0.Eccezione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Eccezione readEccezione(String fileName) throws DeserializerException {
		return (Eccezione) this.xmlToObj(fileName, Eccezione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.cnipa.schemas._2003.egovit.exception1_0.Eccezione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.cnipa.schemas._2003.egovit.exception1_0.Eccezione}
	 * @return Object type {@link it.cnipa.schemas._2003.egovit.exception1_0.Eccezione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Eccezione readEccezione(File file) throws DeserializerException {
		return (Eccezione) this.xmlToObj(file, Eccezione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.cnipa.schemas._2003.egovit.exception1_0.Eccezione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.cnipa.schemas._2003.egovit.exception1_0.Eccezione}
	 * @return Object type {@link it.cnipa.schemas._2003.egovit.exception1_0.Eccezione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Eccezione readEccezione(InputStream in) throws DeserializerException {
		return (Eccezione) this.xmlToObj(in, Eccezione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.cnipa.schemas._2003.egovit.exception1_0.Eccezione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.cnipa.schemas._2003.egovit.exception1_0.Eccezione}
	 * @return Object type {@link it.cnipa.schemas._2003.egovit.exception1_0.Eccezione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Eccezione readEccezione(byte[] in) throws DeserializerException {
		return (Eccezione) this.xmlToObj(in, Eccezione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.cnipa.schemas._2003.egovit.exception1_0.Eccezione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.cnipa.schemas._2003.egovit.exception1_0.Eccezione}
	 * @return Object type {@link it.cnipa.schemas._2003.egovit.exception1_0.Eccezione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Eccezione readEccezioneFromString(String in) throws DeserializerException {
		return (Eccezione) this.xmlToObj(in.getBytes(), Eccezione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: EccezioneProcessamento
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneProcessamento}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneProcessamento}
	 * @return Object type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneProcessamento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public EccezioneProcessamento readEccezioneProcessamento(String fileName) throws DeserializerException {
		return (EccezioneProcessamento) this.xmlToObj(fileName, EccezioneProcessamento.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneProcessamento}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneProcessamento}
	 * @return Object type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneProcessamento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public EccezioneProcessamento readEccezioneProcessamento(File file) throws DeserializerException {
		return (EccezioneProcessamento) this.xmlToObj(file, EccezioneProcessamento.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneProcessamento}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneProcessamento}
	 * @return Object type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneProcessamento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public EccezioneProcessamento readEccezioneProcessamento(InputStream in) throws DeserializerException {
		return (EccezioneProcessamento) this.xmlToObj(in, EccezioneProcessamento.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneProcessamento}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneProcessamento}
	 * @return Object type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneProcessamento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public EccezioneProcessamento readEccezioneProcessamento(byte[] in) throws DeserializerException {
		return (EccezioneProcessamento) this.xmlToObj(in, EccezioneProcessamento.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneProcessamento}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneProcessamento}
	 * @return Object type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneProcessamento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public EccezioneProcessamento readEccezioneProcessamentoFromString(String in) throws DeserializerException {
		return (EccezioneProcessamento) this.xmlToObj(in.getBytes(), EccezioneProcessamento.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: MessaggioDiErroreApplicativo
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.cnipa.schemas._2003.egovit.exception1_0.MessaggioDiErroreApplicativo}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.cnipa.schemas._2003.egovit.exception1_0.MessaggioDiErroreApplicativo}
	 * @return Object type {@link it.cnipa.schemas._2003.egovit.exception1_0.MessaggioDiErroreApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessaggioDiErroreApplicativo readMessaggioDiErroreApplicativo(String fileName) throws DeserializerException {
		return (MessaggioDiErroreApplicativo) this.xmlToObj(fileName, MessaggioDiErroreApplicativo.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.cnipa.schemas._2003.egovit.exception1_0.MessaggioDiErroreApplicativo}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.cnipa.schemas._2003.egovit.exception1_0.MessaggioDiErroreApplicativo}
	 * @return Object type {@link it.cnipa.schemas._2003.egovit.exception1_0.MessaggioDiErroreApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessaggioDiErroreApplicativo readMessaggioDiErroreApplicativo(File file) throws DeserializerException {
		return (MessaggioDiErroreApplicativo) this.xmlToObj(file, MessaggioDiErroreApplicativo.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.cnipa.schemas._2003.egovit.exception1_0.MessaggioDiErroreApplicativo}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.cnipa.schemas._2003.egovit.exception1_0.MessaggioDiErroreApplicativo}
	 * @return Object type {@link it.cnipa.schemas._2003.egovit.exception1_0.MessaggioDiErroreApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessaggioDiErroreApplicativo readMessaggioDiErroreApplicativo(InputStream in) throws DeserializerException {
		return (MessaggioDiErroreApplicativo) this.xmlToObj(in, MessaggioDiErroreApplicativo.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.cnipa.schemas._2003.egovit.exception1_0.MessaggioDiErroreApplicativo}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.cnipa.schemas._2003.egovit.exception1_0.MessaggioDiErroreApplicativo}
	 * @return Object type {@link it.cnipa.schemas._2003.egovit.exception1_0.MessaggioDiErroreApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessaggioDiErroreApplicativo readMessaggioDiErroreApplicativo(byte[] in) throws DeserializerException {
		return (MessaggioDiErroreApplicativo) this.xmlToObj(in, MessaggioDiErroreApplicativo.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.cnipa.schemas._2003.egovit.exception1_0.MessaggioDiErroreApplicativo}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.cnipa.schemas._2003.egovit.exception1_0.MessaggioDiErroreApplicativo}
	 * @return Object type {@link it.cnipa.schemas._2003.egovit.exception1_0.MessaggioDiErroreApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessaggioDiErroreApplicativo readMessaggioDiErroreApplicativoFromString(String in) throws DeserializerException {
		return (MessaggioDiErroreApplicativo) this.xmlToObj(in.getBytes(), MessaggioDiErroreApplicativo.class);
	}	
	
	
	

}