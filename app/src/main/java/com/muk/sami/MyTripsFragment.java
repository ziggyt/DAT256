package com.muk.sami;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.muk.sami.model.Trip;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nullable;

import static com.firebase.ui.auth.AuthUI.TAG;

public class MyTripsFragment extends Fragment {


    private ListView tripsListView;
    private List<Trip> trips;

    private String userID;

    private FirebaseFirestore mDatabase;
    private CollectionReference mTripsRef;

    private View view;


    public MyTripsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_trips, container, false);

        //Initialize components
        tripsListView = view.findViewById(R.id.listView_Trips);
        trips = new ArrayList<>();

        //Initialize Firebase and Listeners
        initFirebaseSetup();
        initListeners();

        return view;
    }

    private void initFirebaseSetup() {

        mDatabase = FirebaseFirestore.getInstance();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Create a reference to the trips collection
        mTripsRef = mDatabase.collection("trips");

        //Create a query against the collection to find trips where the user is a driver
        Query driverQuery = mTripsRef.whereEqualTo("driver", userID);

        driverQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    //Log.w(TAG, "Listen failed.", e);
                    return;
                }

                trips.addAll(queryDocumentSnapshots.toObjects(Trip.class));

                Collections.sort(trips, new Comparator<Trip>() {
                    @Override
                    public int compare(Trip o1, Trip o2) {
                        return o1.getDate().compareTo(o2.getDate());
                    }
                });

                if (getActivity() != null) {
                    TripListAdapter adapter = new TripListAdapter(getActivity(), trips, userID);
                    tripsListView.setAdapter(adapter);
                }
            }
        });

        //Create a query against the collection to find trips where the user is a passenger
        Query passengerQuery = mTripsRef.whereEqualTo("passengers." + userID , true );

        passengerQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    //Log.w(TAG, "Listen failed.", e);
                    return;
                }

                trips.addAll(queryDocumentSnapshots.toObjects(Trip.class));

                Collections.sort(trips, new Comparator<Trip>() {
                    @Override
                    public int compare(Trip o1, Trip o2) {
                        return o1.getDate().compareTo(o2.getDate());
                    }
                });

                if (getActivity() != null) {
                    TripListAdapter adapter = new TripListAdapter(getActivity(), trips, userID);
                    tripsListView.setAdapter(adapter);
                }
            }
        });


    }


    private void initListeners() {

    }


}
