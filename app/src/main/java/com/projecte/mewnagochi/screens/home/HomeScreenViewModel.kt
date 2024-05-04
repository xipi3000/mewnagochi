package com.projecte.mewnagochi.screens.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projecte.mewnagochi.services.auth.AccountServiceImpl
import com.projecte.mewnagochi.services.storage.Item
import com.projecte.mewnagochi.services.storage.StorageServiceImpl
import kotlinx.coroutines.launch

data class HomeScreenUi(
    val selectedFurnitureId: String = "",
    val isEditingFurniture:Boolean = false,
    val isAnyFurnitureSelected:Boolean = false,
)
class HomeScreenViewModel (
    private val savedStateHandle : SavedStateHandle
) : ViewModel() {
    private val storageService = StorageServiceImpl()
    var uiState =  mutableStateOf(HomeScreenUi())
        private set

    val items = storageService.items



    fun deleteObject(id: String){
        viewModelScope.launch {
            val item = storageService.getItem(id)
            if (item != null) {
                updateItem(item.copy(visible = false))
                uiState.value = uiState.value.copy(isAnyFurnitureSelected=false, selectedFurnitureId = "")
            }
        }
    }
    fun startEditing(){
        uiState.value = uiState.value.copy(isEditingFurniture = true)
    }
    fun stopEditing(){

        uiState.value = uiState.value.copy(isEditingFurniture = false)
    }
    fun selectFurniture(id : String) {
        uiState.value = uiState.value.copy(isAnyFurnitureSelected=true, selectedFurnitureId = id)

    }
    fun deselectFurniture(){
        uiState.value = uiState.value.copy(isAnyFurnitureSelected=false)
    }
    fun addItem(item: Item){
        storageService.updateItem(item.copy(visible=true)){}

    }

    fun updateItem(item: Item) {
        storageService.updateItem(item){}
    }


}