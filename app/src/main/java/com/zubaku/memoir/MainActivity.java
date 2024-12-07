package com.zubaku.memoir;

import static com.zubaku.memoir.utils.Constants.WELCOME_CHANNEL_ID;
import static com.zubaku.memoir.utils.Helpers.areParamsValid;
import static com.zubaku.memoir.utils.Helpers.buildErrorMessage;
import static com.zubaku.memoir.utils.Helpers.showToast;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.zubaku.memoir.activity.AllPostsActivity;
import com.zubaku.memoir.activity.ForgotPasswordActivity;
import com.zubaku.memoir.activity.SignUpActivity;
import com.zubaku.memoir.activity.TwoFactorActivity;

public class MainActivity extends AppCompatActivity {

  // Widgets
  Button loginButton;
  Button createAccountButton;
  EditText email;
  EditText password;
  TextView errorMessage;

  private FirebaseAuth auth;
  private static final String PREFS_NAME = "AppPreferences";
  private static final String CHANNEL_CREATED_KEY = "NotificationChannelCreated";
  private static final String NOTIFICATION_SHOWN_KEY = "NotificationShown";

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

    // Ensure notification channel is created only once
    if (!isNotificationChannelCreated()) {
      createNotificationChannel();
      setNotificationChannelCreated();
      Log.i("QETU", "CHANNEL CREATED");
    }

    // Show the welcome notification only once during the app's first launch
    if (!isNotificationShown()) {
      Log.i("QETU", "NTF SHOWN");
      // For Android 13+, request notification permission and show notification
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        showWelcomeNotification();
      }
      setNotificationShown(); // Mark notification as shown
    }

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

    TextView forgotPasswordText = findViewById(R.id.forgotPasswordText);
    forgotPasswordText.setPaintFlags(
        forgotPasswordText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    forgotPasswordText.setClickable(true);
    forgotPasswordText.setFocusable(true);
    forgotPasswordText.setOnClickListener(
        v -> startActivity(new Intent(MainActivity.this, ForgotPasswordActivity.class)));
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
      errorMessage.setText(null);
      startActivity(new Intent(MainActivity.this, TwoFactorActivity.class));
    }
  }

  private void onFailure(Exception exception) {
    showToast(getString(R.string.login_failed), this);
    errorMessage.setText(buildErrorMessage(exception));
  }

  private void createNotificationChannel() {
    CharSequence name = "Welcome Channel";
    String description = "Channel for welcome notifications";
    int importance = NotificationManager.IMPORTANCE_HIGH;
    NotificationChannel channel = new NotificationChannel(WELCOME_CHANNEL_ID, name, importance);
    channel.setDescription(description);

    NotificationManager notificationManager = getSystemService(NotificationManager.class);
    if (notificationManager != null) {
      notificationManager.createNotificationChannel(channel);
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
  public void showWelcomeNotification() {
    // Check and request permission if necessary
    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
        != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(
          this, new String[] {android.Manifest.permission.POST_NOTIFICATIONS}, 1);
      return;
    }

    // Create the notification with full-screen intent to make it a pop-up
    Intent intent = new Intent(this, AllPostsActivity.class);
    PendingIntent pendingIntent =
        PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

    NotificationCompat.Builder builder =
        new NotificationCompat.Builder(this, WELCOME_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getString(R.string.welcome_to_the_app))
            .setContentText(getString(R.string.we_re_glad_to_have_you_here))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .setFullScreenIntent(pendingIntent, true);

    // Show the notification
    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
    notificationManager.notify(1, builder.build());
  }

  @Override
  public void onRequestPermissionsResult(
      int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    if (requestCode == 1) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
          showWelcomeNotification();
        }
      } else {
        showToast(getString(R.string.notification_permission_denied), this);
      }
    }
  }

  // Check if notification channel has been created
  private boolean isNotificationChannelCreated() {
    SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    return prefs.getBoolean(CHANNEL_CREATED_KEY, false);
  }

  // Mark the notification channel as created
  private void setNotificationChannelCreated() {
    SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    SharedPreferences.Editor editor = prefs.edit();
    editor.putBoolean(CHANNEL_CREATED_KEY, true);
    editor.apply();
  }

  // Check if the notification has been shown before
  private boolean isNotificationShown() {
    SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    return prefs.getBoolean(NOTIFICATION_SHOWN_KEY, false);
  }

  // Mark the notification as shown
  private void setNotificationShown() {
    SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    SharedPreferences.Editor editor = prefs.edit();
    editor.putBoolean(NOTIFICATION_SHOWN_KEY, true);
    editor.apply();
  }
}
