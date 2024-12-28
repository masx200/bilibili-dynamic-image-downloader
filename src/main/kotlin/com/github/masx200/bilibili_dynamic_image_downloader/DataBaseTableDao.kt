package com.github.masx200.bilibili_dynamic_image_downloader

import com.github.artbits.jsqlite.DB
import com.github.artbits.jsqlite.DataSupport
import org.jetbrains.exposed.sql.Op

class DataBaseTableDao<T : DataSupport<T>>(
    var database: DB, var entityClass: Class<T>,
) {

    fun findOne(id: Long): T {

        return database.findOne<T>(entityClass, id)
    }

    fun findOne(condition: Op<Boolean>): T {
//        println(condition)
//        condition.print
        val predicate = TODO() as String
        val args = TODO() as Array<Any?>
        return database.findOne<T>(entityClass, predicate, *args)
    }
}