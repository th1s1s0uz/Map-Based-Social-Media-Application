package com.example.b2bouz;
public class Post {
    private String postID;
    private String userID;
    private String title;
    private String content;
    private String uploadDate;

    public Post() {
        // Boş yapıcı metot gereklidir.
    }

    public Post(String postID, String userID, String title, String content, String uploadDate) {
        this.postID = postID;
        this.userID = userID;
        this.title = title;
        this.content = content;
        this.uploadDate = uploadDate;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }
}
