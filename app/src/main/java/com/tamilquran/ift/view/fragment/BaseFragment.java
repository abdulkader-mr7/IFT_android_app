package com.tamilquran.ift.view.fragment;

import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment {

    protected void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    protected void showToast(@StringRes int messageRes) {
        if (getContext() != null) {
            Toast.makeText(getContext(), messageRes, Toast.LENGTH_SHORT).show();
        }
    }
}
