package com.example.listlab

import android.app.Application
import android.content.Context
import repository.MainRepository

class AppList : Application() {
    override fun onCreate() {
        super.onCreate()
        MainRepository.getInstance().loadData()
    }
    init {
        instance = this
    }
    companion object{
        private var instance: AppList? = null

        val context get()= applicationContext()

        private fun applicationContext(): Context{
            return instance!!.applicationContext
        }
    }
}