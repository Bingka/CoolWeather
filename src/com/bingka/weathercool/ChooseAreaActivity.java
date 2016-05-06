package com.bingka.weathercool;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {
	
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private WeatherCoolDB coolWeatherDB;
	private List<String> dataList = new ArrayList<String>();
	/**
	* 省列表
	*/
	private List<Province> provinceList;
	/**
	* 市列表
	*/
	private List<City> cityList;
	/**
	* 县列表
	*/
	private List<County> countyList;
	/**
	* 选中的省份
	*/
	private Province selectedProvince;
	/**
	* 选中的城市
	*/
	private City selectedCity;
	/**
	/**
	* 选中的县级
	*/
	private County selectedCounty;
	/**
	* 当前选中的级别
	*/
	private int currentLevel;
	
	private static final int MESSAGE_FINISH = 0;
	private static final int MESSAGE_ERROR = 1;
	
	@SuppressLint("HandlerLeak") Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MESSAGE_FINISH:
						handfinish((String)msg.obj);
					break;
				case MESSAGE_ERROR:
						handerror();
					break;
				default:
					break;
			}
			closeProgressDialog();
		}

		private void handerror() {
			Toast.makeText(ChooseAreaActivity.this,
					"加载失败", Toast.LENGTH_SHORT).show();
		}

		private void handfinish(String type) {
			if ("province".equals(type)) {
				queryProvinces();
			} else if ("city".equals(type)) {
				queryCities();
			} else if ("county".equals(type)) {
				queryCounties();
			}
		}
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		SharedPreferences prefs = getSharedPreferences("WeatherInfo2", 0);
		if (prefs.getBoolean("city_selected", false)) {
			Intent intent = new Intent(this, WeatherActivity.class);
			startActivity(intent);
		}
		initView();
		
	}
	
	private void initView() {
		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		coolWeatherDB = WeatherCoolDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(currentLevel == LEVEL_PROVINCE){
					//当前处于省级list，跳转到市级list
					selectedProvince = provinceList.get(position);
					queryCities();
				}else if(currentLevel == LEVEL_CITY){
					//当前处于市级list，跳转到县级list
					selectedCity = cityList.get(position);
					queryCounties();
				}else if(currentLevel == LEVEL_COUNTY){
					//当前处于县级list，跳转到对应的天气信息界面
					android.util.Log.d("[YY]","currentLevel = "+currentLevel);
					selectedCounty = countyList.get(position);
					Intent intent = new Intent(ChooseAreaActivity.this,WeatherActivity.class);
					String countyName = selectedCounty.getCountyName();
					String countyCode = selectedCounty.getCountyCode();
					intent.putExtra("countyName", countyName);
					intent.putExtra("countyCode", countyCode);
					startActivity(intent);
				}
			}
		});
		queryProvinces(); // 加载省级数据
	}
	
	/**
	* 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询。
	*/
	private void queryProvinces() {
		
		provinceList = coolWeatherDB.loadProvinces();
		if(provinceList.size() > 0){
			dataList.clear();
			for (Province p : provinceList) {
				String provinceName = p.getProvinceName();
				dataList.add(provinceName);
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		} else {
			queryFromServer(null, "province");
		}
	}
	

	/**
	* 查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询。
	*/
	private void queryCities() {
		cityList = coolWeatherDB.loadCities(selectedProvince.getId());
		if (cityList.size() > 0) {
			dataList.clear();
			for (City city : cityList) {
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		} else {
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}
	
	/**
	* 查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询。
	*/
	private void queryCounties() {
		countyList = coolWeatherDB.loadCounties(selectedCity.getId());
		if (countyList.size() > 0) {
			dataList.clear();
			for (County county : countyList) {
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
		} else {
			queryFromServer(selectedCity.getCityCode(), "county");
		}
	}

	/**
	 * 
	 * @param object
	 * @param string
	 */
	private void queryFromServer(String code, final String type) {
		String address;
		if(!TextUtils.isEmpty(code)){
			address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
		}else{
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				//boolean isSucces = Utility.handleProvincesResponse(coolWeatherDB, response);
				boolean result = false;
				if ("province".equals(type)) {
					result = Utility.handleProvincesResponse(coolWeatherDB,response);
				} else if ("city".equals(type)) {
					result = Utility.handleCitiesResponse(coolWeatherDB,
												response, selectedProvince.getId());
				} else if ("county".equals(type)) {
					result = Utility.handleCountiesResponse(coolWeatherDB,
												response, selectedCity.getId());
				}
				if (result) {
					// 1.通过runOnUiThread()方法回到主线程处理逻辑
					/*
					ChooseAreaActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							closeProgressDialog();
							if ("province".equals(type)) {
								queryProvinces();
							} else if ("city".equals(type)) {
								queryCities();
							} else if ("county".equals(type)) {
								queryCounties();
							}
						}
					});
					*/
					//2.通过handle来更新UI
					Message msg = new Message();
					msg.what = 0;
					msg.obj = type;
					handler.sendMessage(msg);
				}
			}
			
			@Override
			public void onError(Exception e) {
				// 通过runOnUiThread()方法回到主线程处理逻辑
				/*
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this,
											"加载失败", Toast.LENGTH_SHORT).show();
					}
				});
				*/
				//2.通过handle来更新UI
				Message msg = new Message();
				msg.what = 1;
				handler.sendMessage(msg);
				
			}
		});
	}
	
	/**
	* 显示进度对话框
	*/
	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	/**
	* 关闭进度对话框
	*/
	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}
	
	
	/**
	* 捕获Back按键，根据当前的级别来判断，此时应该返回市列表、省列表、还是直接退出。
	*/
	@Override
	public void onBackPressed() {
		if (currentLevel == LEVEL_COUNTY) {
			queryCities();
		} else if (currentLevel == LEVEL_CITY) {
			queryProvinces();
		} else {
			finish();
		}
	}
	
}
