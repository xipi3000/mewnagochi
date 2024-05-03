package com.projecte.mewnagochi.services.storage

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.dataObjects
import com.google.firebase.firestore.firestore
import com.projecte.mewnagochi.services.auth.AccountServiceImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.tasks.await


class StorageServiceImpl : StorageService {
    companion object {
        private const val USER_ID_FIELD = "userId"
        private const val COMPLETED_FIELD = "completed"
        private const val PRIORITY_FIELD = "priority"
        private const val FLAG_FIELD = "flag"
        private const val CREATED_AT_FIELD = "createdAt"
        private const val ITEM_COLLECTION = "items"
        private const val SAVE_TASK_TRACE = "saveItem"
        private const val UPDATE_TASK_TRACE = "updateItem"
    }

    private val firestore: FirebaseFirestore = Firebase.firestore
    private val auth = AccountServiceImpl()

    private val collection get() = firestore.collection(ITEM_COLLECTION)
        .whereEqualTo(USER_ID_FIELD, auth.getUserId())


    @OptIn(ExperimentalCoroutinesApi::class)
    override val items: Flow<List<Item>>
        get() =
            auth.currentUser.flatMapLatest { user ->

                firestore
                    .collection(ITEM_COLLECTION)
                    .whereEqualTo(USER_ID_FIELD, user.id)
                    .dataObjects()
            }

    override fun getItem(taskId: String, onError: (Throwable) -> Unit, onSuccess: (Item) -> Unit) {
        TODO("Not yet implemented")
    }

    override suspend fun saveItem(task: Item, onResult: (Throwable?) -> Unit, onSuccess: () -> Unit) {
            val updateItem = task.copy(userId = auth.getUserId())
            firestore.collection(ITEM_COLLECTION).document(updateItem.name).set(updateItem).addOnCompleteListener{
                it ->
                if(it.isComplete){
                    onSuccess()
                }
                else{
                    onResult(it.exception)
                }
            }

    }



    override fun updateItem(task: Item, onResult: (Throwable?) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun deleteItem(taskId: String, onResult: (Throwable?) -> Unit) {
        TODO("Not yet implemented")
    }
}