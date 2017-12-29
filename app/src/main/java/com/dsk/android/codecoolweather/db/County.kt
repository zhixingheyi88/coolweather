package com.dsk.android.codecoolweather.db

import org.litepal.crud.DataSupport

/**
 * 县
 */
class County:DataSupport() {
    var id:Int?=null;
    var countyName:String?=null;
    var weatherId:String?=null;
    var cityId:Int?=null;
}