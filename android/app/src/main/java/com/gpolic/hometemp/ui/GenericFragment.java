package com.gpolic.hometemp.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gpolic.hometemp.R;
import com.gpolic.hometemp.adapters.MyRecyclerViewAdapter;
import com.gpolic.hometemp.data.TemperaturesDBController;
import com.gpolic.hometemp.logger.LLog;
import com.gpolic.hometemp.model.TemperatureItem;
import com.gpolic.hometemp.model.TestTempData;
import com.gpolic.hometemp.sync.GetTempFromServerUpdater;
import com.gpolic.hometemp.sync.GetTempsLocalDBUpdater;
import com.gpolic.hometemp.util.DateUtils;
import com.gpolic.hometemp.util.MyHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * the GenericFragment is using layout: fragment_prod_grid
 * <p>
 * the items layout are inflated in the adapter's code onCreateViewHolder
 * specific items are attached in adapter's onBindViewHolder
 * <p>
 * I moved the setupRecycleView in the postExecute so that the list is displayed as soon as we have data from the server
 */
public class GenericFragment extends Fragment {

    public static final int FRAG_CURRENT_TEMP = 1;
    public static final int FRAG_PAST_TEMP = 2;
    public static final String ARG_FRAG_TYPE = "fragment_type";
    private static final String TAG = GenericFragment.class.getSimpleName();

    //    private static TemperaturesList tempsList = TemperaturesList.getInstance();
    private List<TemperatureItem> tempsList = new ArrayList<TemperatureItem>();
    // keep reference in a static

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private BottomSheetBehavior mBottomSheetBehavior;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_prod_list, container, false);
        recyclerView = mainView.findViewById(R.id.recyclerview);
        swipeRefreshLayout = mainView.findViewById(R.id.swipeRefreshLayout);
        final Context mainActivity = getActivity().getApplicationContext();


        // setup the recyclerView
        setupRecyclerView(recyclerView);

        new GetTempsLocalDBUpdater().updateDataFromLocalDB(mainActivity, recyclerView);   // TODO change it to get Data from server DB, Before getting from Local DB

        // setup swipeRefresh action
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                //TODO code duplicated in Main Activity when the app is loading
                final TemperaturesDBController databaseController = new TemperaturesDBController(mainActivity);
                final Date date = databaseController.getLatestTempRecordDate();

                final String dateString = date != null ? DateUtils.dateToDBString(date) : "1/1/1900";  // if DB returns null date then use 1/1/1900

                new GetTempFromServerUpdater().getJSON(getActivity(), MyHelper.PULL_AFTER_DATE_URL, getString(R.string.load_server_data_msg), dateString);
                // end of duplicate code

                new GetTempsLocalDBUpdater().updateDataFromLocalDB(mainActivity, recyclerView);
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        View bottomSheet = mainView.findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        // setup bottom sheet action
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    mBottomSheetBehavior.setPeekHeight(0);
                }
            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {
            }
        });
        mBottomSheetBehavior.setPeekHeight(0);  // zero to hide the bottomsheet

        return mainView;
    }


    /**
     * getDataList returns the array of temperatures for Analytics fragment
     *
     * @return Returns an array of double Temperature values
     */
    private List<Float> getDataList() {
        ArrayList<Float> list = new ArrayList<>(TestTempData.NUM_OF_TEMPS);

        if (tempsList.size() != 0) {
            for (TemperatureItem tmi : tempsList) {
                list.add(tmi.getTemperature());
            }
        } else LLog.e(TAG, "The temps array is empty");
        return list;
    }


    // TODO - add ItemAnimators get Staggered Layout example working better by varying the length of content more
    private void setupRecyclerView(RecyclerView recyclerView) {
        MyRecyclerViewAdapter myRecyclerViewAdapter;

        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.addItemDecoration(new GridDividerDecoration(recyclerView.getContext()));

        myRecyclerViewAdapter = new MyRecyclerViewAdapter(getActivity(), getActivity(), tempsList);
        recyclerView.setAdapter(myRecyclerViewAdapter);
    }
}

