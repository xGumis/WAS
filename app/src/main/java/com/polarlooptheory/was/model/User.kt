package com.polarlooptheory.was.model

/**
 * User's model class
 * @property username Logged in user's username
 * @property email Logged in user's email
 * @property UserToken Access/Refresh token held by the user
 */
object User {
    var username:String = ""
    var email:String = ""
    object UserToken{
        var access_token:String = ""
        var refresh_token:String = ""
    }

    /**
     * Clears logged in user info
     */
    fun clear(){
        username = ""
        email = ""
        UserToken.refresh_token = ""
        UserToken.access_token = ""
    }
}