package com.projecte.mewnagochi.stats

data class StatsUiState(
    var steps: Long = 0,
    var weight: Double = 0.0,
    var heartRate: Long = 0,
    var lastExerciseSession: String = "No data",
)
