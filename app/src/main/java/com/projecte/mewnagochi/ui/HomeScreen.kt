package com.projecte.mewnagochi.ui

import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import com.projecte.mewnagochi.R
import com.projecte.mewnagochi.ui.animation.Animation
import com.projecte.mewnagochi.ui.animation.AnimationManager
import java.util.Timer
import java.util.TimerTask

@Composable
fun HomeScreen(){

    val idle = ImageBitmap.imageResource(id = R.drawable.advnt_full)
    val run0 : ImageBitmap = ImageBitmap.imageResource(id = R.drawable.run0)
    val run1= ImageBitmap.imageResource(id = R.drawable.run1)
    val run2 = ImageBitmap.imageResource(id = R.drawable.run2)
    val run3 = ImageBitmap.imageResource(id = R.drawable.run3)
    val run4 = ImageBitmap.imageResource(id = R.drawable.run4)
    val idleAnim = Animation(arrayOf(idle), animTime = 1)
    val running = Animation(arrayOf(run0,run1,run2,run3,run4), animTime = 1)
    val aniManager = AnimationManager(arrayOf( idleAnim,running))

    aniManager.playAnim(1)
    //aniManager.Draw()

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