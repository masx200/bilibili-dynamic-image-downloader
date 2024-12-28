package com.github.masx200.bilibili_dynamic_image_downloader

import org.jetbrains.exposed.sql.Table

open class DataSupportTable(name: String = "") : Table(name) {
    val id = long("id")
    val createdAt = long("createdAt")
    val updatedAt = long("updatedAt")
}