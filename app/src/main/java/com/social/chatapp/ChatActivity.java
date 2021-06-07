package com.social.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.social.chatapp.databinding.ActivityChatBinding;
import com.social.chatapp.notifications.FcmNotificationsSender;
import com.social.chatapp.storagedata.ChatItem;
import com.social.chatapp.storagedata.ChatMessage;
import com.social.chatapp.storagedata.DataBaseContract;
import com.social.chatapp.storagedata.Profilex;
import com.social.chatapp.utils.MessagesAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private String ref;
    private final String CHAT = "chats";
    private String My_EMAIL;
    private Profilex profilex;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference chatRef;
    private MessagesAdapter adapter;
    private RecyclerView recyclerView;
    private DatabaseReference chatRoom;
    private DatabaseReference refChatRoom;
    private Profilex MyProfile;
    private String userToken = null;
    private DatabaseReference tokenRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).hide();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference().keepSynced(true);
        getExtra();
        My_EMAIL = DataBaseContract.convertToKey(getIntent().getStringExtra("num"));
        ref = DataBaseContract.convertToKey(profilex.getEmail());

        //DataBaseContract.convertToKey(getIntent().getStringExtra("ref"));
        adapter = new MessagesAdapter(My_EMAIL);

        // for reading chat
        chatRef = firebaseDatabase.getReference(CHAT).child(My_EMAIL).child(ref);
        chatRoom = firebaseDatabase.getReference(DataBaseContract.chatRoom).child(My_EMAIL).child(ref);
        refChatRoom = firebaseDatabase.getReference(DataBaseContract.chatRoom).child(ref).child(My_EMAIL);
        chatRef.addChildEventListener(childEventListener);
        tokenRef = firebaseDatabase.getReference(DataBaseContract.user).child(ref).child("token");
        ImageButton button = binding.sendmessagebutton;

        recyclerView = binding.chatRecycleView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
       // linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        button.setOnClickListener(view -> {
            // todo not implement for image yet
            EditText editText = binding.messageInput;
            String text = editText.getText().toString().trim();
            if (text.length() == 0) {
                return;
            }
            ChatMessage chatMessage = new ChatMessage(text, String.valueOf(new Date().getTime())
                    , My_EMAIL, ref, "0", null, null);

            ChatItem chatItem = new ChatItem(ref, profilex.getEmail(), profilex.getProfilePicture(), chatMessage, profilex.getName());
            ChatItem chatItem1 = new ChatItem(My_EMAIL, My_EMAIL, null, chatMessage, MyProfile.getName());

            firebaseDatabase.getReference(CHAT).child(My_EMAIL).child(ref).push().setValue(chatMessage);
            firebaseDatabase.getReference(CHAT).child(ref).child(My_EMAIL).push().setValue(chatMessage);
            chatRoom.setValue(chatItem);
            refChatRoom.setValue(chatItem1);

            if (userToken == null) {
                valueEventListenerToken = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            userToken = snapshot.getValue(String.class);
                            FcmNotificationsSender notificationsSender = new
                                    FcmNotificationsSender(
                                    userToken, profilex.getName(), chatMessage.getText(), ChatActivity.this,
                                    ChatActivity.this);
                            notificationsSender.SendNotifications();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                };
                tokenRef.addValueEventListener(valueEventListenerToken);

            } else {
                FcmNotificationsSender notificationsSender = new
                        FcmNotificationsSender(
                        userToken, profilex.getName(), chatMessage.getText(), this, this);
                notificationsSender.SendNotifications();
            }
            editText.setText("");
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                firebaseDatabase.getReference(DataBaseContract.user).child(My_EMAIL)
                        .child(DataBaseContract.profilePicture)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                                String pic = snapshot.getValue(String.class);
                                refChatRoom.child(DataBaseContract.profilePicture).setValue(pic);
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                            }
                        });
            }
        }).start();

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (valueEventListenerToken != null) {
            tokenRef.removeEventListener(valueEventListenerToken);
        }
    }

    private void getExtra() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("extra");
        profilex = new Profilex(bundle.getString("name"), bundle.getString("email"), bundle.getString("profilePicture"));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        assert user != null;
        MyProfile = new Profilex(Objects.requireNonNull(user.getDisplayName()),
                Objects.requireNonNull(user.getEmail()), null);

        ImageView pic = binding.userImage;
        TextView name = binding.username;
        name.setText(profilex.getName());
        Glide.with(pic).load(profilex.getProfilePicture()).placeholder(R.drawable.placeholder).into(pic);
    }


    private ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            try {
                ChatMessage chatMessage = snapshot.getValue(ChatMessage.class);
              //  recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);

                adapter.addmessage(chatMessage);
                recyclerView.scrollToPosition(adapter.getItemCount()-1);
               recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);

            } catch (Exception e) {

                Log.e("child Listener", e.getMessage());
            }
        }

        @Override
        public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

        }

        @Override
        public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {

        }

        @Override
        public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

        }

        @Override
        public void onCancelled(@NonNull @NotNull DatabaseError error) {

        }
    };
    private ValueEventListener valueEventListenerToken;
}