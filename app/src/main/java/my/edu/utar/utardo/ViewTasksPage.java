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
        String courseCode = getIntent().getStringExtra("courseCode");
        String courseName = getIntent().getStringExtra("courseName");
        courseCodeName.setText(courseCode + " - " + courseName);

        // Retrieve tasks related to the course from Firestore
        retrieveTasksFromFirestore(courseCode);

        // Handle back button click
        leftBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void retrieveTasksFromFirestore(String courseCode) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("courses")
                .document(courseCode)
                .collection("tasks")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        taskList.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            Task taskItem = document.toObject(Task.class);
                            if (!taskItem.isDone()) { // Only add tasks that are not done
                                taskList.add(taskItem);
                            }
                        }
                        // Sort tasks by endDate
                        Collections.sort(taskList, new Comparator<Task>() {
                            @Override
                            public int compare(Task task1, Task task2) {
                                Date endDate1 = task1.getEndDate();
                                Date endDate2 = task2.getEndDate();
                                return endDate1.compareTo(endDate2);
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
