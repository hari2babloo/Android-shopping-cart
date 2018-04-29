package com.example.jasim.plateup;

/**
 * Created by jasim on 27.01.2017.
 */

public class Category {


    protected String name;
    protected String category;

    public Category(String name) {
        this.name = name;
    }
    public Category(){

    }
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }


    public void setName() {
        this.name = name;
    }
}
