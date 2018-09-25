ALTER TABLE users DROP COLUMN multi_tenant;
ALTER TABLE users ADD COLUMN soggetto_pddconsole VARCHAR(255);
ALTER TABLE users ADD COLUMN soggetto_pddmonitor VARCHAR(255);
