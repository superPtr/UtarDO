package my.edu.utar.utardo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class ViewEventsPage extends AppCompatActivity {

    private static final String TAG = "ViewEventsPage";
    private RecyclerView recyclerViewEvents;
    private EventAdapter eventsAdapter;
    private TextView courseInfo;
    private ImageView leftBack;
    private String selectedCourseCode, selectedLabel;
    private FirebaseFirestore db;
    private List<Event> eventList = new ArrayList<>();
    private EventAdapter eventAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_events_page);

        db = FirebaseFirestore.getInstance();

        selectedCourseCode = getIntent().getStringExtra("selectedCourseCode");
        selectedLabel = getIntent().getStringExtra("selectedLabel");

        recyclerViewEvents = findViewById(R.id.recyclerViewEvents);
        courseInfo = findViewById(R.id.courseInfo);
        leftBack = findViewById(R.id.leftBack);

        recyclerViewEvents.setLayoutManager(new LinearLayoutManager(this));
        eventsAdapter = new EventAdapter(new ArrayList<>());
        recyclerViewEvents.setAdapter(eventsAdapter);

        leftBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        courseInfo.setText(selectedCourseCode);

        retrieveEvents(selectedCourseCode, selectedLabel);


        //Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.menu_add_button);
        }
        setupBottomNavigation();
    }

    private void retrieveEvents(String selectedCourseCode, String selectedLabel) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userEmail = null;
        if (currentUser != null) {
            userEmail = currentUser.getEmail();

        } else {
            GoogleSignInAccount googleAcc = GoogleSignIn.getLastSignedInAccount(this);
            if (googleAcc != null) {
                userEmail = googleAcc.getEmail();
            }
        }

        if (userEmail != null && selectedLabel != null && selectedCourseCode != null) {
            db.collection("users")
                    .document(userEmail).collection("labels").document(selectedLabel).collection("courses").document(selectedCourseCode).collection("events")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()) {
                                eventList.clear();
                                for(QueryDocumentSnapshot document : task.getResult()) {
                                    String eventName = (String) document.getString("eventName");
                                    String eventDetails = (String) document.getString("eventDetails");
                                    String eventDate = document.getString("eventDate");
                                    Log.d(TAG, "Event added: Name=" + eventName + ", Details=" + eventDetails + ", Date=" + eventDate);
                                    eventList.add(new Event(eventName, eventDetails, eventDate));
                                }
                                eventsAdapter.notifyDataSetChanged();
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Error retrieving events: ", e);
                        }
                    });
        } else {
            Log.e(TAG, "One or more variables are null. Unable to construct document path.");
        }
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
        String[] options = {"Add Event", "Add Task"};

        // Create and configure the dropdown list dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewEventsPage.this);
        builder.setTitle("Select an option");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the selected option
                if (which == 0) {
                    // Redirect to the page for creating a new label
                    startActivity(new Intent(ViewEventsPage.this, AddEvent.class));
                } else if (which == 1) {
                    // Redirect to the page for creating a new course
                    startActivity(new Intent(ViewEventsPage.this, AddTask.class));
                }
            }
        });

        // Display the dropdown list dialog
        builder.show();
    }
}
