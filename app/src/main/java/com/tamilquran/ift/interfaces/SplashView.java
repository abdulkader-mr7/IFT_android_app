package com.tamilquran.ift.interfaces;

public interface SplashView {
    void showLoading(boolean visible);

    void showStorageError();

    void showError(String message);

    void navigateToMain();

    void post(Runnable runnable);
}
