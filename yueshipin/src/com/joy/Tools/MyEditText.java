package com.joy.Tools;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.widget.EditText;


public class MyEditText extends EditText {

 public MyEditText(Context context) {
  super(context);
 }

 public MyEditText(Context context, AttributeSet attrs) {
  super(context, attrs);
 }

 @Override
 protected void onDraw(Canvas canvas) {
  super.onDraw(canvas);
 }
 
 //在编辑框顶部添加图片
 public void setDrawableTop(Drawable top) {
  setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
 }
 //在编辑框顶部添加图片
 public void setDrawableTop(int top) {
  setCompoundDrawablesWithIntrinsicBounds(0, top, 0, 0);
 }
 //在编辑框顶部左边图片
 public void setDrawableRight(int right) {
  setCompoundDrawablesWithIntrinsicBounds(0, 0, right, 0);
 }
 public void setDrawableRight(Drawable right) {
  setCompoundDrawablesWithIntrinsicBounds(null, null, right, null);
}
 //在编辑框左边添加图片
 public void setDrawableLeft(int left) {
  setCompoundDrawablesWithIntrinsicBounds(left, 0, 0, 0);
 }
 //在编辑框底部添加图片
 public void setDrawableButtom(int buttom) {
  setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, buttom);
 }

 
 @Override
 protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
  super.onMeasure(widthMeasureSpec, heightMeasureSpec);
 }
 //在编辑框内添加图片或者表情
 public void insertIcon(int id) {
  SpannableString ss = new SpannableString(getText().toString()
    + "[smile]");//new一个SpannableString里面包含EditText已有内容，另外添加一个字符串[smile]用于在后面替换一个图片
  Drawable d = getResources().getDrawable(id);
  d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
  ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);//将图片实例化为一个ImageSpan型
  ss.setSpan(span, getText().length(),
    getText().length() + "[smile]".length(),
    Spannable.SPAN_INCLUSIVE_EXCLUSIVE);//将ImageSpan代替之前添加的[smile]字符串
  setText(ss);
 }
}

