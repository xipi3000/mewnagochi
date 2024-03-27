package com.projecte.mewnagochi.ui.animation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Canvas

class AnimationManager(private val animations: Array<Animation>) {
    private var animationIndex = 0
    fun playAnim(index: Int) {
        for (i in animations.indices) {
            if (i == index) {
                if (!animations[index].isPlaying) animations[i].play()
            } else animations[i].stop()
        }
        animationIndex = index
    }

    @Composable
    fun Draw() {
        if (animations[animationIndex].isPlaying)
            animations[animationIndex].Draw()
    }

    fun update() {
        if (animations[animationIndex].isPlaying) animations[animationIndex].update()
    }
}

