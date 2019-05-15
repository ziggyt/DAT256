package com.muk.sami;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.muk.sami.model.Coordinates;

import java.util.Arrays;

public class SearchTripFragment extends Fragment {

    private static final String TAG = "MainActivity";

    private Button searchTripButton;

    private View view;

    private Place startPlace;
    private Place destinationPlace;

    private AutocompleteSupportFragment startAutocompleteFragment;
    private AutocompleteSupportFragment destinationAutocompleteFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {

        getActivity().setTitle(R.string.navigation_trip_search);

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

            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        searchTripButton = view.findViewById(R.id.search_trip_button);
        searchTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // opens fragment for filtering trips

                Coordinates startCoordinatesToBePassed = new Coordinates(startPlace.getLatLng().latitude, startPlace.getLatLng().longitude);

                SearchTripFragmentDirections.ActionSearchTripFragmentToFilteredTripsFragment action = SearchTripFragmentDirections.actionSearchTripFragmentToFilteredTripsFragment();
                action.setStartLatitude(Double.toString(startCoordinatesToBePassed.getLat()));
                action.setStartLongitude(Double.toString(startCoordinatesToBePassed.getLon()));

                Navigation.findNavController(v).navigate(action);
            }

        });
        return view;
    }
}

