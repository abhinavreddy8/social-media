package com.example.socialmedia.model;

public class posts {
        private String postid;
        private String image;
        private String description;
        private String publisher;

        public posts(String postid, String image, String description, String publisher) {
            this.postid = postid;
            this.image = image;
            this.description = description;
            this.publisher = publisher;
        }

        public posts() {
        }

        public String getPostid() {
            return postid;
        }

        public void setPostid(String postid) {
            this.postid = postid;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String postimage) {
            this.image = postimage;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getPublisher() {
            return publisher;
        }

        public void setPublisher(String publisher) {
            this.publisher = publisher;
        }
    }

