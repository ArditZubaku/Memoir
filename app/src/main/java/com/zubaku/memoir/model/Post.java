package com.zubaku.memoir.model;

import androidx.annotation.NonNull;
import com.google.firebase.Timestamp;

public class Post {
  // Firestore document ID
  private String id;
  private String title;
  private String description;
  private String imageURL;
  private String userID;
  private String username;
  private Timestamp timeAdded;

  // When using Firebase, always create an empty constructor
  public Post() {}

  public Post(
      String id,
      String title,
      String description,
      String imageURL,
      String userID,
      String username,
      Timestamp timeAdded) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.imageURL = imageURL;
    this.userID = userID;
    this.username = username;
    this.timeAdded = timeAdded;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getImageURL() {
    return imageURL;
  }

  public void setImageURL(String imageURL) {
    this.imageURL = imageURL;
  }

  public String getUserID() {
    return userID;
  }

  public void setUserID(String userID) {
    this.userID = userID;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Timestamp getTimeAdded() {
    return timeAdded;
  }

  public void setTimeAdded(Timestamp timeAdded) {
    this.timeAdded = timeAdded;
  }

  @NonNull
  @Override
  public String toString() {
    return "Post{"
        + "id='"
        + id
        + '\''
        + ", title='"
        + title
        + '\''
        + ", description='"
        + description
        + '\''
        + ", imageURL='"
        + imageURL
        + '\''
        + ", userID='"
        + userID
        + '\''
        + ", username='"
        + username
        + '\''
        + ", timeAdded="
        + timeAdded
        + '}';
  }
}
