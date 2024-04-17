package my.edu.utar.utardo;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class CreateCourse extends AppCompatActivity {

    private String selectedLabel;
    private EditText courseCodeInput, courseNameInput, lecturerNameInput;
    private Spinner lectureGroupSpinner, tutorialGroupSpinner;
    private DocumentReference docRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course);

        courseCodeInput = findViewById(R.id.courseCodeInput);
        courseNameInput = findViewById(R.id.courseNameInput);
        lecturerNameInput = findViewById(R.id.lecturerNameInput);
        lectureGroupSpinner = findViewById(R.id.lectureGroupSpinner);
        tutorialGroupSpinner = findViewById(R.id.tutorialGroupSpinner);

        // Configure spinners
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, generateNumbers(1, 30));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lectureGroupSpinner.setAdapter(adapter);
        tutorialGroupSpinner.setAdapter(adapter);

        // Retrieve the selectedLabel from the previous activity
        selectedLabel = getIntent().getStringExtra("selectedLabel");

        // initialize database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userEmail = currentUser != null ? currentUser.getEmail() : null;

        if (userEmail == null) {
            GoogleSignInAccount googleAcc = GoogleSignIn.getLastSignedInAccount(this);
            if (googleAcc != null) {
                userEmail = googleAcc.getEmail();
            }
        }

        if (userEmail != null) {
            docRef = db.collection("users").document(userEmail).collection("labels").document(selectedLabel);
        } else {
            Toast.makeText(this, "User email not found, cannot create course.", Toast.LENGTH_LONG).show();
            finish(); // Close activity if user email is not found
        }
    }

    // back button function
    public void onBackClicked(View view) {
        finish();
    }

    public void createCourse(View view) {
        if (docRef != null){
            // Reference to the new sub-collection under the existing document
            CollectionReference coursesCollection = docRef.collection("Courses");

            String courseCode = courseCodeInput.getText().toString().trim();
            String courseName = courseNameInput.getText().toString().trim();
            String lecturerName = lecturerNameInput.getText().toString().trim();
            Integer lectureGroup = (Integer) lectureGroupSpinner.getSelectedItem();
            Integer tutorialGroup = (Integer) tutorialGroupSpinner.getSelectedItem();

            // check whether the field is empty
            if (courseCode.isEmpty() || courseName.isEmpty() || lecturerName.isEmpty() || lectureGroup == null || tutorialGroup == null) {
                // Show an error message or alert to the user
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            } else {
                // if all fields are filled, then create a Map to store the course data
                Map<String, Object> courseData = new HashMap<>();
                courseData.put("courseName", courseName);
                courseData.put("lecturerName", lecturerName);
                courseData.put("lectureGroup", lectureGroup);
                courseData.put("tutorialGroup", tutorialGroup);

                // Reference to the specific document in the sub-collection or create the document with id is courseCode if it didn't exist
                DocumentReference courseDocRef = docRef.collection("Courses").document(courseCode);

                // create or overwrite the document with courseCode as the ID
                courseDocRef.set(courseData)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(CreateCourse.this, "Course created successfully!", Toast.LENGTH_SHORT).show();
                            // Handler to delay starting the View Courses Activity
                            new Handler().postDelayed(() -> {
                                // Back to View Course Page
                                Intent intent = new Intent(CreateCourse.this, ViewCoursesPage.class);
                                intent.putExtra("selectedLabel", selectedLabel);
                                startActivity(intent);
                                finish(); // close current activity
                            }, 1000);  // Delay of 1s, enough?

                        })
                        .addOnFailureListener(e -> Toast.makeText(CreateCourse.this, "Failed to create course: " + e.getMessage(), Toast.LENGTH_SHORT).show());

            }
        }
        else {
            Toast.makeText(this, "Document reference not initialized.", Toast.LENGTH_SHORT).show();
        }
    }

    private Integer[] generateNumbers(int start, int end) {
        Integer[] numbers = new Integer[end - start + 1];
        for (int i = start; i <= end; i++) {
            numbers[i - start] = i;
        }
        return numbers;
    }
}