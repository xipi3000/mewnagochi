package com.projecte.mewnagochi.services.storage

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.snapshots
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.dataObjects
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import com.projecte.mewnagochi.services.auth.AccountServiceImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID


class StorageServiceImpl : StorageService {
    companion object {
        private const val USER_ID_FIELD = "userId"
        private const val ITEM_COLLECTION = "items"
        private const val USER_COLLECTION = "usersPreferences"
        private const val MONEY_COLLECTION = "usersMoney"

    }

    private val firestore: FirebaseFirestore = FirestoreProvider.firestore
    private val auth = AccountServiceImpl()
    private val database: FirebaseDatabase = FirebaseDatabaseProvider.instance
    private val storage = FirebaseStorageProvider.instance


 override fun setFirestoreNetworkEnabled() {
        val firestore = FirebaseFirestore.getInstance()
         firestore.enableNetwork()
             .addOnCompleteListener { task ->
                 if (task.isSuccessful) {
                     Log.i("firestoreNetwork","Going online")
                 } else {
                     // Handle failure
                 }
             }
         database.goOnline()
         FirebaseStorageProvider.enable()



 }
    override fun setFirestoreNetworkDisabled() {
    firestore.disableNetwork()
    .addOnCompleteListener { task ->
        if (task.isSuccessful) {
            Log.i("firestoreNetwork","Going offline")
        } else {
            Log.e("firestoreNetwork","Error going offline")
        }
    }
    database.goOffline()
    FirebaseStorageProvider.disable()

}
     override suspend fun listImages(onPhotosReceived: (List<StorageReference>) -> Unit) {
        if(FirebaseStorageProvider.enabled.value) {
            val photos = storage.listAll().await().items
            onPhotosReceived(photos)
        }
         else{
            onPhotosReceived(emptyList())
        }
     }
    override fun getImage(name : String): StorageReference? {
        if(FirebaseStorageProvider.enabled.value) {
            try {
                return storage.child("/").child(name)
            } catch (e: Exception) {
                return storage.child("/default_pfp.png")
            }
        }
        else{
            return null
        }

    }


    @OptIn(ExperimentalCoroutinesApi::class)
    override val items: Flow<List<Item>>
        get() =
            auth.currentUser.flatMapLatest { user ->
                firestore
                    .collection(ITEM_COLLECTION)
                    .whereEqualTo(USER_ID_FIELD, user.id)
                    .dataObjects()
            }
    @OptIn(ExperimentalCoroutinesApi::class)
    override val userPreferences: Flow<UserPreferences?>
        get() =try{
            auth.currentUser.flatMapLatest { user ->
                try {
                    firestore
                        .collection(USER_COLLECTION)
                        .document(user.id)
                        .dataObjects()
                }catch (e: Exception){
                    flow{}
                }
            }

        }
        catch (e: Exception){
            flow{}
        }
    @OptIn(ExperimentalCoroutinesApi::class)
    override val money: Flow<Long>
        get() =
            try{
                auth.currentUser.flatMapLatest { user ->
                    try {
                        database.getReference(MONEY_COLLECTION)
                            .child(user.id)
                            .snapshots.map {
                                if (it.value == null) 0L
                                else {

                                    try{it.value as Long}
                                    catch (e:Exception){
                                        0L
                                    }
                                }
                            }
                    }
                    catch (e:Exception){
                        flow{}
                    }
                }
            }
            catch (e: Exception){
                flow{}
            }

    override suspend fun getItem(itemId: String): Item? =
        firestore.collection(ITEM_COLLECTION).document(itemId).get().await().toObject()

    override suspend fun getUserPreferences(): UserPreferences? =
        firestore.collection(USER_COLLECTION).document(auth.getUserId()).get().await().toObject()
    override suspend fun getMoney(): Long =
        try{
        database.getReference(MONEY_COLLECTION)
            .child(auth.getUserId()).get().await().value as Long? ?: 0L}
        catch (e : Exception){
            0L
        }

    override suspend fun saveMoney(
        savings: Long,
        onResult: (Throwable?) -> Unit,
        onSuccess: () -> Unit
    ) {
               database.reference.child(MONEY_COLLECTION)
            .child(auth.getUserId())
                .setValue(getMoney()+savings)
    }

    override suspend fun spendMoney(
        cost: Long,
        onResult: (Throwable?) -> Unit,
        onSuccess: () -> Unit
    ) {
        val usersMoney = getMoney()
        Log.i("wallet",(usersMoney-cost).toString())
        if (usersMoney-cost<0) {
            Log.i("wallet","error")
            onResult(Exception("Not enough money"))
        }
        else {
            Log.i("wallet","passed")
            database.reference.child(MONEY_COLLECTION)
                .child(auth.getUserId())
                .setValue(usersMoney - cost)
            onSuccess()
        }

    }



    override suspend fun saveItem(item: Item, onResult: (Throwable?) -> Unit, onSuccess: () -> Unit) {
            val updateItem = item.copy(userId = auth.getUserId(), id = UUID.randomUUID().toString())
            firestore.collection(ITEM_COLLECTION).document(updateItem.id).set(updateItem).addOnCompleteListener{
                if(it.isComplete){
                    onSuccess()
                }
                else{
                    onResult(it.exception)
                }
            }

    }



    override fun updateItem(item: Item, onResult: (Throwable?) -> Unit) {

        firestore.collection(ITEM_COLLECTION).document(item.id).set(item).addOnCompleteListener {
            if(!it.isComplete) onResult(it.exception)
        }
    }
    override fun createPreferences(onResult: (Throwable?) -> Unit, userId: String,onSuccess: () -> Unit) {
        firestore.collection(USER_COLLECTION).document(userId).set(UserPreferences()).addOnCompleteListener{
            if(it.isComplete){
                onSuccess()
            }
            else{
                onResult(it.exception)
            }
        }

    }
    override fun updatePreferences(preferences: UserPreferences, onResult: (Throwable?) -> Unit) {

        firestore.collection(USER_COLLECTION).document(auth.getUserId()).set(preferences).addOnCompleteListener {
            if(!it.isComplete){
                createPreferences(userId = auth.getUserId(), onSuccess = {}, onResult = {})
                firestore.collection(USER_COLLECTION).document(auth.getUserId()).set(preferences)
            }
        }
    }

}
object FirestoreProvider {
    val firestore: FirebaseFirestore by lazy {
        Firebase.firestore
    }
}

object FirebaseDatabaseProvider {
    val instance: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance("https://mewnagochi-default-rtdb.europe-west1.firebasedatabase.app")
    }
}
object FirebaseStorageProvider {
    val enabled = MutableStateFlow(true)
    val enableFlow = enabled.asStateFlow()
    val instance  : StorageReference by lazy {
        Firebase.storage.reference.child("/")
    }
    fun disable(){
        enabled.value = false
    }
    fun enable(){
        enabled.value = true
    }

}