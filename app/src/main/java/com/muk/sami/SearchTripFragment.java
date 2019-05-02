package com.muk.sami;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.muk.sami.model.Trip;
import com.muk.sami.model.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class SearchTripFragment extends Fragment {

    private static final String TAG = "MainActivity";

    private Button button;

    private FirebaseFirestore mDatabase;
    private CollectionReference mTripsRef;

    private User activeUser;
    private DocumentReference mUserRef;

    private ListView listViewTrips;
    private List<Trip> trips;

    private View view;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search_trip, container, false);

        mDatabase = FirebaseFirestore.getInstance();
        mTripsRef = mDatabase.collection("trips");
        mTripsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                trips.clear();
                trips.addAll(queryDocumentSnapshots.toObjects(Trip.class));

                if (getActivity() != null) {
                    TripListAdapter adapter = new TripListAdapter(getActivity(), trips);
                    listViewTrips.setAdapter(adapter);
                }
            }
        });

        // Inflate the layout for this fragment
        mDatabase = FirebaseFirestore.getInstance();

        //Add listener for login
        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if(user != null){
                    //User is signed in
                    String userId = user.getUid();

                    //Retrieve the user
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
                        }
                    });


                }else{
                    //User is not signed in yet
                }
            }
        });


        trips = new ArrayList<>();

        listViewTrips = view.findViewById(R.id.listView_Trips);

        button = view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Opens the tripCreator-dialog
                createTripDialog();
            }
        });

        listViewTrips.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Trip trip = trips.get(position);

                SearchTripFragmentDirections.DetailViewAction action = SearchTripFragmentDirections.detailViewAction();
                action.setTripId(trip.getTripId());
                Navigation.findNavController(view).navigate(action);


            }
        });


        return view;
    }

    private void createTripDialog() {
        //Create a dialog and set the title
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Ny resa"); //TODO replace with string value

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.layout_dialog, (ViewGroup) getView(), false);

        //Initialize the components
        final EditText fromEditText = dialogView.findViewById(R.id.edit_From);
        final EditText toEditText = dialogView.findViewById(R.id.edit_To);
        final EditText seatsEditText = dialogView.findViewById(R.id.edit_Seats);
        final TextView dateTextView = dialogView.findViewById(R.id.textViewDate);
        final DatePicker datePicker = dialogView.findViewById(R.id.datePicker);
        final TimePicker timePicker = dialogView.findViewById(R.id.timePicker);

        //Set the content of the main dialog view
        builder.setView(dialogView);


        // Set up the OK-button
        builder.setPositiveButton("Lägg till", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { //TODO replace with string value
                String from = fromEditText.getText().toString();
                String to = toEditText.getText().toString();
                String seats = seatsEditText.getText().toString();
                Date date = dateFromDatePicker(datePicker, timePicker);

                createTrip(from, to, date, Integer.parseInt(seats));
                dialog.cancel();
            }
        });

        //Set up the Cancel-button
        builder.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { //TODO replace with string value
                dialog.cancel();
            }
        });

        builder.show();

    }

    private Date dateFromDatePicker(DatePicker p, TimePicker t){
        int year = p.getYear();
        int month = p.getMonth();
        int day = p.getDayOfMonth();
        int hour = t.getHour();
        int min = t.getMinute();

        Calendar calendar = Calendar.getInstance();

        calendar.set(year, month, day, hour, min);

        return calendar.getTime();
    }

    private void createTrip(String from, String to, Date date, int seats) {

        String tripId = mTripsRef.document().getId();
        Trip trip = new Trip(tripId, from, to, date,0 , seats, activeUser);
        mTripsRef.document(tripId).set(trip);

        Toast.makeText(getContext(), "Resa tillagd", Toast.LENGTH_LONG).show(); //TODO replace with string value
    }

}