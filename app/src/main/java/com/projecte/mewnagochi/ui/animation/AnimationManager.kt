package com.projecte.mewnagochi.ui.animation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf


class AnimationManager(private val animations: Array<Animation>) {
    private val animationIndex = mutableIntStateOf(0)
    fun playAnim(index: Int) {
        for (i in animations.indices) {
            if (i == index) {

                if (!animations[index].isPlaying) {
                    //Log.i("anim",index.toString())
                    animations[i].play()


                }
            } else animations[i].stop()
        }
        animationIndex.intValue = index

    }


    @Composable
    fun Draw(modifier: androidx.compose.ui.Modifier) {
        if (animations[animationIndex.intValue].isPlaying)
            animations[animationIndex.intValue].Draw(modifier = modifier)
    }

    fun update() {
        if (animations[animationIndex.intValue].isPlaying) animations[animationIndex.intValue].update()
    }
}

