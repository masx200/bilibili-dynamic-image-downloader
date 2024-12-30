package com.github.masx200.bilibili_dynamic_image_downloader

//import java.sql.DriverManager
//import com.github.masx200.jsqlite.DB.connect
import com.github.masx200.jsqlite.DB
import com.github.masx200.jsqlite.DB.Companion.connect
import com.github.masx200.jsqlite.recreateColumnsOnSchemaChangeInColumnTypes
import com.github.masx200.jsqlite.recreateTablesOnSchemaChangeInPrimaryKeyAndAutoIncrement
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

fun getDynamicSequenceWithDOWNLOAD_STATE_FILE(options: MyArgs, cookie_str: String) {
    val dbFile = options.download_state_file
    var data1 = Database.connect("jdbc:sqlite:$dbFile", "org.sqlite.JDBC")

    var listeners = mutableListOf<AutoCloseable>()
//    println(data1)
    try {
        val db2 = connect(dbFile)
//        db2.findOne<DynamicRanges>( DynamicRanges::class.java,"dynamicranges.userId = '10013290'")
//        println(db2)
//
        val dynamicRangesTable = DataBaseTableDao(db2, DynamicRanges::class.java)
//        DataBaseTableDao(db2, SpaceHistory::class.java)
//        DataBaseTableDao(db2, DynamicPictures::class.java)
        val sqlIdentifiers = listOf("select", "create", "alter", "delete", "drop", "insert", "update")

        db2.use { db ->

            sqlIdentifiers.forEach { identifier ->
                listeners.add(

                    registerEventListenerForIdentifier(db, identifier)
                )
            }
//        println(db)
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

//        db.findOne<DynamicRanges>(DynamicRanges::class.java){
//
//        }
//全量同步
//        修改DynamicRanges
//        val dynamicRanges = db.select<DynamicRanges>().where {
//            DynamicRanges::userId.eq(options.host_uid)
//        }.toList()
            //增量同步
            transaction(data1) {
//                println(DynamicRanges())
                val oldranges = (dynamicRangesTable.findOneByPredicate {
                    (DynamicRangesSchema.userId eq options.host_uid)// and (DynamicRanges.id eq 1L)
//eq()
                })
//                println(oldranges)
                if (oldranges == null) {
                    val data11 = DynamicRanges {
                        it.userId = options.host_uid

                        it.endwith_dynamic_id =
                            if (options.endwith_dynamic_id != "") options.endwith_dynamic_id.toLong() else null


                        it.offset_dynamic_id =
                            if (options.offset_dynamic_id != "") options.offset_dynamic_id.toLong() else null


                    }
//                    println(data11)

                    println(dynamicRangesTable.insert(data11))

                } else {
                    val data11 = DynamicRanges {
                        it.userId = options.host_uid

                        it.endwith_dynamic_id =
                            if (options.endwith_dynamic_id != "") options.endwith_dynamic_id.toLong() else null


                        it.offset_dynamic_id =
                            if (options.offset_dynamic_id != "") options.offset_dynamic_id.toLong() else null


                    }
                    data11.id = oldranges.id
//                    println(oldranges)

//                    println(oldranges)
                    println(dynamicRangesTable.updateById(data11))
                }
                println(dynamicRangesTable.deleteByPredicate {
                    DynamicRangesSchema.userId eq options.host_uid
                })
            }
        }

    } finally {
//        println(  data1.connector())
        data1.connector().close()


        listeners.forEach { it.close() }
    }

}

fun registerEventListenerForIdentifier(db: DB, identifier: String): AutoCloseable {
    var asyncEventBus = db.getAsyncEventBus(identifier)
    var myEventListener = MyEventListener {
        System.out.println("$identifier:" + "Received event:" + it.message)
    }
    asyncEventBus.register(myEventListener)
    return object : AutoCloseable {
        override fun close() {
            asyncEventBus.unregister(myEventListener)
        }
    }

}
