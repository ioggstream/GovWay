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
package org.openspcoop2.core.eccezione.details;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.openspcoop2.core.eccezione.details package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
*/

@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openspcoop2.core.eccezione.details
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Eccezione }
     */
    public Eccezione createEccezione() {
        return new Eccezione();
    }

    /**
     * Create an instance of {@link DominioSoggetto }
     */
    public DominioSoggetto createDominioSoggetto() {
        return new DominioSoggetto();
    }

    /**
     * Create an instance of {@link DettaglioEccezione }
     */
    public DettaglioEccezione createDettaglioEccezione() {
        return new DettaglioEccezione();
    }

    /**
     * Create an instance of {@link Dominio }
     */
    public Dominio createDominio() {
        return new Dominio();
    }

    /**
     * Create an instance of {@link Dettagli }
     */
    public Dettagli createDettagli() {
        return new Dettagli();
    }

    /**
     * Create an instance of {@link Eccezioni }
     */
    public Eccezioni createEccezioni() {
        return new Eccezioni();
    }

    /**
     * Create an instance of {@link Dettaglio }
     */
    public Dettaglio createDettaglio() {
        return new Dettaglio();
    }


 }
