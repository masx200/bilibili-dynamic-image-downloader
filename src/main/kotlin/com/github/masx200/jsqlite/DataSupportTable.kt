package com.github.masx200.jsqlite

import org.jetbrains.exposed.dao.id.LongIdTable

open class DataSupportTable(name: String = "") : LongIdTable(name) {
    //    val id = long("id")
    val createdAt = long("createdAt")
    val updatedAt = long("updatedAt")
}