package com.github.masx200.bilibili_dynamic_image_downloader

import com.google.common.eventbus.Subscribe

class MyEventListener(var callback: (MyEvent) -> Unit) {
    @Subscribe
    fun handleMyEvent(event: MyEvent) {
        callback(event)
    }
}

