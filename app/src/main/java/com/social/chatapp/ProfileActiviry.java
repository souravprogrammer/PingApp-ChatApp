package com.social.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.social.chatapp.storagedata.DataBaseContract;
import com.social.chatapp.utils.ImageDialog;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ProfileActiviry extends AppCompatActivity {


    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private String MY_EMAIL;
    private final int RC_PHOTO_PICKER = 140;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference profilePic;
    private ImageView imageView;
    private FirebaseAuth mAuth;
    TextView name;
    TextView email;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_activiry);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Profile");
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getColor(R.color.light_blue)));
        mAuth = FirebaseAuth.getInstance();

        MY_EMAIL = DataBaseContract.convertToKey(getIntent().getStringExtra("email"));
        name = findViewById(R.id.profileusername);
        email = findViewById(R.id.userEmail);


        firebaseStorage = FirebaseStorage.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        // firebaseDatabase.setPersistenceEnabled(true);

        storageReference = firebaseStorage.getReference(MY_EMAIL);
        imageView = findViewById(R.id.profile_image);
        imageView.setOnClickListener(v -> {
            ImageDialog dialog = new ImageDialog(imageView.getDrawable());
            dialog.show(getSupportFragmentManager(),"profilePhoto");

        });


        ImageView button = findViewById(R.id.picPhotoAdd);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/jpeg");
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
        });
        profilePic = FirebaseDatabase.getInstance()
                .getReference(DataBaseContract.user)
                .child(MY_EMAIL).child(DataBaseContract.profilePicture);

        profilePic.addListenerForSingleValueEvent(valueEventListener);


    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);

    }

    @Override
    protected void onPause() {
        super.onPause();
        profilePic.addListenerForSingleValueEvent(valueEventListener);
        mAuth.removeAuthStateListener(authStateListener);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        findViewById(R.id.photoProgressbar).setVisibility(View.VISIBLE);

        if (resultCode == Activity.RESULT_CANCELED && requestCode == RC_PHOTO_PICKER) {
            findViewById(R.id.photoProgressbar).setVisibility(View.GONE);
            Snackbar.make(imageView, " Photo failed Updated", Snackbar.LENGTH_LONG).show();
        }


        if (resultCode == Activity.RESULT_OK && requestCode == RC_PHOTO_PICKER) {
            if (data != null) {
                Uri uri = data.getData();

                if (uri != null) {
                    ImageView imageView = findViewById(R.id.profile_image);
                    //  Glide.with(getApplicationContext()).load("uri").placeholder(R.drawable.placeholder).into(imageView);
                    storageReference
                            .child(DataBaseContract.profilePicture)
                            .putFile(uri)
                            .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            if (uri != null) {
                                                DatabaseReference Q = firebaseDatabase.getReference(DataBaseContract.user)
                                                        .child(MY_EMAIL).child("profilePicture");
                                                Q.setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        //Snackbar.make()
                                                        findViewById(R.id.photoProgressbar).setVisibility(View.GONE);
                                                        Snackbar.make(imageView, "Photo Updated", Snackbar.LENGTH_LONG).show();
                                                    }
                                                });
                                            }
                                        }
                                    });
                                    // HashMap<String,Uri> profile = new HashMap<>();
                                    //  profile.put("profilePicture",uri);
                                }
                            });
                }
            }
        }
    }

    private final ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

            String url = snapshot.getValue(String.class);
            if (url != null) {
                Glide.with(getApplicationContext()).load(url)
                        .placeholder(R.drawable.placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .into(imageView);
            }
        }

        @Override
        public void onCancelled(@NonNull @NotNull DatabaseError error) {

        }
    };

    private final FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull @NotNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                name.setText(user.getDisplayName());
                email.setText(user.getEmail());
            }
        }
    };
}