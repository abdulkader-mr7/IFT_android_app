package com.tamilquran.ift;

import com.tamilquran.ift.controller.SearchController;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SearchControllerTest {

    @Test
    public void getTotalPages_singlePage() {
        SearchController controller = new SearchController(null);
        assertEquals(1, controller.getTotalPages(5));
        assertEquals(1, controller.getTotalPages(10));
    }

    @Test
    public void getTotalPages_multiplePages() {
        SearchController controller = new SearchController(null);
        assertEquals(2, controller.getTotalPages(11));
        assertEquals(3, controller.getTotalPages(25));
    }
}
