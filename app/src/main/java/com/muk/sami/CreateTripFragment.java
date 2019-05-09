package com.muk.sami;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

public class CreateTripFragment extends Fragment {
    private EditText fromEditText;
    private EditText toEditText;
    private EditText seatsEditText;
    private TextView dateTextView;
    private DatePicker datePicker;
    private TimePicker timePicker;

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_create_trip, container, false);

        fromEditText = view.findViewById(R.id.edit_From);
        toEditText = view.findViewById(R.id.edit_To);
        seatsEditText = view.findViewById(R.id.edit_Seats);
        dateTextView = view.findViewById(R.id.textViewDate);
        datePicker = view.findViewById(R.id.datePicker);
        timePicker = view.findViewById(R.id.timePicker);

        return inflater.inflate(R.layout.fragment_create_trip, container, false);
    }

}
