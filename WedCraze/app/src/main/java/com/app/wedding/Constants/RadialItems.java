package com.app.wedding.Constants;

/**
 * Created by LENOVO on 5/2/2017.
 */

public class RadialItems {
    int res;
    String value;

    public RadialItems(int res, String value) {
        this.res = res;
        this.value = value;
    }

    public String itemValue(){
        return  this.value;
    }

    public int getResource(){
        return  this.res;
    }
}
