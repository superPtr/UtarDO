package my.edu.utar.utardo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class BaseActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    public String TAG;
    TextView accEmail, accName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db =  FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

    }

    // Method to read data from Firestore
    protected void readFromFirestore(String collectionPath, String documentId,String collectionToRead, OnReadCompleteListener listener) {
        // Get a reference to the user's document
        DocumentReference userDocRef = db.collection("users").document(documentId);

        // Get the "labels" subcollection of the user
        userDocRef.collection(collectionToRead)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Create a map to store label data
                            Map<String, Object> labelsData = new HashMap<>();
                            // Iterate through the documents in the "labels" subcollection
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Add each document to the map
                                labelsData.put(document.getId(), document.getData());
                            }
                            // Pass the label data to the listener
                            listener.onReadComplete(labelsData);
                        } else {
                            // Error reading documents
                            listener.onReadError(task.getException());
                        }
                    }
                });
    }


    // Method to write data to Firestore
    protected void writeToFirestore(String collectionPath, String documentId, String labelName, OnWriteCompleteListener listener) {
        // Get a reference to the user's document
        DocumentReference userDocRef = db.collection("users").document(documentId);

        // Add a new document in the "labels" subcollection of the user
        userDocRef.collection("labels").document(labelName)
                .set(new HashMap<>())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void a) {
                        listener.onWriteComplete();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onWriteError(e);
                    }
                });
    }


    // Interface for read completion listener
    public interface OnReadCompleteListener {
        void onReadComplete(Map<String, Object> data);
        void onReadError(Exception e);
    }

    // Interface for write completion listener
    public interface OnWriteCompleteListener {
        void onWriteComplete();
        void onWriteError(Exception e);
    }



}

