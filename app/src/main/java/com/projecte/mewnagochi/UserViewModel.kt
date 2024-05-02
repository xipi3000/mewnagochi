package com.projecte.mewnagochi

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class UserItem(val id : Int, val name: String)

class UserViewModel : ViewModel() {
    private val _myItems = MutableStateFlow<MutableMap<Int,UserItem>>(mutableMapOf(
        R.drawable.window to UserItem(R.drawable.window,"FINESTRA")
    ))
    val myItems: StateFlow<MutableMap<Int,UserItem>> = _myItems

    fun addItem(item: UserItem) {
        _myItems.value[item.id]=item
    }

    fun hasItem(id: Int): Boolean {
        return _myItems.value[id] != null
    }
}


