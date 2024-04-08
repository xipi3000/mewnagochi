package com.projecte.mewnagochi

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.projecte.mewnagochi.ui.MovableObject

class HomeScreenViewModel (
    private val savedStateHandle : SavedStateHandle
) : ViewModel() {
    val addedObject = savedStateHandle.getStateFlow("addedObject",0)
    val deletedObject = savedStateHandle.getStateFlow("deletedObject",0)
    val selectedFurnitureId = savedStateHandle.getStateFlow("selectedFurnitureId",0)
    val isEditingFurniture = savedStateHandle.getStateFlow("isEditingFurniture",false)
    val isAnyFurnitureSelected = savedStateHandle.getStateFlow("isAnyFurnitureSelected",false)
    val  furnitures = savedStateHandle.getStateFlow("furnitures", mutableListOf<Int>(0))
    fun addObject(id: Int){
        savedStateHandle["addedObject"] = id
    }
    fun deleteObject(id: Int){
        savedStateHandle["deletedObject"] = id
    }
    fun startEditing(){
        savedStateHandle["isEditingFurniture"] = true
    }
    fun stopEditing(){
        savedStateHandle["isEditingFurniture"] = false
    }
    fun selectFurniture(id : Int) {
        savedStateHandle["selectedFurnitureId"] = id
        savedStateHandle["isAnyFurnitureSelected"] = true
    }
    fun deselectFurniture(){

        savedStateHandle["isAnyFurnitureSelected"] = false
    }
    fun addItem( id: Int){
        val list = furnitures.value
        if(!list.contains(id)) {
            list.add(id)
            savedStateHandle["furnitures"] = list
        }
    }
    fun removeItem( id: Int){
        val list = furnitures.value
        if(list.contains(id)) {
            list.remove(id)
            savedStateHandle["furnitures"] = list
        }
    }


    fun deleteSelected() {
        savedStateHandle["selectedFurnitureId"] = 0
        savedStateHandle["isAnyFurnitureSelected"] = false

    }

}