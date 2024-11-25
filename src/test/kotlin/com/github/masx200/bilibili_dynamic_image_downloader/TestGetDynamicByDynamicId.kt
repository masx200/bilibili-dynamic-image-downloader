package com.github.masx200.bilibili_dynamic_image_downloader

import cn.hll520.linling.biliClient.BiliClientFactor
import org.junit.Test

class TestGetDynamicByDynamicId {


    @Test
    fun test() {
        val client = BiliClientFactor.getClient()

        val dynamic = client.dynamic().withDynamicId("1001872568590794774".toLong()).get()
        System.out.println("类型为--> " + dynamic.getType())
        System.out.println("作者为--> " + dynamic.getName())
        System.out.println("作者ID为--> " + dynamic.getUid())
        // 指定动态数据
        val data = dynamic.getData()
        System.out.println(data)
        System.out.println("动态阅读--> " + data.getView())
        System.out.println("动态点赞--> " + data.getLike())
        System.out.println("动态转发--> " + data.getRepost())


    }
}