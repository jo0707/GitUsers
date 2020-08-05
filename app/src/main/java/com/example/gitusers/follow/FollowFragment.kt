package com.example.gitusers.follow

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gitusers.R
import com.example.gitusers.utility.ApiErrorEvent
import com.example.gitusers.utility.UserAdapter
import kotlinx.android.synthetic.main.fragment_follow.*

class FollowFragment : Fragment(R.layout.fragment_follow), View.OnClickListener {

    companion object {
        private const val FOLLOW_URL      = "follow_url"
        private const val FOLLOW_COUNT    = "follow_count"
        private const val IS_FOLLOWERS    = "is_followers"

        fun newInstance(followUrl: String, count: Int, isFollowers: Boolean): FollowFragment {
            val fragment = FollowFragment()

            val bundle = bundleOf(
                FOLLOW_URL      to followUrl,
                FOLLOW_COUNT    to count,
                IS_FOLLOWERS    to isFollowers
            )

            fragment.arguments = bundle
            return fragment
        }
    }


    private lateinit var followFragmentViewModel: FollowFragmentViewModel
    private lateinit var followRvAdapter: UserAdapter
    private var followUrl   = ""
    private var followCount = 0
    private var isFollowers = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setMainViewModelObserver()

        followRvAdapter = UserAdapter()
        followRvAdapter.notifyDataSetChanged()

        followRecyclerView.apply {
            layoutManager   = LinearLayoutManager(context)
            adapter         = followRvAdapter
        }

        arguments?.apply {
            isFollowers = getBoolean(IS_FOLLOWERS)
            followUrl   = getString(FOLLOW_URL) as String
            followCount = getInt(FOLLOW_COUNT)
        }

        followRetryButton.setOnClickListener(this)
    }

    // update recyclerview favorite
    override fun onResume() {
        super.onResume()
        val userlist    = followFragmentViewModel.getUsers().value

        if (userlist != null)
            followRvAdapter.setData(followFragmentViewModel.refreshFavorite((activity as Context), userlist))
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.followRetryButton)
            activity?.recreate()
    }

    private fun setMainViewModelObserver() {
        followFragmentViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
            .get(FollowFragmentViewModel::class.java)

        if (followUrl.isNotEmpty())
            followFragmentViewModel.apply {
                setUser((activity as Context), followUrl)

                getError().observe(viewLifecycleOwner, Observer { error ->
                    if (error != ApiErrorEvent.NONE) {
                        showErrorInfo(true, error.getErrorResource())
                    } else {
                        followFragmentViewModel.getUsers()
                            .observe(viewLifecycleOwner, Observer { userList ->
                                if (userList != null) {
                                    showErrorInfo(false)
                                    followRvAdapter.setData(userList)
                                }
                            })
                    } // else
                })
            } // apply
    } // setMainViewModelObserver

    private fun showErrorInfo(state: Boolean, messageResId: Int? = null) {
        if (!state) {
            followErrorText.visibility      = View.INVISIBLE
            followRetryButton.visibility    = View.INVISIBLE
            followRecyclerView.visibility   = View.VISIBLE
        } else {
            followErrorText.text = messageResId?.let { getString(it) }
            followErrorText.visibility = View.VISIBLE
            followRecyclerView.visibility = View.INVISIBLE

            if (messageResId != R.string.no_users)
                followRetryButton.visibility    = View.VISIBLE
            else {
                followRetryButton.visibility   = View.INVISIBLE

                if (isFollowers)
                    followErrorText.text    = getString(R.string.no_followers)
                else
                    followErrorText.text    = getString(R.string.no_following)
            }
        }

        followProgressBar.visibility    = View.INVISIBLE
    }

}