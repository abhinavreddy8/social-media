package com.example.socialmedia;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.example.socialmedia.fragments.Home;
import com.example.socialmedia.fragments.Profile;
import com.example.socialmedia.fragments.add;
import com.example.socialmedia.fragments.notification;
import com.example.socialmedia.fragments.search;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    private Animation slideIn, slideOut, fadeIn, fadeOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(" ");
        Bundle intent = getIntent().getExtras();
        if (intent != null){
            String publisher = intent.getString("publisherid");

            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
            editor.putString("profileid", publisher);
            editor.apply();

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new Profile()).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new Home()).commit();
        }
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //actionBar.setDisplayHomeAsUpEnabled(true);
        //toolbar.setDisplayShowHomeEnabled(true);
        auth = FirebaseAuth.getInstance();
        slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in);
        slideOut = AnimationUtils.loadAnimation(this, R.anim.slide_out);
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        Home fragment1 = new Home();
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.fragment_container, fragment1);
        ft1.commit();
        BottomNavigationView navigationView = findViewById(R.id.bottomnavigationview);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {


            if (item.getItemId() == R.id.menu_home) {
                Home fragment1 = new Home();
                FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                ft1.replace(R.id.fragment_container, fragment1);
                findViewById(R.id.fragment_container).startAnimation(fadeIn);
                ft1.commit();
                return true;
            }
            else if (item.getItemId() == R.id.menu_search) {
                search fragment2 = new search();
                FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                ft2.replace(R.id.fragment_container, fragment2);
                //findViewById(R.id.fragment_container).startAnimation(fadeIn);
                ft2.commit();
                return true;
            }
            else if (item.getItemId() == R.id.menu_add) {
                add fragment3 = new add();
                FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
                ft3.replace(R.id.fragment_container, fragment3);
                findViewById(R.id.fragment_container).startAnimation(fadeIn);
                ft3.commit();
                return true;
            }
            else if (item.getItemId() == R.id.menu_likes) {
                notification fragment3 = new notification();
                FragmentTransaction ft4 = getSupportFragmentManager().beginTransaction();
                ft4.replace(R.id.fragment_container, fragment3);
                findViewById(R.id.fragment_container).startAnimation(fadeIn);
                ft4.commit();
                return true;
            }
            else if (item.getItemId() == R.id.menu_account) {
                Log.i("users","update start");
                SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                editor.apply();
                Log.i("users","update successfull");

                Profile fragment3 = new Profile();
                FragmentTransaction ft5 = getSupportFragmentManager().beginTransaction();
                ft5.replace(R.id.fragment_container, fragment3);
                findViewById(R.id.fragment_container).startAnimation(fadeIn);
                ft5.commit();
                return true;
            }


            return false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            auth.signOut();
            startActivity(new Intent(MainActivity.this, login.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    private void animateIconTextVisibility(final View iconView, final View textView) {
        iconView.startAnimation(fadeOut);
        textView.startAnimation(fadeIn);
        iconView.setVisibility(View.GONE);
        textView.setVisibility(View.VISIBLE);
    }
}
