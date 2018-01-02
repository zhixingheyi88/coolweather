package com.dsk.android.codecoolweather.activities

import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import com.dsk.android.codecoolweather.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var prefs:SharedPreferences=PreferenceManager.getDefaultSharedPreferences(this)
        if (prefs.getString("weather",null)!=null){
            var intent=Intent(this,WeatherActivity::class.java)
            startActivity(intent)
            finish();
        }
    }
}
