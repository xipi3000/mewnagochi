package com.projecte.mewnagochi.ui.theme

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.projecte.mewnagochi.ui.theme.PersonState

class PersonViewModel (
    private val savedStateHandle : SavedStateHandle
) : ViewModel() {

    val offsetX = savedStateHandle.getStateFlow("offsetX",0F)
    val personState = savedStateHandle.getStateFlow("personState",PersonState.IDLE)
    fun setOffsetX(value : Float) {
        savedStateHandle["offsetX"] = value
    }

    fun setState(personState : PersonState) {
        savedStateHandle["personState"] = personState
    }
}