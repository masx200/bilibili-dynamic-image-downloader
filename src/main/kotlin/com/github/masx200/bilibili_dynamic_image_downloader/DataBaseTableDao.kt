package com.github.masx200.bilibili_dynamic_image_downloader

//import com.sun.tools.javac.tree.TreeInfo.args
import com.github.masx200.jsqlite.DB
import com.github.masx200.jsqlite.DataSupport
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder

class DataBaseTableDao<T : DataSupport<T>>(
    var database: DB, var entityClass: Class<T>,
) {
    fun insert(data1: T) {
        database.insert<T>(data1)
    }
    fun findOne(id: Long): T? {

        return database.findOne<T>(entityClass, id)
    }

    fun findOne(block: SqlExpressionBuilder.() -> Op<Boolean>): T? {
        val condition = Op.build(block)

//        println(database)
//        println(entityClass)
        val predicate = condition.toString() //
//        println(predicate)
        return database.findOne<T>(entityClass, predicate)
    }

    // 新增的 update 方法
    fun update(data: T, block: SqlExpressionBuilder.() -> Op<Boolean>) {
        val condition = Op.build(block)

//        println(database)
//        println(entityClass)
        val predicate = condition.toString() //
        database.update(data, predicate)
    }
    // 新增的 update 方法
    fun update(data: T) {
        if (data.id == null) {
            throw IllegalArgumentException("The entity must have an id to be updated.")
        }
        database.update(data)
    }

}