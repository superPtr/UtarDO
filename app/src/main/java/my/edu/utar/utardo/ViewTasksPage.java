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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
        String selectedCourse = getIntent().getStringExtra("selectedCourse");
        String courseName = getIntent().getStringExtra("courseName");
        courseCodeName.setText(selectedCourse + " - " + courseName);

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
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            taskList.clear();
                            for (DocumentSnapshot document : task.getResult()) {
                                Task taskItem = document.toObject(Task.class);
                                if ("Pending".equals(taskItem.getTaskStatus())) {
                                    taskList.add(taskItem);
                                }
                            }
                            Collections.sort(taskList, (task1, task2) -> task1.getEndDate().compareTo(task2.getEndDate()));
                            taskAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting tasks: ", task.getException());
                        }
                    });
        } else {
            Log.e(TAG, "User email, selected label, or selected course is null");
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
