package my.edu.utar.utardo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Date;

public class ViewCoursesPage extends BaseActivity {
    private RecyclerView recyclerViewCourses;
    private List<Course> coursesList = new ArrayList<>();
    private CoursesAdapter coursesAdapter;
    private String selectedLabel;
    ImageView backButton;

    TextView labelName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_courses_page);

        TextView labelName = findViewById(R.id.labelName);
        ImageView backButton = findViewById(R.id.leftBack);
        ImageView addCourseBtn = findViewById(R.id.addCourseBtn);

        //handle back button click here
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Set click listener for add course button
        addCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add new course
                handleAddCourse(selectedLabel);
            }
        });

        // Get the selected label from the previous activity
        selectedLabel = getIntent().getStringExtra("selectedLabel");
        labelName.setText(selectedLabel);
        Log.d(TAG, "Selected label: " + selectedLabel);

        recyclerViewCourses.setLayoutManager(new GridLayoutManager(this, 3));

        coursesAdapter = new CoursesAdapter(coursesList);
        recyclerViewCourses.setAdapter(coursesAdapter);

        // Set item click listener
        coursesAdapter.setOnItemClickListener(new CoursesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Course course) {
                // Get the selected course code
                String selectedCourseCode = course.getDocumentId();

                // Start SpecificCoursePage activity with the selected course code
                Intent intent = new Intent(ViewCoursesPage.this, SpecificCoursePage.class);
                intent.putExtra("selectedLabel", selectedLabel);
                intent.putExtra("selectedCourseCode", selectedCourseCode);


                // Read events and tasks data from Firestore
                //readEventsFromFirestore(selectedCourseCode, intent);
                //readTasksFromFirestore(selectedCourseCode, intent);

                startActivity(intent);
            }
        });

        readCoursesFromFirestore();



        //Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.menu_add_button);
        }
        setupBottomNavigation();
    }

    private void readCoursesFromFirestore() {
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
        // In ViewCoursesPageActivity

        readFromFirestore("users", userEmail, "labels/" + selectedLabel + "/courses", new OnReadCompleteListener() {
            @Override
            public void onReadComplete(Map<String, Object> data) {
                coursesList.clear();
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    String courseCode = entry.getKey();
                    Map<String, Object> courseData = (Map<String, Object>) entry.getValue();
                    String courseName = (String) courseData.get("courseName");
                    coursesList.add(new Course(courseCode, courseName));
                }
                coursesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onReadError(Exception e) {
                Log.e(TAG, "Error reading courses from Firestore: " + e.getMessage());
            }
        });
    }

    private void readEventsFromFirestore(String selectedCourseCode, Intent intent) {
        FirebaseFirestore.getInstance().collection("courses")
                .document(selectedCourseCode)
                .collection("events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<Event> eventsList = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        String eventTitle = document.getString("eventName");
                        String eventDetails = document.getString("eventDetails");
                        String eventDate = document.getString("eventDate");
                        Event event = new Event(eventTitle, eventDetails, eventDate);
                        eventsList.add(event);
                    }
                    // Pass events data to the intent
                    intent.putParcelableArrayListExtra("eventsList", eventsList);

                    // Start activity with the intent
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error reading events from Firestore: ", e);
                });
    }

//    private void readTasksFromFirestore(String selectedCourseCode, Intent intent) {
//        FirebaseFirestore.getInstance().collection("courses")
//                .document(selectedCourseCode)
//                .collection("tasks")
//                .get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    ArrayList<Task> tasksList = new ArrayList<>();
//                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
//                        String taskTitle = document.getString("taskTitle");
//                        String taskDetails = document.getString("taskDetails");
//                        boolean taskStatus = document.getBoolean("taskStatus");
//                        Date endDate = document.getDate("endDate");
//                        Task task = new Task(taskTitle, taskDetails, taskStatus, endDate);
//                        tasksList.add(task);
//                    }
//                    // Pass tasks data to the intent
//                    intent.putParcelableArrayListExtra("tasksList", tasksList);
//
//                    // Start activity with the intent
//                    startActivity(intent);
//                })
//                .addOnFailureListener(e -> {
//                    Log.e(TAG, "Error reading tasks from Firestore: ", e);
//                });
//    }


    // Method to handle add course event
    private void handleAddCourse(String labelText) {
        Intent intent = new Intent(this, CreateCourse.class);
        intent.putExtra("selectedLabel", labelText);
        startActivity(intent);
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
        String[] options = {"View label", "View course", "View event", "View task", "New Event", "New Task"};

        // Create and configure the dropdown list dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewCoursesPage.this);
        builder.setTitle("Select an option");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the selected option
                if (which == 0) {
                    // Redirect to the page for creating a new label
                    startActivity(new Intent(ViewCoursesPage.this, LabelPage.class));
                } else if (which == 1) {
                    // Redirect to the page for creating a new course
                    startActivity(new Intent(ViewCoursesPage.this, ViewCoursesPage.class));
                } else if (which == 2) {
                    // Redirect to the page for creating a new event
                    startActivity(new Intent(ViewCoursesPage.this, ViewEventsPage.class));
                } else if (which == 3) {
                    // Redirect to the page for creating a new task
                    startActivity(new Intent(ViewCoursesPage.this, ViewTasksPage.class));
                } else if (which == 4) {
                    // Redirect to the page for creating a new task
                    startActivity(new Intent(ViewCoursesPage.this, AddEvent.class));
                } else if (which == 5) {
                    // Redirect to the page for creating a new task
                    startActivity(new Intent(ViewCoursesPage.this, AddTask.class));
                }
            }
        });

        // Display the dropdown list dialog
        builder.show();
    }
}
