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
    private TextView textViewSeats;
    private TextView textViewTime;
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

        //String tripId = getArguments().getString("tripId");
        String tripId = TripDetailViewFragmentArgs.fromBundle(getArguments()).getTripId();

        mDatabase = FirebaseFirestore.getInstance();
        mTripRef = mDatabase.document("trips/" + tripId);
        mTripRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    //Log.w(TAG, "Listen failed.", e);
                    return;
                }

                displayedTrip = documentSnapshot.toObject(Trip.class);

                if(displayedTrip != null){
                    textViewFrom.setText(displayedTrip.getFrom());
                }


            }
        });


        /*final Task<DocumentSnapshot> task = mDatabase.document("trips/" + tripId).get();

        task.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                displayedTrip = documentSnapshot.toObject(Trip.class);
            }
        });*/
        /*
        task.addOnSuccessListener(new OnSuccessListener() {

            @Override
            public void onSuccess(Object o) {

            }

            public void onSuccess(DocumentSnapshot snapshot) {
                // handle the document snapshot here
                displayedTrip = snapshot.toObject(Trip.class);
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            public void onFailure(Exception e) {
                // handle any errors here
            }
        });*/




        //Trip displayedTrip = mDatabase.collection("trips").document(tripId).get().getResult().toObject(Trip.class);
        //DocumentReference docref = mDatabase.collection("trips").document(tripId);
        /*docref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        //Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                        displayedTrip = document.toObject(Trip.class);
                    }else{
                        Log.d("TAG", "Cached get failed" + task.getException());
                    }
                }
            }
        });*/

        //final Trip trip;
        /*docref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
               displayedTrip = documentSnapshot.toObject(Trip.class);
            }
        });*/





        textViewFrom = view.findViewById(R.id.textview_from);
        textViewTo = view.findViewById(R.id.textview_to);
        //textViewDate = view.findViewById(R.id.textview_date);
        //textViewSeats = view.findViewById(R.id.textview_seats);
        //textViewTime = view.findViewById(R.id.textview_time);



        //textViewFrom.setText(displayedTrip.from);



        return view;
    }

}
