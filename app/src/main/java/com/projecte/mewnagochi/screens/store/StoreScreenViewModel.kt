package com.projecte.mewnagochi.screens.store

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.projecte.mewnagochi.R
import com.projecte.mewnagochi.services.auth.AccountServiceImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class StoreItem(val id: Int, val name: String, val isPurchasedState: MutableState<Boolean>, val isNew: Boolean) {
    var isPurchased: Boolean
        get() = isPurchasedState.value
        set(value) { isPurchasedState.value = value }
}
class StoreScreenViewModel : ViewModel(){
    private val _currentUser : MutableState<String> = mutableStateOf("")
    val currentUser : MutableState<String> = _currentUser

    private val _items = MutableStateFlow<MutableList<StoreItem>>(mutableListOf(
        StoreItem(R.drawable.window,"FINESTRA", mutableStateOf(false),true),
        StoreItem(R.drawable.chest,"CHEST",mutableStateOf(false),false),

    ))
    val items: StateFlow<MutableList<StoreItem>> = _items

    init {
        Log.i("User3", AccountServiceImpl().currentEmail)
        _currentUser.value = AccountServiceImpl().currentEmail
    }

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