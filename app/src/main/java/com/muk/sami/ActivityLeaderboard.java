package com.muk.sami;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class ActivityLeaderboard extends AppCompatActivity {


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_trip_search:
                    Intent a = new Intent(ActivityLeaderboard.this,MainActivitySearchTrip.class);
                    startActivity(a);
                    finish();
                    break;
                case R.id.navigation_my_page:
                    Intent b = new Intent(ActivityLeaderboard.this,ActivityMyPage.class);
                    startActivity(b);
                    finish();
                    break;
                case R.id.navigation_leaderboard:
                    break;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        ActivityLeaderboard.this.overridePendingTransition(0,0);

        TextView title = (TextView) findViewById(R.id.leaderboardTitle);
        title.setText(R.string.navigation_leaderboard);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation_bar);
        Menu menu = navigation.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
}
