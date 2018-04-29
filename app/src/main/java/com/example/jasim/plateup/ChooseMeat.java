package com.example.jasim.plateup;

import java.util.HashMap;

/**
 * Created by jasim on 11.11.2017.
 */

class ChooseMeat {
    private HashMap<String, String> meat;
    public ChooseMeat(HashMap<String, String> meat) {
        this.meat = meat;
    }

    public HashMap<String, String> getMeat() {
        return meat;
    }

    public void setMeat(HashMap<String, String> meat) {
        this.meat = meat;
    }
}
