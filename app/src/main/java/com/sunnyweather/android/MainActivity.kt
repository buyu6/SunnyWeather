package com.sunnyweather.android

import android.os.Bundle
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