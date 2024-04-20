package my.edu.utar.utardo;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomePage extends AppCompatActivity {

    private RecyclerView recyclerViewEvents;
    private RecyclerView recyclerViewTasks;
    private EventAdapter eventAdapter;
    private TaskAdapter taskAdapter;
    private List<Event> eventList;
    private List<Task> taskList;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page); // Set the correct layout file

        db = FirebaseFirestore.getInstance();
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

        // Initialize RecyclerViews
        recyclerViewEvents = findViewById(R.id.recyclerViewEvents);
        recyclerViewTasks = findViewById(R.id.recyclerViewTasks);

//        // Initialize event list
//        eventList = new ArrayList<>();
//        eventList.add(new Event("Event 1"));
//        eventList.add(new Event("Event 2"));
//        eventList.add(new Event("Event 44"));
//        eventList.add(new Event("Event 42"));
//        eventList.add(new Event("Event 43"));
//        eventList.add(new Event("Event 32"));
//        eventList.add(new Event("Event 443"));
//        eventList.add(new Event("Event 453"));
//
//        // Initialize task list
//        taskList = new ArrayList<>();
//        taskList.add(new Task("Task 1"));
//        taskList.add(new Task("Task 2"));
//        taskList.add(new Task("Task 3"));

        // Initialize adapters
        eventAdapter = new EventAdapter(eventList);
        taskAdapter = new TaskAdapter(taskList);

        // Set layout managers and adapters
        recyclerViewEvents.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));

        Log.d("HomePage", "user email: " + userEmail);
        retrieveTodayEvents(userEmail);

        recyclerViewEvents.setAdapter(eventAdapter);
        recyclerViewTasks.setAdapter(taskAdapter);
    }

    private void retrieveTodayEvents(String userEmail) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String todayDate = sdf.format(new Date());
        ArrayList<String> labelDocumentIds = new ArrayList<>();
        ArrayList<String> courseDocumentIds = new ArrayList<>();
        ArrayList<String> eventDocumentIds = new ArrayList<>();
        Log.d("HomePage", "today date = " + todayDate );

        // using bad way for trying to retrieve
        db.collection("users")
                .document(userEmail).collection("labels")
                .get()
                .addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        task1.getResult().forEach(document -> {
                            Log.d("HomePage", "label id = " + document.getId() );
                            labelDocumentIds.add(document.getId());
                        });

                        for (String labelId : labelDocumentIds) {
                            db.collection("users")
                                    .document(userEmail).collection("labels").document(labelId).collection("courses")
                                    .get()
                                    .addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            task2.getResult().forEach(document -> {
                                                courseDocumentIds.add(document.getId());
                                            });

                                            for (String courseId : courseDocumentIds) {
                                                db.collection("users")
                                                        .document(userEmail).collection("labels").document(labelId).collection("courses").document(courseId).collection("events")
                                                        .get()
                                                        .addOnCompleteListener(task3 -> {
                                                            if (task3.isSuccessful()) {
                                                                task3.getResult().forEach(document -> {
                                                                    eventDocumentIds.add(document.getId());
                                                                });

                                                                eventList.clear();
                                                                for (String eventId : eventDocumentIds) {
                                                                    db.collection("users")
                                                                            .document(userEmail).collection("labels").document(labelId).collection("courses").document(courseId).collection("events").document(eventId)
                                                                            .get()
                                                                            .addOnSuccessListener(documentSnapshot -> {
                                                                                String eventDate = documentSnapshot.getString("eventDate");
                                                                                if (eventDate != null && eventDate.equals(todayDate)) {
                                                                                    String eventName = (String) documentSnapshot.getString("eventName");
                                                                                    String eventDetails = (String) documentSnapshot.getString("eventDetails");
                                                                                    Log.d("HomePage", "Event added: Name=" + eventName + ", Details=" + eventDetails + ", Date=" + eventDate);
                                                                                    eventList.add(new Event(eventName, eventDetails, eventDate));
                                                                                }
                                                                            })
                                                                            .addOnFailureListener(e -> System.err.println("Error fetching event document: " + e.getMessage()));
                                                                }
                                                                eventAdapter.notifyDataSetChanged();
                                                            }
                                                        });
                                            }

                                        }
                                    });
                        }
                    }
                });
    }
}