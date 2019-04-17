package com.muk.sami;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;


public class ExampleDialog extends AppCompatDialogFragment {
    private EditText editTextFrom;
    private EditText editTextTo;
    private EditText editTextSeats;
    private TextView textViewDate;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private ExampleDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);

        builder.setView(view)
                .setTitle("Ny Resa")
                .setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Lägg till", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String from = "Från: " + editTextFrom.getText().toString();
                        String to = "Till: " + editTextTo.getText().toString();
                        String seats = "Antal passagerare: " + editTextSeats.getText().toString();

                        String year = Integer.toString(datePicker.getYear());
                        String month = Integer.toString(datePicker.getMonth()+1);
                        String day = Integer.toString(datePicker.getDayOfMonth());
                        if(Integer.valueOf(month) < 10){

                            month = "0" + month;
                        }
                        if(Integer.valueOf(day) < 10){

                            day  = "0" + day ;
                        }
                        String date = day +"-"+ month + "-" + year;

                        String hour = Integer.toString(timePicker.getHour());
                        String minute = Integer.toString(timePicker.getMinute());
                        if(Integer.valueOf(hour) < 10){

                            hour = "0" + hour;
                        }
                        if(Integer.valueOf(minute) < 10){

                            minute  = "0" + minute ;
                        }
                        String time = hour +":" + minute ;
                        listener.applyTexts(from, to, date, seats, time);
                    }
                });

        editTextFrom = view.findViewById(R.id.edit_From);
        editTextTo = view.findViewById(R.id.edit_To);
        editTextSeats = view.findViewById(R.id.edit_Seats);
        textViewDate = view.findViewById(R.id.textViewDate);
        datePicker = view.findViewById(R.id.datePicker);
        timePicker = view.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (ExampleDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }

    public interface ExampleDialogListener {
        void applyTexts(String from, String to, String date, String seats, String time);
    }
}
