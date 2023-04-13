package com.evanzheng.bibliobarcode

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import org.acra.ACRA.init

//PPL remove annotattions...
//Implements a error-reporting system so I can receive error messages from crashes on other devices
class BaseApplication : Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
        init(this)
    }
}