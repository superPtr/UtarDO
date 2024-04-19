package my.edu.utar.utardo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;
import java.util.Map;

import my.edu.utar.utardo.databinding.ActivitySignUpBinding;

public class signUp extends AppCompatActivity {

    ActivitySignUpBinding binding;
    private String TAG;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    EditText passwordEditText;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        setVariable();


        passwordEditText = findViewById(R.id.passwordInput);

        // Set click listener for the drawableEnd (password icon) of the EditText
        passwordEditText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // Check if the click event is within the bounds of the drawableEnd icon
                if (event.getRawX() >= (passwordEditText.getRight() - passwordEditText.getCompoundDrawables()[2].getBounds().width())) {
                    togglePasswordVisibility();
                    return true;
                }
            }
            return false;
        });
    }

    // Method to toggle password visibility
    private void togglePasswordVisibility() {
        if (passwordEditText.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            // Hide password characters
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            // Show password characters
            passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
        // Move cursor to the end of the text
        passwordEditText.setSelection(passwordEditText.getText().length());
    }


    private void setVariable() {
        binding.signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = binding.usernameInput.getText().toString();
                String email = binding.emailInput.getText().toString();
                String password = binding.passwordInput.getText().toString();

                if (password.length() < 8) {
                    Toast.makeText(signUp.this, "Password entered must be 8 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(signUp.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isComplete()) {
                            Log.i(TAG, "onComplete: ");
                            // User authentication successful, save additional user data to Firestore
                            //String userID = mAuth.getCurrentUser().getUid();
                            saveUserDataToFirestore(email, username);

                        } else {
                            Log.i(TAG, "failure: " + task.getException());
                            Toast.makeText(signUp.this, "Failed to Authenticate", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void saveUserDataToFirestore(String email, String username) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("user_name", username);

        db.collection("users").document(email)
                .set(userData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // User data saved successfully
                            startActivity(new Intent(signUp.this, MainActivity.class));
                            finish();
                        } else {
                            Log.e(TAG, "Error adding document", task.getException());
                            Toast.makeText(signUp.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}