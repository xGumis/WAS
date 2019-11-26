package com.polarlooptheory.was

/**
 * @property server_address Server's address for communication
 * @property app_username Username used for authorizing app
 * @property app_password Password used for authorizing app
 * @property error_message Error description obtained from server(clear after use)
 */
object Settings {
    val server_address = "http://10.0.2.2:8080/"
    var error_message = ""
    val app_username = "web"
    val app_password = "password"
}