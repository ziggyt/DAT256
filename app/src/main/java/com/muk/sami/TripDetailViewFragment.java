package com.muk.sami;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TripDetailViewFragment extends Fragment {

    private TextView textViewFrom;
    private TextView textViewTo;
    private TextView textViewDate;
    private TextView textViewTime;
    private TextView textViewSeats;
    //private Button button;

    private FirebaseFirestore mDatabase;
    private DocumentReference mTripRef;

    private Trip displayedTrip;

    private View view;

    public TripDetailViewFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,@Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_trip_detailview, container, false);

        //Retrieve the tripId string that was passed along from SearchTripFragment
        String tripId = TripDetailViewFragmentArgs.fromBundle(getArguments()).getTripId();

        //Get the database instance and a reference to the selected trip
        mDatabase = FirebaseFirestore.getInstance();
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
                if(displayedTrip != null){
                    textViewFrom.setText(displayedTrip.getFrom());
                    textViewTo.setText(displayedTrip.getTo());
                    textViewDate.setText(displayedTrip.getDate());
                    textViewTime.setText(displayedTrip.getTime());
                }


            }
        });

        textViewFrom = view.findViewById(R.id.textview_from);
        textViewTo = view.findViewById(R.id.textview_to);
        textViewDate = view.findViewById(R.id.textview_date);
        textViewTime = view.findViewById(R.id.textview_time);
        //textViewSeats = view.findViewById(R.id.textview_seats);


        return view;
    }

}
