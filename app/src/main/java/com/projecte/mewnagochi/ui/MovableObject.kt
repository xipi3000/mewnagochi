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
import androidx.compose.runtime.LaunchedEffect
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



open class MovableObject (
    val id: String,
    val res: Int,
    private var x: Float = 0F,
    private var y : Float= 0F,

) {


    lateinit var  context : Context
    @Composable
    open fun getAppSeting() : MovableObjectState{
        return context.dataStore.data.collectAsState(
            initial = MovableObjectState()
        ).value
    }


    @Composable
    fun Draw(viewModel: HomeScreenViewModel = viewModel(),
    ){
        context = LocalContext.current
        val appSettings = getAppSeting()
        Log.i("show",appSettings.show.toString())
        var offsetX  by remember { mutableFloatStateOf(appSettings.x) }
        var offsetY by remember { mutableFloatStateOf(appSettings.y) }
        var personState  by remember { mutableStateOf(PersonState.IDLE) }
        val addedObject by viewModel.addedObject.collectAsState()
        val deletedObject by viewModel.deletedObject.collectAsState()
        val selectedId by viewModel.selectedFurnitureId.collectAsState()
        val isEditingFurniture by viewModel.isEditingFurniture.collectAsState()
        val funr by viewModel.furnitures.collectAsState()
        var visible by remember {
            mutableStateOf(false)
        }

        val scope = rememberCoroutineScope()
        if(addedObject==res) {

            LaunchedEffect(Unit){
                show()
                visible = true
                viewModel.addObject(0)
            }


        }
        if(deletedObject ==res) {
            Log.i("deleting",res.toString())
            LaunchedEffect(Unit){
                hide()
                visible = false
                viewModel.deleteObject(0)
                viewModel.removeItem(res)
            }

        }
        if(selectedId!=res){
            personState = PersonState.IDLE
        }
        visible = appSettings.show
        offsetX = appSettings.x
        offsetY = appSettings.y
        val lifecycleOwner = LocalLifecycleOwner.current
        DisposableEffect(key1 = lifecycleOwner, effect = {

            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {

                    Log.i("asdf",appSettings.x.toString())
                    Log.i("asdf",appSettings.y.toString())
                    offsetX = appSettings.x
                    offsetY = appSettings.y
                }
                if (event == Lifecycle.Event.ON_PAUSE) {


                }
            }

            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        })
        if(appSettings.show) {
            viewModel.addItem(res)
            Image(
                painter = painterResource(id = res),
                contentDescription = "",
                modifier = Modifier
                    .offset {
                        if (personState == PersonState.BEING_DRAGED) {
                            IntOffset(
                                offsetX.roundToInt(),
                                offsetY.roundToInt()
                            )

                        }
                        else{
                            IntOffset(
                                appSettings.x.roundToInt(),
                                appSettings.y.roundToInt()
                            )
                        }

                    }
                    .then(
                        if (isEditingFurniture) {
                            Modifier
                                .pointerInput(Unit) {
                                    detectDragGestures(
                                        onDragStart = {

                                            if (personState == PersonState.CLICKED)
                                                personState = PersonState.BEING_DRAGED
                                        },
                                        onDragEnd = {
                                            viewModel.deselectFurniture()
                                            personState = PersonState.FALLING
                                            x = offsetX
                                            y = offsetY
                                            scope.launch {
                                                Log.i("offset", offsetX.toString())
                                                Log.i("saved", appSettings.x.toString())
                                                val increment = offsetX + appSettings.x
                                                Log.i("inc", increment.toString())

                                                setX(offsetX, offsetY)

                                                //setY(offsetY)
                                                //Log.i("saved",appSettings.x.toString())
                                            }

                                            //viewModel.deleteSelected(id)
                                            /* viewModel.updateItem(
                                            id,
                                            MovableObject(
                                                x = offsetX,
                                                y = offsetY,
                                                id = id,
                                                res = res,
                                                fileName = ""
                                            )
                                        )*/
                                            Log.i("huh", funr.size.toString())
                                            //viewModel.deselectFurniture()
                                            //personState = PersonState.IDLE
                                        }
                                    ) { change, dragAmount ->
                                        if (personState == PersonState.BEING_DRAGED) {
                                            change.consume()
                                            Log.i("log", dragAmount.x.toString())


                                            offsetX += dragAmount.x
                                            offsetY += dragAmount.y

                                            x = offsetX
                                            y = offsetY
                                        }
                                    }
                                }
                                .clickable(
                                    interactionSource = MutableInteractionSource(),
                                    indication = null
                                ) {
                                    personState = if (personState != PersonState.CLICKED) {
                                        viewModel.selectFurniture(res)
                                        Log.i("selected", res.toString())
                                        PersonState.CLICKED
                                    } else {

                                        x = offsetX
                                        y = offsetY
                                        viewModel.deselectFurniture()
                                        PersonState.IDLE
                                    }
                                }
                                .then(
                                    if (personState == PersonState.CLICKED || personState == PersonState.BEING_DRAGED) Modifier.border(
                                        2.dp,
                                        Color.Yellow
                                    ) else Modifier
                                )
                        } else Modifier
                    )
            )
        }
    }
     open suspend fun setX(x: Float, y:Float) {
        context.dataStore.updateData {
            it.copy(x = x,y=y)
        }
    }
     open suspend fun setY(y: Float) {

        context.dataStore.updateData {
            it.copy(y = y)
        }
    }

    open suspend fun show() {
        context.dataStore.updateData {
            it.copy(show=true)
        }
    }
    open suspend fun hide() {
        context.dataStore.updateData {
            it.copy(show=false)
        }
    }
}