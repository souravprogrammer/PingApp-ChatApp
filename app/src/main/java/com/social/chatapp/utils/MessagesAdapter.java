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
import com.social.chatapp.storagedata.ChatMessage;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {


    private ArrayList<ChatMessage> chatList = new ArrayList<>();
    private final String num;
    private final int SEND = 1, Rec = 2;

    public MessagesAdapter(final String num) {
        this.num = num;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == SEND) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sendermessage, parent, false);

        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recivermessagelayout, parent, false);
        }
        return new ViewHolder(view);

    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = chatList.get(position);

        if (message.receiver.equals(num)) {
            return Rec;
        } else {
            return SEND;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        String msg = chatList.get(position).getText();
        String img = chatList.get(position).getImageUrl();
        if (msg != null) {
            holder.textMessage.setText(msg);
            holder.textMessage.setVisibility(View.VISIBLE);
        } else if (img != null) {
            holder.imageMessage.setVisibility(View.VISIBLE);
            Glide.with(holder.imageMessage).load(img)
                    .into(holder.imageMessage);
        }

        holder.messageTime.setText(convertTime(chatList.get(position).getTimestamp()));

    }

    private String convertTime( String timemili ) {

        Date date = new Date();

        date.setTime(Long.parseLong(timemili)) ;
        SimpleDateFormat simpleFormatter = new SimpleDateFormat("dd-M-yyyy");

        String time  = simpleFormatter.format(date);
        if (simpleFormatter.format(System.currentTimeMillis()).equals(time)) {

            time =  new SimpleDateFormat("hh:mm a").format(date);
        } else {
            time =   simpleFormatter.format(date);
        }

        return time;

    }
    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public void addmessage(ChatMessage chatMessage) {
        chatList.add(chatMessage);
        //notifyItemInserted(0);
        notifyItemChanged(getItemCount());
    }

    public void addmessage(ArrayList<ChatMessage> chatList) {
        this.chatList = chatList;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage;
        ImageView imageMessage;
        TextView messageTime ;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.textMesageField);
            imageMessage = itemView.findViewById(R.id.imageMessageField);
            messageTime = itemView.findViewById(R.id.messagetime);
        }
    }
}
