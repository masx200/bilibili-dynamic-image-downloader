package com.github.masx200.bilibili_dynamic_image_downloader

import com.github.artbits.jsqlite.Column
import com.github.artbits.jsqlite.DataSupport

class DynamicRanges(consumer: (DynamicRanges) -> Unit) :
    DataSupport<DynamicRanges>(consumer as ((DynamicRanges) -> Unit)?) {
    @Column(index = true)
    var userId: String? = null

    @Column(index = true)
    var ENDWITH_DYNAMIC_ID: String? = null

    @Column(index = true)
    var offset_dynamic_id: String? = null

    @Column(index = true)
    var earliestDynamicId: String? = null

    // Kotlin主构造函数可以直接初始化父类
    init {
        consumer(this)
    }

    override fun toString(): String {
        return "DynamicRanges(userId=$userId, ENDWITH_DYNAMIC_ID=$ENDWITH_DYNAMIC_ID, earliestDynamicId=$earliestDynamicId, offset_dynamic_id=$offset_dynamic_id)"
    }
}