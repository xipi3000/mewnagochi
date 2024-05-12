package com.projecte.mewnagochi.services.storage

data class UserPreferences(
    val selectedSkin : String = "adventurer",
    var stepsGoal : Int = 0,
    var notificationHour : Int = 20,
)
