package com.eclev.lawrence.essencia;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignIN extends AppCompatActivity {
    private EditText userLoginEmail, userLoginPass;
    private TextView signup;
    private Button login;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mdatabaseRef;
    private SignInButton googleButton;
    private static final int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleApiClient;
    private final static String TAG = "LoginActivity";
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        userLoginEmail = (EditText) findViewById(R.id.et_user_login_email);
        userLoginPass = (EditText) findViewById(R.id.et_user_login_pass);
        signup = (TextView) findViewById(R.id.tv_signUp);
        login = (Button) findViewById(R.id.btn_login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mdatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mdatabaseRef.keepSynced(true);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = userLoginEmail.getText().toString();
                String pass = userLoginPass.getText().toString();
                if(!TextUtils.isEmpty(email)&& !TextUtils.isEmpty(pass)){
                    mFirebaseAuth.signInWithEmailAndPassword(email,pass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                    doesUserEvenExistInDB();
//                                        Intent mainIntent = new Intent(SignIN.this, MainActivity.class);
//                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                        startActivity(mainIntent);
                                    }else{
                                        Toast.makeText(SignIN.this, "Invalid Email ID or Password",
                                                Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }

            }
        });
    }

    private void doesUserEvenExistInDB() {
        if(mFirebaseAuth.getCurrentUser() != null){
            final String User_id = mFirebaseAuth.getCurrentUser().getUid();
            mdatabaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild(User_id)){
                        Intent mainIntent = new Intent(SignIN.this, MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                    }else{
                        Toast.makeText(SignIN.this, "U might need to setup account", Toast.LENGTH_SHORT).show();
                        // i dont have an activity for that yet
                        // Normally i would create the activity or send user to register
                        Intent newUserIntent = new Intent(SignIN.this, SetUpAccountActivity.class);
                        newUserIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(newUserIntent);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
