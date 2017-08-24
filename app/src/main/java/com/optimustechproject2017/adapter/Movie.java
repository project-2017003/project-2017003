package com.optimustechproject2017.adapter;

/**
 * Created by HemanthKandula on 7/18/2017.
 */

public class Movie {
    private String title, URL, ID;

    public Movie() {
    }

    public Movie(String title, String url, String ID) {
        this.title = title;

        this.URL = url;
        this.ID = ID;


    }


    public Movie(String title, String ID) {
        this.title = title;


        this.ID = ID;

    }
    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getID() {
        return ID;
    }


    public void setID() {
        this.ID = ID;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }


}