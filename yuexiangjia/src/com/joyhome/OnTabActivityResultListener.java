package com.joyhome;

import android.content.Intent;

/**
 * 解决子Activity无法接收Activity回调的问题
 * @author Administrator
 *
 */
public interface OnTabActivityResultListener {
    public void onTabActivityResult(int requestCode, int resultCode, Intent data);
}