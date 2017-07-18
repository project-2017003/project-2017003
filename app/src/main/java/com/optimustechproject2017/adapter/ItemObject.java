package com.optimustechproject2017.adapter;

/**
 * Created by HemanthKandula on 7/18/2017.
 */

public class ItemObject {
    private String contents,smalldetails,price;
    private int imaage;



    public ItemObject(String contents) {
        this.contents = contents;
    }
    public ItemObject(String title, String smalldetails, String price) {
        this.contents = title;
        this.smalldetails = smalldetails;
        this.price = price;
    }

    public String getContents() {
        return contents;
    }

    public String getSmalldetails() {
        return smalldetails;
    }

    public String getPrice() {
        return price;
    }
}
