package com.example.photomine.application

import android.app.Application
import android.content.Context
import com.example.photomine.di.listModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PhotoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setupKoin(this)
    }

    private fun setupKoin(context: Context) {
        startKoin {
            androidContext(context)
            modules(listModule)
        }
    }
}