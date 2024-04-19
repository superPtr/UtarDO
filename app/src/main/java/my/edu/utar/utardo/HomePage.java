package my.edu.utar.utardo;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends AppCompatActivity {

    private RecyclerView recyclerViewEvents;
    private RecyclerView recyclerViewTasks;
    private EventAdapter eventAdapter;
    private TaskAdapter taskAdapter;
    private List<Event> eventList;
    private List<Task> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page); // Set the correct layout file

        // Initialize RecyclerViews
        recyclerViewEvents = findViewById(R.id.recyclerViewEvents);
        recyclerViewTasks = findViewById(R.id.recyclerViewTasks);

//        // Initialize event list
//        eventList = new ArrayList<>();
//        eventList.add(new Event("Event 1"));
//        eventList.add(new Event("Event 2"));
//        eventList.add(new Event("Event 44"));
//        eventList.add(new Event("Event 42"));
//        eventList.add(new Event("Event 43"));
//        eventList.add(new Event("Event 32"));
//        eventList.add(new Event("Event 443"));
//        eventList.add(new Event("Event 453"));
//
//        // Initialize task list
//        taskList = new ArrayList<>();
//        taskList.add(new Task("Task 1"));
//        taskList.add(new Task("Task 2"));
//        taskList.add(new Task("Task 3"));

        // Initialize adapters
        eventAdapter = new EventAdapter(eventList);
        taskAdapter = new TaskAdapter(taskList);

        // Set layout managers and adapters
        recyclerViewEvents.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewEvents.setAdapter(eventAdapter);
        recyclerViewTasks.setAdapter(taskAdapter);
    }
}