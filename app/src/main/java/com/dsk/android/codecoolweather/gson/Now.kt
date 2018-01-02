package com.dsk.android.codecoolweather.gson

import com.google.gson.annotations.SerializedName

/**
 * Created by gaoxikun on 2017/12/30.
 */
class Now {
    @SerializedName("tmp")
    var temperature:String?=null;
    @SerializedName("cond")
    var more:More?=null;
    inner class More{
        @SerializedName("txt")
        var info:String?=null;
    }
}