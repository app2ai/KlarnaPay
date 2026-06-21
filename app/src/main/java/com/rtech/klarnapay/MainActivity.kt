package com.rtech.klarnapay

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.rtech.klarnapay.presentation.nav.KlarnaNavGraph
import com.rtech.klarnapay.ui.theme.KlarnaPayTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KlarnaPayTheme {
                KlarnaNavGraph()
            }
        }
    }
}
