package com.dsk.android.codecoolweather.util

import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Created by Administrator on 2017/12/29.
 */
class HttpUtil {
    companion object {
        fun sendOkHttpRequest(address:String,callback: okhttp3.Callback){
           var client = OkHttpClient();
            var request:Request=Request.Builder().url(address).build();
            client.newCall(request).enqueue(callback);
        }
    }
}