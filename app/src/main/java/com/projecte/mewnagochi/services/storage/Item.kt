package com.projecte.mewnagochi.services.storage

data class Item(
    val id: String ="",
    val name: String = "",
    val res: Int = 0,
    val posX: Float = 0F,
    val posY: Float = 0F,
    val visible: Boolean = false,
    val userId: String = "",
)