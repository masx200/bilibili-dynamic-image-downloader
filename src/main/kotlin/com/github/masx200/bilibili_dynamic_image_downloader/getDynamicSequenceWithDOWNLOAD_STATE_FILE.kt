package com.github.masx200.bilibili_dynamic_image_downloader

//import java.sql.DriverManager
import com.github.artbits.jsqlite.DB
import com.github.masx200.biliClient.model.dynamic.Dynamic

fun getDynamicSequenceWithDOWNLOAD_STATE_FILE(options: MyArgs): Sequence<Dynamic> {
    val dbFile = options.download_state_file

    // 连接到SQLite数据库
//    val connection: Connection = DriverManager.getConnection("jdbc:sqlite:$dbFile")
//    connection.use {
//        println(connection)
//    }
    val db = DB.connect(dbFile)
    db.use { db ->
//        println(db)
        db.tables(SpaceHistory::class.java, DynamicPictures::class.java)
    }


    // 这里需要实现具体的逻辑来获取动态序列
    // 例如，调用API获取动态ID列表，然后转换为Sequence
    // 以下是一个示例实现，假设我们有一个函数fetchDynamicIds返回动态ID列表
    TODO()
}

