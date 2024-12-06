package com.zubaku.memoir.activity;

import static com.zubaku.memoir.utils.Helpers.areParamsValid;
import static com.zubaku.memoir.utils.Helpers.areTextFieldsValid;
import static com.zubaku.memoir.utils.Helpers.buildErrorMessage;
import static com.zubaku.memoir.utils.Helpers.getStringValue;
import static com.zubaku.memoir.utils.Helpers.showToast;

import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.zubaku.memoir.R;

public class SignUpActivity extends AppCompatActivity {
  // UI Elements
  TextView signUpText;
  EditText signUpUsername;
  AutoCompleteTextView signUpEmail;
  EditText signUpPassword;
  Button signUpButton;
  TextView signUpErrorMessage;

  // Firebase Auth
  private FirebaseAuth auth;

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

    // Initialize UI components
    signUpText = findViewById(R.id.signUpText);
    signUpUsername = findViewById(R.id.signUpUsername);
    signUpEmail = findViewById(R.id.signUpEmail);
    signUpPassword = findViewById(R.id.signUpPassword);
    signUpButton = findViewById(R.id.signUpButton);
    signUpErrorMessage = findViewById(R.id.signUpErrorMessage);

    // Initialize Firebase Auth
    auth = FirebaseAuth.getInstance();

    // Set up sign-up button click listener
    signUpButton.setOnClickListener(
        v -> {
          if (areTextFieldsValid(signUpUsername, signUpEmail, signUpPassword)) {
            String username = getStringValue(signUpUsername);
            String email = getStringValue(signUpEmail);
            String password = getStringValue(signUpPassword);

            createUserAccount(username, email, password);
          } else {
            showToast(getString(R.string.please_fill_fields), this);
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
                  // User created successfully, now set the display name
                  FirebaseUser user = auth.getCurrentUser();
                  if (user != null) {
                    updateUserProfile(user, username); // Update display name
                  }
                } else {
                  // If sign-up fails, show the error message
                  signUpErrorMessage.setText(buildErrorMessage(task.getException()));
                  showToast(getString(R.string.sign_up_error_message), this);
                }
              });
    }
  }

  private void updateUserProfile(FirebaseUser user, String username) {
    // Set the user's display name
    UserProfileChangeRequest profileUpdates =
        new UserProfileChangeRequest.Builder()
            .setDisplayName(username) // Set the username as display name
            .build();

    user.updateProfile(profileUpdates)
        .addOnCompleteListener(
            profileUpdateTask -> {
              if (profileUpdateTask.isSuccessful()) {
                // Successfully updated display name
                showToast(getString(R.string.sign_up_success), this);
                clearFields();
                // TODO: Add a go back to sign in button
              } else {
                signUpErrorMessage.setText(getString(R.string.error_updating_profile));
              }
            });
    }

  private void clearFields() {
    // Clear all fields and error message after successful sign-up
    signUpUsername.setText(null);
    signUpEmail.setText(null);
    signUpPassword.setText(null);
    signUpErrorMessage.setText(null);
  }
}