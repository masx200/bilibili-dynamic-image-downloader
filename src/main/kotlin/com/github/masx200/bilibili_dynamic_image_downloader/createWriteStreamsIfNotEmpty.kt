package com.github.masx200.bilibili_dynamic_image_downloader

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

/**
 * 根据文件路径创建BufferedWriter对象，仅当文件路径不为空时创建
 *
 * @param file_dynamic_ids ID文件路径，如果为空，则对应返回值为null
 * @param file_dynamic_images 图像文件路径，如果为空，则对应返回值为null
 * @return 一个Pair对象，包含两个可能为null的BufferedWriter对象，分别对应ID和图像文件的写入流
 */
fun createWriteStreamsIfNotEmpty(
    file_dynamic_ids: String, file_dynamic_images: String
): Pair<BufferedWriter?, BufferedWriter?> {
    val idsWriter: BufferedWriter?
    val imagesWriter: BufferedWriter?

    if (file_dynamic_ids.isNotEmpty() && file_dynamic_images.isNotEmpty()) {

        val directory1 = File(file_dynamic_images).parentFile
        if (!directory1.exists())
            directory1.mkdirs()

        val directory2 = File(file_dynamic_ids).parentFile
        if (!directory2.exists())
            directory2.mkdirs()
        idsWriter = BufferedWriter(FileWriter(File(file_dynamic_ids)))
        imagesWriter = BufferedWriter(FileWriter(File(file_dynamic_images)))
    } else {
        return Pair(null, null)
    }

    return Pair(idsWriter, imagesWriter)
}