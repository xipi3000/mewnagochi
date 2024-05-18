package com.projecte.mewnagochi.screens.profile

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.StorageReference
import com.projecte.mewnagochi.screens.login.User
import com.projecte.mewnagochi.services.auth.AccountServiceImpl
import com.projecte.mewnagochi.services.notification.MyFirebaseMessagingService
import com.projecte.mewnagochi.services.storage.StorageServiceImpl
import com.projecte.mewnagochi.services.storage.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

import kotlinx.coroutines.tasks.await

data class ProfileUiState(
    val openAlertDialog: Boolean = false,
    val stepsGoal: Int = 0,
    val notificationHour : Int = 20,
    val currentSteps : Int = 0,
    val selectProfilePhoto: Boolean = false,
    val selectedProfilePhoto: String = "",
)

class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    lateinit var mFMS: MyFirebaseMessagingService

    private val _photoList = MutableStateFlow<List<StorageReference>>(emptyList())
    val photoList: StateFlow<List<StorageReference>> = _photoList

    val accountService = AccountServiceImpl()
    val currentUser: Flow<User> = AccountServiceImpl().currentUser
    private val storageService = StorageServiceImpl()
    val money: Flow<Long> = storageService.money

    val userPreferences: Flow<UserPreferences?> = storageService.userPreferences

    init {
        viewModelScope.launch {
            val preferences = storageService.getUserPreferences()
            if(preferences!=null) {
                _uiState.value = uiState.value.copy(stepsGoal = preferences.stepsGoal, notificationHour = preferences.notificationHour)
            }
        }
    }

    fun logOut(onSuccess: () -> Unit, onResult: (Throwable?) -> Unit) {
        accountService.signOut(onSuccess, onResult)
    }

    fun setSkinWhitch() {
        viewModelScope.launch {
            var userPreferences = storageService.getUserPreferences()
            if (userPreferences == null) {
                userPreferences = UserPreferences()
            }
            userPreferences = userPreferences.copy(selectedSkin = "witch")
            storageService.updatePreferences(userPreferences){}
        }
    }

    fun getProfilePictures() {
        viewModelScope.launch {
            storageService.listImages { photos ->
                _photoList.value = photos
            }
        }
    }

    val profilePicture: Flow<ImageBitmap>
        get() = flow {
            try {
                val refe =
                    storageService.getImage(storageService.getUserPreferences()!!.selectedPfp)!!
                        .getBytes(
                            ONE_MEGABYTE
                        ).await()
                emit(BitmapFactory.decodeByteArray(refe, 0, refe.size).asImageBitmap())
            } catch (e: Exception) {
                try {
                    val refe = storageService.getImage("/default_pfp.png")!!.getBytes(
                        ONE_MEGABYTE
                    ).await()
                    emit(BitmapFactory.decodeByteArray(refe, 0, refe.size).asImageBitmap())
                } catch (e: Exception) {
                    emit(ImageBitmap(1,1))
                }
            }
        }

    fun getProfilePicture(name: String) {
        try {
            storageService.getImage(name)!!.getBytes(ONE_MEGABYTE).addOnSuccessListener {
                BitmapFactory.decodeByteArray(it, 0, it.size).asImageBitmap()
            }
        } catch (e: Exception) {
            BitmapFactory.decodeByteArray(null, 0, 0).asImageBitmap()
        }

    }

    fun setSkinAdventurer() {
        viewModelScope.launch {
            var userPreferences = storageService.getUserPreferences()
            if (userPreferences == null) {
                userPreferences = UserPreferences()
            }
            userPreferences = userPreferences.copy(selectedSkin = "adventurer")
            storageService.updatePreferences(userPreferences){}
        }
    }

    fun onOpenAlertDialogChange(value: Boolean) {
        _uiState.value = uiState.value.copy(openAlertDialog = value)
    }

    fun onSelectProfilePhotoChange(value: Boolean) {
        _uiState.value = uiState.value.copy(selectProfilePhoto = value)
    }

    fun onStepsGoalChanged(value : Float) {
        _uiState.value = uiState.value.copy(stepsGoal = value.toInt())
    }
    fun onHourNotificationChanged(value : Float) {
        _uiState.value = uiState.value.copy(notificationHour = value.toInt())
    }

    fun onStepsGoalSet(preferences: UserPreferences) {
        storageService.updatePreferences(preferences.copy(stepsGoal = uiState.value.stepsGoal)) {
        }
    }

    fun onHourNotificationSet(preferences: UserPreferences) {
        storageService.updatePreferences(preferences.copy(notificationHour = uiState.value.notificationHour)) {
        }
        mFMS.modifyHourValue(uiState.value.notificationHour)
    }

    fun selectProfilePicture(name: String) {
        _uiState.value = uiState.value.copy(selectedProfilePhoto = name)
    }

    fun setProfilePicture(name: String) {
        onSelectProfilePhotoChange(false)
        viewModelScope.launch {
            var userPreferences = storageService.getUserPreferences()
            if (userPreferences == null) {
                userPreferences = UserPreferences()
            }
            userPreferences = userPreferences.copy(selectedPfp = name)
            storageService.updatePreferences(userPreferences){}
        }
    }

    fun selectInternetPreference(context: Context, index: Int) {
        viewModelScope.launch {
            context.InternetPreferenceStateDataStore.updateData {
                it.copy(internetPreferenceSelected = index)
            }
        }
    }
}