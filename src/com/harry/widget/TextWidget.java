package com.harry.widget;

import com.harry.anim.SlideAnimationProviderEx;
import com.harry.image.BitmapManager;
import com.harry.widget.AbstractGestureDetector.OnGestureListener;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import android.app.Activity;

public class TextWidget extends View implements OnGestureListener{
	private int pageIndex = 1;
	private final BitmapManager myBitmapManager;
	private final SlideAnimationProviderEx animator;

	private AbstractGestureDetector mDetector;

	private int batteryLevel;
	
	// 总页数
	private int pageTotal = 0;
	// 分页进度
	private int pageProgress;
	
	private Context mContext;

	public TextWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		myBitmapManager = BitmapManager.getInstance(context);
		animator = SlideAnimationProviderEx.getInstance(myBitmapManager, context);
		animator.setMyWidget(this);

		mDetector = AbstractGestureDetector.newInstance(context, this);

		mScroller = new Scroller(context, new LinearInterpolator());
		
		mContext  = context;
		
		init();
	}
	
	private void init() {
		SharedPreferences preferences = ((Activity) mContext)
				.getPreferences(Context.MODE_PRIVATE);
		pageIndex = preferences.getInt("PageIndex", 1);
	}
	
	public void destroy(){
		SharedPreferences preferences = ((Activity) mContext)
				.getPreferences(Context.MODE_PRIVATE);
		preferences.edit().putInt("PageIndex", pageIndex).commit();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mDetector.onTouchEvent(event);
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// createWakeLock();
		super.onDraw(canvas);

		if (isDrag || isAutoMove) {
			animator.drawMovePage(canvas, pageIndex);
		} 
		else {
			animator.drawStaticPage(canvas, pageIndex);
		}
		
		invalidate();
	}
	
	

	public int getBatteryLevel() {
		return batteryLevel;
	}

	public void setBatteryLevel(int mBatteryLevel) {
		batteryLevel = mBatteryLevel;
	}

	public void setPageIndex(int mPageIndex) {
		pageIndex = mPageIndex;
	}

	public int getPageTotal() {
		return pageTotal;
	}

	public void setPageTotal(int mPageTotal) {
		pageTotal = mPageTotal;
	}

	public int getPageProgress() {
		return pageProgress;
	}

	public void setPageProgress(int mPageProgress) {
		pageProgress = mPageProgress;
	}
	
	
	private Scroller mScroller = null;
	@Override
	public void computeScroll() {
		if(isAutoMove){
			if(!mScroller.isFinished()){
            	if (mScroller.computeScrollOffset()) {
                	int mPosX = mScroller.getCurrX();
                	animator.setmPosX(mPosX);
                    // 位置发生偏移，重绘
                    invalidate();
                }
            }
            else{
            	isAutoMove = false;
            	int toPageIndex = animator.getToPageIndex();
            	if(toPageIndex != -1){
					setPageIndex(toPageIndex);
					animator.setmPosX(0);
            	}
            	
            	animator.setToPageIndex(-1);

            	invalidate();
            }
		}
		super.computeScroll();
	}

	
	
	
	@Override
	public void onAutoMove() {
		animator.startAfterDragScrolling(mScroller, pageIndex);
	}
	
	@Override
	public void onDrag(int dx) {
		if (animator.getmPosX() + dx > 0 && pageIndex == 1) {
			if (animator.getmPosX() != 0) {
				animator.setmPosX(0);
			}
		} else if (animator.getmPosX() + dx < 0 && pageIndex == pageTotal) {
			if (animator.getmPosX() != 0) {
				animator.setmPosX(0);
			}
		} else {
			animator.appendPosX(dx);
		}

		invalidate();
	}
	
	private boolean isDrag = false;
	private boolean isAutoMove = false;
	@Override
	public void setDragAndAutoMoveFlag(boolean mIsDrag, boolean mIsAutoMove) {
		isDrag = mIsDrag;
		isAutoMove = mIsAutoMove;
	}
	
}