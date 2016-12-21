-- **** Protocol Properties ****

CREATE SEQUENCE seq_protocol_properties AS BIGINT START WITH 1 INCREMENT BY 1 ; -- (Scommentare in hsql 2.x) NO CYCLE;

CREATE TABLE protocol_properties
(
	-- tipoProprietario
	tipo_proprietario VARCHAR(255) NOT NULL,
	-- idOggettoProprietarioDocumento
	id_proprietario BIGINT NOT NULL,
	-- nome property
	name VARCHAR(255) NOT NULL,
	-- valore come stringa
	value_string VARCHAR(4000),
	-- valore come numero
	value_number BIGINT,
	-- valore true o false
	value_boolean INT,
	-- valore binario
	value_binary VARBINARY(16777215),
	file_name VARCHAR(4000),
	-- fk/pk columns
	id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1),
	-- unique constraints
	CONSTRAINT unique_protocol_properties_1 UNIQUE (tipo_proprietario,id_proprietario,name),
	-- fk/pk keys constraints
	CONSTRAINT pk_protocol_properties PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_protocol_properties_1 ON protocol_properties (tipo_proprietario,id_proprietario,name);
CREATE TABLE protocol_properties_init_seq (id BIGINT);
INSERT INTO protocol_properties_init_seq VALUES (NEXT VALUE FOR seq_protocol_properties);

