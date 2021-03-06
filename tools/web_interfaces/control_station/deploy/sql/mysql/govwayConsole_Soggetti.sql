-- **** Soggetti ****

CREATE TABLE soggetti
(
	nome_soggetto VARCHAR(255) NOT NULL,
	tipo_soggetto VARCHAR(255) NOT NULL,
	descrizione VARCHAR(255),
	identificativo_porta VARCHAR(255),
	-- 1/0 (true/false) Indicazione se il soggetto svolge è quello di default per il protocollo
	is_default INT DEFAULT 0,
	-- 1/0 (true/false) svolge attivita di router
	is_router INT DEFAULT 0,
	id_connettore BIGINT NOT NULL,
	superuser VARCHAR(255),
	server VARCHAR(255),
	-- 1/0 (true/false) indica se il soggetto e' privato/pubblico
	privato INT DEFAULT 0,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	ora_registrazione TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3),
	profilo VARCHAR(255),
	codice_ipa VARCHAR(255) NOT NULL,
	tipoauth VARCHAR(255),
	utente VARCHAR(255),
	password VARCHAR(255),
	subject VARCHAR(255),
	pd_url_prefix_rewriter VARCHAR(255),
	pa_url_prefix_rewriter VARCHAR(255),
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_soggetti_1 UNIQUE (nome_soggetto,tipo_soggetto),
	CONSTRAINT unique_soggetti_2 UNIQUE (codice_ipa),
	-- fk/pk keys constraints
	CONSTRAINT fk_soggetti_1 FOREIGN KEY (id_connettore) REFERENCES connettori(id),
	CONSTRAINT pk_soggetti PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE UNIQUE INDEX index_soggetti_1 ON soggetti (nome_soggetto,tipo_soggetto);
CREATE UNIQUE INDEX index_soggetti_2 ON soggetti (codice_ipa);



CREATE TABLE soggetti_ruoli
(
	id_soggetto BIGINT NOT NULL,
	id_ruolo BIGINT NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_soggetti_ruoli_1 UNIQUE (id_soggetto,id_ruolo),
	-- fk/pk keys constraints
	CONSTRAINT fk_soggetti_ruoli_1 FOREIGN KEY (id_soggetto) REFERENCES soggetti(id),
	CONSTRAINT fk_soggetti_ruoli_2 FOREIGN KEY (id_ruolo) REFERENCES ruoli(id),
	CONSTRAINT pk_soggetti_ruoli PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE UNIQUE INDEX index_soggetti_ruoli_1 ON soggetti_ruoli (id_soggetto,id_ruolo);


