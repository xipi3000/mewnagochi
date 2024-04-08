package com.projecte.mewnagochi.ui.furniture

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState

import com.projecte.mewnagochi.MovableObjectState

class Chest(
    id: String,
    res: Int,
) : MovableObject(id, res){
    @Composable
    override fun getAppSetting() : MovableObjectState{
        return context.chestDataStore.data.collectAsState(
            initial = MovableObjectState()
        ).value
    }
    override suspend fun setX(x: Float,y:Float) {
        context.chestDataStore.updateData {
            it.copy(x = x,y=y)
        }
    }
    override suspend fun setY(y: Float) {

        context.chestDataStore.updateData {
            it.copy(y = y)
        }
    }
    override suspend fun show() {
        context.chestDataStore.updateData {
            it.copy(show=true)
        }
    }
    override suspend fun hide() {
        context.chestDataStore.updateData {
            it.copy(show=false)
        }
    }


}