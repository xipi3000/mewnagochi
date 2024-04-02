package com.projecte.mewnagochi.ui

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

import androidx.datastore.dataStore
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.projecte.mewnagochi.HomeScreenViewModel
import com.projecte.mewnagochi.MovableObjectSerializer
import com.projecte.mewnagochi.MovableObjectState
import com.projecte.mewnagochi.ui.theme.PersonState
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

val Context.dataStore1 by dataStore("torch.json", MovableObjectSerializer)


class Torch(
    id: String,
    res: Int,
    x: Float = 0F,
    y: Float = 0F,
) : MovableObject(id, res){
    @Composable
    override fun getAppSeting() : MovableObjectState{
        return context.dataStore1.data.collectAsState(
            initial = MovableObjectState()
        ).value
    }
    override suspend fun setX(x: Float,y:Float) {
        context.dataStore1.updateData {
            it.copy(x = x,y=y)
        }
    }
    override suspend fun setY(y: Float) {

        context.dataStore1.updateData {
            it.copy(y = y)
        }
    }
    override suspend fun show() {
        context.dataStore1.updateData {
            it.copy(show=true)
        }
    }
    override suspend fun hide() {
        context.dataStore1.updateData {
            it.copy(show=false)
        }
    }


}