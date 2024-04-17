package my.edu.utar.utardo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        recyclerViewCourses = findViewById(R.id.recyclerViewCourses);
        recyclerViewCourses.setLayoutManager(new GridLayoutManager(this, 3));

        coursesAdapter = new CoursesAdapter(coursesList);
        recyclerViewCourses.setAdapter(coursesAdapter);

        readCoursesFromFirestore();
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

        readFromFirestore("users", userEmail, "labels/" + selectedLabel + "/Courses", new OnReadCompleteListener() {
            @Override
            public void onReadComplete(Map<String, Object> data) {
                coursesList.clear();
                for (String documentId : data.keySet()) {
                    Course course = new Course(documentId);
                    coursesList.add(course);
                }
                coursesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onReadError(Exception e) {
                // Handle error
            }
        });
    }

    // Method to handle add course event
    private void handleAddCourse(String labelText) {
        Intent intent = new Intent(this, CreateCourse.class);
        intent.putExtra("selectedLabel", labelText);
        startActivity(intent);
    }
}
