package com.github.masx200.bilibili_dynamic_image_downloader

import com.github.artbits.jsqlite.Column
import com.github.artbits.jsqlite.DataSupport

class DynamicPictures(consumer: (DynamicPictures) -> Unit) :
    DataSupport<DynamicPictures>(consumer as ((DynamicPictures) -> Unit)?) {
    @Column(index = true)
    var dynamicId: Long? = null

    @Column(index = true)
    var pictureSrc: String? = null
    @Column(index = true)
    var userId: String? = null

    @Column(index = true)
    var dynamicType: Long? = null

    // Kotlin主构造函数可以直接初始化父类
    init {
        consumer(this)
    }

    override fun toString(): String {
        return "DynamicPictures(dynamicId=$dynamicId, pictureSrc=$pictureSrc, userId=$userId, dynamicType=$dynamicType)"
    }

    companion object {
        const val dynamicId = "dynamicId"
        const val pictureSrc = "pictureSrc"
        const val userId = "userId"
        const val dynamicType = "dynamicType"
    }
}