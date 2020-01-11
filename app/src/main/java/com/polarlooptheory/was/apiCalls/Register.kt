package com.polarlooptheory.was.apiCalls

import android.util.Patterns
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.polarlooptheory.was.Settings
import kotlinx.coroutines.*
import org.json.JSONObject

/**
 * Object for handling registration
 */
object Register {
    /**
     * Available fields for registration
     */
    enum class Field{
        USERNAME,EMAIL,PASSWORD
    }

    /**
     * @return list of required [Fields][Field] for registration
     */
    fun requiredFields():List<Field>{
        return listOf(Field.USERNAME,Field.EMAIL,Field.PASSWORD)
    }

    /**
     * Registration function
     * @param username Registration user's username
     * @param email Registration user's email
     * @param password Registration user's password
     * @return true if ended in success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun register(username: String, email: String, password: String): Boolean {
        val json = JSONObject()
        json.put("username", username.toLowerCase())
        json.put("email", email)
        json.put("password", password)
        var success = false
        val endpoint = Settings.server_address + "register"
        runBlocking {
            val (_, _, result) = endpoint.httpPost().jsonBody(json.toString()).awaitStringResponseResult()
            when (result) {
                is Result.Success -> {
                    success = true
                }
                is Result.Failure -> {
                    ApiErrorHandling.handleError(result.error)
                }
            }
        }
        return success
    }

    /**
     * Check if [fields][Field] requirements are met
     * @param username Registration user's username
     * @param email Registration user's email
     * @param password Registration user's password
     * @return list of [fields][Field] which doesn't meet the requirements
     */
    fun checkFields(username: String, email: String, password: String):List<Field>{
        val list = mutableListOf<Field>()
        if(username.length<4) list.add(Field.USERNAME)
        if(password.length<6) list.add(Field.PASSWORD)
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())list.add(Field.EMAIL)
        return list
    }
}