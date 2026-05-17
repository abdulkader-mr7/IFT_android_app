package com.tamilquran.ift;

import com.tamilquran.ift.utils.TamilNormalizer;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TamilNormalizerTest {

    @Test
    public void normalize_convertsLegacyVowelForms() {
        assertEquals("கோ", TamilNormalizer.normalize("கோ"));
        assertEquals("தொ", TamilNormalizer.normalize("தொ"));
    }

    @Test
    public void normalize_trimsInput() {
        assertEquals("தேடல்", TamilNormalizer.normalize("  தேடல்  "));
    }
}
