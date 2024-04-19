package my.edu.utar.utardo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    TextView accEmail, accName;
    FirebaseAuth mAuth;



    private RecyclerView recyclerViewEvents;
    private RecyclerView recyclerViewTasks;
    private EventAdapter eventAdapter;
    private TaskAdapter taskAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        accName = findViewById(R.id.accName);
        accEmail = findViewById(R.id.accEmail);

        //set button for sign in to Google account
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);


        mAuth = FirebaseAuth.getInstance();



        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.menu_home);
        }

        setupBottomNavigation();




        // Check if a user is signed in with Google account
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



        // Initialize RecyclerViews
        recyclerViewEvents = findViewById(R.id.recyclerViewEvents);
        recyclerViewTasks = findViewById(R.id.recyclerViewTasks);

        // Set layout managers and adapters
        recyclerViewEvents.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewEvents.setAdapter(eventAdapter);
        recyclerViewTasks.setAdapter(taskAdapter);


    }
    private void displayFirebaseUserDetails(FirebaseUser user) {
        String personEmail = user.getEmail();

        // Assuming your Firestore collection is named "users"
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(personEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String personName = document.getString("user_name");
                                String greeting = "Hi, " + (personName != null ? personName : "No display name");
                                accName.setText(greeting);
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
        accName.setText(personName != null ? personName : "No display name");
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
                    startActivity(new Intent(MainActivity.this, login.class)); // Start login activity
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
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Select an option");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the selected option
                if (which == 0) {
                    // Redirect to the page for creating a new label
                    startActivity(new Intent(MainActivity.this, LabelPage.class));
                } else if (which == 1) {
                    // Redirect to the page for creating a new course
                    startActivity(new Intent(MainActivity.this, ViewCoursesPage.class));
                } else if (which == 2) {
                    // Redirect to the page for creating a new event
                    startActivity(new Intent(MainActivity.this, ViewEventsPage.class));
                } else if (which == 3) {
                    // Redirect to the page for creating a new task
                    startActivity(new Intent(MainActivity.this, ViewTasksPage.class));
                }
            }
        });

        // Display the dropdown list dialog
        builder.show();
    }
}
