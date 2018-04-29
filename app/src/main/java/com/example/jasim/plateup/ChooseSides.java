package com.example.jasim.plateup;

import java.util.HashMap;

/**
 * Created by jasim on 11.11.2017.
 */

class ChooseSides {
    private HashMap<String, String> sides;

    public ChooseSides(HashMap<String, String> sides) {
        this.sides = sides;
    }
    public HashMap<String, String> getSides() {
        return sides;
    }

    public void setSides(HashMap<String, String> sides) {
        this.sides = sides;
    }
}
