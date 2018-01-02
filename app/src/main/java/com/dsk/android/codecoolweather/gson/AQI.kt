package com.dsk.android.codecoolweather.gson

/**
 * Created by gaoxikun on 2017/12/30.
 */
class AQI {
    var city:AQICity?=null;
    inner class AQICity{
        var aqi:String?=null;
        var pm25:String?=null;
    }
}