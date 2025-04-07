package com.example.socialmedia;

import static com.example.socialmedia.R.id.*;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class open extends AppCompatActivity {
    CardView card1,card2;
    TextView text;
    ImageView img;
    Animation top,bottom;
    Button button, button2;
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_open);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        card1=findViewById(R.id.card1);

        img=findViewById(R.id.imageView);

        top=AnimationUtils.loadAnimation(this,R.anim.topanim);
        bottom=AnimationUtils.loadAnimation(this,R.anim.botanim);
        card1.setAnimation(top);
        user=FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(open.this,MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }, 7000);
        }
        else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(open.this,register.class);
                    startActivity(i);
                    finish();
                }
            }, 7000);
        }
    }
}