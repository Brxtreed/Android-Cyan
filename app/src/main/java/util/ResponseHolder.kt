package util

class ResponseHolder<T> {
    var responseCode: ResponseCode = ResponseCode.DEFAULT
    var httpErrorType: Int? = null
    var data: T? = null
    var errorMessage: String = ""
    var isCacheResult: Boolean = false

    fun resetDefaultCode(){
        responseCode = ResponseCode.DEFAULT
    }

}