package com.example.socialmedia.model;

public class commentsmodel {
    private String comment;

    private String publisher;
    private String commentid;
    public commentsmodel() {}

    public commentsmodel(String comment, String publisher, String commentid) {
        this.comment = comment;
        this.publisher = publisher;
        this.commentid = commentid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public  String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getCommentid() {
        return commentid;
    }

    public void setCommentid(String commentid) {
        this.commentid = commentid;
    }
}
