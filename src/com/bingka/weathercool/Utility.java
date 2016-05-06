package com.bingka.weathercool;

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
}
