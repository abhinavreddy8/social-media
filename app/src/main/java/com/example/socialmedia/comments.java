package com.example.socialmedia;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmedia.adapter.commentadap;
import com.example.socialmedia.model.Users;
import com.example.socialmedia.model.commentsmodel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class comments extends AppCompatActivity {
    private RecyclerView recyclerView;
    private commentadap commentAdapter;
    private List<commentsmodel> commentList;

    EditText addcomment;
    ImageView image_profile;
    TextView post;

    String postid;
    String publisherid;

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_comments);
        recyclerView = findViewById(R.id.recycler_view);
        addcomment = findViewById(R.id.add_comment);
        image_profile = findViewById(R.id.image_profile);
        post = findViewById(R.id.post);
        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = getIntent();
        postid = intent.getStringExtra("postid");
        publisherid = intent.getStringExtra("publisherid");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
       // recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        commentList = new ArrayList<>();
        commentAdapter = new commentadap(this, commentList, postid);
        recyclerView.setAdapter(commentAdapter);
        readComments();
        getImage();
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addcomment.getText().toString().equals("")){
                    Toast.makeText(comments.this, "You can't send empty comment", Toast.LENGTH_SHORT).show();
                } else {
                    addComment();
                }
            readComments();

            }

            private void addComment() {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("comments").child(postid);
                String comment_text = addcomment.getText().toString();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("comment", comment_text);
                hashMap.put("publisher", firebaseUser.getUid());
                reference.push().setValue(hashMap);
                addNotification();
                addcomment.setText("");

            }
        });
    }
    private void readComments(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("comments").child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                commentList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    commentsmodel comment = snapshot.getValue(commentsmodel.class);
                    commentList.add(comment);
                }

                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void getImage(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);
                Glide.with(getApplicationContext()).load(user.getProfile()).into(image_profile);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void addNotification(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(publisherid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "commented: "+addcomment.getText().toString());
        hashMap.put("postid", postid);
        hashMap.put("ispost", true);

        reference.push().setValue(hashMap);
    }
}