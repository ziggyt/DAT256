package com.muk.sami;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.muk.sami.model.SimpleNotification;
import com.muk.sami.model.Trip;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DriverDetailViewFragment extends Fragment {

    private TextView fromTextView;
    private TextView toTextView;
    private TextView dateTextView;
    private TextView timeTextView;

    private Button startTripButton;
    private Button finishTripButton;
    private Button showQrCodeButton;

    private ListView passengerListView;

    private PassengerListAdapter adapter;

    private FirebaseFirestore mDatabase;
    private DocumentReference mTripRef;
    private CollectionReference mNotificationRef;

    private List<String> passengerList;
    private List<String> passengersStatus;

    private String userId;

    private static BroadcastReceiver tickReceiver;

    private Trip displayedTrip;
    private String tripId;

    private Context context;
    private View view;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {
                    checkIfPastStartTime();
                }
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getActivity().setTitle(R.string.navigation_trip_detailview);
        this.context = getContext();
        view = inflater.inflate(R.layout.fragment_driver_detail_view, container, false);

        fromTextView = view.findViewById(R.id.from_text_view);
        toTextView = view.findViewById(R.id.to_text_view);
        dateTextView = view.findViewById(R.id.date_text_view);
        timeTextView = view.findViewById(R.id.time_text_view);
        startTripButton = view.findViewById(R.id.start_trip_button);
        finishTripButton = view.findViewById(R.id.finish_trip_button);
        showQrCodeButton = view.findViewById(R.id.show_qr_code_button);
        passengerListView = view.findViewById(R.id.passengers_list_view);

        passengerList = new ArrayList<>();
        passengersStatus = new ArrayList<>();
        adapter = new PassengerListAdapter(getActivity(), passengerList, passengersStatus);
        passengerListView.setAdapter(adapter);

        initListeners();
        initFirebaseSetup();

        return view;
    }

    private void initFirebaseSetup() {

        mDatabase = FirebaseFirestore.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() == null)
            throw new IllegalStateException("user should be signed in");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

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

                if (displayedTrip.isTripFinished()) {
                    showViewForFinishedTrip();
                } else if (displayedTrip.isTripStarted()) {
                    showViewForStartedTrip();

                    if (displayedTrip.passengersFinishedTrip()) {
                        finishTrip();
                    }
                }

                //Set the components
                fromTextView.setText(displayedTrip.getStartAddress());
                toTextView.setText(displayedTrip.getDestinationAddress());
                dateTextView.setText(displayedTrip.getDateString());
                timeTextView.setText(displayedTrip.getTimeString());

                passengerList.clear();
                passengersStatus.clear();
                passengersStatus.addAll(displayedTrip.getPassengerStatus());

                createPassengerList(displayedTrip);
            }
        });

        //Get the reference to the notifications collection
        mNotificationRef = mDatabase.collection("tripBookingNotification");
    }

    private void createPassengerList(Trip trip) {
        for (String passenger : trip.getPassengers()) {
            DocumentReference mUserRef = mDatabase.collection("users").document(passenger);
            mUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot dsUser = task.getResult();
                    assert dsUser != null;
                    passengerList.add(dsUser.getString("displayName"));
                    passengerListView.setAdapter(adapter);
                }
            });
        }
    }

    private void initListeners() {
        showQrCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TripDetailViewFragmentDirections.ActionTripDetailViewFragmentToActiveTripFragment action = TripDetailViewFragmentDirections.actionTripDetailViewFragmentToActiveTripFragment(displayedTrip.getTripId());
                Navigation.findNavController(view).navigate(action);
            }
        });

        startTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTrip();
            }
        });

        finishTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishTrip();
            }
        });
    }

    private void startTrip() {
        displayedTrip.startTrip();
        mTripRef.set(displayedTrip);

        showViewForStartedTrip();
        sendTripStartedMessage();
    }

    private void finishTrip() {
        displayedTrip.finishTrip();
        mTripRef.set(displayedTrip);

        tripFinishedDialog();
        showViewForFinishedTrip();
        cancelTripMessaging();
    }

    private void showViewForStartedTrip() {
        startTripButton.setVisibility(View.INVISIBLE);
        finishTripButton.setVisibility(View.VISIBLE);
    }

    private void showViewForFinishedTrip() {
        startTripButton.setVisibility(View.INVISIBLE);
        finishTripButton.setVisibility(View.GONE);
    }

    private void cancelTripMessaging() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(tripId);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(tripId.hashCode());
    }

    /**
     * Changes the document at tripBookingNotification/tripId, which triggers a cloud function,
     * sending a notification to the passengers confirming that the trip has started
     */
    private void sendTripStartedMessage() {
        //The topic name, which equals the tripId
        String topic = tripId;

        //The driverId to send with the message
        String driverId = displayedTrip.getDriver();


        //Creates a message to be sent via Firestore Cloud Messaging
        SimpleNotification message = new SimpleNotification("Trip started," + userId + "," + driverId);
        mNotificationRef.document(topic).set(message);

    }

    private void checkIfPastStartTime() {
        if (displayedTrip.isTripStarted()) {
            return;
        }

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(new Date());
        cal1.set(Calendar.MILLISECOND, 0);
        cal1.set(Calendar.SECOND, 0);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(displayedTrip.getDate());
        cal2.set(Calendar.MILLISECOND, 0);
        cal2.set(Calendar.SECOND, 0);

        if (cal2.getTime().compareTo(cal1.getTime()) > 0) {
            startTripButton.setBackgroundColor(Color.GRAY);
            startTripButton.setClickable(false);
        } else {
            startTripButton.setBackgroundColor(Color.rgb(2, 255, 114));
            startTripButton.setClickable(true);
        }
    }

    /**
     * Temporary dialog, this confirmation that the trip is finished could be placed in fragment
     */
    private void tripFinishedDialog() {

        //Create a dialog and set the title/message
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Resan avslutad");
        builder.setMessage("Alla passagerare har fullföljt sin resa. Du får snart din betalning");

        // Set up the OK-button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { //TODO replace with string value
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void registerTickReceiver() {
        getActivity().registerReceiver(tickReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    private void unRegisterTickReceiver() {
        getActivity().unregisterReceiver(tickReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        registerTickReceiver();
    }

    @Override
    public void onPause() {
        super.onPause();
        unRegisterTickReceiver();
    }

}
