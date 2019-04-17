package com.muk.sami;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchTripFragment extends Fragment implements ExampleDialog.ExampleDialogListener {

    private TextView textViewFrom;
    private TextView textViewTo;
    private TextView textViewDate;
    private TextView textViewSeats;
    private TextView textViewTime;
    private Button button;

    private FirebaseDatabase testdatabase;
    private DatabaseReference myRef;

    private ListView listViewTrips;
    private List<Trip> trips;

    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        view  = inflater.inflate(R.layout.fragment_search_trip, container, false);

        FirebaseApp.initializeApp(getContext());
        testdatabase = FirebaseDatabase.getInstance();
        myRef = testdatabase.getReference("trips");

        trips = new ArrayList<>();

        listViewTrips = (ListView) view.findViewById(R.id.listView_Trips);
        textViewFrom = (TextView) view.findViewById(R.id.textview_from);
        textViewTo = (TextView) view.findViewById(R.id.textview_to);
        textViewDate = (TextView) view.findViewById(R.id.textview_date);
        textViewSeats = (TextView) view.findViewById(R.id.textview_seats);
        textViewTime = (TextView) view.findViewById(R.id.textview_time);

        button = (Button) view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        listViewTrips.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Trip trip = trips.get(position);

                textViewFrom.setText(trip.getFrom());
                textViewTo.setText(trip.getTo());
                textViewDate.setText(trip.getDate());
                textViewSeats.setText(trip.getSeats());
                textViewTime.setText(trip.getTime());
            }
        });


        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                trips.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting trip
                    Trip trip = postSnapshot.getValue(Trip.class);
                    //adding trip to the list
                    trips.add(trip);
                }

                TripList adapter = new TripList (getActivity(), trips);
                listViewTrips.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }



    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_search_trip);
        FirebaseApp.initializeApp(this);
        testdatabase = FirebaseDatabase.getInstance();
        myRef = testdatabase.getReference("trips");


        trips = new ArrayList<>();
        listViewTrips = (ListView) findViewById(R.id.listView_Trips);
        textViewFrom = (TextView) findViewById(R.id.textview_from);
        textViewTo = (TextView) findViewById(R.id.textview_to);
        textViewDate = (TextView) findViewById(R.id.textview_date);
        textViewSeats = (TextView) findViewById(R.id.textview_seats);
        textViewTime = (TextView) findViewById(R.id.textview_time);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        listViewTrips.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Trip trip = trips.get(position);

                textViewFrom.setText(trip.getFrom());
                textViewTo.setText(trip.getTo());
                textViewDate.setText(trip.getDate());
                textViewSeats.setText(trip.getSeats());
                textViewTime.setText(trip.getTime());
            }
        });
    }*/

    /*@Override
    protected void onStart() {
        super.onStart();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                trips.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting trip
                    Trip trip = postSnapshot.getValue(Trip.class);
                    //adding trip to the list
                    trips.add(trip);
                }

                TripList adapter = new TripList (SearchTripFragment.this, trips);
                listViewTrips.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/

    public void openDialog() {
        ExampleDialog exampleDialog = new ExampleDialog();
        exampleDialog.show(getChildFragmentManager(), "example");
    }

    /* private void addTrip(){
        String from = textViewFrom.getText().toString();
        String to = textViewTo.getText().toString();
        String date = textViewDate.getText().toString();
        String time = textViewTime.getText().toString();
        String seats = textViewSeats.getText().toString();

        String tripid = myRef.push().getKey();

        Trip trip = new Trip (tripid, from, to, date, time, seats);

        myRef.child(tripid).setValue(trip);

        Toast.makeText(this,"Resa tillagd", Toast.LENGTH_LONG).show();

    } */

    @Override
    public void applyTexts(String from, String to, String date, String seats, String time) {
       /* textViewFrom.setText(from);
        textViewTo.setText(to);
        textViewDate.setText(date);
        textViewSeats.setText(seats);
        textViewTime.setText(time); */

       String tripid = myRef.push().getKey();

        Trip trip = new Trip (tripid, from, to, date, time, seats);

        myRef.child(tripid).setValue(trip);

        Toast.makeText(getContext(),"Resa tillagd", Toast.LENGTH_LONG).show();

    }
}