package com.projecte.mewnagochi.screens.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class HomeScreenViewModel (
    private val savedStateHandle : SavedStateHandle
) : ViewModel() {
    val addedObject = savedStateHandle.getStateFlow("addedObject",0)
    val deletedObject = savedStateHandle.getStateFlow("deletedObject",0)
    val selectedFurnitureId = savedStateHandle.getStateFlow("selectedFurnitureId",0)
    val isEditingFurniture = savedStateHandle.getStateFlow("isEditingFurniture",false)
    val isAnyFurnitureSelected = savedStateHandle.getStateFlow("isAnyFurnitureSelected",false)
    val  furniture = savedStateHandle.getStateFlow("furniture", mutableListOf<Int>())
    fun addObject(id: Int){
        savedStateHandle["addedObject"] = id
    }
    fun deleteObject(id: Int){
        savedStateHandle["deletedObject"] = id
        savedStateHandle["selectedFurnitureId"] = 0
        savedStateHandle["isAnyFurnitureSelected"] = false
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
        val list = furniture.value
        if(!list.contains(id)) {
            list.add(id)
            savedStateHandle["furniture"] = list
        }
    }
    fun removeItem( id: Int){
        val list = furniture.value
        if(list.contains(id)) {
            list.remove(id)
            savedStateHandle["furniture"] = list
        }
    }



}