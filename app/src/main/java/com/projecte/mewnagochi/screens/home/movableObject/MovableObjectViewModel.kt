package com.projecte.mewnagochi.screens.home.movableObject

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.projecte.mewnagochi.ui.theme.PersonState

class MovableObjectViewModel (
    private val savedStateHandle : SavedStateHandle
) : ViewModel() {

    val offsetX = savedStateHandle.getStateFlow("offsetX",0F)
    val offsetY = savedStateHandle.getStateFlow("offsetY",0F)
    val personState = savedStateHandle.getStateFlow("personState",PersonState.IDLE)
    fun setOffsetX(value : Float) {
        savedStateHandle["offsetX"] = value
    }
    fun setOffsetY(value : Float) {
        savedStateHandle["offsetY"] = value
    }

    fun setState(personState : PersonState) {
        savedStateHandle["personState"] = personState
    }
}