package com.dsk.android.codecoolweather.db

import org.litepal.crud.DataSupport

/**
 * 省
 */
class Province:DataSupport() {
    var id:Int?=null;
    var provinceName:String?=null;
    var provinceCode:Int?=null;
}