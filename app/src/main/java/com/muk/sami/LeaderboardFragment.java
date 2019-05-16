package com.muk.sami;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.muk.sami.model.Trip;
import com.muk.sami.model.User;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class LeaderboardFragment extends Fragment {

    private FirebaseFirestore mDatabase;
    private CollectionReference mTripsRef;

    private ListView listViewTrips;
    private List<User> users;

    private View view;

    public LeaderboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle(R.string.navigation_leaderboard);

        view = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        mDatabase = FirebaseFirestore.getInstance();
        mTripsRef = mDatabase.collection("users");
        // Inflate the layout for this fragment
        return view;
    }

}
