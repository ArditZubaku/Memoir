package com.zubaku.memoir.activity;

import static com.zubaku.memoir.utils.Constants.DESCRIPTION;
import static com.zubaku.memoir.utils.Constants.IMAGE_URL;
import static com.zubaku.memoir.utils.Constants.NO;
import static com.zubaku.memoir.utils.Constants.POST_ID;
import static com.zubaku.memoir.utils.Constants.TITLE;
import static com.zubaku.memoir.utils.Constants.YES;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
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

  // RecyclerView
  private RecyclerView recyclerView;

  // Adapter
  private AllPostsAdapter allPostsAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_all_posts);
    ViewCompat.setOnApplyWindowInsetsListener(
        findViewById(R.id.main),
        (v, insets) -> {
          Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
          v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
          return insets;
        });

    auth = FirebaseAuth.getInstance();
    currentUser = auth.getCurrentUser();

    // Widgets
    recyclerView = findViewById(R.id.recyclerView);
    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));

    postsList = new ArrayList<>();

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
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    int itemId = item.getItemId();

    if (itemId == R.id.action_add) {
      if (currentUser != null && auth != null) {
        // Go to the add post activity
        startActivity(new Intent(AllPostsActivity.this, AddPostActivity.class));
      }
    } else if (itemId == R.id.action_sign_out) {
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
        .get()
        .addOnSuccessListener(this::handleQuerySuccess)
        .addOnFailureListener(Helpers::buildErrorMessage);
  }

  // Handle the success of the Firestore query
  private void handleQuerySuccess(QuerySnapshot querySnapshot) {
    // Save the size of the list before clearing
    int previousSize = postsList.size();

    // Clear the previous list to avoid duplicates
    postsList.clear();

    // Loop through the snapshots and add posts to the list
    for (QueryDocumentSnapshot snapshot : querySnapshot) {
      Post post = snapshot.toObject(Post.class);
      post.setId(snapshot.getId()); // Assign the Firestore document ID to the post object
      postsList.add(post);
    }

    // If adapter is not already set, set it
    if (allPostsAdapter == null) {
      allPostsAdapter =
          new AllPostsAdapter(
              AllPostsActivity.this,
              postsList,
              new AllPostsAdapter.PostClickListener() {
                @Override
                public void onPostClick(Post post) {
                  // View post
                  openEditPostActivity(post);
                }

                @Override
                public void onEditPostClick(Post post) {
                  // Edit post
                  openEditPostActivity(post);
                }

                @Override
                public void onDeletePostClick(Post post) {
                  // Delete post
                  deletePost(post);
                }
              });
      recyclerView.setAdapter(allPostsAdapter);
    } else {
      // Use notifyItemRangeInserted to notify only about the new items added
      allPostsAdapter.notifyItemRangeInserted(previousSize, postsList.size());
    }
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
                        aVoid -> {
                          // Find the post's position and remove it
                          int position = postsList.indexOf(post);
                          if (position >= 0) {
                            postsList.remove(position);
                            allPostsAdapter.notifyItemRemoved(position);
                          }
                        })
                    .addOnFailureListener(Helpers::buildErrorMessage))
        .setNegativeButton(NO, null)
        .show();
  }
}
