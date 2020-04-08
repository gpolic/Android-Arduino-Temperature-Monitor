package com.gpolic.hometemp.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gpolic.hometemp.R;
import com.gpolic.hometemp.data.StatisticsDBController;
import com.gpolic.hometemp.logger.LLog;
import com.gpolic.hometemp.model.TempStatItem;
import com.gpolic.hometemp.model.TempStatList;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;

import static com.gpolic.hometemp.util.DateUtils.DayInDate;

/**
 * chart Fragment with the average temperatures
 */
public class TempsChartFragment extends Fragment {

//    private final static String[] daysNum = new String[]{"Mon", "Tue", "Wen", "Thu", "Fri", "Sat", "Sun"};
//    private final static String[] days = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
//            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
//            "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};
//    private final int currentYear = Calendar.getInstance().get(Calendar.YEAR);

    private final static String[] monthThreeLetterNames = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug",
            "Sep", "Oct", "Nov", "Dec"};
    private static final String TAG = TempsChartFragment.class.getSimpleName();


    private LineChartView topChartView;
    private ColumnChartView bottomChartView;
    private LineChartData topLineData;
    private ColumnChartData bottomColumnData;
    private List<MonthlyStatistics> fullStatisticDataList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_chart, container, false);

        topChartView = mainView.findViewById(R.id.chart);
        bottomChartView = mainView.findViewById(R.id.chart_bottom);

//        final Context mainActivity = getActivity().getApplicationContext();

        // Build the full statistic data
        calculateMonthValues();

        generateTopDetailChart();
        generateBottomMonthChart();

        return mainView;
    }


    /**
     * Build the fullStatisticDataList. It will contain one list for each month
     * fullStatisticDataList is a list of MonthlyStatistics
     * Each monthly statistic has a list of TempItems for each day
     */
    private void calculateMonthValues() {
        List<TempStatItem> temperatureStatsFullList;
        Calendar calendar = Calendar.getInstance();
        LLog.d(TAG, "Calculating month values");

        // Retrieve the statistic data from the Database. We need data for the past 12 months
        temperatureStatsFullList = new StatisticsDBController(getContext()).getAllTempStatisticsList();

        // Initialize variables before starting the loop
        int prevMonth = -1;
        int counter = 0;
        double avgTemp = 0.0;
        fullStatisticDataList = new ArrayList<MonthlyStatistics>();
        MonthlyStatistics tempMonthlyStatsHolder = new MonthlyStatistics();

        /* prepare a list with 12 (month) lists.
        Add each item in the respective month sublist/starting from the latest month in the data
        The first item (zero) is the data of the last month - rightmost column in our chart
        */
        for (int i = temperatureStatsFullList.size() - 1; i > 0; i--) {

            TempStatItem tempItem = temperatureStatsFullList.get(i);
            calendar.setTime(tempItem.getDate());
            int month = calendar.get(Calendar.MONTH) + 1;
            int year = calendar.get(Calendar.YEAR);

            if (month != prevMonth) {
                if (prevMonth != -1) {
                    // when a months data is scanned add this Monthly data to the full Statistics
                    tempMonthlyStatsHolder.avgMonthTemperature = (float) avgTemp / counter;
                    fullStatisticDataList.add(tempMonthlyStatsHolder);
                    counter = 0;
                    avgTemp = 0.0;
                }
                counter++;
                avgTemp += tempItem.getAvg();
                tempMonthlyStatsHolder = new MonthlyStatistics();
                tempMonthlyStatsHolder.tempStatList = new TempStatList();
                tempMonthlyStatsHolder.year = year;
                tempMonthlyStatsHolder.month = month;
                tempMonthlyStatsHolder.tempStatList.add(tempItem);
                prevMonth = month;
            } else {
                counter++;
                avgTemp += tempItem.getAvg();
                tempMonthlyStatsHolder.tempStatList.add(tempItem);
            }
        }
    }


    private void generateTopDetailChart() {

        if (fullStatisticDataList.size() == 0) return;

        List<AxisValue> daysXBottomLabels = new ArrayList<AxisValue>();
        List<PointValue> daysYValues = new ArrayList<PointValue>();

        // during the creation of the chart we will show data for the last month (item 0)
        MonthlyStatistics lastMonthData = fullStatisticDataList.get(0);
        TempStatList lastMonthTemperatureStats = lastMonthData.tempStatList;

        int axisCounter = 0;
        for (int i = lastMonthTemperatureStats.size() - 1; i >= 0; i--) {
            TempStatItem dayTemperatureStats = lastMonthTemperatureStats.get(i);
            int day = DayInDate(dayTemperatureStats.getDate());
            float dailyAverageTemperature = dayTemperatureStats.getAvg();

            daysYValues.add(new PointValue(day, dailyAverageTemperature));  // Dates start from 1 not 0
            daysXBottomLabels.add(new AxisValue(axisCounter).setLabel(day + ""));
            axisCounter++;
        }

        Line line = new Line(daysYValues);
        line.setColor(ChartUtils.COLOR_GREEN).setCubic(true);

        List<Line> lines = new ArrayList<Line>();
        lines.add(line);

        topLineData = new LineChartData(lines);
        topLineData.setAxisXBottom(new Axis(daysXBottomLabels).setHasLines(true));
        topLineData.setAxisYLeft(new Axis().setHasLines(true).setMaxLabelChars(3));

        topChartView.setLineChartData(topLineData);

        // For build-up animation you have to disable viewport recalculation.
        topChartView.setViewportCalculationEnabled(false);

        // And set initial max viewport and current viewport- remember to set viewports after data.
        // TODO fix the size of the graph related to the data
        Viewport v = new Viewport(0, 50, 31, -10);

        topChartView.setMaximumViewport(v);
        topChartView.setCurrentViewport(v);
        topChartView.setZoomType(ZoomType.VERTICAL);
    }

    private void generateBottomMonthChart() {
        final int numSubcolumns = 1;
        final int numColumns = fullStatisticDataList.size();

        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        List<Column> columns = new ArrayList<Column>();
        List<SubcolumnValue> values;

        int axisCounter = 0;
        for (int i = numColumns - 1; i >= 0; i--) {

            values = new ArrayList<SubcolumnValue>();
            for (int j = 0; j < numSubcolumns; ++j) {
                float avgTemperature = fullStatisticDataList.get(i).avgMonthTemperature;

                int columnColor;
                if (avgTemperature < 18.0f) columnColor = Color.parseColor("#02CCEE");
                else if (avgTemperature < 20.0f) columnColor = Color.parseColor("#02CC56");
                else if (avgTemperature < 22.0f) columnColor = Color.parseColor("#5EDD32");
                else if (avgTemperature < 26.0f) columnColor = Color.parseColor("#7EEE22");
                else if (avgTemperature < 28.0f) columnColor = Color.parseColor("#EEEE02");
                else columnColor = Color.parseColor("#EE7000");

                values.add(new SubcolumnValue(avgTemperature, columnColor));
            }

            // TODO beware !!
            final int monthNumber = fullStatisticDataList.get(i).month;
            axisValues.add(new AxisValue(axisCounter++).setLabel(monthThreeLetterNames[monthNumber - 1]));

            columns.add(new Column(values).setHasLabelsOnlyForSelected(true));
        }

        bottomColumnData = new ColumnChartData(columns);
        bottomColumnData.setAxisXBottom(new Axis(axisValues).setHasLines(true));
        bottomColumnData.setAxisYLeft(new Axis().setHasLines(true).setMaxLabelChars(2));
        bottomChartView.setColumnChartData(bottomColumnData);

        // Set value touch listener that will trigger changes for chartTop.
        bottomChartView.setOnValueTouchListener(new ValueTouchListener());

        // Set selection mode to keep selected month column highlighted.
        bottomChartView.setValueSelectionEnabled(true);
        bottomChartView.setZoomType(ZoomType.HORIZONTAL);

        Viewport v = new Viewport(0, 50, 12, -10);
        bottomChartView.setCurrentViewport(v);

    }


    private void generateTopChartDataWhenClicked(int month, int color) {
        // Cancel last animation if not finished.
        topChartView.cancelDataAnimation();

        List<AxisValue> daysXBottomLabels = new ArrayList<AxisValue>();
        List<PointValue> daysYValues = new ArrayList<PointValue>();


        final int fullDataSize = fullStatisticDataList.size();
        LLog.d(TAG, "Click on Chart Months on month " + month + " of " + fullDataSize);

        // Avoid invalid month values which would crash the app
        if (month < 0 || month > fullDataSize) {
            LLog.e(TAG, "Month value is not valid: " + month);
            return;
        }

        // we will get the Data for the month selected.
        MonthlyStatistics selectedMonthData = fullStatisticDataList.get(fullDataSize - month - 1);

        TempStatList selectedMonthTemperatureStats = selectedMonthData.tempStatList;

        Line line = topLineData.getLines().get(0);// For this example there is always only one line.
        line.setColor(color);

//        for (PointValue value : line.getValues()) {
//            // Change target only for Y value.
//            value.setTarget(value.getX(), (float) Math.random() * 100);
//        }

        int axisCounter = 0;
        for (int i = selectedMonthTemperatureStats.size() - 1; i >= 0; i--) {
            TempStatItem dayTemperatureStats = selectedMonthTemperatureStats.get(i);
            int day = DayInDate(dayTemperatureStats.getDate());
            float dailyAverageTemperature = dayTemperatureStats.getAvg();

            daysYValues.add(new PointValue(day, dailyAverageTemperature));  // Dates start from 1 not 0
            daysXBottomLabels.add(new AxisValue(axisCounter).setLabel(day + ""));
            axisCounter++;
        }

        Line newLine = new Line(daysYValues);
        newLine.setColor(ChartUtils.COLOR_GREEN).setCubic(true);

        List<Line> lines = new ArrayList<Line>();
        lines.add(newLine);
        topLineData.setLines(lines);
        topLineData.setAxisXBottom(new Axis(daysXBottomLabels).setHasLines(true));
        topLineData.setAxisYLeft(new Axis().setHasLines(true).setMaxLabelChars(3));

        topChartView.setLineChartData(topLineData);

        // adjust the width for very small sizes (a few samples in the month look weird)
        int width = selectedMonthTemperatureStats.size();
        if (width < 20) width = 31;

        Viewport v = new Viewport(0, 60, width - 1, -10);
        topChartView.setCurrentViewportWithAnimation(v);

        // Start new data animation with 200ms duration;
        topChartView.startDataAnimation(200);
    }


    private class ValueTouchListener implements ColumnChartOnValueSelectListener {

        @Override
        public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
            LLog.d(TAG, "" + columnIndex + " " + subcolumnIndex);
            generateTopChartDataWhenClicked(columnIndex, value.getColor());
        }

        @Override
        public void onValueDeselected() {
            generateTopChartDataWhenClicked(0, 0);
        }
    }


    private class MonthlyStatistics {
        int month, year;
        float avgMonthTemperature;
        TempStatList tempStatList;
    }
}

