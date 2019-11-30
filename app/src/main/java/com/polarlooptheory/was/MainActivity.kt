package com.polarlooptheory.was

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.polarlooptheory.was.apiCalls.Scenario
import com.polarlooptheory.was.model.User
import com.polarlooptheory.was.model.mCharacter
import com.polarlooptheory.was.model.mScenario
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //runBlocking { Login.getToken("admin", "password") }
        User.UserToken.access_token = Settings.test_token
        User.username = "test"
        val scenario = mScenario(scenarioKey = "TESTSCEN")
        val char = mCharacter(name = "UGA")
        button.setOnClickListener {
            GlobalScope.launch {
                //Scenario.joinWebsocket(scenario)
            }
        }
        button2.setOnClickListener {
            println(Settings.error_message)
        }

    }
}
