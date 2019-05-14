package com.muk.sami;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.muk.sami.model.User;

import java.util.ArrayList;
import java.util.List;

public class PassengerListAdapter extends ArrayAdapter<String> {
    private Activity mContext;
    private List<String> passengers;
    private List<String> passengersStatus;


    public PassengerListAdapter(Activity context, List<String> passengers, List<String> passengersStatus) {
        super(context, 0, passengers);
        this.mContext = context;
        this.passengers = passengers;
        this.passengersStatus = passengersStatus;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = mContext.getLayoutInflater();
        View view = inflater.inflate(R.layout.passenger_list_item, null, true);

        TextView passengerNameTextView = view.findViewById(R.id.passenger_name_text_view);
        CheckBox checkBox = view.findViewById(R.id.checkBox);

        String passenger = passengers.get(position);
        String passengerStatus = passengersStatus.get(position);

        passengerNameTextView.setText(passenger);

        if (passengerStatus.equals("Finished trip")){
            checkBox.setChecked(true);
        }

        return view;
    }
}


