package com.github.masx200.bilibili_dynamic_image_downloader

import java.io.BufferedWriter

fun printorwriteimageurl(url: String? = null, imagesWriter: BufferedWriter? = null) {
    if (url != null) {
        if (imagesWriter != null) {
            // 将图片链接写入文件
//            if (true) {
            imagesWriter.write(url)
//            }
            imagesWriter.newLine()
        } else {
            // 如果没有提供imagesWriter，则将图片链接输出到控制台
            println(url)
        }
    }
}