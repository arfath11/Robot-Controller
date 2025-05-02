package com.example.robotoperator

import android.app.Application
import com.example.robotoperator.opengl.Model
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RobotApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: RobotApplication

        // Store the current model globally, so that we don't have to re-decode it upon
        // relaunching the main
        // TODO: handle this a bit better.
        var currentModel: Model? = null
    }
}
