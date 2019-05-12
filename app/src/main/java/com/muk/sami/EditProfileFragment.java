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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.muk.sami.model.BankCard;
import com.muk.sami.model.User;

import javax.annotation.Nullable;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends Fragment {

    private Button manageBankCardButton;

    private FirebaseFirestore mDatabase;
    private DocumentReference mUserRef;

    private View view;

    private String userId;
    private User user;

    public EditProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_edit_profile, container, false);
        mDatabase = FirebaseFirestore.getInstance();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) throw new IllegalStateException("user should be signed in");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Get a document reference for the active User

        mUserRef = mDatabase.collection("users").document(userId);

        mUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot dsUser = task.getResult();
                assert dsUser != null;

                user = dsUser.toObject(User.class);
            }
        });

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

        final EditText cardNumberEditText = dialogView.findViewById(R.id.card_number_edit_text);
        final EditText cardYearEditText = dialogView.findViewById(R.id.card_year_edit_text);
        final EditText cardMonthEditText = dialogView.findViewById(R.id.card_month_edit_text);
        final EditText cardCVCEditText = dialogView.findViewById(R.id.card_cvc_edit_text);


        //Set the content of the main dialog view
        builder.setView(dialogView);

        // Set up the OK-button
        builder.setPositiveButton("Lägg till", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Create a bankcard from fields
                String cardNumber = cardNumberEditText.getText().toString();
                String cardYear = cardYearEditText.getText().toString();
                String cardMonth = cardMonthEditText.getText().toString();
                String cardCvc = cardCVCEditText.getText().toString();
                BankCard bankCard = new BankCard(cardNumber, cardYear, cardMonth, cardCvc);
                user.setBankCard(bankCard);

                mDatabase.collection("users").document(userId).set(user);
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
