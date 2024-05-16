package com.projecte.mewnagochi.screens.profile

import android.content.Context
import androidx.datastore.dataStore

import androidx.datastore.core.Serializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream


val Context.InternetPreferenceStateDataStore by dataStore("internet_preference_state.json", MovableObjectSerializer)
@Serializable
data class InternetPreferenceState(
    val internetPreferenceSelected: Int = 0
)

@Suppress("BlockingMethodInNonBlockingContext")
object MovableObjectSerializer : Serializer<InternetPreferenceState> {

    override val defaultValue: InternetPreferenceState
        get() = InternetPreferenceState()

    override suspend fun readFrom(input: InputStream): InternetPreferenceState {
        return try {
            Json.decodeFromString(
                deserializer = InternetPreferenceState.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (e: SerializationException) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: InternetPreferenceState, output: OutputStream) {
        output.write(
            Json.encodeToString(
                serializer = InternetPreferenceState.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}