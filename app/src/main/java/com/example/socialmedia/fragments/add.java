package com.example.socialmedia.fragments;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.socialmedia.MainActivity;
import com.example.socialmedia.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;


public class add extends Fragment {

    private Uri imgUri;
    private ImageView close, img;
    private TextView post;
    private EditText description;
    private StorageTask uploadTask;
    private StorageReference storageRef;
    String url = "";
    public int count=0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        close = view.findViewById(R.id.imageView2);
        img = view.findViewById(R.id.imag);
        post = view.findViewById(R.id.textView2);
        description = view.findViewById(R.id.description);

        storageRef = FirebaseStorage.getInstance().getReference("posts");

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), MainActivity.class);
                startActivity(i);

            }
        });
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"select picture"),10);
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });



        return view;
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void uploadImage() {

        if (imgUri != null){
            final ProgressDialog pd = new ProgressDialog(getActivity());
            pd.setMessage("Posting");
            pd.show();
            final StorageReference fileReference = storageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(imgUri));

            uploadTask = fileReference.putFile(imgUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {

                        Uri downloadUri = task.getResult();
                        url = downloadUri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts");

                        String postid = reference.push().getKey();

                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("postid", postid);
                        hashMap.put("image", url);
                        hashMap.put("description", description.getText().toString());
                        hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());


                        reference.child(postid).setValue(hashMap);

                        pd.dismiss();
                        Intent i = new Intent(getActivity(), MainActivity.class);
                        startActivity(i);

                        //startActivity(new Intent(getActivity(), Home.class));


                    } else {
                        Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(getActivity(), "No image selected", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==10 && resultCode == RESULT_OK && data != null && data.getData() != null){
                imgUri=data.getData();
                img.setImageURI(imgUri);
        }
    }
}
