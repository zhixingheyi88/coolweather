package com.dsk.android.codecoolweather.db

import org.litepal.crud.DataSupport

/**
 * 省
 */
class Province:DataSupport() {
    private var id:Int? = null;
    private  var provinceName:String?=null;
    private var provinceCode:Int?=null;

    fun getId():Int?{
        return this.id;
    }
     fun setId(id:Int){
         this.id=id
    }
    fun getProvinceName():String?{
        return this.provinceName;
    }
     fun setProvinceName(provinceName:String){
         this.provinceName=provinceName
    }
    fun getProvinceCode():Int?{
        return this.provinceCode;
    }
    fun setProvinceCode(provinceCode:Int){
        this.provinceCode=provinceCode
    }

}