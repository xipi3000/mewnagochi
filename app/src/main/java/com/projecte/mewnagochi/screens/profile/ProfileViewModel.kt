package com.projecte.mewnagochi.screens.profile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.StorageReference
import com.projecte.mewnagochi.screens.login.User
import com.projecte.mewnagochi.services.auth.AccountServiceImpl
import com.projecte.mewnagochi.services.storage.StorageServiceImpl
import com.projecte.mewnagochi.services.storage.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class ProfileUiState(
    val openAlertDialog: Boolean = false,
    val runningGoal: Float = 0F,
    val selectProfilePhoto: Boolean = false,
    val selectedProfilePhoto: String = "",
)

class ProfileViewModel : ViewModel() {
    var uiState = mutableStateOf(ProfileUiState())
        private set
    private val _photoList = MutableStateFlow<List<StorageReference>>(emptyList())
    val photoList: StateFlow<List<StorageReference>> = _photoList

    val accountService = AccountServiceImpl()
    val currentUser : Flow<User> = AccountServiceImpl().currentUser
    private val storageService = StorageServiceImpl()
    val money: Flow<Long> = storageService.money

    val userPreferences: Flow<UserPreferences?> = storageService.userPreferences
    fun logOut(onSuccess: () -> Unit,onResult: (Throwable?) -> Unit) {
        accountService.signOut(onSuccess, onResult)
    }
    fun setSkinWhitch(){
        viewModelScope.launch {
            var userPreferences = storageService.getUserPreferences()
            if(userPreferences==null) {
                userPreferences = UserPreferences()
            }
            userPreferences = userPreferences.copy(selectedSkin = "witch")
            storageService.updatePreferences(userPreferences){


            }
        }

    }
    fun getProfilePictures(){

        viewModelScope.launch {
            storageService.listImages { photos ->
                _photoList.value = photos
            }
        }
    }

    val profilePicture : Flow<ImageBitmap> get()= flow{



        val refe = storageService.getImage(storageService.getUserPreferences()!!.selectedPfp).getBytes(ONE_MEGABYTE).await()

        emit(BitmapFactory.decodeByteArray(refe, 0, refe.size).asImageBitmap())

    }

    fun getProfilePicture(name:String){
        storageService.getImage(name).getBytes(ONE_MEGABYTE).addOnSuccessListener {

               BitmapFactory.decodeByteArray(it, 0, it.size).asImageBitmap()
      }

    }
    fun setSkinAdventurer() {
        viewModelScope.launch {
            var userPreferences = storageService.getUserPreferences()
            if(userPreferences==null) {
                userPreferences = UserPreferences()
            }
            userPreferences =  userPreferences.copy(selectedSkin = "adventurer")
            storageService.updatePreferences(userPreferences){


            }
        }
    }

    fun onOpenAlertDialogChange(value: Boolean) {
        uiState.value = uiState.value.copy(openAlertDialog =value)
    }
    fun onSelectProfilePhotoChange(value: Boolean) {
        uiState.value = uiState.value.copy(selectProfilePhoto =value)
    }
    fun onRunningGoalChanged(value : Float) {
        uiState.value = uiState.value.copy(runningGoal =value)

    }

    fun onRunningGoalSet(preferences: UserPreferences) {
        storageService.updatePreferences(preferences.copy(runningGoal = uiState.value.runningGoal)) {

        }
    }
    fun selectProfilePicture(name: String) {
        uiState.value = uiState.value.copy(selectedProfilePhoto =name)
    }
    fun setProfilePicture(name: String) {
        onSelectProfilePhotoChange(false)
        viewModelScope.launch {
            var userPreferences = storageService.getUserPreferences()
            if(userPreferences==null) {
                userPreferences = UserPreferences()
            }
            userPreferences =  userPreferences.copy(selectedPfp = name)
            storageService.updatePreferences(userPreferences){


            }
        }
    }

}
