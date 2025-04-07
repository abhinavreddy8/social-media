package com.example.socialmedia.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmedia.R;
import com.example.socialmedia.model.Users;
import com.example.socialmedia.fragments.Profile;
import com.example.socialmedia.model.notmodel;
import com.example.socialmedia.postdetail;
import com.example.socialmedia.model.posts;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class notadapter extends RecyclerView.Adapter<notadapter.ViewHolder> {
    private Context context;
    private List<notmodel> notmodels;
    public notadapter(Context context, List<notmodel> notmodels) {
        this.context = context;
        this.notmodels = notmodels;
    }
    @NonNull
    @Override
    public notadapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notitem, parent, false);
        return new notadapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull notadapter.ViewHolder holder, int position) {
        final notmodel notification = notmodels.get(position);

        holder.text.setText(notification.getText());

        getUserInfo(holder.image_profile, holder.username, notification.getUserid());

        if (notification.isIspost()) {
            holder.post_image.setVisibility(View.VISIBLE);
            getPostImage(holder.post_image, notification.getPostid());
        } else {
            holder.post_image.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (notification.isIspost()) {
                    SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                    editor.putString("postid", notification.getPostid());
                    editor.apply();

                    ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new postdetail()).commit();
                } else {
                    SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                    editor.putString("profileid", notification.getUserid());
                    editor.apply();

                    ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new Profile()).commit();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return notmodels.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView image_profile, post_image;
        public TextView username, text;

        public ViewHolder(View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            post_image = itemView.findViewById(R.id.post_image);
            username = itemView.findViewById(R.id.username);
            text = itemView.findViewById(R.id.comment);
        }
    }
    private void getUserInfo(final ImageView imageView, final TextView username, String publisherid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(publisherid);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);
                Glide.with(context).load(user.getProfile()).into(imageView);
                username.setText(user.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getPostImage(final ImageView post_image, String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("posts").child(postid);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                posts post = dataSnapshot.getValue(posts.class);
                Glide.with(context).load(post.getImage()).into(post_image);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
