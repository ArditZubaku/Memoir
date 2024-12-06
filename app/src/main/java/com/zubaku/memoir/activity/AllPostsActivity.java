package com.zubaku.memoir.activity;

import android.os.Bundle;
import android.view.Menu;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zubaku.memoir.R;
import com.zubaku.memoir.adapter.AllPostsAdapter;
import com.zubaku.memoir.model.Post;
import com.zubaku.memoir.utils.Collections;
import java.util.ArrayList;
import java.util.List;

public class AllPostsActivity extends AppCompatActivity {
  private FirebaseAuth auth;
  private FirebaseUser currentUser;

  // Firebase Firestore
  private final FirebaseFirestore db = FirebaseFirestore.getInstance();
  private final CollectionReference collectionReference = db.collection(Collections.Posts);

  // List of Journals
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
  }

  // Add a menu

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.post_menu, menu);
    return super.onCreateOptionsMenu(menu);
  }
}
