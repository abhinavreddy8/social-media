package com.example.socialmedia.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmedia.R;
import com.example.socialmedia.model.Users;
import com.example.socialmedia.editprofile;
import com.example.socialmedia.followers;
import com.example.socialmedia.adapter.photosadapter;
import com.example.socialmedia.model.posts;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class Profile extends Fragment {
    ImageView image_profile, options;
    TextView posts, followers, following, fullname, bio, username;
    Button edit_profile;

    private List<String> mySaves;

    FirebaseUser firebaseUser;
    String profileid;

    private RecyclerView recyclerView;
    private photosadapter adapter;
    private List<com.example.socialmedia.model.posts> postList;

    private RecyclerView recyclersaves;
    private photosadapter savesadapter;;
    private List<posts> postsaves;

   ImageButton myphotos, savedphotos;
    @Nullable

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=  inflater.inflate(R.layout.fragment_profile, container, false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", MODE_PRIVATE);
        profileid = prefs.getString("profileid", "none");
        Log.i("profileid", profileid);
        image_profile = view.findViewById(R.id.image_profile);
        posts = view.findViewById(R.id.posts);
        followers = view.findViewById(R.id.followers);
        following = view.findViewById(R.id.following);
        savedphotos=view.findViewById(R.id.saved_fotos);
       // bio = view.findViewById(R.id.bio);
        myphotos=view.findViewById(R.id.my_fotos);
        edit_profile = view.findViewById(R.id.edit_profile);
        username = view.findViewById(R.id.username);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(mLayoutManager);
        postList = new ArrayList<>();
        adapter = new photosadapter(getContext(), postList);
        recyclerView.setAdapter(adapter);

        recyclersaves = view.findViewById(R.id.recycler_view_save);
        recyclersaves.setHasFixedSize(true);
        LinearLayoutManager LayoutManager = new GridLayoutManager(getContext(), 3);
        recyclersaves.setLayoutManager(LayoutManager);
        postsaves = new ArrayList<>();
        savesadapter = new photosadapter(getContext(), postsaves);
        recyclersaves.setAdapter(savesadapter);

        recyclerView.setVisibility(View.VISIBLE);
        recyclersaves.setVisibility(View.GONE);
        userInfo();
        getFollowers();
        getNrPosts();
        myphotos();
        mySaves();
        if (profileid.equals(firebaseUser.getUid())){
            edit_profile.setText("Edit Profile");

        } else {
            checkFollow();
            savedphotos.setVisibility(View.GONE);
        }
        savedphotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.setVisibility(View.GONE);
                recyclersaves.setVisibility(View.VISIBLE);
            }
        });
        myphotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclersaves.setVisibility(View.GONE);
            }
        });
        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btn = edit_profile.getText().toString();

                if (btn.equals("Edit Profile")){

                    startActivity(new Intent(getContext(), editprofile.class));

                } else if (btn.equals("follow")){

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileid).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("followers").child(firebaseUser.getUid()).setValue(true);
                    addNotification();
                } else if (btn.equals("following")){

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileid).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("followers").child(firebaseUser.getUid()).removeValue();

                }
            }
        });
        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), com.example.socialmedia.followers.class);
                intent.putExtra("id", profileid);
                intent.putExtra("title", "followers");
                startActivity(intent);
            }
        });

        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), followers.class);
                intent.putExtra("id", profileid);
                intent.putExtra("title", "following");
                startActivity(intent);
            }
        });
        return view;
    }
    private void userInfo(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (getContext() == null){
                    return;
                }
                Users user = dataSnapshot.getValue(Users.class);

                Glide.with(getContext()).load(user.getProfile()).into(image_profile);
                username.setText(user.getName());
                //fullname.setText(user.getFullname());
                //bio.setText(user.getBio());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void checkFollow(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(profileid).exists()){
                    edit_profile.setText("following");
                } else{
                    edit_profile.setText("follow");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void getFollowers(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow").child(profileid).child("followers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                followers.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Follow").child(profileid).child("following");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                following.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getNrPosts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    posts post = snapshot.getValue(posts.class);
                    if (post.getPublisher().equals(profileid)){
                        i++;
                    }
                }
                posts.setText(""+i);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void myphotos(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    posts post = snapshot.getValue(posts.class);
                    if (post.getPublisher().equals(profileid)){
                        postList.add(post);
                    }
                }
                Collections.reverse(postList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void mySaves(){
        mySaves = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Saves").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    mySaves.add(snapshot.getKey());
                }
                readSaves();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void readSaves(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postsaves.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    posts post = snapshot.getValue(posts.class);

                    for (String id : mySaves) {
                        if (post.getPostid().equals(id)) {
                            postsaves.add(post);
                        }
                    }
                }
                savesadapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void addNotification(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(profileid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "started following you");
        hashMap.put("postid", "");
        hashMap.put("ispost", false);

        reference.push().setValue(hashMap);
    }

}