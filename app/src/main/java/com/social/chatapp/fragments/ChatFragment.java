package com.social.chatapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.social.chatapp.ChatActivity;
import com.social.chatapp.R;
import com.social.chatapp.SearchFriends;
import com.social.chatapp.storagedata.ChatItem;
import com.social.chatapp.storagedata.DataBaseContract;
import com.social.chatapp.storagedata.Profilex;
import com.social.chatapp.utils.ChatAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private static String MY_EMAIL;
    private List<ChatItem> list = new ArrayList<>();
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference chatRef;
    private DatabaseReference userProfile;
    private ConstraintLayout constraintLayout;
    private Button findFriends;

    public ChatFragment(@NonNull String email) {
        MY_EMAIL = email;

    }

    public ChatFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = view.findViewById(R.id.chatRefRecycleView);
        firebaseDatabase = FirebaseDatabase.getInstance();
        try {
            firebaseDatabase.setPersistenceEnabled(true);
        } catch (Exception e) {
            Log.e("precedence", e.getMessage());
        }

        chatRef = firebaseDatabase.getReference(DataBaseContract.chatRoom).child(DataBaseContract.convertToKey(MY_EMAIL));
        chatRef.keepSynced(true);
        chatRef.limitToLast(200).addChildEventListener(childEventListener);
        userProfile = firebaseDatabase.getReference(DataBaseContract.user);
        adapter = new ChatAdapter(new ChatAdapter.OnclickChat() {
            @Override
            public void onclick(@NotNull Profilex room) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        /**for updating the profile picture of a user*/
                        userProfile.child(DataBaseContract.convertToKey(room.getEmail()))
                                .child(DataBaseContract.profilePicture)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {


                                        if (snapshot.exists()) {
                                            String profile = snapshot.getValue(String.class);

                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    DatabaseReference up = firebaseDatabase.
                                                            getReference(DataBaseContract.chatRoom).
                                                            child(DataBaseContract.convertToKey(MY_EMAIL))
                                                            .child(DataBaseContract.convertToKey(room.getEmail()))
                                                            .child(DataBaseContract.profilePicture);
                                                    up.setValue(profile);
                                                }
                                            }).start();

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                    }
                                });


                    }
                }).start();


                Intent intent = new Intent(getContext(), ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("name", room.getName());
                bundle.putString("email", room.getEmail());
                bundle.putString("profilePicture", room.getProfilePicture());
                intent.putExtra("extra", bundle);
                //  intent.putExtra("ref", room);
                intent.putExtra("num", MY_EMAIL);
                startActivity(intent);
            }
        });

        adapter.addimageclicklistnner(true, getChildFragmentManager());
        constraintLayout = view.findViewById(R.id.no_chat);
        findFriends = view.findViewById(R.id.find_friends);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        findFriends.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SearchFriends.class);
            intent.putExtra("email", MY_EMAIL);
            startActivity(intent);
        });
        return view;
    }

    private final ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot,
                                 @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
            ChatItem chatItem = snapshot.getValue(ChatItem.class);
            if (chatItem != null) {
                list.add(chatItem);
                adapter.addList(list);

                if (list.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    constraintLayout.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    constraintLayout.setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot,
                                   @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
            ChatItem chatItem = snapshot.getValue(ChatItem.class);
            //todo change this function
            if (chatItem != null) {
                //  list.add(chatItem);
                //  adapter.setList(list);
                List<ChatItem> list = adapter.getList();

                for (int i = 0; i < list.size(); i++) {
                    if (chatItem.getEmail().equals(list.get(i).getEmail())) {
                        list.remove(i);
                        list.add(i, chatItem);
                        break;
                    }
                }
                adapter.addList(list);
                adapter.notifyDataSetChanged();
            }
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


}
