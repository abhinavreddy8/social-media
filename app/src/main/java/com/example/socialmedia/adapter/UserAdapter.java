package com.example.socialmedia.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmedia.R;
import com.example.socialmedia.model.Users;
import com.example.socialmedia.fragments.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context mContext;
    private List<Users> mUsers;
    private boolean isFragment;
    final String TAG= "UserAdapter";
    public UserAdapter(Context context, List<Users> users, boolean isFragment) {
        this.mContext = context;
        this.mUsers = users;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "inflating");
        View view = LayoutInflater.from(mContext).inflate(R.layout.useritem, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Users user = mUsers.get(position);
        holder.username.setText(user.getName());
        Log.i(TAG, String.valueOf(position));
        Log.i(TAG, user.getName());
        Log.i(TAG, "binding");
        // Ensure user is not null before proceeding
        if (firebaseUser == null || user.getUid() == null) {
            return;
        }

        isFollowing(user.getUid(), holder.btn);

        holder.username.setText(user.getName());
        holder.btn.setVisibility(View.VISIBLE);

        Glide.with(mContext).load(user.getProfile()).into(holder.img);

        if (user.getUid().equals(firebaseUser.getUid())) {
            holder.btn.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFragment) {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                    editor.putString("profileid", user.getUid());
                    editor.apply();

                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new Profile()).commit();
                }
            }
        });

        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.btn.getText().toString().equals("follow")) {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(user.getUid()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getUid())
                            .child("followers").child(firebaseUser.getUid()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(user.getUid()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getUid())
                            .child("followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers != null ? mUsers.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        private Button btn;
        public ImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            btn = itemView.findViewById(R.id.btn);
            img = itemView.findViewById(R.id.img);
        }
    }

    private void isFollowing(final String userId, final Button button) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser == null) {
            return;
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(userId).exists()) {
                    button.setText("following");
                } else {
                    button.setText("follow");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
