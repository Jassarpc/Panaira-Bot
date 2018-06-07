package com.madageekscar.panaira.searchengine;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GoogleSearch {


    public static final String GOOGLE_SEARCH_URL = "https://www.google.com/search";


    public static Map<String, String> search (String searchTerm, int num) {

        String searchURL = GOOGLE_SEARCH_URL + "?q="+searchTerm+"&num="+num;
        // Contiendra les résultats de recherche
        Map<String, String> searchResults = new HashMap<>();

        try {
            Document doc = Jsoup.connect(searchURL).userAgent("Mozilla/5.0").get();

            Elements results = doc.select("h3.r > a");
            for (Element result : results) {
                String linkHref = result.attr("href");
                String linkText = result.text();
               // System.out.println("Text::" + linkText + ", URL::" + linkHref.substring(7, linkHref.indexOf("&")));

                searchResults.put(linkText, linkHref.substring(7, linkHref.indexOf("&")));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return searchResults;

    }




}
