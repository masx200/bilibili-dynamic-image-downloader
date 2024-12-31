package com.github.masx200.bilibili_dynamic_image_downloader


import com.github.masx200.biliClient.model.dynamic.Dynamic
import com.github.masx200.biliClient.model.dynamic.Picture
import com.github.masx200.jsqlite.DB.Companion.connect
import com.github.masx200.jsqlite.recreateColumnsOnSchemaChangeInColumnTypes
import com.github.masx200.jsqlite.recreateTablesOnSchemaChangeInPrimaryKeyAndAutoIncrement
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

fun getDynamicSequenceWithDOWNLOAD_STATE_FILE(options: MyArgs, cookie_str: String) {
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


            val iteritems: Sequence<Dynamic> = getDynamicSequence(options, cookie_str)
            var earliestDynamicId: Long? = null
            for (item in iteritems) {

                var datatoinsertcallbacks = mutableListOf<() -> Unit>()
                println(item)
                println("https://t.bilibili.com/" + item.data!!.dynamic_id)


                val spaceHistory = SpaceHistory {
                    it.userId = options.host_uid
                    it.dynamicId = item.desc?.dynamic_id_str?.toLong()
                    it.dynamicType = item.desc?.type?.toLong()
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
                                val str = picture!!.img_src

                                str?.let { url ->


                                    println(url)


                                    var dynamicPictures = DynamicPictures {

                                        it.dynamicId = item.data!!.dynamic_id_str?.toLong()
                                        it.pictureSrc = url.toString()
                                        it.userId = options.host_uid
                                    }
                                    println(dynamicPictures)
                                    assert(
                                        dynamicPictures.dynamicId != null && dynamicPictures.pictureSrc != null && dynamicPictures.userId != null
                                    )
                                    datatoinsertcallbacks.add {
                                        println(
                                            dynamicPicturesTable.insert(dynamicPictures)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                datatoinsertcallbacks.forEach { it() }
            }
            transaction(data1) {
                earliestDynamicId.let {
                    if (it != null) {
                        val data11 = DynamicRanges {
                            it.earliestDynamicId

                        }




                        println(dynamicRangesTable.updateByPredicate(data11) {
                            DynamicRangesSchema.userId eq options.host_uid
                        })
                    }
                }
            }

        }

    } finally {

        data1.connector().close()


        listeners.forEach { it.close() }
    }

}

