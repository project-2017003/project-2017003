package com.optimustechproject2017.adapter;


/**
 * Created by HemanthKandula on 7/18/2017.
 */

public class ItemObject {
    private String contents,smalldetails,price;
    private int imaage;
    private String quant, ID, URL;


//    public ItemObject(String contents) {
//        this.contents = contents;
//    }

    public ItemObject(String quant){
        this.quant =quant;
    }

    public ItemObject(String id, String title, String smalldetails, String price, String URL, String l) {
        this.contents = title;
        this.smalldetails = smalldetails;
        this.price = price;
        this.ID = id;
        this.URL = URL;

    }

    public ItemObject(String id, String itemName, String itemdetails, String price, String quant) {


        this.contents = itemName;
        this.smalldetails = itemdetails;
        this.price = price;
        this.ID = id;

        this.quant = quant;
    }

    public String getContents() {
        return contents;
    }

    public String getURL() {
        return URL;
    }


    public String getSmalldetails() {
        return smalldetails;
    }

    public String getPrice() {
        return price;
    }

    public String getID() {
        return ID;
    }



    public String getQuant(){
        return quant;
    }
}
