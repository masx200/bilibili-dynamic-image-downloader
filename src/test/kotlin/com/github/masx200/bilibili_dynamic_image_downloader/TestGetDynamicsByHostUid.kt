package com.github.masx200.bilibili_dynamic_image_downloader

import cn.hll520.linling.biliClient.BiliClientFactor
import kotlin.test.Test

class TestGetDynamicsByHostUid {


    @Test
    fun test() {
        val client = BiliClientFactor.getClient()

        val list = client.dynamic().withHostUid("10013290".toLong()).list()
        System.out.println(list)
        System.out.println("是还有动态--> " + (list.getHasMore() == 1))
        // 动态集合
        val items = list.getItems()
        System.out.println(items)

    }
}