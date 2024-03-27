package com.projecte.mewnagochi.ui.animation

import android.util.Log
import androidx.compose.runtime.Composable
import java.lang.reflect.Modifier

class AnimationManager(private val animations: Array<Animation>) {
    private var animationIndex = 0
    fun playAnim(index: Int) {
        for (i in animations.indices) {
            if (i == index) {

                if (!animations[index].isPlaying) {
                    Log.i("anim",index.toString())
                    animations[i].play()


                }
            } else animations[i].stop()
        }
        animationIndex = index
    }

    @Composable
    fun Draw(modifier: androidx.compose.ui.Modifier) {
        if (animations[animationIndex].isPlaying)
            animations[animationIndex].Draw(modifier = modifier)
    }

    fun update() {
        if (animations[animationIndex].isPlaying) animations[animationIndex].update()
    }
}

