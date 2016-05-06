package com.bingka.weathercool;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity {

	private LinearLayout weatherInfoLayout;
	/**
	* 用于显示城市名
	*/
	private TextView cityNameText;
	/**
	* 用于显示发布时间
	*/
	private TextView publishText;
	/**
	* 用于显示天气描述信息
	*/
	private TextView weatherDespText;
	/**
	* 用于显示气温1
	*/
	private TextView temp1Text;
	/**
	* 用于显示气温2
	*/
	private TextView temp2Text;
	/**
	* 用于显示当前日期
	*/
	private TextView currentDateText;
	/**
	* 切换城市按钮
	*/
	private Button switchCity;
	/**
	* 更新天气按钮
	*/
	private Button refreshWeather;
	
	private static final int MESSAGE_FINISH = 0;
	
	private static final int MESSAGE_ERROR = 1;
	
	@SuppressLint("HandlerLeak") private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_FINISH:
				showWeather();
				break;
			case MESSAGE_ERROR:
				publishText.setText("同步失败");
				break;
			default:
				break;
			}
		};
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//必须放在setContentView之前不然报错
		//android.util.AndroidRuntimeException: requestFeature() must be called before adding content
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		initView();
		String countyCode = getIntent().getStringExtra("countyCode");
		android.util.Log.d("[YY]","countyCode = "+countyCode);
		if (!TextUtils.isEmpty(countyCode)) {
			// 有县级代号时就去查询天气
			publishText.setText("同步中...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		} else {
			// 没有县级代号时就直接显示本地天气
			showWeather();
		}
			
	}
	
	/**
	 * 从SharedPreferences文件中读取存储的天气信息，并显示到界面上。
	 */
	private void showWeather() {
		SharedPreferences prefs = getSharedPreferences("WeatherInfo2", 0);
		cityNameText.setText( prefs.getString("city", ""));
		temp1Text.setText(prefs.getString("temp2", ""));
		temp2Text.setText(prefs.getString("temp1", ""));
		weatherDespText.setText(prefs.getString("weather", ""));
		//publishText.setText("今天" + prefs.getString("ptime", "") + "发布");
		currentDateText.setText(prefs.getString("current_data", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 获取对应县的天气代号
	 * @param countyCode
	 */
	private void queryWeatherCode(String countyCode) {	
		String address = "http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
		
		queryFromServer(address, "countyCode");
	}
	
	/**
	* 查询天气代号所对应的天气。
	*/
	private void queryWeatherInfo(String weatherCode) {
		//由于数据很不准确，修改address。
		//String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
		String address = "http://wthrcdn.etouch.cn/weather_mini?citykey="+weatherCode;
		queryFromServer(address, "weatherCode");
	}
	
	/**
	 * 根据address查询到对应内容(1.县级天气代号;2.对应县的天气信息)
	 * @param address：1.县级代号网址;2县级天气代号网址
	 * @param string："countyCode":县级代号;"weatherCode":县级天气代号
	 */
	private void queryFromServer(String address, final String string) {
		
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				if ("countyCode".equals(string)) {
					// 从服务器返回的数据中解析出天气代号
					if (!TextUtils.isEmpty(response)) {
						String[] strings = response.split("\\|");
						if (strings != null && strings.length == 2) {
							queryWeatherInfo(strings[1]);
						}
					}
				}else if ("weatherCode".equals(string)) {
					// 处理服务器返回的天气信息
					Utility.handleWeatherResponse(WeatherActivity.this, response);
					//1.通过runOnUiThread()方法回到主线程处理逻辑
					/*
					runOnUiThread(new Runnable() {		
						@Override
						public void run() {
							showWeather();
						}
					});
					*/
					Message message = new Message();
					message.what = MESSAGE_FINISH;
					handler.sendMessage(message);
				}
			}
			
			@Override
			public void onError(Exception e) {
				/*
				runOnUiThread(new Runnable() {		
					@Override
					public void run() {
						publishText.setText("同步失败");
					}
				});
				*/
				Message message = new Message();
				message.what = MESSAGE_ERROR;
				handler.sendMessage(message);
			}
		});
		
	}

	private void initView() {
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText = (TextView) findViewById(R.id.city_name);
		publishText = (TextView) findViewById(R.id.publish_text);
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		temp1Text = (TextView) findViewById(R.id.temp1);
		temp2Text = (TextView) findViewById(R.id.temp2);
		currentDateText = (TextView) findViewById(R.id.current_date);
		//switchCity = (Button) findViewById(R.id.switch_city);
		//refreshWeather = (Button) findViewById(R.id.refresh_weather);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
