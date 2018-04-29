package com.example.jasim.plateup;

import com.google.firebase.database.Exclude;

import java.util.HashMap;

/**
 * Created by jasim on 03.07.2017.
 */

public class SmallContent {
    private String id;
    private String dishname;
    private int antall;
    private int price;
    String type = "0";
    private int quantity;
    private String comment;
    private HashMap<String, Integer> extraInfo = new HashMap<>();

    public SmallContent() {

    }




    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public SmallContent(String id, int antall, String dishname, int price, String type, int quantity, String comment, HashMap<String, Integer> extraInfo) {
        this.dishname = dishname;
        this.antall = antall;
        this.id = id;
        this.price = price;
        this.type = type;
        this.quantity = quantity;
        this.comment = comment;
        this.extraInfo = extraInfo;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }
    @Exclude
    public Integer getQuantity() {
        return quantity;
    }


    public void setId(String id) {
        this.id = id;
    }

    public int getAntall() {
        return antall;
    }

    public void setAntall(int antall) {
        this.antall = antall;
    }

    public String getDishname() {
        return dishname;
    }

    public void setDishname(String dishname) {
        this.dishname = dishname;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public HashMap<String, Integer> getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(HashMap<String, Integer> extraInfo) {
        this.extraInfo = extraInfo;
    }
}
