package com.github.masx200.bilibili_dynamic_image_downloader

object DynamicPicturesSchema : DataSupportTable("dynamicpictures") {
    val dynamicId = long("dynamicId")
    val pictureSrc = text("pictureSrc")
    val userId = text("userId")
    val dynamicType = long("dynamicType")
}