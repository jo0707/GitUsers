package com.example.gitusers.favorite

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.gitusers.R
import com.example.gitusers.detail.DetailActivity
import com.example.gitusers.helper.SetDatabaseHelper
import com.example.gitusers.utility.SimpleUser
import com.example.gitusers.widget.FavoriteWidget
import kotlinx.android.synthetic.main.user_item.view.*

class FavoriteAdapter: RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {
    private val userList = ArrayList<SimpleUser>()

    fun setData(items: ArrayList<SimpleUser>) {
        userList.apply {
            clear()
            addAll(items)
        }
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int = userList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(userList[position], position)

        with(holder.itemView) {
            setOnClickListener {
                val goToDetail = Intent(context, DetailActivity::class.java)
                goToDetail.putExtra(DetailActivity.USER_URL_KEY, userList[position].userUrl)
                context.startActivity(goToDetail)
            }
        }
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

                itemFavoriteButton.apply {
                    setImageResource(R.drawable.ic_favorite_24)

                    setOnClickListener {
                        SetDatabaseHelper.deleteFromDatabase(context, simpleUser.username)

                        userList.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, userList.size)
                        this.setImageResource(R.drawable.ic_favorite_empty_24)

                        val appWidgetManager    = AppWidgetManager.getInstance(context.applicationContext)
                        val favoriteWidget      = ComponentName(context.applicationContext, FavoriteWidget::class.java)
                        val appWidgetIds        = appWidgetManager.getAppWidgetIds(favoriteWidget)
                        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.favoriteStackView)
                    }
                } // favoriteButton.apply
            } // with itemView
        } // fun bind()
    } // class ViewHolder

}

