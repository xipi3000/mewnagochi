package com.projecte.mewnagochi.ui.furniture

import android.content.Context
import androidx.datastore.dataStore
import com.projecte.mewnagochi.screens.home.movableObject.MovableObjectSerializer

val Context.torchDataStore by dataStore("torch.json", MovableObjectSerializer)
val Context.doorDataStore by dataStore("door.json", MovableObjectSerializer)
val Context.chestDataStore by dataStore("chest.json", MovableObjectSerializer)
val Context.windowDataStore by dataStore("window.json", MovableObjectSerializer)

val Context.dataStore by dataStore("move.json", MovableObjectSerializer)

