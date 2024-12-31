--
-- SQLiteStudio v3.4.13 生成的文件，周二 12月 31 23:35:40 2024
--
-- 所用的文本编码：System
--
PRAGMA foreign_keys = off;
BEGIN TRANSACTION;

-- 表：dynamicpictures
DROP TABLE IF EXISTS dynamicpictures;

CREATE TABLE IF NOT EXISTS dynamicpictures (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    dynamicid       INTEGER,
    picturesrc      TEXT,
    userid          TEXT,
    dynamicoriginid INTEGER,
    createdat       INTEGER,
    updatedat       INTEGER
);


-- 表：dynamicranges
DROP TABLE IF EXISTS dynamicranges;

CREATE TABLE IF NOT EXISTS dynamicranges (
    id                 INTEGER PRIMARY KEY AUTOINCREMENT,
    userid             TEXT,
    endwith_dynamic_id INTEGER,
    offset_dynamic_id  INTEGER,
    earliestdynamicid  INTEGER,
    createdat          INTEGER,
    updatedat          INTEGER
);


-- 表：spacehistory
DROP TABLE IF EXISTS spacehistory;

CREATE TABLE IF NOT EXISTS spacehistory (
    id                INTEGER PRIMARY KEY AUTOINCREMENT,
    userid            TEXT,
    dynamictype       INTEGER,
    dynamicid         INTEGER,
    dynamicorigintype INTEGER,
    dynamicoriginid   INTEGER,
    createdat         INTEGER,
    updatedat         INTEGER
);


COMMIT TRANSACTION;
PRAGMA foreign_keys = on;
