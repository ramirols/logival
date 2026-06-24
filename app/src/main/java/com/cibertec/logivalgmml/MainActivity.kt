package com.cibertec.logivalgmml

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.cibertec.logivalgmml.navigation.AppNavigation
import com.cibertec.logivalgmml.ui.theme.LogiValGMMLTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            LogiValGMMLTheme {
                AppNavigation()
            }
        }
    }
}