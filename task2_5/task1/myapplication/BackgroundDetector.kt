package com.example.myapplication

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import timber.log.Timber

class BackgroundDetector : DefaultLifecycleObserver {

    override fun onStart(owner: LifecycleOwner) {
        Timber.d("Застосунок перейшов у FOREGROUND (видимий на екрані)")
    }

    override fun onStop(owner: LifecycleOwner) {
        Timber.d("Застосунок перейшов у BACKGROUND (згорнутий у фон)")
    }
}