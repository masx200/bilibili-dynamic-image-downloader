package com.github.masx200.bilibili_dynamic_image_downloader;

public class MyEvent {
    private final String message;

    public MyEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
