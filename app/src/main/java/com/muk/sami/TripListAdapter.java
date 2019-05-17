package com.muk.sami;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.muk.sami.model.Trip;

import java.util.List;

public class TripListAdapter extends ArrayAdapter <Trip> {
    private Activity context;
    List<Trip> trips;
    private String userID;

    public TripListAdapter(Activity context, List<Trip> trips, String userID) {
        super(context, 0, trips);
        this.context = context;
        this.trips = trips;
        this.userID = userID;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        View listItem = convertView;

        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.listitem_trip, parent, false);
        }


        ImageView driverOrNot  = listItem.findViewById(R.id.driverOrNot);
        TextView textViewFrom  = listItem.findViewById(R.id.from_text_view);
        TextView textViewTo    = listItem.findViewById(R.id.to_text_view);
        TextView textViewDate  = listItem.findViewById(R.id.date_text_view);
        TextView textViewTime  = listItem.findViewById(R.id.time_text_view);
        TextView textViewSeats = listItem.findViewById(R.id.seats_text_view);
        TextView tripLengthTextView = listItem.findViewById(R.id.trip_length_text_view);

        Trip trip = trips.get(position);

        if( trip.getDriver().equals(userID) ){
            //If the user is the driver of this trip, set the image to a car
            driverOrNot.setImageResource(R.drawable.ic_directions_car_black_24dp);
        }else if( userID != null ) {
            //If the person is a passenger, set the image to passengers
            driverOrNot.setImageResource(R.drawable.ic_passengers_black_24dp);
        }

        textViewFrom.setText(trip.getStartAddress());
        textViewTo.setText(trip.getDestinationAddress());
        textViewDate.setText(trip.getDateString());
        textViewTime.setText(trip.getTimeString());
        tripLengthTextView.setText(Math.round(trip.getDistanceBetweenStartAndDestination()/1000) + "km");

        int remainingSeats = trip.getTotalNumberOfSeats() - trip.getNumberOfBookedSeats();
        if(remainingSeats == 0) {
            textViewSeats.setText("Resan är fullbokad");
        } else if(remainingSeats == 1){
            textViewSeats.setText(remainingSeats + " plats kvar");
        } else {
            textViewSeats.setText(remainingSeats + " platser kvar");
        }

        return listItem;
    }

}

