package com.muk.sami;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

public class SearchTripFragment extends Fragment {

    private static final String TAG = "MainActivity";

    private Button addButton;

    private View view;

    private Place startPlace;
    private Place destinationPlace;

    private AutocompleteSupportFragment startAutocompleteFragment;
    private AutocompleteSupportFragment destinationAutocompleteFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search_trip, container, false);


        // Initialize the AutocompleteSupportFragment.
        startAutocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.start_autocomplete_fragment);

        timeTextView = view.findViewById(R.id.timeTextView);


        filterisOn = false;


        startAutocompleteFragment.setCountry("SE");

        // Specify the types of place data to return.
        startAutocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG, Place.Field.ADDRESS));
        startAutocompleteFragment.setHint("Startplats");


        // Set up a PlaceSelectionListener to handle the response.
        startAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                startPlace = place;
                startAutocompleteFragment.setHint(place.getAddress()); // SetText är buggad https://stackoverflow.com/questions/54499335/android-place-autocomplete-fragment-unable-to-set-text

            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        // Initialize the AutocompleteSupportFragment.
        destinationAutocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.destination_autocomplete_fragment);


        destinationAutocompleteFragment.setCountry("SE");
        destinationAutocompleteFragment.setHint("Destination");

        // Specify the types of place data to return.
        destinationAutocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG, Place.Field.ADDRESS));

        // Set up a PlaceSelectionListener to handle the response.
        destinationAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override

            public void onPlaceSelected(Place place) {
                destinationPlace = place;
                destinationAutocompleteFragment.setHint(place.getAddress());  // SetText är buggad https://stackoverflow.com/questions/54499335/android-place-autocomplete-fragment-unable-to-set-text

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Trip trip = trips.get(position);
                FirebaseUser activeUser = FirebaseAuth.getInstance().getCurrentUser();

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

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });


        addButton = view.findViewById(R.id.addTripButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // opens fragment for creating a trip
                Navigation.findNavController(v).navigate(R.id.action_searchTripFragment_to_createTripFragment);
            }
        });

        return view;
    }
}

