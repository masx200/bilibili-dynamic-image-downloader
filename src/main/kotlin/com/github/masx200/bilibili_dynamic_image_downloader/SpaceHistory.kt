package com.github.masx200.bilibili_dynamic_image_downloader

import com.github.masx200.jsqlite.Column
import com.github.masx200.jsqlite.DataSupport
import com.github.masx200.jsqlite.Table

@Table(name = "spacehistory")
class SpaceHistory(consumer: (SpaceHistory) -> Unit) :
    DataSupport<SpaceHistory>(consumer as ((SpaceHistory) -> Unit)?) {
    @Column(index = true)
    var userId: String? = null

    @Column(index = true)
    var dynamicType: Long? = null

    @Column(index = true)
    var dynamicId: Long? = null


    // Kotlin主构造函数可以直接初始化父类
    init {
        consumer(this)
    }

    override fun toString(): String {
        return "SpaceHistory(userId=$userId, dynamicId=$dynamicId, dynamicType=$dynamicType)"
    }


}


