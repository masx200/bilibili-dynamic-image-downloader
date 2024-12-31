package com.github.masx200.bilibili_dynamic_image_downloader

import com.github.masx200.sqlite_java.DB

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