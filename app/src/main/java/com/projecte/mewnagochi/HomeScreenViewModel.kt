package com.projecte.mewnagochi

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.projecte.mewnagochi.ui.Location
import com.projecte.mewnagochi.ui.theme.MovableObject
import com.projecte.mewnagochi.ui.theme.PersonState

class HomeScreenViewModel (
    private val savedStateHandle : SavedStateHandle
) : ViewModel() {

    val selectedFurnitureId = savedStateHandle.getStateFlow("selectedFurnitureId","")
    val deletedId = savedStateHandle.getStateFlow("deletedId","")
    val isAnyFurnitureSelected = savedStateHandle.getStateFlow("isAnyFurnitureSelected",false)
    val  furnitures = savedStateHandle.getStateFlow("furnitures", mapOf<String,MovableObject?>())

    fun selectFurniture(id : String) {
        savedStateHandle["selectedFurnitureId"] = id
        savedStateHandle["isAnyFurnitureSelected"] = true
    }
    fun deselectFurniture(){

        savedStateHandle["isAnyFurnitureSelected"] = false
    }
    fun updateItem( id: String, obj : MovableObject?){

        savedStateHandle["furnitures"] = furnitures.value + (id to  obj )
    }

    fun deleteSelected(id:String) {
        savedStateHandle["selectedFurnitureId"] = ""
        val map = furnitures.value.toMutableMap()
        map.remove(id)
        val newMap = map.toMap()
        savedStateHandle["isAnyFurnitureSelected"] = false
        savedStateHandle["deletedId"] = id
        savedStateHandle["furnitures"] = newMap
    }

}