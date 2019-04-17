package com.muk.sami;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TripListAdapter extends ArrayAdapter <Trip> {
    private Activity context;
    List<Trip> trips;

    public TripListAdapter(Activity context, List<Trip> trips) {
        super(context, 0, trips);
        this.context = context;
        this.trips = trips;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View listitem = inflater.inflate(R.layout.layout_list, null, true);

        TextView textViewFrom = (TextView) listitem.findViewById(R.id.textView_From);
        TextView textViewTo = (TextView) listitem.findViewById(R.id.textView_To);
        TextView textViewDate = (TextView) listitem.findViewById(R.id.textView_Date);
        TextView textViewTime = (TextView) listitem.findViewById(R.id.textView_Time);

        Trip trip = trips.get(position);
        textViewFrom.setText(trip.getFrom());
        textViewTo.setText(trip.getTo());
        textViewDate.setText(trip.getDate());
        textViewTime.setText(trip.getTime());

        return listitem;
    }

}

