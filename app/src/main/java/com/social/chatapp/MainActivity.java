package com.social.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.social.chatapp.databinding.ActivityMainBinding;
import com.social.chatapp.fragments.ChatFragment;
import com.social.chatapp.notifications.FcmNotificationsSender;
import com.social.chatapp.notifications.FirebaseMessagingService;
import com.social.chatapp.storagedata.MyResponse;
import com.social.chatapp.storagedata.NotificationData;
import com.social.chatapp.storagedata.PushNotification;


import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final int RC_PHOTO_PICKER = 2;
    private ActivityMainBinding binding;
    private final String Ch = "1";

    private String MY_EMAIL;
    private ImageButton moreButton;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ChatFragment chatFragment;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
      //  FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    //    sendNotification("eyJhbGciOiJSUzI1NiIsImtpZCI6IjMwMjUxYWIxYTJmYzFkMzllNDMwMWNhYjc1OTZkNDQ5ZDgwNDI1ZjYiLCJ0eXAiOiJKV1QifQ.eyJuYW1lIjoibWFuaXNoIHNoYXJtYSIsInBpY3R1cmUiOiJodHRwczovL2xoMy5nb29nbGV1c2VyY29udGVudC5jb20vYS9BQVRYQUp3YUhfRm44T043bUYwRmhfNkJNRlVXRjNNTU5YN1BFS1ZqRDkwQz1zOTYtYyIsImlzcyI6Imh0dHBzOi8vc2VjdXJldG9rZW4uZ29vZ2xlLmNvbS9zb2NpYWwtbWVkaWEtZDM5ZmUiLCJhdWQiOiJzb2NpYWwtbWVkaWEtZDM5ZmUiLCJhdXRoX3RpbWUiOjE2MjIzOTQxOTgsInVzZXJfaWQiOiJhaXhtc05md0ZwUmlMelMyVm0zU0p0cnVuUnQxIiwic3ViIjoiYWl4bXNOZndGcFJpTHpTMlZtM1NKdHJ1blJ0MSIsImlhdCI6MTYyMjQ3NTcyOSwiZXhwIjoxNjIyNDc5MzI5LCJlbWFpbCI6ImxldG1la2lja3lvdXJhc3N5b0BnbWFpbC5jb20iLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiZmlyZWJhc2UiOnsiaWRlbnRpdGllcyI6eyJnb29nbGUuY29tIjpbIjExNjQ5NTY1MzAxNTU5NTkyNTc0NSJdLCJlbWFpbCI6WyJsZXRtZWtpY2t5b3VyYXNzeW9AZ21haWwuY29tIl19LCJzaWduX2luX3Byb3ZpZGVyIjoiZ29vZ2xlLmNvbSJ9fQ.ZIuSbTOd-BGd2VCVAvj4MT68kout9uVNqpIuIkFVe_WK0ZAMKDrPFhIrs1mvQGRF6XkKiKmXcpEKmICmQClwW3XdyARUbiu7d3uELiRTtujnza-q03JXycSUvxA5dXJhz5YVL1vvOXu0jsT5T0pammHyyCoCImrJ7JGx7hDDIRw1UFV7QME7K3cjXVFj7NCT2fCbqSqW-hVB0MCJ4pC4S4tUOwZ0lOewoZ-k1US9qHLT81lXSCai4CnxYhucl5WH-scJJx_lcOzGFohLBb8cSm5-6Ejckw-xHKYWsImOX3P7Wmnh0zhgQRoWX7gXctKuFp0oFKOCeAT4pFr0OY70oQ");


        Objects.requireNonNull(getSupportActionBar()).hide();
        getUser();
        tabLayout = binding.tabLayout;
        moreButton = binding.moreImageButton;
        viewPager = binding.mainFragmentFrame;
        MainAdapter adapter = new MainAdapter(getSupportFragmentManager(), getLifecycle());

        viewPager.setAdapter(adapter);
        tabLayout.addTab(tabLayout.newTab().setText("Chats"));
        tabLayout.addTab(tabLayout.newTab().setText("Status"));

        FloatingActionButton floatingActionButton = binding.floatingAction;

        //  firebaseStorage = FirebaseStorage.getInstance();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

        moreButton.setOnClickListener(this::showPop);

        floatingActionButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SearchFriends.class);
            intent.putExtra("email", MY_EMAIL);
            startActivity(intent);
        });

    }

    private void showPop(View view) {

        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.settings:
                        Toast.makeText(getApplicationContext(), "settings", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.profile:
                        Intent intent = new Intent(getApplicationContext(), ProfileActiviry.class);
                        intent.putExtra("email",MY_EMAIL);
                        startActivity(intent);
                        return true;

                }
                return false;
            }
        });
        popupMenu.inflate(R.menu.more_menu);
        popupMenu.show();
    }


    private void getUser() {
        //  MY_EMAIL = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
        MY_EMAIL = getIntent().getStringExtra("email");
        chatFragment = new ChatFragment(MY_EMAIL);

    }


    private class MainAdapter extends FragmentStateAdapter {


        public MainAdapter(@NonNull @NotNull FragmentManager fragmentManager, @NonNull @NotNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @NotNull
        @Override
        public Fragment createFragment(int position) {

            if (position == 1) {
                // status fragment
            }
            return chatFragment;
        }

        @Override
        public int getItemCount() {
            return 1;
        }
    }



}