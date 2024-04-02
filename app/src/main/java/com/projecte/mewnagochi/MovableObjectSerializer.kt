package com.projecte.mewnagochi

import androidx.datastore.core.Serializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

@Suppress("BlockingMethodInNonBlockingContext")
object MovableObjectSerializer : Serializer<MovableObjectState> {

    override val defaultValue: MovableObjectState
        get() = MovableObjectState()

    override suspend fun readFrom(input: InputStream): MovableObjectState {
        return try {
            Json.decodeFromString(
                deserializer = MovableObjectState.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (e: SerializationException) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: MovableObjectState, output: OutputStream) {
        output.write(
            Json.encodeToString(
                serializer = MovableObjectState.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}