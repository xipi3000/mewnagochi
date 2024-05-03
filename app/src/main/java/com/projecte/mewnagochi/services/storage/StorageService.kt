package com.projecte.mewnagochi.services.storage

import com.projecte.mewnagochi.ui.furniture.MovableObject
import kotlinx.coroutines.flow.Flow


interface StorageService {

    fun getItem(taskId: String, onError: (Throwable) -> Unit, onSuccess: (Item) -> Unit)
    suspend fun saveItem(task: Item, onResult: (Throwable?) -> Unit, onSuccess: () -> Unit)
    fun updateItem(task: Item, onResult: (Throwable?) -> Unit)
    fun deleteItem(taskId: String, onResult: (Throwable?) -> Unit)
    val items: Flow<List<Item>>
}

