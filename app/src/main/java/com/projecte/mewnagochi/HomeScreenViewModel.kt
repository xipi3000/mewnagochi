package com.projecte.mewnagochi

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.projecte.mewnagochi.ui.theme.PersonState

class HomeScreenViewModel (
    private val savedStateHandle : SavedStateHandle
) : ViewModel() {

    val selectedFurnitureId = savedStateHandle.getStateFlow("selectedFurnitureId","")
    val isAnyFurnitureSelected = savedStateHandle.getStateFlow("isAnyFurnitureSelected",false)

    fun selectFurniture(id : String) {
        savedStateHandle["selectedFurnitureId"] = id
        savedStateHandle["isAnyFurnitureSelected"] = true
    }
    fun deselectFurniture(){

        savedStateHandle["isAnyFurnitureSelected"] = false
    }

}