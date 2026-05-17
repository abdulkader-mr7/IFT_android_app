package com.tamilquran.ift.view.activity;

import com.tamilquran.ift.model.repository.BookCatalogRepository;

public class BooksActivity extends BooksCatalogActivity {

    @Override
    protected BookCatalogRepository.CatalogType getCatalogType() {
        return BookCatalogRepository.CatalogType.IFT_BOOKS;
    }

    @Override
    protected String getScreenTitle() {
        return "புத்தகங்கள்";
    }

    @Override
    protected boolean isSamarasam() {
        return false;
    }
}
