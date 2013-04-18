package com.joyplus.faye;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

public class FayeReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		if(action.equals(ConnectivityManager.CONNECTIVITY_ACTION)){
			boolean noConnectivity = intent.getBooleanExtra(
					ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
			if(noConnectivity) {
				
				
			}
			else if(!noConnectivity) {
				Intent serviceIntent = new Intent(context, FayeService.class);          
		        context.startService(serviceIntent);  
			}
		}
	}

}
