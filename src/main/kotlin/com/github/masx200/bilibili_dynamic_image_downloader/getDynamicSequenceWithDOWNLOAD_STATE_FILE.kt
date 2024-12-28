package com.github.masx200.bilibili_dynamic_image_downloader

//import java.sql.DriverManager
import com.github.artbits.jsqlite.DB
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.Table

open class DataSupportTable : Table() {
    val id = long("id")
    val createdAt = long("createdAt")
    val updatedAt = long("updatedAt")
}
fun getDynamicSequenceWithDOWNLOAD_STATE_FILE(options: MyArgs) {
    val dbFile = options.download_state_file

    // 连接到SQLite数据库
//    val connection: Connection = DriverManager.getConnection("jdbc:sqlite:$dbFile")
//    connection.use {
//        println(connection)
//    }
    val db = DB.connect(dbFile)
//
    val dynamicRangesTable = DataBaseTableDao(db, DynamicRanges::class.java)
    DataBaseTableDao(db, SpaceHistory::class.java)
    DataBaseTableDao(db, DynamicPictures::class.java)

    db.use { db ->
//        println(db)
        db.tables(
            SpaceHistory::class.java, DynamicPictures::class.java,
            DynamicRanges::class.java
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
    }
    dynamicRangesTable.findOne(Op.build {
        (DynamicRangesSchema.userId eq options.host_uid)// and (DynamicRanges.id eq 1L)
//eq()
    })

    // 这里需要实现具体的逻辑来获取动态序列
    // 例如，调用API获取动态ID列表，然后转换为Sequence
    // 以下是一个示例实现，假设我们有一个函数fetchDynamicIds返回动态ID列表

}

