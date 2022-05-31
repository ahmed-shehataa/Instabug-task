package com.ashehata.instabugtask

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.ashehata.instabugtask.util.makeApiCall
import android.net.ConnectivityManager
import com.ashehata.instabugtask.models.*
import com.ashehata.instabugtask.util.isValidURL
import java.util.concurrent.*


class HomeActivity : AppCompatActivity() {


    companion object {
        const val RESPONSE_KEY = "response"
    }

    private lateinit var runnable: Runnable
    private lateinit var typeGroup: RadioGroup
    private lateinit var getRadio: RadioButton
    private lateinit var postRadio: RadioButton
    private lateinit var sendButton: Button
    private lateinit var urlEt: EditText
    private lateinit var requestBody: EditText

    private lateinit var imageAddHeader: ImageView
    private lateinit var imageAddQuery: ImageView

    private lateinit var headersHostLinear: LinearLayout
    private lateinit var queriesHostLinear: LinearLayout
    private lateinit var linear_request_body: LinearLayout

    private lateinit var headersViewsList: MutableList<View>
    private lateinit var queriesViewsList: MutableList<View>

    private lateinit var executor: ExecutorService
    private lateinit var progress: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        executor = Executors.newSingleThreadExecutor()
        initViews()
        initLists()
        onAddHeader()
        onAddQuery()
        onSendClick()
        onRequestTypeSelected()
    }

    private fun onRequestTypeSelected() {
        typeGroup.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.rb_get -> {
                    queriesHostLinear.visibility = View.VISIBLE
                    linear_request_body.visibility = View.GONE
                }
                R.id.rb_post -> {
                    queriesHostLinear.visibility = View.GONE
                    linear_request_body.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun initLists() {
        headersViewsList = mutableListOf()
        queriesViewsList = mutableListOf()
    }

    private fun onAddQuery() {
        imageAddQuery.setOnClickListener {
            val view = LayoutInflater.from(this)
                .inflate(R.layout.key_value_item, queriesHostLinear, false)

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

                    queriesHostLinear.removeView(deletedView)
                    queriesViewsList.remove(deletedView)

                }
            }
            queriesHostLinear.addView(view)
            queriesViewsList.add(view)
        }
    }

    private fun onAddHeader() {
        imageAddHeader.setOnClickListener {
            val view = LayoutInflater.from(this)
                .inflate(R.layout.key_value_item, headersHostLinear, false)

            view.tag = headersViewsList.size.toString()
            Log.i("onAddHeader: Counter", headersViewsList.size.toString())

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
                    //Toast.makeText(this, deletedView?.tag.toString(), Toast.LENGTH_SHORT).show()

                    headersHostLinear.removeView(deletedView)
                    headersViewsList.remove(deletedView)

                }
            }
            headersHostLinear.addView(view)
            headersViewsList.add(view)
        }
    }

    private fun onSendClick() {
        sendButton.setOnClickListener {
            // validate url and type request

           /* if (!urlEt.text.toString().isValidURL()) {
                Toast.makeText(this, getString(R.string.empty_url), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (typeGroup.checkedRadioButtonId == -1) {
                Toast.makeText(this, getString(R.string.empty_request_type), Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val requestBody = requestBody.text.toString().trim()

            val requestType = when (typeGroup.checkedRadioButtonId) {
                R.id.rb_get -> {
                    RequestType.GET
                }
                R.id.rb_post -> {
                    RequestType.POST
                }
                else -> RequestType.NONE
            }*/
            // after that try to get data
            val headersList = collectHeadersData()
            val quries = collectQueriesData()

            val mRequestModel = RequestModel(
                url = "https://dog.ceo/api/breeds/image/random",
                requestType = RequestType.GET,
                requestBody = "requestBody",
                headers = headersList,
                queryParameters = quries
            )
            getApiData(mRequestModel)

        }
    }

    private fun getApiData(mRequestModel: RequestModel) {
        if (isNetworkConnected()) {
            showLoadingDialog()
            executor.execute {
                makeApiCall(
                    requestModel = mRequestModel,
                    onResponse = { responseModel ->
                        hideDialog()
                        runOnUiThread {
                            if (responseModel.errorMessage != null) {
                                Toast.makeText(this, responseModel.errorMessage, Toast.LENGTH_SHORT)
                                    .show()
                            }
                            openResultScreen(responseModel)
                        }
                    }
                )
            }
        } else {
            Toast.makeText(this, "No internet", Toast.LENGTH_SHORT).show()
        }
    }

    private fun hideDialog() {
        runOnUiThread {
            progress.hide()
        }
    }

    private fun showLoadingDialog() {
        progress = ProgressDialog(this)
        progress.apply {
            setMessage("loading")
            setButton(Dialog.BUTTON_NEGATIVE, getString(R.string.cancel)) { dialogInterface, i ->
                //runnable.
            }
            setCancelable(false)
            show()
        }
    }

    private fun openResultScreen(responseModel: ResponseModel) {
        startActivity(
            Intent(this, ResultActivity::class.java).apply {
                putExtra(RESPONSE_KEY, responseModel)
            }
        )
    }

    private fun isNetworkConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
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

    private fun initViews() {
        sendButton = findViewById(R.id.btn_send)
        typeGroup = findViewById(R.id.type_group)
        getRadio = findViewById(R.id.rb_get)
        postRadio = findViewById(R.id.rb_post)
        urlEt = findViewById(R.id.et_url)
        requestBody = findViewById(R.id.et_request_body)

        imageAddHeader = findViewById(R.id.iv_add_header)
        imageAddQuery = findViewById(R.id.iv_add_query)

        headersHostLinear = findViewById(R.id.linear_headers_parent_host)
        queriesHostLinear = findViewById(R.id.linear_queries_parent_host)
        linear_request_body = findViewById(R.id.linear_request_body)
    }

    override fun onDestroy() {
        // prevent memory leaks !!
        if (!executor.isShutdown) {
            executor.shutdown()
        }
        super.onDestroy()
    }
}