package com.madageekscar.panaira.results;

public class GoogleResult {

    private String title;
    private String url;
    private String thubmnail;


    public GoogleResult(String title, String url) {
        setTitle(title);
        setUrl(url);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThubmnail() {
        return thubmnail;
    }

    public void setThubmnail(String thubmnail) {
        this.thubmnail = thubmnail;
    }
}
