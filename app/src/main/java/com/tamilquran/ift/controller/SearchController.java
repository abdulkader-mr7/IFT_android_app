package com.tamilquran.ift.controller;

import com.tamilquran.ift.model.entity.VerseRow;
import com.tamilquran.ift.model.repository.QuranRepository;
import com.tamilquran.ift.utils.TamilNormalizer;

import java.util.List;

public class SearchController {

    public static final int PAGE_SIZE = 10;

    private final QuranRepository repository;

    public SearchController(QuranRepository repository) {
        this.repository = repository;
    }

    public String normalizeQuery(String raw) {
        return TamilNormalizer.normalize(raw);
    }

    public SearchValidation validateQuery(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return SearchValidation.invalid("தேடவேண்டிய சொல் ?");
        }
        String normalized = normalizeQuery(raw);
        int count = repository.countSearchResults(normalized);
        if (count == 0) {
            return SearchValidation.invalid("தேடிய சொல் கிடைக்கவில்லை.");
        }
        return SearchValidation.valid(normalized, count);
    }

    public int getTotalPages(int count) {
        if (count <= PAGE_SIZE) {
            return 1;
        }
        int pages = count / PAGE_SIZE;
        if (count % PAGE_SIZE > 0) {
            pages++;
        }
        return pages;
    }

    public List<VerseRow> loadPage(String query, int page) {
        int offset = (page - 1) * PAGE_SIZE;
        return repository.searchVerses(query, offset, PAGE_SIZE);
    }

    public static final class SearchValidation {
        public final boolean valid;
        public final String query;
        public final int count;
        public final String error;

        private SearchValidation(boolean valid, String query, int count, String error) {
            this.valid = valid;
            this.query = query;
            this.count = count;
            this.error = error;
        }

        public static SearchValidation valid(String query, int count) {
            return new SearchValidation(true, query, count, null);
        }

        public static SearchValidation invalid(String error) {
            return new SearchValidation(false, null, 0, error);
        }
    }
}
