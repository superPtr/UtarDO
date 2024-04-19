package my.edu.utar.utardo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CalendarPage extends AppCompatActivity {

    private GridLayout calendarGrid;
    private TextView monthTextView;
    private Calendar currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_page);

        RelativeLayout mainLayout = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;


        });

        calendarGrid = findViewById(R.id.calendarGrid);
        monthTextView = findViewById(R.id.monthTextView);

        // Initialize current date
        currentDate = Calendar.getInstance();

        // Populate calendar
        populateCalendar(currentDate);




        // Set onClickListener for previous month button
        Button prevMonthButton = findViewById(R.id.prevMonthButton);
        prevMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDate.add(Calendar.MONTH, -1); // Move to previous month
                populateCalendar(currentDate); // Update calendar
            }
        });

        // Set onClickListener for next month button
        Button nextMonthButton = findViewById(R.id.nextMonthButton);
        nextMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDate.add(Calendar.MONTH, 1); // Move to next month
                populateCalendar(currentDate); // Update calendar
            }
        });

        // Set current month text
        updateMonthText(currentDate);





        //Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.menu_calendar);
        }
        setupBottomNavigation();
    }

    private void populateCalendar(Calendar calendar) {
        // Clear previous calendar
        calendarGrid.removeAllViews();

        // Logic to populate the calendar with dates
        // You can customize this as per your requirement
        // For simplicity, let's assume 31 days for each month
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int buttonSize = (screenWidth - 16 * 8) / 7; // Adjust margins and columns as needed
        for (int i = 1; i <= daysInMonth; i++) {
            Button dateButton = new Button(this);
            dateButton.setText(String.valueOf(i));
            dateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle click event for date button
                    // Show details for the clicked date
                    // You can show details in a dialog, new activity, etc.
                    // For now, let's just log the clicked date
                    Button clickedButton = (Button) v;
                    String clickedDate = clickedButton.getText().toString();
                    // Log the clicked date
                    System.out.println("Clicked Date: " + clickedDate);

                    // Highlight the clicked date
                    highlightDate(clickedButton);


                    //----------------------------------------------------------------------
                    // Populate event view with events for the clicked date
                    populateEventView(clickedDate);
                    //----------------------------------------------------------------------
                }
            });
            // Set button size and margins
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(buttonSize, buttonSize);
            params.setMargins(4, 4, 4, 4); // Decreased margins
            dateButton.setLayoutParams(params);

            // Highlight the selected date if it matches the current date
            Calendar currentDate = Calendar.getInstance();
            if (calendar.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR) &&
                    calendar.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH) &&
                    i == currentDate.get(Calendar.DAY_OF_MONTH)) {
                highlightDate(dateButton);
            }

            calendarGrid.addView(dateButton);
        }

        // Update month text
        updateMonthText(calendar);


    }

    private void updateMonthText(Calendar calendar) {
        // Update the month text with the current month
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        String currentMonth = sdf.format(calendar.getTime());
        monthTextView.setText(currentMonth);
    }

    private void highlightDate(Button dateButton) {
        // Remove highlight from all buttons
        for (int i = 0; i < calendarGrid.getChildCount(); i++) {
            View childView = calendarGrid.getChildAt(i);
            if (childView instanceof Button) {
                Button button = (Button) childView;
                button.setBackgroundResource(android.R.drawable.btn_default); // Reset background to default
            }
        }

        // Highlight the selected date button
        dateButton.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light)); // Set the background color
    }





    //----------------------------------------------------------------------
    private void populateEventView(String clickedDate) {
        // Clear previous events
        LinearLayout eventLinearLayout = findViewById(R.id.eventLinearLayout);
        eventLinearLayout.removeAllViews();

        // Dummy events for demonstration
        String[] dummyEvents = {
                "Event 1 on " + clickedDate,
                "Event 2 on " + clickedDate,
                "Event 3 on " + clickedDate,
                "Event 4 on " + clickedDate,
                "Event 5 on " + clickedDate,
                "Event 6 on " + clickedDate
        };

        // Populate event view with dummy events
        for (String event : dummyEvents) {
            TextView eventTextView = new TextView(this);
            eventTextView.setText(event);
            eventTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            eventLinearLayout.addView(eventTextView);
        }
    }
    //----------------------------------------------------------------------






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
        String[] options = {"New label", "New course", "New event", "New task"};

        // Create and configure the dropdown list dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(CalendarPage.this);
        builder.setTitle("Select an option");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the selected option
                if (which == 0) {
                    // Redirect to the page for creating a new label
                    startActivity(new Intent(CalendarPage.this, LabelPage.class));
                } else if (which == 1) {
                    // Redirect to the page for creating a new course
                    startActivity(new Intent(CalendarPage.this, ViewCoursesPage.class));
                } else if (which == 2) {
                    // Redirect to the page for creating a new event
                    startActivity(new Intent(CalendarPage.this, ViewEventsPage.class));
                } else if (which == 3) {
                    // Redirect to the page for creating a new task
                    startActivity(new Intent(CalendarPage.this, ViewTasksPage.class));
                }
            }
        });

        // Display the dropdown list dialog
        builder.show();
    }

}