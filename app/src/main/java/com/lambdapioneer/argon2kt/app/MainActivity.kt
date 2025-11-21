package com.lambdapioneer.argon2kt.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.lambdapioneer.argon2kt.app.ui.AppTheme
import com.lambdapioneer.argon2kt.app.ui.Argon2Screen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppTheme {
                Argon2Screen()
            }
        }
    }
}
