package my.edu.utar.utardo;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddTask extends AppCompatActivity {

    private String selectedLabel, selectedCourse;
    private EditText taskTitleInput, detailsInput;
    private Spinner labelSpinner, courseIDSpinner, startDaySpr, startMonthSpr, startYearSpr, endDaySpr, endMonthSpr, endYearSpr;
    private LinearLayout startDateLayout, endDateLayout;
    private Switch reminderSwitch;
    private DocumentReference docRef;
    private CollectionReference colLabelRef, colCourseRef;

    // initialize database
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_task);

        labelSpinner = findViewById(R.id.labelSpinner);
        courseIDSpinner = findViewById(R.id.courseIDSpinner);
        taskTitleInput = findViewById(R.id.taskTitleInput);
        detailsInput = findViewById(R.id.detailsInput);
        reminderSwitch = findViewById(R.id.reminderSwitch);
        startDateLayout = findViewById(R.id.startDateLayout);
        endDateLayout = findViewById(R.id.endDateLayout);

        // Retrieve the selectedLabel & selectedCourse from the previous activity
        selectedLabel = getIntent().getStringExtra("selectedLabel");
        selectedCourse = getIntent().getStringExtra("selectedCourse");


        String userEmail = currentUser != null ? currentUser.getEmail() : null;

        if (userEmail == null) {
            GoogleSignInAccount googleAcc = GoogleSignIn.getLastSignedInAccount(this);
            if (googleAcc != null) {
                userEmail = googleAcc.getEmail();
            }
        }

        if (userEmail != null) {
            colLabelRef = db.collection("users").document(userEmail).collection("labels");
            colCourseRef = db.collection("users").document(userEmail).collection("labels").document(selectedLabel).collection("courses");

        } else {
            Toast.makeText(this, "User email not found, cannot create course.", Toast.LENGTH_LONG).show();
        }

        loadLabel(colLabelRef);
        loadCourseID(colCourseRef);

        setupDateSpinners((Spinner) findViewById(R.id.spinnerStartDay), (Spinner) findViewById(R.id.spinnerStartMonth), (Spinner) findViewById(R.id.spinnerStartYear));
        setupDateSpinners((Spinner) findViewById(R.id.spinnerEndDay), (Spinner) findViewById(R.id.spinnerEndMonth), (Spinner) findViewById(R.id.spinnerEndYear));

        reminderSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            startDateLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            endDateLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });
    }

    // back button
    public void onBackClicked(View view) {
        finish();
    }

    private void setupDateSpinners(Spinner daySpinner, Spinner monthSpinner, Spinner yearSpinner) {
        ArrayAdapter<Integer> dayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, generateNumberList(1, 31));
        ArrayAdapter<Integer> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, generateNumberList(1, 12));
        ArrayAdapter<Integer> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, generateNumberList(1900, Calendar.getInstance().get(Calendar.YEAR)));

        daySpinner.setAdapter(dayAdapter);
        monthSpinner.setAdapter(monthAdapter);
        yearSpinner.setAdapter(yearAdapter);
    }

    private List<Integer> generateNumberList(int start, int end) {
        List<Integer> numbers = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            numbers.add(i);
        }
        return numbers;
    }

    // retrieve label from firestore
    private void loadLabel(CollectionReference colLabelRef) {
        colLabelRef
                .get()
                .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<String> documentIds = new ArrayList<>();
                            QuerySnapshot documents = task.getResult();
                            if (documents != null) {
                                documents.forEach(document -> {
                                    documentIds.add(document.getId());
                                });
                                setLabelSpinner(documentIds);
                            }
                        } else {
                            // Handle the error
                            Log.d("FirestoreError", "Error getting documents: ", task.getException());
                        }
                    });

    }

    // to configure the elements within spinner
    private void setLabelSpinner(List<String> documentIds) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, documentIds);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        labelSpinner.setAdapter(adapter);
    }

    // retrieve course id from firestore
    private void loadCourseID(CollectionReference colCourseRef) {
        colCourseRef
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> courseIds = new ArrayList<>();
                        QuerySnapshot documents = task.getResult();
                        if (documents != null) {
                            documents.forEach(document -> {
                                courseIds.add(document.getId());
                            });
                            setCourseSpinner(courseIds);
                        }
                    } else {
                        // Handle the error
                        Log.d("FirestoreError", "Error getting documents: ", task.getException());
                    }
                });

    }

    // to configure the elements within spinner
    private void setCourseSpinner(List<String> documentIds) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, documentIds);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseIDSpinner.setAdapter(adapter);
    }

    // add btn
    public void addTask(View view) {
        startDaySpr = findViewById(R.id.spinnerStartDay);
        startMonthSpr = findViewById(R.id.spinnerStartMonth);
        startYearSpr = findViewById(R.id.spinnerStartYear);
        endDaySpr = findViewById(R.id.spinnerEndDay);
        endMonthSpr = findViewById(R.id.spinnerEndMonth);
        endYearSpr = findViewById(R.id.spinnerEndYear);

        String labelID = (String) labelSpinner.getSelectedItem();
        String courseID = (String) courseIDSpinner.getSelectedItem();
        String taskTitle = taskTitleInput.getText().toString().trim();
        String details = detailsInput.getText().toString().trim();
        boolean switchState = reminderSwitch.isChecked();

        String startDate = null;
        String endDate = null;
        String flag = "OFF";

        // if reminder is on
        if(switchState){
            String startDay = String.valueOf(startDaySpr.getSelectedItem());
            String startMonth = String.valueOf(startMonthSpr.getSelectedItem());
            String startYear = String.valueOf(startYearSpr.getSelectedItem());
            String endDay = String.valueOf(endDaySpr.getSelectedItem());
            String endMonth = String.valueOf(endMonthSpr.getSelectedItem());
            String endYear = String.valueOf(endYearSpr.getSelectedItem());

            // Concatenate the strings for startDate
            startDate = startDay + "-" + startMonth + "-" + startYear;
            // Concatenate the strings for endDate
            endDate = endDay + "-" + endMonth + "-" + endYear;
            flag = "ON";

        }

        // check whether the field is empty
        if (labelID == null || courseID == null || taskTitle.isEmpty()) {
            // Show an error message or alert to the user
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
        } else {
            // if all fields are filled, then create a Map to store
            Map<String, Object> taskData = new HashMap<>();
            taskData.put("taskTitle", taskTitle);
            taskData.put("taskDetails", details);
            taskData.put("taskStatus", "Pending");
            taskData.put("reminderStatus", flag);
            taskData.put("startDate", startDate);
            taskData.put("endDate", endDate);

            CollectionReference colTask;
            docRef = colCourseRef.document(courseID);
            colTask = docRef.collection("tasks");
            colTask.add(taskData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AddTask.this, "Task created successfully!", Toast.LENGTH_SHORT).show();
                        // Handler to delay starting the Specific Courses Activity
                        new Handler().postDelayed(() -> {
                            // Back to View Course Page
                           Intent intent = new Intent(AddTask.this, ViewTasksPage.class);
                            intent.putExtra("selectedLabel", selectedLabel);
                            intent.putExtra("selectedCourseCode", courseID);
                            startActivity(intent);
                            finish(); // close current activity
                        }, 1000);  // Delay of 1s, enough?

                    })
                    .addOnFailureListener(e -> Toast.makeText(AddTask.this, "Failed to create course: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }

    }
}