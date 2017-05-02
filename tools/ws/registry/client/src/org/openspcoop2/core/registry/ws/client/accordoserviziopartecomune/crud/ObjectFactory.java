/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it).
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

package org.openspcoop2.core.registry.ws.client.accordoserviziopartecomune.crud;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.openspcoop2.core.registry.ws.client.accordoserviziopartecomune.crud package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _SearchFilterAccordoServizioParteComune_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "search-filter-accordo-servizio-parte-comune");
    private final static QName _IdSoggetto_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "id-soggetto");
    private final static QName _AccordoServizioParteComuneServizioComposto_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "accordo-servizio-parte-comune-servizio-composto");
    private final static QName _WrapperIdAccordoCooperazione_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "wrapperIdAccordoCooperazione");
    private final static QName _WrapperIdAccordoServizioParteComune_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "wrapperIdAccordoServizioParteComune");
    private final static QName _WrapperIdPortaDominio_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "wrapperIdPortaDominio");
    private final static QName _WrapperIdSoggetto_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "wrapperIdSoggetto");
    private final static QName _WrapperIdAccordoServizioParteSpecifica_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "wrapperIdAccordoServizioParteSpecifica");
    private final static QName _WrapperIdRuolo_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "wrapperIdRuolo");
    private final static QName _RegistryServiceException_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "registry-service-exception");
    private final static QName _RegistryNotFoundException_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "registry-not-found-exception");
    private final static QName _RegistryMultipleResultException_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "registry-multiple-result-exception");
    private final static QName _RegistryNotImplementedException_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "registry-not-implemented-exception");
    private final static QName _RegistryNotAuthorizedException_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "registry-not-authorized-exception");
    private final static QName _Create_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "create");
    private final static QName _CreateResponse_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "createResponse");
    private final static QName _Update_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "update");
    private final static QName _UpdateResponse_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "updateResponse");
    private final static QName _UpdateOrCreate_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "updateOrCreate");
    private final static QName _UpdateOrCreateResponse_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "updateOrCreateResponse");
    private final static QName _DeleteById_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "deleteById");
    private final static QName _DeleteByIdResponse_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "deleteByIdResponse");
    private final static QName _DeleteAll_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "deleteAll");
    private final static QName _DeleteAllResponse_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "deleteAllResponse");
    private final static QName _DeleteAllByFilter_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "deleteAllByFilter");
    private final static QName _DeleteAllByFilterResponse_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "deleteAllByFilterResponse");
    private final static QName _Delete_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "delete");
    private final static QName _DeleteResponse_QNAME = new QName("http://www.openspcoop2.org/core/registry/management", "deleteResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openspcoop2.core.registry.ws.client.accordoserviziopartecomune.crud
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SearchFilterAccordoServizioParteComune }
     * 
     */
    public SearchFilterAccordoServizioParteComune createSearchFilterAccordoServizioParteComune() {
        return new SearchFilterAccordoServizioParteComune();
    }

    /**
     * Create an instance of {@link IdSoggetto }
     * 
     */
    public IdSoggetto createIdSoggetto() {
        return new IdSoggetto();
    }

    /**
     * Create an instance of {@link AccordoServizioParteComuneServizioComposto }
     * 
     */
    public AccordoServizioParteComuneServizioComposto createAccordoServizioParteComuneServizioComposto() {
        return new AccordoServizioParteComuneServizioComposto();
    }

    /**
     * Create an instance of {@link WrapperIdAccordoCooperazione }
     * 
     */
    public WrapperIdAccordoCooperazione createWrapperIdAccordoCooperazione() {
        return new WrapperIdAccordoCooperazione();
    }

    /**
     * Create an instance of {@link WrapperIdAccordoServizioParteComune }
     * 
     */
    public WrapperIdAccordoServizioParteComune createWrapperIdAccordoServizioParteComune() {
        return new WrapperIdAccordoServizioParteComune();
    }

    /**
     * Create an instance of {@link WrapperIdPortaDominio }
     * 
     */
    public WrapperIdPortaDominio createWrapperIdPortaDominio() {
        return new WrapperIdPortaDominio();
    }

    /**
     * Create an instance of {@link WrapperIdSoggetto }
     * 
     */
    public WrapperIdSoggetto createWrapperIdSoggetto() {
        return new WrapperIdSoggetto();
    }

    /**
     * Create an instance of {@link WrapperIdAccordoServizioParteSpecifica }
     * 
     */
    public WrapperIdAccordoServizioParteSpecifica createWrapperIdAccordoServizioParteSpecifica() {
        return new WrapperIdAccordoServizioParteSpecifica();
    }

    /**
     * Create an instance of {@link WrapperIdRuolo }
     * 
     */
    public WrapperIdRuolo createWrapperIdRuolo() {
        return new WrapperIdRuolo();
    }

    /**
     * Create an instance of {@link RegistryServiceException }
     * 
     */
    public RegistryServiceException createRegistryServiceException() {
        return new RegistryServiceException();
    }

    /**
     * Create an instance of {@link RegistryNotFoundException }
     * 
     */
    public RegistryNotFoundException createRegistryNotFoundException() {
        return new RegistryNotFoundException();
    }

    /**
     * Create an instance of {@link RegistryMultipleResultException }
     * 
     */
    public RegistryMultipleResultException createRegistryMultipleResultException() {
        return new RegistryMultipleResultException();
    }

    /**
     * Create an instance of {@link RegistryNotImplementedException }
     * 
     */
    public RegistryNotImplementedException createRegistryNotImplementedException() {
        return new RegistryNotImplementedException();
    }

    /**
     * Create an instance of {@link RegistryNotAuthorizedException }
     * 
     */
    public RegistryNotAuthorizedException createRegistryNotAuthorizedException() {
        return new RegistryNotAuthorizedException();
    }

    /**
     * Create an instance of {@link Create }
     * 
     */
    public Create createCreate() {
        return new Create();
    }

    /**
     * Create an instance of {@link CreateResponse }
     * 
     */
    public CreateResponse createCreateResponse() {
        return new CreateResponse();
    }

    /**
     * Create an instance of {@link Update }
     * 
     */
    public Update createUpdate() {
        return new Update();
    }

    /**
     * Create an instance of {@link UpdateResponse }
     * 
     */
    public UpdateResponse createUpdateResponse() {
        return new UpdateResponse();
    }

    /**
     * Create an instance of {@link UpdateOrCreate }
     * 
     */
    public UpdateOrCreate createUpdateOrCreate() {
        return new UpdateOrCreate();
    }

    /**
     * Create an instance of {@link UpdateOrCreateResponse }
     * 
     */
    public UpdateOrCreateResponse createUpdateOrCreateResponse() {
        return new UpdateOrCreateResponse();
    }

    /**
     * Create an instance of {@link DeleteById }
     * 
     */
    public DeleteById createDeleteById() {
        return new DeleteById();
    }

    /**
     * Create an instance of {@link DeleteByIdResponse }
     * 
     */
    public DeleteByIdResponse createDeleteByIdResponse() {
        return new DeleteByIdResponse();
    }

    /**
     * Create an instance of {@link DeleteAll }
     * 
     */
    public DeleteAll createDeleteAll() {
        return new DeleteAll();
    }

    /**
     * Create an instance of {@link DeleteAllResponse }
     * 
     */
    public DeleteAllResponse createDeleteAllResponse() {
        return new DeleteAllResponse();
    }

    /**
     * Create an instance of {@link DeleteAllByFilter }
     * 
     */
    public DeleteAllByFilter createDeleteAllByFilter() {
        return new DeleteAllByFilter();
    }

    /**
     * Create an instance of {@link DeleteAllByFilterResponse }
     * 
     */
    public DeleteAllByFilterResponse createDeleteAllByFilterResponse() {
        return new DeleteAllByFilterResponse();
    }

    /**
     * Create an instance of {@link Delete }
     * 
     */
    public Delete createDelete() {
        return new Delete();
    }

    /**
     * Create an instance of {@link DeleteResponse }
     * 
     */
    public DeleteResponse createDeleteResponse() {
        return new DeleteResponse();
    }

    /**
     * Create an instance of {@link UseInfo }
     * 
     */
    public UseInfo createUseInfo() {
        return new UseInfo();
    }

    /**
     * Create an instance of {@link InUseCondition }
     * 
     */
    public InUseCondition createInUseCondition() {
        return new InUseCondition();
    }

    /**
     * Create an instance of {@link ObjectId }
     * 
     */
    public ObjectId createObjectId() {
        return new ObjectId();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SearchFilterAccordoServizioParteComune }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "search-filter-accordo-servizio-parte-comune")
    public JAXBElement<SearchFilterAccordoServizioParteComune> createSearchFilterAccordoServizioParteComune(SearchFilterAccordoServizioParteComune value) {
        return new JAXBElement<SearchFilterAccordoServizioParteComune>(ObjectFactory._SearchFilterAccordoServizioParteComune_QNAME, SearchFilterAccordoServizioParteComune.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IdSoggetto }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "id-soggetto")
    public JAXBElement<IdSoggetto> createIdSoggetto(IdSoggetto value) {
        return new JAXBElement<IdSoggetto>(ObjectFactory._IdSoggetto_QNAME, IdSoggetto.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AccordoServizioParteComuneServizioComposto }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "accordo-servizio-parte-comune-servizio-composto")
    public JAXBElement<AccordoServizioParteComuneServizioComposto> createAccordoServizioParteComuneServizioComposto(AccordoServizioParteComuneServizioComposto value) {
        return new JAXBElement<AccordoServizioParteComuneServizioComposto>(ObjectFactory._AccordoServizioParteComuneServizioComposto_QNAME, AccordoServizioParteComuneServizioComposto.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WrapperIdAccordoCooperazione }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "wrapperIdAccordoCooperazione")
    public JAXBElement<WrapperIdAccordoCooperazione> createWrapperIdAccordoCooperazione(WrapperIdAccordoCooperazione value) {
        return new JAXBElement<WrapperIdAccordoCooperazione>(ObjectFactory._WrapperIdAccordoCooperazione_QNAME, WrapperIdAccordoCooperazione.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WrapperIdAccordoServizioParteComune }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "wrapperIdAccordoServizioParteComune")
    public JAXBElement<WrapperIdAccordoServizioParteComune> createWrapperIdAccordoServizioParteComune(WrapperIdAccordoServizioParteComune value) {
        return new JAXBElement<WrapperIdAccordoServizioParteComune>(ObjectFactory._WrapperIdAccordoServizioParteComune_QNAME, WrapperIdAccordoServizioParteComune.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WrapperIdPortaDominio }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "wrapperIdPortaDominio")
    public JAXBElement<WrapperIdPortaDominio> createWrapperIdPortaDominio(WrapperIdPortaDominio value) {
        return new JAXBElement<WrapperIdPortaDominio>(ObjectFactory._WrapperIdPortaDominio_QNAME, WrapperIdPortaDominio.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WrapperIdSoggetto }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "wrapperIdSoggetto")
    public JAXBElement<WrapperIdSoggetto> createWrapperIdSoggetto(WrapperIdSoggetto value) {
        return new JAXBElement<WrapperIdSoggetto>(ObjectFactory._WrapperIdSoggetto_QNAME, WrapperIdSoggetto.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WrapperIdAccordoServizioParteSpecifica }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "wrapperIdAccordoServizioParteSpecifica")
    public JAXBElement<WrapperIdAccordoServizioParteSpecifica> createWrapperIdAccordoServizioParteSpecifica(WrapperIdAccordoServizioParteSpecifica value) {
        return new JAXBElement<WrapperIdAccordoServizioParteSpecifica>(ObjectFactory._WrapperIdAccordoServizioParteSpecifica_QNAME, WrapperIdAccordoServizioParteSpecifica.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WrapperIdRuolo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "wrapperIdRuolo")
    public JAXBElement<WrapperIdRuolo> createWrapperIdRuolo(WrapperIdRuolo value) {
        return new JAXBElement<WrapperIdRuolo>(ObjectFactory._WrapperIdRuolo_QNAME, WrapperIdRuolo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegistryServiceException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "registry-service-exception")
    public JAXBElement<RegistryServiceException> createRegistryServiceException(RegistryServiceException value) {
        return new JAXBElement<RegistryServiceException>(ObjectFactory._RegistryServiceException_QNAME, RegistryServiceException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegistryNotFoundException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "registry-not-found-exception")
    public JAXBElement<RegistryNotFoundException> createRegistryNotFoundException(RegistryNotFoundException value) {
        return new JAXBElement<RegistryNotFoundException>(ObjectFactory._RegistryNotFoundException_QNAME, RegistryNotFoundException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegistryMultipleResultException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "registry-multiple-result-exception")
    public JAXBElement<RegistryMultipleResultException> createRegistryMultipleResultException(RegistryMultipleResultException value) {
        return new JAXBElement<RegistryMultipleResultException>(ObjectFactory._RegistryMultipleResultException_QNAME, RegistryMultipleResultException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegistryNotImplementedException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "registry-not-implemented-exception")
    public JAXBElement<RegistryNotImplementedException> createRegistryNotImplementedException(RegistryNotImplementedException value) {
        return new JAXBElement<RegistryNotImplementedException>(ObjectFactory._RegistryNotImplementedException_QNAME, RegistryNotImplementedException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegistryNotAuthorizedException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "registry-not-authorized-exception")
    public JAXBElement<RegistryNotAuthorizedException> createRegistryNotAuthorizedException(RegistryNotAuthorizedException value) {
        return new JAXBElement<RegistryNotAuthorizedException>(ObjectFactory._RegistryNotAuthorizedException_QNAME, RegistryNotAuthorizedException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Create }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "create")
    public JAXBElement<Create> createCreate(Create value) {
        return new JAXBElement<Create>(ObjectFactory._Create_QNAME, Create.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "createResponse")
    public JAXBElement<CreateResponse> createCreateResponse(CreateResponse value) {
        return new JAXBElement<CreateResponse>(ObjectFactory._CreateResponse_QNAME, CreateResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Update }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "update")
    public JAXBElement<Update> createUpdate(Update value) {
        return new JAXBElement<Update>(ObjectFactory._Update_QNAME, Update.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "updateResponse")
    public JAXBElement<UpdateResponse> createUpdateResponse(UpdateResponse value) {
        return new JAXBElement<UpdateResponse>(ObjectFactory._UpdateResponse_QNAME, UpdateResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateOrCreate }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "updateOrCreate")
    public JAXBElement<UpdateOrCreate> createUpdateOrCreate(UpdateOrCreate value) {
        return new JAXBElement<UpdateOrCreate>(ObjectFactory._UpdateOrCreate_QNAME, UpdateOrCreate.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateOrCreateResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "updateOrCreateResponse")
    public JAXBElement<UpdateOrCreateResponse> createUpdateOrCreateResponse(UpdateOrCreateResponse value) {
        return new JAXBElement<UpdateOrCreateResponse>(ObjectFactory._UpdateOrCreateResponse_QNAME, UpdateOrCreateResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteById }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "deleteById")
    public JAXBElement<DeleteById> createDeleteById(DeleteById value) {
        return new JAXBElement<DeleteById>(ObjectFactory._DeleteById_QNAME, DeleteById.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteByIdResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "deleteByIdResponse")
    public JAXBElement<DeleteByIdResponse> createDeleteByIdResponse(DeleteByIdResponse value) {
        return new JAXBElement<DeleteByIdResponse>(ObjectFactory._DeleteByIdResponse_QNAME, DeleteByIdResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteAll }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "deleteAll")
    public JAXBElement<DeleteAll> createDeleteAll(DeleteAll value) {
        return new JAXBElement<DeleteAll>(ObjectFactory._DeleteAll_QNAME, DeleteAll.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteAllResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "deleteAllResponse")
    public JAXBElement<DeleteAllResponse> createDeleteAllResponse(DeleteAllResponse value) {
        return new JAXBElement<DeleteAllResponse>(ObjectFactory._DeleteAllResponse_QNAME, DeleteAllResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteAllByFilter }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "deleteAllByFilter")
    public JAXBElement<DeleteAllByFilter> createDeleteAllByFilter(DeleteAllByFilter value) {
        return new JAXBElement<DeleteAllByFilter>(ObjectFactory._DeleteAllByFilter_QNAME, DeleteAllByFilter.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteAllByFilterResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "deleteAllByFilterResponse")
    public JAXBElement<DeleteAllByFilterResponse> createDeleteAllByFilterResponse(DeleteAllByFilterResponse value) {
        return new JAXBElement<DeleteAllByFilterResponse>(ObjectFactory._DeleteAllByFilterResponse_QNAME, DeleteAllByFilterResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Delete }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "delete")
    public JAXBElement<Delete> createDelete(Delete value) {
        return new JAXBElement<Delete>(ObjectFactory._Delete_QNAME, Delete.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/registry/management", name = "deleteResponse")
    public JAXBElement<DeleteResponse> createDeleteResponse(DeleteResponse value) {
        return new JAXBElement<DeleteResponse>(ObjectFactory._DeleteResponse_QNAME, DeleteResponse.class, null, value);
    }

}
