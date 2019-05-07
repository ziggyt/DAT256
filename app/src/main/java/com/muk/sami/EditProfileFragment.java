package com.muk.sami;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends Fragment {

    private Button manageBankCardButton;

    private View view;


    public EditProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_edit_profile, container, false);


        manageBankCardButton = view.findViewById(R.id.manage_bank_card_button);
        manageBankCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageCardDialog();
            }
        });
        return view;
    }


    private void manageCardDialog() {

        //Create a dialog and set the title
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Hantera kort");

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.manage_bank_card_dialog, (ViewGroup) getView(), false);

        EditText cardNumberEditText = dialogView.findViewById(R.id.card_number_edit_text);
        EditText cardDateEditText = dialogView.findViewById(R.id.card_date_edit_text);
        EditText cardCVCEditText = dialogView.findViewById(R.id.card_cvc_edit_text);


        //Set the content of the main dialog view
        builder.setView(dialogView);

        // Set up the OK-button
        builder.setPositiveButton("Lägg till", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

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

}
