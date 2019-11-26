package com.polarlooptheory.was.apiCalls

import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import com.github.kittinunf.fuel.httpPatch
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.polarlooptheory.was.model.User
import com.polarlooptheory.was.Settings
import kotlinx.coroutines.runBlocking
import org.json.JSONObject

/**
 * Object for handling login and tokens
 */
object Login {
    /**
     * Login function which updates [User]
     * @param username [User]'s login username
     * @param password [User]'s login password
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun getToken(username: String, password: String): Boolean {
        val endpoint = Settings.server_address + "oauth/token"
        var success = false
        runBlocking {
            val (_, _, result) = endpoint.httpPost(
                listOf("grant_type" to "password", "username" to username, "password" to password)
            ).authentication().basic(
                Settings.app_username,
                Settings.app_password
            ).awaitStringResponseResult()
            when (result) {
                is Result.Success -> {
                    User.username = username
                    User.email = JSONObject(result.value).getString("email")
                    User.UserToken.access_token = JSONObject(result.value).getString("access_token")
                    User.UserToken.refresh_token =
                        JSONObject(result.value).getString("refresh_token")
                    success = true
                }
                is Result.Failure -> {
                    if (result.error.response.statusCode == 401)
                        Settings.error_message = "Unauthorized"
                    else if (result.error.response.statusCode == 400)
                        Settings.error_message =
                            JSONObject(String(result.error.errorData)).getString("error_description")
                    else
                        ApiErrorHandling.handleError(result.error)
                }
            }
        }
        return success
    }

    /**
     * Refreshes expired [access_token][User.UserToken.access_token]
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun refreshToken(): Boolean {
        val endpoint = Settings.server_address + "oauth/token"
        var success = false
        runBlocking {
            val (_, _, result) = endpoint.httpPost(
                listOf(
                    "grant_type" to "refresh_token",
                    "refresh_token" to User.UserToken.refresh_token
                )
            ).authentication().basic(
                Settings.app_username,
                Settings.app_password
            ).awaitStringResponseResult()
            when (result) {
                is Result.Success -> {
                    User.UserToken.access_token = JSONObject(result.value).getString("access_token")
                    User.UserToken.refresh_token =
                        JSONObject(result.value).getString("refresh_token")
                    success = true
                }
                is Result.Failure -> {
                    if (result.error.response.statusCode == 401)
                        Settings.error_message = "Unauthorized"
                    else if (result.error.response.statusCode == 400)
                        Settings.error_message =
                            JSONObject(String(result.error.errorData)).getString("error_description")
                    else if (result.error.response.statusCode == 403)
                        Settings.error_message = "Session expired"
                    else
                        ApiErrorHandling.handleError(result.error)
                    logout()
                }
            }
        }
        return success
    }

    /**
     * Changes the [user][User]'s password
     * @param oldPassword [User][User]'s old password
     * @param newPassword [User][User]'s new password
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun changePassword(oldPassword: String, newPassword: String): Boolean {
        val endpoint = "${Settings.server_address}action/changePassword"
        var success = false
        runBlocking {
            val (_, _, result) = endpoint.httpPatch().authentication().bearer(User.UserToken.access_token).awaitStringResponseResult()
            when (result) {
                is Result.Success -> {
                    success = true
                }
                is Result.Failure -> {
                    if (ApiErrorHandling.handleError(result.error))
                        changePassword(oldPassword, newPassword)
                }
            }
        }
        return success
    }

    /**
     * Logout function which clears all the cached data
     */
    fun logout() {
        User.clear()
        Scenario.clear()
    }
}