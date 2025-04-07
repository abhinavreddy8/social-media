package com.example.socialmedia;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class forgotpassword extends AppCompatActivity {
    EditText editTextEmail;
    Button loginButton;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgotpassword);
        editTextEmail = findViewById(R.id.editTextText);
        loginButton = findViewById(R.id.button);
        auth = FirebaseAuth.getInstance();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTextEmail.getText().toString().isEmpty()){
                    Toast.makeText(forgotpassword.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                }
                else{
                   reset(editTextEmail.getText().toString());
                }
            }
        });
    }
    public void reset(String email){
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(forgotpassword.this, "Check your Email", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(forgotpassword.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}