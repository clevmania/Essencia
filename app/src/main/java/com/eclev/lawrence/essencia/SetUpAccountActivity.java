package com.eclev.lawrence.essencia;

import android.content.Intent;
import android.media.Image;
import android.media.tv.TvContract;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class SetUpAccountActivity extends AppCompatActivity {
    private ImageButton userImage;
    private EditText userName;
    private Button btnProfileSetup;
    private static final int gallery_Request = 1;
    private Uri imageUri = null;
    private DatabaseReference mUserReference;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference mStorageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_account);

        userImage = (ImageButton) findViewById(R.id.ib_profile_pix);
        userName = (EditText) findViewById(R.id.et_user_profile_name);
        btnProfileSetup = (Button) findViewById(R.id.btn_userProfile_setup);
        mUserReference = FirebaseDatabase.getInstance().getReference().child("E_Users");
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mStorageReference = mStorage.getReference().child("profileImages");

        btnProfileSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String loggedInUser = userName.getText().toString();
                final String u_id = mAuth.getCurrentUser().getUid();
                if(!TextUtils.isEmpty(loggedInUser) && imageUri != null){
                    StorageReference imgPath = mStorageReference.child(imageUri.getLastPathSegment());
                    imgPath.putFile(imageUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String downloadUri = taskSnapshot.getDownloadUrl().toString();
                            mUserReference.child(u_id).child("name").setValue(loggedInUser);
                            mUserReference.child(u_id).child("image").setValue(downloadUri);

                            Intent mainIntent = new Intent(SetUpAccountActivity.this,
                                    MainActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(mainIntent);
                        }
                    });


                }
            }
        });

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == gallery_Request && resultCode == RESULT_OK){
            // Add the dependency for the image cropping utility
            // its com.theartofdev.edmodo:android-image-cropper:2.3.+
            // you can visit github , github.com/ArthurHub/Android-Image-Cropper

            Uri imageUrl = data.getData();
            CropImage.activity(imageUrl).setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                userImage.setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}





