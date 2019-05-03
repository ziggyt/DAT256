package com.muk.sami;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.muk.sami.model.Trip;

import java.util.List;

public class TripListAdapter extends ArrayAdapter <Trip> {
    private Activity context;
    List<Trip> trips;

    public TripListAdapter(Activity context, List<Trip> trips) {
        super(context, 0, trips);
        this.context = context;
        this.trips = trips;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View listitem = inflater.inflate(R.layout.listitem_trip, null, true);

        TextView textViewFrom  = listitem.findViewById(R.id.textView_From);
        TextView textViewTo    = listitem.findViewById(R.id.textView_To);
        TextView textViewDate  = listitem.findViewById(R.id.textView_Date);
        TextView textViewTime  = listitem.findViewById(R.id.textView_Time);
        TextView textViewSeats = listitem.findViewById(R.id.textView_Seats);

        Trip trip = trips.get(position);

        textViewFrom.setText(trip.getFrom());
        textViewTo.setText(trip.getTo());
        textViewDate.setText(trip.getDateString());
        textViewTime.setText(trip.getTimeString());

        int remainingSeats = trip.getTotalNumberOfSeats() - trip.getNumberOfBookedSeats();
        if(remainingSeats == 0) {
            textViewSeats.setText("Resan är fullbokad");
        } else if(remainingSeats == 1){
            textViewSeats.setText(remainingSeats + " plats kvar");
        } else {
            textViewSeats.setText(remainingSeats + " platser kvar");
        }

        return listitem;
    }

}

