package my.edu.utar.utardo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class LabelPage extends BaseActivity {

    private GridLayout labelGrid;
    private ImageButton addLabelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label);

        labelGrid = findViewById(R.id.labelGrid);

        addLabelButton = findViewById(R.id.addLabelButton);

        // Set click listener for add label button
        addLabelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add new label
                addNewLabel();
            }
        });

        // Read labels from Firestore
        readLabelsFromFirestore();
    }

    private void addNewLabel() {
        // Display a dialog to prompt the user to enter the label name
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Label");

        // Set up the input field
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newLabelName = input.getText().toString().trim();
                if (!newLabelName.isEmpty()) {
                    // Add the new label to Firestore
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    String userEmail = currentUser.getEmail();
                    if (userEmail != null) {
                        writeToFirestore("users", userEmail, newLabelName, new OnWriteCompleteListener() {
                            @Override
                            public void onWriteComplete() {
                                // Add the new label to the grid
                                addLabelToGrid(newLabelName);
                            }

                            @Override
                            public void onWriteError(Exception e) {
                                // Handle write error
                                Toast.makeText(LabelPage.this, "Error adding label: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(LabelPage.this, "Please enter a label name", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Show the dialog
        builder.show();
    }


    // Method to read labels from Firestore
    private void readLabelsFromFirestore() {
        // Get the current user's email
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            readLabelsFromEmail(userEmail);
        } else {
            GoogleSignInAccount googleAcc = GoogleSignIn.getLastSignedInAccount(this);
            if (googleAcc != null) {
                String UserEmail = googleAcc.getEmail();
                readLabelsFromEmail(UserEmail);
            }
        }
    }

    // Read labels from Firestore using the current user's email
    private void readLabelsFromEmail(String userEmail) {
        readFromFirestore("users", userEmail, "labels", new OnReadCompleteListener() {
            @Override
            public void onReadComplete(Map<String, Object> data) {
                if (data != null) {
                    // Populate label grid with labels from Firestore
                    populateLabelGrid(data);
                }
            }

            @Override
            public void onReadError(Exception e) {
                // Handle read error
                Log.e(TAG, "Error reading labels from Firestore: " + e.getMessage());
            }
        });
    }

    private void populateLabelGrid(Map<String, Object> data) {
        // Iterate through the data and add labels to the grid
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String labelText = entry.getKey();
            addLabelToGrid(labelText);
        }
    }


    // Method to add a label to the grid
    private void addLabelToGrid(String labelText) {
        // Create a new Button to represent the label
        Button labelButton = new Button(this);
        // Set the text of the button to the label text
        labelButton.setText(labelText);
        // Set text size
        labelButton.setTextSize(24);

        // Disable automatic uppercase conversion
        labelButton.setAllCaps(false);
        labelButton.setBackgroundResource(R.drawable.label_background);
        // Set the OnClickListener for the button
        labelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the label click
                handleLabelClick(labelText);
            }
        });

        // Define layout parameters for the button
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 380;
        params.height = 380;
        params.setMargins(24, 24, 24, 24); // Set margins if desired
        labelButton.setLayoutParams(params);

        // Add the button to the GridLayout
        labelGrid.addView(labelButton);
    }


    // Method to handle label click events
    private void handleLabelClick(String labelText) {
        // Perform an action when a label is clicked
        Intent intent = new Intent(this, ViewCoursesPage.class);
        intent.putExtra("selectedLabel", labelText);
        startActivity(intent);
    }
}