package com.projecte.mewnagochi.screens.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projecte.mewnagochi.services.auth.AccountServiceImpl
import com.projecte.mewnagochi.services.storage.StorageService
import com.projecte.mewnagochi.services.storage.StorageServiceImpl
import kotlinx.coroutines.launch

class HomeScreenViewModel (
    private val savedStateHandle : SavedStateHandle
) : ViewModel() {
    val addedObject = savedStateHandle.getStateFlow("addedObject",0)
    val deletedObject = savedStateHandle.getStateFlow("deletedObject",0)
    val selectedFurnitureId = savedStateHandle.getStateFlow("selectedFurnitureId",0)
    val isEditingFurniture = savedStateHandle.getStateFlow("isEditingFurniture",false)
    val isAnyFurnitureSelected = savedStateHandle.getStateFlow("isAnyFurnitureSelected",false)
    val  furniture = savedStateHandle.getStateFlow("furniture", mutableListOf<Int>())
    private val storageService = StorageServiceImpl()
    private val accountService = AccountServiceImpl()
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