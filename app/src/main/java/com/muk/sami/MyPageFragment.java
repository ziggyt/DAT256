package com.muk.sami;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyPageFragment extends Fragment {

    private Button editProfileButton;
    private Button signOutButton;
    private Button deleteAccountButton;

    private View view;

    private OnAccountManageListener mAccountManageListener;

    public MyPageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_page, container, false);

        editProfileButton = view.findViewById(R.id.edit_profile_button);
        signOutButton = view.findViewById(R.id.sign_out_button);
        deleteAccountButton = view.findViewById(R.id.delete_account_button);

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

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnAccountManageListener) {
            mAccountManageListener = (OnAccountManageListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mAccountManageListener = null;
    }

    public interface OnAccountManageListener {
        void onSignOut();
        void onDeleteAccount();
    }

}
