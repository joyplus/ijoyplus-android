package com.joyplus;

import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.UMShareMsg;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import com.androidquery.AQuery;

public class Sina_Share extends Activity {
	private App app;
	private AQuery aq;
	private String prod_name = null;
	private static final int MAX_COUNT = 120;
	private MultiAutoCompleteTextView mEditText = null;
	private TextView tv_count = null;
	private Context mContext;
	private static String ue_sina_share = "新浪微博分享";

	public static final String DESCRIPTOR = "joyplus";
	UMSocialService controller;

	UMShareMsg shareMsg;

	// 设置分享文字

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sina_share);
		app = (App) getApplication();
		aq = new AQuery(this);
		mContext = this;
		Intent intent = getIntent();
		prod_name = intent.getStringExtra("prod_name");
		aq.id(R.id.program_name).text(prod_name);
		prod_name = "我在用#悦视频#Android版观看<" + prod_name
				+ ">，推荐给大家哦！更多精彩尽在悦视频，欢迎下载：http://ums.bz/REGLDb/，快来和我一起看吧！";
		aq.id(R.id.multiAutoCompleteTextView1).text(prod_name);
		aq.id(R.id.multiAutoCompleteTextView1).getEditText()
				.setSelection(prod_name.length());
		tv_count = (TextView) findViewById(R.id.count);
		mEditText = (MultiAutoCompleteTextView) findViewById(R.id.multiAutoCompleteTextView1);
		mEditText.addTextChangedListener(mTextWatcher);
		setLeftCount();

		controller = UMServiceFactory.getUMSocialService(DESCRIPTOR,
				RequestType.SOCIAL);
		shareMsg = new UMShareMsg();

	}

	private TextWatcher mTextWatcher = new TextWatcher() {

		private int editStart;

		private int editEnd;

		@Override
		public void afterTextChanged(Editable s) {
			editStart = mEditText.getSelectionStart();
			editEnd = mEditText.getSelectionEnd();

			mEditText.removeTextChangedListener(mTextWatcher);

			while (calculateLength(s.toString()) > MAX_COUNT) { // �������ַ��������ƵĴ�Сʱ�����нضϲ���
				s.delete(editStart - 1, editEnd);
				editStart--;
				editEnd--;
			}
			// aq.id(R.id.multiAutoCompleteTextView1).text(s);
			aq.id(R.id.multiAutoCompleteTextView1).setSelection(editStart);

			// �ָ�������
			mEditText.addTextChangedListener(mTextWatcher);

			setLeftCount();
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

	};

	private long calculateLength(CharSequence c) {
		double len = 0;
		for (int i = 0; i < c.length(); i++) {
			// int tmp = c.charAt(i);
			len++;
		}
		return Math.round(len);
	}

	private void setLeftCount() {
		tv_count.setText(String.valueOf((MAX_COUNT - getInputCount())));
	}

	private long getInputCount() {
		return calculateLength(aq.id(R.id.multiAutoCompleteTextView1).getText()
				.toString());
	}

	public void OnClickTab1TopLeft(View v) {
		finish();
	}

	public void OnClickShare(View v) {
		InputMethodManager imm = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		aq.id(R.id.multiAutoCompleteTextView1).getTextView()
				.setCursorVisible(false);// 失去光标
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

		String share_word = aq.id(R.id.multiAutoCompleteTextView1).getText()
				.toString();
		shareMsg.text = share_word;
		sina_shared();
		finish();
	}

	private void sina_shared() {

		controller.postShare(mContext, SHARE_MEDIA.SINA, shareMsg,
				new SnsPostListener() {
					@Override
					public void onStart() {
					}

					@Override
					public void onComplete(SHARE_MEDIA platform, int eCode,
							SocializeEntity entity) {
						if (eCode == 200) {
							MobclickAgent.onEvent(mContext, ue_sina_share);
							app.MyToast(mContext, "分享成功");
						} else {
							String eMsg = "";
							if (eCode == -101)
								eMsg = "没有授权";

							app.MyToast(mContext, "分享失败 " + eMsg);
						}
					}
				});

	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}
}
