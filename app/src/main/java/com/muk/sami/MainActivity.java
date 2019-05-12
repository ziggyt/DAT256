package com.muk.sami;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity implements MyPageFragment.OnAccountManageListener {

    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 0;

    private FirebaseFirestore mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.bottom_navigation_bar);
        NavController navController = Navigation.findNavController(findViewById(R.id.nav_host_fragment));
        NavigationUI.setupWithNavController(navigation, navController);


        // Initialize Places.
        Places.initialize(getApplicationContext(), getString(R.string.google_places_api_key));

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        mDatabase = FirebaseFirestore.getInstance();
    }

    /**
     * Starts the sign in flow.
     * Checks if there is a FirebaseAuth instance with an already signed in user, and if not,
     * starts a new activity with the sign in screen.
     */
    private void signIn() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) { // Already signed in

        } else { // Not signed in
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
     * <p>
     * Firebase cloud functions will handle adding newly created users to the database, but non
     * OAuth registration methods, like email + password, do not update the displayName before
     * the cloud function gets called even though it is a field you can fill in. So after successful
     * sign in, if it is a new user, manually add the displayName to the database (in
     * {@link #onUserCreation}).
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            recreate();
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) { // Successfully signed in
                //Check if the user is new
                FirebaseUserMetadata metadata = FirebaseAuth.getInstance().getCurrentUser().getMetadata();
                if (metadata.getCreationTimestamp() == metadata.getLastSignInTimestamp()) {
                    onUserCreation();
                }
            } else { // Sign in failed
                // User pressed back button
                if (response == null) return;
                //Network error
                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) return;
                //Unknown error
                Log.e(TAG, "Sign-in error: ", response.getError());
            }
        }
    }

    private void onUserCreation() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("displayName", firebaseUser.getDisplayName());
        mDatabase.collection("users").document(firebaseUser.getUid())
                .set(dataMap, SetOptions.merge());
    }

    /**
     * Signs out from Firebase Auth and starts the sign in process again.
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
     * If unsuccessful, reauthentication is required.
     * <p>
     * To delete an account with Firebase Auth the user needs to have manually signed in recently.
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
                            Snackbar.make(findViewById(android.R.id.content), R.string.deletion_failed_message, Snackbar.LENGTH_LONG)
                                    .setAction(R.string.sign_in, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            onSignOut();
                                        }
                                    })
                                    .show();
                        }
                    }
                });
    }
}
