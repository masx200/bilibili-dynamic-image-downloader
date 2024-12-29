package com.github.masx200.bilibili_dynamic_image_downloader

//import java.sql.DriverManager
//import com.github.masx200.jsqlite.DB.connect
import com.github.masx200.jsqlite.DB.connect
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

fun getDynamicSequenceWithDOWNLOAD_STATE_FILE(options: MyArgs) {
    val dbFile = options.download_state_file
    var data1 = Database.connect("jdbc:sqlite:$dbFile", "org.sqlite.JDBC")
//    println(data1)
    try {
        val db2 = connect(dbFile)
//        db2.findOne<DynamicRanges>( DynamicRanges::class.java,"dynamicranges.userId = '10013290'")
//        println(db2)
//
        val dynamicRangesTable = DataBaseTableDao(db2, DynamicRanges::class.java)
        DataBaseTableDao(db2, SpaceHistory::class.java)
        DataBaseTableDao(db2, DynamicPictures::class.java)

        db2.use { db ->
//        println(db)
            db.tables(
                SpaceHistory::class.java, DynamicPictures::class.java, DynamicRanges::class.java
            )
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
                val oldranges = (dynamicRangesTable.findOne {
                    (DynamicRangesSchema.userId eq options.host_uid)// and (DynamicRanges.id eq 1L)
//eq()
                })
                println(oldranges)
                if (oldranges == null) {
                    dynamicRangesTable.insert(DynamicRanges {
                        it.userId = options.host_uid
                        if (options.endwith_dynamic_id != "") {
                            it.ENDWITH_DYNAMIC_ID = options.endwith_dynamic_id.toLong()
                        }
                        if (options.offset_dynamic_id != "") {
                            it.offset_dynamic_id = options.offset_dynamic_id.toLong()
                        }

                    })

                } else {
//                    println(oldranges)
                    if (options.offset_dynamic_id != "") {
                        oldranges.offset_dynamic_id = options.offset_dynamic_id.toLong()
                    }
                    if (options.endwith_dynamic_id != "") {
                        oldranges.ENDWITH_DYNAMIC_ID = options.endwith_dynamic_id.toLong()
                    }
                    dynamicRangesTable.update(oldranges)
                }
//                println(dynamicRangesTable.findOne(Op.build {
//                    (DynamicRangesSchema.userId eq options.host_uid)// and (DynamicRanges.id eq 1L)
////eq()
//                }))
//
                println((dynamicRangesTable.findOne {
                    (DynamicRangesSchema.userId eq options.host_uid)// and (DynamicRanges.id eq 1L)
//eq()
                }))
            }
        }

    } finally {
//        println(  data1.connector())
        data1.connector().close()
    }
//    Database.close
    // 连接到SQLite数据库
//    val connection: Connection = DriverManager.getConnection("jdbc:sqlite:$dbFile")
//    connection.use {
//        println(connection)
//    }


    // 这里需要实现具体的逻辑来获取动态序列
    // 例如，调用API获取动态ID列表，然后转换为Sequence
    // 以下是一个示例实现，假设我们有一个函数fetchDynamicIds返回动态ID列表

}

