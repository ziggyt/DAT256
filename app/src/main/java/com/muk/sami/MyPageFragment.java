package com.muk.sami;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.muk.sami.model.User;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyPageFragment extends Fragment {

    private Button editProfileButton;
    private Button signOutButton;
    private Button deleteAccountButton;

    private RatingBar userRatingBar;
    private CircleImageView circleImageView;
    private TextView userNameTextView;

    private TextView CO2PointsTextView;

    private String profilePictureUrl = "";

    private View view;

    private FirebaseFirestore mDatabase;
    private User activeUser;
    private DocumentReference mUserRef;

    private OnAccountManageListener mAccountManageListener;
    private SignInRequestListener mSignInRequestListener;

    public MyPageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle(R.string.navigation_my_page);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) { // If not signed in
            view = inflater.inflate(R.layout.sign_in_prompt, container, false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSignInRequestListener != null) mSignInRequestListener.onSignInRequest();
                }
            });
            return view;
        }

        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getActivity()));

        view = inflater.inflate(R.layout.fragment_my_page, container, false);

        userRatingBar = view.findViewById(R.id.user_rating_ratingbar);
        circleImageView = view.findViewById(R.id.profile_picture_circleimageview);
        userNameTextView = view.findViewById(R.id.user_name_textview);
        CO2PointsTextView = view.findViewById(R.id.CO2_points_text_view);


        editProfileButton = view.findViewById(R.id.edit_profile_button);
        signOutButton = view.findViewById(R.id.sign_out_button);
        deleteAccountButton = view.findViewById(R.id.delete_account_button);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase = FirebaseFirestore.getInstance();

        DocumentReference mUserRef = mDatabase.collection("users").document(userId);

        mUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot dsUser = task.getResult();
                assert dsUser != null;

                userNameTextView.setText(dsUser.getString("displayName"));
                userRatingBar.setRating(3);
                CO2PointsTextView.setText(String.valueOf(dsUser.get("savedCarbon")) + "Kg CO2 sparat");

                ImageLoader imageLoader = ImageLoader.getInstance();

                String profilePictureUrl = dsUser.getString("photoURL");

                imageLoader.loadImage(profilePictureUrl, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        circleImageView.setImageBitmap(loadedImage);
                    }
                });
            }
        });


        initListeners();
        return view;
    }

    private void initListeners() {

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.editProfileAction);
            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAccountManageListener != null) {
                    mAccountManageListener.onSignOut();
                }
            }
        });

        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAccountManageListener != null) {
                    mAccountManageListener.onDeleteAccount();
                }
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnAccountManageListener) {
            mAccountManageListener = (OnAccountManageListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAccountManageListener");
        }

        if (context instanceof SignInRequestListener) {
            mSignInRequestListener = (SignInRequestListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SignInRequestListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mAccountManageListener = null;
        mSignInRequestListener = null;
    }

    public interface OnAccountManageListener {
        void onSignOut();

        void onDeleteAccount();
    }

}
