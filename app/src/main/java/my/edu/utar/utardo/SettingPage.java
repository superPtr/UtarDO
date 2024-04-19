package my.edu.utar.utardo;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingPage extends AppCompatActivity {


    private static final String TAG = "SettingPage";
    FirebaseAuth mAuth;
    Button accSignout;
    TextView usernameTextView, accEmail;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    //usernameTextView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting_page);

        // Find the CardView by its ID
        CardView cardView = findViewById(R.id.cardView);




        // Set padding to handle edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });




        //Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.menu_settings);
        }
        setupBottomNavigation();




        //setting for logout and email
        mAuth = FirebaseAuth.getInstance();
        usernameTextView = findViewById(R.id.usernameTextView);
        accEmail = findViewById(R.id.accEmail);

        //set button for sign in to Google account
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);



        //Check if a user is signed in with Google account
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            // If signed in with Google, display Google account details
            displayGoogleAccountDetails(account);
        } else {
            // If not signed in with Google, check Firebase authentication
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                // If signed in with email/password, display Firebase user details
                displayFirebaseUserDetails(currentUser);
            } else {
                // User is not signed in
                Toast.makeText(this, "No user signed in", Toast.LENGTH_SHORT).show();
            }
        }




        // Set OnClickListener to the CardView
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a dialog window to confirm sign-out
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingPage.this);
                builder.setTitle("Confirm Sign Out");
                builder.setMessage("Are you sure you want to sign out?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Call the signOut method to sign out the user
                        signOut();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User cancelled sign-out, dismiss the dialog
                        dialog.dismiss();
                    }
                });
                // Show the dialog
                builder.show();
            }
        });

    }



        protected void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.menu_home) {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            } else if (item.getItemId() == R.id.menu_label) {
                startActivity(new Intent(this, LabelPage.class));
                return true;
            } else if (item.getItemId() == R.id.menu_add_button) {
                showSpinner();
                return true;
            } else if (item.getItemId() == R.id.menu_calendar) {
                startActivity(new Intent(this, CalendarPage.class));
                return true;
            } else if (item.getItemId() == R.id.menu_settings) {
                startActivity(new Intent(this, SettingPage.class));
                return true;
            }
            return false;
        });
    }


    private void showSpinner() {
        // Create an array of options
        String[] options = {"New label", "New course", "New event", "New task"};

        // Create and configure the dropdown list dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingPage.this);
        builder.setTitle("Select an option");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the selected option
                if (which == 0) {
                    // Redirect to the page for creating a new label
                    startActivity(new Intent(SettingPage.this, LabelPage.class));
                } else if (which == 1) {
                    // Redirect to the page for creating a new course
                    startActivity(new Intent(SettingPage.this, ViewCoursesPage.class));
                } else if (which == 2) {
                    // Redirect to the page for creating a new event
                    startActivity(new Intent(SettingPage.this, ViewEventsPage.class));
                } else if (which == 3) {
                    // Redirect to the page for creating a new task
                    startActivity(new Intent(SettingPage.this, ViewTasksPage.class));
                }
            }
        });

        // Display the dropdown list dialog
        builder.show();
    }



    private void displayFirebaseUserDetails(FirebaseUser user) {
        String personEmail = user.getEmail();

        // Assuming your Firestore collection is named "users"
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(personEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String personName = document.getString("user_name");
                                String greeting = "Hi, " + (personName != null ? personName : "No display name");
                                usernameTextView.setText(greeting);
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "Error getting document: ", task.getException());
                        }
                    }
                });


        //display email
        accEmail.setText(personEmail);
    }

    private void displayGoogleAccountDetails(GoogleSignInAccount account) {
        String personName = account.getDisplayName();
        String personEmail = account.getEmail();
        usernameTextView.setText(personName != null ? personName : "No display name");
        accEmail.setText(personEmail);
    }

    private void signOut() {
        GoogleSignInClient gsc = GoogleSignIn.getClient(this, gso);
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    FirebaseAuth.getInstance().signOut(); // Firebase sign out
                    finish(); // Close the current activity
                    startActivity(new Intent(SettingPage.this, login.class)); // Start login activity
                } else {
                    // Handle sign-out failure
                    Log.e(TAG, "Google sign out failed.", task.getException());

                }
            }
        });
    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // Do nothing or perform any additional actions you want when the back button is pressed

    }
}