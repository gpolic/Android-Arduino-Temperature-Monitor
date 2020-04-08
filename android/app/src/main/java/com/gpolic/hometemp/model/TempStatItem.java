package com.gpolic.hometemp.model;

import java.util.Date;

/**
 * Created by George on 23/8/2017.
 */

public class TempStatItem {
    private Date date;
    private float min, max, avg;

//    public TempStatItem(Date date, float min, float max, float avg) {
//        this.date = date;
//        this.min = min;
//        this.max = max;
//        this.avg = avg;
//    }


    public void setDate(Date date) {
        this.date = date;
    }

    public void setMin(float min) {
        this.min = min;
    }

    public void setMax(float max) {
        this.max = max;
    }

    public void setAvg(float avg) {
        this.avg = avg;
    }

    public float getAvg() {
        return avg;
    }

    public Date getDate() {
        return date;
    }

}
