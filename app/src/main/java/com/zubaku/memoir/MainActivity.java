package com.zubaku.memoir;

import static com.zubaku.memoir.utils.Helpers.areParamsValid;
import static com.zubaku.memoir.utils.Helpers.buildErrorMessage;
import static com.zubaku.memoir.utils.Helpers.showToast;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.zubaku.memoir.activity.AllPostsActivity;
import com.zubaku.memoir.activity.SignUpActivity;

public class MainActivity extends AppCompatActivity {

  // Widgets
  Button loginButton;
  Button createAccountButton;
  EditText email;
  EditText password;
  TextView errorMessage;

  private FirebaseAuth auth;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_main);
    ViewCompat.setOnApplyWindowInsetsListener(
        findViewById(R.id.main),
        (v, insets) -> {
          Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
          v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
          return insets;
        });

    loginButton = findViewById(R.id.loginButton);
    createAccountButton = findViewById(R.id.createAccountButton);
    email = findViewById(R.id.email);
    password = findViewById(R.id.password);
    errorMessage = findViewById(R.id.errorMessage);

    createAccountButton.setOnClickListener(
        v -> startActivity(new Intent(MainActivity.this, SignUpActivity.class)));

    auth = FirebaseAuth.getInstance();
    loginButton.setOnClickListener(
        v -> {
          String emailText = email.getText().toString().trim();
          String passwordText = password.getText().toString().trim();

          loginUser(emailText, passwordText);
        });
  }

  private void loginUser(String email, String password) {
    if (areParamsValid(email, password)) {
      auth.signInWithEmailAndPassword(email, password)
          .addOnSuccessListener(this::onSuccess)
          .addOnFailureListener(this::onFailure);
    } else {
      showToast(getString(R.string.please_fill_fields), this);
    }
  }

  private void onSuccess(AuthResult authResult) {
    FirebaseUser currentUser = auth.getCurrentUser();
    if (currentUser != null) {
      Log.i("Memoir", currentUser.toString());
      startActivity(new Intent(MainActivity.this, AllPostsActivity.class));
      errorMessage.setText(null);
    }
  }

  private void onFailure(Exception exception) {
    showToast(getString(R.string.login_failed), this);
    errorMessage.setText(buildErrorMessage(exception));
  }
}
