package com.github.masx200.bilibili_dynamic_image_downloader

import com.github.masx200.biliClient.BiliClientFactor
import com.github.masx200.biliClient.exception.BiliRequestException
import com.github.masx200.biliClient.model.dynamic.Dynamic
import com.github.masx200.biliClient.model.dynamic.DynamicItems
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow


/**
 * 提取动态数据序列
 *
 * 该函数根据给定的参数，获取并生成一个动态数据序列它通过指定的用户ID和cookie信息，
 * 利用BiliClientFactor创建一个客户端实例，然后循环获取用户的动态数据直到没有更多数据为止
 *
 * @param //包含了获取动态数据所需的各种参数，如cookie、偏移动态ID、主机UID等
 * @return 返回一个动态数据的序列，每个元素代表一个动态数据项
 */
fun getDynamicSequence(
    offset_dynamic_id: String = "",
    host_uid: String,
    endwith_dynamic_id: String = "",
    cookie_str: String, acceptEmpty: Boolean = false
): Flow<Dynamic> {
    return flow {


        val cookie = cookie_str


        val client = BiliClientFactor.getClient { requestBase ->
            requestBase!!.setHeader("cookie", cookie)
        }
        var offset: String = offset_dynamic_id
        var hasMore = true
        while (hasMore) {
            val list: DynamicItems? =
                try {
                    if (offset != "") client.dynamic().withHostUid(host_uid.toLong()).list(offset.toLong()) else {
                        client.dynamic().withHostUid(host_uid.toLong()).list()
                    }
                } catch (e: BiliRequestException) {
                    if (e.message == "由于触发哔哩哔哩安全风控策略，该次访问请求被拒绝。") {
//                        println("由于触发哔哩哔哩安全风控策略，该次访问请求被拒绝。")
                        val seconds = 30
                        println(
                            "由于触发哔哩哔哩安全风控策略，该次访问请求被拒绝。" + "等待${seconds}秒后重试"
                        )
                        delay(seconds * 1000L)
                        var sequence = getDynamicSequence(
                            offset_dynamic_id,
                            host_uid,
                            endwith_dynamic_id,
                            cookie_str

                        )
                        return@flow emitAll(
                            sequence
                        )
                    }
                    throw (e)
                } catch (e: Exception) {
                    if (acceptEmpty && e.message == "cardsarray为null,可能未登录") {
                        return@flow
                    }
                    throw (e)
                }

            //System.out.println(list)
//            System.out.println("是还有动态--> " + (list.hasMore == 1))
//            System.out.println("nextOffset--> " + (list.nextOffset))
            hasMore = list?.has_more == 1L
            offset = list?.next_offset.toString()
            // 动态集合
            val items = list?.items
//            System.out.println(items)
            if (items!!.isNotEmpty()) {
                for (item in items) {
                    val dynamicId = item!!.data!!.dynamic_id

                    if (endwith_dynamic_id != "" && (dynamicId.toString() == endwith_dynamic_id || dynamicId.toString()
                            .toLong() < endwith_dynamic_id.toLong())
                    ) {

                        return@flow
                    } else {

                        emit(item)
                    }
                }
            }
        }
    }
}