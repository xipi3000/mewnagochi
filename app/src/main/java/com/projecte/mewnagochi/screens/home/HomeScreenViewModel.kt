package com.projecte.mewnagochi.screens.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projecte.mewnagochi.services.auth.AccountServiceImpl
import com.projecte.mewnagochi.services.storage.Item
import com.projecte.mewnagochi.services.storage.StorageService
import com.projecte.mewnagochi.services.storage.StorageServiceImpl
import kotlinx.coroutines.launch

class HomeScreenViewModel (
    private val savedStateHandle : SavedStateHandle
) : ViewModel() {
    private val storageService = StorageServiceImpl()
    private val accountService = AccountServiceImpl()

    val addedObject = savedStateHandle.getStateFlow("addedObject",0)
    val deletedObject = savedStateHandle.getStateFlow("deletedObject",0)
    val selectedFurnitureId = savedStateHandle.getStateFlow("selectedFurnitureId",0)
    val isEditingFurniture = savedStateHandle.getStateFlow("isEditingFurniture",false)
    val isAnyFurnitureSelected = savedStateHandle.getStateFlow("isAnyFurnitureSelected",false)
    val  furniture = savedStateHandle.getStateFlow("furniture", mutableListOf<Int>())


    val items = storageService.items


    fun addObject(id: Int=0,item: Item = Item()){
        savedStateHandle["addedObject"] = item.res
        //val updatedItem = item.copy(visible = true)
        //storageService.updateItem(updatedItem){}
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

    fun obj(item: Item) {
        val updatedItem = item.copy(visible = true)
        storageService.updateItem(updatedItem){}
    }


}