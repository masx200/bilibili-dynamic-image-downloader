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

import java.util.*
import java.util.function.BiConsumer

internal object SQLTemplate {
    fun create(tClass: Class<*>): String {
        val columnsString = StringBuffer("id integer primary key,")
        Reflect<Any?>(tClass).getDBColumnsWithType(BiConsumer { column: String?, type: String? ->
            if (column != "id") {
                columnsString.append(column).append(" ").append(type).append(",")
            }
        })
        columnsString.deleteCharAt(columnsString.length - 1)
        val tableName = getTableNameFromClass(tClass)
        return `$`("create table %s (%s);", tableName, columnsString)
    }

    fun addTableColumn(tableName: String?, column: String?, type: String?): String {
        return `$`("alter table %s add column %s %s;", tableName, column, type)
    }

    fun drop(tClass: Class<*>): String {
        return `$`("drop table %s;", tClass.getSimpleName().lowercase(Locale.getDefault()))
    }

    fun <T> insert(t: T?): String {
        val columnsString = StringBuffer()
        val valueString = StringBuffer()
        Reflect<T?>(t).getDBColumnsWithValue(BiConsumer { column: String?, value: Any? ->
            if (column != "id") {
                columnsString.append(column).append(",")
                valueString.append(value).append(",")
            }
        })
        columnsString.deleteCharAt(columnsString.length - 1)
        valueString.deleteCharAt(valueString.length - 1)
        val tableName = t!!.javaClass.getSimpleName().lowercase(Locale.getDefault())
        return `$`("insert into %s (%s) values (%s);", tableName, columnsString, valueString)
    }

    fun <T> update(t: T?, options: Options): String {
        val tableName = t!!.javaClass.getSimpleName().lowercase(Locale.getDefault())
        val whereString = if (options.wherePredicate != null) `$`("where %s ", options.wherePredicate) else ""
        val setString = StringBuffer()
        Reflect<T?>(t).getDBColumnsWithValue(BiConsumer { column: String?, value: Any? ->
            if (value != null && column != "id") {
                setString.append(column).append(" = ").append(value).append(",")
            }
        })
        setString.deleteCharAt(setString.length - 1)
        val SQLBuilder = StringBuilder()
        return SQLBuilder
            .append(`$`("update %s set %s ", tableName, setString))
            .append(whereString)
            .append(";")
            .deleteCharAt(SQLBuilder.length - 2)
            .toString()
    }

    fun <T> delete(tClass: Class<T?>, options: Options): String {
        val deleteString = `$`("delete from %s ", tClass.getSimpleName().lowercase(Locale.getDefault()))
        val whereString = if (options.wherePredicate != null) `$`("where %s ", options.wherePredicate) else ""
        val SQLBuilder = StringBuilder()
        return SQLBuilder
            .append(deleteString)
            .append(whereString)
            .append(";")
            .deleteCharAt(SQLBuilder.length - 2)
            .toString()
    }

    fun <T> query(table: String?, options: Options?): String {
        if (options == null) {
            return `$`("select * from %s;", table)
        }
        val fromString = `$`("from %s ", table)
        val selectString = `$`("select %s ", Optional.ofNullable<String?>(options.selectColumns).orElse("*"))
        val whereString = if (options.wherePredicate != null) `$`("where %s ", options.wherePredicate) else ""
        val groupString = if (options.groupColumns != null) `$`("group by %s ", options.groupColumns) else ""
        val orderString = if (options.orderColumns != null) `$`("order by %s ", options.orderColumns) else ""
        val limitString = if (options.limitSize != null) `$`("limit %d ", options.limitSize) else ""
        val offsetString = if (options.offsetSize != null) `$`("offset %d ", options.offsetSize) else ""
        val SQLBuilder = StringBuilder()
        return SQLBuilder
            .append(selectString)
            .append(fromString)
            .append(whereString)
            .append(groupString)
            .append(orderString)
            .append(limitString)
            .append(offsetString)
            .append(";")
            .deleteCharAt(SQLBuilder.length - 2)
            .toString()
    }

    fun <T> query(tClass: Class<T?>, options: Options?): String {
        return query<Any?>(tClass.getSimpleName().lowercase(Locale.getDefault()), options)
    }

    fun createIndex(tClass: Class<*>, column: String?): String {
        val table = getTableNameFromClass(tClass)
        val index = `$`("idx_%s_%s", table, column)
        return `$`("create index %s on %s(%s)", index, table, column)
    }

    fun <T> dropIndex(index: String?): String {
        return `$`("drop index %s", index)
    }

    private fun `$`(format: String, vararg objects: Any?): String {
        return String.format(format, *objects)
    }

    fun alterTableColumn(string: String, string1: String?, string2: String?): String? {
        println(
            """
                alterTableColumn()
                string: $string
                string1: $string1
                string2: $string2
                """.trimIndent()
        )
        TODO()
    }

    fun dropTableColumn(p0: String, p1: String): String? {
        println(
            """
                dropTableColumn()
                p0: $p0
                p1: $p1
                """.trimIndent()
        )
        TODO()
    }
}
