package com.example.socialmedia;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.text.TextUtils;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class register extends AppCompatActivity {

    // Declare the views
    EditText editTextEmail,editTextName;
    EditText editTextPassword;
    EditText editTextRePassword;
    ImageView imageView;
    Button registerButton;
    String pattern="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    FirebaseAuth auth;
    ProgressDialog progressDialog;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Create Account");

        //actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        // Initialize the views
        textView=findViewById(R.id.textView);
        editTextEmail = findViewById(R.id.editTextText);
        registerButton = findViewById(R.id.button);
        editTextPassword=findViewById(R.id.editTextTextPassword);
        editTextRePassword=findViewById(R.id.editTextTextPassword1);
        editTextName=findViewById(R.id.editTextText1);
        auth=FirebaseAuth.getInstance();

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Registering User...");
        // Apply window insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(register.this,login.class));
                finish();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_email=editTextEmail.getText().toString();
                String txt_password=editTextPassword.getText().toString();
                String txt_repassword=editTextRePassword.getText().toString();
                String txt_name=editTextName.getText().toString();
                if(TextUtils.isEmpty(txt_email)||TextUtils.isEmpty(txt_password)||TextUtils.isEmpty(txt_repassword)||TextUtils.isEmpty(txt_name)){
                    Toast.makeText(register.this,"Please enter all the fields",Toast.LENGTH_SHORT).show();

                } else if (txt_password.length()<6) {
                    Toast.makeText(register.this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
                } else if (!txt_password.equals(txt_repassword))  {
                    editTextRePassword.setError("Passwords do not match");
                }
                else if(!txt_email.matches(pattern)){
                    editTextEmail.setError("Invalid email address");
                }
                else{
                    register(txt_name,txt_email,txt_password);
                }
            }
        });
    }

    private void register(String name,String email,String password){
        progressDialog.show();
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    FirebaseUser fuser=auth.getCurrentUser();
                    fuser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                String email=fuser.getEmail();
                                String uid=fuser.getUid();
                                //Stirng name=fuser.getDisplayName();
                                HashMap<String,String> hashMap=new HashMap<>();
                                hashMap.put("email",email);
                                hashMap.put("uid",uid);
                                hashMap.put("name", name);
                                hashMap.put("profile","https://firebasestorage.googleapis.com/v0/b/socialmedia-b9148.appspot.com/o/images.jpg?alt=media&token=d469e478-bd31-45de-a9c3-39ac9c96f9e8");
                                hashMap.put("password",password);
                                //Users(email,uid,name,profile,password);
                                //hashMap.put("cover","");
                                DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users").child(uid);
                                ref.setValue(hashMap);
                                Toast.makeText(register.this,"Registration successful. Verification email sent to your email address.",Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(register.this,"Failed to send verification email.",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    Toast.makeText(register.this,"Registration successful. Please verify your email address to continue.",Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(register.this,"Registration failed",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser fuser=auth.getCurrentUser();
        if(fuser!=null && fuser.isEmailVerified()){
            startActivity(new Intent(register.this,MainActivity.class));
            finish();
        }
    }
}