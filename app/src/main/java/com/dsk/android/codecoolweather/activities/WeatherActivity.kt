package com.dsk.android.codecoolweather.activities

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.dsk.android.codecoolweather.R
import com.dsk.android.codecoolweather.gson.Weather
import com.dsk.android.codecoolweather.service.AutoUpdateService
import com.dsk.android.codecoolweather.util.HttpUtil
import com.dsk.android.codecoolweather.util.Utility
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.w3c.dom.Text
import java.io.IOException

class WeatherActivity : AppCompatActivity() {
    private var weatherLayout:ScrollView?=null;
    private var titleCity:TextView?=null;
    private var titleUpdateTime:TextView?=null;
    private var degreeText:TextView?=null;
    private var weatherInfoText:TextView?=null;
    private var forecastLayout:LinearLayout?=null;
    private var aqiText:TextView?=null;
    private var pm25Text:TextView?=null;
    private var comfortText:TextView?=null;
    private var carWashText:TextView?=null;
    private var sportText:TextView?=null;
    private var bingPicImg:ImageView?=null;
    var swipeRefresh:SwipeRefreshLayout?=null;
    var drawerLayout:DrawerLayout?=null;
    private var navButton:Button?=null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)
        if (Build.VERSION.SDK_INT >= 21){
            var decorView:View=window.decorView
            decorView.systemUiVisibility=View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            //View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            //
            window.statusBarColor=Color.TRANSPARENT
        }
        //初始化控件
        weatherLayout=findViewById(R.id.weather_layout)
        titleCity=findViewById(R.id.title_city)
        titleUpdateTime=findViewById(R.id.title_update_time)
        degreeText=findViewById(R.id.degree_text)
        weatherInfoText=findViewById(R.id.weather_info_text)
        forecastLayout=findViewById(R.id.forecast_layout)
        aqiText=findViewById(R.id.aqi_text)
        pm25Text=findViewById(R.id.pm25_text)
        comfortText=findViewById(R.id.comfort_text)
        carWashText=findViewById(R.id.car_wash_text)
        sportText=findViewById(R.id.sport_text)
        bingPicImg=findViewById(R.id.bing_pic_img)
        swipeRefresh=findViewById(R.id.swipe_refresh)
        swipeRefresh!!.setColorSchemeResources(R.color.colorPrimary)
        drawerLayout=findViewById(R.id.drawer_layout)
        navButton=findViewById(R.id.nav_button)

        navButton!!.setOnClickListener(object :View.OnClickListener{
            override fun onClick(p0: View?) {
                drawerLayout!!.openDrawer(GravityCompat.START)
            }
        })

        var prefs:SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        var bingPic:String?=prefs.getString("bing_pic",null);
        if (bingPic!=null){
           Glide.with(this).load(bingPic).into(bingPicImg)
        }else{
            loadBingPic();
        }
        var weatherString:String?=prefs.getString("weather",null);
        var weatherId:String?=null;
        if(weatherString !=null){
            var weather:Weather=Utility.handleWeatherResponse(weatherString)!!;
            weatherId=weather.basic!!.weatherId
            showWeatherInfo(weather)
        }else{
            weatherId=intent.getStringExtra("weather_id");
            weatherLayout!!.visibility=View.INVISIBLE
            requestWeather(weatherId)
        }
        swipeRefresh!!.setOnRefreshListener(object :SwipeRefreshLayout.OnRefreshListener{
            override fun onRefresh() {
                requestWeather(weatherId!!)
            }
        })
    }
    /*
    * 根据天气id请求城市天气信息
    * */
    fun requestWeather(weatherId:String ){
        var weatherUrl="http://guolin.tech/api/weather?cityid="+weatherId+"&key=bc0418b57b2d4918819d3974ac1285d9";
        HttpUtil.sendOkHttpRequest(weatherUrl,object : Callback {
            override fun onResponse(call: Call?, response: Response?) {
                val responseText:String=response!!.body().string();
                val weather:Weather=Utility.handleWeatherResponse(responseText)!!
                runOnUiThread(object :Runnable{
                    override fun run() {
                        if (weather !=null &&"ok".equals(weather.status)){
                            var editor:SharedPreferences.Editor =PreferenceManager.getDefaultSharedPreferences(this@WeatherActivity).edit();
                        editor.putString("weather",responseText)
                            editor.apply();
                            showWeatherInfo(weather);
                        }else{
                            Toast.makeText(this@WeatherActivity,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh!!.isRefreshing=false
                    }
                })
            }

            override fun onFailure(call: Call?, e: IOException?) {
                e!!.printStackTrace()
                runOnUiThread(object :Runnable{
                    override fun run() {
                        Toast.makeText(this@WeatherActivity,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        swipeRefresh!!.isRefreshing=false
                    }
                })
            }
        })

    }
    /**
     * 处理并展示Weather实体类中的数据
     * */
    private fun showWeatherInfo(weather:Weather){
        if (weather !=null && "ok".equals(weather.status)){


        var cityName:String?=weather.basic!!.cityName;
        var updateTime:String?=weather.basic!!.update!!.updateTime!!.split(" ")[1];
        var degree:String?=weather.now!!.temperature+" ℃";
        var weatherInfo:String?=weather.now!!.more!!.info;

        titleCity!!.setText(cityName)
        titleUpdateTime!!.setText(updateTime)
        degreeText!!.setText(degree)
        weatherInfoText!!.setText(weatherInfo)
        forecastLayout!!.removeAllViews();
        for (forecast in weather.forecastList!!){
            var view:View=LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false)
            var dateText:TextView=view.findViewById(R.id.date_text)
            var infoText=view.findViewById<TextView>(R.id.info_text)
            var maxText:TextView=view.findViewById(R.id.max_text)
            var minText:TextView=view.findViewById(R.id.min_text)
            dateText.setText(forecast.date)
            infoText.setText(forecast.more!!.info!!)
            maxText.setText(forecast.temperature!!.max)
            minText.setText(forecast.temperature!!.min)
            forecastLayout!!.addView(view)
        }
        if (weather.aqi !=null){
            aqiText!!.setText(weather!!.aqi!!.city!!.aqi)
            pm25Text!!.setText(weather!!.aqi!!.city!!.pm25)
        }
        var comfort:String="舒适度："+weather.suggestion!!.comfort!!.info
        var carwash:String="洗车指数："+weather.suggestion!!.carWash!!.info
        var sport:String="运动建议："+weather.suggestion!!.sport!!.info

        comfortText!!.setText(comfort)
        carWashText!!.setText(carwash)
        sportText!!.setText(sport)
        weatherLayout!!.visibility=View.VISIBLE
            //启动服务
        var intent=Intent(this,AutoUpdateService::class.java)
         startService(intent)
        }else{
            Toast.makeText(this,"获取天气信息失败",Toast.LENGTH_SHORT)
        }
    }

    private fun loadBingPic(){
        var requetBingPic="http://guolin.tech/api/bing_pic"
        HttpUtil.sendOkHttpRequest(requetBingPic,object :Callback{
            override fun onResponse(call: Call?, response: Response?) {
                var  bingPic:String=response!!.body().string();
                var editor:SharedPreferences.Editor =PreferenceManager.getDefaultSharedPreferences(this@WeatherActivity).edit()
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(object:Runnable{
                    override fun run() {
                        Glide.with(this@WeatherActivity).load(bingPic).into(bingPicImg)
                    }
                } )
            }

            override fun onFailure(call: Call?, e: IOException?) {
                e!!.printStackTrace()
            }
        })
    }
}
