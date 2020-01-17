package com.polarlooptheory.was.apiCalls

import com.github.kittinunf.fuel.core.FuelError
import com.polarlooptheory.was.Settings
import kotlinx.coroutines.runBlocking

/**
 * Object for handling requests' errors
 */
object ApiErrorHandling {
    /**
     * Handles error codes from requests
     *
     * Updates [message][Settings.error_message] with error description
     * @return true if token was refreshed
     */
    fun handleError(error:FuelError):Boolean{
        var refreshed = false
        when(error.response.statusCode){
            //Various errors
            400 ->{
                Settings.error_message = String(error.errorData)
            }
            401 -> {
                runBlocking {
                    refreshed = Login.refreshToken()
                }
            }
            403 -> {
                Settings.error_message = "Forbidden"
            }
            404 -> {
                Settings.error_message = "Wrong address"
            }
            else -> {
                Settings.error_message = error.message.orEmpty()
            }
        }
        return refreshed
    }
}