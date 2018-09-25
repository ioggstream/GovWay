ALTER TABLE users DROP COLUMN multi_tenant;
ALTER TABLE users ADD soggetto_pddconsole VARCHAR(255);
ALTER TABLE users ADD soggetto_pddmonitor VARCHAR(255);
CALL SYSPROC.ADMIN_CMD ('REORG TABLE users') ;
