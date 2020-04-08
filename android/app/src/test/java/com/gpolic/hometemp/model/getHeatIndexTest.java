package com.gpolic.hometemp.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by George on 12/5/2017.
 */


public class getHeatIndexTest {

    TemperatureItem tempItem1, tempItem2, tempItem3;

    @Before
    public void setUp() {

        tempItem1 = new TemperatureItem(21.8f, 65.0f);
        tempItem2 = new TemperatureItem(21.8f, 65.1f);
        tempItem3 = new TemperatureItem(27.8f, 48.7f);
    }

    @Test
    public void testGetHeatIndexCel() {
        double heatIndex1 = tempItem1.getHeatIndexCel();
        double heatIndex2 = tempItem2.getHeatIndexCel();
        double heatIndex3 = tempItem3.getHeatIndexCel();

        heatIndex1 = Math.round(heatIndex1 * 100.0) / 100.0;
        heatIndex2 = Math.round(heatIndex2 * 100.0) / 100.0;
        heatIndex3 = Math.round(heatIndex3 * 100.0) / 100.0;

        Assert.assertEquals(21.73, heatIndex1, 0.00001f);
        Assert.assertEquals(21.74, heatIndex2, 0.00001f);
        Assert.assertEquals(28.12, heatIndex3, 0.00001f);

    }

}