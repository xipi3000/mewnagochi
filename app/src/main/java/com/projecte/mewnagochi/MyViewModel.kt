package com.projecte.mewnagochi

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class MyViewModel(
    private val savedStateHandle : SavedStateHandle
) :ViewModel() {


    val navigationBarSelected = savedStateHandle.getStateFlow("navigationBarSelected",0)

    fun setNewSelected(index: Int) {
        savedStateHandle["navigationBarSelected"] = index
    }

}