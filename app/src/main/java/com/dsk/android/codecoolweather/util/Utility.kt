package com.dsk.android.codecoolweather.util

import android.text.TextUtils
import com.dsk.android.codecoolweather.db.City
import com.dsk.android.codecoolweather.db.County
import com.dsk.android.codecoolweather.db.Province
import com.dsk.android.codecoolweather.gson.Weather
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by Administrator on 2017/12/29.
 */
class Utility {
    companion object {
        fun handleProvinceResponse(response:String):Boolean{
            if (!TextUtils.isEmpty(response)){
                var allPrinces= JSONArray(response)
                try {
                    var province:Province?=null;
                    for (i in 0 .. allPrinces.length()-1){
                        var princeObject:JSONObject=allPrinces.getJSONObject(i);
                        province= Province();
                        province.setProvinceName(princeObject.getString("name"))
                        province.setProvinceCode(princeObject.getInt("id"))
                        province.save()
                    }
                    return true;
                }catch (e:JSONException){
                    e.printStackTrace()
                }
            }
            return false;
        }

        fun handleCityResponse(response: String,provinceId:Int):Boolean{
            if (!TextUtils.isEmpty(response)){
                var allCities= JSONArray(response)
                try {
                    var city:City?=null;
                    for (i in 0 .. allCities.length()-1){
                        var princeObject:JSONObject=allCities.getJSONObject(i);
                        city= City();
                        city.cityName=princeObject.getString("name")
                        city.cityCode=princeObject.getInt("id")
                        city.provinceId=provinceId
                        city.save()
                    }
                    return true;
                }catch (e:JSONException){
                    e.printStackTrace()
                }
            }
            return false;
        }

        fun handleCountryResponse(response: String,cityId:Int):Boolean{
            if (!TextUtils.isEmpty(response)){
                var allCountries= JSONArray(response)
                try {
                    var county:County?=null;
                    for (i in 0 .. allCountries.length()-1){
                        var princeObject:JSONObject=allCountries.getJSONObject(i);
                        county= County();
                        county.countyName=princeObject.getString("name")
                        county.weatherId=princeObject.getString("weather_id")
                        county.cityId=cityId
                        county.save()
                    }
                    return true;
                }catch (e:JSONException){
                    e.printStackTrace()
                }
            }
            return false;
        }
        fun handleWeatherResponse(response: String):Weather?{
            try {
                var jsonObject=JSONObject(response)
                var jsonArray=jsonObject.getJSONArray("HeWeather")
                var weatherContent=jsonArray.getJSONObject(0).toString()
                return  Gson().fromJson(weatherContent,Weather::class.java)
            }catch (e:Exception){
                e.printStackTrace()
            }
            return  null;
        }


    }
}