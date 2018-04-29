package com.example.jasim.plateup;


import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Range;

import com.example.jasim.plateup.Sorting.RangeVariables;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jasim on 10.03.2017.
 */

public class RestaurantModel implements Serializable, Comparable<RestaurantModel> {

    private String id;
    private String email;
    private String name;
    private String telephone;
    private String type;
    private String username;
    private String address;
    private String category;
    private String city;
    private String phonenumber;
    private String postal;
    private String bankaccount;
    private String waittime;
    private String image;
    private String checkusername;
    private Uri imageURI;
    private HashMap<String, Openings> openings;
    private String status;
    private String amountopenings;
    private String rating;
    private String amountraters;
    private HashMap<String, String> raters;
    private HashMap<String, Order> orders;
    private double latitude;
    private double longitude;
    private double distanceTo;
    private String verified;

    private Analytics analytics;


    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public Analytics getAnalytics() {
        return analytics;
    }

    public void setAnalytics(Analytics analytics) {
        this.analytics = analytics;
    }

    public double getDistanceTo() {
        return distanceTo;
    }

    public void setDistanceTo(double distanceTo) {
        this.distanceTo = distanceTo;
    }

    public double getLatitude() {
        return latitude;
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

    public HashMap<String, Order> getOrders() {
        return orders;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public void setCheckusername(String checkusername) {
        this.checkusername = checkusername;
    }

    public String getCheckusername() {
        return checkusername;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setOrders(HashMap<String, Order> orders) {
        this.orders = orders;
    }

    public String getImage() {
        return image;
    }


    public void setAmountOpenings(String amountopenings) {
        this.amountopenings = amountopenings;
    }

    public Uri getImageURI() {
        return imageURI;
    }

    public void setImageURI(Uri imageURI) {
        this.imageURI = imageURI;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getWaittime() {
        return waittime;
    }

    public void setWaittime(String waittime) {
        this.waittime = waittime;
    }

    public String getRating() {
        return rating;
    }

    public String getAmountraters() {
        return amountraters;
    }


    public String getAmountOpenings() {
        return amountopenings;
    }



    public String getStatus() {
        return status;
    }

    public HashMap<String, String> getRaters() {
        return raters;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBankaccount() {
        return bankaccount;
    }

    public void setBankaccount(String bankaccount) {
        this.bankaccount = bankaccount;
    }

    public String getPostal() {
        return postal;
    }

    public void setPostal(String postal) {
        this.postal = postal;
    }



    public RestaurantModel() {}


    public HashMap<String, Openings> getOpenings() {
        return openings;
    }

    public void setOpenings(HashMap<String, Openings> openings) {
        this.openings = openings;
    }

    public String getAddress() {
        return address;
    }

    public String getCategory() {
        return category;
    }

    public String getCity() {
        return city;
    }

    public String getName() {
        return name;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getType() {
        return type;
    }

    public String getUsername() {
        return username;
    }


    public void setAddress(String address) {
        this.address = address;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFavoriteCategory() {

    }

    public int compareTo(@NonNull RestaurantModel content) {
        // Performance??? bytt slik at den ikke maa teste dette hver gang.
        RestaurantModel user = MainUActivity.getUser();
        int accumulatedRangeCut = 0;
        int accumulatedRangeCut2 = 0;
        if(user.getAnalytics() != null) {
            if(user.getAnalytics().getFavCat().equals(content.getCategory())) {
                accumulatedRangeCut += RangeVariables.getFav_cat();
            }
            if(user.getAnalytics().getFavRest().equals(content.getId())) {
                accumulatedRangeCut += RangeVariables.getFav_rest();
            }

            if(user.getAnalytics().getFavCat().equals(this.getCategory())) {
                accumulatedRangeCut2 += RangeVariables.getFav_cat();
            }
            if(user.getAnalytics().getFavRest().equals(this.getId())) {
                accumulatedRangeCut2 += RangeVariables.getFav_rest();
            }


        }

        if(MainUActivity.getUser().getAnalytics() != null && MainUActivity.getUser().getAnalytics().getFavRest().equals(content.getId())) {
            if (content.getDistanceTo() - accumulatedRangeCut < this.getDistanceTo()) {
                return 1;
            } else if (content.getDistanceTo() - accumulatedRangeCut > this.getDistanceTo()) {
                return -1;
            }
        }
        else if(MainUActivity.getUser().getAnalytics() != null && MainUActivity.getUser().getAnalytics().getFavRest().equals(this.getId())) {
            if (content.getDistanceTo() < this.getDistanceTo() - accumulatedRangeCut2) {
                return 1;
            } else if (content.getDistanceTo() > this.getDistanceTo() - accumulatedRangeCut2) {
                return -1;
            }
        }
        else {
            if (content.getDistanceTo() < this.getDistanceTo()) {
                return 1;
            } else if (content.getDistanceTo() > this.getDistanceTo()) {
                return -1;
            }
        }
        return 0;
    }
}
