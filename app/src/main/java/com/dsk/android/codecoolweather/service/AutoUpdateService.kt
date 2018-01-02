package com.dsk.android.codecoolweather.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.IBinder
import android.os.SystemClock
import android.preference.PreferenceManager
import com.dsk.android.codecoolweather.gson.Weather
import com.dsk.android.codecoolweather.util.HttpUtil
import com.dsk.android.codecoolweather.util.Utility
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class AutoUpdateService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return  null;
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        updateWeather();
        updateBingPic();
        var manager:AlarmManager=getSystemService(Context.ALARM_SERVICE)  as AlarmManager
         var anHour:Int =8*60*60*1000;//八小时的毫秒数
        var triggerAtTime:Long=SystemClock.elapsedRealtime()+anHour;
        var i=Intent(this,AutoUpdateService::class.java)
        var pi:PendingIntent = PendingIntent.getService(this,0,i,0)
        manager.cancel(pi)
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi)
        return super.onStartCommand(intent, flags, startId)
    }
    private fun  updateWeather(){
        var prefs:SharedPreferences =PreferenceManager.getDefaultSharedPreferences(this)
        var weatherString:String=prefs.getString("weather",null)
        if (weatherString !=null){
            var weather:Weather=Utility.handleWeatherResponse(weatherString!!)!!
            var weatherId=weather.basic!!.weatherId;
            var weatherUrl="http://guolin.tech/api/weather?cityid="+weatherId+"&key=bc0418b57b2d4918819d3974ac1285d9"
            HttpUtil.sendOkHttpRequest(weatherUrl,object : Callback{
                override fun onResponse(call: Call?, response: Response?) {
                    val responseText:String=response!!.body().string();
                    val weather:Weather?=Utility.handleWeatherResponse(responseText)
                    if (weather!=null && "ok".equals(weather.status)){
                        var editor:SharedPreferences.Editor=PreferenceManager.getDefaultSharedPreferences(this@AutoUpdateService).edit()
                       editor.putString("weather",responseText)
                        editor.apply()
                    }
                }

                override fun onFailure(call: Call?, e: IOException?) {
                    e!!.printStackTrace()
                }
            })
        }
    }
    private fun updateBingPic(){
        var requestBingPic:String="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic,object :Callback{
            override fun onResponse(call: Call?, response: Response?) {
           var bingPic:String=response!!.body().string();
                var editor:SharedPreferences.Editor=PreferenceManager.getDefaultSharedPreferences(this@AutoUpdateService).edit()
                editor.putString("bing_pic",bingPic)
                editor.apply()
            }

            override fun onFailure(call: Call?, e: IOException?) {
                e!!.printStackTrace()
            }
        })
    }
}
