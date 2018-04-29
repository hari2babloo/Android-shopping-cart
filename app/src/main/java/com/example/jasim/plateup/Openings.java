package com.example.jasim.plateup;

import java.io.Serializable;

/**
 * Created by jasim on 26.03.2017.
 */

public class Openings{

    private String fromDay;
    private String toDay;
    private String open;
    private String close;

    public Openings() {
    }

    public String getFromDay() {
        return fromDay;
    }

    public void setFromDay(String fromDay) {
        this.fromDay = fromDay;
    }

    public String getToDay() {
        return toDay;
    }

    public void setToDay(String toDay) {
        this.toDay = toDay;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }
}
