package com.tamilquran.ift.view.activity;

import com.tamilquran.ift.model.repository.BookCatalogRepository;

public class SamarasamActivity extends BooksCatalogActivity {

    @Override
    protected BookCatalogRepository.CatalogType getCatalogType() {
        return BookCatalogRepository.CatalogType.SAMARASAM;
    }

    @Override
    protected String getScreenTitle() {
        return "சமரசம் இதழ்";
    }

    @Override
    protected boolean isSamarasam() {
        return true;
    }
}
