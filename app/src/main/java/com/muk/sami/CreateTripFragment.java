package com.muk.sami;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

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

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.muk.sami.model.Coordinates;
import com.muk.sami.model.SimpleNotification;
import com.muk.sami.model.Trip;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class CreateTripFragment extends Fragment {
    private EditText fromEditText;
    private EditText toEditText;
    private TextInputLayout textInputSeats;
    private TextView dateTextView;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private Button addTripButton;

    private FirebaseFirestore mDatabase;
    private CollectionReference mTripsRef;
    private CollectionReference mNotificationRef;

    private Place startPlace;
    private Place destinationPlace;

    private AutocompleteSupportFragment startAutocompleteFragment;
    private AutocompleteSupportFragment destinationAutocompleteFragment;

    private View view;

    private static final String TAG = "CreateTripFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle(R.string.navigation_create_trip);

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_create_trip, container, false);

        fromEditText = view.findViewById(R.id.edit_From);
        toEditText = view.findViewById(R.id.edit_To);
        textInputSeats = view.findViewById(R.id.text_input_seats);
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
                createTrip();
            }
        });


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
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });


        return view;
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

    private void createTrip() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) throw new IllegalStateException("user should be signed in");

        if (!validateInput()) return;

        String tripId = mTripsRef.document().getId();
        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        int seats = Integer.parseInt(textInputSeats.getEditText().getText().toString().trim());
        Date date = dateFromDatePicker(datePicker, timePicker);

        String startAddress = startPlace.getAddress();
        String destinationAddress = destinationPlace.getAddress();

        Coordinates startCoordinates = new Coordinates(startPlace.getLatLng().latitude, startPlace.getLatLng().longitude);
        Coordinates destinationCoordinates = new Coordinates(destinationPlace.getLatLng().latitude, destinationPlace.getLatLng().longitude);

        Trip trip = new Trip(tripId, date, seats, driverId, startCoordinates, destinationCoordinates, startAddress, destinationAddress);

        mTripsRef.document(tripId).set(trip);

        SimpleNotification message = new SimpleNotification("Trip created");
        mNotificationRef.document(tripId).set(message);

        System.out.println(trip.toString());

        Toast.makeText(getContext(), "Resa tillagd", Toast.LENGTH_LONG).show(); //TODO replace with string value

        FirebaseMessaging.getInstance().subscribeToTopic(tripId)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        /*String msg = getString(R.string.msg_subscribed);
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_subscribe_failed);
                        }
                        Log.d(TAG, msg);
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();*/
                    }
                });

        Navigation.findNavController(view).popBackStack();
    }

    private boolean validateInput() {
        String seatsInput = textInputSeats.getEditText().getText().toString().trim();
        if (seatsInput.isEmpty()) {
            textInputSeats.setError("Fältet kan inte vara tomt"); //TODO replace with string value
            return false;
        } else {
            textInputSeats.setError(null);
            return true;
        }
    }

}
