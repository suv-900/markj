package com.mark.web.models;

public class Post {
   private int postID;
   private String postTitle;
   private String postContent;
   private String postCreatedAt;   

    public void setPostID(int id){
        this.postID=id;
    }
    
    public void setPostTitle(String postTitle){
        this.postTitle=postTitle;
    }

    public void setPostContent(String postContent){
        this.postContent=postContent;
    }

    public void setPostCreatedAt(String time){
        this.postCreatedAt=time;
    }
}
