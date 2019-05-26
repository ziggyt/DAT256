package com.muk.sami;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SearchTripFragment extends Fragment {

    private static final String TAG = "MainActivity";

    private TextView timeTextview;
    private Button searchTripButton;

    private View view;

    private Place startPlace;
    private Place destinationPlace;
    private Date searchDate;

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

        timeTextview = view.findViewById(R.id.time_textview);
        timeTextview.setText("Välj avgångstid");
        timeTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterTripDialog();
            }
        });

        searchTripButton = view.findViewById(R.id.search_trip_button);
        searchTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // opens fragment for filtering trips
                if (startPlace == null) {
                    Toast.makeText(getContext(), "Du måste välja startpunkt!", Toast.LENGTH_SHORT).show();
                    return;
                }


                Coordinates startCoordinatesToBePassed = new Coordinates(startPlace.getLatLng().latitude, startPlace.getLatLng().longitude);

                Coordinates destinationCoordinatesToBePassed = null;
                if(destinationPlace != null) {
                    destinationCoordinatesToBePassed = new Coordinates(destinationPlace.getLatLng().latitude, destinationPlace.getLatLng().longitude);
                }
                if (searchDate == null) {
                    searchDate = new Date();
                }

                SearchTripFragmentDirections.ActionSearchTripFragmentToFilteredTripsFragment action = SearchTripFragmentDirections.actionSearchTripFragmentToFilteredTripsFragment();
                action.setStartLatitude(Double.toString(startCoordinatesToBePassed.getLat()));
                action.setStartLongitude(Double.toString(startCoordinatesToBePassed.getLon()));

                if(destinationCoordinatesToBePassed != null) {
                    action.setDestinationLatitude(Double.toString(destinationCoordinatesToBePassed.getLat()));
                    action.setDestinationLongitude(Double.toString(destinationCoordinatesToBePassed.getLon()));
                }

                action.setDateString(searchDate.toString());

                Navigation.findNavController(v).navigate(action);
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
                searchDate = dateFromDatePicker(datePicker, timePicker);
                String dateAndTime = getDateAndTimeString();

                timeTextview.setText("Avgångstid: " + dateAndTime); //TODO replace with string value
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

    public String getDateAndTimeString() {
        SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);
        SimpleDateFormat simpleDateFormatTime = new SimpleDateFormat("HH:mm", Locale.GERMAN);

        String dateAndTime = simpleDateFormatDate.format(searchDate) + "  " + simpleDateFormatTime.format(searchDate);

        return dateAndTime;
    }
}

