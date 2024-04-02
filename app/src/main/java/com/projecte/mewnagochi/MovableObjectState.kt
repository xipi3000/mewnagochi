package com.projecte.mewnagochi
import kotlinx.serialization.Serializable

@Serializable
data class MovableObjectState(
    val x: Float =0F,
    val y: Float =0F,
    val show: Boolean = false,
)