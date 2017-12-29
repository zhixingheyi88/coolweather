package com.dsk.android.codecoolweather.db

import org.litepal.crud.DataSupport

/**
 * 市
 */
class City:DataSupport() {
    var id:Int?=null;
    var cityName:String?=null;
    var cityCode:Int?=null;
    var provinceId:Int?=null;
}