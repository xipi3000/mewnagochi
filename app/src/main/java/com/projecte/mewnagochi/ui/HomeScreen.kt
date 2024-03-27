package com.projecte.mewnagochi.ui

import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.Dp
import androidx.core.graphics.scale
import com.projecte.mewnagochi.R
import com.projecte.mewnagochi.ui.animation.Animation
import com.projecte.mewnagochi.ui.animation.AnimationManager
import java.lang.Thread.sleep
import java.util.Timer
import java.util.TimerTask

@Composable
fun HomeScreen(){

    val image = ImageBitmap.imageResource(id = R.drawable.advnt_full)
    //val run0 : ImageBitmap = ImageBitmap.imageResource(id = R.drawable.advnt_full1).asAndroidBitmap().scale(2,2).asImageBitmap()

    val run0 : ImageBitmap = ImageBitmap.imageResource(id = R.drawable.run0)
    val run1= ImageBitmap.imageResource(id = R.drawable.run1)
    val run2 = ImageBitmap.imageResource(id = R.drawable.run2)
    val run3 = ImageBitmap.imageResource(id = R.drawable.run3)
    val run4 = ImageBitmap.imageResource(id = R.drawable.run4)
    val anim = Animation(arrayOf(run0,run1,run2,run3,run4), animTime = 1)
    val aniManager = AnimationManager(arrayOf( anim))

    aniManager.playAnim(0)
    aniManager.Draw()

    val handler = Handler(Looper.getMainLooper())

    handler.post(object : Runnable {
        override fun run() {
            // Call your function here
            aniManager.update()

            // Schedule the next execution
            handler.postDelayed(this, 150) // Execute every 1000 milliseconds (1 second)
        }
    })

}