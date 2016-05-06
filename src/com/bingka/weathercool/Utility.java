package com.bingka.weathercool;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

public class Utility {
	
	
	/**
	* 解析和处理服务器返回的省级数据
	*/
	public synchronized static boolean handleProvincesResponse(WeatherCoolDB db, String response){
		if(!TextUtils.isEmpty(response)){
			String[] allProvinces = response.split(",");
			if(allProvinces != null && allProvinces.length > 0) {
				for (String string : allProvinces) {
					String[] p = string.split("\\|");
					Province province = new Province();
					province.setProvinceName(p[1]);
					province.setProvinceCode(p[0]);
					db.saveProvince(province);
				}
				return true;
			}else{
				android.util.Log.d("[YY]","[Provinces] response.split() has somr error");
				return false;
			}
		}else{
			android.util.Log.d("[YY]","[Provinces] response is empty！");
			return false;
		}
	}
	
	/**
	* 解析和处理服务器返回的市级数据
	*/
	public synchronized static boolean handleCitiesResponse(WeatherCoolDB db, String response,int provinceId){
		if (!TextUtils.isEmpty(response)) {
			String[] strings = response.split(",");
			if(strings != null && strings.length > 0){
				for (String string : strings) {
					String[] p = string.split("\\|");
					City city = new City();
					city.setCityCode(p[0]);
					city.setCityName(p[1]);
					city.setProvinceId(provinceId);
					db.saveCity(city);
				}
				return true;
			}else{
				android.util.Log.d("[YY]","[Cities] response.split() has some error");
				return false;
			}
		}
		android.util.Log.d("[YY]","[Cities] response is empty！");
		return false;
		
	}
	
	/**
	* 解析和处理服务器返回的县级数据
	*/
	public static boolean handleCountiesResponse(WeatherCoolDB db, String response, int cityId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCounties = response.split(",");
			if (allCounties != null && allCounties.length > 0) {
				for (String c : allCounties) {
					String[] array = c.split("\\|");
					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					// 将解析出来的数据存储到County表
					db.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}
	/**
	* 解析服务器返回的JSON数据，并将解析出的数据存储到本地。
	*/
	public static void handleWeatherResponse(Context context, String response) {
		try {
			android.util.Log.d("[YY]","response = "+response);
			JSONObject jsonObject = new JSONObject(response);
			/*
			JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
			String city = weatherInfo.getString("city");
			String cityid = weatherInfo.getString("cityid");
			String temp1 = weatherInfo.getString("temp1");
			String temp2 = weatherInfo.getString("temp2");
			String weather = weatherInfo.getString("weather");
			String ptime = weatherInfo.getString("ptime");
			saveWeatherInfo(context, city, cityid, temp1, temp2,
						weather, ptime);
			*/
			JSONObject dataInfo = jsonObject.getJSONObject("data");	
			JSONArray arrayInfo = dataInfo.getJSONArray("forecast");
			JSONObject weatherInfo = arrayInfo.getJSONObject(0);
			String city = dataInfo.getString("city");
			//String cityid = weatherInfo.getString("cityid");
			String high = weatherInfo.getString("high");
			String low = weatherInfo.getString("low");
			String weather = weatherInfo.getString("type");
			//String ptime = weatherInfo.getString("ptime");
			saveWeatherInfo(context, city, high, low,
					weather);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private static void saveWeatherInfo(Context context, String city,
			String high, String low, String weather) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
		SharedPreferences preferences = context.getSharedPreferences("WeatherInfo2", 0);
		Editor editor = preferences.edit();
		//
		editor.putBoolean("city_selected", true);
		android.util.Log.d("[YY]","city = "+city);
		android.util.Log.d("[YY]","high = "+high);
		android.util.Log.d("[YY]","low = "+low);
		android.util.Log.d("[YY]","weather = "+weather);
		high = (high.split(" "))[1];
		low = (low.split(" "))[1];
		editor.putString("city", city);
		editor.putString("temp1", high);
		editor.putString("temp2", low);
		editor.putString("weather", weather);
		editor.putString("current_data", sdf.format(new Date()));
		editor.commit();
		
	}

	
	private static void saveWeatherInfo(Context context, String city,
			String cityid, String temp1, String temp2, String weather,
			String ptime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
		SharedPreferences preferences = context.getSharedPreferences("WeatherInfo", 0);
		Editor editor = preferences.edit();
		//
		editor.putBoolean("city_selected", true);
		
		editor.putString("city", city);
		editor.putString("cityid", cityid);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather", weather);
		editor.putString("ptime", ptime);
		editor.putString("current_data", sdf.format(new Date()));
		editor.commit();
	}
}
