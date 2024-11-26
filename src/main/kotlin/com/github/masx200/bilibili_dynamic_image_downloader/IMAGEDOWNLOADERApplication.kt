package com.github.masx200.bilibili_dynamic_image_downloader

import com.github.masx200.biliClient.BiliClientFactor
import com.github.masx200.biliClient.model.dynamic.Dynamic
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import com.xenomachina.argparser.mainBody

/**
 * The entry point of the program.
 * Parses command line arguments and processes Bilibili dynamic image downloads.
 *
 * @param args Command line arguments, containing parameters for program operation.
 */
fun main(args: Array<String>) {
    mainBody("bilibili-dynamic-image-downloader") {
        println("bilibili-dynamic-image-downloader")
        // Creates a Netty server
        ArgParser(args).parseInto(::MyArgs).run {
            printmyargs(this)
            val iteritems = getDynamicSequence(this)
            for (item in iteritems) {
                println("id=" + item.data.dynamic_id)
                println(item)
                if (item.detail != null) {

                    if (item.detail.pictures != null)
                        item.detail.pictures.forEach {
                            println(it.img_src)
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
    val cookie = options.cookie
    val offset_dynamic_id = options.offset_dynamic_id
    val host_uid = options.host_uid

    val endwith_dynamic_id = options.endwith_dynamic_id

    println("{")
    println("offset_dynamic_id=${offset_dynamic_id}")
    println("cookie=${cookie}")
    println("host_uid=${host_uid}")
    println("endwith_dynamic_id=${endwith_dynamic_id}")
    println("}")
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
            requestBase.setHeader("cookie", cookie)
        }
        var offset: Long = offset_dynamic_id.toLong()
        var hasMore = true
        while (hasMore) {

            val list = if (offset != 0L) client.dynamic().withHostUid(host_uid.toLong())
                .list(offset) else {
                client.dynamic().withHostUid(host_uid.toLong()).list()
            }
            //System.out.println(list)
            System.out.println("是还有动态--> " + (list.hasMore == 1))
            System.out.println("nextOffset--> " + (list.nextOffset))
            hasMore = list.hasMore == 1
            offset = list.nextOffset
            // 动态集合
            val items = list.items
//            System.out.println(items)
            if (items.size > 0) {
                for (item in items) {
                    val dynamicId = item.data.dynamic_id

                    if (dynamicId.toString() == endwith_dynamic_id && endwith_dynamic_id != "0") {

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
    ).default("0")

    val endwith_dynamic_id by parser.storing(
        "-e", "--endwith_dynamic_id",
        help = "endwith_dynamic_id"
    ).default("0")
}