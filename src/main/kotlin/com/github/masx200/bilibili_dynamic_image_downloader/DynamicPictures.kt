package com.github.masx200.bilibili_dynamic_image_downloader

import com.github.masx200.jsqlite.Column
import com.github.masx200.jsqlite.DataSupport
import com.github.masx200.jsqlite.Table

@Table(name = "dynamicpictures")
class DynamicPictures(consumer: ((DynamicPictures) -> Unit)) :
    DataSupport<DynamicPictures>(consumer) {
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

//    @Column(index = true)
//    var dynamicType: Long? = null

    // Kotlin主构造函数可以直接初始化父类
    init {
        consumer(this)
    }

    override fun toString(): String {
        return "DynamicPictures(dynamicId=$dynamicId, pictureSrc=$pictureSrc, userId=$userId" + ")" + super.toString()
//                ", dynamicType=$dynamicType)"
    }


}

