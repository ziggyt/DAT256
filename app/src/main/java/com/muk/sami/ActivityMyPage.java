package com.muk.sami;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class ActivityMyPage extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_trip_search:
                    Intent a = new Intent(ActivityMyPage.this,MainActivitySearchTrip.class);
                    startActivity(a);
                    break;
                case R.id.navigation_my_page:
                    break;
                case R.id.navigation_leaderboard:
                    Intent b = new Intent(ActivityMyPage.this,ActivityLeaderboard.class);
                    startActivity(b);
                    break;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);
        ActivityMyPage.this.overridePendingTransition(0,0);

        TextView title = (TextView) findViewById(R.id.myPageTitle);
        title.setText(R.string.navigation_my_page);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation_bar);
        Menu menu = navigation.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

}
