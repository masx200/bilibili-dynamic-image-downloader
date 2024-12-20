package com.github.masx200.bilibili_dynamic_image_downloader

import com.github.masx200.biliClient.BiliClientFactor
import com.github.masx200.biliClient.model.dynamic.Dynamic
import com.github.masx200.biliClient.model.dynamic.Picture
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import com.xenomachina.argparser.mainBody
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

/**
 * The entry point of the program.
 * Parses command line arguments and processes Bilibili dynamic image downloads.
 *
 * @param args Command line arguments, containing parameters for program operation.
 */
fun main(args: Array<String>) {
    println("bilibili-dynamic-image-downloader")
    println(args.contentToString())
    mainBody("bilibili-dynamic-image-downloader") {

        // Creates a Netty server
        ArgParser(args).parseInto(::MyArgs).run {
            printmyargs(this)
            val iteritems = getDynamicSequence(this)

            val (idsWriter, imagesWriter) = createWriteStreamsIfNotEmpty(file_dynamic_ids, file_dynamic_images)

            if (idsWriter != null && imagesWriter != null) {
                idsWriter.use { idsWriter ->
                    imagesWriter.use { imagesWriter ->
                        processDynamicItems(iteritems, idsWriter, imagesWriter)
                    }
                }
            } else {
                processDynamicItems(iteritems)
            }

        }
    }

}

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


/**
 * 打印[MyArgs]类中的参数信息
 * 此函数接收一个[MyArgs]对象作为参数，从中提取并打印出各个字段的值
 * 主要用于调试或日志记录，以帮助开发者理解传递给函数的参数值
 *
 * @param options 包含参数的[MyArgs]对象，用于提取并打印参数信息
 */
fun printmyargs(options: MyArgs) {

    println(options.toString())

}

/**
 * 提取动态数据序列
 *
 * 该函数根据给定的参数，获取并生成一个动态数据序列它通过指定的用户ID和cookie信息，
 * 利用BiliClientFactor创建一个客户端实例，然后循环获取用户的动态数据直到没有更多数据为止
 *
 * @param options 包含了获取动态数据所需的各种参数，如cookie、偏移动态ID、主机UID等
 * @return 返回一个动态数据的序列，每个元素代表一个动态数据项
 */
fun getDynamicSequence(options: MyArgs): Sequence<Dynamic> {
    return sequence {


        val cookie = options.cookie
        val offset_dynamic_id = options.offset_dynamic_id
        val host_uid = options.host_uid

        val endwith_dynamic_id = options.endwith_dynamic_id

        val client = BiliClientFactor.getClient { requestBase ->
            requestBase!!.setHeader("cookie", cookie)
        }
        var offset: String = offset_dynamic_id
        var hasMore = true
        while (hasMore) {

            val list = if (offset != "") client.dynamic().withHostUid(host_uid.toLong()).list(offset.toLong()) else {
                client.dynamic().withHostUid(host_uid.toLong()).list()
            }
            //System.out.println(list)
//            System.out.println("是还有动态--> " + (list.hasMore == 1))
//            System.out.println("nextOffset--> " + (list.nextOffset))
            hasMore = list?.hasMore == 1L
            offset = list?.nextOffset.toString()
            // 动态集合
            val items = list?.items
//            System.out.println(items)
            if (items!!.isNotEmpty()) {
                for (item in items) {
                    val dynamicId = item!!.data!!.dynamic_id

                    if (endwith_dynamic_id != "" && (dynamicId.toString() == endwith_dynamic_id || dynamicId.toString()
                            .toLong() < endwith_dynamic_id.toLong())
                    ) {

                        return@sequence
                    } else {

                        yield(item)
                    }
                }
            }
        }
    }
}

/**
 * Class for parsing command line arguments.
 * This class uses the ArgParser library to define and store command line arguments.
 * The purpose of this class is to provide easy access to command line arguments, converting them into properties for use.
 *
 * @param parser An instance of ArgParser used to parse command line arguments.
 */
class MyArgs(parser: ArgParser) {
    val cookie by parser.storing(
        "-c", "--cookie",
        help = "cookie"
    )

    val host_uid by parser.storing(
        "-u", "--host_uid",
        help = "host_uid"
    )
    val offset_dynamic_id by parser.storing(
        "-o", "--offset_dynamic_id",
        help = "offset_dynamic_id"
    ).default("")

    val endwith_dynamic_id by parser.storing(
        "-e", "--endwith_dynamic_id",
        help = "endwith_dynamic_id"
    ).default("")

    val file_dynamic_ids by parser.storing(
        "-d", "--file_dynamic_ids", help = "file_dynamic_ids"
    ).default("")
    val file_dynamic_images by parser.storing(
        "-i", "--file_dynamic_images", help = "file_dynamic_images"
    ).default("")

    override fun toString(): String {
        return "MyArgs(cookie='$cookie', host_uid='$host_uid', offset_dynamic_id='$offset_dynamic_id', endwith_dynamic_id='$endwith_dynamic_id', file_dynamic_ids='$file_dynamic_ids', file_dynamic_images='$file_dynamic_images')"
    }

}