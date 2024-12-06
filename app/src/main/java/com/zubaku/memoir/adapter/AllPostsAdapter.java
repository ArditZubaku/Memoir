package com.zubaku.memoir.adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.zubaku.memoir.R;
import com.zubaku.memoir.model.Post;
import java.util.List;

public class AllPostsAdapter extends RecyclerView.Adapter<AllPostsAdapter.AllPostsViewHolder> {
  private final Context context;
  private final List<Post> postsList;
  private final PostClickListener postClickListener;

  public AllPostsAdapter(
      Context context, List<Post> postsList, PostClickListener postClickListener) {
    this.context = context;
    this.postsList = postsList;
    this.postClickListener = postClickListener;
  }

  @NonNull
  @Override
  public AllPostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context).inflate(R.layout.post_row, parent, false);
    return new AllPostsViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull AllPostsViewHolder holder, int position) {
    Post currentPost = postsList.get(position);
    holder.title.setText(currentPost.getTitle());
    holder.description.setText(currentPost.getDescription());
    holder.username.setText(currentPost.getUsername());

    String timeAgo =
        (String)
            DateUtils.getRelativeTimeSpanString(currentPost.getTimeAdded().getSeconds() * 1000);
    holder.timeAdded.setText(timeAgo);

    String imageUrl = currentPost.getImageURL();
    Glide.with(context).load(imageUrl).fitCenter().into(holder.image);

    // Handle Edit and Delete button clicks
    holder.editButton.setOnClickListener(v -> postClickListener.onEditPostClick(currentPost));
    holder.deleteButton.setOnClickListener(v -> postClickListener.onDeletePostClick(currentPost));

    // Add click listener to trigger post view details
    holder.itemView.setOnClickListener(v -> postClickListener.onPostClick(currentPost));
  }

  @Override
  public int getItemCount() {
    return postsList.size();
  }

  public static class AllPostsViewHolder extends RecyclerView.ViewHolder {
    public TextView title, description, timeAdded, username;
    public ImageView image;
    public ImageButton editButton, deleteButton;

    public AllPostsViewHolder(@NonNull View itemView) {
      super(itemView);
      title = itemView.findViewById(R.id.title);
      description = itemView.findViewById(R.id.description);
      timeAdded = itemView.findViewById(R.id.timestamp);
      username = itemView.findViewById(R.id.username);

      Context context = itemView.getContext();
      Animation fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);
      username.startAnimation(fadeIn);

      image = itemView.findViewById(R.id.image);
      editButton = itemView.findViewById(R.id.editButton);
      deleteButton = itemView.findViewById(R.id.deleteButton);
    }
  }

  // Interface for post click listener
  public interface PostClickListener {
    // View post
    void onPostClick(Post post);

    // Edit post
    void onEditPostClick(Post post);

    // Delete post
    void onDeletePostClick(Post post);
  }
}
