package com.tamilquran.ift.utils;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

public final class ArabicTextHelper {

    private ArabicTextHelper() {
    }

    public static String cleanLegacyGlyphs(String text) {
        if (text == null) {
            return "";
        }
        return text
                .replace("ؕ", "")
                .replace("ۚ", "")
                .replace("\uE01B", "")
                .replace("\uE01A", "")
                .replace("\uE01C", "")
                .replace("ۢ", "")
                .replace("ۖ", "")
                .replace("\uE022\u200F", "")
                .replace("\uE021", "")
                .replace("\uE01E", "")
                .replace("\uE01F", "")
                .replace("\uE003", "ِ");
    }

    public static SpannableStringBuilder buildStyledArabic(String arabicText) {
        String content = cleanLegacyGlyphs(arabicText);
        SpannableStringBuilder builder = new SpannableStringBuilder(content);
        applyMarker(builder, content, "۩", 0xFFFF0000);
        applyMarker(builder, content, "\uE022", 0xFFFB7600);
        applyMarker(builder, content, "ۘ", 0xFFFB7600);
        return builder;
    }

    private static void applyMarker(SpannableStringBuilder builder, String content, String marker, int color) {
        int index = content.indexOf(marker);
        if (index >= 0) {
            builder.setSpan(new ForegroundColorSpan(color), index, index + marker.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setSpan(new StyleSpan(Typeface.BOLD), index, index + marker.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }
}
