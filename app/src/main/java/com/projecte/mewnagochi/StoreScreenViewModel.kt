package com.projecte.mewnagochi

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class StoreItem(val id: Int, val name: String, val isPurchasedState: MutableState<Boolean>, val isNew: Boolean) {
    var isPurchased: Boolean
        get() = isPurchasedState.value
        set(value) { isPurchasedState.value = value }
}
class StoreScreenViewModel : ViewModel(){

    private val _items = MutableStateFlow<MutableList<StoreItem>>(mutableListOf(
        StoreItem(R.drawable.window,"FINESTRA", mutableStateOf(false),true),
        StoreItem(R.drawable.chest,"CHEST",mutableStateOf(false),false),

    ))
    val items: StateFlow<MutableList<StoreItem>> = _items



    fun updateItems(items: MutableList<StoreItem>) {
        _items.value = items
    }
    fun buyItem(id : Int){
        _items.value.forEach {
            item ->
            if(item.id==id)
                item.isPurchased = true
        }
    }
}