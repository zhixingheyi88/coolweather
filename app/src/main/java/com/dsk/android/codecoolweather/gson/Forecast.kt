package com.dsk.android.codecoolweather.gson

import com.google.gson.annotations.SerializedName

/**
 * Created by gaoxikun on 2017/12/30.
 */
class Forecast {
    var date:String?=null;

    @SerializedName("tmp")
    var temperature:Temperature?=null;

    @SerializedName("cond")
    var more:More?=null;

    inner class Temperature{
        var max:String?=null;
        var min:String?=null;
    }
    inner class More{
        @SerializedName("txt_d")
        var info:String?=null;
    }
}