package my.edu.utar.utardo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ViewTasksPage extends AppCompatActivity {

    private static final String TAG = "ViewTasksPage";

    private RecyclerView recyclerViewTasks;
    private TaskAdapter taskAdapter;
    private List<Task> taskList = new ArrayList<>();

    private ImageView leftBack;
    private TextView courseCodeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tasks_page);

        // Initialize views
        leftBack = findViewById(R.id.leftBack);
        courseCodeName = findViewById(R.id.courseCodeName);
        recyclerViewTasks = findViewById(R.id.recyclerViewTasks);

        // Set up RecyclerView
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(taskList);
        recyclerViewTasks.setAdapter(taskAdapter);

        // Get course code and name passed from previous activity
        String selectedLabel = getIntent().getStringExtra("selectedLabel");
        String selectedCourse = getIntent().getStringExtra("selectedCourseCode");
        String courseName = getIntent().getStringExtra("courseName");
        courseCodeName.setText(selectedCourse);

        // Retrieve tasks related to the course from Firestore
        retrieveTasksFromFirestore(selectedLabel, selectedCourse);

        // Handle back button click
        leftBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        //Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.menu_add_button);
        }
        setupBottomNavigation();
    }

    private void retrieveTasksFromFirestore(String selectedLabel, String selectedCourse) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && selectedLabel != null && selectedCourse != null) {
            String userEmail = currentUser.getEmail();
            db.collection("users")
                    .document(userEmail)
                    .collection("labels")
                    .document(selectedLabel)
                    .collection("courses")
                    .document(selectedCourse)
                    .collection("tasks")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                            if(task.isSuccessful()) {
                                Log.d("BeforeAdapter", "List is " + (taskList == null ? "null" : "not null"));
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
                                //taskAdapter.notifyDataSetChanged();
                                Log.d(TAG, "sizeeeeeeee:" + taskList.size());
                                taskAdapter.setTaskList(taskList);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error retrieving events: ", e);
                    });
        } else {
            Log.e(TAG, "User email, selected label, or selected course is null:"+currentUser+":"+selectedLabel+":"+selectedCourse);
        }
    }



    protected void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.menu_home) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewTasksPage.this);
        builder.setTitle("Select an option");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the selected option
                if (which == 0) {
                    // Redirect to the page for creating a new label
                    startActivity(new Intent(ViewTasksPage.this, AddEvent.class));
                } else if (which == 1) {
                    // Redirect to the page for creating a new course
                    startActivity(new Intent(ViewTasksPage.this, AddTask.class));
                }
            }
        });

        // Display the dropdown list dialog
        builder.show();
    }
}
