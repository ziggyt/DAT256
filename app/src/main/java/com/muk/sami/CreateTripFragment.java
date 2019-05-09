package com.muk.sami;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.muk.sami.model.Trip;

import java.util.Calendar;
import java.util.Date;

public class CreateTripFragment extends Fragment {
    private EditText fromEditText;
    private EditText toEditText;
    private EditText seatsEditText;
    private TextView dateTextView;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private Button addTripButton;

    private FirebaseFirestore mDatabase;
    private CollectionReference mTripsRef;

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
        addTripButton = view.findViewById(R.id.addTripButton);
        timePicker.setIs24HourView(true);

        mDatabase = FirebaseFirestore.getInstance();
        mTripsRef = mDatabase.collection("trips");

        addTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String from = fromEditText.getText().toString();
                String to = toEditText.getText().toString();
                String seats = seatsEditText.getText().toString();
                Date date = dateFromDatePicker(datePicker, timePicker);

                createTrip(from, to, date, Integer.parseInt(seats));
            }
        });

        return inflater.inflate(R.layout.fragment_create_trip, container, false);
    }

    private Date dateFromDatePicker(DatePicker p, TimePicker t){
        int year = p.getYear();
        int month = p.getMonth();
        int day = p.getDayOfMonth();
        int hour = t.getHour();
        int min = t.getMinute();

        Calendar calendar = Calendar.getInstance();

        calendar.set(year, month, day, hour, min);

        return calendar.getTime();
    }

    private void createTrip(String from, String to, Date date, int seats) {

        String tripId = mTripsRef.document().getId();
        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Trip trip = new Trip(tripId, from, to, date,0 , seats, driverId);
        mTripsRef.document(tripId).set(trip);

        Toast.makeText(getContext(), "Resa tillagd", Toast.LENGTH_LONG).show(); //TODO replace with string value
    }

}
