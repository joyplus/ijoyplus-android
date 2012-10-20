package com.joy;

import android.app.Application;
import android.content.SharedPreferences;

public class GetThird_AccessToken extends Application {
	String AccessToken = "";
	String VerificationCode = "";
	String phoneName="";
	String phoneNum="";
	String Activitytype="";
	String dinayingName="";
	String Expires_in = "";
	String Button_Name = "";
	String OpenID = "";
	public void setOpenID(String OpenID){
		this.OpenID=OpenID;
	}
	public String getOpenID(){
		return OpenID;
	}
	
	public void setButton_Name(String Button_Name){
		this.Button_Name=Button_Name;
	}
	public String getButton_Name(){
		return Button_Name;
	}
	
	public void setdinayingName(String a){
		dinayingName=a;
	}
	public String getdinayingName(){
		return dinayingName;
	}
	public void setExpires_in(String Expires_in){
		this.Expires_in=Expires_in;
	}
	public String getExpires_in(){
		return Expires_in;
	}
	
	public void setActivitytype(String a){
		Activitytype=a;
	}
	public String getActivitytype(){
		return Activitytype;
	}
	public void setphoneNum(String n){
		phoneNum=n;
	}
	public String getphoneNum(){
		return phoneNum;
	}
	public void setphoneName(String n){
		phoneName=n;
	}
	public String getphoneName(){
		return phoneName;
	}
	String login_where = "";
	//判断哪个接入点
	public void setlogin_where(String login_where)
	{
		this.login_where = login_where;
		
	}
	public String getlogin_where()
	{
		return login_where;
	}
	//唯一值
	public void setAccessToken(String AccessToken)
	{
		this.AccessToken = AccessToken;
		
	}
	public String getAccessToken()
	{
		return AccessToken;
	}
	//验证码
	public void setVerificationCode(String VerificationCode)
	{
		this.VerificationCode = VerificationCode;
		
	}
	public String getVerificationCode()
	{
		return VerificationCode;
	}
	
	public void SaveAccessToken()
	{
		SharedPreferences.Editor sharedatab = getSharedPreferences("AccessToken_"+login_where, 0).edit();
		sharedatab.putString("AccessToken", AccessToken);
		sharedatab.commit();
	}
	public void GetAccessToken()
	{
		SharedPreferences sharedata = getSharedPreferences("AccessToken_"+login_where, 0);
		AccessToken=sharedata.getString("AccessToken", "");
	}
	
	// 保存新浪Expires_in值
	public void SaveExpires_in()
	{
		SharedPreferences.Editor sharedatab = getSharedPreferences("Expires_in", 0).edit();
		sharedatab.putString("Expires_in", Expires_in);
		sharedatab.commit();
	}
	public void GetExpires_in()
	{
		SharedPreferences sharedata = getSharedPreferences("Expires_in", 0);
		Expires_in=sharedata.getString("Expires_in", "");
	}
	//保存QQ的OpenID
	public void SaveOpenID()
	{
		SharedPreferences.Editor sharedatab = getSharedPreferences("OpenID", 0).edit();
		sharedatab.putString("OpenID", OpenID);
		sharedatab.commit();
	}
	public void GetOpenID()
	{
		SharedPreferences sharedata = getSharedPreferences("OpenID", 0);
		OpenID=sharedata.getString("OpenID", "");
	}
}
