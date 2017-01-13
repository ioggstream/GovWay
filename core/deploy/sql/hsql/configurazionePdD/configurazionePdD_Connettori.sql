-- **** Connettori ****

CREATE SEQUENCE seq_connettori AS BIGINT START WITH 1 INCREMENT BY 1 ; -- (Scommentare in hsql 2.x) NO CYCLE;

CREATE TABLE connettori
(
	-- (disabilitato,http,jms)
	endpointtype VARCHAR(255) NOT NULL,
	nome_connettore VARCHAR(255) NOT NULL,
	-- url nel caso http
	url VARCHAR(255),
	-- nel caso di http indicazione se usare chunking
	transfer_mode VARCHAR(255),
	transfer_mode_chunk_size INT,
	-- nel caso di http indicazione se seguire il redirect o meno
	redirect_mode VARCHAR(255),
	redirect_max_hop INT,
	-- nome coda jms
	nome VARCHAR(255),
	-- tipo coda jms (queue,topic)
	tipo VARCHAR(255),
	-- utente di una connessione jms
	utente VARCHAR(255),
	-- password per una connessione jms
	password VARCHAR(255),
	-- context property: initial_content
	initcont VARCHAR(255),
	-- context property: url_pkg
	urlpkg VARCHAR(255),
	-- context property: provider_url
	provurl VARCHAR(255),
	-- ConnectionFactory JMS
	connection_factory VARCHAR(255),
	-- Messaggio JMS inviato come text/byte message
	send_as VARCHAR(255),
	-- 1/0 (true/false) abilita il debug tramite il connettore
	debug INT,
	-- 1/0 (true/false) abilita l'utilizzo di un proxy tramite il connettore
	proxy INT,
	proxy_type VARCHAR(255),
	proxy_hostname VARCHAR(255),
	proxy_port VARCHAR(255),
	proxy_username VARCHAR(255),
	proxy_password VARCHAR(255),
	-- 1/0 (true/false) indica se il connettore e' gestito tramite le proprieta' custom
	custom INT,
	-- fk/pk columns
	id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1),
	-- unique constraints
	CONSTRAINT unique_connettori_1 UNIQUE (nome_connettore),
	-- fk/pk keys constraints
	CONSTRAINT pk_connettori PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_connettori_1 ON connettori (nome_connettore);

ALTER TABLE connettori ALTER COLUMN debug SET DEFAULT 0;
ALTER TABLE connettori ALTER COLUMN proxy SET DEFAULT 0;
ALTER TABLE connettori ALTER COLUMN custom SET DEFAULT 0;

CREATE TABLE connettori_init_seq (id BIGINT);
INSERT INTO connettori_init_seq VALUES (NEXT VALUE FOR seq_connettori);



CREATE SEQUENCE seq_connettori_custom AS BIGINT START WITH 1 INCREMENT BY 1 ; -- (Scommentare in hsql 2.x) NO CYCLE;

CREATE TABLE connettori_custom
(
	name VARCHAR(255) NOT NULL,
	value VARCHAR(255) NOT NULL,
	id_connettore BIGINT NOT NULL,
	-- fk/pk columns
	id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1),
	-- unique constraints
	CONSTRAINT unique_connettori_custom_1 UNIQUE (id_connettore,name,value),
	-- fk/pk keys constraints
	CONSTRAINT fk_connettori_custom_1 FOREIGN KEY (id_connettore) REFERENCES connettori(id),
	CONSTRAINT pk_connettori_custom PRIMARY KEY (id)
);

CREATE TABLE connettori_custom_init_seq (id BIGINT);
INSERT INTO connettori_custom_init_seq VALUES (NEXT VALUE FOR seq_connettori_custom);



CREATE SEQUENCE seq_connettori_properties AS BIGINT START WITH 1 INCREMENT BY 1 ; -- (Scommentare in hsql 2.x) NO CYCLE;

CREATE TABLE connettori_properties
(
	-- nome connettore personalizzato attraverso file properties
	nome_connettore VARCHAR(255) NOT NULL,
	-- location del file properties
	path VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1),
	-- unique constraints
	CONSTRAINT unique_connettori_properties_1 UNIQUE (nome_connettore),
	-- fk/pk keys constraints
	CONSTRAINT pk_connettori_properties PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_connettori_properties_1 ON connettori_properties (nome_connettore);
CREATE TABLE connettori_properties_init_seq (id BIGINT);
INSERT INTO connettori_properties_init_seq VALUES (NEXT VALUE FOR seq_connettori_properties);


