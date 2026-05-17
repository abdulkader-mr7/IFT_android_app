package com.tamilquran.ift;

import android.app.Application;

public class IftApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppContainer.getInstance(this);
    }
}
