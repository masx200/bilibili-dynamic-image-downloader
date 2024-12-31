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
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    dynamicid INTEGER,
    picturesrc TEXT,
    userid TEXT,
    dynamicoriginid INTEGER,
    createdat INTEGER,
    updatedat INTEGER
);

-- 表：dynamicranges
DROP TABLE IF EXISTS dynamicranges;

CREATE TABLE IF NOT EXISTS dynamicranges (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    userid TEXT,
    endwith_dynamic_id INTEGER,
    offset_dynamic_id INTEGER,
    earliestdynamicid INTEGER,
    createdat INTEGER,
    updatedat INTEGER
);

-- 表：spacehistory
DROP TABLE IF EXISTS spacehistory;

CREATE TABLE IF NOT EXISTS spacehistory (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    userid TEXT,
    dynamictype INTEGER,
    dynamicid INTEGER,
    dynamicorigintype INTEGER,
    dynamicoriginid INTEGER,
    createdat INTEGER,
    updatedat INTEGER
);

COMMIT TRANSACTION;

PRAGMA foreign_keys = on;

BEGIN TRANSACTION;

CREATE INDEX idx_dynamicpictures_dynamicid on dynamicpictures (dynamicid);

CREATE INDEX idx_dynamicpictures_dynamicoriginid on dynamicpictures (dynamicoriginid);

CREATE INDEX idx_dynamicpictures_picturesrc on dynamicpictures (picturesrc);

CREATE INDEX idx_dynamicpictures_userid on dynamicpictures (userid);

CREATE INDEX idx_dynamicranges_earliestdynamicid on dynamicranges (earliestdynamicid);

CREATE INDEX idx_dynamicranges_endwith_dynamic_id on dynamicranges (endwith_dynamic_id);

CREATE INDEX idx_dynamicranges_offset_dynamic_id on dynamicranges (offset_dynamic_id);

CREATE INDEX idx_dynamicranges_userid on dynamicranges (userid);

CREATE INDEX idx_spacehistory_dynamicid on spacehistory (dynamicid);

CREATE INDEX idx_spacehistory_dynamicoriginid on spacehistory (dynamicoriginid);

CREATE INDEX idx_spacehistory_dynamicorigintype on spacehistory (dynamicorigintype);

CREATE INDEX idx_spacehistory_dynamictype on spacehistory (dynamictype);

CREATE INDEX idx_spacehistory_userid on spacehistory (userid);

COMMIT;