package com.example.gitusers.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gitusers.R
import com.example.gitusers.favorite.FavoriteActivity
import com.example.gitusers.settings.SettingsActivity
import com.example.gitusers.utility.ApiErrorEvent
import com.example.gitusers.utility.UserAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var mainViewModel: MainViewModel
    private lateinit var searchViewText: TextView
    private lateinit var userAdapter: UserAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSearchView()

        favoriteFab.setOnClickListener(this)
        retryButton.setOnClickListener(this)
        nextPageButton.setOnClickListener(this)
        previousPageButton.setOnClickListener(this)

        setSupportActionBar(mainToolbar as Toolbar?)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        userAdapter = UserAdapter()
        userAdapter.notifyDataSetChanged()

        mainRecyclerView.apply {
            layoutManager   = LinearLayoutManager(this@MainActivity)
            adapter         = userAdapter
        }

        setMainViewModelObserver()
    }

    // refresh recyclerview favorite
    override fun onResume() {
        super.onResume()
        val userlist    = mainViewModel.getUsers().value

        if (userlist != null)
            userAdapter.setData(mainViewModel.refreshFavorite(this, userlist))
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.retryButton -> {
                mainSearchView.setQuery(mainSearchView.query, true)
            }

            R.id.nextPageButton -> {
                mainViewModel.setPageUp(true)
                mainSearchView.setQuery(mainViewModel.prevQuery, true)
            }

            R.id.previousPageButton -> {
                mainViewModel.setPageUp(false)
                mainSearchView.setQuery(mainViewModel.prevQuery, true)
            }

            R.id.favoriteFab -> {
                val goToFavorite    = Intent(this@MainActivity, FavoriteActivity::class.java)
                startActivity(goToFavorite)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menuSettings) {
            val mIntent = Intent(this, SettingsActivity::class.java)
            startActivity(mIntent)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setMainViewModelObserver() {
        mainViewModel   = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(MainViewModel::class.java)

        mainViewModel.apply {
            getError().observe(this@MainActivity, Observer { error ->
                if (error != ApiErrorEvent.NONE) {
                    showErrorInfo(true, error.getErrorResource())
                } else {
                    getUsers().observe(this@MainActivity, Observer { userList ->
                        if (userList != null) {
                            userAdapter.setData(userList)
                            showRecyclerView(true)
                            showPageInfo(true, mainViewModel.getPageInfo())
                        }
                    })
                } // else
            }) // getError.observe
        } // mainViewModel.apply
    } // setMainViewModelObserver()

    private fun setSearchView() {
        searchViewText  = mainSearchView.findViewById(androidx.appcompat.R.id.search_src_text)
        searchViewText.typeface = ResourcesCompat.getFont(this, R.font.quicksand_regular)
        mainSearchView.isSubmitButtonEnabled    = true

        mainSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val userQuery   = mainSearchView.query.toString()
                if (userQuery.isEmpty()) return false

                showLoading(true)

                mainViewModel.searchUsers(this@MainActivity, userQuery)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }


    // show/hide properties
    private fun showRecyclerView(state: Boolean) {
        if (state)  {
            mainRecyclerView.visibility = View.VISIBLE
            showLoading(false)
            showErrorInfo(false)
        }
        else    mainRecyclerView.visibility = View.INVISIBLE
    }

    private fun showLoading(state: Boolean) {
        if (state)  {
            mainProgressBar.visibility  = View.VISIBLE
            showRecyclerView(false)
            showErrorInfo(false)
            showPageInfo(false)
        }
        else    mainProgressBar.visibility  = View.INVISIBLE
    }

    private fun showErrorInfo(state: Boolean, messageResId: Int? = null) {
        if (!state) {
            retryButton.visibility  = View.INVISIBLE
            statusText.visibility   = View.INVISIBLE
        }
        else if (state && messageResId != null) {
            retryButton.visibility  = View.VISIBLE
            statusText.visibility   = View.VISIBLE
            statusText.text         = getString(messageResId)

            showLoading(false)
            showPageInfo(false)
            showRecyclerView(false)
        } else return
    }

    private fun showPageInfo(state: Boolean, pageInfo: Array<Int>? = null) {
        if (!state) {
            pageInfoTv.visibility           = View.INVISIBLE
            nextPageButton.visibility       = View.INVISIBLE
            previousPageButton.visibility   = View.INVISIBLE
        }
        else {
            val minPageState    = pageInfo?.get(0) == 1
            val maxPageState    = pageInfo?.get(0) == pageInfo?.get(1)

            pageInfoTv.text                 = getString(R.string.page_template, pageInfo?.get(0), pageInfo?.get(1))
            pageInfoTv.visibility           = View.VISIBLE
            nextPageButton.visibility       = if (maxPageState) View.INVISIBLE else View.VISIBLE
            previousPageButton.visibility   = if (minPageState) View.INVISIBLE else View.VISIBLE
        }
    }

}