package com.example.socialmedia;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.EventListener;

public class login extends AppCompatActivity {
    EditText editTextEmail;
    EditText editTextPassword;
    Button loginButton;
    TextView textView,textView1;

    String pattern="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Login");
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        auth=FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.editTextText);
        loginButton = findViewById(R.id.button);
        editTextPassword=findViewById(R.id.editTextTextPassword);
        textView=findViewById(R.id.textView);
        textView1=findViewById(R.id.textView3);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(login.this,register.class));
                finish();
            }
        });
        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(login.this,forgotpassword.class));

            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_email=editTextEmail.getText().toString();
                String txt_password=editTextPassword.getText().toString();
                if(TextUtils.isEmpty(txt_email)||TextUtils.isEmpty(txt_password)){
                    Toast.makeText(login.this,"Please enter all the fields",Toast.LENGTH_SHORT).show();

                }
                else if(!txt_email.matches(pattern)){
                    editTextEmail.setError("Invalid email address");
                }
                else{
                    login(txt_email,txt_password);
                }
            }
        });
    }
    private void login(String email,String password) {
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(login.this, MainActivity.class);
                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                }
                else{
                    Toast.makeText(login.this,"Authentication failed",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


}