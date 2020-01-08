package com.polarlooptheory.was

/**
 * @property server_address Server's address for communication
 * @property app_username Username used for authorizing app
 * @property app_password Password used for authorizing app
 * @property error_message Error description obtained from server(clear after use)
 */
object Settings {
    const val server_address = "http://192.168.1.14:8080/"
    var error_message = ""
    const val app_username = "web"
    const val app_password = "password"
}