package com.bingka.weathercool;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class WeatherCoolOpenHelper extends SQLiteOpenHelper {
	
	public static final String PROVINCE_TABLE = "Province";
	public static final String CITY_TABLE = "City";
	public static final String COUNTY_TABLE = "County";
	
	/**
	* Province表建表语句(省)
	*/
	public static final String CREATE_PROVINCE = "create table Province(" +
			"id integer primary key autoincrement, " +
			"province_name text, " +
			"province_code text)";
	
	/**
	* City表建表语句(市)
	*/
	public static final String CREATE_CITY = "create table City(" +
			"id integer primary key autoincrement, " +
			"city_name text, " +
			"city_code text, " +
			"province_id integer)";
	
	/**
	* County表建表语句(县)
	*/
	public static final String CREATE_COUNTY = "create table County(" +
			"id integer primary key autoincrement, " +
			"county_name text, " +
			"county_code text, " +
			"city_id integer)";
	
	
	public WeatherCoolOpenHelper(Context context, String name,
			CursorFactory factory, int version,
			DatabaseErrorHandler errorHandler) {
		
		super(context, name, factory, version, errorHandler);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_PROVINCE);  // 创建Province表
		db.execSQL(CREATE_CITY);      // 创建City表
		db.execSQL(CREATE_COUNTY);    // 创建County表

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		

	}

}
