package com.github.masx200.jsqlite

import java.util.*

fun getTableNameFromClass(tClass: Class<*>): String {
    if (tClass.isAnnotationPresent(Table::class.java)) {

        val annotation = tClass.getAnnotation(Table::class.java)
        if (annotation.name.isNotEmpty()) {
//            println(annotation.name)
            return annotation.name
        }

    }
    val simplename = tClass.getSimpleName()
    val tableName = simplename.lowercase(Locale.getDefault())
    return tableName
}