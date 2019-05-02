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

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.muk.sami.model.Trip;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class SearchTripFragment extends Fragment {

    private static final String TAG = "MainActivity";

    private TextView textViewFrom;
    private TextView textViewTo;
    private TextView textViewDate;
    private TextView textViewSeats;
    private TextView textViewTime;
    private Button button;

    private FirebaseFirestore mDatabase;
    private CollectionReference mTripsRef;

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

        trips = new ArrayList<>();

        listViewTrips = view.findViewById(R.id.listView_Trips);
        textViewFrom = view.findViewById(R.id.textview_from);
        textViewTo = view.findViewById(R.id.textview_to);
        textViewDate = view.findViewById(R.id.textview_date);
        textViewSeats = view.findViewById(R.id.textview_seats);
        textViewTime = view.findViewById(R.id.textview_time);

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

                /*textViewFrom.setText(trip.getFrom());
                textViewTo.setText(trip.getTo());
                textViewDate.setText(trip.getDate());
                textViewSeats.setText(trip.getSeats());
                textViewTime.setText(trip.getTime());*/
            }
        });


        return view;
    }

    private void createTripDialog() {
        //Create a dialog and set the title
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Ny resa");

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.layout_dialog, (ViewGroup) getView(), false);

        //Initialize the components
        final EditText editTextFrom = dialogView.findViewById(R.id.edit_From);
        final EditText editTextTo = dialogView.findViewById(R.id.edit_To);
        final EditText editTextSeats = dialogView.findViewById(R.id.edit_Seats);
        final TextView textViewDate = dialogView.findViewById(R.id.textViewDate);
        final DatePicker datePicker = dialogView.findViewById(R.id.datePicker);
        final TimePicker timePicker = dialogView.findViewById(R.id.timePicker);

        //Set the content of the main dialog view
        builder.setView(dialogView);

        // Set up the OK-button
        builder.setPositiveButton("Lägg till", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String from = editTextFrom.getText().toString();
                String to = editTextTo.getText().toString();
                String seats = editTextSeats.getText().toString();

                String year = Integer.toString(datePicker.getYear());
                String month = Integer.toString(datePicker.getMonth() + 1);
                String day = Integer.toString(datePicker.getDayOfMonth());
                if (Integer.valueOf(month) < 10) {

                    month = "0" + month;
                }
                if (Integer.valueOf(day) < 10) {

                    day = "0" + day;
                }
                String date = day + "-" + month + "-" + year;

                String hour = Integer.toString(timePicker.getHour());
                String minute = Integer.toString(timePicker.getMinute());
                if (Integer.valueOf(hour) < 10) {

                    hour = "0" + hour;
                }
                if (Integer.valueOf(minute) < 10) {

                    minute = "0" + minute;
                }
                String time = hour + ":" + minute;
                createTrip(from, to, date, Integer.parseInt(seats), time);
                dialog.cancel();
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

    private void createTrip(String from, String to, String date, int seats, String time) {
        /* textViewFrom.setText(from);
        textViewTo.setText(to);
        textViewDate.setText(date);
        textViewSeats.setText(seats);
        textViewTime.setText(time); */

        String tripId = mTripsRef.document().getId();
        Trip trip = new Trip(tripId, from, to, date, time, seats);
        mTripsRef.document(tripId).set(trip);

        Toast.makeText(getContext(), "Resa tillagd", Toast.LENGTH_LONG).show();
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

}