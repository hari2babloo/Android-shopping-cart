package com.example.jasim.plateup;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
public class Dish implements Comparable<Dish> {

    String dishname;
    String dishnumber;
    String description;
    String price;
    String type = "0";
    ArrayList<String> allergens = new ArrayList<>();
    int nextData = 0;

    public void setData(Object attribute) {
        if(nextData == 0) {
//            for (Object value : ((HashMap<String, Dish>)attribute).values()) {
//                allergens.add(value.toString());
//            }
        }
        else if(nextData == 1) {
            price = attribute.toString();
        }
        else if(nextData == 2) {
            description = attribute.toString();
        }
        else if(nextData == 3) {
            dishnumber = attribute.toString();
        }
        else if(nextData == 4) {

        }
        else if(nextData == 5) {
            dishname = attribute.toString();
        }
        nextData++;
    }

    public String getDishname() {
        return dishname;
    }

    public void setDishname(String dishname) {
        this.dishname = dishname;
    }


    public String getDishnumber() {
        return dishnumber;
    }

    public void setDishnumber(String dishnumber) {
        this.dishnumber = dishnumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public ArrayList<String> getAllergens() {
        return allergens;
    }

    public void setAllergens(ArrayList<String> allergens) {
        this.allergens = allergens;
    }


    public int compareTo(@NonNull Dish dish) {
        if(Integer.parseInt(dish.getDishnumber()) < Integer.parseInt(this.getDishnumber())) {
            return 1;
        }
        else if(Integer.parseInt(dish.getDishnumber()) > Integer.parseInt(this.getDishnumber())) {
            return -1;
        }
        return 0;
    }
}
