-- **** Messaggi ****

CREATE TABLE MESSAGGI
(
	ID_MESSAGGIO VARCHAR2(255) NOT NULL,
	TIPO VARCHAR2(255) NOT NULL,
	RIFERIMENTO_MSG VARCHAR2(255),
	ERRORE_PROCESSAMENTO CLOB,
	-- data dalla quale il msg puo' essere rispedito in caso di errori
	RISPEDIZIONE TIMESTAMP NOT NULL,
	ORA_REGISTRAZIONE TIMESTAMP NOT NULL,
	PROPRIETARIO VARCHAR2(255),
	-- le colonne seguenti servono per il servizio di TransactionManager
	MOD_RICEZ_CONT_APPLICATIVI VARCHAR2(255),
	MOD_RICEZ_BUSTE VARCHAR2(255),
	MOD_INOLTRO_BUSTE VARCHAR2(255),
	MOD_INOLTRO_RISPOSTE VARCHAR2(255),
	MOD_IMBUSTAMENTO VARCHAR2(255),
	MOD_IMBUSTAMENTO_RISPOSTE VARCHAR2(255),
	MOD_SBUSTAMENTO VARCHAR2(255),
	MOD_SBUSTAMENTO_RISPOSTE VARCHAR2(255),
	-- Thread Pool:impedisce la gestione di messaggi gia schedulati
	SCHEDULING NUMBER,
	-- permette la riconsegna del messaggio dopo tot tempo
	REDELIVERY_DELAY TIMESTAMP NOT NULL,
	-- numero di riconsegne effettuate
	REDELIVERY_COUNT NUMBER,
	-- id del nodo del cluster che deve gestire questo messaggio.
	CLUSTER_ID VARCHAR2(255),
	-- memorizza l'ora in cui il messaggio e stato schedulato la prima volta
	SCHEDULING_TIME TIMESTAMP,
	-- contiene un messaggio serializzato
	MSG_BYTES BLOB,
	CORRELAZIONE_APPLICATIVA VARCHAR2(255),
	CORRELAZIONE_RISPOSTA VARCHAR2(255),
	PROTOCOLLO VARCHAR2(255) NOT NULL,
	id_transazione VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	-- check constraints
	CONSTRAINT chk_MESSAGGI_1 CHECK (TIPO IN ('INBOX','OUTBOX')),
	-- fk/pk keys constraints
	CONSTRAINT pk_MESSAGGI PRIMARY KEY (ID_MESSAGGIO,TIPO)
);

-- index
CREATE INDEX MESSAGGI_TRANS ON MESSAGGI (id_transazione,TIPO);
CREATE INDEX MESSAGGI_SEARCH ON MESSAGGI (ORA_REGISTRAZIONE,RIFERIMENTO_MSG,TIPO,PROPRIETARIO);
CREATE INDEX MESSAGGI_RIFMSG ON MESSAGGI (RIFERIMENTO_MSG);
CREATE INDEX MESSAGGI_TESTSUITE ON MESSAGGI (PROPRIETARIO,ID_MESSAGGIO,RIFERIMENTO_MSG);

ALTER TABLE MESSAGGI MODIFY SCHEDULING DEFAULT 0;
ALTER TABLE MESSAGGI MODIFY REDELIVERY_COUNT DEFAULT 0;


CREATE TABLE MSG_SERVIZI_APPLICATIVI
(
	ID_MESSAGGIO VARCHAR2(255) NOT NULL,
	TIPO VARCHAR2(255) NOT NULL,
	SERVIZIO_APPLICATIVO VARCHAR2(255) NOT NULL,
	SBUSTAMENTO_SOAP NUMBER NOT NULL,
	SBUSTAMENTO_INFO_PROTOCOL NUMBER NOT NULL,
	INTEGRATION_MANAGER NUMBER NOT NULL,
	MOD_CONSEGNA_CONT_APPLICATIVI VARCHAR2(255),
	-- Assume il valore 'Connettore' se la consegna avviente tramite un connettore,
	-- 'ConnectionReply' se viene ritornato tramite connectionReply,
	-- 'IntegrationManager' se e' solo ottenibile tramite IntegrationManager
	TIPO_CONSEGNA VARCHAR2(255) NOT NULL,
	ERRORE_PROCESSAMENTO CLOB,
	-- data dalla quale il msg puo' essere rispedito in caso di errori
	RISPEDIZIONE TIMESTAMP NOT NULL,
	NOME_PORTA VARCHAR2(255),
	-- fk/pk columns
	-- check constraints
	CONSTRAINT chk_MSG_SERVIZI_APPLICATIVI_1 CHECK (TIPO IN ('INBOX','OUTBOX')),
	CONSTRAINT chk_MSG_SERVIZI_APPLICATIVI_2 CHECK (TIPO_CONSEGNA IN ('Connettore','ConnectionReply','IntegrationManager')),
	-- fk/pk keys constraints
	CONSTRAINT fk_MSG_SERVIZI_APPLICATIVI_1 FOREIGN KEY (ID_MESSAGGIO,TIPO) REFERENCES MESSAGGI(ID_MESSAGGIO,TIPO) ON DELETE CASCADE,
	CONSTRAINT pk_MSG_SERVIZI_APPLICATIVI PRIMARY KEY (ID_MESSAGGIO,SERVIZIO_APPLICATIVO)
);

-- index
CREATE INDEX MSG_SERV_APPL_LIST ON MSG_SERVIZI_APPLICATIVI (SERVIZIO_APPLICATIVO,INTEGRATION_MANAGER);
CREATE INDEX MSG_SERV_APPL_TIMEOUT ON MSG_SERVIZI_APPLICATIVI (ID_MESSAGGIO,TIPO_CONSEGNA,INTEGRATION_MANAGER);

ALTER TABLE MSG_SERVIZI_APPLICATIVI MODIFY TIPO DEFAULT 'INBOX';


CREATE TABLE DEFINIZIONE_MESSAGGI
(
	ID_MESSAGGIO VARCHAR2(255) NOT NULL,
	TIPO VARCHAR2(255) NOT NULL,
	CONTENT_TYPE VARCHAR2(255) NOT NULL,
	MSG_BYTES BLOB,
	MSG_CONTEXT BLOB,
	-- fk/pk columns
	-- check constraints
	CONSTRAINT chk_DEFINIZIONE_MESSAGGI_1 CHECK (TIPO IN ('INBOX','OUTBOX')),
	-- fk/pk keys constraints
	CONSTRAINT fk_DEFINIZIONE_MESSAGGI_1 FOREIGN KEY (ID_MESSAGGIO,TIPO) REFERENCES MESSAGGI(ID_MESSAGGIO,TIPO) ON DELETE CASCADE,
	CONSTRAINT pk_DEFINIZIONE_MESSAGGI PRIMARY KEY (ID_MESSAGGIO,TIPO)
);


-- **** Correlazione Applicativa ****

CREATE SEQUENCE seq_CORRELAZIONE_APPLICATIVA MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 CYCLE;

CREATE TABLE CORRELAZIONE_APPLICATIVA
(
	ID_MESSAGGIO VARCHAR2(255) NOT NULL,
	ID_APPLICATIVO VARCHAR2(255) NOT NULL,
	SERVIZIO_APPLICATIVO VARCHAR2(255) NOT NULL,
	TIPO_MITTENTE VARCHAR2(255) NOT NULL,
	MITTENTE VARCHAR2(255) NOT NULL,
	TIPO_DESTINATARIO VARCHAR2(255) NOT NULL,
	DESTINATARIO VARCHAR2(255) NOT NULL,
	TIPO_SERVIZIO VARCHAR2(255),
	SERVIZIO VARCHAR2(255),
	VERSIONE_SERVIZIO NUMBER NOT NULL,
	AZIONE VARCHAR2(255),
	SCADENZA TIMESTAMP,
	ora_registrazione TIMESTAMP NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT pk_CORRELAZIONE_APPLICATIVA PRIMARY KEY (id)
);

-- index
CREATE INDEX CORR_APPL_SCADUTE ON CORRELAZIONE_APPLICATIVA (SCADENZA);
CREATE INDEX CORR_APPL_OLD ON CORRELAZIONE_APPLICATIVA (ora_registrazione);

ALTER TABLE CORRELAZIONE_APPLICATIVA MODIFY ora_registrazione DEFAULT CURRENT_TIMESTAMP;

CREATE TRIGGER trg_CORRELAZIONE_APPLICATIVA
BEFORE
insert on CORRELAZIONE_APPLICATIVA
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_CORRELAZIONE_APPLICATIVA.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


