package com.example.usedItem.strategy;

import java.util.Optional;

import org.jsoup.nodes.Element;

import com.example.usedItem.domain.ScrapedItem;

public class BunjangScraperStrategy implements SiteScraperStrategy {

    @Override
    public String getSiteCode() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSiteCode'");
    }

    @Override
    public String getProductListSelector() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getProductListSelector'");
    }

    @Override
    public Optional<ScrapedItem> parseItem(Element productElement) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'parseItem'");
    }

}
