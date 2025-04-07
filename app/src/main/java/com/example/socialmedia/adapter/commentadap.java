package com.example.socialmedia.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmedia.MainActivity;
import com.example.socialmedia.R;
import com.example.socialmedia.model.Users;
import com.example.socialmedia.model.commentsmodel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class commentadap extends RecyclerView.Adapter<commentadap.ViewHolder> {
    private Context mContext;
    private List<commentsmodel> mComment;
    private String postid;

    private FirebaseUser firebaseUser;
    public commentadap(Context context, List<commentsmodel> comments, String postid){
        mContext = context;
        mComment = comments;
        this.postid = postid;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.commentitem, parent, false);
        return new commentadap.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final commentsmodel comments = mComment.get(position);
        holder.comment.setText(comments.getComment());
        getUserInfo(holder.image_profile, holder.username, comments.getPublisher());
        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("publisherid", comments.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("publisherid", comments.getPublisher());
                mContext.startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return mComment.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image_profile;
        public TextView username, comment;
        public ViewHolder(View view) {
            super(view);
                image_profile = itemView.findViewById(R.id.image_profile);
                username = itemView.findViewById(R.id.username);
                comment = itemView.findViewById(R.id.comment);

        }
    }
    private void getUserInfo(ImageView imageProfile, TextView username, String publisher) {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Users").child(publisher);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                Glide.with(mContext).load(user.getProfile()).into(imageProfile);
                username.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
