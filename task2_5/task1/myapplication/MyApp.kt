package com.example.myapplication

import android.app.Activity
import android.app.Application
import android.content.pm.ApplicationInfo
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import timber.log.Timber

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        if (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) {
            Timber.plant(Timber.DebugTree())
        }

        ProcessLifecycleOwner
            .get()
            .lifecycle
            .addObserver(BackgroundDetector())

        registerActivityLifecycleCallbacks(
            object : ActivityLifecycleCallbacks {

                private fun getState(activity: Activity): String {
                    val owner = activity as? LifecycleOwner
                    return owner?.lifecycle?.currentState?.name ?: "UNKNOWN"
                }

                override fun onActivityCreated(
                    activity: Activity,
                    savedInstanceState: Bundle?
                ) {
                    Timber.d(
                        "onActivityCreated: ${activity.javaClass.simpleName}, state=${getState(activity)}"
                    )
                }

                override fun onActivityStarted(activity: Activity) {
                    Timber.d(
                        "onActivityStarted: ${activity.javaClass.simpleName}, state=${getState(activity)}"
                    )
                }

                override fun onActivityResumed(activity: Activity) {
                    Timber.d(
                        "onActivityResumed: ${activity.javaClass.simpleName}, state=${getState(activity)}"
                    )
                }

                override fun onActivityPaused(activity: Activity) {
                    Timber.d(
                        "onActivityPaused: ${activity.javaClass.simpleName}, state=${getState(activity)}"
                    )
                }

                override fun onActivityStopped(activity: Activity) {
                    Timber.d(
                        "onActivityStopped: ${activity.javaClass.simpleName}, state=${getState(activity)}"
                    )
                }

                override fun onActivitySaveInstanceState(
                    activity: Activity,
                    outState: Bundle
                ) {
                }

                override fun onActivityDestroyed(activity: Activity) {
                    Timber.d(
                        "onActivityDestroyed: ${activity.javaClass.simpleName}, state=${getState(activity)}"
                    )
                }
            }
        )
    }
}