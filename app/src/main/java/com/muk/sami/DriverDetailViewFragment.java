package com.muk.sami;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.muk.sami.model.BankCard;
import com.muk.sami.model.Trip;
import com.muk.sami.model.User;

import java.util.Calendar;
import java.util.Date;

public class DriverDetailViewFragment extends Fragment {

    private TextView fromTextView;
    private TextView toTextView;
    private TextView dateTextView;
    private TextView timeTextView;

    private Button startTripButton;
    private Button showQrCodeButton;

    private FirebaseFirestore mDatabase;
    private DocumentReference mTripRef;
    private DocumentReference mUserRef;

    private FirebaseUser activeUser;

    private static BroadcastReceiver tickReceiver;

    private Trip displayedTrip;

    View view;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_driver_detail_view, container, false);

        fromTextView = view.findViewById(R.id.from_text_view);
        toTextView = view.findViewById(R.id.to_text_view);
        dateTextView = view.findViewById(R.id.date_text_view);
        timeTextView = view.findViewById(R.id.time_text_view);

        startTripButton = view.findViewById(R.id.start_trip_button);

        showQrCodeButton = view.findViewById(R.id.show_qr_code_button);

        initListeners();
        initFirebaseSetup();

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tickReceiver=new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().compareTo(Intent.ACTION_TIME_TICK)==0) {
                    checkIfPastStartTime();
                }
            }
        };
        getActivity().registerReceiver(tickReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    public void onStop() {
        super.onStop();
        if(tickReceiver!=null)
            getActivity().unregisterReceiver(tickReceiver);
    }


    private void initFirebaseSetup() {

        mDatabase = FirebaseFirestore.getInstance();
        activeUser = FirebaseAuth.getInstance().getCurrentUser();

        //Retrieve the tripId string that was passed along from SearchTripFragment
        String tripId = TripDetailViewFragmentArgs.fromBundle(getArguments()).getTripId();

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

                //Set the components
                if (displayedTrip != null) {
                    fromTextView.setText(displayedTrip.getFrom());
                    toTextView.setText(displayedTrip.getTo());
                    dateTextView.setText(displayedTrip.getDateString());
                    timeTextView.setText(displayedTrip.getTimeString());
                }
            }
        });
    }

    private void initListeners() {
        showQrCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TripDetailViewFragmentDirections.ActionTripDetailViewFragmentToActiveTripFragment action = TripDetailViewFragmentDirections.actionTripDetailViewFragmentToActiveTripFragment(displayedTrip.getTripId());
                Navigation.findNavController(view).navigate(action);
            }
        });
    }

    private void checkIfPastStartTime() {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(new Date());
        cal1.set(Calendar.MILLISECOND, 0);
        cal1.set(Calendar.SECOND, 0);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(displayedTrip.getDate());
        cal2.set(Calendar.MILLISECOND, 0);
        cal2.set(Calendar.SECOND, 0);

        if(cal2.getTime().compareTo(cal1.getTime()) > 0) {
            startTripButton.setBackgroundColor(Color.GRAY);
            startTripButton.setClickable(false);
        } else {
            startTripButton.setBackgroundColor(Color.rgb(2,255,114));
            startTripButton.setClickable(true);
        }
    }

}
