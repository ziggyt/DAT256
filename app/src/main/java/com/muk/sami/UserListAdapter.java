package com.muk.sami;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.muk.sami.model.User;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

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
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(context));

    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        View listItem = convertView;

        if (listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.user_list_item, parent, false);
        }


        User currentUser = users.get(position);

        final CircleImageView userImage = listItem.findViewById(R.id.profile_picture);

        ImageLoader imageLoader = ImageLoader.getInstance();
        String profilePictureURL = currentUser.getPhotoURL();

        imageLoader.loadImage(profilePictureURL, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                userImage.setImageBitmap(loadedImage);
            }
        });

        TextView username = listItem.findViewById(R.id.user_name_textview);
        username.setText( currentUser.getDisplayName() );

        TextView savedCarb = listItem.findViewById(R.id.user_saved_carbon_textview);
        savedCarb.setText( Integer.toString( currentUser.getSavedCarbon()) + " kg");

        return listItem;
    }
}


