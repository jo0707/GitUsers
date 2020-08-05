package com.example.gitconsumer.main

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.gitconsumer.R
import com.example.gitconsumer.database.DatabaseContract.NoteColums.Companion.CONTENT_URI
import com.example.gitconsumer.utility.SimpleUser
import kotlinx.android.synthetic.main.user_item.view.*

class MainAdapter: RecyclerView.Adapter<MainAdapter.ViewHolder>() {
    private val userList = ArrayList<SimpleUser>()

    fun setData(items: ArrayList<SimpleUser>) {
        userList.clear()
        userList.addAll(items)
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int = userList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(userList[position], position)
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(simpleUser: SimpleUser, position: Int) {
            with(itemView) {
                usernameContainer.text  = simpleUser.username
                userIdContainer.text    = context.getString(R.string.id_template, simpleUser.userId)

                Glide.with(itemView.context)
                    .load(simpleUser.imageUrl)
                    .apply(RequestOptions().override(128))
                    .into(imageContainer)

                favoriteButton.apply {
                    setImageResource(R.drawable.ic_favorite_24)

                    setOnClickListener {
                        val usernameUri = Uri.parse("$CONTENT_URI/${simpleUser.username}")
                        context.contentResolver.delete(usernameUri, null, null)

                        userList.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, userList.size)
                        this.setImageResource(R.drawable.ic_favorite_empty_24)
                    }
                } // favoriteButton.apply
            } // with itemView
        } // fun bind()
    } // class ViewHolder
}