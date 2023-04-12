package com.evanzheng.bibliobarcode;






//PPL remove annotattions...

import android.app.Application;
import android.content.Context;

import org.acra.ACRA;

//Implements a error-reporting system so I can receive error messages from crashes on other devices
public class BaseApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        ACRA.init(this);
    }
}
