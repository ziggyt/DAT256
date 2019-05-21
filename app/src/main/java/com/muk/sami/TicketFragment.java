package com.muk.sami;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.muk.sami.model.Trip;


public class TicketFragment extends Fragment {

    private View view;

    private TextView startTextview;
    private TextView destinationTextview;
    private TextView dateTimeTextview;
    private TextView priceTextview;
    private TextView passengernameTextView;
    private TextView drivernameTextView;

    private ImageView qrImageView;

    private FirebaseFirestore mDatabase;
    private DocumentReference mTripRef;

    private Trip displayedTrip;
    private String tripId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {

        getActivity().setTitle(R.string.show_ticket);

        view = inflater.inflate(R.layout.fragment_ticket, container, false);

        startTextview = view.findViewById(R.id.startingPoint);
        destinationTextview = view.findViewById(R.id.destination);
        dateTimeTextview = view.findViewById(R.id.dateAndTime);
        priceTextview = view.findViewById(R.id.price);
        drivernameTextView = view.findViewById(R.id.driver);
        passengernameTextView = view.findViewById(R.id.passenger);
        qrImageView = view.findViewById(R.id.qr_image_view);

        tripId = ActiveTripFragmentArgs.fromBundle(getArguments()).getTripId();

        initFirebase();

        try {
            Bitmap qrCode = createQrBitmap();
            qrImageView.setImageBitmap(qrCode);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        return view;
    }

    private void initFirebase(){
        mDatabase = FirebaseFirestore.getInstance();
        //Get a reference to the selected trip
        mTripRef = mDatabase.collection("trips").document(tripId);
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
                startTextview.setText(displayedTrip.getStartAddress());
                destinationTextview.setText(displayedTrip.getDestinationAddress());
                dateTimeTextview.setText(displayedTrip.getDateString() + "   " + displayedTrip.getTimeString());
                priceTextview.setText("Pris: "+ displayedTrip.getSeatPrice() + " kr");

                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                mDatabase.collection("users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot user = task.getResult();
                            if (user != null) {
                                passengernameTextView.setText("Passagerare: " + user.getString("displayName"));
                            }
                        }
                    }
                });

                mDatabase.collection("users").document(displayedTrip.getDriver()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot driver = task.getResult();
                            if (driver != null) {
                                drivernameTextView.setText("Förare: " + driver.getString( "displayName"));
                            }
                        }
                    }
                });


            }
        });



    }

    private Bitmap createQrBitmap() throws WriterException {
        String textToEncode = tripId;
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        BitMatrix bitMatrix = multiFormatWriter.encode(textToEncode, BarcodeFormat.QR_CODE, 500, 500);
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        return barcodeEncoder.createBitmap(bitMatrix);
    }

}
