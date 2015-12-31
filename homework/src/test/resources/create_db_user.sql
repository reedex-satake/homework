-- ------------------------------------------------------------------------------
-- CREATE USER
-- ------------------------------------------------------------------------------
CREATE USER db_user@localhost IDENTIFIED BY 'db_pass';

-- ------------------------------------------------------------------------------
-- CREATE DATABASE
-- ------------------------------------------------------------------------------
CREATE DATABASE homework DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
GRANT ALL PRIVILEGES ON homework.* TO db_user@localhost IDENTIFIED BY 'db_pass';

CREATE DATABASE homework_ut DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
GRANT ALL PRIVILEGES ON homework_ut.* TO db_user@localhost IDENTIFIED BY 'db_pass';

