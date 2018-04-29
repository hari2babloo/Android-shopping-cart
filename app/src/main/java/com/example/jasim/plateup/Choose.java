package com.example.jasim.plateup;

/**
 * Created by jasim on 11.11.2017.
 */

public class Choose {

    ChooseMeat choosemeat;
    ChooseSides choosesides;


    public Choose(ChooseMeat choosemeat, ChooseSides choosesides) {
        this.choosemeat = choosemeat;
        this.choosesides = choosesides;
    }

    public ChooseMeat getChoosemeat() {
        return choosemeat;
    }

    public ChooseSides getChoosesides() {
        return choosesides;
    }

    public void setChoosemeat(ChooseMeat choosemeat) {
        this.choosemeat = choosemeat;
    }

    public void setChoosesides(ChooseSides choosesides) {
        this.choosesides = choosesides;
    }
}
