package com.zubaku.memoir.activity;

import static com.zubaku.memoir.utils.Helpers.buildErrorMessage;
import static com.zubaku.memoir.utils.Helpers.showToast;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.zubaku.memoir.R;

public class ForgotPasswordActivity extends AppCompatActivity {

  private EditText emailField;
  private ProgressBar progressBar;
  private FirebaseAuth firebaseAuth;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_forgot_password);

    firebaseAuth = FirebaseAuth.getInstance();

    emailField = findViewById(R.id.forgot_password_email);
    progressBar = findViewById(R.id.forgot_password_progress_bar);

    Button sendResetButton = findViewById(R.id.forgot_password_send_button);
    sendResetButton.setOnClickListener(v -> handlePasswordReset());
  }

  private void handlePasswordReset() {
    String email = emailField.getText().toString().trim();

    if (!TextUtils.isEmpty(email)) {
      progressBar.setVisibility(View.VISIBLE);
      firebaseAuth
          .sendPasswordResetEmail(email)
          .addOnSuccessListener(
              aVoid -> {
                progressBar.setVisibility(View.INVISIBLE);
                showToast(getString(R.string.link_sent), this);
                finish();
              })
          .addOnFailureListener(
              e -> {
                progressBar.setVisibility(View.INVISIBLE);
                showToast(buildErrorMessage(e), this);
                Log.e("QETU", buildErrorMessage(e));
              });
    } else {
      showToast(getString(R.string.enter_valid_email), this);
    }
  }
}
