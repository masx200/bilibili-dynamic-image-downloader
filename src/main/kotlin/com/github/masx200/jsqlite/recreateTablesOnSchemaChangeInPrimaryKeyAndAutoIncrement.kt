package com.github.masx200.jsqlite

fun recreateTablesOnSchemaChangeInPrimaryKeyAndAutoIncrement(db: DB, vararg list: Class<*>): List<String> {
    var resultList = mutableListOf<String>()
    for (klass in list) {


        if (db.checkTableDifferenceInPrimaryKeyAndAutoIncrement(klass)) {
            db.drop(klass).forEach {
                resultList.add(it)
            }

            db.create(klass).forEach {
                resultList.add(it)
            }

        }
    }
    return resultList
}