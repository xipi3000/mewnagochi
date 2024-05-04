package com.projecte.mewnagochi.services.storage


import kotlinx.coroutines.flow.Flow


interface StorageService {

     suspend fun saveItem(task: Item, onResult: (Throwable?) -> Unit, onSuccess: () -> Unit)
    fun updateItem(task: Item, onResult: (Throwable?) -> Unit)
    fun deleteItem(taskId: String, onResult: (Throwable?) -> Unit)
    val items: Flow<List<Item>>

    suspend fun getItem(taskId: String): Item?
}

