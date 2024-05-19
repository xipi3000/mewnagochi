package com.projecte.mewnagochi.screens.store

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projecte.mewnagochi.services.storage.Item
import com.projecte.mewnagochi.services.storage.StorageServiceImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class StoreItemNet(
    val id: Int = 0,
    val name: String = "",
    val purchasedState: Boolean =false,
    val new: Boolean= false,
    val cost : Long = 0L,
){
    fun toStoreItem() : StoreItem{
        return StoreItem(this.id,this.name,mutableStateOf(this.purchasedState),this.new,this.cost)
    }
}

data class StoreItem(
    val id: Int = 0,
    val name: String = "",
    val isPurchasedState: MutableState<Boolean> = mutableStateOf(false),
    val isNew: Boolean= false,
    val cost : Long = 0L,
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
    /*    private val _items1 = MutableStateFlow<MutableList<StoreItem>>(
        mutableListOf(
            StoreItem(R.drawable.window, "FINESTRA", mutableStateOf(false), true,5),
            StoreItem(R.drawable.chest, "CHEST", mutableStateOf(false), false,10),
            StoreItem(R.drawable.door, "PORTA", mutableStateOf(false), false,20),
            StoreItem(R.drawable.torch, "TORXA", mutableStateOf(false), false,2),
            StoreItem(R.drawable.finestra_infern, "FINESTRA\nINFERN", mutableStateOf(false), false,10),
        )
    )
    private val _itemsNet = MutableStateFlow(
        mutableListOf(
            StoreItemNet(R.drawable.window, "FINESTRA", false, true,5),
            StoreItemNet(R.drawable.chest, "CHEST", false, false,10),
            StoreItemNet(R.drawable.door, "PORTA", false, false,20),
            StoreItemNet(R.drawable.torch, "TORXA", false, false,2),
            StoreItemNet(R.drawable.finestra_infern, "FINESTRA\nINFERN", false, false,10),
        )
    )*/
    val itemsRemote = storageService.storeItems
    val _items = MutableStateFlow<List<StoreItem>>(emptyList())
    val items: StateFlow<List<StoreItem>> get() = _items
    init {
        viewModelScope.launch {
            combine(itemsRemote, userItems) { remoteItems, userItemList ->
                remoteItems.map { remoteItem ->
                    val isPurchased = userItemList.any { it.name == remoteItem.name }
                    remoteItem.copy(purchasedState = isPurchased).toStoreItem()
                }
            }.collect { newItems ->
                _items.value = newItems
                newItems.forEach { item ->
                    Log.i("store", item.name)
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