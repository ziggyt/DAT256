package com.muk.sami;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TripDetailViewFragment extends Fragment {

    private TextView textViewFrom;
    private TextView textViewTo;
    private TextView textViewDate;
    private TextView textViewSeats;
    private TextView textViewTime;
    //private Button button;

    private FirebaseFirestore mDatabase;
    //private CollectionReference mTripsRef;

    private View view;

    public TripDetailViewFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,@Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_trip_detailview, container, false);

        //String tripId = getArguments().getString("tripId");
        String tripId = TripDetailViewFragmentArgs.fromBundle(getArguments()).getTripId();

        mDatabase = FirebaseFirestore.getInstance();

        textViewFrom = view.findViewById(R.id.textview_from);
        textViewTo = view.findViewById(R.id.textview_to);
        //textViewDate = view.findViewById(R.id.textview_date);
        //textViewSeats = view.findViewById(R.id.textview_seats);
        //textViewTime = view.findViewById(R.id.textview_time);

        textViewFrom.setText(tripId);



        return view;
    }

}
