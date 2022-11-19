package com.abdl.mydicodingstories.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.abdl.mydicodingstories.adapter.ItemStoryAdapter
import com.abdl.mydicodingstories.adapter.LoadingStateAdapter
import com.abdl.mydicodingstories.data.PagingRepository
import com.abdl.mydicodingstories.data.remote.response.ListStoryItem
import com.abdl.mydicodingstories.data.remote.service.ApiConfig
import com.abdl.mydicodingstories.databinding.ActivityMainBinding
import com.abdl.mydicodingstories.ui.login.LoginActivity
import com.abdl.mydicodingstories.ui.maps.MapsStoryActivity
import com.abdl.mydicodingstories.utils.SessionManager
import com.abdl.mydicodingstories.utils.ViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var pagingViewModel: PagingViewModel
    private lateinit var rvAdapter: ItemStoryAdapter
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rvAdapter = ItemStoryAdapter()

        val apiService = ApiConfig.getApiService(this)
        val repository = PagingRepository(apiService)
        session = SessionManager(this)
        mainViewModel =
            ViewModelProvider(
                this,
                ViewModelFactory(session, apiService, repository)
            )[MainViewModel::class.java]
        pagingViewModel = ViewModelProvider(
            this,
            ViewModelFactory(session, apiService, repository)
        )[PagingViewModel::class.java]


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

        Log.d("MainActivity", "cek token : ${session.fetchAuthToken()}")

        binding.tvName.text = session.fetchName()

        binding.tvUserId.text = session.fetchUserId()

        binding.mapStories.setOnClickListener {
//            Toast.makeText(this, "Fitur belum tersedia", Toast.LENGTH_SHORT).show()
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
        val dialogTitle = "Yakin?"
        val dialogMessage = "Apakah anda yakin akan melakukan logout?"

        val alertDialogBuilder = AlertDialog.Builder(this)
        with(alertDialogBuilder) {
            setTitle(dialogTitle)
            setMessage(dialogMessage)
            setCancelable(false)
            setPositiveButton("Ya") { _, _ ->
                val onLogout = Intent(this@MainActivity, LoginActivity::class.java)
                onLogout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                onLogout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

                session.deleteAuthToken()
                Log.d("MainActivity", "cek token : ${session.fetchAuthToken()}")
                onLogout.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(
                    onLogout,
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this@MainActivity).toBundle()
                )
                finish()
            }
            setNegativeButton("Tidak") { dialog, _ ->
                dialog.cancel()
            }
        }

        val alertDialog = alertDialogBuilder.create()
        return alertDialog.show()
    }
}