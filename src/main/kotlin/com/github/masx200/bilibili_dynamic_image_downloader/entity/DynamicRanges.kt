package com.github.masx200.bilibili_dynamic_image_downloader.entity

import com.github.masx200.sqlite_java.Column
import com.github.masx200.sqlite_java.DataSupport
import com.github.masx200.sqlite_java.Table

@Table(name = "dynamicranges")
class DynamicRanges(consumer: ((DynamicRanges) -> Unit)) :
    DataSupport<DynamicRanges>(consumer) {
    constructor() : this({})

    //
//    @Column(autoIncrement = true, primaryKey = true)
//    var id: Long? = null;
    @Column(index = true)
    var userId: String? = null

    @Column(index = true)
    var endwith_dynamic_id: Long? = null

    @Column(index = true)
    var offset_dynamic_id: Long? = null

    @Column(index = true)
    var earliestDynamicId: Long? = null

    // Kotlin主构造函数可以直接初始化父类
    init {
        consumer(this)
    }

    override fun toString(): String {
        return "DynamicRanges(userId=$userId, endwith_dynamic_id=$endwith_dynamic_id, earliestDynamicId=$earliestDynamicId, offset_dynamic_id=$offset_dynamic_id)" + super.toString()
    }


}

