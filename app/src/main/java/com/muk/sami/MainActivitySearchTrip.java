package com.muk.sami;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivitySearchTrip extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivitySearchTrip.this.overridePendingTransition(0,0);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation_bar);
        Menu menu = navigation.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);



    }

    /*private void initStartingView(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new StartPageFragment())
                .commit();
    }*/

    /**
     * Sets a fragment in the fragment container to be displayed on the screen.
     * @param f The fragment to set as the displayed fragment
     */
    /*private void setFragmentContainerContent(Fragment f){
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_from_top)
                .replace(R.id.fragment_container, f)
                .addToBackStack(null)
                .commit();
    }*/


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_trip_search:
                    break;
                case R.id.navigation_my_page:
                    Intent a = new Intent(MainActivitySearchTrip.this,ActivityMyPage.class);
                    startActivity(a);
                    finish();
                    break;
                case R.id.navigation_leaderboard:
                    Intent b = new Intent(MainActivitySearchTrip.this,ActivityLeaderboard.class);
                    startActivity(b);
                    finish();
                    break;
            }
            return false;
        }
    };

}
