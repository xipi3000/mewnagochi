package com.projecte.mewnagochi.services.storage

data class UserPreferences(
    val selectedSkin : String = "adventurer",
    val selectedPfp : String = "",
    val runningGoal : Float = 0F,
    val selectedInternetPreference: Int = 0,
)
