package com.projecte.mewnagochi.services.storage


import kotlinx.coroutines.flow.Flow


interface StorageService {
    suspend fun saveMoney(savings: Long, onResult: (Throwable?) -> Unit, onSuccess: () -> Unit)
    suspend fun spendMoney(cost: Long, onResult: (Throwable?) -> Unit, onSuccess: () -> Unit)
    val userPreferences: Flow<UserPreferences?>
    val money: Flow<Long>

    suspend fun saveItem(item: Item, onResult: (Throwable?) -> Unit, onSuccess: () -> Unit)
    fun updateItem(item: Item, onResult: (Throwable?) -> Unit)
    fun deleteItem(itemId: String, onResult: (Throwable?) -> Unit)
    val items: Flow<List<Item>>

    suspend fun getItem(itemId: String): Item?
    fun createPreferences(onResult: (Throwable?) -> Unit, userId: String, onSuccess: () -> Unit)
    fun updatePreferences(preferences: UserPreferences, onResult: (Throwable?) -> Unit)
    suspend fun getUserPreferences(): UserPreferences?
}

