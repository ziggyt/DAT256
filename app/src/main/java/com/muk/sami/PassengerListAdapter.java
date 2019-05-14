package com.muk.sami;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.muk.sami.model.User;

import java.util.ArrayList;
import java.util.List;

public class PassengerListAdapter extends ArrayAdapter<String> {
    private Activity mContext;
    private List<String> passengers;


    public PassengerListAdapter(Activity context, List<String> objects) {
        super(context, 0, objects);
        this.mContext = context;
        this.passengers = objects;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = mContext.getLayoutInflater();
        View view = inflater.inflate(R.layout.passenger_list_item, null, true);
        TextView passengerNameTextView = view.findViewById(R.id.passenger_name_text_view);

        String passenger = passengers.get(position);

        passengerNameTextView.setText(passenger);

        return view;
    }
}

