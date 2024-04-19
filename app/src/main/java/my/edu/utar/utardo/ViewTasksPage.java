package my.edu.utar.utardo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    }

    private void retrieveTasksFromFirestore(String selectedLabel, String selectedCourse) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userEmail = currentUser != null ? currentUser.getEmail() : null;
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
                            // Assuming "Pending" is the status for not done tasks
                            if ("Pending".equals(taskItem.getTaskStatus())) {
                                taskList.add(taskItem);
                            }
                        }
                        // Sort tasks by endDate, assuming endDate is a String
                        Collections.sort(taskList, new Comparator<Task>() {
                            @Override
                            public int compare(Task task1, Task task2) {
                                return task1.getEndDate().compareTo(task2.getEndDate());
                            }
                        });
                        // Notify adapter about the data change
                        taskAdapter.notifyDataSetChanged();
                    } else {
                        Log.d(TAG, "Error getting tasks: ", task.getException());
                    }
                });
    }
}
