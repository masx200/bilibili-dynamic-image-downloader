/**
 * Copyright 2023 Zhang Guanhu
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.masx200.jsqlite

import com.google.gson.Gson
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import java.util.function.BiConsumer
import java.util.function.Consumer

internal class Core(path: String) : DB {
    private val lock = ReentrantLock()

    private var connection: Connection? = null

    init {
        try {
            val databasePath = Paths.get(path)
            val parentPath = databasePath.parent
            if (parentPath != null) {
                Files.createDirectories(parentPath)
            }
            Class.forName("org.sqlite.JDBC")
            connection = DriverManager.getConnection("jdbc:sqlite:" + path)
            Runtime.getRuntime().addShutdownHook(Thread(Runnable { this.close() }))
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    override fun close() {
        try {
            connection!!.close()
        } catch (e: SQLException) {
            throw RuntimeException(e)
        }
    }

    override fun tables(vararg classes: Class<*>) {
        val tablesMap = HashMap<String?, HashMap<String?, String?>?>()
        val indexMap = HashMap<String?, String?>()
        val s = SQLTemplate.query<Any?>("sqlite_master", Options().where("type = ?", "table"))
        try {
            connection!!.createStatement().use { statement ->
                statement.executeQuery(s).use { result ->
                    val metaData = connection!!.metaData
                    while (result.next()) {
                        val tableColumnTypeMap = HashMap<String?, String?>()
                        val tableName = result.getString("name")
                        metaData.getColumns(null, null, tableName, null).use { set ->
                            while (set.next()) {
                                val column = set.getString("COLUMN_NAME")
                                val type = set.getString("TYPE_NAME").lowercase(Locale.getDefault())
                                tableColumnTypeMap.put(column, type)
                            }
                        }
                        tablesMap.put(tableName, tableColumnTypeMap)
                        metaData.getIndexInfo(null, null, tableName, false, false).use { set ->
                            while (set.next()) {
                                val index = set.getString("INDEX_NAME")
                                val column = set.getString("COLUMN_NAME")
                                Optional.ofNullable<String?>(index)
                                    .ifPresent(Consumer { i: String? -> indexMap.put(index, column) })
                            }
                        }
                    }
                    for (tClass in classes) {
                        val tableName = getTableNameFromClass(tClass)
                        val tableColumnTypeMap = tablesMap.getOrDefault(tableName, null)
                        val reflect: Reflect<*> = Reflect<Any?>(tClass)
                        if (tableColumnTypeMap == null) {
                            statement.executeUpdate(SQLTemplate.create(tClass))
                        } else {
                            reflect.getDBColumnsWithType(BiConsumer { column: String?, type: String? ->
                                if (tableColumnTypeMap.getOrDefault(column, null) == null) {
                                    try {
                                        statement.executeUpdate(SQLTemplate.addTableColumn(tableName, column, type))
                                    } catch (e: SQLException) {
                                        throw RuntimeException(e)
                                    }
                                }
                            })
                        }
                        reflect.getIndexList(BiConsumer { index: String?, column: String? ->
                            try {
                                if (indexMap.get(index) != null) {
                                    indexMap.remove(index, column)
                                } else {
                                    statement.executeUpdate(SQLTemplate.createIndex(tClass, column))
                                }
                            } catch (e: SQLException) {
                                throw RuntimeException(e)
                            }
                        })
                    }
                    indexMap.forEach { (index: String?, _: String?) ->
                        try {
                            statement.executeUpdate(SQLTemplate.dropIndex<Any?>(index))
                        } catch (e: SQLException) {
                            throw RuntimeException(e)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    override fun drop(vararg classes: Class<*>) {
        try {
            connection!!.createStatement().use { statement ->
                for (tClass in classes) {
                    statement.executeUpdate(SQLTemplate.drop(tClass))
                }
            }
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    override fun version(): String? {
        val s = "select sqlite_version();"
        try {
            connection!!.createStatement().use { statement ->
                statement.executeQuery(s).use { resultSet ->
                    return if (resultSet.next()) resultSet.getString(1) else "unknown"
                }
            }
        } catch (e: SQLException) {
            throw RuntimeException(e)
        }
    }

    override fun <T : DataSupport<T?>?> insert(t: T?) {
        try {
            connection!!.createStatement().use { statement ->
                lock.lock()
                t!!.createdAt = System.currentTimeMillis()
                t.updatedAt = t.createdAt
                statement.executeUpdate(SQLTemplate.insert<T?>(t))
                statement.executeQuery("select last_insert_rowid()").use { result ->
                    if (result.next()) {
                        t.id = result.getLong(1)
                    }
                }
            }
        } catch (e: Exception) {
            throw RuntimeException(e)
        } finally {
            lock.unlock()
        }
    }

    override fun <T : DataSupport<T?>?> update(t: T?, predicate: String?, vararg args: Any?) {
        try {
            connection!!.createStatement().use { statement ->
                lock.lock()
                t!!.updatedAt = System.currentTimeMillis()
                statement.executeUpdate(SQLTemplate.update<T?>(t, Options().where(predicate, *args)))
            }
        } catch (e: Exception) {
            throw RuntimeException(e)
        } finally {
            lock.unlock()
        }
    }

    override fun <T : DataSupport<T?>?> update(t: T?) {
        update<T?>(t, "id = ?", t!!.id())
    }

    override fun <T : DataSupport<T?>?> delete(tClass: Class<T?>, predicate: String?, vararg args: Any?) {
        val sql = SQLTemplate.delete<T?>(tClass, Options().where(predicate, *args))
        try {
            connection!!.createStatement().use { statement ->
                lock.lock()
                statement.executeUpdate(sql)
            }
        } catch (e: Exception) {
            throw RuntimeException(e)
        } finally {
            lock.unlock()
        }
    }

    override fun <T : DataSupport<T?>?> delete(tClass: Class<T?>, ids: MutableList<Long?>?) {
        val builder = StringBuilder(ids.toString())
        builder.deleteCharAt(0).deleteCharAt(builder.length - 1)
        delete<T?>(tClass, "id in(?)", builder)
    }

    override fun <T : DataSupport<T?>?> delete(tClass: Class<T?>, vararg ids: Long?) {
        delete<T?>(tClass, Arrays.asList<Long?>(*ids))
    }

    override fun <T : DataSupport<T?>?> deleteAll(tClass: Class<T?>) {
        delete<T?>(tClass, null, null as Any?)
    }

    override fun <T : DataSupport<T?>?> find(tClass: Class<T?>, consumer: Consumer<Options?>?): MutableList<T?> {
        val options = if (consumer != null) Options() else null
        Optional.ofNullable<Consumer<Options?>?>(consumer)
            .ifPresent(Consumer { c: Consumer<Options?>? -> c!!.accept(options) })
        val sql = SQLTemplate.query<T?>(tClass, options)
        try {
            connection!!.createStatement().use { statement ->
                statement.executeQuery(sql).use { resultSet ->
                    val list: MutableList<T?> = ArrayList<T?>()
                    while (resultSet.next()) {
                        val t = Reflect.toEntity<T?>(tClass, options, resultSet)
                        Optional.ofNullable<T?>(t).ifPresent(Consumer { e: T? -> list.add(e) })
                    }
                    return list
                }
            }
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    override fun <T : DataSupport<T?>?> find(tClass: Class<T?>, ids: MutableList<Long?>?): MutableList<T?> {
        val builder = StringBuilder(ids.toString())
        builder.deleteCharAt(0).deleteCharAt(builder.length - 1)
        return find<T?>(tClass, Consumer { options: Options? -> options!!.where("id in(?)", builder) })
    }

    override fun <T : DataSupport<T?>?> find(tClass: Class<T?>, vararg ids: Long?): MutableList<T?> {
        return find<T?>(tClass, Arrays.asList<Long?>(*ids))
    }

    override fun <T : DataSupport<T?>?> findAll(tClass: Class<T?>): MutableList<T?> {
        return find<T?>(tClass, null as Consumer<Options?>?)
    }

    override fun <T : DataSupport<T?>?> findOne(tClass: Class<T?>, predicate: String?, vararg args: Any?): T? {
        val list = find<T?>(tClass, Consumer { options: Options? -> options!!.where(predicate, *args) })
        return if (!list.isEmpty()) list.get(0) else null
    }

    override fun <T : DataSupport<T?>?> findOne(tClass: Class<T?>, id: Long?): T? {
        return findOne<T?>(tClass, "id = ?", id)
    }

    override fun <T : DataSupport<T?>?> first(tClass: Class<T?>, predicate: String?, vararg args: Any?): T? {
        val list = find<T?>(
            tClass,
            Consumer { options: Options? -> options!!.where(predicate, *args).order("id", Options.ASC) })
        return if (!list.isEmpty()) list.get(0) else null
    }

    override fun <T : DataSupport<T?>?> first(tClass: Class<T?>): T? {
        return first<T?>(tClass, null, null as Any?)
    }

    override fun <T : DataSupport<T?>?> last(tClass: Class<T?>, predicate: String?, vararg args: Any?): T? {
        val list = find<T?>(
            tClass,
            Consumer { options: Options? -> options!!.where(predicate, *args).order("id", Options.DESC) })
        return if (!list.isEmpty()) list.get(0) else null
    }

    override fun <T : DataSupport<T?>?> last(tClass: Class<T?>): T? {
        return last<T?>(tClass, null, null as Any?)
    }

    override fun <T : DataSupport<T?>?> count(tClass: Class<T?>, predicate: String?, vararg args: Any?): Long {
        val s = SQLTemplate.query<T?>(tClass, Options().select("count(*)").where(predicate, *args))
        try {
            connection!!.createStatement().use { statement ->
                statement.executeQuery(s).use { resultSet ->
                    return if (resultSet.next()) resultSet.getLong(1) else 0
                }
            }
        } catch (e: SQLException) {
            throw RuntimeException(e)
        }
    }

    override fun <T : DataSupport<T?>?> count(tClass: Class<T?>): Long {
        return count<T?>(tClass, null, null as Any?)
    }

    override fun <T : DataSupport<T?>?> average(
        tClass: Class<T?>,
        column: String?,
        predicate: String?,
        vararg args: Any?
    ): Double {
        val s = SQLTemplate.query<T?>(
            tClass,
            Options().select(String.format("avg(%s)", column)).where(predicate, *args)
        )
        try {
            connection!!.createStatement().use { statement ->
                statement.executeQuery(s).use { resultSet ->
                    return if (resultSet.next()) resultSet.getDouble(1) else 0.0
                }
            }
        } catch (e: SQLException) {
            throw RuntimeException(e)
        }
    }

    override fun <T : DataSupport<T?>?> average(tClass: Class<T?>, column: String?): Double {
        return average<T?>(tClass, column, null, null as Any?)
    }

    override fun <T : DataSupport<T?>?> sum(
        tClass: Class<T?>,
        column: String?,
        predicate: String?,
        vararg args: Any?
    ): Number? {
        val s = SQLTemplate.query<T?>(
            tClass,
            Options().select(String.format("sum(%s)", column)).where(predicate, *args)
        )
        try {
            connection!!.createStatement().use { statement ->
                statement.executeQuery(s).use { resultSet ->
                    return if (resultSet.next()) resultSet.getObject(1) as Number? else 0
                }
            }
        } catch (e: SQLException) {
            throw RuntimeException(e)
        }
    }

    override fun <T : DataSupport<T?>?> sum(tClass: Class<T?>, column: String?): Number? {
        return sum<T?>(tClass, column, null, null as Any?)
    }

    override fun <T : DataSupport<T?>?> max(
        tClass: Class<T?>,
        column: String?,
        predicate: String?,
        vararg args: Any?
    ): Number? {
        val s = SQLTemplate.query<T?>(
            tClass,
            Options().select(String.format("max(%s)", column)).where(predicate, *args)
        )
        try {
            connection!!.createStatement().use { statement ->
                statement.executeQuery(s).use { resultSet ->
                    return if (resultSet.next()) resultSet.getObject(1) as Number? else 0
                }
            }
        } catch (e: SQLException) {
            throw RuntimeException(e)
        }
    }

    override fun <T : DataSupport<T?>?> max(tClass: Class<T?>, column: String?): Number? {
        return max<T?>(tClass, column, null, null as Any?)
    }

    override fun <T : DataSupport<T?>?> min(
        tClass: Class<T?>,
        column: String?,
        predicate: String?,
        vararg args: Any?
    ): Number? {
        val s = SQLTemplate.query<T?>(
            tClass,
            Options().select(String.format("min(%s)", column)).where(predicate, *args)
        )
        try {
            connection!!.createStatement().use { statement ->
                statement.executeQuery(s).use { resultSet ->
                    return if (resultSet.next()) resultSet.getObject(1) as Number? else 0
                }
            }
        } catch (e: SQLException) {
            throw RuntimeException(e)
        }
    }

    override fun <T : DataSupport<T?>?> min(tClass: Class<T?>, column: String?): Number? {
        return min<T?>(tClass, column, null, null as Any?)
    }

    companion object {
        @JvmField
        val gson: Gson = Gson()
    }
}
