package com.shoppinghook.chargecuz;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.shoppinghook.chargecuz.R;

public class MainActivity extends Activity implements OnSeekBarChangeListener{

	TextView topLabel;
	TextView centerLabel;
	TextView bottomLabel;
	
	TextView topDisable;
	TextView centerDisable;
	TextView bottomDisable;
	
	SeekBar topBar = null;
	SeekBar centerBar = null;
	SeekBar bottomBar = null;

	int batteryLevel = 0;
	static int topValue = 0;
	static int centerValue = 0;
	static int bottomValue = 0;

	public static Context context; 
	static int previous = 0;
	public static HashMap<Integer, Integer> _meta = new HashMap<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
		                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		metaData();
		context = this;
		setContentView(R.layout.activity_main);		


		topLabel = (TextView)findViewById(R.id.topLabel);
		centerLabel = (TextView)findViewById(R.id.centerLabel);
		bottomLabel = (TextView)findViewById(R.id.bottomLabel);

		topBar = (SeekBar)findViewById(R.id.seekBar1);
		centerBar = (SeekBar)findViewById(R.id.seekBar2);
		bottomBar = (SeekBar)findViewById(R.id.seekBar3);
		topBar.setOnSeekBarChangeListener(this);
		centerBar.setOnSeekBarChangeListener(this);
		bottomBar.setOnSeekBarChangeListener(this);		

		startService(new Intent(getBaseContext(), MyService.class));
		setFonts();

	}	


	public void setFonts()
	{

		Typeface tf = Typeface.createFromAsset(getAssets(),"fonts/OpenSans-Bold.ttf");
		topLabel.setTypeface(tf);
		centerLabel.setTypeface(tf);
		bottomLabel.setTypeface(tf);

		tf = Typeface.createFromAsset(getAssets(),"fonts/OpenSans-Regular.ttf");
		TextView label = (TextView)findViewById(R.id.topLabelStart);
		label.setTypeface(tf);
		label = (TextView)findViewById(R.id.topLabelEnd);
		label.setTypeface(tf);
		label = (TextView)findViewById(R.id.centerLabelStart);
		label.setTypeface(tf);
		label = (TextView)findViewById(R.id.centerLabelEnd);
		label.setTypeface(tf);
		label = (TextView)findViewById(R.id.bottomLabelStart);
		label.setTypeface(tf);
		label = (TextView)findViewById(R.id.bottomLabelEnd);
		label.setTypeface(tf);
		
		tf = Typeface.createFromAsset(getAssets(),"fonts/phillysansps.otf");
		topDisable = (TextView)findViewById(R.id.topDisableLabel);
		topDisable.setTypeface(tf);
		centerDisable = (TextView)findViewById(R.id.centerDisableLabel);
		centerDisable.setTypeface(tf);
		bottomDisable = (TextView)findViewById(R.id.bottomDisableLabel);
		bottomDisable.setTypeface(tf);
	}

	private void metaData()
	{		
		int step = 10;
		int multiplier = 0;
		for(int i = 6; i < 96; i+=step)
		{
			multiplier ++;
			for(int j=i; j<i+step; j++)
			{
				_meta.put(j, 10*multiplier);
			}
		}
//
//		for(Integer key : _meta.keySet())
//		{
//			System.out.println("**************** "+key + " == " + _meta.get(key));
//		}
	}

	public static int getBatteryPercentage(int progress)
	{
		int percentage = 0;
		if(progress < 6){
			percentage = 0;
		} else if(progress > 90) {
			percentage = 90;
		} else {
			percentage = _meta.get(progress);
		}
		return percentage;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) 
	{
		progress = getBatteryPercentage(progress);				
		String label = progress+"%";
		if(seekBar == topBar) {			 
			topLabel.setText(label);
		}
		else if(seekBar== centerBar){
			centerLabel.setText(label);
		}		
		else if (seekBar == bottomBar){
			bottomLabel.setText(label);
		}

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {	
//		seekBar.setProgressDrawable(getResources().getDrawable(R.drawable.slider_bar_select));
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		int currentProgress = getBatteryPercentage(seekBar.getProgress()); 
		if(seekBar == topBar) {
			if((currentProgress == centerValue && centerValue != 0) || (currentProgress == bottomValue && bottomValue != 0)){
				seekBar.setProgress(topValue);
			}else {
				topValue = currentProgress;
				saveValue("topValue", topValue);
			}
		}
		else if(seekBar== centerBar){
			if((currentProgress == topValue && topValue != 0) || (currentProgress == bottomValue && bottomValue !=0)){
				seekBar.setProgress(centerValue);
			}else {
				centerValue = currentProgress;
				saveValue("centerValue", centerValue);
			}
		}		
		else if (seekBar == bottomBar){
			if((currentProgress == topValue && topValue !=0) || (currentProgress == centerValue && centerValue !=0)){
				seekBar.setProgress(bottomValue);
			}else {
				bottomValue = currentProgress;
				saveValue("bottomValue", bottomValue);
			}
		}
		manageVisiblity();
		startService(new Intent(getBaseContext(), MyService.class));
	}
	
	public void onTopClicked(View v)
	{
		playSound(topValue);
	}
	public void onCenterClicked(View v)
	{
		playSound(centerValue);
	}
	public void onBottomClicked(View v)
	{
		playSound(bottomValue);
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
	private int getSoundForPercentage(int percentage)
	{
		if(percentage == 10) {
			return R.raw.percent_10;
		} else if(percentage == 20){
			return R.raw.percent_20;
		} else if(percentage == 30){
			return R.raw.percent_30;
		} else if(percentage == 40){
			return R.raw.percent_40;
		} else if(percentage == 50){
			return R.raw.percent_50;
		} else if(percentage == 60){
			return R.raw.percent_60;
		} else if(percentage == 70){
			return R.raw.percent_70;
		} else if(percentage == 80){
			return R.raw.percent_80;
		} else if(percentage == 90){
			return R.raw.percent_90;
		}		
		return 0;
	}
	
	public void playSound(int percentage)
	{
		int sound = getSoundForPercentage(percentage);
		if(sound == 0)
		{return;}
		MediaPlayer mp = MediaPlayer.create(this, sound);
		mp.start();
	}
		
	@Override
	protected void onResume() {
		super.onResume();
		topValue = getValue("topValue");
		this.topBar.setProgress(topValue);
		centerValue = getValue("centerValue");
		this.centerBar.setProgress(centerValue);
		bottomValue = getValue("bottomValue");
		this.bottomBar.setProgress(bottomValue);
		manageVisiblity();
	}
	
	private void manageVisiblity()
	{
		if(topValue == 0){
			topDisable.setVisibility(View.VISIBLE);
		} else {
			topDisable.setVisibility(View.INVISIBLE);
		}
		if(centerValue == 0){
			centerDisable.setVisibility(View.VISIBLE);
		} else {
			centerDisable.setVisibility(View.INVISIBLE);
		}
		if(bottomValue == 0){
			bottomDisable.setVisibility(View.VISIBLE);
		}else{
			bottomDisable.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();		
	}
}
