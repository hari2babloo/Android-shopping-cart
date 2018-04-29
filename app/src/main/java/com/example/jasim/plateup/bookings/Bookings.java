package com.example.jasim.plateup.bookings;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by jasim on 07.05.2017.
 */

public class Bookings implements Serializable {
    private HashMap<String, HashMap<String, HashMap<String, BookingInfo>>> bookings;


    public Bookings(HashMap<String, HashMap<String, HashMap<String, BookingInfo>>> bookings) {
        this.bookings = bookings;
    }

    public Bookings() {

    }

    public HashMap<String, HashMap<String, HashMap<String, BookingInfo>>> getBookings() {
        return bookings;
    }

    public void setBookingList(HashMap<String, HashMap<String, HashMap<String, BookingInfo>>> bookings) {
        this.bookings = bookings;
    }
}
