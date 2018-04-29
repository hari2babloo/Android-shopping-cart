package com.example.jasim.plateup.bookings;

import java.io.Serializable;

/**
 * Created by jasim on 19.03.2017.
 */

public class BookingInfo implements Serializable {
    private String bookid;
    private String username;
    private String date;
    private String hour;
    private String minutes;
    private String persons;
    private String telephone;
    private String user_id;
    private String rest_id;
    private String status;


    public String getRest_id() {
        return rest_id;
    }

    public void setRest_id(String rest_id) {
        this.rest_id = rest_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getBookid() {
        return bookid;
    }

    public void setBookid(String bookid) {
        this.bookid = bookid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHour() {
        return hour;
    }

    public String getTime() {
        return hour + ":" + minutes;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMinutes() {
        return minutes;
    }

    public void setMinutes(String minutes) {
        this.minutes = minutes;
    }

    public String getPersons() {
        return persons;
    }

    public void setPersons(String persons) {
        this.persons = persons;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

}
