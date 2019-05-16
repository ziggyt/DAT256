package com.muk.sami;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.muk.sami.model.Trip;
import com.muk.sami.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class LeaderboardFragment extends Fragment {

    private FirebaseFirestore mDatabase;
    private CollectionReference mUsersRef;

    private ListView listViewUsers;
    private List<User> users;

    private View view;

    public LeaderboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getActivity().setTitle(R.string.navigation_leaderboard);

        view = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        mDatabase = FirebaseFirestore.getInstance();
        mUsersRef = mDatabase.collection("users");
        mUsersRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                   // Log.w(TAG, "Listen failed.", e);
                    return;
                }

                users.clear();
                users.addAll(queryDocumentSnapshots.toObjects(User.class));



                Collections.sort(users, new Comparator<User>() {
                    @Override
                    public int compare(User o1, User o2) {
                        Integer i1 = o1.getSavedCarbon();
                        Integer i2 = o2.getSavedCarbon();

                        return i1.compareTo(i2);
                    }
                });

                UserListAdapter adapter = new UserListAdapter(getActivity(), users);
                listViewUsers.setAdapter(adapter);


            }
        });


        users = new ArrayList<>();

        listViewUsers = view.findViewById(R.id.listView_Users);


        // Inflate the layout for this fragment
        return view;
    }

}
