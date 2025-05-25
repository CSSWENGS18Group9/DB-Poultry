-- Creating database and db_poultry user
-- these lines are PostgreSQL-flavored (i.e. these may not work on MySQL)
CREATE USER db_poultry WITH #####; -- replace ##### with password
CREATE DATABASE db_poultry OWNER db_poultry;
GRANT ALL PRIVILEGES ON DATABASE db_poultry TO db_poultry;
ALTER USER db_poultry WITH SUPERUSER;
