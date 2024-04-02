package com.projecte.mewnagochi.ui.animation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import com.projecte.mewnagochi.ui.theme.PersonState
import java.lang.reflect.Modifier

class AnimationManager(private val animations: Array<Animation>) {
    private val animationIndex = mutableIntStateOf(0)
    fun playAnim(index: Int) {
        for (i in animations.indices) {
            if (i == index) {

                if (!animations[index].isPlaying) {
                    Log.i("anim",index.toString())
                    animations[i].play()


                }
            } else animations[i].stop()
        }
        animationIndex.value = index

    }


    @Composable
    fun Draw(modifier: androidx.compose.ui.Modifier) {
        if (animations[animationIndex.value].isPlaying)
            animations[animationIndex.value].Draw(modifier = modifier)
    }

    fun update() {
        if (animations[animationIndex.value].isPlaying) animations[animationIndex.value].update()
    }
}

