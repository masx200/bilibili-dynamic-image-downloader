package com.github.masx200.bilibili_dynamic_image_downloader.entity

import com.github.masx200.sqlite_java.Column
import com.github.masx200.sqlite_java.DataSupport
import com.github.masx200.sqlite_java.Table

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
        return "SpaceHistory(userId=$userId, dynamicId=$dynamicId, dynamicType=$dynamicType," +
                "dynamicOriginType=$dynamicOriginType," +
                "dynamicOriginId=$dynamicOriginId," +
                ")" + super.toString()
    }


}


