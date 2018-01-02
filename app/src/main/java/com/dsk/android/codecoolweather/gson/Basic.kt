package com.dsk.android.codecoolweather.gson

import com.google.gson.annotations.SerializedName

/**
 * Created by gaoxikun on 2017/12/30.
 */
class Basic {
    @SerializedName("city")
    var cityName:String?=null;

    @SerializedName("id")
    var weatherId:String?=null;

    var update:Update?=null;
    inner class Update{
        @SerializedName("loc")
        var updateTime:String?=null;
    }
}