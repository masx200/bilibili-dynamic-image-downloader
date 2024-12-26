package com.github.masx200.bilibili_dynamic_image_downloader

import com.github.masx200.biliClient.BiliClientFactor
import com.github.masx200.biliClient.model.dynamic.Dynamic

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