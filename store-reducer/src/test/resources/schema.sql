drop table IF EXISTS tbl_red_store;
create TABLE IF NOT EXISTS tbl_red_store (
    data_id VARCHAR(128) NOT NULL,
    user_key VARCHAR(128) not null,
    collection_key VARCHAR(512) not null,
    data_value BLOB not null,
    date_created TIMESTAMP default CURRENT_TIMESTAMP,
    PRIMARY KEY (data_id)
);