package com.shoppinghook.chargecuz;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.shoppinghook.chargecuz.R;

public class MyService extends Service 
{

	private boolean mRunning;	
	int batteryLevel = 0;
	static int topValue = 0;
	static int centerValue = 0;
	static int bottomValue = 0;	
	static int previous = 0;
	public static Context context;	
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		context = this;
		mRunning = false;
	}
	
	private BroadcastReceiver mBroadCastReceiver = new BroadcastReceiver() {		
		@Override
		public void onReceive(Context context, Intent intent) {
			batteryLevel = intent.getIntExtra("level", 0);
//			batteryLevel = MainActivity.getBatteryPercentage(batteryLevel);
			if(batteryLevel == topValue || batteryLevel == centerValue || batteryLevel == bottomValue) {
				if(previous != batteryLevel)
				{
					notifyUser();
				}
			 	previous = batteryLevel;
			}							
		}
	};
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		if(!mRunning){
			this.registerReceiver(mBroadCastReceiver,
					new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
			mRunning = true;
		}
		topValue = getValue("topValue");
		bottomValue = getValue("bottomValue");
		centerValue = getValue("centerValue");
				
		return START_STICKY;
	}
	
	public static void saveValue(String key, int value)
	{
		SharedPreferences.Editor editor = context.getSharedPreferences("Charge_Pref", MainActivity.MODE_PRIVATE).edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public static int getValue(String key)
	{
		int count = 0;
		SharedPreferences pref = context.getSharedPreferences("Charge_Pref", MainActivity.MODE_PRIVATE);
		count = pref.getInt(key, 0);
		return count;
	}
	public void notifyUser(){
		 
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(this)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle("Battery notification")
		        .setContentText("battery level is "+batteryLevel+"%");
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(this, MainActivity.class);

		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(MainActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =
		    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mBuilder.setSound(Uri.parse(getSoundForPercentage(batteryLevel)));
		// mId allows you to update the notification later on.
		mNotificationManager.notify(1, mBuilder.build()); 
	}
		
	private String getSoundForPercentage(int percentage)
	{
		String uri = "android.resource://" + getPackageName() + "/";
		if(percentage == 10) {
			uri += R.raw.percent_10;
		} else if(percentage == 20){
			uri += R.raw.percent_20;
		} else if(percentage == 30){
			uri += R.raw.percent_30;
		} else if(percentage == 40){
			uri += R.raw.percent_40;
		} else if(percentage == 50){
			uri += R.raw.percent_50;
		} else if(percentage == 60){
			uri += R.raw.percent_60;
		} else if(percentage == 70){
			uri += R.raw.percent_70;
		} else if(percentage == 80){
			uri += R.raw.percent_80;
		} else if(percentage == 90){
			uri += R.raw.percent_90;
		}		
		return uri;
	}

	
	public void playSound(int perc)
	{
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
