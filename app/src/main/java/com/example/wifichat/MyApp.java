package com.example.wifichat;

import android.app.Application;
import android.content.Context;

import java.lang.ref.WeakReference;

public class MyApp extends Application {
    private static WeakReference<Context> globalContext;
    private static P2pManager globalP2pManger;

    @Override
    public void onCreate() {
        super.onCreate();
        globalContext = new WeakReference<>(getApplicationContext());
        globalP2pManger = new P2pManager();
        globalP2pManger.onInit();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        globalP2pManger.onDestroy();
    }

    public static Context getAppContext() {
        assert globalContext != null;
        return globalContext.get();
    }

    public static P2pManager getP2pManger() {
        assert globalP2pManger != null;
        return globalP2pManger;
    }
}
