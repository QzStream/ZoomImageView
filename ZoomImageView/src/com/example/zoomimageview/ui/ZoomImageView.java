package com.example.zoomimageview.ui;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class ZoomImageView extends View {
	private static final int STATUS_INIT=0;
	private static final int STATUS_LARGE=1;
	private static final int STATUS_MINI=2;
	private static final int STATUS_MOVE=3;
	private int statusCurr=0;
	private static Matrix matrix=new Matrix();
	private float centerPointX,centerPointY;
	private int width,height;
	private float totalRatio;
	//最后一次x位置
	private float lastXMove = -1;
	//最后一次y位置
    private float lastYMove = -1;
    private float totalTranslateX=0;  
    /** 
     * 记录图片在矩阵上的纵向偏移值 
     */  
    private float totalTranslateY=0;
	//x方向移动距离
	private float movedDistanceX;
	//y方向移动距离
    private float movedDistanceY; 
    /**
	 * 记录当前图片的宽度，图片被缩放时，这个值会一起变动
	 */
	private float currentBitmapWidth;

	/**
	 * 记录当前图片的高度，图片被缩放时，这个值会一起变动
	 */
	private float currentBitmapHeight;
    private float firstDistance,lastDistance;
	private Bitmap bitmap;
	private Context context;
	private float initRatio;
	public ZoomImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context=context;
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_POINTER_DOWN:
			if(event.getPointerCount()==2){
				firstDistance=getDistance(event);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if(event.getPointerCount()==1){
				statusCurr=STATUS_MOVE;
				float xMove = event.getX();  
                float yMove = event.getY();
                if (lastXMove == -1 && lastYMove == -1) {  
                    lastXMove = xMove;  
                    lastYMove = yMove;  
                }  
                movedDistanceX = xMove - lastXMove;  
                movedDistanceY = yMove - lastYMove;
                if (totalTranslateX + movedDistanceX > 0) {
					movedDistanceX = 0;
				} else if (width - (totalTranslateX + movedDistanceX) > currentBitmapWidth) {
					movedDistanceX = 0;
				}
				if (totalTranslateY + movedDistanceY > 0) {
					movedDistanceY = 0;
				} else if (height - (totalTranslateY + movedDistanceY) > currentBitmapHeight) {
					movedDistanceY = 0;
				}
                
                
                invalidate();  
                lastXMove = xMove;  
                lastYMove = yMove;
			}else if(event.getPointerCount()==2){
				getCenter(event);
				
				lastDistance=getDistance(event);
				if((int)firstDistance==0){
					firstDistance=lastDistance;
				}
				if(lastDistance>firstDistance){
					statusCurr=STATUS_LARGE;
				}else{
					statusCurr=STATUS_MINI;
				}
				Log.i("why",firstDistance+"........");
				totalRatio=totalRatio*(float)(lastDistance/firstDistance);				
				Log.i("why",totalRatio+"");
				invalidate();
				firstDistance=lastDistance;
			}
			break;
		case MotionEvent.ACTION_POINTER_UP:  
            if (event.getPointerCount() == 2) {  
                // 手指离开屏幕时将临时值还原  
                lastXMove = -1;  
                lastYMove = -1;  
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
	private float getDistance(MotionEvent event) {
		float disX=event.getX(0)-event.getX(1);
		float disY=event.getY(0)-event.getY(1);
		return (float) Math.sqrt(disX*disX+disY*disY);
		
	}
	private void getCenter(MotionEvent event) {
		centerPointX=(event.getX(0)+event.getX(1))/2;
		centerPointY=(event.getY(0)+event.getY(1))/2;
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
			break;
		case STATUS_INIT:
			initBitmap(canvas);
			break;
		case STATUS_LARGE:
			zoom(canvas);
			break;
		case STATUS_MINI:
			zoom(canvas);
			break;
		default:
			break;
		}
		
	}
	private void zoom(Canvas canvas) {
		matrix.reset();
		currentBitmapHeight=bitmap.getHeight()*totalRatio;
		currentBitmapWidth=bitmap.getWidth()*totalRatio;
		matrix.postScale(totalRatio, totalRatio);
		canvas.drawBitmap(bitmap, matrix, null);
	}
	private void move(Canvas canvas) {
		matrix.reset();
		totalTranslateX=totalTranslateX+movedDistanceX;
		totalTranslateY=totalTranslateY+movedDistanceY;
		matrix.postScale(totalRatio, totalRatio);
		matrix.postTranslate(totalTranslateX, totalTranslateY);
		canvas.drawBitmap(bitmap, matrix, null);
	}
	private void initBitmap(Canvas canvas){
		matrix.reset();
		int viewWidth=bitmap.getWidth();
		int viewHeight=bitmap.getHeight();
		initRatio=(float)width/(float)viewWidth;
		totalRatio=initRatio;
		//float ratioY=(float)height/(float)viewHeight;
		//ratio=Math.min(ratioX,ratioY);
		matrix.postScale(initRatio, initRatio);
		matrix.postTranslate(0,(height-viewHeight*initRatio)/2);
		canvas.drawBitmap(bitmap, matrix, null);
	}
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		if(changed){
			width=right-left;
			height=bottom-top;
			System.out.println(width+"   "+height);
		}
		super.onLayout(changed, left, top, right, bottom);
	}

}
