#!/usr/bin/env bash
echo "star the db server"
echo "cd $H2_HOME/bin"
java -cp h2-1.4.199.jar org.h2.tools.Server
echo "start an interactive shell"
java -cp h2-1.4.199.jar org.h2.tools.Shell
echo "execute queries"
echo "CREATE TABLE IF NOT EXISTS tbl_red_store (
    data_id INT(20) IDENTITY NOT NULL,
    user_key VARCHAR(128) not null,
    collection_key VARCHAR(512) not null,
    data_value BLOB not null,
    date_created TIMESTAMP default CURRENT_TIMESTAMP,
    PRIMARY KEY (data_id)
);"
echo "enter quit to exit shell"
