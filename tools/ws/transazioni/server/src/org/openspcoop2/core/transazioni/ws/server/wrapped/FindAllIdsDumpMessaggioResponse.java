package org.openspcoop2.core.transazioni.ws.server.wrapped;

/**
 * <p>Java class for FindAllIdsDumpMessaggioResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="findAllIdsResponse">
 *     &lt;sequence>
 *         &lt;element name="itemsFound" type="{http://www.openspcoop2.org/core/transazioni}dump-messaggio" maxOccurs="unbounded" />
 *     &lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;
import org.openspcoop2.core.transazioni.IdDumpMessaggio;

/**     
 * FindAllIdsDumpMessaggioResponse
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "findAllIdsResponse", namespace="http://www.openspcoop2.org/core/transazioni/management", propOrder = {
    "itemsFound"
})
@javax.xml.bind.annotation.XmlRootElement(name = "findAllIdsResponse")
public class FindAllIdsDumpMessaggioResponse extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@XmlElement(name="itemsFound",required=true,nillable=false)
	private java.util.List<IdDumpMessaggio> itemsFound;
	
	public void setItemsFound(java.util.List<IdDumpMessaggio> itemsFound){
		this.itemsFound = itemsFound;
	}
	
	public java.util.List<IdDumpMessaggio> getItemsFound(){
		return this.itemsFound;
	}
	
	
	
	
}