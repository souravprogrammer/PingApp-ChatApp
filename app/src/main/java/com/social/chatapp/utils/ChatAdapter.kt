package com.social.chatapp.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.social.chatapp.R
import com.social.chatapp.storagedata.ChatItem
import com.social.chatapp.storagedata.Profilex
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatAdapter(val listner: OnclickChat) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    var list: List<ChatItem> = ArrayList()
    var show: Boolean = false
    var fragmentManager: FragmentManager? = null
    fun addimageclicklistnner(show: Boolean, fragmentManager: FragmentManager) {
        this.show = show
        this.fragmentManager = fragmentManager
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return ViewHolder(view)
    }

    private fun convertTime(timemili: String): String {
        val date = Date()
        date.time = timemili.toLong()
        val simpleFormatter = SimpleDateFormat("dd-M-yyyy")
        var time: String = simpleFormatter.format(date)
        time = if (simpleFormatter.format(System.currentTimeMillis()).equals(time)) {
            SimpleDateFormat("hh:mm a").format(date)
        } else {
            simpleFormatter.format(date)
        }

        return time

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.name.text = list[position].name

        if (list[position].lastMessage != null) {
            //TODO change for time
            holder.messagetime.text = list[position].lastMessage?.timestamp?.let { convertTime(it) }
            when {
                list[position].lastMessage?.text != null -> {
                    holder.message.text = list[position].lastMessage?.text
                }
                list[position].lastMessage?.imageUrl != null -> {
                    val x = "image"
                    holder.message.text = x
                }
                list[position].lastMessage?.videoUrl != null -> {
                    val x = "video"
                    holder.message.text = x
                }
            }
        }

        holder.item.setOnClickListener {
            if(list.isNotEmpty()){
            val profile =
                Profilex(list[position].name, list[position].email, list[position].profilePicture)
            listner.onclick(profile)
            }
        }


        if (list[position].profilePicture != null) {
            Glide.with(holder.imageView)
                .load(R.drawable.placeholder)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .load(list[position].profilePicture).into(holder.imageView)
        }


        holder.imageView.setOnClickListener {
            if (show) {
                val drawable = holder.imageView.drawable;
                val imageDialog = ImageDialog(drawable)
                if (fragmentManager != null)
                    fragmentManager?.let { it1 -> imageDialog.show(it1, "photo") }
            }
        }


    }

    fun addList(list: List<ChatItem>) {
        this.list = list
    }

    fun updateItem(item: ChatItem) {

    }

    fun getlist(): List<ChatItem> {
        return list
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder : RecyclerView.ViewHolder {
        lateinit var message: TextView
        lateinit var messagetime: TextView
        lateinit var name: TextView
        lateinit var item: View
        lateinit var imageView: ImageView

        constructor(itemView: View) : super(itemView) {
            item = itemView
            name = itemView.findViewById(R.id.username)
            message = itemView.findViewById(R.id.userlastmessage)
            imageView = itemView.findViewById(R.id.userImage)
            messagetime = itemView.findViewById(R.id.unreadmesagetime)
        }
    }

    interface OnclickChat {
        fun onclick(room: Profilex)
    }
}