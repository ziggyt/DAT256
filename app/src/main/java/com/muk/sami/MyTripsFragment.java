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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    private FloatingActionButton createTripButton;

    private List<Trip> driverTrips;
    private List<Trip> passengerTrips;

    private boolean firstListenerDone;

    private FirebaseFirestore mDatabase;
    private CollectionReference mTripsRef;

    private View view;

    private SignInListener mSignInListener;


    public MyTripsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_trips, container, false);

        //Initialize components
        tripsListView = view.findViewById(R.id.listView_Trips);
        createTripButton = view.findViewById(R.id.createTripButton);

        driverTrips = new ArrayList<>();
        passengerTrips = new ArrayList<>();
        firstListenerDone = false;

        //Initialize Firebase and Listeners
        initFirebaseSetup();
        initListeners();

        return view;
    }

    private void initFirebaseSetup() {

        mDatabase = FirebaseFirestore.getInstance();
        //Create a reference to the trips collection
        mTripsRef = mDatabase.collection("trips");

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

            //Create a query against the collection to find trips where the user is a driver
            final Query driverQuery = mTripsRef.whereEqualTo("driver", userID);

            driverQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        //Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    driverTrips.addAll(queryDocumentSnapshots.toObjects(Trip.class));

                    Collections.sort(driverTrips, new Comparator<Trip>() {
                        @Override
                        public int compare(Trip o1, Trip o2) {
                            return o1.getDate().compareTo(o2.getDate());
                        }
                    });

                    if (getActivity() != null && firstListenerDone) {
                        driverTrips.addAll(passengerTrips);
                        TripListAdapter adapter = new TripListAdapter(getActivity(), driverTrips, userID);
                        tripsListView.setAdapter(adapter);
                    }

                    if (!firstListenerDone) {
                        firstListenerDone = true;
                    }
                }
            });

            //Create a query against the collection to find trips where the user is a passenger
            Query passengerQuery = mTripsRef.whereArrayContains("passengers", userID);

            passengerQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        //Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    passengerTrips.addAll(queryDocumentSnapshots.toObjects(Trip.class));

                    Collections.sort(passengerTrips, new Comparator<Trip>() {
                        @Override
                        public int compare(Trip o1, Trip o2) {
                            return o1.getDate().compareTo(o2.getDate());
                        }
                    });

                    if (getActivity() != null && firstListenerDone) {
                        driverTrips.addAll(passengerTrips);
                        TripListAdapter adapter = new TripListAdapter(getActivity(), driverTrips, userID);
                        tripsListView.setAdapter(adapter);
                    }

                    if (!firstListenerDone) {
                        firstListenerDone = true;
                    }
                }
            });
        }

    }


    private void initListeners() {

        tripsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Trip trip = driverTrips.get(position);
                FirebaseUser activeUser = FirebaseAuth.getInstance().getCurrentUser();
                if (activeUser == null) throw new IllegalStateException("user should be signed in");

                if(trip.getDriver().equals(activeUser.getUid())) {
                    MyTripsFragmentDirections.DriverDetailViewAction action = MyTripsFragmentDirections.driverDetailViewAction();
                    action.setTripId(trip.getTripId());
                    Navigation.findNavController(view).navigate(action);
                } else {
                    MyTripsFragmentDirections.DetailViewAction action = MyTripsFragmentDirections.detailViewAction();
                    action.setTripId(trip.getTripId());
                    Navigation.findNavController(view).navigate(action);
                }
            }
        });

        createTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sign in first if not signed in
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    if (mSignInListener != null) mSignInListener.signIn();
                    return;
                }

                // opens fragment for creating a trip
                Navigation.findNavController(v).navigate(R.id.createTripAction);
            }
        });

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof SignInListener) {
            mSignInListener = (SignInListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SignInListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSignInListener = null;
    }

}
