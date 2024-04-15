package my.edu.utar.utardo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    TextView accEmail, accName;
    Button accSignout, label, homepage, calendarpage, settingpage;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        accName = findViewById(R.id.accName);
        accEmail = findViewById(R.id.accEmail);
        accSignout = findViewById(R.id.accSignout);

        label = findViewById(R.id.label);

        //set button for sign in to Google account
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);


        mAuth = FirebaseAuth.getInstance();


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

        accSignout.setOnClickListener(v -> signOut());

        label.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LabelPage.class);
            startActivity(intent);
        });



        homepage = findViewById(R.id.homepage);
        calendarpage = findViewById(R.id.calendarpage);
        settingpage = findViewById(R.id.settingpage);



        homepage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the Homepage activity
                Intent intent = new Intent(MainActivity.this, HomePage.class);
                startActivity(intent);
            }
        });

        calendarpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the Homepage activity
                Intent intent = new Intent(MainActivity.this, CalendarPage.class);
                startActivity(intent);
            }
        });

        settingpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the Homepage activity
                Intent intent = new Intent(MainActivity.this, SettingPage.class);
                startActivity(intent);
            }
        });




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
                                accName.setText(personName != null ? personName : "No display name");
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

}
