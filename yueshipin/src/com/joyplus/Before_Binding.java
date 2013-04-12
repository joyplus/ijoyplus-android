package com.joyplus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class Before_Binding extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.before_binding);
		ImageButton confirmBinding = (ImageButton)findViewById(R.id.confirm_binding);
		confirmBinding.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			Intent intent = new Intent(Before_Binding.this, Relieve_Binding.class);
			startActivity(intent);
			finish();	
			}
		});
	}
	public void OnClickTab1TopLeft(View v){
		finish();
	}
}
