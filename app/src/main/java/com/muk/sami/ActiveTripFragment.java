package com.muk.sami;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;


public class ActiveTripFragment extends Fragment {

    private View view;
    private ImageView qrImageView;
    private String tripId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {

        getActivity().setTitle(R.string.navigation_active_trip);

        view = inflater.inflate(R.layout.fragment_ticket, container, false);

        qrImageView = view.findViewById(R.id.qr_image_view);

        tripId = ActiveTripFragmentArgs.fromBundle(getArguments()).getTripId();

        try {
            Bitmap qrCode = createQrBitmap();
            qrImageView.setImageBitmap(qrCode);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        return view;
    }

    private Bitmap createQrBitmap() throws WriterException {
        String textToEncode = tripId;
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        BitMatrix bitMatrix = multiFormatWriter.encode(textToEncode, BarcodeFormat.QR_CODE, 500, 500);
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        return barcodeEncoder.createBitmap(bitMatrix);
    }

}
