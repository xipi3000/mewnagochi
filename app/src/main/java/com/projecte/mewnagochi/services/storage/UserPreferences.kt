package com.projecte.mewnagochi.services.storage

data class UserPreferences(
    val selectedSkin : String = "adventurer",
    val stepsGoal : Int = 0,
    val notificationHour : Int = 20,
    val currentSteps : Int = 0,
    val stepsAverage : Int = 0
)
