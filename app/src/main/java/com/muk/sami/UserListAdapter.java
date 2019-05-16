package com.muk.sami;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.muk.sami.model.User;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

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
        View view = inflater.inflate(R.layout.user_list_item, null, true);

        User currentUser = users.get(position);

        //CircleImageView userImage = view.findViewById(R.id.profile_picture);
        //userImage.setImageBitmap();

        TextView username = view.findViewById(R.id.user_name_textview);
        username.setText( currentUser.getDisplayName() );

        TextView savedCarb = view.findViewById(R.id.user_saved_carbon_textview);
        savedCarb.setText( Integer.toString( currentUser.getSavedCarbon()) + " kg");

        return view;
    }
}


