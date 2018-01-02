package com.dsk.android.codecoolweather.gson

import com.google.gson.annotations.SerializedName

/**
 * Created by gaoxikun on 2017/12/30.
 */
class Weather {
    var status:String?=null;
    var basic:Basic?=null;
    var aqi:AQI?=null;
    var now:Now?=null;
    var suggestion:Suggestion?=null;

    @SerializedName("daily_forecast")
    var forecastList:List<Forecast>?=null;
}