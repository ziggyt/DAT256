package com.muk.sami;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.muk.sami.model.User;

import java.util.List;

public class UserListAdapter extends ArrayAdapter<User> {

    private Activity mContext;
    private List<User> users;



    public UserListAdapter(Activity context, List<User> users) {
        super(context, 0, users);
        this.mContext = context;
        this.users = users;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = mContext.getLayoutInflater();
        View view = inflater.inflate(R.layout.passenger_list_item, null, true);

        return view;
    }
}


