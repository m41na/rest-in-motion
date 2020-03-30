DROP TABLE IF EXISTS tbl_todos;
create TABLE IF NOT EXISTS tbl_todos (
    id int(20) IDENTITY NOT NULL ,
    task varchar(100) NOT NULL,
    completed boolean default false,
    UNIQUE(task),
    PRIMARY KEY (id)
);