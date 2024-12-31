package com.github.masx200.bilibili_dynamic_image_downloader.schema

import com.github.masx200.sqlite_java.DataSupportTable

object SpaceHistorySchema : DataSupportTable("spacehistory") {
    val userId = text("userId").index()
    val dynamicId = long("dynamicId").index()
    val dynamicType = long("dynamicType").index()

}