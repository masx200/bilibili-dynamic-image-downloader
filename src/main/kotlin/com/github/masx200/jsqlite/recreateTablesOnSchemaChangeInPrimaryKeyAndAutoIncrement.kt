package com.github.masx200.jsqlite

import com.github.masx200.bilibili_dynamic_image_downloader.DynamicPictures
import com.github.masx200.bilibili_dynamic_image_downloader.DynamicRanges
import com.github.masx200.bilibili_dynamic_image_downloader.SpaceHistory

fun recreateTablesOnSchemaChangeInPrimaryKeyAndAutoIncrement(db: DB): List<String> {
    var resultList = mutableListOf<String>()
    for (klass in listOf(SpaceHistory::class.java, DynamicPictures::class.java, DynamicRanges::class.java)) {


        if (db.checkTableDifferenceInPrimaryKeyAndAutoIncrement(klass)) {
            db.drop(klass).forEach {
                resultList.add(it)
            }

            db.create(klass).forEach {
                resultList.add(it)
            }

        }
    }
    return resultList
}