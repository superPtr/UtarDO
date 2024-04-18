package my.edu.utar.utardo;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.app.DatePickerDialog;
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
import android.widget.TextView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddEvent extends AppCompatActivity {
    private String selectedLabel, selectedCourse;
    private EditText eventNameInput, detailsInput;
    private Spinner labelSpinner, courseIDSpinner;
    private Calendar myCalendar;
    private DocumentReference docRef;
    private CollectionReference colLabelRef, colCourseRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        // initialize database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        myCalendar = null;


        myCalendar = Calendar.getInstance();
        View dateButton = findViewById(R.id.imageButtonDate);

        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateDisplay();
        };

        dateButton.setOnClickListener(v -> new DatePickerDialog(this, dateSetListener,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show());

        labelSpinner = findViewById(R.id.labelSpinner);
        courseIDSpinner = findViewById(R.id.courseIDSpinner);
        eventNameInput = findViewById(R.id.eventNameInput);
        detailsInput = findViewById(R.id.detailsInput);

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
    }

    // back button
    public void onBackClicked(View view) {
        finish();
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

    private void updateDateDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        String formattedDate = sdf.format(myCalendar.getTime());

        TextView dateTextView = findViewById(R.id.textViewDate);
        dateTextView.setText(formattedDate);
    }

    private String formatDate(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        return sdf.format(calendar.getTime());
    }

    // add btn
    public void addEvent(View view){
        String labelID = (String) labelSpinner.getSelectedItem();
        String courseID = (String) courseIDSpinner.getSelectedItem();
        String eventName = eventNameInput.getText().toString().trim();
        String details = detailsInput.getText().toString().trim();
        String formattedDate = (myCalendar != null) ? formatDate(myCalendar) : null;

        // check whether the field is empty
        if (labelID == null || courseID == null || eventName.isEmpty()) {
            // Show an error message or alert to the user
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
        } else {
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("eventName", eventName);
            eventData.put("eventDetails", details);
            eventData.put("eventDate", formattedDate);

            CollectionReference colEvent;
            docRef = colCourseRef.document(courseID);
            colEvent = docRef.collection("events");
            colEvent.add(eventData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AddEvent.this, "Task created successfully!", Toast.LENGTH_SHORT).show();
                        // Handler to delay starting the Specific Courses Activity
                        new Handler().postDelayed(() -> {
                            // Back to View Event Page
                           Intent intent = new Intent(AddEvent.this, ViewEventsPage.class);
                            intent.putExtra("selectedLabel", selectedLabel);
                            intent.putExtra("selectedCourseCode", courseID);
                            startActivity(intent);
                            finish(); // close current activity
                        }, 1000);  // Delay of 1s, enough?

                    })
                    .addOnFailureListener(e -> Toast.makeText(AddEvent.this, "Failed to create course: " + e.getMessage(), Toast.LENGTH_SHORT).show());

        }
    }
}