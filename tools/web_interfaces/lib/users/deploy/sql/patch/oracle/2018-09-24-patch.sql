ALTER TABLE users DROP COLUMN multi_tenant;
ALTER TABLE users ADD soggetto_pddconsole VARCHAR2(255);
ALTER TABLE users ADD soggetto_pddmonitor VARCHAR2(255);
ALTER TABLE users ADD soggetti_all INT;
ALTER TABLE users ADD servizi_all INT;
