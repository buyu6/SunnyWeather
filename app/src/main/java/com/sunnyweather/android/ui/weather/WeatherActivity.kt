package com.sunnyweather.android.ui.weather

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sunnyweather.android.R
import com.sunnyweather.android.databinding.ActivityWeatherBinding
import com.sunnyweather.android.databinding.ForecastBinding
import com.sunnyweather.android.databinding.LifeIndexBinding
import com.sunnyweather.android.databinding.NowBinding
import com.sunnyweather.android.logic.model.Weather
import com.sunnyweather.android.logic.model.getSky
import java.text.SimpleDateFormat
import java.util.Locale

class WeatherActivity : AppCompatActivity() {
    private lateinit var bindingNow:NowBinding
    private lateinit var bindingForecast:ForecastBinding
    private lateinit var bindingLifeIndex:LifeIndexBinding
    private lateinit var binding: ActivityWeatherBinding
    val viewModel by lazy{ViewModelProvider(this).get(WeatherViewModel::class.java)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //将背景与系统状态栏融合
        val decorView=window.decorView
        decorView.systemUiVisibility=View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor=Color.TRANSPARENT
        //布局初始化
        binding=ActivityWeatherBinding.inflate(layoutInflater)
        bindingNow=NowBinding.inflate(layoutInflater)
        bindingForecast=ForecastBinding.inflate(layoutInflater)
        bindingLifeIndex=LifeIndexBinding.inflate(layoutInflater)
        val rootView = binding.root
        val container = LinearLayout(this)
        container.orientation = LinearLayout.VERTICAL
// 将子视图添加到容器中
        container.addView(bindingNow.root)
        container.addView(bindingForecast.root)
        container.addView(bindingLifeIndex.root)
// 将容器设置为根视图
        if (rootView is ScrollView) {
            rootView.removeAllViews()
            rootView.addView(container)
        } else {
            rootView.addView(container)
        }
        setContentView(rootView)

        //取值等操作
        if(viewModel.locationLng.isEmpty()){
            viewModel.locationLng=intent.getStringExtra("location_lng")?:""
        }
        if(viewModel.locationLat.isEmpty()){
            viewModel.locationLat=intent.getStringExtra("location_lat")?:""
        }
        if(viewModel.placeName.isEmpty()){
            viewModel.placeName=intent.getStringExtra("place_name")?:""
        }
        viewModel.weatherLiveData.observe(this, Observer { result->
            val weather=result.getOrNull()
            if(weather!=null){
                showWeatherInfo(weather)
            }else{
                Toast.makeText(this,"无法获取天气",Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
        viewModel.refreshWeather(viewModel.locationLng,viewModel.locationLat)
    }
    private fun showWeatherInfo(weather: Weather){
            bindingNow.placeName.text=viewModel.placeName
        val realtime=weather.realtime
        val daily=weather.daily
        //填充now.xml中的数据
        val currentTempText="${realtime.temperature.toInt()}℃"
        bindingNow.currentTemp.text=currentTempText
        bindingNow.currentSky.text= getSky(realtime.skycon).info
        val currentPM25Text="空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
        bindingNow.currentAQI.text=currentPM25Text
        bindingNow.nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)
        //填充forecast.xml数据
        bindingForecast.forecastLayout.removeAllViews()
        val days=daily.skycon.size
        for (i in 0 until days ){
            val skycon=daily.skycon[i]
            val temperature=daily.temperature[i]
            val view=LayoutInflater.from(this).inflate(R.layout.forecast_item,bindingForecast.forecastLayout,false)
            val dateInfo=view.findViewById<TextView>(R.id.dateInfo)
            val skyIcon=view.findViewById<ImageView>(R.id.skyIcon)
            val skyInfo=view.findViewById<TextView>(R.id.skyInfo)
            val temperatureInfo=view.findViewById<TextView>(R.id.temperatureInfo)
            val simpleDateFormat=SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateInfo.text=simpleDateFormat.format(skycon.date)
            val sky= getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text=sky.info
            val tempText="${temperature.min.toInt()} ~ ${temperature.max.toInt()}℃"
            temperatureInfo.text=tempText
            bindingForecast.forecastLayout.addView(view)
        }
        //填充life_index中的数据
        val lifeIndex=daily.lifeIndex
        bindingLifeIndex.coldRiskText.text=lifeIndex.coldRisk[0].desc
        bindingLifeIndex.dressingText.text=lifeIndex.dressing[0].desc
        bindingLifeIndex.ultravioletText.text=lifeIndex.ultraviolet[0].desc
        bindingLifeIndex.carWashingText.text=lifeIndex.carWashing[0].desc
        binding.weatherLayout.visibility=View.VISIBLE
    }
}