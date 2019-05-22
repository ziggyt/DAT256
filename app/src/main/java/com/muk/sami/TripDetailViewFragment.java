package com.muk.sami;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.muk.sami.model.SimpleNotification;
import com.muk.sami.model.Trip;
import com.muk.sami.model.User;

import android.content.Context;


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
    private Button finishTripButton;

    private RatingBar driverRatingBar;

    private FirebaseFirestore mDatabase;
    private CollectionReference mNotificationRef;
    private DocumentReference mTripRef;
    private DocumentReference mUserRef;

    private FirebaseUser activeUser;

    private Trip displayedTrip;
    private String tripId;

    private User user;

    private Context context;
    private View view;

    private SignInRequestListener mSignInRequestListener;

    public TripDetailViewFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {

        getActivity().setTitle(R.string.navigation_trip_detailview);
        this.context = getContext();
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
        showQrCodeButton = view.findViewById(R.id.show_ticket);
        finishTripButton = view.findViewById(R.id.finish_trip_button);

        driverRatingBar = view.findViewById(R.id.driver_rating_bar);

        initFirebaseSetup();
        initListeners();

        return view;
    }

    private void showViewForUnbookedUser() {
        bookTripButton.setVisibility(View.VISIBLE);
        cancelTripButton.setVisibility(View.INVISIBLE);
        showQrCodeButton.setVisibility(View.INVISIBLE);
        finishTripButton.setVisibility(View.INVISIBLE);
    }

    private void showViewForBookedUser() {
        bookTripButton.setVisibility(View.INVISIBLE);
        cancelTripButton.setVisibility(View.VISIBLE);
        showQrCodeButton.setVisibility(View.VISIBLE);
        finishTripButton.setVisibility(View.INVISIBLE);
    }

    private void showViewForStartedTrip() {
        bookTripButton.setVisibility(View.INVISIBLE);
        cancelTripButton.setVisibility(View.INVISIBLE);
        showQrCodeButton.setVisibility(View.INVISIBLE);
        finishTripButton.setVisibility(View.VISIBLE);
    }

    private void showViewForFinishedTrip() {
        bookTripButton.setVisibility(View.INVISIBLE);
        cancelTripButton.setVisibility(View.INVISIBLE);
        showQrCodeButton.setVisibility(View.INVISIBLE);
        finishTripButton.setVisibility(View.INVISIBLE);
    }

    private void initFirebaseSetup() {

        mDatabase = FirebaseFirestore.getInstance();
        activeUser = FirebaseAuth.getInstance().getCurrentUser();

        if (activeUser != null) {
            mUserRef = mDatabase.collection("users").document(activeUser.getUid());
            mUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot dsUser = task.getResult();
                    assert dsUser != null;

                    user = dsUser.toObject(User.class);
                }
            });
        }

        //Retrieve the tripId string that was passed along from SearchTripFragment
        tripId = TripDetailViewFragmentArgs.fromBundle(getArguments()).getTripId();

        //Get a reference to the selected trip
        mTripRef = mDatabase.collection("trips").document(tripId);
        mTripRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    //Listen failed
                    return;
                }

                //Convert the snapshot to a trip object
                displayedTrip = documentSnapshot.toObject(Trip.class);
                if (displayedTrip == null) { // Trip doesn't exist
                    tripRemovedDialog();
                    return;
                }

                // Check if the user is a passenger and then if the trip is started or finished
                if (activeUser != null && displayedTrip.userInTrip(activeUser.getUid())) {
                    if (displayedTrip.isTripFinished()) {
                        showViewForFinishedTrip();
                    } else if (displayedTrip.isTripStarted()) {
                        showViewForStartedTrip();
                    } else {
                        showViewForBookedUser();
                    }
                } else {
                    if (displayedTrip.isTripFinished() || displayedTrip.isTripStarted()) {
                        showViewForFinishedTrip();
                    } else {
                        showViewForUnbookedUser();
                        checkIfTripIsFull();
                    }
                }

                //Set the components
                fromTextView.setText(displayedTrip.getStartAddress());
                toTextView.setText(displayedTrip.getDestinationAddress());
                dateTextView.setText(displayedTrip.getDateString());
                timeTextView.setText(displayedTrip.getTimeString());
                totalNumOfSeatsTextView.setText(String.valueOf(displayedTrip.getTotalNumberOfSeats()));
                numOfBookedSeatsTextView.setText(String.valueOf(displayedTrip.getNumberOfBookedSeats()));


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
            }
        });

        //Get the reference to the notifications collection
        mNotificationRef = mDatabase.collection("tripBookingNotification");

    }

    private void checkIfTripIsFull() {
        if (displayedTrip.tripIsFull()) {
            bookTripButton.setAlpha(.5f);
            bookTripButton.setClickable(false);
        } else {
            bookTripButton.setAlpha(1);
            bookTripButton.setClickable(true);
        }
    }

    private void initListeners() {

        bookTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payTripDialog();
            }
        });

        cancelTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeUser == null) throw new IllegalStateException("user should be signed in");
                if (displayedTrip.removePassenger(activeUser.getUid())) {
                    mTripRef.set(displayedTrip);
                    showViewForUnbookedUser();
                    cancelTripMessaging();
                    Toast.makeText(getContext(), R.string.user_removed_from_trip, Toast.LENGTH_SHORT).show();
                }
                checkIfTripIsFull();
            }
        });

        showQrCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TripDetailViewFragmentDirections.ActionTripDetailViewFragmentToActiveTripFragment action = TripDetailViewFragmentDirections.actionTripDetailViewFragmentToActiveTripFragment(displayedTrip.getTripId());
                Navigation.findNavController(view).navigate(action);
            }
        });

        finishTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tripFinishedDialog2();
            }
        });

    }

    private void bookTrip() {
        // Sign in first if not signed in
        if (activeUser == null) {
            if (mSignInRequestListener != null) mSignInRequestListener.onSignInRequest();
            return;
        }
        if (user.getBankCard() == null) { //TODO 'add bankcard' request
            Toast.makeText(getContext(), "Finns inget giltigt bankkort", Toast.LENGTH_SHORT).show();
            return;
        }
        payTripDialog();
    }

    private void payTripDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Betala resan");

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.pay_trip_dialog, (ViewGroup) getView(), false);

        //Set the content of the main dialog view
        builder.setView(dialogView);

        TextView priceTextView = dialogView.findViewById(R.id.price_text_view);

        priceTextView.setText("Din plats kommer att kosta " + String.valueOf(displayedTrip.getSeatPrice()) + " Kr");

        // Set up the OK-button
        builder.setPositiveButton("Betala", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                displayedTrip.addPassenger(activeUser.getUid());
                mTripRef.set(displayedTrip);
                checkIfTripIsFull();
                showViewForBookedUser();
                initTripMessaging();
                sendTripBookedMessage();
                Toast.makeText(getContext(), R.string.user_added_to_trip, Toast.LENGTH_SHORT).show();
            }
        });

        //Set up the Cancel-button
        builder.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void tripRemovedDialog() {
        //Create a dialog and set the title
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.trip_removed);

        builder.setPositiveButton(R.string.ok_button, null);

        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Navigation.findNavController(view).popBackStack();
            }
        });

        builder.show();
    }

    private void initTripMessaging() {
        FirebaseMessaging.getInstance().subscribeToTopic(tripId)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = getString(R.string.msg_subscribed);
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_subscribe_failed);
                        }
                        //Log.d(TAG, msg);
                        //Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void cancelTripMessaging() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(tripId)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = getString(R.string.msg_unsubscribed);
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_unsubscribe_failed);
                        }
                        //Log.d(TAG, msg);
                        //Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                });

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(tripId.hashCode());
    }


    /**
     * Changes the document at tripBookingNotification/tripId, which triggers a cloud function,
     * sending a notification to the driver/passengers telling them that a passenger has joined
     */
    private void sendTripBookedMessage() {
        //The topic name, which equals the tripId
        String topic = tripId;

        //The driverId to send with the message
        String driverId = displayedTrip.getDriver();

        if (activeUser == null) throw new IllegalStateException("user should be signed in");
        //Creates a message to be sent via Firestore Cloud Messaging
        SimpleNotification message = new SimpleNotification("Passenger joined," + activeUser.getUid() + "," + driverId);
        mNotificationRef.document(topic).set(message);

    }

    /**
     * Dialog for the passenger to confirm arrival
     * TODO: Add Rating of driver
     */
    private void tripFinishedDialog() {

        //Create a dialog and set the title/message
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Bekräfta ankomst");
        builder.setMessage("Har du nått din utlovade destination?");

        // Set up the OK-button
        builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { //TODO replace with string value

                if (activeUser == null) throw new IllegalStateException("user should be signed in");

                displayedTrip.finishTripPassenger(activeUser.getUid());
                mTripRef.set(displayedTrip);

                cancelTripMessaging();
            }
        });

        builder.setNegativeButton("Nej", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void tripFinishedDialog2() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Bekräfta ankomst");
        builder.setMessage("Har du nått din utlovade destination?");

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.finish_trip_passenger_dialog, (ViewGroup) getView(), false);

        //Set the content of the main dialog view
        builder.setView(dialogView);

        RatingBar driverRatingBar = dialogView.findViewById(R.id.driver_rating_bar);
        driverRatingBar.setMax(0);
        driverRatingBar.setStepSize(0.5f);

        // Set up the OK-button
        builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (activeUser == null) throw new IllegalStateException("user should be signed in");

                displayedTrip.finishTripPassenger(activeUser.getUid());
                mTripRef.set(displayedTrip);

                cancelTripMessaging();
            }
        });

        //Set up the Cancel-button
        builder.setNegativeButton("Nej", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof SignInRequestListener) {
            mSignInRequestListener = (SignInRequestListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SignInRequestListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSignInRequestListener = null;
    }

}
