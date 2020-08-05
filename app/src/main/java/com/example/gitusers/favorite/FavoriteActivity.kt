package com.example.gitusers.favorite

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gitusers.R
import kotlinx.android.synthetic.main.activity_favorite.*
import kotlinx.android.synthetic.main.main_toolbar.*

class FavoriteActivity : AppCompatActivity() {

    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var favoriteAdapter: FavoriteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        setToolbar()
        showLoading()

        favoriteAdapter = FavoriteAdapter()
        favoriteAdapter.notifyDataSetChanged()

        favoriteRecyclerView.apply {
            layoutManager   = LinearLayoutManager(this@FavoriteActivity)
            adapter         = favoriteAdapter
        }

        favoriteViewModel   = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(FavoriteViewModel::class.java)
        setViewModelObserver()
    }

    override fun onResume() {
        super.onResume()
        setViewModelObserver()
    }

    private fun setViewModelObserver() {
        favoriteViewModel.apply {
            searchFavoriteList(this@FavoriteActivity)

            getFavoriteList().observe(this@FavoriteActivity, Observer { favoriteList ->
                if (favoriteList.size > 0) {
                    favoriteAdapter.setData(favoriteList)
                    showRecyclerView()
                } else
                    showNoUserMessage()
            })
        }
    }

    private fun setToolbar() {
        toolbarTitle.text   = getString(R.string.favorite)

        (favoriteToolbar as Toolbar).apply {
            setNavigationIcon(R.drawable.ic_back_white_24)
            setNavigationOnClickListener { finish() }
        }
    }

    private fun showLoading() {
        favoriteProgressBar.visibility  = View.VISIBLE
        favoriteRecyclerView.visibility = View.INVISIBLE
        favoriteStatusText.visibility   = View.INVISIBLE
    }

    private fun showRecyclerView() {
        favoriteProgressBar.visibility  = View.INVISIBLE
        favoriteRecyclerView.visibility = View.VISIBLE
        favoriteStatusText.visibility   = View.INVISIBLE
    }

    private fun showNoUserMessage() {
        favoriteProgressBar.visibility  = View.INVISIBLE
        favoriteRecyclerView.visibility = View.INVISIBLE
        favoriteStatusText.visibility   = View.VISIBLE
    }

}