package com.tamilquran.ift.utils;

public final class TamilNormalizer {

    private TamilNormalizer() {
    }

    public static String normalize(String input) {
        if (input == null) {
            return "";
        }
        String result = input.trim();
        String[][] replacements = {
                {"கோ", "கோ"}, {"ஙோ", "ஙோ"}, {"சோ", "சோ"}, {"டோ", "டோ"}, {"ணோ", "ணோ"},
                {"தோ", "தோ"}, {"நோ", "நோ"}, {"போ", "போ"}, {"மோ", "மோ"}, {"யோ", "யோ"},
                {"ரோ", "ரோ"}, {"லோ", "லோ"}, {"வோ", "வோ"}, {"ழோ", "ழோ"}, {"ளோ", "ளோ"},
                {"றோ", "றோ"}, {"னோ", "னோ"}, {"ஸோ", "ஸோ"}, {"ஷோ", "ஷோ"},
                {"கொ", "கொ"}, {"ஙொ", "ஙொ"}, {"சொ", "சொ"}, {"டொ", "டொ"}, {"ணொ", "ணொ"},
                {"தொ", "தொ"}, {"நொ", "நொ"}, {"பொ", "பொ"}, {"மொ", "மொ"}, {"யொ", "யொ"},
                {"ரொ", "ரொ"}, {"லொ", "லொ"}, {"வொ", "வொ"}, {"ழொ", "ழொ"}, {"ளொ", "ளொ"},
                {"றொ", "றொ"}, {"னொ", "னொ"}, {"ஸொ", "ஸொ"}, {"ஷொ", "ஷொ"}
        };
        for (String[] pair : replacements) {
            result = result.replace(pair[0], pair[1]);
        }
        return result;
    }
}
