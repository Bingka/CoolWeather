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
	* ������ʾ������
	*/
	private TextView cityNameText;
	/**
	* ������ʾ����ʱ��
	*/
	private TextView publishText;
	/**
	* ������ʾ����������Ϣ
	*/
	private TextView weatherDespText;
	/**
	* ������ʾ����1
	*/
	private TextView temp1Text;
	/**
	* ������ʾ����2
	*/
	private TextView temp2Text;
	/**
	* ������ʾ��ǰ����
	*/
	private TextView currentDateText;
	/**
	* �л����а�ť
	*/
	private Button switchCity;
	/**
	* ����������ť
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
				publishText.setText("ͬ��ʧ��");
				break;
			default:
				break;
			}
		};
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//�������setContentView֮ǰ��Ȼ����
		//android.util.AndroidRuntimeException: requestFeature() must be called before adding content
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		initView();
		String countyCode = getIntent().getStringExtra("countyCode");
		android.util.Log.d("[YY]","countyCode = "+countyCode);
		if (!TextUtils.isEmpty(countyCode)) {
			// ���ؼ�����ʱ��ȥ��ѯ����
			publishText.setText("ͬ����...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		} else {
			// û���ؼ�����ʱ��ֱ����ʾ��������
			showWeather();
		}
			
	}
	
	/**
	 * ��SharedPreferences�ļ��ж�ȡ�洢��������Ϣ������ʾ�������ϡ�
	 */
	private void showWeather() {
		SharedPreferences prefs = getSharedPreferences("WeatherInfo2", 0);
		cityNameText.setText( prefs.getString("city", ""));
		temp1Text.setText(prefs.getString("temp2", ""));
		temp2Text.setText(prefs.getString("temp1", ""));
		weatherDespText.setText(prefs.getString("weather", ""));
		//publishText.setText("����" + prefs.getString("ptime", "") + "����");
		currentDateText.setText(prefs.getString("current_data", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
	}
	
	/**
	 * ��ȡ��Ӧ�ص���������
	 * @param countyCode
	 */
	private void queryWeatherCode(String countyCode) {	
		String address = "http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
		
		queryFromServer(address, "countyCode");
	}
	
	/**
	* ��ѯ������������Ӧ��������
	*/
	private void queryWeatherInfo(String weatherCode) {
		//�������ݺܲ�׼ȷ���޸�address��
		//String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
		String address = "http://wthrcdn.etouch.cn/weather_mini?citykey="+weatherCode;
		queryFromServer(address, "weatherCode");
	}
	
	/**
	 * ����address��ѯ����Ӧ����(1.�ؼ���������;2.��Ӧ�ص�������Ϣ)
	 * @param address��1.�ؼ�������ַ;2�ؼ�����������ַ
	 * @param string��"countyCode":�ؼ�����;"weatherCode":�ؼ���������
	 */
	private void queryFromServer(String address, final String string) {
		
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				if ("countyCode".equals(string)) {
					// �ӷ��������ص������н�������������
					if (!TextUtils.isEmpty(response)) {
						String[] strings = response.split("\\|");
						if (strings != null && strings.length == 2) {
							queryWeatherInfo(strings[1]);
						}
					}
				}else if ("weatherCode".equals(string)) {
					// ������������ص�������Ϣ
					Utility.handleWeatherResponse(WeatherActivity.this, response);
					//1.ͨ��runOnUiThread()�����ص����̴߳����߼�
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
						publishText.setText("ͬ��ʧ��");
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
