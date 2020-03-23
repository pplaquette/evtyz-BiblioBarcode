package com.evanzheng.bibliobarcode;

import android.app.Application;
import android.content.Context;

import org.acra.ACRA;
import org.acra.annotation.AcraCore;
import org.acra.annotation.AcraMailSender;


//Implements a error-reporting system so I can receive error messages from crashes on other devices
@AcraCore(buildConfigClass = BuildConfig.class)
@AcraMailSender(mailTo = "evan.ty.zheng@gmail.com")
public class BaseApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        ACRA.init(this);
    }
}
