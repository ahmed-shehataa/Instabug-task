package com.ashehata.instabugtask.models

sealed class HttpErrorType {
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
