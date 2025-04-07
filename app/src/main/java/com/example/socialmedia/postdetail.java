package com.example.socialmedia;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.adapter.postadapter;
import com.example.socialmedia.model.posts;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class postdetail extends Fragment {
    String postid;

    RecyclerView recyclerView;
    postadapter postAdapter;
    List<posts> postList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



            View view = inflater.inflate(R.layout.fragment_postdetail, container, false);

            SharedPreferences prefs = getContext().getSharedPreferences("PREFS", MODE_PRIVATE);
            postid = prefs.getString("postid", "none");

            recyclerView = view.findViewById(R.id.recycler_view);
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(mLayoutManager);

            postList = new ArrayList<>();
            postAdapter = new postadapter(getContext(), postList);
            recyclerView.setAdapter(postAdapter);

            readPost();

            return view;
        }
        private void readPost(){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts").child(postid);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                   postList.clear();
                    posts post = dataSnapshot.getValue(posts.class);
                    postList.add(post);

                    postAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
}