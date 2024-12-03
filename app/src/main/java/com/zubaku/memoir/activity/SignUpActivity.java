package com.zubaku.memoir.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
  }
}