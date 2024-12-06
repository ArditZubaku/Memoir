package com.zubaku.memoir.activity;

import static com.zubaku.memoir.utils.Helpers.buildErrorMessage;
import static com.zubaku.memoir.utils.Helpers.showToast;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.zubaku.memoir.BuildConfig;
import com.zubaku.memoir.R;
import java.util.Properties;
import java.util.Random;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class TwoFactorActivity extends AppCompatActivity {

  private EditText verificationCodeEditText;
  private String verificationCode;
  private String userEmail;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_two_factor);

    FirebaseAuth auth = FirebaseAuth.getInstance();

    verificationCodeEditText = findViewById(R.id.verificationCodeEditText);
    Button verifyButton = findViewById(R.id.verifyButton);
    TextView resendCodeText = findViewById(R.id.resendCodeText);

    // Retrieve the current user's email from FirebaseAuth
    FirebaseUser currentUser = auth.getCurrentUser();
    if (currentUser != null) {
      userEmail = currentUser.getEmail();
      if (userEmail != null) {
        sendVerificationCode(userEmail);
      } else {
        showToast(getString(R.string.failed_to_get_user_email), this);
        finish();
      }
    } else {
      showToast(getString(R.string.user_not_authenticated), this);
      finish(); // End activity if no user is logged in
    }

    // Verify the code when the user clicks "Verify"
    verifyButton.setOnClickListener(v -> verifyCode());

    // Allow user to resend the code
    resendCodeText.setOnClickListener(v -> resendVerificationCode());
  }

  // Send a verification code to the user's email
  private void sendVerificationCode(String email) {
    verificationCode = generateRandomCode();
    sendVerificationEmail(email, verificationCode);
  }

  // Generate a random 6-digit verification code
  private String generateRandomCode() {
    Random random = new Random();
    int code = random.nextInt(999999 - 100000) + 100000;
    return String.valueOf(code);
  }

  // Send the verification email to the user
  private void sendVerificationEmail(String email, String verificationCode) {
    String subject = "Your Verification Code";
    String message = "Use this code to verify your login: " + verificationCode;

    sendEmailUsingBackend(email, subject, message);
  }

  // Verify the entered code from the user
  private void verifyCode() {
    String enteredCode = verificationCodeEditText.getText().toString().trim();

    if (!TextUtils.isEmpty(enteredCode)) {
      if (isCodeValid(enteredCode)) {
        startActivity(new Intent(TwoFactorActivity.this, AllPostsActivity.class));
        finish();
      } else {
        showToast(getString(R.string.invalid_verification_code_please_try_again), this);
      }
    } else {
      showToast(getString(R.string.please_enter_the_verification_code), this);
    }
  }

  // Check if the entered code matches the sent verification code
  private boolean isCodeValid(String enteredCode) {
    return enteredCode.equals(verificationCode);
  }

  // Resend the verification code
  private void resendVerificationCode() {
    if (userEmail != null) {
      // Resend the verification email
      sendVerificationCode(userEmail); // Resend the code to the same email
    } else {
      showToast(getString(R.string.user_email_is_not_available), this);
    }
  }

  private void sendEmailUsingBackend(String email, String subject, String message) {
    String senderEmail = BuildConfig.SMTP_SENDER_EMAIL;
    String senderPassword = BuildConfig.SMTP_SENDER_PASSWORD;

    Properties properties = new Properties();
    properties.put("mail.smtp.host", "smtp.gmail.com");
    properties.put("mail.smtp.port", "587");
    properties.put("mail.smtp.auth", "true");
    properties.put("mail.smtp.starttls.enable", "true");

    // Create an authenticator for the SMTP server
    Authenticator auth =
        new javax.mail.Authenticator() {
          @Override
          protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(senderEmail, senderPassword);
          }
        };

    // Create a session with the SMTP server
    Session session = Session.getInstance(properties, auth);

    try {
      MimeMessage mimeMessage = new MimeMessage(session);

      mimeMessage.setFrom(new InternetAddress(senderEmail));
      mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(email));

      mimeMessage.setSubject(subject);
      mimeMessage.setText(message);

      // Send the email
      new Thread(
              () -> {
                try {
                  Transport.send(mimeMessage);
                  runOnUiThread(
                      () ->
                          showToast(
                              getString(R.string.a_verification_code_has_been_sent_to_your_email),
                              this));
                } catch (MessagingException e) {
                  runOnUiThread(
                      () ->
                          showToast(
                              getString(
                                  R.string.failed_to_send_the_verification_email_please_try_again),
                              this));
                }
              })
          .start();
    } catch (MessagingException e) {
      showToast(getString(R.string.failed_to_create_the_email_message), this);
      showToast(buildErrorMessage(e), this);
    }
  }
}
