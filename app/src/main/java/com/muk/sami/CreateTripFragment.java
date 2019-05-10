package com.muk.sami;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.muk.sami.model.SimpleNotification;
import com.muk.sami.model.Trip;

import java.util.Calendar;
import java.util.Date;

import static com.firebase.ui.auth.AuthUI.TAG;

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
    private CollectionReference mNotificationRef;

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
        mNotificationRef = mDatabase.collection("tripBookingNotification");

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

        return view;
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

        SimpleNotification message = new SimpleNotification("Trip created");
        mNotificationRef.document(tripId).set(message);

        Toast.makeText(getContext(), "Resa tillagd", Toast.LENGTH_LONG).show(); //TODO replace with string value

        FirebaseMessaging.getInstance().subscribeToTopic(tripId)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = getString(R.string.msg_subscribed);
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_subscribe_failed);
                        }
                        //Log.d(TAG, msg);
                        //Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
