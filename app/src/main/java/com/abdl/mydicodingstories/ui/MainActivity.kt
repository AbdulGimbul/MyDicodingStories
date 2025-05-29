package com.abdl.mydicodingstories.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import com.abdl.mydicodingstories.R
import com.abdl.mydicodingstories.adapter.ItemStoryAdapter
import com.abdl.mydicodingstories.adapter.LoadingStateAdapter
import com.abdl.mydicodingstories.data.remote.response.ListStoryItem
import com.abdl.mydicodingstories.databinding.ActivityMainBinding
import com.abdl.mydicodingstories.ui.login.LoginActivity
import com.abdl.mydicodingstories.ui.maps.MapsStoryActivity
import com.abdl.mydicodingstories.utils.SessionManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var rvAdapter: ItemStoryAdapter
    private lateinit var session: SessionManager

    private val mainViewModel: MainViewModel by viewModels()
    private val pagingViewModel: PagingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            (binding.view.layoutParams as ViewGroup.MarginLayoutParams).topMargin = insets.top
            binding.view.requestLayout()

            binding.view.updatePadding(bottom = insets.bottom)

            view.updatePadding(left = insets.left, right = insets.right)

            WindowInsetsCompat.CONSUMED
        }

        rvAdapter = ItemStoryAdapter()

        session = SessionManager(this)

        mainViewModel.isLoading.observe(this) {
            if (it == true) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        mainViewModel.errorMessage.observe(this) {
            Toast.makeText(this, "$it", Toast.LENGTH_SHORT).show()
        }

        binding.tvName.text = session.fetchName()
        binding.tvUserId.text = session.fetchUserId()

        binding.mapStories.setOnClickListener {
            startActivity(Intent(this@MainActivity, MapsStoryActivity::class.java))
        }

        binding.profileButton.setOnClickListener {
            Toast.makeText(this, "Fitur belum tersedia", Toast.LENGTH_SHORT).show()
        }

        binding.btnAddStory.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            showLogoutDialog()
        }

        showStoryList()
        showSelectedStory()
    }

    override fun onResume() {
        super.onResume()

        showStoryList()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_change_language -> {
                showLanguageSelectionDialog()
                true
            }

            else -> {
                return false
            }
        }
    }

    private fun showLanguageSelectionDialog() {
        val languages = arrayOf("English", "Indonesia")
        val languageCodes = arrayOf("en", "in")

        val currentLanguageCode = sessionManager.fetchLanguage()
        var checkedItem = languageCodes.indexOf(currentLanguageCode)
        if (checkedItem == -1) {
            checkedItem =
                languageCodes.indexOf(SessionManager.DEFAULT_LANGUAGE_CODE)
        }


        AlertDialog.Builder(this)
            .setTitle(getString(R.string.change_language))
            .setSingleChoiceItems(languages, checkedItem) { dialog, which ->
                setLocaleAndRecreate(languageCodes[which])
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showStoryList() {
        binding.apply {
            rvFeed.layoutManager = LinearLayoutManager(this@MainActivity)
            rvFeed.adapter = rvAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    rvAdapter.retry()
                }
            )
            pagingViewModel.getStoryWithPage.observe(this@MainActivity) {
                rvAdapter.submitData(lifecycle, it)
            }
        }
    }

    private fun showSelectedStory() {
        rvAdapter.setOnItemClickCallback(object : ItemStoryAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListStoryItem?) {
                val toDetailStory = Intent(this@MainActivity, DetailStoryActivity::class.java)
                toDetailStory.putExtra(DetailStoryActivity.EXTRA_STORY, data)

                startActivity(toDetailStory)
            }
        })
    }

    private fun showLogoutDialog() {
        val dialogTitle = R.string.logout_confirmation_title
        val dialogMessage = R.string.logout_confirmation_message

        val alertDialogBuilder = AlertDialog.Builder(this)
        with(alertDialogBuilder) {
            setTitle(dialogTitle)
            setMessage(dialogMessage)
            setCancelable(false)
            setPositiveButton(R.string.yes) { _, _ ->
                val onLogout = Intent(this@MainActivity, LoginActivity::class.java)
                onLogout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                onLogout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

                session.deleteAuthToken()
                onLogout.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(
                    onLogout,
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this@MainActivity).toBundle()
                )
                finish()
            }
            setNegativeButton(R.string.no) { dialog, _ ->
                dialog.cancel()
            }
        }

        val alertDialog = alertDialogBuilder.create()
        return alertDialog.show()
    }
}