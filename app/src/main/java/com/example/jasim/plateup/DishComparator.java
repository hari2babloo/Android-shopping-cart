package com.example.jasim.plateup;

import java.util.Comparator;


public class DishComparator implements Comparator<Dish> {

    @Override
    public int compare(Dish o1, Dish o2) {
        return o1.compareTo(o2);
    }
}
