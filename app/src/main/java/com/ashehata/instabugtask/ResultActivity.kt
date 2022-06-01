package com.ashehata.instabugtask

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.ashehata.instabugtask.dialogs.showHeadersDialog
import com.ashehata.instabugtask.dialogs.showQueryDialog
import com.ashehata.instabugtask.models.HttpErrorType
import com.ashehata.instabugtask.models.RequestType
import com.ashehata.instabugtask.models.ResponseModel

class ResultActivity : AppCompatActivity() {

    private lateinit var responseModel: ResponseModel

    /**
     * define views
     */
    private lateinit var tvCode: TextView
    private lateinit var tvError: TextView

    private lateinit var ivHeaders: ImageView
    private lateinit var ivQquery: ImageView
    private lateinit var linearQuery: LinearLayout
    private lateinit var linearRequestBody: LinearLayout

    private lateinit var tvRequestBody: TextView
    private lateinit var tvResponseBody: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        responseModel = intent.getSerializableExtra(HomeActivity.RESPONSE_KEY) as ResponseModel
        initViews()
        displayResponseData()

        onDisplayHeaders()
        onDisplayQuery()
    }

    private fun onDisplayQuery() {
        ivQquery.setOnClickListener {
            val query = responseModel.requestModel?.queryParameters
            if (query != null) {
                showQueryDialog(query)
            }
        }
    }

    private fun onDisplayHeaders() {
        ivHeaders.setOnClickListener {
            val responseHeaders = responseModel.headers
            val requestHeaders = responseModel.requestModel?.headers
            if (responseHeaders != null) {
                showHeadersDialog(responseHeaders, requestHeaders)
            }
        }
    }

    private fun initViews() {
        tvCode = findViewById(R.id.tv_code)
        tvError = findViewById(R.id.tv_error)

        ivHeaders = findViewById(R.id.iv_display_headers)
        ivQquery = findViewById(R.id.iv_display_query)
        linearQuery = findViewById(R.id.linear_query_parameters)
        linearRequestBody = findViewById(R.id.linear_request_body)

        tvRequestBody = findViewById(R.id.tv_request_body)
        tvResponseBody = findViewById(R.id.tv_response_body)


    }

    private fun displayResponseData() {
        responseModel.apply {
            tvCode.text = responseCode.toString()

            if (!this.errorMessage.isNullOrEmpty()) {
                tvError.text = errorMessage
            } else {
                tvError.text = displayErrorMessage(error)
            }
            tvRequestBody.text = requestModel?.requestBody
            tvResponseBody.text = responseBody
        }

        if (responseModel.requestModel?.queryParameters.isNullOrEmpty()) {
            linearQuery.visibility = View.GONE
        }

        when (responseModel.requestModel?.requestType) {
            RequestType.GET -> {
                linearRequestBody.visibility = View.GONE
            }
            RequestType.POST -> {
                linearQuery.visibility = View.GONE
            }
            null -> {}
        }
    }

    private fun displayErrorMessage(error: HttpErrorType?): String {
        return when (error) {
            HttpErrorType.None -> "No Error"
            HttpErrorType.BadGateway -> "Bad gateway"
            HttpErrorType.BadRequest -> "BadRequest"
            HttpErrorType.DataInvalid -> "DataInvalid"
            HttpErrorType.Forbidden -> "Forbidden "
            HttpErrorType.InternalServerError -> "InternalServerError "
            HttpErrorType.NotAuthorized -> "NotAuthorized "
            HttpErrorType.NotFound -> "NotFound "
            HttpErrorType.Unknown -> "Unknown "
            else -> "No Error"
        }

    }
}