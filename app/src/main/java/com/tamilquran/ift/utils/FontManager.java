package com.tamilquran.ift.utils;

import android.content.Context;
import android.graphics.Typeface;

public final class FontManager {

    private static Typeface arabicTypeface;
    private static Typeface tamilTypeface;

    private FontManager() {
    }

    public static Typeface getArabicTypeface(Context context) {
        if (arabicTypeface == null) {
            arabicTypeface = Typeface.createFromAsset(
                    context.getAssets(), "font/Al_Qalam_Quran_Majeed_Updated.ttf");
        }
        return arabicTypeface;
    }

    public static Typeface getTamilTypeface(Context context) {
        if (tamilTypeface == null) {
            tamilTypeface = Typeface.DEFAULT;
        }
        return tamilTypeface;
    }
}
