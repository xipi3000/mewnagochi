package com.projecte.mewnagochi

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class PersonViewModel (
    private val savedStateHandle : SavedStateHandle
) : ViewModel() {

    val offsetX = savedStateHandle.getStateFlow("offsetX",0F)

    fun setOffsetX(value : Float) {
        savedStateHandle["offsetX"] = value
    }


}