package com.tamilquran.ift;

import com.tamilquran.ift.model.Result;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ResultTest {

    @Test
    public void success_carriesData() {
        Result<String> result = Result.success("ok");
        assertTrue(result.isSuccess());
        assertEquals("ok", result.getData());
    }

    @Test
    public void error_carriesMessage() {
        Result<String> result = Result.error("failed", null);
        assertEquals(Result.Status.ERROR, result.getStatus());
        assertEquals("failed", result.getMessage());
    }
}
