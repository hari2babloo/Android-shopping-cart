package com.example.jasim.plateup;

/**
 * Created by jasim on 13.04.2017.
 */

public class DayMonthConverter {



    public static String numberToDay(String day) {
        if (day.equals("2")) {
            return "Mon";
        } else if (day.equals("3")) {
            return "Tue";
        } else if (day.equals("4")) {
            return "Wed";
        } else if (day.equals("5")) {
            return "Thu";
        } else if (day.equals("6")) {
            return "Fri";
        } else if (day.equals("7")) {
            return "Sat";
        } else if (day.equals("1")) {
            return "Sun";
        }
        return "";
    }


    public static String numberToMonth(int month) {
        if (month == 1) {
            return "Jan";
        } else if (month == 2) {
            return "Feb";
        } else if (month == 3) {
            return "Mar";
        } else if (month == 4) {
            return "Apr";
        } else if (month == 5) {
            return "May";
        } else if (month == 6) {
            return "June";
        } else if (month == 7) {
            return "July";
        }
        else if (month == 8) {
            return "Aug";
        }
        else if (month == 9) {
            return "Sept";
        }
        else if (month == 10) {
            return "Oct";
        }
        else if (month == 11) {
            return "Nov";
        }
        else if (month == 12) {
            return "Dec";
        }
        return "";
    }
}
