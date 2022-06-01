package com.ashehata.instabugtask.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.ashehata.instabugtask.R
import com.ashehata.instabugtask.databinding.ActivityHomeBinding
import com.ashehata.instabugtask.models.*
import com.ashehata.instabugtask.result.ResultActivity
import com.ashehata.instabugtask.util.LogMe
import com.ashehata.instabugtask.util.isNetworkConnected
import java.util.concurrent.*


class HomeActivity : AppCompatActivity() {


    companion object {
        const val RESPONSE_KEY = "response"
    }

    /**
     * dynamic headers and query parameters
     */
    private lateinit var headersViewsList: MutableList<View>
    private lateinit var queriesViewsList: MutableList<View>

    /**
     * other
     */

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        listenToChanges()
        initLists()
        // on plus icon header clicked listener
        onAddHeader()
        // on plus icon query clicked listener
        onAddQuery()
        // on send button clicked listener
        onSendClick()
        // on request type radio buttons checked listener
        onRequestTypeSelected()

    }

    private fun listenToChanges() {
        viewModel.stateLiveData.observe(this, Observer {
            if (it.isSuccess && it.responseModel != null) {
                sendClearIntent()
                openResultScreen(it.responseModel)
            }

            if (it.isLoading) {
                binding.pbLoading.visibility = View.VISIBLE
                binding.btnSend.isEnabled = false
            } else {
                binding.btnSend.isEnabled = true
                binding.pbLoading.visibility = View.GONE
            }

            if (it.isUrlValid != null) {
                if (!it.isUrlValid) {
                    Toast.makeText(this, getString(R.string.empty_url), Toast.LENGTH_SHORT)
                        .show()
                }
            }

            if (it.isRequestTypeValid != null) {
                if (!it.isRequestTypeValid) {
                    Toast.makeText(this, getString(R.string.empty_request_type), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        })
    }

    private fun sendClearIntent() {
        viewModel.intentLiveData.value = HomeIntent.Clear
    }

    private fun onRequestTypeSelected() {
        binding.typeGroup.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.rb_get -> {
                    binding.linearQueriesParentHost.visibility = View.VISIBLE
                    binding.linearRequestBody.visibility = View.GONE
                }
                R.id.rb_post -> {
                    binding.linearQueriesParentHost.visibility = View.GONE
                    binding.linearRequestBody.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun initLists() {
        headersViewsList = mutableListOf()
        queriesViewsList = mutableListOf()
    }

    private fun onAddQuery() {
        binding.ivAddQuery.setOnClickListener {
            val view = LayoutInflater.from(this)
                .inflate(R.layout.key_value_item, binding.linearQueriesParentHost, false)

            view.tag = queriesViewsList.size.toString()
            Log.i("onAddHeader: Counter", queriesViewsList.size.toString())

            view.findViewById<ImageView>(R.id.iv_remove_item).setOnClickListener {
                // try to remove the header
                if (queriesViewsList.size != 0) {
                    val viewTag = (it.parent as View).tag.toString().toInt()
                    Log.i("onAddHeader: Tag", viewTag.toString())

                    val deletedView = queriesViewsList.find {
                        Log.i("onAddHeader: find", it.tag.toString())
                        return@find it.tag.toString().toInt() == viewTag
                    }
                    Log.i("onAddHeader: find", deletedView?.tag.toString())
                    //Toast.makeText(this, deletedView?.tag.toString(), Toast.LENGTH_SHORT).show()

                    binding.linearQueriesParentHost.removeView(deletedView)
                    queriesViewsList.remove(deletedView)

                }
            }
            binding.linearQueriesParentHost.addView(view)
            queriesViewsList.add(view)
        }
    }

    private fun onAddHeader() {
        binding.ivAddHeader.setOnClickListener {
            // try inflate header item from layouts
            val view = LayoutInflater.from(this)
                .inflate(R.layout.key_value_item, binding.linearHeadersParentHost, false)

            view.tag = headersViewsList.size.toString()
            Log.i("onAddHeader: Counter", headersViewsList.size.toString())

            /**
             * on remove header cliciked
             */
            view.findViewById<ImageView>(R.id.iv_remove_item).setOnClickListener {
                // try to remove the header
                if (headersViewsList.size != 0) {
                    val viewTag = (it.parent as View).tag.toString().toInt()
                    Log.i("onAddHeader: Tag", viewTag.toString())

                    val deletedView = headersViewsList.find {
                        Log.i("onAddHeader: find", it.tag.toString())
                        return@find it.tag.toString().toInt() == viewTag
                    }
                    Log.i("onAddHeader: find", deletedView?.tag.toString())

                    // remove header view from list and view hierarchy
                    binding.linearHeadersParentHost.removeView(deletedView)
                    headersViewsList.remove(deletedView)

                }
            }
            // add header view to list and view hierarchy
            binding.linearHeadersParentHost.addView(view)
            headersViewsList.add(view)
        }
    }

    private fun onSendClick() {
        binding.btnSend.setOnClickListener {
            // collect user inserted data
            val url = binding.etUrl.text.toString().trim()
            val requestBody = binding.etRequestBody.text.toString().trim()

            val requestType = when (binding.typeGroup.checkedRadioButtonId) {
                R.id.rb_get -> {
                    RequestType.GET
                }
                R.id.rb_post -> {
                    RequestType.POST
                }
                else -> RequestType.NONE
            }
            val headersList = collectHeadersData()
            val queries = collectQueriesData()

            val mRequestModel = RequestModel(
                url = url,
                requestType = requestType,
                requestBody = requestBody,
                headers = headersList,
                queryParameters = queries
            )
            LogMe.i("sendGetDataIntent", "called")
            sendGetDataIntent(mRequestModel)

        }
    }

    private fun sendGetDataIntent(mRequestModel: RequestModel) {
        if (isNetworkConnected()) {
            viewModel.intentLiveData.value = HomeIntent.GetResponse(mRequestModel)
        } else {
            Toast.makeText(this, "No internet", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openResultScreen(responseModel: ResponseModel) {
        startActivity(
            Intent(this, ResultActivity::class.java).apply {
                putExtra(RESPONSE_KEY, responseModel)
            }
        )
    }

    private fun collectHeadersData(): List<KeyValue> {
        val headers = mutableMapOf<String, String>()
        headersViewsList.map {
            val linearView = it as LinearLayout
            val key = (linearView.getChildAt(0) as EditText).text.toString()
            val value = (linearView.getChildAt(1) as EditText).text.toString()
            headers.put(key, value)
        }

        return headers.map {
            return@map KeyValue(it.key, it.value)
        }.filter { return@filter !(it.key.isNullOrEmpty() && it.value.isNullOrEmpty()) }
            .toList()
    }

    private fun collectQueriesData(): List<KeyValue> {
        val quries = mutableMapOf<String, String>()
        queriesViewsList.map {
            val linearView = it as LinearLayout
            val key = (linearView.getChildAt(0) as EditText).text.toString()
            val value = (linearView.getChildAt(1) as EditText).text.toString()
            quries.put(key, value)
        }
        return quries.map {
            return@map KeyValue(it.key, it.value)
        }.filter { return@filter !(it.key.isNullOrEmpty() && it.value.isNullOrEmpty()) }
            .toList()
    }
}