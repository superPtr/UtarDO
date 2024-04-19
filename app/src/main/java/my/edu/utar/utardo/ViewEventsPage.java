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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class ViewEventsPage extends AppCompatActivity {

    private static final String TAG = "ViewEventsPage";
    private RecyclerView recyclerViewEvents;
    private EventAdapter eventsAdapter;
    private TextView courseInfo;
    private ImageView leftBack;
    private String courseCode;
    private String courseName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_events_page);

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

        courseCode = getIntent().getStringExtra("courseCode");
        courseName = getIntent().getStringExtra("courseName");
        courseInfo.setText(courseCode + " - " + courseName);

        retrieveEvents(courseCode);
    }

    private void retrieveEvents(String courseCode) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events")
                .whereEqualTo("courseCode", courseCode)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Event> events = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                Event event = document.toObject(Event.class);
                                events.add(event);
                            }
                            eventsAdapter.setEvents(events);
                        } else {
                            Log.d(TAG, "Error getting events: ", task.getException());
                        }
                    }
                });
    }
}
