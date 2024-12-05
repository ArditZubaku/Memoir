package com.zubaku.memoir.adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

  public AllPostsAdapter(Context context, List<Post> postsList) {
    this.context = context;
    this.postsList = postsList;
  }

  // Responsible for creating new view holders for the items
  @NonNull
  @Override
  public AllPostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context).inflate(R.layout.post_row, parent, false);
    return new AllPostsViewHolder(view);
  }

  // Responsible for updating the view holder with the data from the model
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
  }

  @Override
  public int getItemCount() {
    return postsList.size();
  }

  // ViewHolder pattern:
  // Declare the views that you want to display in the list
  // Holds references to the views within an item layout
  public static class AllPostsViewHolder extends RecyclerView.ViewHolder {
    public TextView title, description, timeAdded, username;
    public ImageView image, shareButton;

    public AllPostsViewHolder(@NonNull View itemView) {
      super(itemView);
      title = itemView.findViewById(R.id.title);
      description = itemView.findViewById(R.id.description);
      timeAdded = itemView.findViewById(R.id.timestamp);
      username = itemView.findViewById(R.id.username);
      image = itemView.findViewById(R.id.image);
      shareButton = itemView.findViewById(R.id.shareButton);
      shareButton.setOnClickListener(v -> Log.i("Memoir", "Sharing the post..."));
    }
  }
}
