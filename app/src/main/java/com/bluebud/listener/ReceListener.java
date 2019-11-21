package com.bluebud.listener;

public interface ReceListener {
	void onRequestFailure(int payNetworkError, int statue);

	void onSetSuccess(int Result);

	void onReceiveData(String wifiName, String wifiPassword);
	

}
