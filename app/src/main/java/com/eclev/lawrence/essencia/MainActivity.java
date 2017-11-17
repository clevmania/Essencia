package com.eclev.lawrence.essencia;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private RecyclerView bloglistView;
    // Firebase Database
    private DatabaseReference mDatabaseReference;
    // Firebase Authentication
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bloglistView = (RecyclerView) findViewById(R.id.rv_blog_list);
        bloglistView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        bloglistView.setLayoutManager(layoutManager);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Essencia_Blog");
        mDatabaseReference.keepSynced(true);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                 if (firebaseAuth.getInstance().getCurrentUser() == null){
                     Intent signupIntent = new Intent(MainActivity.this,SignUP.class);
                     signupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                     startActivity(signupIntent);
                 }
            }
        };

        doesUserEvenExistInDB();

    }

    @Override
    protected void onStart() {
        super.onStart();

        mFirebaseAuth.addAuthStateListener(mAuthStateListener);

        FirebaseRecyclerAdapter<Blogs,blogViewHolder> firebaseAdapter =
                new FirebaseRecyclerAdapter<Blogs, blogViewHolder>(
                        Blogs.class,
                        R.layout.blog_rows,
                        blogViewHolder.class,
                        mDatabaseReference
                ) {
                    @Override
                    protected void populateViewHolder(blogViewHolder viewHolder, Blogs model, int position) {
                        final String postedChildKey = getRef(position).getKey();
                        viewHolder.setTitle(model.getTitle());
                        viewHolder.setDesc(model.getDesc());
                        viewHolder.setImaage(getApplicationContext(),model.getImaage());

                        viewHolder.myView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(MainActivity.this, "i am supposed to do something", Toast.LENGTH_SHORT).show();
//                                Intent detailedIntent = new Intent(MainActivity.this, Essencia.class);
//                                detailedIntent.putExtra("D_key", postedChildKey);
//                                startActivity(detailedIntent);
                            }
                        });
                    }
                };
        bloglistView.setAdapter(firebaseAdapter);
    }

    public static class blogViewHolder extends RecyclerView.ViewHolder{
        View myView;
        public blogViewHolder(View itemView) {
            super(itemView);
            myView = itemView;
        }

        public void setTitle(String title){
            TextView blogTitle = (TextView) myView.findViewById(R.id.tv_blog_posted_title);
            blogTitle.setText(title);
        }

        public void setDesc(String desc){
            TextView blogDescription = (TextView) myView.findViewById(R.id.tv_blog_posted_description);
            blogDescription.setText(desc);
        }

        public void setImaage(Context context, String img){
            ImageView blogImage = (ImageView) myView.findViewById(R.id.iv_blog_posted_image);
            Glide.with(context).load(img).into(blogImage);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.mm_add_blog_menu:
                // start intent for the postActivity
                startActivity(new Intent(MainActivity.this,BlogPostActivity.class));
            case R.id.mm_add_settings_menu:
                // settings Page
        }
        return super.onOptionsItemSelected(item);
    }

    private void doesUserEvenExistInDB() {
        if(mFirebaseAuth.getCurrentUser() != null){
            final String User_id = mFirebaseAuth.getCurrentUser().getUid();
            mDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.hasChild(User_id)){
                        // todo i don't have an activity for that yet
                        Intent setupAccountIntent = new Intent(MainActivity.this, SetUpAccountActivity.class);
                        setupAccountIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setupAccountIntent);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
