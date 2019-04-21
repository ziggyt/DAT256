package com.muk.sami;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity implements MyPageFragment.OnAccountManageListener {

    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 0;

    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.bottom_navigation_bar);
        NavController navController = Navigation.findNavController(findViewById(R.id.nav_host_fragment));
        NavigationUI.setupWithNavController(navigation, navController);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        signIn();
    }

    /**
     * Starts the sign in flow.
     * Checks if there is a FirebaseAuth instance with an already signed in user, and if not,
     * starts a new activity with the sign in screen.
     * <p>
     * If the user is not signed in, screen inputs are disabled to prevent any actions to be
     * made before the sign in screen has a chance to load.
     */
    private void signIn() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) { // Already signed in

        } else { // Not signed in
            // Disable the screen before successful sign in
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            // Start AuthUI's sign in flow
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.GoogleBuilder().build(),
                                    new AuthUI.IdpConfig.EmailBuilder().build()))
                            .setTosAndPrivacyPolicyUrls(
                                    "https://superapp.example.com/terms-of-service.html",
                                    "https://superapp.example.com/privacy-policy.html")
                            .build(),
                    RC_SIGN_IN);
        }
    }

    /**
     * Handles what happens after successful sign in and unsuccessful attempts with errors.
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
                //Re-enable the screen after successful sign in
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            } else { // Sign in failed
                if (response == null) { // User pressed back button
                    //showSnackbar(R.string.sign_in_cancelled);
                    finish();
                    return;
                }

                signIn();

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    //showSnackbar(R.string.no_internet_connection);
                    return;
                }

                //showSnackbar(R.string.unknown_error);
                Log.e(TAG, "Sign-in error: ", response.getError());
            }
        }
    }

    /**
     * Signs out, and on completing the sign out, starts the sign in process again.
     */
    @Override
    public void onSignOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) { // User is now signed out
                        signIn();
                    }
                });
    }

    /**
     * Tries to delete the account.
     * If successful, starts the sign in process again.
     * If unsuccessful, TODO: handle re-authentication
     */
    @Override
    public void onDeleteAccount() {
        AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) { // Deletion succeeded
                            signIn();
                        } else { // Deletion failed
                            // Re-authentication required
                            // You need to have signed in recently to be allowed to delete
                        }
                    }
                });
    }
}
