package com.github.masx200.bilibili_dynamic_image_downloader

import com.github.masx200.biliClient.BiliClientFactor
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import com.xenomachina.argparser.mainBody

fun main(args: Array<String>) {
    mainBody("bilibili-dynamic-image-downloader") {
        println("bilibili-dynamic-image-downloader")
        // Creates a Netty server
        ArgParser(args).parseInto(::MyArgs).run {
            println("offset_dynamic_id=${offset_dynamic_id}")
            println("cookie=${cookie}")
            println("host_uid=${host_uid}")
            val client = BiliClientFactor.getClient { requestBase ->
                requestBase.setHeader("cookie", cookie)
            }
            var offset: Long? = offset_dynamic_id.toLong()
            var hasMore = true
            while (hasMore) {

                val list = if (offset != null && offset != 0L) client.dynamic().withHostUid(host_uid.toLong())
                    .list(offset) else {
                    client.dynamic().withHostUid(host_uid.toLong()).list()
                }
//            System.out.println(list)
                System.out.println("是还有动态--> " + (list.getHasMore() == 1))
                System.out.println("nextOffset--> " + (list.nextOffset))
                hasMore = list.getHasMore() == 1
                offset = list.nextOffset
                // 动态集合
                val items = list.getItems()
//            System.out.println(items)
                if (items.size > 0) {
                    for (item in items) {
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
    }

}

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
}