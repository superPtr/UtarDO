package my.edu.utar.utardo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import my.edu.utar.utardo.databinding.ActivityLoginBinding;

public class login extends BaseActivity {

    ActivityLoginBinding binding;

    private Button loginButton;

    EditText passwordEditText;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    ImageView googleBtn;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        run();
        passwordEditText = findViewById(R.id.editTextPassword);
        googleBtn = findViewById(R.id.google);

        //set button for sign in to Google account
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);



        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        GoogleSignInAccount act = GoogleSignIn.getLastSignedInAccount(this);
        if (act != null) {
            navigateToHomepage();
        }



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


    private void run(){
        binding.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.editTextEmail.getText().toString();
                String password = binding.editTextPassword.getText().toString();

                if(!email.isEmpty() && !password.isEmpty()){
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(login.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                startActivity(new Intent(login.this, MainActivity.class));
                            }else{
                                Toast.makeText(login.this, "Failed to Authenticate", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else{
                    Toast.makeText(login.this,"Fill in email and password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.signUpNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login.this, signUp.class));
            }

        });


    }

    //Sign in method
    void signIn(){
        Intent signinInt = gsc.getSignInIntent();
        startActivityForResult(signinInt, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Attempt to retrieve the signed-in account
                GoogleSignInAccount account = task.getResult(ApiException.class);

                //save user data to database
                saveUserDataToFirestore(account);

                // Navigate to the homepage
                navigateToHomepage();
            } catch (ApiException e) {
                // Handle the exception
                Log.e("GoogleSignIn", "signInResult:failed code=" + e.getStatusCode());
                Toast.makeText(getApplicationContext(), "Google sign in failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveUserDataToFirestore(GoogleSignInAccount account) {
        String email = account.getEmail();
        String displayName = account.getDisplayName();

        // Assuming your Firestore collection is named "users"
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> userData = new HashMap<>();
        userData.put("user_name", displayName);

        db.collection("users").document(email)
                .set(userData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User data saved successfully");
                        } else {
                            Log.e(TAG, "Error adding document", task.getException());
                        }
                    }
                });
    }



    private void navigateToHomepage() {
        Intent intent = new Intent(login.this, MainActivity.class);
        startActivity(intent);
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
}