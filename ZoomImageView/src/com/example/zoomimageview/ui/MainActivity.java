package com.example.zoomimageview.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.example.zoomimageview.R;



public class MainActivity extends Activity {
	private ZoomImageView image;
	private Bitmap bitmap=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		image=(ZoomImageView) findViewById(R.id.iv_face);
		bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.a);
		image.setBitmap(bitmap);
	}
}