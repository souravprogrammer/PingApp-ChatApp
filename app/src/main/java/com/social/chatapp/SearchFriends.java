package com.social.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.social.chatapp.storagedata.ChatItem;
import com.social.chatapp.storagedata.DataBaseContract;
import com.social.chatapp.storagedata.Profile;
import com.social.chatapp.storagedata.Profilex;
import com.social.chatapp.utils.ChatAdapter;
import com.social.chatapp.utils.SearchFriendAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class SearchFriends extends AppCompatActivity {


    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference user;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private long timeSinceLastRequest;
    private SearchFriendAdapter adapter;
    private String MY_EMAIL;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friends);
       Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getColor(R.color.light_blue)));
        Objects.requireNonNull(getSupportActionBar()).setTitle("Search friends");
        MY_EMAIL = DataBaseContract.convertToKey(getIntent().getStringExtra("email"));

        RecyclerView recyclerView = findViewById(R.id.search_recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // recyclerView.setAdapter(adapter);
        firebaseDatabase = FirebaseDatabase.getInstance();
        user = firebaseDatabase.getReference(DataBaseContract.user);


        adapter = new SearchFriendAdapter(new ChatAdapter.OnclickChat() {
            @Override
            public void onclick(@NotNull Profilex room) {


                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("name", room.getName());
                bundle.putString("email", room.getEmail());
                bundle.putString("profilePicture", room.getProfilePicture());
                intent.putExtra("extra", bundle);
                //  intent.putExtra("ref", room);
                intent.putExtra("num", MY_EMAIL);
                startActivity(intent);
                finish();
            }
        });


        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search here");
        timeSinceLastRequest = System.currentTimeMillis();
        // create the Observable
        Observable<String> observableQueryText = Observable
                .create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(final ObservableEmitter<String> emitter) throws Exception {

                        // Listen for text input into the SearchView
                        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String query) {
                                return false;
                            }

                            @Override
                            public boolean onQueryTextChange(final String newText) {
                                if (!emitter.isDisposed()) {
                                    emitter.onNext(newText); // Pass the query to the emitter
                                }
                                return false;
                            }
                        });
                    }
                })
                .debounce(800, TimeUnit.MILLISECONDS) // Apply Debounce() operator to limit requests
                .subscribeOn(Schedulers.io());

        // Subscribe an Observer
        observableQueryText.subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposables.add(d);
            }

            @Override
            public void onNext(String s) {

                timeSinceLastRequest = System.currentTimeMillis();

                sendRequestToServer(s);
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });


        return super.onCreateOptionsMenu(menu);
    }

    private void sendRequestToServer(String email) {

        String key = DataBaseContract.convertToKey(email);
        // TODO make firebase request here
        if (email.length() > 3) {

            Query q = user.orderByChild(DataBaseContract.email).startAt(email).endAt(email + "\uf8ff");
            q.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    List<Profilex> list = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Profilex profile = dataSnapshot.getValue(Profilex.class);
                        if (profile != null) {
                            if (!DataBaseContract.convertToEmail(profile.getEmail())
                                    .toLowerCase().trim().equals(DataBaseContract.convertToEmail(MY_EMAIL)
                                            .toLowerCase().trim())) {
                                list.add(profile);
                            }
                        }
                    }
                    adapter.addList(list);
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });

        }


    }
}