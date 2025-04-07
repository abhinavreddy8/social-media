package com.example.socialmedia.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.R;
import com.example.socialmedia.adapter.postadapter;
import com.example.socialmedia.model.posts;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Home extends Fragment {

    private RecyclerView recyclerView;
    private postadapter postAdapter;
    private List<posts> postList;

    private RecyclerView recyclerView_story;
    //private StoryAdapter storyAdapter;
    //private List<Story> storyList;

    private List<String> followingList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        postList = new ArrayList<>();
        postAdapter = new postadapter(getContext(), postList);
        recyclerView.setAdapter(postAdapter);
        isFollowing();
        return view;
    }

    private void isFollowing() {
        followingList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot datasnapshot : snapshot.getChildren()) {
                    followingList.add(datasnapshot.getKey());
                }
                readpost();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readpost() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    posts post = snapshot.getValue(posts.class);
                    for (String id : followingList) {
                        if (post.getPublisher().equals(id)) {
                            postList.add(post);
                        }
                    }
                }

                postAdapter.notifyDataSetChanged();
                //progress_circular.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}