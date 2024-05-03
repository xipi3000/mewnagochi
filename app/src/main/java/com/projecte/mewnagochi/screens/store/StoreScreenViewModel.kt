package com.projecte.mewnagochi.screens.store

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projecte.mewnagochi.R
import com.projecte.mewnagochi.services.auth.AccountServiceImpl
import com.projecte.mewnagochi.services.storage.Item
import com.projecte.mewnagochi.services.storage.StorageService
import com.projecte.mewnagochi.services.storage.StorageServiceImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

data class StoreItem(
    val id: Int,
    val name: String,
    val isPurchasedState: MutableState<Boolean>,
    val isNew: Boolean
) {
    var isPurchased: Boolean
        get() = isPurchasedState.value
        set(value) {
            isPurchasedState.value = value
        }

    fun toItem(): Item {
        return Item(name, id)
    }
}

class StoreScreenViewModel : ViewModel() {
    private val _currentUser: MutableState<String> = mutableStateOf("")
    val currentUser: MutableState<String> = _currentUser
    private val storageService = StorageServiceImpl()
    val userItems = storageService.items
    //TODO: implementar-ho a firebase, agafar això del nubol, fent query amb el user que estem ara mateix per a poder agafar tmb els que ja té el user i indicarlos com a pucharsed
    private val _items = MutableStateFlow<MutableList<StoreItem>>(
        mutableListOf(
            StoreItem(R.drawable.window, "FINESTRA", mutableStateOf(false), true),
            StoreItem(R.drawable.chest, "CHEST", mutableStateOf(false), false),
        )
    )
    val items: StateFlow<MutableList<StoreItem>> = _items

    init {
        Log.i("User3", AccountServiceImpl().currentEmail)
        _currentUser.value = AccountServiceImpl().currentEmail
        viewModelScope.launch {
            userItems.collect{
                it.forEach {

                    item->
                    Log.i("items",item.name)
                    _items.value.forEach{
                        if(it.id == item.res){
                            //TODO: implementar-ho a firebase, osigui que s'haurà de actualitzar al nubol
                            it.isPurchased=true
                        }
                    }
                }
            }
        }


    }

    fun updateItems(items: MutableList<StoreItem>) {
        _items.value = items
    }

    fun buyItem(id: Int) {
        _items.value.forEach { item ->
            if (item.id == id)
                item.isPurchased = true
        }
    }

    fun addItem(item: Item) {
        viewModelScope.launch {
            storageService.saveItem(item,
                onSuccess = {
                    _items.value.forEach{
                        if(it.id == item.res){
                            //TODO: implementar-ho a firebase, osigui que s'haurà de actualitzar al nubol
                            it.isPurchased=true
                        }
                    }
                }, onResult = {})
        }

    }


}