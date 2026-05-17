package com.tamilquran.ift.view.dialog;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.tamilquran.ift.R;

public final class DialogUtils {

    private DialogUtils() {
    }

    public static void showExitDialog(Context context, Runnable onConfirm) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.exit_dialog_title)
                .setMessage(R.string.exit_dialog_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    dialog.dismiss();
                    onConfirm.run();
                })
                .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss())
                .show();
    }

    public static void showConfirmDialog(
            Context context,
            String title,
            String message,
            Runnable onConfirm
    ) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    dialog.dismiss();
                    onConfirm.run();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .show();
    }
}
