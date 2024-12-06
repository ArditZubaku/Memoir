package com.zubaku.memoir.activity;

import static com.zubaku.memoir.utils.Helpers.buildErrorMessage;
import static com.zubaku.memoir.utils.Helpers.getStringValue;
import static com.zubaku.memoir.utils.Helpers.showToast;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.zubaku.memoir.MainActivity;
import com.zubaku.memoir.R;
import com.zubaku.memoir.model.Post;
import com.zubaku.memoir.utils.Collections;
import com.zubaku.memoir.utils.Helpers;
import com.zubaku.memoir.utils.Paths;
import java.util.Date;

public class AddPostActivity extends AppCompatActivity {

  // UI Components
  private ProgressBar progressBar;
  private EditText postTitle, postDescription;
  private ImageView postImageView;

  // Firebase
  private final FirebaseFirestore db = FirebaseFirestore.getInstance();
  private final CollectionReference collectionReference = db.collection(Collections.Posts);
  private StorageReference storageReference;

  // User Authentication
  private FirebaseAuth firebaseAuth;
  private String currentUserId, currentUserName;

  // Image Capture
  private ActivityResultLauncher<String> mTakePhoto;
  private Uri imageUri;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setupEdgeToEdgeLayout();
    setContentView(R.layout.activity_add_post);

    initializeUIComponents();
    initializeFirebase();
    setupPhotoCapture();

    // Handle Save Button Click
    Button saveButton = findViewById(R.id.post_save_journal_button);
    saveButton.setOnClickListener(v -> savePost());
  }

  // Set up edge-to-edge layout for the activity
  private void setupEdgeToEdgeLayout() {
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_add_post);
    ViewCompat.setOnApplyWindowInsetsListener(
        findViewById(R.id.main),
        (v, insets) -> {
          Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
          v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
          return insets;
        });
  }

  // Initialize UI components
  private void initializeUIComponents() {
    progressBar = findViewById(R.id.post_progressBar);
    postTitle = findViewById(R.id.post_title);
    postDescription = findViewById(R.id.post_description);
    postImageView = findViewById(R.id.post_imageView);
    progressBar.setVisibility(View.INVISIBLE);
  }

  // Initialize Firebase-related components
  private void initializeFirebase() {
    firebaseAuth = FirebaseAuth.getInstance();
    storageReference = FirebaseStorage.getInstance().getReference();
  }

  // Setup photo capture using Activity Result Launcher
  private void setupPhotoCapture() {
    mTakePhoto =
        registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            result -> {
              // Display selected image
              postImageView.setImageURI(result);
              // Store URI of the selected image
              imageUri = result;
            });

    ImageView addPhotoBtn = findViewById(R.id.postCameraButton);
    addPhotoBtn.setOnClickListener(v -> mTakePhoto.launch(Paths.Images));
  }

  // Save the post to Firestore and Firebase Storage
  private void savePost() {
    String title = getStringValue(postTitle);
    String description = getStringValue(postDescription);

    if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || imageUri == null) {
      showErrorMessage();
      return;
    }

    progressBar.setVisibility(View.VISIBLE);

    // Firebase Storage file path for the image
    final StorageReference filePath =
        storageReference.child(Paths.Folder).child(Paths.Prefix + Timestamp.now().getSeconds());

    // Upload image to Firebase Storage
    uploadImage(filePath, title, description);
  }

  // Upload image to Firebase Storage and handle post creation
  private void uploadImage(StorageReference filePath, String title, String description) {
    filePath
        .putFile(imageUri)
        .addOnSuccessListener(
            taskSnapshot ->
                filePath
                    .getDownloadUrl()
                    .addOnSuccessListener(
                        uri -> {
                          String imageUrl = uri.toString();
                          createPost(title, description, imageUrl);
                        }))
        .addOnFailureListener(
            e -> {
              progressBar.setVisibility(View.INVISIBLE);
              // Show error if image upload fails
              buildErrorMessage(e);
            });
  }

  // Create a Post object and save it to Firestore
  private void createPost(String title, String description, String imageUrl) {
    Post post =
        new Post(
            title,
            description,
            imageUrl,
            currentUserId,
            currentUserName,
            new Timestamp(new Date()));

    collectionReference
        .add(post)
        .addOnSuccessListener(
            documentReference -> {
              progressBar.setVisibility(View.INVISIBLE);
              startActivity(new Intent(AddPostActivity.this, AllPostsActivity.class));
              finish();
            })
        .addOnFailureListener(Helpers::buildErrorMessage);
  }

  // Handle user authentication and retrieve user details
  @Override
  protected void onStart() {
    super.onStart();
    FirebaseUser currentUser = firebaseAuth.getCurrentUser();

    if (currentUser == null) {
      redirectToMainActivity();
    } else {
      currentUserId = currentUser.getUid();
      currentUserName =
          currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Anonymous";
    }
  }

  // Redirect to MainActivity if the user is not logged in
  private void redirectToMainActivity() {
    startActivity(new Intent(AddPostActivity.this, MainActivity.class));
    finish();
  }

  // Show error message
  private void showErrorMessage() {
    progressBar.setVisibility(View.INVISIBLE);
    showToast(getString(R.string.fill_and_select_image), this);
  }
}
