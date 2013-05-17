package com.joyplus;

import android.app.Activity;
import android.os.Bundle;

/*
 * 清除系统默认的专用activity
 */
public class DefaultActivity extends Activity
{
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    finish();
  }
}