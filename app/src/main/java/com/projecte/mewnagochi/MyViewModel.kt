package com.projecte.mewnagochi

import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.SavedStateHandle

class MyViewModel(
    private val savedStateHandle : SavedStateHandle
) :ViewModel() {



    val navigationBarSelected = savedStateHandle.getStateFlow("navigationBarSelected",0)

    fun setNewSelected(index: Int) {
        savedStateHandle["navigationBarSelected"] = index
    }

}