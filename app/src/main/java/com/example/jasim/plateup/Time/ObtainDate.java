package com.example.jasim.plateup.Time;

/**
 * Created by jasim on 10.01.2018.
 */

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class ObtainDate
{
    public static void main(String[] args)
    {
        ObtainDate obtainDate = new ObtainDate();
        TimeZone timeZone = TimeZone.getTimeZone("US/Eastern");
        String dateFormat = "MMMM dd,yyyy G"; //MMMM dd,yyyy G
        String timeFormat = "hh:mm:ss.SSS a zzzz";
        String dayFormat = "EEEEEE";

        System.out.println("Todays Day:" + obtainDate.getTodaysDay(dayFormat,timeZone));
        System.out.println("Todays Date:" + obtainDate.getTodayDate(dateFormat,timeZone));
        System.out.println("Current Time:" + obtainDate.getCurrentTime(timeFormat,timeZone));
    }

    public String getTodaysDay(String dayFormat, TimeZone timeZone)
    {
        Date date = new Date();
      /* Specifying the format */
        DateFormat requiredFormat = new SimpleDateFormat(dayFormat);
      /* Setting the Timezone */
        requiredFormat.setTimeZone(timeZone);
      /* Picking the day value in the required Format */
        String strCurrentDay = requiredFormat.format(date).toUpperCase();
        return strCurrentDay;
    }


    public String getCurrentTime(String timeFormat, TimeZone timeZone)
    {
      /* Specifying the format */
        DateFormat dateFormat = new SimpleDateFormat(timeFormat);
      /* Setting the Timezone */
        Calendar cal = Calendar.getInstance(timeZone);
        dateFormat.setTimeZone(cal.getTimeZone());
      /* Picking the time value in the required Format */
        String currentTime = dateFormat.format(cal.getTime());
        return currentTime;
    }


    public String getTodayDate(String dateFormat, TimeZone timeZone)
    {
        Date todayDate = new Date();
       /* Specifying the format */
        DateFormat todayDateFormat = new SimpleDateFormat(dateFormat);
       /* Setting the Timezone */
        todayDateFormat.setTimeZone(timeZone);
       /* Picking the date value in the required Format */
        String strTodayDate = todayDateFormat.format(todayDate);
        return strTodayDate;
    }
}