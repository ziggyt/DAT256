package com.muk.sami;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class TripList extends ArrayAdapter <Trip> {
    private Activity context;
    List<Trip> trips;

    public TripList(Activity context, List<Trip> trips) {
        super(context, R.layout.layout_list, trips);
        this.context = context;
        this.trips = trips;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_list, null, true);

        TextView textViewFrom = (TextView) listViewItem.findViewById(R.id.textView_From);
        TextView textViewTo = (TextView) listViewItem.findViewById(R.id.textView_To);
        TextView textViewDate = (TextView) listViewItem.findViewById(R.id.textView_Date);
        TextView textViewTime = (TextView) listViewItem.findViewById(R.id.textView_Time);

        Trip trip = trips.get(position);
        textViewFrom.setText(trip.getFrom());
        textViewTo.setText(trip.getTo());
        textViewDate.setText(trip.getDate());
        textViewTime.setText(trip.getTime());

        return listViewItem;
    }
}

