package com.example.gitusers.detail

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.gitusers.R
import com.example.gitusers.follow.FollowPagerAdapter
import com.example.gitusers.utility.ApiErrorEvent
import com.example.gitusers.utility.FullUser
import kotlinx.android.synthetic.main.activity_detail.*
import me.samlss.broccoli.Broccoli

class DetailActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val USER_URL_KEY  = "user_url"
    }

    private lateinit var detailViewModel: DetailViewModel
    private lateinit var followPagerAdapter: FollowPagerAdapter
    private lateinit var broccoli: Broccoli
    private lateinit var userUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        userUrl = intent.getStringExtra(USER_URL_KEY) as String

        setLoadingAnim()
        setDetailViewModel()
        setToolbar()

        detailRetryButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.detailRetryButton -> {
                val goToDetail      = Intent(this@DetailActivity, DetailActivity::class.java)
                goToDetail.putExtra(USER_URL_KEY, userUrl)
                startActivity(goToDetail)
                finish()
            }
        }
    }

    private fun setDetailViewModel() {
        detailViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(DetailViewModel::class.java)

        detailViewModel.setUser(userUrl)
        detailViewModel.getError().observe(this, Observer { error ->
            broccoli.show()
            if (error != ApiErrorEvent.NONE) {
                showErrorInfo(error.getErrorResource())
                broccoli.removeAllPlaceholders()
            }

            else {
                detailViewModel.getUser().observe(this, Observer { fullUser: FullUser ->
                    if (fullUser != FullUser.dummy) {
                        setPagerAdapter(fullUser)
                        setAllProperties(fullUser)
                        broccoli.removeAllPlaceholders()
                    }
                })
            }
        })
    }


    // properties setter
    private fun setPagerAdapter(fullUser: FullUser) {
        with(fullUser) {
            followPagerAdapter  = FollowPagerAdapter(
                this@DetailActivity, supportFragmentManager,
                followersUrl, followingUrl, followers, following
            )
        }

        detailViewPager.adapter = followPagerAdapter
        detTabLayout.setupWithViewPager(detailViewPager)

        supportActionBar?.elevation = 0f
    }

    private fun showErrorInfo(messageResId: Int) {
        detailErrorText.text        = getString(messageResId)
        detailErrorText.visibility  = View.VISIBLE
        detailRetryButton.visibility= View.VISIBLE
        errorBackground.visibility  = View.VISIBLE
        detailViewPager.visibility  = View.INVISIBLE
    }

    private fun setAllProperties(fullUser: FullUser) {
        with(fullUser) {
            detailName.text         = name
            detailUsername.text     = username
            detailId.text           = getString(R.string.id_template, id)
            detailLocation.text     = location
            detailRepo.text         = repository.toString()
            detailCompany.text      = company

            Glide.with(this@DetailActivity)
                .load(imageUrl)
                .apply(RequestOptions().override(192))
                .into(detailImage)

        }
    }

    private fun setToolbar() {
        (detailToolbar as Toolbar).apply {
            setNavigationIcon(R.drawable.ic_back_white_24)
            setNavigationOnClickListener { finish() }
        }
    }

    private fun setLoadingAnim() {
        broccoli    = Broccoli()
        broccoli.addPlaceholders(
            detailName,
            detailUsername,
            detailId,
            detailLocation,
            detailRepo,
            detailCompany,
            detailImage
        )
    }

}