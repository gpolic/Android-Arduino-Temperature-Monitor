package com.gpolic.hometemp.adapters;

import com.gpolic.hometemp.model.TemperatureItem;

import java.util.List;

/**
 * Enables us to use setValues without knowing which Adapter we have
 */

public interface RecyclerAdapterWithSetValues {

    void setValues(List<TemperatureItem> values);

}
