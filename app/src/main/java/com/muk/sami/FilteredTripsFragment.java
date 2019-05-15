package com.muk.sami;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.muk.sami.model.Coordinates;
import com.muk.sami.model.Trip;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;


public class FilteredTripsFragment extends Fragment {

    private static final String TAG = "FilteredTripsFragment";

    private TextView timeTextView;

    private Button filterButton;
    private Button revertFilterButton;

    private FirebaseFirestore mDatabase;
    private CollectionReference mTripsRef;

    private ListView listViewTrips;
    private List<Trip> trips;
    private List<Trip> filteredTrips;

    private Date filterDate;
    private boolean filterOn;

    private String startLocation = "";
    private String destinationLocation = "";

    private Coordinates enteredDestinationCoordinates;


    private View view;

    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {

        getActivity().setTitle(R.string.navigation_filtered_trips);

        view = inflater.inflate(R.layout.fragment_filtered_trips, container, false);

        timeTextView = view.findViewById(R.id.timeTextView);
        filterOn = false;

        double startLatitude = Double.parseDouble(FilteredTripsFragmentArgs.fromBundle(getArguments()).getStartLatitude());
        double startLongitude =  Double.parseDouble(FilteredTripsFragmentArgs.fromBundle(getArguments()).getStartLongitude());

        final Coordinates c = new Coordinates(2.3, 2.5);


        final Coordinates enteredStartCoordinates = new Coordinates(startLatitude, startLongitude);

        mDatabase = FirebaseFirestore.getInstance();
        mTripsRef = mDatabase.collection("trips");
        mTripsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                trips.clear();
                trips.addAll(queryDocumentSnapshots.toObjects(Trip.class));


                Collections.sort(trips, new Comparator<Trip>() {
                    @Override
                    public int compare(Trip o1, Trip o2) {
                        return Double.compare(o1.getDistanceBetweenStartAndCustomCoordinates(enteredStartCoordinates), o2.getDistanceBetweenStartAndCustomCoordinates(enteredStartCoordinates)) ;
                    }
                });

                /*
                Collections.sort(trips, new Comparator<Trip>() {
                    @Override
                    public int compare(Trip o1, Trip o2) {
                        return o1.getDate().compareTo(o2.getDate());
                    }
                });
                */

                if (filterOn) {
                    applyFilter();
                } else if (getActivity() != null) {
                    TripListAdapter adapter = new TripListAdapter(getActivity(), trips, null);
                    listViewTrips.setAdapter(adapter);
                }

            }
        });

        // Inflate the layout for this fragment
        mDatabase = FirebaseFirestore.getInstance();

        trips = new ArrayList<>();
        filteredTrips = new ArrayList<>();

        listViewTrips = view.findViewById(R.id.listView_Trips);


        filterButton = view.findViewById(R.id.filterButton);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Opens the filter dialog
                applyFilter();
            }
        });

        revertFilterButton = view.findViewById(R.id.revertFilterButton);
        revertFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Revert the filtering
                revertFilter();
            }
        });


        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterTripDialog();
            }
        });

        //Visibility for filter buttons
        showFilterButton();

        listViewTrips.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Trip trip = trips.get(position);
                FirebaseUser activeUser = FirebaseAuth.getInstance().getCurrentUser();
                if (activeUser == null) throw new IllegalStateException("user should be signed in");

                if(trip.getDriver().equals(activeUser.getUid())) {
                    FilteredTripsFragmentDirections.DriverDetailViewAction action = FilteredTripsFragmentDirections.driverDetailViewAction();
                    action.setTripId(trip.getTripId());
                    Navigation.findNavController(view).navigate(action);
                } else {
                    FilteredTripsFragmentDirections.DetailViewAction action = FilteredTripsFragmentDirections.detailViewAction();
                    action.setTripId(trip.getTripId());
                    Navigation.findNavController(view).navigate(action);
                }

            }
        });
        return view;
    }

    private void filterTripDialog() {

        //Create a dialog and set the title
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Välj tidigast avgångstid"); //TODO replace with string value

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.filter_trip_dialog, (ViewGroup) getView(), false);

        //Initialize the components
        final DatePicker datePicker = dialogView.findViewById(R.id.datePicker);
        final TimePicker timePicker = dialogView.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        //Set the content of the main dialog view
        builder.setView(dialogView);

        // Set up the OK-button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { //TODO replace with string value
                filterDate = dateFromDatePicker(datePicker, timePicker);
                String dateAndTime = getDateAndTimeString();

                timeTextView.setText("Avgångstid: " + dateAndTime); //TODO replace with string value
                //applyFilter();
            }
        });

        //Set up the Cancel-button
        builder.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { //TODO replace with string value
                // dialog.cancel();
            }
        });

        builder.show();
    }

    private Date dateFromDatePicker(DatePicker p, TimePicker t) {
        int year = p.getYear();
        int month = p.getMonth();
        int day = p.getDayOfMonth();
        int hour = t.getHour();
        int min = t.getMinute();

        Calendar calendar = Calendar.getInstance();

        calendar.set(year, month, day, hour, min);

        return calendar.getTime();
    }

    private void applyFilter() {

        if (filterDate == null) {
            return;
        }

        filteredTrips.clear();

        for (int i = 0; i < trips.size(); i++) {
            if (filterDate.compareTo(trips.get(i).getDate()) <= 0) {
                filteredTrips.add(trips.get(i));
            }
        }

        if (getActivity() != null) {
            TripListAdapter adapter = new TripListAdapter(getActivity(), filteredTrips, null);
            listViewTrips.setAdapter(adapter);
        }

        filterOn = true;

        showRevertFilterButton();

    }

    private void revertFilter() {

        filteredTrips.clear();

        if (getActivity() != null) {
            TripListAdapter adapter = new TripListAdapter(getActivity(), trips, null);
            listViewTrips.setAdapter(adapter);
        }

        filterOn = false;

        showFilterButton();
    }

    private void showFilterButton() {
        filterButton.setVisibility(View.VISIBLE);
        revertFilterButton.setVisibility(View.INVISIBLE);
    }

    private void showRevertFilterButton() {
        filterButton.setVisibility(View.INVISIBLE);
        revertFilterButton.setVisibility(View.VISIBLE);
    }

    public String getDateAndTimeString() {
        SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);
        SimpleDateFormat simpleDateFormatTime = new SimpleDateFormat("HH:mm", Locale.GERMAN);

        String dateAndTime = simpleDateFormatDate.format(filterDate) + "  " + simpleDateFormatTime.format(filterDate);

        return dateAndTime;
    }


}