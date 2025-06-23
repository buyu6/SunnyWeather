package com.sunnyweather.android

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.sunnyweather.android.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var bindind:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindind=ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindind.root)
    }
}