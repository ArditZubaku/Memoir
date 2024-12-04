package com.zubaku.memoir.activity;

import static com.zubaku.memoir.utils.Helpers.areParamsValid;
import static com.zubaku.memoir.utils.Helpers.areTextFieldsValid;
import static com.zubaku.memoir.utils.Helpers.getStringValue;
import static com.zubaku.memoir.utils.Helpers.showToast;

import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zubaku.memoir.R;
import com.zubaku.memoir.utils.Collections;

public class SignUpActivity extends AppCompatActivity {
  TextView signUpText;
  EditText signUpUsername;
  AutoCompleteTextView signUpEmail;
  EditText signUpPassword;
  Button signUpButton;
  TextView signUpErrorMessage;

  // Firebase Auth
  private FirebaseAuth auth;
  // Used to check changes on user's auth state - triggers callback when user signs in or out
  private FirebaseAuth.AuthStateListener authStateListener;
  // Current authenticated user
  private FirebaseUser currentUser;

  // Connection
  private FirebaseFirestore db = FirebaseFirestore.getInstance();
  private CollectionReference collectionReference = db.collection(Collections.Users);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_sign_up);
    ViewCompat.setOnApplyWindowInsetsListener(
        findViewById(R.id.main),
        (v, insets) -> {
          Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
          v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
          return insets;
        });

    signUpText = findViewById(R.id.signUpText);
    signUpUsername = findViewById(R.id.signUpUsername);
    signUpEmail = findViewById(R.id.signUpEmail);
    signUpPassword = findViewById(R.id.signUpPassword);
    signUpButton = findViewById(R.id.signUpButton);
    signUpErrorMessage = findViewById(R.id.signUpErrorMessage);

    auth = FirebaseAuth.getInstance();

    // Listen for changes in the authentication state
    // and respond to them accordingly when the state changes
    authStateListener =
        firebaseAuth -> {
          currentUser = firebaseAuth.getCurrentUser();
          // Check if the user is logged in
          if (currentUser != null) {
            // User is already logged in
          } else {
            // No user is logged in
          }
        };

    signUpButton.setOnClickListener(
        v -> {
          if (areTextFieldsValid(signUpUsername, signUpEmail, signUpPassword)) {
            String username = getStringValue(signUpUsername);
            String email = getStringValue(signUpEmail);
            String password = getStringValue(signUpPassword);

            createUserAccount(username, email, password);
          } else {
            showToast("Please fill out all the fields!", this);
          }
        });
  }

  private void createUserAccount(String username, String email, String password) {
    if (areParamsValid(username, email, password)) {
      // Create a new user with the email and password
      auth.createUserWithEmailAndPassword(email, password)
          .addOnCompleteListener(
              task -> {
                if (task.isSuccessful()) {
                  showToast("User Created Successfully", this);
                  clearFields();
                } else {
                  signUpErrorMessage.setText(buildErrorMessage(task));
                  showToast("User Creation Failed", this);
                }
              });
    }
  }

  private void clearFields() {
    signUpUsername.setText(null);
    signUpEmail.setText(null);
    signUpPassword.setText(null);
    signUpErrorMessage.setText(null);
  }

  @NonNull
  private String buildErrorMessage(@NonNull Task<AuthResult> task) {
    Exception exception = task.getException();
    if (exception != null && exception.getMessage() != null) {
      return String.join("\n", exception.getMessage().split("(?<=\\.)"));
    }
    return "An unknown error occurred.";
  }
}
