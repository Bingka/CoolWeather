package com.bingka.weathercool;

public interface HttpCallbackListener {
	
	void onFinish(String response);
	
	void onError(Exception e);
}
