package com.gpolic.hometemp.model;

import java.util.ArrayList;

/**
 * Created by George on 4/4/2017.
 * Singleton object for the temps data
 */

public class TemperaturesList {
// TODO  create a new arraylist here. need to provide add()  and get()  methods
    ArrayList<TemperatureItem> tempFromTheServer =  new ArrayList<TemperatureItem>();

    private static final TemperaturesList holder = new TemperaturesList();
    // create static

    public static TemperaturesList getInstance() {
        return  holder;
    }


    public void add(TemperatureItem temperatureItem) {
        tempFromTheServer.add(temperatureItem);
    }

    public TemperatureItem get(int position) {
        return tempFromTheServer.get(position);
    }

    public int size() {
        return tempFromTheServer.size();
    }

    public void clear() {
        tempFromTheServer.clear();
    }

    public ArrayList<TemperatureItem> items() {
        return tempFromTheServer;
    }

}
