package com.github.masx200.bilibili_dynamic_image_downloader

object DynamicRangesSchema : DataSupportTable("dynamicranges") {
    val userId = text("userId")
    val ENDWITH_DYNAMIC_ID = long("ENDWITH_DYNAMIC_ID").index()
    val earliestDynamicId = long("earliestDynamicId").index()
    val offset_dynamic_id = long(
        "offset_dynamic_id"
    ).index()
}