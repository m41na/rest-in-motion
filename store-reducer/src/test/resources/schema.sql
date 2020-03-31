DROP TABLE IF EXISTS tbl_todos;
create TABLE IF NOT EXISTS tbl_todos (
    id INT(20) IDENTITY NOT NULL ,
    task VARCHAR(100) NOT NULL,
    completed BOOLEAN default false,
    UNIQUE(task),
    PRIMARY KEY (id)
);

DROP TABLE IF EXISTS tbl_red_store;
CREATE TABLE IF NOT EXISTS tbl_red_store (
    data_id INT(20) IDENTITY NOT NULL,
    user_key VARCHAR(128) not null,
    collection_key VARCHAR(512) not null,
    data_value BLOB not null,
    date_created TIMESTAMP default CURRENT_TIMESTAMP,
    PRIMARY KEY (data_id)
);