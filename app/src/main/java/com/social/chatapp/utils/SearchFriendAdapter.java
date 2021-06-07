package com.social.chatapp.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.social.chatapp.R;
import com.social.chatapp.storagedata.Profilex;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SearchFriendAdapter extends RecyclerView.Adapter<SearchFriendAdapter.ViewHolder> {

    private List<Profilex> list = new ArrayList<>();
    private ChatAdapter.OnclickChat listener ;


    public SearchFriendAdapter(ChatAdapter.OnclickChat listener){
        this.listener = listener ;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.searchresult, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {

        holder.name.setText(list.get(position).getName());

        Glide.with(holder.profileImage)
                .load(list.get(position).getProfilePicture())
                .placeholder(R.drawable.placeholder)
                .into(holder.profileImage);

        holder.item.setOnClickListener(room->{
            //todo change
            Profilex profilex = new Profilex(list.get(position).getName(),list.get(position).getEmail()
            ,list.get(position).getProfilePicture());
            listener.onclick(profilex);
        });
    }


    public void addList(List<Profilex> list) {
        this.list = list;
        notifyDataSetChanged();

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView profileImage;
        View item ;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.userNameProfile);
            profileImage = itemView.findViewById(R.id.userImageProfile);
            item = itemView ;
        }
    }
}
