package com.optimustechproject2017.adapter;

import me.himanshusoni.quantityview.QuantityView;

/**
 * Created by HemanthKandula on 7/18/2017.
 */

public class ItemObject {
    private String contents,smalldetails,price;
    private QuantityView quantityView;
    private int imaage;
    private String quant;



//    public ItemObject(String contents) {
//        this.contents = contents;
//    }

    public ItemObject(String quant){
        this.quant =quant;
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

    public String getQuant(){
        return quant;
    }
    public QuantityView getQuantityView(){
     return quantityView;
    }
}
