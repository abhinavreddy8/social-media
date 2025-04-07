package com.example.socialmedia.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmedia.R;
import com.example.socialmedia.postdetail;
import com.example.socialmedia.model.posts;

import java.util.List;

public class photosadapter extends RecyclerView.Adapter<photosadapter.ViewHolder>{

    private Context context;
    private List<com.example.socialmedia.model.posts> posts;
    public photosadapter(Context context, List<posts> posts){
        this.context = context;
        this.posts = posts;
    }
    @NonNull
    @Override
    public photosadapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.photoitem, parent, false);
        return new photosadapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull photosadapter.ViewHolder holder, int position) {
        final posts post = posts.get(position);

        Glide.with(context).load(post.getImage()).into(holder.post_image);
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
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView post_image;


        public ViewHolder(View itemView) {
            super(itemView);

            post_image = itemView.findViewById(R.id.post_image);

        }
    }
}
