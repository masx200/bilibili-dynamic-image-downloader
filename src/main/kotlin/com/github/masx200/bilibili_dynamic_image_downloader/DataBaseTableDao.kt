package com.github.masx200.bilibili_dynamic_image_downloader

//import com.sun.tools.javac.tree.TreeInfo.args
import com.github.masx200.jsqlite.DB
import com.github.masx200.jsqlite.DataSupport
import com.github.masx200.jsqlite.Options
import java.util.function.Consumer
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder

class DataBaseTableDao<T : DataSupport<T>>(
    var database: DB, var entityClass: Class<T>,
) {
//    fun getAsyncEventBus(identifier: String): AsyncEventBus {
//        return database.getAsyncEventBus(identifier)
//    }

    fun deleteAll(): List<String> {
        return database.deleteAll<T>(entityClass)
    }

    fun delete(vararg ids: Long): List<String> {
        return database.deleteByVarargId<T>(entityClass, *ids)
    }

    fun delete(ids: List<Long>): List<String> {
        return database.deleteByListId<T>(entityClass, ids)
    }

    fun delete(block: SqlExpressionBuilder.() -> Op<Boolean>): List<String> {
        val condition = Op.build(block)
        val predicate = condition.toString()
        return database.deleteByPredicate<T>(entityClass, predicate)
    }

    fun insert(data1: T): List<String> {
        return database.insert<T>(data1)
    }

    fun findOne(id: Long): T? {

        return database.findOneById<T>(entityClass, id)
    }

    fun findOne(block: SqlExpressionBuilder.() -> Op<Boolean>): T? {
        val condition = Op.build(block)

//        println(database)
//        println(entityClass)
        val predicate = condition.toString() //
//        println(predicate)
        return database.findOneByPredicate<T>(entityClass, predicate)
    }

    // 新增的 update 方法
    fun update(data: T, block: SqlExpressionBuilder.() -> Op<Boolean>): List<String> {
        val condition = Op.build(block)

//        println(database)
//        println(entityClass)
        val predicate = condition.toString() //
        return database.updateByPredicate(data, predicate)
    }

    // 新增的 update 方法
    fun update(data: T): List<String> {
        if (data.id == null) {
            throw IllegalArgumentException("The entity must have an id to be updated.")
        }
        return database.updateById(data)
    }

    fun find(consumer: Consumer<Options>): List<T> {
        return database.findByConsumer<T>(entityClass, consumer)
    }

    fun find(ids: List<Long>): List<T> {
        return database.findByListId<T>(entityClass, ids)
    }

    fun find(vararg ids: Long): List<T> {
        return database.findByVarargId<T>(entityClass, *ids)
    }

    fun findAll(): List<T> {
        return database.findAll<T>(entityClass)
    }
}