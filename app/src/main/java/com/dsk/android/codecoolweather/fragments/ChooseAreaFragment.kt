package com.dsk.android.codecoolweather.fragments

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.dsk.android.codecoolweather.R
import com.dsk.android.codecoolweather.activities.MainActivity
import com.dsk.android.codecoolweather.activities.WeatherActivity
import com.dsk.android.codecoolweather.db.City
import com.dsk.android.codecoolweather.db.County
import com.dsk.android.codecoolweather.db.Province
import com.dsk.android.codecoolweather.util.HttpUtil
import com.dsk.android.codecoolweather.util.Utility
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.Response
import org.litepal.crud.DataSupport
import java.io.IOException
import kotlin.math.log

/**
 * Created by Administrator on 2017/12/29.
 */
class ChooseAreaFragment: Fragment() {
    private var progressDialog:ProgressDialog?=null;
    private var titleText:TextView?=null;
    private var backButton:Button?=null;
    private var listView:ListView?=null;
    private var adapter:ArrayAdapter<String>?=null;
    private var dataList:ArrayList<String>?=ArrayList();
    private var provinceList:List<Province>?=null;
    private var cityList:List<City>?=null;
    private var countyList:List<County>?=null;
    private var selectedProvince:Province?=null;
    private var selectedCity:City?=null;
    private var currentLevel:Int?=null;
    companion object {
        val LEVEL_PROVINCE=0;
        val LEVEL_CITY=1;
        val LEVEL_COUNTY=2;
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       var view:View =inflater.inflate(R.layout.choose_area,container,false)
        titleText=view.findViewById(R.id.title_text)
        backButton=view.findViewById(R.id.back_button)
        listView=view.findViewById(R.id.list_view)
        adapter= ArrayAdapter(context,android.R.layout.simple_list_item_1,dataList)
        listView!!.adapter=adapter
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listView!!.setOnItemClickListener(object:AdapterView.OnItemClickListener{
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
              if (currentLevel== LEVEL_PROVINCE){
                  selectedProvince=provinceList!!.get(position)
               queryCities();
              }else if (currentLevel== LEVEL_CITY){
                  selectedCity=cityList!!.get(position)
                 queryCounties();
              }else if (currentLevel == LEVEL_COUNTY){
                  var weatherId:String=countyList!!.get(position).weatherId!!
                  if (activity is MainActivity){
                  var intent=Intent(activity,WeatherActivity::class.java)
                  intent.putExtra("weather_id",weatherId)
                  startActivity(intent)
                  activity!!.finish();
                  }else if(activity is WeatherActivity){
                  var activity:WeatherActivity=activity as WeatherActivity
                      activity.drawerLayout!!.closeDrawers()
                      activity.swipeRefresh!!.isRefreshing=true
                      activity.requestWeather(weatherId)
                  }
              }
            }
        })
        backButton!!.setOnClickListener(object :View.OnClickListener{
            override fun onClick(p0: View?) {
                if (currentLevel == LEVEL_COUNTY){
                    queryCities();
                }else if (currentLevel == LEVEL_CITY){
                    queryProinces();
                }
            }
        })

        queryProinces();

    }
    /*
    * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询
    * */
    private fun  queryProinces(){
        titleText!!.setText("中国")
        backButton!!.visibility=View.GONE
        provinceList=DataSupport.findAll(Province::class.java)
        if (provinceList!=null&&provinceList!!.size>0){
            dataList!!.clear();
            for (province in provinceList!!){
                dataList!!.add(province!!.getProvinceName()!!)
             }
            adapter!!.notifyDataSetChanged()
            listView!!.setSelection(0)
            currentLevel= LEVEL_PROVINCE
        }else{
            var address="http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }
    }
    /*
   * 查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询
   * */
    fun queryCities(){
        titleText!!.setText(selectedProvince!!.getProvinceName()!!)
        backButton!!.visibility=View.VISIBLE
        cityList=DataSupport.where("provinceid=?",selectedProvince!!.getId().toString()).find(City::class.java)
        if (cityList!!.size>0){
            dataList!!.clear();
            for (city in cityList!!){
                dataList!!.add(city!!.cityName!!)
            }
            adapter!!.notifyDataSetChanged()
            listView!!.setSelection(0)
            currentLevel= LEVEL_CITY
        }else{
            var provinceCode:Int=selectedProvince!!.getProvinceCode()!!
            var address="http://guolin.tech/api/china/$provinceCode";
            queryFromServer(address,"city")
        }
    }

    /*
  * 查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询
  * */
    fun queryCounties(){
        titleText!!.setText(selectedCity!!.cityName)
        backButton!!.visibility=View.VISIBLE
        countyList=DataSupport.where("cityid = ?",selectedCity!!.id.toString()).find(County::class.java)
        if (countyList!!.size>0){
            dataList!!.clear();
            for (county in countyList!!){
                dataList!!.add(county!!.countyName!!)
            }
            adapter!!.notifyDataSetChanged()
            listView!!.setSelection(0)
            currentLevel= LEVEL_COUNTY
        }else{
            var provinceCode:Int=selectedProvince!!.getProvinceCode()!!
            var cityCode:Int=selectedCity!!.cityCode!!
            var address="http://guolin.tech/api/china/$provinceCode/$cityCode";
            queryFromServer(address,"county")
        }
    }

    /*
    * 根据传入的地址和类型从服务器上查询省市县数据
    * */
    private fun queryFromServer(address:String, type:String){
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address,object :Callback{

            override fun onResponse(call: Call?, response: Response?) {
              var responseText:String=response!!.body().string();
              var result=false;
                if ("province".equals(type)){
                    result=Utility.handleProvinceResponse(responseText)
                }else if("city".equals(type)){
                    result=Utility.handleCityResponse(responseText,selectedProvince!!.getId()!!)
                }else if ("county".equals(type)){
                    result=Utility.handleCountryResponse(responseText,selectedCity!!.id!!)
                }
                if (result){
                    activity!!.runOnUiThread(object :Runnable {
                        override fun run() {
                         closeProgressDialog()
                            if ("province".equals(type)){
                                queryProinces();
                            }else if ("city".equals(type)){
                              queryCities();
                            }else if("county".equals(type)){}
                              queryCounties();
                        }
                    })
                }

            }
            override fun onFailure(call: Call?, e: IOException?) {
                //通过runOnUIThread()方法回到主线程处理逻辑
                activity!!.runOnUiThread({
                    closeProgressDialog();
                    Toast.makeText(context,"加载失败",Toast.LENGTH_LONG).show()
                })
            }

        })

    }
    /*
   * 显示进度对话框
   * */
private fun showProgressDialog(){
        if (progressDialog ==null){
            progressDialog=ProgressDialog(activity);
            progressDialog!!.setMessage("正在加载...")
            progressDialog!!.setCanceledOnTouchOutside(false)
        }
        progressDialog!!.show();
    }
    /*
   * 关闭进度对话框
   * */
    private fun closeProgressDialog(){
        if (progressDialog !==null){
           progressDialog!!.dismiss();
        }
    }
}