-- **** Porte Applicative ****

CREATE SEQUENCE seq_porte_applicative MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE porte_applicative
(
	nome_porta VARCHAR2(4000) NOT NULL,
	descrizione VARCHAR2(255),
	-- Soggetto Virtuale
	id_soggetto_virtuale NUMBER,
	tipo_soggetto_virtuale VARCHAR2(255),
	nome_soggetto_virtuale VARCHAR2(255),
	-- Servizio
	id_servizio NUMBER,
	tipo_servizio VARCHAR2(255) NOT NULL,
	servizio VARCHAR2(255) NOT NULL,
	versione_servizio NUMBER NOT NULL,
	id_accordo NUMBER,
	id_port_type NUMBER,
	-- azione
	azione VARCHAR2(255),
	mode_azione VARCHAR2(255),
	pattern_azione VARCHAR2(255),
	nome_porta_delegante_azione VARCHAR2(255),
	-- abilitato/disabilitato
	force_interface_based_azione VARCHAR2(255),
	-- disable/packaging/unpackaging/verify
	mtom_request_mode VARCHAR2(255),
	-- disable/packaging/unpackaging/verify
	mtom_response_mode VARCHAR2(255),
	-- abilitato/disabilitato (se abilitato le WSSproperties sono presenti nelle tabelle ...._security_request/response)
	security VARCHAR2(255),
	-- abilitato/disabilitato
	security_mtom_req VARCHAR2(255),
	-- abilitato/disabilitato
	security_mtom_res VARCHAR2(255),
	security_request_mode VARCHAR2(255),
	security_response_mode VARCHAR2(255),
	-- abilitato/disabilitato
	ricevuta_asincrona_sim VARCHAR2(255),
	-- abilitato/disabilitato
	ricevuta_asincrona_asim VARCHAR2(255),
	-- abilitato/disabilitato/warningOnly
	validazione_contenuti_stato VARCHAR2(255),
	-- wsdl/interface/xsd
	validazione_contenuti_tipo VARCHAR2(255),
	-- abilitato/disabilitato
	validazione_contenuti_mtom VARCHAR2(255),
	-- lista di tipi separati dalla virgola
	integrazione VARCHAR2(255),
	-- scadenza correlazione applicativa
	scadenza_correlazione_appl VARCHAR2(255),
	-- abilitato/disabilitato
	allega_body VARCHAR2(255),
	-- abilitato/disabilitato
	scarta_body VARCHAR2(255),
	-- abilitato/disabilitato
	gestione_manifest VARCHAR2(255),
	-- abilitato/disabilitato
	stateless VARCHAR2(255),
	behaviour VARCHAR2(255),
	-- Controllo Accessi
	autenticazione VARCHAR2(255),
	-- abilitato/disabilitato
	autenticazione_opzionale VARCHAR2(255),
	-- Gestione Token
	token_policy VARCHAR2(255),
	token_opzionale VARCHAR2(255),
	token_validazione VARCHAR2(255),
	token_introspection VARCHAR2(255),
	token_user_info VARCHAR2(255),
	token_forward VARCHAR2(255),
	token_options VARCHAR2(4000),
	token_authn_issuer VARCHAR2(255),
	token_authn_client_id VARCHAR2(255),
	token_authn_subject VARCHAR2(255),
	token_authn_username VARCHAR2(255),
	token_authn_email VARCHAR2(255),
	-- Autorizzazione
	autorizzazione VARCHAR2(255),
	autorizzazione_xacml CLOB,
	autorizzazione_contenuto VARCHAR2(255),
	-- all/any
	ruoli_match VARCHAR2(255),
	scope_stato VARCHAR2(255),
	-- all/any
	scope_match VARCHAR2(255),
	-- abilitato/disabilitato
	ricerca_porta_azione_delegata VARCHAR2(255),
	-- Livello Log Messaggi Diagnostici
	msg_diag_severita VARCHAR2(255),
	tracciamento_esiti VARCHAR2(255),
	-- abilitato/disabilitato
	stato VARCHAR2(255),
	-- proprietario porta applicativa
	id_soggetto NUMBER NOT NULL,
	ora_registrazione TIMESTAMP,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_porte_applicative_1 UNIQUE (nome_porta),
	-- fk/pk keys constraints
	CONSTRAINT fk_porte_applicative_1 FOREIGN KEY (id_soggetto) REFERENCES soggetti(id),
	CONSTRAINT pk_porte_applicative PRIMARY KEY (id)
);


ALTER TABLE porte_applicative MODIFY versione_servizio DEFAULT 1;
ALTER TABLE porte_applicative MODIFY ora_registrazione DEFAULT CURRENT_TIMESTAMP;

CREATE TRIGGER trg_porte_applicative
BEFORE
insert on porte_applicative
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_porte_applicative.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_porte_applicative_sa MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE porte_applicative_sa
(
	id_porta NUMBER NOT NULL,
	id_servizio_applicativo NUMBER NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_porte_applicative_sa_1 UNIQUE (id_porta,id_servizio_applicativo),
	-- fk/pk keys constraints
	CONSTRAINT fk_porte_applicative_sa_1 FOREIGN KEY (id_servizio_applicativo) REFERENCES servizi_applicativi(id),
	CONSTRAINT fk_porte_applicative_sa_2 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_porte_applicative_sa PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PA_SA ON porte_applicative_sa (id_porta);
CREATE TRIGGER trg_porte_applicative_sa
BEFORE
insert on porte_applicative_sa
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_porte_applicative_sa.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_pa_properties MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pa_properties
(
	id_porta NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	valore VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_pa_properties_1 UNIQUE (id_porta,nome,valore),
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_properties_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_properties PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PA_PROP ON pa_properties (id_porta);
CREATE TRIGGER trg_pa_properties
BEFORE
insert on pa_properties
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pa_properties.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_pa_mtom_request MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pa_mtom_request
(
	id_porta NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	pattern CLOB NOT NULL,
	content_type VARCHAR2(255),
	required NUMBER NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_mtom_request_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_mtom_request PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PA_MTOMTREQ ON pa_mtom_request (id_porta);
CREATE TRIGGER trg_pa_mtom_request
BEFORE
insert on pa_mtom_request
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pa_mtom_request.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_pa_mtom_response MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pa_mtom_response
(
	id_porta NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	pattern CLOB NOT NULL,
	content_type VARCHAR2(255),
	required NUMBER NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_mtom_response_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_mtom_response PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PA_MTOMTRES ON pa_mtom_response (id_porta);
CREATE TRIGGER trg_pa_mtom_response
BEFORE
insert on pa_mtom_response
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pa_mtom_response.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_pa_security_request MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pa_security_request
(
	id_porta NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	valore CLOB NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_security_request_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_security_request PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PA_WSSREQ ON pa_security_request (id_porta);
CREATE TRIGGER trg_pa_security_request
BEFORE
insert on pa_security_request
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pa_security_request.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_pa_security_response MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pa_security_response
(
	id_porta NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	valore CLOB NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_security_response_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_security_response PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PA_WSSRES ON pa_security_response (id_porta);
CREATE TRIGGER trg_pa_security_response
BEFORE
insert on pa_security_response
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pa_security_response.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_pa_correlazione MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pa_correlazione
(
	id_porta NUMBER NOT NULL,
	nome_elemento VARCHAR2(255),
	-- modalita di scelta user input, content-based, url-based, disabilitato
	mode_correlazione VARCHAR2(255),
	-- pattern utilizzato solo per content/url based
	pattern CLOB,
	-- blocca/accetta
	identificazione_fallita VARCHAR2(255),
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_correlazione_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_correlazione PRIMARY KEY (id)
);

CREATE TRIGGER trg_pa_correlazione
BEFORE
insert on pa_correlazione
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pa_correlazione.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_pa_correlazione_risposta MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pa_correlazione_risposta
(
	id_porta NUMBER NOT NULL,
	nome_elemento VARCHAR2(255),
	-- modalita di scelta user input, content-based, url-based, disabilitato
	mode_correlazione VARCHAR2(255),
	-- pattern utilizzato solo per content/url based
	pattern CLOB,
	-- blocca/accetta
	identificazione_fallita VARCHAR2(255),
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_correlazione_risposta_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_correlazione_risposta PRIMARY KEY (id)
);

CREATE TRIGGER trg_pa_correlazione_risposta
BEFORE
insert on pa_correlazione_risposta
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pa_correlazione_risposta.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_pa_ruoli MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pa_ruoli
(
	id_porta NUMBER NOT NULL,
	ruolo VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_pa_ruoli_1 UNIQUE (id_porta,ruolo),
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_ruoli_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_ruoli PRIMARY KEY (id)
);

CREATE TRIGGER trg_pa_ruoli
BEFORE
insert on pa_ruoli
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pa_ruoli.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_pa_scope MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pa_scope
(
	id_porta NUMBER NOT NULL,
	scope VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_pa_scope_1 UNIQUE (id_porta,scope),
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_scope_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_scope PRIMARY KEY (id)
);

CREATE TRIGGER trg_pa_scope
BEFORE
insert on pa_scope
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pa_scope.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_pa_soggetti MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pa_soggetti
(
	id_porta NUMBER NOT NULL,
	tipo_soggetto VARCHAR2(255) NOT NULL,
	nome_soggetto VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_pa_soggetti_1 UNIQUE (id_porta,tipo_soggetto,nome_soggetto),
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_soggetti_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_soggetti PRIMARY KEY (id)
);

CREATE TRIGGER trg_pa_soggetti
BEFORE
insert on pa_soggetti
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pa_soggetti.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_porte_applicative_sa_auth MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE porte_applicative_sa_auth
(
	id_porta NUMBER NOT NULL,
	id_servizio_applicativo NUMBER NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_pa_sa_auth_1 UNIQUE (id_porta,id_servizio_applicativo),
	-- fk/pk keys constraints
	CONSTRAINT fk_porte_applicative_sa_auth_1 FOREIGN KEY (id_servizio_applicativo) REFERENCES servizi_applicativi(id),
	CONSTRAINT fk_porte_applicative_sa_auth_2 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_porte_applicative_sa_auth PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PA_SA_AUTH ON porte_applicative_sa_auth (id_porta);
CREATE TRIGGER trg_porte_applicative_sa_auth
BEFORE
insert on porte_applicative_sa_auth
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_porte_applicative_sa_auth.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_pa_azioni MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pa_azioni
(
	id_porta NUMBER NOT NULL,
	azione VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_pa_azioni_1 UNIQUE (id_porta,azione),
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_azioni_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_azioni PRIMARY KEY (id)
);

CREATE TRIGGER trg_pa_azioni
BEFORE
insert on pa_azioni
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pa_azioni.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


