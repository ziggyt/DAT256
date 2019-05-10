package com.muk.sami;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class SearchTripFragment extends Fragment {

    private static final String TAG = "MainActivity";

    private TextView timeTextView;

    private Button addButton;
    private Button filterButton;
    private Button revertFilterButton;

    private FirebaseFirestore mDatabase;
    private CollectionReference mTripsRef;

    private User activeUser;
    private String userID;
    private DocumentReference mUserRef;

    private ListView listViewTrips;
    private List<Trip> trips;
    private List<Trip> filteredTrips;

    private Date filterDate;
    private boolean filterisOn;

    private View view;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search_trip, container, false);

        // Initialize the AutocompleteSupportFragment.
        startAutocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.start_autocomplete_fragment);


        startAutocompleteFragment.setCountry("SE");

        // Specify the types of place data to return.
        startAutocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG, Place.Field.ADDRESS));
        startAutocompleteFragment.setHint("Startplats");


        // Set up a PlaceSelectionListener to handle the response.
        startAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
          
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
                        return o1.getDate().compareTo(o2.getDate());
                    }
                });

                if(filterisOn){
                    applyFilter();
                }else if(getActivity() != null){
                    TripListAdapter adapter = new TripListAdapter(getActivity(), trips, null);
                    listViewTrips.setAdapter(adapter);
                }
            }
        });

        // Inflate the layout for this fragment
        mDatabase = FirebaseFirestore.getInstance();

        //Add listener for login
        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if(user != null){
                    //User is signed in
                    userID = user.getUid();

                    //Retrieve the user
                    mUserRef = mDatabase.document("users/" + userID);
                    mUserRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                //Listen failed
                                return;
                            }

                            //Convert the snapshot to a trip object
                            activeUser = documentSnapshot.toObject(User.class);
                        }
                    });


                }else{
                    //User is not signed in yet
                }
            }
        });

        trips = new ArrayList<>();
        filteredTrips = new ArrayList<>();

        listViewTrips = view.findViewById(R.id.listView_Trips);

        addButton = view.findViewById(R.id.addTripButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // opens fragment for creating a trip
                Navigation.findNavController(v).navigate(R.id.action_searchTripFragment_to_createTripFragment);
            }
        });

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
        return view;
    }
}

