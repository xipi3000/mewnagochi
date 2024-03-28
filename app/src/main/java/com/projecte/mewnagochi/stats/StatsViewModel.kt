package com.projecte.mewnagochi.stats

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.response.ReadRecordsResponse
import androidx.lifecycle.ViewModel

class StatsViewModel : ViewModel(

){
    lateinit var healthPermissionLauncher: ManagedActivityResultLauncher<Set<String>, Set<String>>
    lateinit var response: ReadRecordsResponse<StepsRecord>

    fun isResponseInitialized(): Boolean {
        return this::response.isInitialized
    }

}