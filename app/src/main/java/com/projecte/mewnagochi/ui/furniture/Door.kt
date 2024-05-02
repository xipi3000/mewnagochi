package com.projecte.mewnagochi.ui.furniture

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState

import com.projecte.mewnagochi.screens.home.movableObject.MovableObjectState

class Door(
    id: String,
    res: Int,
) : MovableObject(id, res){
    @Composable
    override fun getAppSetting() : MovableObjectState {
        return context.doorDataStore.data.collectAsState(
            initial = MovableObjectState()
        ).value
    }
    override suspend fun setX(x: Float,y:Float) {
        context.doorDataStore.updateData {
            it.copy(x = x,y=y)
        }
    }
    override suspend fun setY(y: Float) {

        context.doorDataStore.updateData {
            it.copy(y = y)
        }
    }
    override suspend fun show() {
        context.doorDataStore.updateData {
            it.copy(show=true)
        }
    }
    override suspend fun hide() {
        context.doorDataStore.updateData {
            it.copy(show=false)
        }
    }


}