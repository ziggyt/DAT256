package com.muk.sami;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import androidx.navigation.Navigation;

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
    private Button showQrCodeButton;

    private RatingBar driverRatingBar;

    private FirebaseFirestore mDatabase;
    private DocumentReference mTripRef;

    private FirebaseUser activeUser;

    private Trip displayedTrip;

    private View view;

    public TripDetailViewFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_trip_detailview, container, false);

        fromTextView = view.findViewById(R.id.from_text_view);
        toTextView = view.findViewById(R.id.to_text_view);
        dateTextView = view.findViewById(R.id.date_text_view);
        timeTextView = view.findViewById(R.id.time_text_view);
        driverTextView = view.findViewById(R.id.driver_text_view);

        totalNumOfSeatsTextView = view.findViewById(R.id.total_number_of_seats_text_view);
        numOfBookedSeatsTextView = view.findViewById(R.id.number_of_booked_seats_text_view);

        bookTripButton = view.findViewById(R.id.book_trip_button);
        cancelTripButton = view.findViewById(R.id.cancel_trip_button);
        showQrCodeButton = view.findViewById(R.id.show_qr_code_button);

        driverRatingBar = view.findViewById(R.id.driver_rating_bar);

        initFirebaseSetup();
        initListeners();

        return view;
    }

    private void showViewForUnbookedUser() {
        bookTripButton.setVisibility(View.VISIBLE);
        cancelTripButton.setVisibility(View.INVISIBLE);
        showQrCodeButton.setVisibility(View.INVISIBLE);
    }

    private void showViewForBookedUser() {
        bookTripButton.setVisibility(View.INVISIBLE);
        cancelTripButton.setVisibility(View.VISIBLE);
        showQrCodeButton.setVisibility(View.VISIBLE);
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
                        showViewForUnbookedUser();
                    } else {
                        showViewForBookedUser();
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
                                driverRatingBar.setRating(4);
                            }
                        }
                    }
                });
                totalNumOfSeatsTextView.setText(String.valueOf(displayedTrip.getTotalNumberOfSeats()));
                numOfBookedSeatsTextView.setText(String.valueOf(displayedTrip.getNumberOfBookedSeats()));

                //Check if the user is a passenger
                if (activeUser != null){
                    if (displayedTrip.userInTrip(activeUser.getUid())) {
                        showViewForBookedUser();
                    } else {
                        showViewForUnbookedUser();
                    }
                }
            }
            }
        });

    }

    private void initListeners() {

        bookTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeUser == null) return;
                if (displayedTrip.addPassenger(activeUser.getUid())) {
                    mTripRef.set(displayedTrip);
                    showViewForBookedUser();
                    Toast.makeText(getContext(), R.string.user_added_to_trip, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), R.string.trip_full_message, Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeUser == null) return;
                if (displayedTrip.removePassenger(activeUser.getUid())) {
                    mTripRef.set(displayedTrip);
                    showViewForUnbookedUser();
                    Toast.makeText(getContext(), R.string.user_removed_from_trip, Toast.LENGTH_SHORT).show();
                }
            }
        });

        showQrCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TripDetailViewFragmentDirections.ActionTripDetailViewFragmentToActiveTripFragment action = TripDetailViewFragmentDirections.actionTripDetailViewFragmentToActiveTripFragment(displayedTrip.getTripId());
                Navigation.findNavController(view).navigate(action);


            }
        });

    }

}
