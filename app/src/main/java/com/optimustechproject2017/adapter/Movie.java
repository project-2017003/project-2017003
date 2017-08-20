package com.optimustechproject2017.adapter;

/**
 * Created by HemanthKandula on 7/18/2017.
 */

public class Movie {
    private String title, URL;

    public Movie() {
    }

    public Movie(String title, String url) {
        this.title = title;

        this.URL = url;

    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }





    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }


}