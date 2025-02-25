package com.github.masx200.bilibili_dynamic_image_downloader


//import com.github.masx200.bilibili_dynamic_image_downloader.schema.DynamicRangesSchema.earliestDynamicId
import com.github.masx200.biliClient.model.dynamic.Dynamic
import com.github.masx200.biliClient.model.dynamic.Picture
import com.github.masx200.bilibili_dynamic_image_downloader.entity.DynamicPictures
import com.github.masx200.bilibili_dynamic_image_downloader.entity.DynamicRanges
import com.github.masx200.bilibili_dynamic_image_downloader.entity.SpaceHistory
import com.github.masx200.bilibili_dynamic_image_downloader.schema.DynamicRangesSchema
import com.github.masx200.bilibili_dynamic_image_downloader.schema.SpaceHistorySchema
import com.github.masx200.sqlite_java.DB.Companion.connect
import com.github.masx200.sqlite_java.recreateColumnsOnSchemaChangeInColumnTypes
import com.github.masx200.sqlite_java.recreateTablesOnSchemaChangeInPrimaryKeyAndAutoIncrement
import kotlinx.coroutines.flow.Flow
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

suspend fun getDynamicSequenceWithDOWNLOAD_STATE_FILE(options: MyArgs, cookie_str: String) {
    val dbFile = options.download_state_file
    var data1 = Database.connect("jdbc:sqlite:$dbFile", "org.sqlite.JDBC")

    var listeners = mutableListOf<AutoCloseable>()

    try {
        val db2 = connect(dbFile)


        val dynamicRangesTable = DataBaseTableDao(db2, DynamicRanges::class.java)

        val spaceHistoryTable = DataBaseTableDao(db2, SpaceHistory::class.java)


        val dynamicPicturesTable = DataBaseTableDao(db2, DynamicPictures::class.java)

        val sqlIdentifiers = listOf("select", "create", "alter", "delete", "drop", "insert", "update")

        db2.use { db ->

            sqlIdentifiers.forEach { identifier ->
                listeners.add(

                    registerEventListenerForIdentifier(db, identifier)
                )
            }

            var strings = db.tables(
                SpaceHistory::class.java, DynamicPictures::class.java, DynamicRanges::class.java
            )
            if (strings.isNotEmpty()) {
                println(
                    strings
                )
            }
            var strings1 = db.dropUnusedColumns(
                SpaceHistory::class.java, DynamicPictures::class.java, DynamicRanges::class.java
            )
            if (strings1.isNotEmpty()) {
                println(
                    strings1
                )
            }


            if (options.force_recreate) {
                var strings2 = recreateColumnsOnSchemaChangeInColumnTypes(
                    db, DynamicRanges::class.java, DynamicPictures::class.java, DynamicRanges::class.java
                )
                if (strings2.isNotEmpty()) {
                    println(
                        strings2
                    )
                }
                var strings3 = recreateTablesOnSchemaChangeInPrimaryKeyAndAutoIncrement(
                    db, SpaceHistory::class.java, DynamicPictures::class.java, DynamicRanges::class.java
                )

                if (strings3.isNotEmpty()) {
                    println(
                        strings3
                    )
                }

            }

            var earliestDynamicIdinoldranges: Long? = null
            transaction(data1) {

                val oldranges = (dynamicRangesTable.findOneByPredicate {
                    (DynamicRangesSchema.userId eq options.host_uid)

                })

                if (oldranges == null) {
                    val data11 = DynamicRanges {
                        it.userId = options.host_uid

                        it.endwith_dynamic_id =
                            if (options.endwith_dynamic_id != "") options.endwith_dynamic_id.toLong() else 0L


                        it.offset_dynamic_id =
                            if (options.offset_dynamic_id != "") options.offset_dynamic_id.toLong() else 0L


                    }


                    println(dynamicRangesTable.insert(data11))

                } else {
                    earliestDynamicIdinoldranges = oldranges.earliestDynamicId
                    val data11 = DynamicRanges {
                        it.userId = options.host_uid

                        it.endwith_dynamic_id =
                            if (options.endwith_dynamic_id != "") options.endwith_dynamic_id.toLong() else 0L


                        it.offset_dynamic_id =
                            if (options.offset_dynamic_id != "") options.offset_dynamic_id.toLong() else 0L


                    }
                    data11.id = oldranges.id



                    println(dynamicRangesTable.updateById(data11))
                }


            }

            val maxdynamicid = transaction(data1) {
                spaceHistoryTable.maxByPredicate(SpaceHistorySchema.dynamicId.name) {
                    (SpaceHistorySchema.userId eq options.host_uid)
                }
            }
            val mindynamicid = transaction(data1) {
                spaceHistoryTable.minByPredicate(SpaceHistorySchema.dynamicId.name) {
                    (SpaceHistorySchema.userId eq options.host_uid)
                }
            }
            if (mindynamicid != null && mindynamicid != 0 && maxdynamicid != 0 && maxdynamicid != null) {
                println(
                    "现有的最新的动态内容id为" + maxdynamicid
                )
                println(
                    "现有的最旧的动态内容id为" + mindynamicid
                )
                println(
                    "开始增量更新更新的内容"
                )
                //增量更新,比现有的更大的动态id
                val iteritemslarger: Flow<Dynamic> = getDynamicSequence(
                    options.offset_dynamic_id,
                    options.host_uid,
                    endwith_dynamic_id = maxdynamicid.toString(),
                    cookie_str
                )
                iteritemslarger.collect { item ->

                    var datatoinsertcallbacks = mutableListOf<() -> Unit>()
                    println(item)
                    println("https://t.bilibili.com/" + item.data!!.dynamic_id)


                    val spaceHistory = SpaceHistory {
                        it.userId = options.host_uid
                        it.dynamicId = item.desc?.dynamic_id_str?.toLong()
                        it.dynamicType = item.desc?.type?.toLong()

                        it.dynamicOriginId = item.desc?.orig_dy_id_str?.toLong()
                        it.dynamicOriginType = item.desc?.orig_type?.toLong()
                    }
                    println(spaceHistory)
                    assert(spaceHistory.dynamicId != null)
                    assert(spaceHistory.dynamicType != null)
                    assert(spaceHistory.userId != null)
                    datatoinsertcallbacks.add {
                        println(
                            spaceHistoryTable.insert(spaceHistory)
                        )
                    }
//                    item.desc?.dynamic_id_str?.toLong().let {
//                        earliestDynamicId = it
//                    }

                    if (item.detail != null) {

                        // 如果动态项包含文章信息，则处理图片链接

                        // 如果动态项包含图片信息，则处理图片链接
                        if (item.detail!!.pictures != null) {
                            val pictures = item.detail!!.pictures


                            if (pictures is Iterable<Picture?>) {
                                pictures.forEach { picture ->
                                    processAndInsertDynamicPicture(
                                        picture,
                                        item,
                                        item.data,
                                        options.host_uid,
                                        datatoinsertcallbacks,
                                        dynamicPicturesTable
                                    )
                                }
                            }
                        }
                    }
                    item.origin?.detail?.pictures.let { pictures ->
                        if (pictures != null) {

                            pictures.forEach { picture ->
                                processAndInsertDynamicPicture(
                                    picture,
                                    item,
                                    item.data,
                                    options.host_uid,
                                    datatoinsertcallbacks,
                                    dynamicPicturesTable
                                )

                            }
                        }
                    }
                    datatoinsertcallbacks.forEach { it() }
                }
                println(
                    "完成增量更新更新的内容"
                )

                if (earliestDynamicIdinoldranges != null && earliestDynamicIdinoldranges != 0L && mindynamicid.toString() == earliestDynamicIdinoldranges.toString()) {

                    println(
                        "无需增量更新更旧的内容,数据库中已经存在的最旧动态id为" + earliestDynamicIdinoldranges.toString() + "已经没有更旧的动态内容"
                    )
                    return
                }
                println(
                    "开始增量更新更旧的内容"
                )
//增量更新,比现有的更小的动态id
                val iteritems: Flow<Dynamic> = getDynamicSequence(
                    mindynamicid.toString(),
                    options.host_uid,
                    endwith_dynamic_id = options.endwith_dynamic_id,
                    cookie_str,
                    acceptEmpty = true
                )
                var earliestDynamicId: Long? = null
                iteritems.collect { item ->

                    var datatoinsertcallbacks = mutableListOf<() -> Unit>()
                    println(item)
                    println("https://t.bilibili.com/" + item.data!!.dynamic_id)


                    val spaceHistory = SpaceHistory {
                        it.userId = options.host_uid
                        it.dynamicId = item.desc?.dynamic_id_str?.toLong()
                        it.dynamicType = item.desc?.type?.toLong()

                        it.dynamicOriginId = item.desc?.orig_dy_id_str?.toLong()
                        it.dynamicOriginType = item.desc?.orig_type?.toLong()
                    }
                    println(spaceHistory)
                    assert(spaceHistory.dynamicId != null)
                    assert(spaceHistory.dynamicType != null)
                    assert(spaceHistory.userId != null)
                    datatoinsertcallbacks.add {
                        println(
                            spaceHistoryTable.insert(spaceHistory)
                        )
                    }
                    item.desc?.dynamic_id_str?.toLong().let {
                        earliestDynamicId = it
                    }

                    if (item.detail != null) {

                        // 如果动态项包含文章信息，则处理图片链接

                        // 如果动态项包含图片信息，则处理图片链接
                        if (item.detail!!.pictures != null) {
                            val pictures = item.detail!!.pictures


                            if (pictures is Iterable<Picture?>) {
                                pictures.forEach { picture ->
                                    processAndInsertDynamicPicture(
                                        picture,
                                        item,
                                        item.data,
                                        options.host_uid,
                                        datatoinsertcallbacks,
                                        dynamicPicturesTable
                                    )
                                }
                            }
                        }
                    }
                    item.origin?.detail?.pictures.let { pictures ->
                        if (pictures != null) {

                            pictures.forEach { picture ->
                                processAndInsertDynamicPicture(
                                    picture,
                                    item,
                                    item.data,
                                    options.host_uid,
                                    datatoinsertcallbacks,
                                    dynamicPicturesTable
                                )

                            }
                        }
                    }
                    datatoinsertcallbacks.forEach { it() }
                }
                transaction(data1) {
                    earliestDynamicId.let { earliestDynamicId ->
                        if (earliestDynamicId != null) {
                            val data11 = DynamicRanges {
                                it.earliestDynamicId = earliestDynamicId

                            }


                            assert(data11.earliestDynamicId != null)

                            println(dynamicRangesTable.updateByPredicate(data11) {
                                DynamicRangesSchema.userId eq options.host_uid
                            })
                        }
                    }
                }
                println(
                    "完成增量更新更旧的内容"
                )
            } else {
                println(
                    "现有的数据库最新的动态内容不存在"
                )
                println(
                    "现有的数据库最旧的动态内容不存在"
                )
                //全量更新
                println(

                    "开始全量更新"
                )
                val iteritems: Flow<Dynamic> = getDynamicSequence(
                    options.offset_dynamic_id, options.host_uid, options.endwith_dynamic_id, cookie_str
                )
                var earliestDynamicId: Long? = null
                iteritems.collect { item ->

                    var datatoinsertcallbacks = mutableListOf<() -> Unit>()
                    println(item)
                    println("https://t.bilibili.com/" + item.data!!.dynamic_id)


                    val spaceHistory = SpaceHistory {
                        it.userId = options.host_uid
                        it.dynamicId = item.desc?.dynamic_id_str?.toLong()
                        it.dynamicType = item.desc?.type?.toLong()

                        it.dynamicOriginId = item.desc?.orig_dy_id_str?.toLong()
                        it.dynamicOriginType = item.desc?.orig_type?.toLong()
                    }
                    println(spaceHistory)
                    assert(spaceHistory.dynamicId != null)
                    assert(spaceHistory.dynamicType != null)
                    assert(spaceHistory.userId != null)
                    datatoinsertcallbacks.add {
                        println(
                            spaceHistoryTable.insert(spaceHistory)
                        )
                    }
                    item.desc?.dynamic_id_str?.toLong().let {
                        earliestDynamicId = it
                    }

                    if (item.detail != null) {

                        // 如果动态项包含文章信息，则处理图片链接

                        // 如果动态项包含图片信息，则处理图片链接
                        if (item.detail!!.pictures != null) {
                            val pictures = item.detail!!.pictures


                            if (pictures is Iterable<Picture?>) {
                                pictures.forEach { picture ->
                                    processAndInsertDynamicPicture(
                                        picture,
                                        item,
                                        item.data,
                                        options.host_uid,
                                        datatoinsertcallbacks,
                                        dynamicPicturesTable
                                    )
                                }
                            }
                        }
                    }
                    item.origin?.detail?.pictures.let { pictures ->
                        if (pictures != null) {

                            pictures.forEach { picture ->
                                processAndInsertDynamicPicture(
                                    picture,
                                    item,
                                    item.data,
                                    options.host_uid,
                                    datatoinsertcallbacks,
                                    dynamicPicturesTable
                                )

                            }
                        }
                    }
                    datatoinsertcallbacks.forEach { it() }
                }
                transaction(data1) {
                    earliestDynamicId.let { earliestDynamicId ->
                        if (earliestDynamicId != null) {
                            val data11 = DynamicRanges {
                                it.earliestDynamicId = earliestDynamicId

                            }


                            assert(data11.earliestDynamicId != null)

                            println(dynamicRangesTable.updateByPredicate(data11) {
                                DynamicRangesSchema.userId eq options.host_uid
                            })
                        }
                    }
                }
                println(

                    "完成全量更新"
                )
            }

        }

    } finally {

        data1.connector().close()


        listeners.forEach { it.close() }
    }

}

