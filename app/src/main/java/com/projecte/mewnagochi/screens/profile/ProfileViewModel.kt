package com.projecte.mewnagochi.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projecte.mewnagochi.screens.login.User
import com.projecte.mewnagochi.services.auth.AccountServiceImpl
import com.projecte.mewnagochi.services.notification.MyFirebaseMessagingService
import com.projecte.mewnagochi.services.storage.StorageServiceImpl
import com.projecte.mewnagochi.services.storage.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val openAlertDialog: Boolean = false,
    val stepsGoal: Int = 0,
    val notificationHour: Int = 20,
    val currentSteps : Int = 0,
)

class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    lateinit var mFMS: MyFirebaseMessagingService

    val accountService = AccountServiceImpl()
    val currentUser : Flow<User> = AccountServiceImpl().currentUser
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
        _uiState.value = uiState.value.copy(openAlertDialog =value)
    }

    fun onStepsGoalChanged(value : Float) {
        _uiState.value = uiState.value.copy(stepsGoal = value.toInt())
    }

    fun onHourNotificationChanged(value : Float) {
        _uiState.value = uiState.value.copy(notificationHour = value.toInt())
        mFMS.modifyHourValue(value.toInt())

    }

    fun onStepsGoalSet(preferences: UserPreferences) {
        storageService.updatePreferences(preferences.copy(stepsGoal = uiState.value.stepsGoal)) {
        }
    }

    fun onHourNotificationSet(preferences: UserPreferences) {
        storageService.updatePreferences(preferences.copy(notificationHour = uiState.value.notificationHour)) {
        }
    }
}
