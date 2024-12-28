package com.github.masx200.bilibili_dynamic_image_downloader
import com.github.artbits.jsqlite.Column
import com.github.artbits.jsqlite.DataSupport

class DynamicRanges(consumer: (DynamicRanges) -> Unit) :
    DataSupport<DynamicRanges>(consumer as ((DynamicRanges) -> Unit)?) {
    @Column(index = true)
    var userId: String? = null

    @Column(index = true)
    var ENDWITH_DYNAMIC_ID: Long? = null

    @Column(index = true)
    var offset_dynamic_id: Long? = null

    @Column(index = true)
    var earliestDynamicId: Long? = null

    // Kotlin主构造函数可以直接初始化父类
    init {
        consumer(this)
    }

    override fun toString(): String {
        return "DynamicRanges(userId=$userId, ENDWITH_DYNAMIC_ID=$ENDWITH_DYNAMIC_ID, earliestDynamicId=$earliestDynamicId, offset_dynamic_id=$offset_dynamic_id)"
    }


}

object DynamicRangesSchema : DataSupportTable() {
    val userId = text("userId")
    val ENDWITH_DYNAMIC_ID = long("ENDWITH_DYNAMIC_ID")
    val earliestDynamicId = long("earliestDynamicId")
    val offset_dynamic_id = long(
        "offset_dynamic_id"
    )
}