package com.github.masx200.bilibili_dynamic_image_downloader

import com.github.masx200.biliClient.model.dynamic.Dynamic
import com.github.masx200.biliClient.model.dynamic.Picture
import java.io.BufferedWriter

/**
 * 处理动态项序列，根据提供的BufferedWriter对象将动态ID和图片链接写入文件或控制台
 *
 * @param iteritems 动态项序列，包含动态数据
 * @param idsWriter 用于写入动态ID的BufferedWriter对象，如果为null，则输出到控制台
 * @param imagesWriter 用于写入图片链接的BufferedWriter对象，如果为null，则输出到控制台
 * 处理动态项目序列，根据需要将动态ID和图片链接写入文件或输出到控制台
 *
 * @param iteritems 动态项目序列，用于遍历和处理每个动态
 * @param idsWriter 用于写入动态ID的BufferedWriter对象，如果不需要写入文件则为null
 * @param imagesWriter 用于写入图片链接的BufferedWriter对象，如果不需要写入文件则为null
 */
fun processDynamicItems(
    iteritems: Sequence<Dynamic>,
    idsWriter: BufferedWriter? = null,
    imagesWriter: BufferedWriter? = null
) {
    // 遍历动态项目序列
    for (item in iteritems) {

        // 根据idsWriter是否为null决定动态ID的输出方式
        if (idsWriter != null) {
            // 将动态ID写入文件
            idsWriter.write("https://t.bilibili.com/" + item.data!!.dynamic_id.toString())
            idsWriter.newLine()
        } else {
            // 如果没有提供idsWriter，则将动态ID输出到控制台
            println("https://t.bilibili.com/" + item.data!!.dynamic_id)
        }

        // 输出当前处理的动态项
        println(item)
        if (item.essay != null) {
            item.essay!!.origin_image_urls!!.forEach { url ->
                if (imagesWriter != null) {
                    // 将图片链接写入文件
                    if (url != null) {
                        imagesWriter.write(url)
                    }
                    imagesWriter.newLine()
                } else {
                    // 如果没有提供imagesWriter，则将图片链接输出到控制台
                    println(url)
                }
            }
        }
        // 如果动态项的详细信息不为空，则进一步处理
        if (item.detail != null) {

            // 如果动态项包含文章信息，则处理图片链接

            // 如果动态项包含图片信息，则处理图片链接
            if (item.detail!!.pictures != null) {
                // 遍历动态项中的图片链接
                (item.detail!!.pictures as Iterable<Picture?>).forEach { picture ->
                    val str = picture!!.img_src
                    if (imagesWriter != null) {
                        // 将图片链接写入文件
                        if (str != null) {
                            imagesWriter.write(str)
                        }
                        imagesWriter.newLine()
                    } else {
                        // 如果没有提供imagesWriter，则将图片链接输出到控制台
                        println(str)
                    }
                }
            }
        }
    }
}