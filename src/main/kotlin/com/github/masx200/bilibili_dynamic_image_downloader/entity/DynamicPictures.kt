package com.github.masx200.bilibili_dynamic_image_downloader.entity

import com.github.masx200.sqlite_java.Column
import com.github.masx200.sqlite_java.DataSupport
import com.github.masx200.sqlite_java.Table
import java.util.function.Consumer

@Table(name = "dynamicpictures")
class DynamicPictures(consumer: ((DynamicPictures) -> Unit)) :
    DataSupport<DynamicPictures>(Consumer(consumer)) {
    constructor() : this({})

    //
//    @Column(autoIncrement = true, primaryKey = true)
//    var id: Long? = null;
    @Column(index = true)
    var dynamicId: Long? = null

    @Column(index = true)
    var pictureSrc: String? = null

    @Column(index = true)
    var userId: String? = null


    @Column(index = true)
    var dynamicOriginId: Long? = null
//    @Column(index = true)
//    var dynamicType: Long? = null

    // Kotlin主构造函数可以直接初始化父类
    init {
        consumer(this)
    }

    override fun toString(): String {
        return "DynamicPictures(dynamicId=$dynamicId, pictureSrc=$pictureSrc, userId=$userId" + "," +
//                "dynamicOriginType=$dynamicOriginType," +
                "dynamicOriginId=$dynamicOriginId," +
                ")" + super.toString()
//                ", dynamicType=$dynamicType)"
    }


}

