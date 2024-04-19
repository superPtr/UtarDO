package my.edu.utar.utardo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SpecificCoursePage extends AppCompatActivity {

    private static final String TAG = "SpecificCoursePage";
    private FirebaseFirestore db;
    private ImageView leftBack;
    private TextView courseId;
    private TextView title;
    private Button addEventsButton;
    private Button addTasksButton;
    private LinearLayout eventsContainer;
    private LinearLayout tasksContainer;
    private String selectedCourseCode, selectedLabel;
    private RecyclerView recyclerViewEvents;
    private RecyclerView recyclerViewTasks;
    private List<Event> eventList = new ArrayList<>();
    private List<Task> taskList = new ArrayList<>();
    private EventAdapter eventAdapter;
    private TaskAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_course_page);

        db = FirebaseFirestore.getInstance();
        leftBack = findViewById(R.id.leftBack);
        courseId = findViewById(R.id.courseId);
        title = findViewById(R.id.title);
        addEventsButton = findViewById(R.id.addEventsButton);
        addTasksButton = findViewById(R.id.addTasksBtn);
        eventsContainer = findViewById(R.id.eventsContainer);
        tasksContainer = findViewById(R.id.tasksContainer);

        recyclerViewEvents = findViewById(R.id.recyclerViewEvents);
        recyclerViewEvents.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTasks = findViewById(R.id.recyclerViewTasks);
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));

        eventAdapter = new EventAdapter(eventList);
        taskAdapter = new TaskAdapter(taskList);


        // adapter set up

        leftBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        selectedCourseCode = getIntent().getStringExtra("selectedCourseCode");
        selectedLabel = getIntent().getStringExtra("selectedLabel");

        retrieveCourseDetails(selectedCourseCode, selectedLabel);
        retrieveEvents(selectedCourseCode, selectedLabel);
        retrieveTasks(selectedCourseCode, selectedLabel);

        recyclerViewEvents.setAdapter(eventAdapter);
        recyclerViewTasks.setAdapter(taskAdapter);

        // set click listener to add Event buttoon
        addEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add new event
                handleAddEvent(selectedLabel, selectedCourseCode);
            }
        });
        // Set click listener for add Task button
        addTasksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add new task
                handleAddTask(selectedLabel, selectedCourseCode);
            }
        });
    }

    private void retrieveCourseDetails(String selectedCourseCode, String selectedLabel) {
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

        db.collection("users")
                .document(userEmail).collection("labels").document(selectedLabel).collection("courses").document(selectedCourseCode)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String courseName = documentSnapshot.getString("courseName");
                        title.setText(courseName);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error retrieving course details: ", e);
                });
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

        db.collection("users")
                .document(userEmail).collection("labels").document(selectedLabel).collection("courses").document(selectedCourseCode).collection("events")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            eventList.clear();
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                String eventName = (String) document.getString("eventName");
                                String eventDetails = (String) document.getString("eventDetails");
                                String eventDate = document.getString("eventDate");
                                Log.d(TAG, "Event added: Name=" + eventName + ", Details=" + eventDetails + ", Date=" + eventDate);
                                eventList.add(new Event(eventName, eventDetails, eventDate));
                            }
                            eventAdapter.notifyDataSetChanged();
                        }else {
                            Log.d(TAG, "No such document");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error retrieving events: ", e);
                });
    }

    private void retrieveTasks(String selectedCourseCode, String selectedLabel) {

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

        db.collection("users")
                .document(userEmail).collection("labels").document(selectedLabel).collection("courses").document(selectedCourseCode).collection("tasks")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            taskList.clear();
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                String taskTitle = (String) document.getString("taskTitle");
                                String taskDetails = (String) document.getString("taskDetails");
                                String taskStatus = (String) document.getString("taskStatus");
                                String reminderStatus = (String) document.getString("reminderStatus");
                                String startDateString = (String) document.getString("startDate");
                                String endDateString = (String) document.getString("endDate");
                                Log.d(TAG, "Task added: Title=" + taskTitle + ", Details=" + taskDetails + ", Status=" + taskStatus +
                                        ", ReminderStatus=" + reminderStatus + ", StartDate=" + startDateString + ", EndDate=" + endDateString);

                                taskList.add(new Task(taskTitle, taskDetails, taskStatus, reminderStatus, startDateString, endDateString));
                            }
                            Log.d(TAG, "Task list size: " + taskList.size());
                            Log.d(TAG, "notifyDataSetChanged called");
                            taskAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error retrieving events: ", e);
                });
    }

    private void handleAddTask(String labelText, String selectedCourseCode) {
        Intent intent = new Intent(this, AddTask.class);
        intent.putExtra("selectedLabel", labelText);
        intent.putExtra("selectedCourseCode", selectedCourseCode);
        startActivity(intent);
    }

    private void handleAddEvent(String labelText, String selectedCourseCode) {
        Intent intent = new Intent(this, AddEvent.class);
        intent.putExtra("selectedLabel", labelText);
        intent.putExtra("selectedCourseCode", selectedCourseCode);
        startActivity(intent);
    }
}
