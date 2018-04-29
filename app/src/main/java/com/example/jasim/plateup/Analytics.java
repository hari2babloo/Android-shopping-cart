package com.example.jasim.plateup;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jasim on 04.01.2018.
 */

class Analytics {

    private HashMap<String, Integer> category;
    private HashMap<String, Integer> restaurants;
    private HashMap<String, Integer> day;
    private HashMap<String, Integer> time;
    private int pickup;
    private int delivery;
    private int totalorders;
    private int totalspendings;
    private int averageSpending;
    private String favCat;
    private String favRest;

    public String getFavCat() {
        return favCat;
    }

    public String getFavRest() {
        return favRest;
    }

    public void setFavCat() {
        Map.Entry<String, Integer> maxEntry = null;

        for (Map.Entry<String, Integer> entry : category.entrySet())
        {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) >= 0)
            {
                maxEntry = entry;
            }
        }

        this.favCat = maxEntry.getKey();
    }

    public void setFavRest() {
        Map.Entry<String, Integer> maxEntry = null;

        for (Map.Entry<String, Integer> entry : restaurants.entrySet())
        {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) >= 0)
            {
                maxEntry = entry;
            }
        }

        this.favRest = maxEntry.getKey();
    }

    public int getAverageSpending() {
        return averageSpending;
    }

    public void setAverageSpending(int averageSpending) {
        this.averageSpending = averageSpending;
    }

    public int getTotalorders() {
        return totalorders;
    }

    public int getTotalspendings() {
        return totalspendings;
    }

    public HashMap<String, Integer> getDay() {
        return day;
    }

    public HashMap<String, Integer> getTime() {
        return time;
    }

    public int getDelivery() {
        return delivery;
    }

    public int getPickup() {
        return pickup;
    }

    public HashMap<String, Integer> getCategory() {
        return category;
    }

    public HashMap<String, Integer> getRestaurants() {
        return restaurants;
    }


    public void setTotalorders(int totalorders) {
        this.totalorders = totalorders;
    }

    public void setTotalspendings(int totalspendings) {
        this.totalspendings = totalspendings;
    }

    public void setDay(HashMap<String, Integer> day) {
        this.day = day;
    }

    public void setTime(HashMap<String, Integer> time) {
        this.time = time;
    }

    public void setDelivery(int delivery) {
        this.delivery = delivery;
    }

    public void setPickup(int pickup) {
        this.pickup = pickup;
    }

    public void setCategory(HashMap<String, Integer> category) {
        this.category = category;
    }

    public void setRestaurants(HashMap<String, Integer> restaurants) {
        this.restaurants = restaurants;
    }
}
