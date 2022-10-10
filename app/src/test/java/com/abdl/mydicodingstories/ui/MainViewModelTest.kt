package com.abdl.mydicodingstories.ui

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.abdl.mydicodingstories.data.remote.response.ListStoryItem
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest{

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var context: Context
    private lateinit var mainViewModel: MainViewModel
    private val dummyStory = DataDummy.generateDummyStoriesEntity()

    @Before
    fun setUp() {
        mainViewModel = MainViewModel(context)
    }

    @Test
    fun `when Get Stories Should Not Null and Return Success`(){
        val observer = Observer<List<ListStoryItem>>{}
        try {
            val expectedStory = MutableLiveData<List<ListStoryItem>>()
            expectedStory.value = dummyStory
            `when`(mainViewModel.fetchStories())
            val actualStory = mainViewModel.listStory
            assertNotNull(actualStory)
        } finally {
            mainViewModel.listStory.removeObserver(observer)
        }
    }
}