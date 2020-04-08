package com.gpolic.hometemp.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gpolic.hometemp.R;
import com.gpolic.hometemp.model.TemperatureItem;

import java.util.List;

/**
 * RecyclerView Adapter to present the list of temperatures
 * It will use and inflate the Layout temp_list_item.xml
 */
public class MyRecyclerViewAdapter
        extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>
        implements RecyclerAdapterWithSetValues {

    private final TypedValue mTypedValue = new TypedValue();
    private int mBackground;

    private List<TemperatureItem> mValues;

    public MyRecyclerViewAdapter(Activity mActivityIn, Context context, List<TemperatureItem> items) {
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
//        Activity mActivity = mActivityIn;
        mBackground = mTypedValue.resourceId;
        mValues = items;
    }

    /**
     * Sets the data for the RecyclerView and notifies the adapter to show the new data
     *
     * @param values
     */
    public void setValues(List<TemperatureItem> values) {
        this.mValues = values;
        this.notifyDataSetChanged();
    }

    public TemperatureItem getValueAt(int position) {
        return mValues.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.temp_list_item, parent, false);
        view.setBackgroundResource(mBackground);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final int pos = holder.getAdapterPosition();
        holder.mBoundString = String.valueOf(mValues.get(pos));
        holder.mTemperatureTextView.setText(mValues.get(pos).getTemperature() + " C");
        holder.mHumidityTextView.setText(mValues.get(pos).getHumidity() + "%");
        holder.mDateTextView.setText(mValues.get(pos).getDateString());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                TemperatureItem tempItem = mValues.get(pos);

                String HeatInd = String.format("%.2f", tempItem.getHeatIndexCel());
                Toast.makeText(context, "Heat Index: " + HeatInd, Toast.LENGTH_SHORT).show();
            }
        });

        Glide.with(holder.mImageView.getContext())
                .load(R.drawable.art_clear)
                .fitCenter()
                .into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private final ImageView mImageView;
        private final TextView mDateTextView;
        private final TextView mTemperatureTextView;
        private final TextView mHumidityTextView;
        private String mBoundString;
        //           public int os_version;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = view.findViewById(R.id.weather_icon);
            mDateTextView = view.findViewById(R.id.date_view);
            mTemperatureTextView = view.findViewById(R.id.temp_view);
            mHumidityTextView = view.findViewById(R.id.humidity_view);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTemperatureTextView.getText();
        }
    }
}
