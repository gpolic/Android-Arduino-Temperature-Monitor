package com.gpolic.hometemp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by George on 23/8/2017.
 */

public class TempStatList {
    private List<TempStatItem> tempStatisticList = new ArrayList<TempStatItem>();

    public void add(TempStatItem item) {
        tempStatisticList.add(item);
    }

    public TempStatItem get(int index) {
        return tempStatisticList.get(index);
    }

    public int size() {
        return tempStatisticList.size();
    }
}
