package com.polarlooptheory.was

/**
 * @property server_address Server's address for communication
 * @property app_username Username used for authorizing app
 * @property app_password Password used for authorizing app
 * @property error_message Error description obtained from server(clear after use)
 */
object Settings {
    const val server_address = "http://10.0.2.2:8080/"
    var error_message = ""
    const val app_username = "web"
    const val app_password = "password"
    const val test_token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsiYXBpLXJlc291cmNlIl0sInVzZXJfbmFtZSI6ImFkbWluIiwic2NvcGUiOlsiUkVBRCIsIldSSVRFIiwiRVhFQ1VURSJdLCJleHAiOjE1NzU1ODkyMzYsImF1dGhvcml0aWVzIjpbIlJPTEVfQURNSU4iLCJST0xFX1VTRVIiXSwianRpIjoiZmYzYzdiODAtNmM1YS00ZGFjLTk2NTYtOTJjZmYxMDZjZjMwIiwiZW1haWwiOiJhZG1pbkBhZG1pbi5jb20iLCJjbGllbnRfaWQiOiJ3ZWIiLCJ1c2VybmFtZSI6ImFkbWluIn0.ICU5pX3BbzW_vNY2ieL3ZD2TdeTOvJWlLUvu7g7Il80"
}