package com.muk.sami;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.muk.sami.model.Trip;
import com.muk.sami.model.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class TripDetailViewFragment extends Fragment {

    private TextView fromTextView;
    private TextView toTextView;
    private TextView dateTextView;
    private TextView timeTextView;
    private TextView totalNumOfSeatsTextView;
    private TextView numOfBookedSeatsTextView;
    private TextView driverTextView;

    private Button bookTripButton;
    private Button cancelTripButton;

    private FirebaseFirestore mDatabase;
    private DocumentReference mTripRef;

    private User activeUser;
    private DocumentReference mUserRef;

    private Trip displayedTrip;

    private View view;

    public TripDetailViewFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_trip_detailview, container, false);

        fromTextView = view.findViewById(R.id.textview_from);
        toTextView = view.findViewById(R.id.textview_to);
        dateTextView = view.findViewById(R.id.textview_date);
        timeTextView = view.findViewById(R.id.textview_time);
        driverTextView = view.findViewById(R.id.driver_text_view);

        totalNumOfSeatsTextView = view.findViewById(R.id.totalNumberOfSeats);
        numOfBookedSeatsTextView = view.findViewById(R.id.numberOfBookedSeats);


        bookTripButton = view.findViewById(R.id.bookTripButton);
        cancelTripButton = view.findViewById(R.id.cancel_trip_btn);

        initFirebaseSetup();
        initListeners();

        return view;
    }

    private void hideBookTripButton() {
        bookTripButton.setVisibility(View.INVISIBLE);
        cancelTripButton.setVisibility(View.VISIBLE);
    }

    private void showBookTripButton() {
        bookTripButton.setVisibility(View.VISIBLE);
        cancelTripButton.setVisibility(View.INVISIBLE);
    }


    private void initFirebaseSetup() {


        // Inflate the layout for this fragment
        mDatabase = FirebaseFirestore.getInstance();


        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mUserRef = mDatabase.document("users/" + userId);
        mUserRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    //Listen failed
                    return;
                }

                //Convert the snapshot to a trip object
                activeUser = documentSnapshot.toObject(User.class);

                //Check if the user is a passenger
                if (displayedTrip != null){
                    if (displayedTrip.userInTrip(activeUser)) {
                        hideBookTripButton();
                    } else {
                        showBookTripButton();
                    }
                }
            }
        });


        //Retrieve the tripId string that was passed along from SearchTripFragment
        String tripId = TripDetailViewFragmentArgs.fromBundle(getArguments()).getTripId();

        //Get a reference to the selected trip
        mTripRef = mDatabase.document("trips/" + tripId);
        mTripRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    //Listen failed
                    return;
                }

                //Convert the snapshot to a trip object
                displayedTrip = documentSnapshot.toObject(Trip.class);

                //Set the components
                if (displayedTrip != null) {
                    fromTextView.setText(displayedTrip.getFrom());
                    toTextView.setText(displayedTrip.getTo());
                    dateTextView.setText(displayedTrip.getDateString());
                    timeTextView.setText(displayedTrip.getTimeString());

                    mDatabase.collection("users").document(displayedTrip.getDriver()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot user = task.getResult();
                                if (user != null) {
                                    driverTextView.setText(user.getString("displayName"));
                                }
                            }
                        }
                    });
                    totalNumOfSeatsTextView.setText(String.valueOf(displayedTrip.getTotalNumberOfSeats()));
                    numOfBookedSeatsTextView.setText(String.valueOf(displayedTrip.getNumberOfBookedSeats()));
                }

                //Check if the user is a passenger
                if (activeUser != null){
                    if (displayedTrip.userInTrip(activeUser)) {
                        hideBookTripButton();
                    } else {
                        showBookTripButton();
                    }
                }
            }
        });

    }

    private void initListeners() {

        bookTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (displayedTrip.addPassenger(activeUser)) {
                    mTripRef.set(displayedTrip);
                    hideBookTripButton();
                    Toast.makeText(getContext(), R.string.user_added_to_trip, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), R.string.trip_full_message, Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (displayedTrip.removePassenger(activeUser)) {
                    mTripRef.set(displayedTrip);
                    showBookTripButton();
                    Toast.makeText(getContext(), R.string.user_removed_from_trip, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
