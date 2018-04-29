package com.example.jasim.plateup;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jasim on 02.07.2017.
 */

public class Order {
    private String id;
    private String u_id;
    private String r_id;

    private String dishNumber;

    private String timeOfOrder;
    private String dateOfOrder;

    private String timeOfArrival;
    private String dateOfArrival;

    private String foodReadyAt;
    private String foodReadyAtDate;

    private String price_w_vat;
    private String vat;
    private String price_wo_vat;
    private String price_w_vat_delivery;
    private String delivery;
    private ArrayList<SmallContent> dishes;
    private String address;
    private String postcode;
    private String username;
    private String deliveryPrice;
    private String restName;
    private boolean seen;


    public void setFoodReadyAtDate(String foodReadyAtDate) {
        this.foodReadyAtDate = foodReadyAtDate;
    }

    public String getFoodReadyAtDate() {
        return foodReadyAtDate;
    }

    public boolean getSeen() {
        return this.seen;
    };

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getRestName() {
        return restName;
    }

    public void setRestName(String restName) {
        this.restName = restName;
    }

    public String getDishNumber() {
        return dishNumber;
    }

    public void setDishNumber(String dishNumber) {
        this.dishNumber = dishNumber;
    }

    public String getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryPrice(String deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }

    public String getPrice_w_vat() {
        return price_w_vat;
    }

    public void setPrice_w_vat(String price_w_vat) {
        this.price_w_vat = price_w_vat;
    }

    public String getPrice_w_vat_delivery() {
        return price_w_vat_delivery;
    }

    public void setPrice_w_vat_delivery(String price_w_vat_delivery) {
        this.price_w_vat_delivery = price_w_vat_delivery;
    }

    public String getPrice_wo_vat() {
        return price_wo_vat;
    }

    public void setPrice_wo_vat(String price_wo_vat) {
        this.price_wo_vat = price_wo_vat;
    }

    public String getVat() {
        return vat;
    }

    public void setVat(String vat) {
        this.vat = vat;
    }

    public String getFoodReadyAt() {
        return foodReadyAt;
    }

    public void setFoodReadyAt(String foodReadyAt) {
        this.foodReadyAt = foodReadyAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getU_id() {
        return u_id;
    }

    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    public String getR_id() {
        return r_id;
    }

    public void setR_id(String r_id) {
        this.r_id = r_id;
    }

    public String getTimeOfOrder() {
        return timeOfOrder;
    }

    public void setTimeOfOrder(String timeOfOrder) {
        this.timeOfOrder = timeOfOrder;
    }

    public String getDateOfOrder() {
        return dateOfOrder;
    }

    public void setDateOfOrder(String dateOfOrder) {
        this.dateOfOrder = dateOfOrder;
    }

    public String getTimeOfArrival() {
        return timeOfArrival;
    }

    public void setTimeOfArrival(String timeOfArrival) {
        this.timeOfArrival = timeOfArrival;
    }

    public String getDateOfArrival() {
        return dateOfArrival;
    }

    public void setDateOfArrival(String dateOfArrival) {
        this.dateOfArrival = dateOfArrival;
    }


    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public ArrayList<SmallContent> getDishes() {
        return dishes;
    }

    public void setDishes(ArrayList<SmallContent> dishes) {
        this.dishes = dishes;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }
}
