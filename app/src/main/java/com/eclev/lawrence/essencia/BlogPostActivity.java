package com.eclev.lawrence.essencia;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Random;

public class BlogPostActivity extends AppCompatActivity {
    private ImageButton blogImageSelect;
    private static final int Image_Request = 1;
    private EditText blogTitle;
    private EditText blogDescription;
    private Button postBtn;
    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseReference;
    private Uri imageUri = null;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_post);

        blogImageSelect = (ImageButton) findViewById(R.id.ib_select_image);
        blogTitle = (EditText) findViewById(R.id.etBlogTitle);
        blogDescription = (EditText) findViewById(R.id.etBlogDescription);
        postBtn = (Button) findViewById(R.id.submit_btn);
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Essencia_Blog");
        mProgressDialog = new ProgressDialog(this);

        blogImageSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,Image_Request);
            }
        });

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postBlogDetails();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Image_Request && resultCode == RESULT_OK){
            imageUri = data.getData();
            blogImageSelect.setImageURI(imageUri);
        }

    }

    public void postBlogDetails(){
        mProgressDialog.setMessage("Sending Posts");

        final String title = blogTitle.getText().toString();
        final String desc = blogDescription.getText().toString();
        if(!TextUtils.isEmpty(title)&& !TextUtils.isEmpty(desc)&& imageUri != null){
            mProgressDialog.show();
            StorageReference filePath = mStorageReference
                    .child("Essencia_Img").child(imageUri.getLastPathSegment());
            filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    DatabaseReference newPost = mDatabaseReference.push();
                    newPost.child("title").setValue(title);
                    newPost.child("description").setValue(desc);
                    newPost.child("Image").setValue(downloadUri.toString());
                    // todo we can also use firebase to get uid and post it too
                    // todo so that later we can know which user posted what
//                    newPost.child("Uid").setValue(FirebaseAuth.getInstance()
//                            .getCurrentUser().getUid());
                    mProgressDialog.dismiss();
                    Toast.makeText(BlogPostActivity.this, "Post Sent", Toast.LENGTH_SHORT).show();
                    // todo create Intent and move user back home...
                    startActivity(new Intent(BlogPostActivity.this,MainActivity.class));

                }
            });
        }
    }

//    public static String random() {
//        Random generator = new Random();
//        StringBuilder randomStringBuilder = new StringBuilder();
//        int randomLength = generator.nextInt(MAX_LENGTH);
//        char tempChar;
//        for (int i = 0; i < randomLength; i++){
//            tempChar = (char) (generator.nextInt(96) + 32);
//            randomStringBuilder.append(tempChar);
//        }
//        return randomStringBuilder.toString();
//    }
}
