package com.example.gitconsumer.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gitconsumer.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var mainAdapter: MainAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showLoading()

        mainAdapter = MainAdapter()
        mainAdapter.notifyDataSetChanged()

        mainRecyclerView.apply {
            layoutManager   = LinearLayoutManager(this@MainActivity)
            adapter         = mainAdapter
        }

        mainViewModel   = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(MainViewModel::class.java)
        setViewModelObserver()
    }

    override fun onResume() {
        super.onResume()
        setViewModelObserver()
    }

    private fun setViewModelObserver() {
        mainViewModel.searchFavoriteList(this)
        mainViewModel.getFavoriteList().observe(this, Observer { favoriteList ->
            if (favoriteList.size > 0) {
                mainAdapter.setData(favoriteList)
                showRecyclerView()
            } else
                showNoUserMessage()
        })
    }

    private fun showLoading() {
        mainProgressBar.visibility  = View.VISIBLE
        mainRecyclerView.visibility = View.INVISIBLE
        mainStatusText.visibility   = View.INVISIBLE
    }

    private fun showRecyclerView() {
        mainProgressBar.visibility  = View.INVISIBLE
        mainRecyclerView.visibility = View.VISIBLE
        mainStatusText.visibility   = View.INVISIBLE
    }

    private fun showNoUserMessage() {
        mainProgressBar.visibility  = View.INVISIBLE
        mainRecyclerView.visibility = View.INVISIBLE
        mainStatusText.visibility   = View.VISIBLE
    }

}
