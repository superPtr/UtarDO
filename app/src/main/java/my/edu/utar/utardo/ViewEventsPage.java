package my.edu.utar.utardo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class ViewEventsPage extends AppCompatActivity {

    private static final String TAG = "ViewEventsPage";
    private RecyclerView recyclerViewEvents;
    private EventAdapter eventsAdapter;
    private TextView courseInfo;
    private ImageView leftBack;
    private String selectedCourseCode, selectedLabel;
    private FirebaseFirestore db;
    private List<Event> eventList = new ArrayList<>();
    private EventAdapter eventAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_events_page);

        db = FirebaseFirestore.getInstance();

        selectedCourseCode = getIntent().getStringExtra("selectedCourseCode");
        selectedLabel = getIntent().getStringExtra("selectedLabel");

        recyclerViewEvents = findViewById(R.id.recyclerViewEvents);
        courseInfo = findViewById(R.id.courseInfo);
        leftBack = findViewById(R.id.leftBack);

        recyclerViewEvents.setLayoutManager(new LinearLayoutManager(this));
        eventsAdapter = new EventAdapter(new ArrayList<>());
        recyclerViewEvents.setAdapter(eventsAdapter);

        leftBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        courseInfo.setText(selectedCourseCode);

        retrieveEvents(selectedCourseCode, selectedLabel);
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
}
