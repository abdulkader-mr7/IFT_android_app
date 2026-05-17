package com.tamilquran.ift.controller;

import android.content.Context;

import com.tamilquran.ift.AppContainer;
import com.tamilquran.ift.model.database.AppDatabase;
import com.tamilquran.ift.model.database.DatabaseBootstrap;
import com.tamilquran.ift.interfaces.SplashView;

public class SplashController {

    private final Context context;
    private final SplashView view;

    public SplashController(Context context, SplashView view) {
        this.context = context.getApplicationContext();
        this.view = view;
    }

    public void startBootstrap() {
        view.showLoading(true);
        AppContainer.getInstance(context).getIoExecutor().execute(() ->
                DatabaseBootstrap.bootstrap(context, new DatabaseBootstrap.BootstrapCallback() {
                    @Override
                    public void onSuccess() {
                        AppDatabase.getInstance(context);
                        view.post(() -> {
                            view.showLoading(false);
                            view.navigateToMain();
                        });
                    }

                    @Override
                    public void onInsufficientStorage() {
                        view.post(() -> {
                            view.showLoading(false);
                            view.showStorageError();
                        });
                    }

                    @Override
                    public void onError(String message) {
                        view.post(() -> {
                            view.showLoading(false);
                            view.showError(message);
                        });
                    }
                })
        );
    }
}
