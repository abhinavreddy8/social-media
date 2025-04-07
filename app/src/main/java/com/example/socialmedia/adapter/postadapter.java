package com.example.socialmedia.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.socialmedia.R;
import com.example.socialmedia.comments;
import com.example.socialmedia.followers;
import com.example.socialmedia.fragments.Profile;
import com.example.socialmedia.postdetail;
import com.example.socialmedia.model.posts;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class postadapter extends RecyclerView.Adapter<postadapter.ViewHolder> {
    private Context context;
    private List<com.example.socialmedia.model.posts> posts;

    private FirebaseUser firebaseUser;

    public postadapter(Context context, List<posts> posts){
        this.context = context;
        this.posts = posts;
    }
    @NonNull
    @Override
    public postadapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.postitem, parent, false);
        return new postadapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull postadapter.ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final posts post = posts.get(position);
        String postImageUrl = post.getImage();
        //Log.i("user",postImageUrl);
        Glide.with(context).load(postImageUrl)
                .apply(new RequestOptions()
                        .override(400, 200)
                        .centerCrop()
                        .placeholder(R.drawable.display))
                .into(holder.post_image);


        if (post.getDescription().equals("")){
            holder.description.setVisibility(View.GONE);
        } else {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(post.getDescription());
        }
        liked(post.getPostid(),holder.like);
        likenum(holder.likes,post.getPostid());
        isSaved(post.getPostid(), holder.save);
        getComments(post.getPostid(),holder.comments);
        GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public  boolean onDoubleTap(MotionEvent e) {
                if (holder.like.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference().child("likes").child(post.getPostid()).child(firebaseUser.getUid()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("likes").child(post.getPostid()).child(firebaseUser.getUid()).removeValue();
                }
                return true;
            }
        });
        holder.post_image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gestureDetector.onTouchEvent(motionEvent);
                return true;
            }
        });
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.like.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("likes").child(post.getPostid()).child(firebaseUser.getUid()).setValue(true);
                    addNotification(post.getPublisher(), post.getPostid());
                }
                else{
                    FirebaseDatabase.getInstance().getReference().child("likes").child(post.getPostid()).child(firebaseUser.getUid()).removeValue();
                }
            }
        });
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, comments.class);
                intent.putExtra("postid", post.getPostid());
                intent.putExtra("publisherid", post.getPublisher());
                context.startActivity(intent);
            }
        });
        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, comments.class);
                intent.putExtra("postid", post.getPostid());
                intent.putExtra("publisherid", post.getPublisher());
                context.startActivity(intent);
            }
        });
        holder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("profileid", post.getPublisher());
                editor.apply();

                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Profile()).commit();
            }
        });

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("profileid", post.getPublisher());
                editor.apply();

                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Profile()).commit();
            }
        });
        holder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("postid", post.getPostid());
                editor.apply();

                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new postdetail()).commit();
            }
        });
        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.save.getTag().equals("save")){
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(post.getPostid()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(post.getPostid()).removeValue();
                }
            }
        });
        holder.likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, followers.class);
                intent.putExtra("id", post.getPostid());
                intent.putExtra("title", "likes");
                context.startActivity(intent);
            }
        });


    }

    private void getComments(String postId, final TextView comments){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("comments").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                comments.setText("View All "+dataSnapshot.getChildrenCount()+" Comments");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView image_profile, post_image, like, comment, save, more;
        public TextView username, likes, publisher, description, comments;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image_profile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            post_image = itemView.findViewById(R.id.post_image);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            save = itemView.findViewById(R.id.save);
            likes = itemView.findViewById(R.id.likes);
            publisher = itemView.findViewById(R.id.publisher);
            description = itemView.findViewById(R.id.description);
            comments = itemView.findViewById(R.id.comments);

        }
    }
    private void liked(final String pid,final ImageView imageView){
        FirebaseUser fuser=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("likes").child(pid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(fuser.getUid()).exists()) {
                    imageView.setImageResource(R.drawable.ic_action_fav);
                    imageView.setTag("liked");
                } else {
                    imageView.setImageResource(R.drawable.ic_action_save);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void likenum(TextView textView,String postid){
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("likes").child(postid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                textView.setText(snapshot.getChildrenCount()+" "+"likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void isSaved(final String postid, final ImageView imageView){

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Saves").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postid).exists()){
                    imageView.setImageResource(R.drawable.ic_action_bookmarked);
                    imageView.setTag("saved");
                } else{
                    imageView.setImageResource(R.drawable.ic_action_bookmark);
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void addNotification(String userid, String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "liked your post");
        hashMap.put("postid", postid);
        hashMap.put("ispost", true);

        reference.push().setValue(hashMap);
    }
}
