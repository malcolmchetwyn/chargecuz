package com.shoppinghook.chargecuz;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.shoppinghook.chargecuz.R;

public class SplashActivity extends Activity 
{
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
	      new Handler().postDelayed(new Runnable() 
	      {	            
	            @Override
	            public void run() 
	            {
	                Intent i = new Intent(SplashActivity.this, MainActivity.class);
	                startActivity(i);
	                finish();
	            }
	        }, 1000);
	}
}