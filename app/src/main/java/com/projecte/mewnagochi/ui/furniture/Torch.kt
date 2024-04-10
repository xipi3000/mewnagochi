package com.projecte.mewnagochi.ui.furniture

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState

import com.projecte.mewnagochi.MovableObjectState

class Torch(
    id: String,
    res: Int,
) : MovableObject(id, res){
    @Composable
    override fun getAppSetting() : MovableObjectState{
        return context.torchDataStore.data.collectAsState(
            initial = MovableObjectState()
        ).value
    }
    override suspend fun setX(x: Float,y:Float) {
        context.torchDataStore.updateData {
            it.copy(x = x,y=y)
        }
    }
    override suspend fun setY(y: Float) {

        context.torchDataStore.updateData {
            it.copy(y = y)
        }
    }
    override suspend fun show() {
        context.torchDataStore.updateData {
            it.copy(show=true)
        }
    }
    override suspend fun hide() {
        context.torchDataStore.updateData {
            it.copy(show=false)
        }
    }


}