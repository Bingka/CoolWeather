package com.bingka.weathercool;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class WeatherCoolDB {
	/**
	* ���ݿ���
	*/
	public static final String DB_NAME = "weather_cool";
	
	/**
	* ���ݿ�汾
	*/
	public static final int VERSION = 1;
	
	private static WeatherCoolDB weatherCoolDB;
	
	private SQLiteDatabase db;


	/**
	* �����췽��˽�л�
	*/
	private WeatherCoolDB(Context context) {
		WeatherCoolOpenHelper coolOpenHelper = new WeatherCoolOpenHelper(
														context, DB_NAME, null, VERSION, null);
		db = coolOpenHelper.getReadableDatabase();
	}

	/**
	* ��ȡCoolWeatherDB��ʵ��(����ģʽ)��
	*/
	public synchronized static WeatherCoolDB getInstance(Context context){
		if(weatherCoolDB == null){
			weatherCoolDB = new WeatherCoolDB(context);
		}
		return weatherCoolDB;
	}
	
	/**
	* ��Provinceʵ���洢�����ݿ⡣
	*/
	public void saveProvince(Province province) {
		if (province != null) {
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert(WeatherCoolOpenHelper.PROVINCE_TABLE, null, values);
		}
	}
	
	/**
	 * �����ݿ��ȡȫ�����е�ʡ����Ϣ��
	 */
	public List<Province> loadProvinces(){
		List<Province> list = new ArrayList<Province>();
		Cursor cursor = db.query(WeatherCoolOpenHelper.PROVINCE_TABLE, null, null, null, null, null, null);
		if(cursor.moveToFirst()){
			do {
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				list.add(province);
			} while (cursor.moveToNext());
		}
		return list;	
	}
	
	/**
	* ��Cityʵ���洢�����ݿ⡣
	*/
	public void saveCity(City city) {
		if(city != null){
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			db.insert(WeatherCoolOpenHelper.CITY_TABLE, null, values);	
		}
	} 
	
	/**
	* �����ݿ��ȡĳʡ�����еĳ�����Ϣ��
	*/
	public List<City> loadCities(int provinceId) {
		List<City> cities = new ArrayList<City>();
		Cursor cursor = db.query(WeatherCoolOpenHelper.CITY_TABLE, null, "province_id = ?",
				new String[]{ String.valueOf(provinceId) }, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				//��ʱlog�����ڴ���
				android.util.Log.d("[YY]","provinceId = "+provinceId);
				android.util.Log.d("[YY]","ProvinceIdfromDB = "+cursor.getInt(cursor.getColumnIndex("province_id")));
				//end
				city.setProvinceId(cursor.getInt(cursor.getColumnIndex("province_id")));
				city.setProvinceId(provinceId);
				cities.add(city);
			} while (cursor.moveToNext());
		}
		return cities;
	}
	
	/**
	* ��Countyʵ���洢�����ݿ⡣
	*/
	public void saveCounty(County county) {
		if (county != null) {
			ContentValues values = new ContentValues();
			values.put("county_name", county.getCountyName());
			values.put("county_code", county.getCountyCode());
			values.put("city_id", county.getCityId());
			db.insert("County", null, values);
		}
	}
	
	/**
	* �����ݿ��ȡĳ���������е�����Ϣ��
	*/
	public List<County> loadCounties(int cityId) {
		List<County> list = new ArrayList<County>();
		Cursor cursor = db.query(WeatherCoolOpenHelper.COUNTY_TABLE, null, "city_id = ?",
				new String[] { String.valueOf(cityId) }, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				County county = new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setCityId(cityId);
				list.add(county);
			} while (cursor.moveToNext());
		}
		return list;
	}
	
	
}
