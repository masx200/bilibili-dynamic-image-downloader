package com.github.masx200.bilibili_dynamic_image_downloader

import java.io.File

fun readCookieFromFile(string: String): String {


    return try {
        val file = File(string)
        if (!file.exists()) {
            throw Exception("cookie file not found")
        }
        file.readText()
    } catch (e: Exception) {
        throw e
    }
}