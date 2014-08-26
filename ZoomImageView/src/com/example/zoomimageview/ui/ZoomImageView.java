package com.example.zoomimageview.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ZoomImageView extends View {
	private static final int STATUS_INIT=0;
	private static final int STATUS_LARGE=1;
	private static final int STATUS_MINI=2;
	private static final int STATUS_MOVE=3;
	private int statusCurr=0;
	private static Matrix matrix=new Matrix();
	private static float lastX,lastY;
	//最后一次x位置
	private float lastXMove = -1;
	//最后一次y位置
    private float lastYMove = -1;  
	//x方向移动距离
	private float movedDistanceX;
	//y方向移动距离
    private float movedDistanceY; 
	private Bitmap bitmap;
	public ZoomImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			System.out.println("down");
			if(event.getPointerCount()==2){
				getStartCenter(event);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			System.out.println("move");
			if(event.getPointerCount()==1){
				System.out.println("move");
				statusCurr=STATUS_MOVE;
				float xMove = event.getX();  
                float yMove = event.getY();
                if (lastXMove == -1 && lastYMove == -1) {  
                    lastXMove = xMove;  
                    lastYMove = yMove;  
                }  
                movedDistanceX = xMove - lastXMove;  
                movedDistanceY = yMove - lastYMove;
                invalidate();  
                lastXMove = xMove;  
                lastYMove = yMove;
			}else if(event.getPointerCount()==2){
				
			}
			break;
		case MotionEvent.ACTION_UP:
			 lastXMove = -1;  
             lastYMove = -1;
			break;
		default:
			break;
		}
		//这一点很重要，view本身不可点击,down之后返回false,截断其他action
		return true;
		
	}
	private void getStartCenter(MotionEvent event) {
		event.getX(0);
		event.getX(1);
		event.getY(0);
		event.getY(1);
		
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap=bitmap;
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		switch (statusCurr) {
		case STATUS_MOVE:
			move(canvas);
		default:
			canvas.drawBitmap(bitmap,matrix,null);
			break;
		}
		
	}
	private void move(Canvas canvas) {
		matrix.postTranslate(movedDistanceX, movedDistanceY);
		canvas.drawBitmap(bitmap, matrix, null);
	}

}
