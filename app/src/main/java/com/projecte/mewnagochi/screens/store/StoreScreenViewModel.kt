package com.projecte.mewnagochi.screens.store

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projecte.mewnagochi.R
import com.projecte.mewnagochi.services.storage.Item
import com.projecte.mewnagochi.services.storage.StorageServiceImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class StoreItem(
    val id: Int,
    val name: String,
    val isPurchasedState: MutableState<Boolean>,
    val isNew: Boolean,
    val cost : Long,
) {
    var isPurchased: Boolean
        get() = isPurchasedState.value
        set(value) {
            isPurchasedState.value = value
        }

    fun fromStoreItemToItem(): Item {
        return Item(name=name, res = id)
    }
}

class StoreScreenViewModel : ViewModel() {

    private val storageService = StorageServiceImpl()
    val userItems = storageService.items
    //TODO: implementar-ho a firebase, agafar això del nubol, fent query amb el user que estem ara mateix per a poder agafar tmb els que ja té el user i indicarlos com a pucharsed
    private val _items = MutableStateFlow<MutableList<StoreItem>>(
        mutableListOf(
            StoreItem(R.drawable.window, "FINESTRA", mutableStateOf(false), true,5),
            StoreItem(R.drawable.chest, "CHEST", mutableStateOf(false), false,10),
            StoreItem(R.drawable.door, "PORTA", mutableStateOf(false), false,20),
            StoreItem(R.drawable.torch, "TORXA", mutableStateOf(false), false,2),
            StoreItem(R.drawable.finestra_infern, "FINESTRA\nINFERN", mutableStateOf(false), false,10),
        )
    )
    val items: StateFlow<MutableList<StoreItem>> = _items

    init {
        viewModelScope.launch {
            userItems.collect{
                it.forEach {
                    item->
                    Log.i("items",item.name)
                    _items.value.forEach{
                        if(it.name == item.name){
                            it.isPurchased=true
                        }
                    }
                }
            }
        }
    }


    fun addItem(item: Item,cost: Long,onResult : (Throwable?) -> Unit) {
        viewModelScope.launch {
            storageService.spendMoney(cost, onResult = onResult, onSuccess = {
                viewModelScope.launch {
                    storageService.saveItem(item,
                        onSuccess = {
                            _items.value.forEach {
                                if (it.name.equals(item.name)) {
                                    it.isPurchased = true
                                }
                            }
                        }, onResult = {})
                }
            })


        }

    }
    fun addMoney(){
        viewModelScope.launch {
            storageService.saveMoney(10, onSuccess = {}, onResult = {})
        }
    }


}