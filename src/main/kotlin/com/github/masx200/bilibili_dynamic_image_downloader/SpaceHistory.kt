package com.github.masx200.bilibili_dynamic_image_downloader

import com.github.masx200.jsqlite.Column
import com.github.masx200.jsqlite.DataSupport
import com.github.masx200.jsqlite.Table

@Table(name = "spacehistory")
class SpaceHistory(consumer: ((SpaceHistory) -> Unit)) :
    DataSupport<SpaceHistory>(consumer) {
    constructor() : this({})

    //    @Column(autoIncrement = true, primaryKey = true)
//    var id: Long? = null;
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

    @Column(index = true)
    var dynamicOriginType: Long? = null

    @Column(index = true)
    var dynamicOriginId: Long? = null

    override fun toString(): String {
        return "SpaceHistory(userId=$userId, dynamicId=$dynamicId, dynamicType=$dynamicType" +
                "dynamicOriginType=$dynamicOriginType" +
                "dynamicOriginId=$dynamicOriginId" +
                ")" + super.toString()
    }


}


