ALTER TABLE soggetti ADD COLUMN is_default INT DEFAULT 0;

ALTER TABLE configurazione ADD COLUMN multitenant_stato VARCHAR(255);
ALTER TABLE configurazione ADD COLUMN multitenant_fruizioni VARCHAR(255);
ALTER TABLE configurazione ADD COLUMN multitenant_erogazioni VARCHAR(255);


CREATE SEQUENCE seq_porte_applicative_sa_auth start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE porte_applicative_sa_auth
(
        id_porta BIGINT NOT NULL,
        id_servizio_applicativo BIGINT NOT NULL,
        -- fk/pk columns
        id BIGINT DEFAULT nextval('seq_porte_applicative_sa_auth') NOT NULL,
        -- unique constraints
        CONSTRAINT uniq_pa_sa_auth_1 UNIQUE (id_porta,id_servizio_applicativo),
        -- fk/pk keys constraints
        CONSTRAINT fk_porte_applicative_sa_auth_1 FOREIGN KEY (id_servizio_applicativo) REFERENCES servizi_applicativi(id),
        CONSTRAINT fk_porte_applicative_sa_auth_2 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
        CONSTRAINT pk_porte_applicative_sa_auth PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PA_SA_AUTH ON porte_applicative_sa_auth (id_porta);



