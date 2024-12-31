package com.github.masx200.bilibili_dynamic_image_downloader.schema

import com.github.masx200.jsqlite.DataSupportTable

object DynamicRangesSchema : DataSupportTable("dynamicranges") {
    val userId = text("userId")
    val endwith_dynamic_id = long("endwith_dynamic_id").index()
    val earliestDynamicId = long("earliestDynamicId").index()
    val offset_dynamic_id = long(
        "offset_dynamic_id"
    ).index()
}