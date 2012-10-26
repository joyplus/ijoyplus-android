package com.joy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SupplementaryInformation extends Activity {
	GetThird_AccessToken getThird_AccessToken;
	Button finish;
	EditText imformation_name,imformation_email,imformation_password;
	Context context;
	Intent in;
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.supplementaryinformation);
        context = this;
        getThird_AccessToken = (GetThird_AccessToken) getApplicationContext();
        finish = (Button) findViewById(R.id.finish);
        imformation_name = (EditText) findViewById(R.id.imformation_name);
        imformation_email = (EditText) findViewById(R.id.imformation_email);
        imformation_password = (EditText) findViewById(R.id.imformation_password);
        
        finish.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int isFinish = 1;
				String error = getString(R.string.error);
				if (!isName(imformation_name.getText().toString().trim())) {
					error += " "+getString(R.string.imformation_nameerror);
					isFinish = 0;
				}
				if (!isEmail(imformation_email.getText().toString().trim())) {
					error += " "+getString(R.string.emailerror);
					isFinish = 0;
					}
				if (imformation_password.getText().toString().trim().length()<6) {
					error += " "+getString(R.string.passworderror);
					isFinish = 0;
				}
				switch(isFinish)
				{
				case 0:
					Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
					break;
				case 1:
					//新浪token
					if (getThird_AccessToken.getlogin_where().equals(getString(R.string.sinawb))) {
						getThird_AccessToken.setAccessToken(getIntent().getStringExtra("token"));
						getThird_AccessToken.setExpires_in(getIntent().getStringExtra("expires_in"));
						getThird_AccessToken.SaveAccessToken();
						getThird_AccessToken.SaveExpires_in();
					}
					//QqTOKEN
					else if (getThird_AccessToken.getlogin_where().equals(getString(R.string.tencent))) 
					{
						getThird_AccessToken.setQQ_Token(getIntent().getStringExtra("token"));
						getThird_AccessToken.SaveQQAccessToken();
//						getThird_AccessToken.GetQQAccessToken();
//						System.out.println("token:"+getThird_AccessToken.getQQ_Token());
					}
					Intent intent = new Intent();
					if (getThird_AccessToken.getwhere_gologin()==1) {
						
					}
					else
					{
						getThird_AccessToken.setActivitytype("1");
						intent.setClass(context, Darentuijian.class);
						startActivity(intent);
					}
					finish();
					break;
				}
			}
		});
	}
	
	
	/*正则表达*/
	public static boolean isEmail(String strEmail) {

		String strPattern = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";

		Pattern p = Pattern.compile(strPattern);

		Matcher m = p.matcher(strEmail);

		return m.matches();

		}
	public static boolean isName(String strName) {

		String strPattern = "^[\\u4e00-\\u9fa5\\w\\d_]{1,16}$";

		Pattern p = Pattern.compile(strPattern);

		Matcher m = p.matcher(strName);

		return m.matches();

		}
	
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
    	switch(keyCode){
        case KeyEvent.KEYCODE_BACK:
        	break;
    	}
        return true;
    }
}
