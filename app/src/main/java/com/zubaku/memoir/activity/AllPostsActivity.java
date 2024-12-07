package com.zubaku.memoir.activity;

import static com.zubaku.memoir.utils.Constants.DESCRIPTION;
import static com.zubaku.memoir.utils.Constants.IMAGE_URL;
import static com.zubaku.memoir.utils.Constants.NO;
import static com.zubaku.memoir.utils.Constants.POST_ID;
import static com.zubaku.memoir.utils.Constants.TITLE;
import static com.zubaku.memoir.utils.Constants.YES;
import static com.zubaku.memoir.utils.Helpers.showToast;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.zubaku.memoir.MainActivity;
import com.zubaku.memoir.R;
import com.zubaku.memoir.adapter.AllPostsAdapter;
import com.zubaku.memoir.model.Post;
import com.zubaku.memoir.utils.Collections;
import com.zubaku.memoir.utils.Helpers;
import java.util.ArrayList;
import java.util.List;

public class AllPostsActivity extends AppCompatActivity {
  private FirebaseAuth auth;
  private FirebaseUser currentUser;

  // Firebase Firestore
  private final FirebaseFirestore db = FirebaseFirestore.getInstance();
  private final CollectionReference collectionReference = db.collection(Collections.Posts);

  // List of Posts
  private List<Post> postsList;

  // Adapter
  private AllPostsAdapter allPostsAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_all_posts);

    // Set up toolbar
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    auth = FirebaseAuth.getInstance();
    currentUser = auth.getCurrentUser();

    // RecyclerView
    RecyclerView recyclerView = findViewById(R.id.recyclerView);
    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));

    postsList = new ArrayList<>();

    allPostsAdapter =
        new AllPostsAdapter(
            AllPostsActivity.this,
            postsList,
            new AllPostsAdapter.PostClickListener() {
              @Override
              public void onPostClick(Post post) {
                openEditPostActivity(post);
              }

              @Override
              public void onEditPostClick(Post post) {
                openEditPostActivity(post);
              }

              @Override
              public void onDeletePostClick(Post post) {
                deletePost(post);
              }
            });
    recyclerView.setAdapter(allPostsAdapter);

    FloatingActionButton fab = findViewById(R.id.fab);
    fab.setOnClickListener(
        v -> {
          if (currentUser != null && auth != null) {
            startActivity(new Intent(AllPostsActivity.this, AddPostActivity.class));
          }
        });
  }

  // Add a menu
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.post_menu, menu);

    // Find the sign out menu item
    MenuItem signOutItem = menu.findItem(R.id.action_sign_out);

    // Apply the custom style (text color)
    if (signOutItem != null) {
      SpannableString spannable = new SpannableString(signOutItem.getTitle());
      spannable.setSpan(new ForegroundColorSpan(Color.BLACK), 0, spannable.length(), 0);
      signOutItem.setTitle(spannable);
    }

    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    int itemId = item.getItemId();

    if (itemId == R.id.action_sign_out) {
      if (currentUser != null && auth != null) {
        auth.signOut();
        startActivity(new Intent(AllPostsActivity.this, MainActivity.class));
        // Close the activity
        finish();
      }
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onStart() {
    super.onStart();
    fetchPosts();
  }

  // Fetch posts from Firestore
  private void fetchPosts() {
    collectionReference
        // Order by timeAdded, latest first
        .orderBy("timeAdded", Query.Direction.DESCENDING)
        .addSnapshotListener(
            (querySnapshot, e) -> {
              if (e != null) {
                Helpers.buildErrorMessage(e);
                return;
              }

              if (querySnapshot != null) {
                for (DocumentChange change : querySnapshot.getDocumentChanges()) {
                  Post post = change.getDocument().toObject(Post.class);
                  post.setId(change.getDocument().getId());

                  handleDocumentChange(change.getType(), post);
                }
              }
            });
  }

  private void handleDocumentChange(DocumentChange.Type changeType, Post post) {
    switch (changeType) {
      case ADDED:
        handlePostAdded(post);
        break;
      case MODIFIED:
        handlePostModified(post);
        break;
      case REMOVED:
        handlePostRemoved(post);
        break;
    }
  }

  private void handlePostAdded(Post post) {
    int position = findPostPositionById(post.getId());
    if (position == -1) {
      postsList.add(post);
      allPostsAdapter.notifyItemInserted(0);
    }
  }

  private void handlePostModified(Post post) {
    int position = findPostPositionById(post.getId());
    if (position >= 0) {
      postsList.set(position, post);
      allPostsAdapter.notifyItemChanged(position);
    }
  }

  private void handlePostRemoved(Post post) {
    int position = findPostPositionById(post.getId());
    if (position >= 0) {
      postsList.remove(position);
      allPostsAdapter.notifyItemRemoved(position);
    }
  }

  private int findPostPositionById(String postId) {
    for (int i = 0; i < postsList.size(); i++) {
      if (postsList.get(i).getId().equals(postId)) {
        return i;
      }
    }
    // Post not found
    return -1;
  }

  // Opens the EditPostActivity for a specific post
  private void openEditPostActivity(Post post) {
    Log.i("QETU", post.toString());
    Intent intent = new Intent(AllPostsActivity.this, EditPostActivity.class);
    intent.putExtra(POST_ID, post.getId());
    intent.putExtra(TITLE, post.getTitle());
    intent.putExtra(DESCRIPTION, post.getDescription());
    intent.putExtra(IMAGE_URL, post.getImageURL());
    startActivity(intent);
  }

  private void deletePost(Post post) {
    // Confirm deletion with a dialog before deleting
    new AlertDialog.Builder(this)
        .setMessage(R.string.are_you_sure_you_want_to_delete_this_post)
        .setPositiveButton(
            YES,
            (dialog, which) ->
                collectionReference
                    .document(post.getId())
                    .delete()
                    .addOnSuccessListener(
                        aVoid ->
                            showToast(getString(R.string.the_post_was_deleted_successfully), this))
                    .addOnFailureListener(Helpers::buildErrorMessage))
        .setNegativeButton(NO, null)
        .show();
  }
}
