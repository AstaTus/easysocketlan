package com.astatus.easysocketlansampleserver.entity

/**
 * Created by Administrator on 2017/11/15.
 */
class MessageEntity(sender: Int, message: String) {
    var sender: Int = 0
    var message: String = ""

    init {
        this.sender = sender
        this.message = message
    }
}