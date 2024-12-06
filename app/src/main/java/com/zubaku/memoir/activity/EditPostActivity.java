package com.zubaku.memoir.activity;

import static com.zubaku.memoir.utils.Constants.DESCRIPTION;
import static com.zubaku.memoir.utils.Constants.IMAGE_URL;
import static com.zubaku.memoir.utils.Constants.POST_ID;
import static com.zubaku.memoir.utils.Constants.TITLE;
import static com.zubaku.memoir.utils.Helpers.areParamsValid;
import static com.zubaku.memoir.utils.Helpers.buildErrorMessage;
import static com.zubaku.memoir.utils.Helpers.getStringValue;
import static com.zubaku.memoir.utils.Helpers.showToast;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.zubaku.memoir.R;
import com.zubaku.memoir.model.Post;
import com.zubaku.memoir.utils.Collections;
import com.zubaku.memoir.utils.Paths;
import java.util.HashMap;
import java.util.Map;

public class EditPostActivity extends AppCompatActivity {

  private EditText editTitle, editDescription;
  private ImageView editImagePreview;

  private FirebaseFirestore db;
  private StorageReference storageReference;

  // ID of the post being edited
  private String postId;
  // Holds the current post data
  private Post currentPost;
  // Holds the new image URI if changed
  private Uri selectedImageUri;
  // Launcher for handling the result of the image picker intent.
  private ActivityResultLauncher<Intent> imagePickerLauncher;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_post);

    // Initialize Firestore and Firebase Storage
    db = FirebaseFirestore.getInstance();
    storageReference = FirebaseStorage.getInstance().getReference();

    // Initialize views
    editTitle = findViewById(R.id.editTitle);
    editDescription = findViewById(R.id.editDescription);
    editImagePreview = findViewById(R.id.editImagePreview);
    Button changeImageButton = findViewById(R.id.changeImageButton);
    Button savePostButton = findViewById(R.id.savePostButton);
    Button cancelEditButton = findViewById(R.id.cancelEditButton);

    // Get the post ID passed from the previous activity
    postId = getIntent().getStringExtra(POST_ID);

    if (!areParamsValid(postId)) {
      showToast(getString(R.string.error_no_post_id_provided), this);
      finish();
      return;
    }

    // Fetch the post details from Firestore
    fetchPostDetails();

    // Set listeners
    changeImageButton.setOnClickListener(v -> openImagePicker());
    savePostButton.setOnClickListener(v -> savePostChanges());
    cancelEditButton.setOnClickListener(v -> finish());

    imagePickerLauncher =
        registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
              if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                selectedImageUri = result.getData().getData();
                // Preview the selected image
                editImagePreview.setImageURI(selectedImageUri);
              }
            });
  }

  private void fetchPostDetails() {
    db.collection(Collections.Posts)
        .document(postId)
        .get()
        .addOnSuccessListener(
            documentSnapshot -> {
              if (documentSnapshot.exists()) {
                currentPost = documentSnapshot.toObject(Post.class);
                if (currentPost != null) {
                  populateFields();
                }
              } else {
                showToast(getString(R.string.post_not_found), this);
                finish();
              }
            })
        .addOnFailureListener(
            e -> {
              showToast(buildErrorMessage(e), this);
              finish();
            });
  }

  private void populateFields() {
    if (currentPost == null) return;

    // Populate fields with current post data
    editTitle.setText(currentPost.getTitle());
    editDescription.setText(currentPost.getDescription());

    // Load the image into the ImageView using Glide
    Glide.with(this)
        .load(currentPost.getImageURL())
        .placeholder(android.R.drawable.ic_menu_gallery)
        .into(editImagePreview);
  }

  private void openImagePicker() {
    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    imagePickerLauncher.launch(intent);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
      selectedImageUri = data.getData();
      editImagePreview.setImageURI(selectedImageUri);
    }
  }

  private void savePostChanges() {
    String updatedTitle = getStringValue(editTitle);
    String updatedDescription = getStringValue(editDescription);

    if (areParamsValid(updatedTitle, updatedDescription)) {
      showToast(getString(R.string.title_and_description_cannot_be_empty), this);
      return;
    }

    // Update Firestore document
    if (selectedImageUri != null) {
      // Upload new image if it was changed
      uploadNewImage(updatedTitle, updatedDescription);
    } else {
      // Update post without changing the image
      updatePostInFirestore(updatedTitle, updatedDescription, currentPost.getImageURL());
    }
  }

  private void uploadNewImage(String title, String description) {
    StorageReference imageRef = storageReference.child(Paths.Images + postId + Paths.Postfix);

    imageRef
        .putFile(selectedImageUri)
        .addOnSuccessListener(
            taskSnapshot ->
                imageRef
                    .getDownloadUrl()
                    .addOnSuccessListener(
                        uri -> {
                          String newImageUrl = uri.toString();
                          updatePostInFirestore(title, description, newImageUrl);
                        }))
        .addOnFailureListener(e -> showToast(buildErrorMessage(e), this));
  }

  private void updatePostInFirestore(String title, String description, String imageUrl) {
    Map<String, Object> updatedData = new HashMap<>();
    updatedData.put(TITLE, title);
    updatedData.put(DESCRIPTION, description);
    updatedData.put(IMAGE_URL, imageUrl);

    db.collection(Collections.Posts)
        .document(postId)
        .update(updatedData)
        .addOnSuccessListener(
            aVoid -> {
              showToast(getString(R.string.post_updated_successfully), this);
              finish();
            })
        .addOnFailureListener(e -> showToast(buildErrorMessage(e), this));
  }
}
