package com.github.masx200.bilibili_dynamic_image_downloader.schema

import com.github.masx200.sqlite_java.DataSupportTable

object DynamicPicturesSchema : DataSupportTable("dynamicpictures") {
    val dynamicId = long("dynamicId").index()
    val pictureSrc = text("pictureSrc").index()
    val userId = text("userId").index()
//    val dynamicType = long("dynamicType")
}