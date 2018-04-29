package com.example.jasim.plateup;

import android.support.annotation.NonNull;

import com.example.jasim.plateup.Sorting.RangeVariables;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by jasim on 22.12.2016.
 */

public class Content implements Serializable, Comparable<Content>{
    private String id;
    private String rest_id;
    private String image;
    private String restaurant_name;
    private String dishname;
    private String description;
    private String price;
    private int totalPrice = 0;
    private int antall = 1;
    private String quantity;
    private String every;
    private String period;
    private String type;
    private String dishnumber;
    private HashMap<String, String> allergies = new HashMap<>();
    private int nextData = 0;
    private Choose choose;
    private String category;

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    private double distanceTo = 0;

    private double latitude;
    private double longitude;

    public double getLatitude() {
        return latitude;
    }

    public double getDistanceTo() {
        return distanceTo;
    }

    public void setDistanceTo(double distance) {
        this.distanceTo = distance;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    private HashMap<String, Integer> extraInfo = new HashMap<>();
    private String comment;


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

    public Choose getChoose() {
        return choose;
    }

    public void setChoose(Choose choose) {
        this.choose = choose;
    }

    public void setData(Object attribute) {
        if(nextData == 0) {
            for (Object value : ((HashMap<String, Dish>)attribute).keySet()) {
                System.out.println("Value: " + value.toString());
                allergies.put(value.toString(), "1");
            }
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
            id = attribute.toString();
        }
        else if(nextData == 5) {
            // Legg inn Bilde
        }
        else if(nextData == 6) {
            dishname = attribute.toString();
        }
        else if(nextData == 7) {
            System.out.println("setting choose!");
            System.out.println("chooseMeat: " + attribute);
            HashMap<String, HashMap<String, String>> test = (HashMap<String, HashMap<String, String>>)attribute;


            HashMap<String, String> chooseMeatList = test.get("choosemeat");
            HashMap<String, String> chooseSidesList = test.get("choosesides");

            ChooseMeat chooseMeat = new ChooseMeat(chooseMeatList);
            ChooseSides chooseSides = new ChooseSides(chooseSidesList);
            choose = new Choose(chooseMeat, chooseSides);


        }
        nextData++;
    }
    public String getDishnumber() {
        return dishnumber;
    }

    public void setDishnumber(String dishnumber) {
        this.dishnumber = dishnumber;
    }

    public String toString() {
        return id;
    }

    public boolean equals(Content content) {
        return this.toString().equals(content.toString());
    }

    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }

    public HashMap<String, String> getAllergies() {
        return allergies;
    }

    public void setAllergies(HashMap<String, String> allergies) {
        this.allergies = allergies;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEvery() {
        return every;
    }

    public String getPeriod() {
        return period;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setEvery(String every) {
        this.every = every;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    // Add restaurant to constructor to make it work with restaurant name.
    public Content(String dishname, String description, String image, String price, String restaurant_name) {
        this.dishname = dishname;
        this.description = description;
        this.image = image;
        this.price = price;
        this.totalPrice = Integer.parseInt(price);
    }
    public Content(){

    }

    public String getRest_id() {
        return rest_id;
    }

    public void setRest_id(String rest_id) {
        this.rest_id = rest_id;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getAntall() {
        return antall;
    }
    public void addOne() {
        antall++;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRestaurant_name() {
        return restaurant_name;
    }

    public void setRestaurant_name(String restaurant_name) { this.restaurant_name = restaurant_name;}

    public String getDishname() {
        return dishname;
    }

    public void setDishname(String dishname) {
        this.dishname = dishname;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setAntall(int antall) {
        this.antall = antall;
    }

    public void setDescription(String description) {
        this.description = description;
    }



    public int compareTo(@NonNull Content content) {
        System.out.println("Comperator content: " + content.getDishname() + content.getDistanceTo());
        RestaurantModel user = MainUActivity.getUser();
        if(user != null) {
            int accumulatedRangeCut = 0;
            int accumulatedRangeCut2 = 0;

            if (user.getAnalytics() != null) {
                if (user.getAnalytics().getFavCat().equals(content.getCategory())) {
                    accumulatedRangeCut += RangeVariables.getFav_cat();
                }
                if (user.getAnalytics().getFavRest().equals(content.getId())) {
                    accumulatedRangeCut += RangeVariables.getFav_rest();
                }

                if (user.getAnalytics().getFavCat().equals(this.getCategory())) {
                    accumulatedRangeCut2 += RangeVariables.getFav_cat();
                }
                if (user.getAnalytics().getFavRest().equals(this.getId())) {
                    accumulatedRangeCut2 += RangeVariables.getFav_rest();
                }


            }


            if (content.getDistanceTo() == 0.0) {
                System.out.println("Kommer vi hit noen gang?? 1");

                if (content.getDishnumber() == null) {
                    return 0;
                }
                if (Integer.parseInt(content.getDishnumber()) < Integer.parseInt(this.getDishnumber())) {
                    return 1;
                } else if (Integer.parseInt(content.getDishnumber()) > Integer.parseInt(this.getDishnumber())) {
                    return -1;
                }
                return 0;
            } else {
                System.out.println("Kommer vi hit noen gang?? 2");

                if (MainUActivity.getUser().getAnalytics() != null && MainUActivity.getUser().getAnalytics().getFavRest().equals(content.getRest_id())) {
                    if (content.getDistanceTo() - accumulatedRangeCut < this.getDistanceTo()) {
                        return 1;
                    } else if (content.getDistanceTo() - accumulatedRangeCut > this.getDistanceTo()) {
                        return -1;
                    }
                } else if (MainUActivity.getUser().getAnalytics() != null && MainUActivity.getUser().getAnalytics().getFavRest().equals(this.getRest_id())) {
                    if (content.getDistanceTo() < this.getDistanceTo() - accumulatedRangeCut2) {
                        return 1;
                    } else if (content.getDistanceTo() > this.getDistanceTo() - accumulatedRangeCut2) {
                        return -1;
                    }
                } else {
                    if (content.getDistanceTo() < this.getDistanceTo()) {
                        return 1;
                    } else if (content.getDistanceTo() > this.getDistanceTo()) {
                        return -1;
                    }
                }
            }
        }
        return 0;
    }
}
