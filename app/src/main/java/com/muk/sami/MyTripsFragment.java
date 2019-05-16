package com.muk.sami;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.muk.sami.model.Trip;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MyTripsFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView tripsListView;
    private FloatingActionButton createTripButton;

    private TripListAdapter adapter;

    private CollectionReference mTripsRef;

    private View view;

    private SignInListener mSignInListener;

    private List<Trip> myTrips = new ArrayList<>();
    private boolean firstTaskDone = false;


    public MyTripsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(R.string.navigation_my_trips);

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_trips, container, false);
        initViews();
        initListeners();

        mTripsRef = FirebaseFirestore.getInstance().collection("trips");
        adapter = new TripListAdapter(getActivity(), myTrips, FirebaseAuth.getInstance().getUid());

        updateTrips();

        return view;
    }

    private void initViews() {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        tripsListView = view.findViewById(R.id.listView_Trips);
        createTripButton = view.findViewById(R.id.createTripButton);
    }


    private void initListeners() {

        tripsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Trip trip = myTrips.get(position);
                FirebaseUser activeUser = FirebaseAuth.getInstance().getCurrentUser();
                if (activeUser == null) throw new IllegalStateException("user should be signed in");

                if (trip.getDriver().equals(activeUser.getUid())) {
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

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateTrips();
            }
        });

    }

    private void updateTrips() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;

        swipeRefreshLayout.setRefreshing(true);
        firstTaskDone = false;
        myTrips.clear();

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //Create a query against the collection to find trips where the user is a driver
        Query driverQuery = mTripsRef.whereEqualTo("driver", userID);
        //Create a query against the collection to find trips where the user is a passenger
        Query passengerQuery = mTripsRef.whereArrayContains("passengers", userID);

        driverQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    myTrips.addAll(task.getResult().toObjects(Trip.class));
                    taskFinished();
                }
            }
        });

        passengerQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    myTrips.addAll(task.getResult().toObjects(Trip.class));
                    taskFinished();
                }
            }
        });

    }

    private void taskFinished() {
        if (!firstTaskDone) {
            firstTaskDone = true;
            return;
        }

        Collections.sort(myTrips, new Comparator<Trip>() {
            @Override
            public int compare(Trip o1, Trip o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });

        tripsListView.setAdapter(adapter);

        swipeRefreshLayout.setRefreshing(false);
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
