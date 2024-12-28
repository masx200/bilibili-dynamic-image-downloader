package com.github.masx200.bilibili_dynamic_image_downloader

object SpaceHistorySchema : DataSupportTable("spacehistory") {
    val userId = text("userId")
    val dynamicId = long("dynamicId")
    val dynamicType = long("dynamicType")

}