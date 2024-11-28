package com.zubaku.memoir.model;

import com.google.firebase.Timestamp;

public class Post {
  private String title;
  private String description;
  private String imageURL;
  private String userID;
  private String username;
  private Timestamp timeAdded;

  // When using Firebase, always create an empty constructor
  public Post() {}

  public Post(String title, String description, String imageURL, String userID, String username, Timestamp timeAdded) {
    this.title = title;
    this.description = description;
    this.imageURL = imageURL;
    this.userID = userID;
    this.username = username;
    this.timeAdded = timeAdded;
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
}
