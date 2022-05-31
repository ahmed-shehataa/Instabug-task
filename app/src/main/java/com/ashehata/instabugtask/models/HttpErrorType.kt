package com.ashehata.instabugtask.models

import java.io.Serializable

sealed class HttpErrorType : Serializable {
    object BadRequest : HttpErrorType()
    object NotAuthorized : HttpErrorType()
    object Forbidden : HttpErrorType()
    object NotFound : HttpErrorType()
    object DataInvalid : HttpErrorType()
    object InternalServerError : HttpErrorType()
    object BadGateway : HttpErrorType()
    object Unknown : HttpErrorType()
    object None : HttpErrorType()

}
