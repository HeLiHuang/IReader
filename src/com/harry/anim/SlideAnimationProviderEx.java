package com.harry.anim;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.widget.Scroller;

import com.harry.image.BitmapManager;
import com.harry.paint.IPaint;
import com.harry.util.ScreenUtil;
import com.harry.widget.TextWidget;

public class SlideAnimationProviderEx extends IPaint {
	private int mPosX;

	private int myWidth;
	private int myHeight;

	private SimpleDateFormat timeFormat;

	private int colorsLeft[] = new int[] { 0x00000000, 0x33000000, 0x70000000 };
	private int colorsRight[] = new int[] { 0x77000000, 0x33000000, 0x00000000 };

	private BitmapManager myBitmapManager;

	private TextWidget myWidget;

	private static SlideAnimationProviderEx instance;

	public synchronized static SlideAnimationProviderEx getInstance(
			BitmapManager bitmapManager, Context context) {
		if (instance == null)
			instance = new SlideAnimationProviderEx(bitmapManager, context);
		return instance;
	}

	private SlideAnimationProviderEx(BitmapManager bitmapManager,
			Context context) {
		myBitmapManager = bitmapManager;
		myWidth = ScreenUtil.screenWidth;
		myHeight = ScreenUtil.screenHeight;

		timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
	}

	public void drawStaticPage(Canvas canvas, int currPageIndex) {
		drawBitmap(canvas, currPageIndex, 0, 0);
		drawFooter(canvas, currPageIndex, 0);
	}

	public void drawMovePage(Canvas canvas, int currPageIndex) {
		if (mPosX == 0) {
			drawStaticPage(canvas, currPageIndex);
			drawFooter(canvas, currPageIndex, 0);
		} else {
			int pageIndexTo = 0;
			if (mPosX > 0) {
				pageIndexTo = currPageIndex - 1;
				if (pageIndexTo < 1) {
					pageIndexTo = 1;
				}
			} else if (mPosX < 0) {
				pageIndexTo = currPageIndex + 1;
			}

			boolean flag1 = drawBitmap(canvas, pageIndexTo, 0, 0);
			boolean flag2 = drawBitmap(canvas, currPageIndex, mPosX, 0);

			if (myWidth == 0) {
				myWidth = ScreenUtil.screenWidth;
				myHeight = ScreenUtil.screenHeight;
			}

			if (flag1 && flag2) {
				drawFooter(canvas, pageIndexTo, 0);
				drawFooter(canvas, currPageIndex, mPosX);

				if (mPosX > 0 && mPosX < myWidth) {
					drawLeftShadow(canvas, mPosX);
				} else if (mPosX < 0 && mPosX > -myWidth) {
					drawRightShadow(canvas, mPosX);
				}
			}
		}
	}

	private boolean drawBitmap(Canvas canvas, int pageIndex, float left,
			float top) {
		Bitmap bitmap = getBitmap(pageIndex);
		if (bitmap != null) {
			canvas.drawBitmap(bitmap, left, top, myBackgroundPaint);
			return true;
		} else {
			canvas.drawRect(0, 0, ScreenUtil.screenWidth,
					ScreenUtil.screenHeight, myBackgroundPaint);
			return false;
		}
	}

	protected Bitmap getBitmap(int pageIndex) {
		return myBitmapManager.getBitmap(pageIndex);
	}

	private Rect mTextbounds;

	/**
	 * 画页脚
	 */
	private void drawFooter(Canvas canvas, int pageIndex, float dx) {
		canvas.drawRect(dx, ScreenUtil.screenHeight - ScreenUtil.mBottomMargin,
				dx + ScreenUtil.screenWidth, ScreenUtil.screenHeight,
				myBackgroundPaint);

		if (mTextbounds == null) {
			mTextbounds = new Rect();
			mTitleOrFooterPaint.getTextBounds("第三页", 0, 3, mTextbounds);
		}

		float textHeight = mTextbounds.height();
		float baseLine = (ScreenUtil.mBottomMargin - textHeight) / 2
				+ ScreenUtil.screenHeight - ScreenUtil.mBottomMargin
				+ textHeight - mTextbounds.bottom;

		// 绘制电池头
		float x = ScreenUtil.mLeftMargin + dx;
		float y = (ScreenUtil.mBottomMargin - textHeight + 10) / 2
				+ ScreenUtil.screenHeight - ScreenUtil.mBottomMargin;
		canvas.drawRect(x, y, x + 5, y + textHeight - 10, mTitleOrFooterPaint);

		// 绘制电池体
		x = ScreenUtil.mLeftMargin + dx + 5;
		y = (ScreenUtil.mBottomMargin - textHeight) / 2
				+ ScreenUtil.screenHeight - ScreenUtil.mBottomMargin;
		canvas.drawRect(x, y, x + ScreenUtil.batteryWidth, y + textHeight,
				mTitleOrFooterPaint);

		x = ScreenUtil.mLeftMargin + dx + 5 + 2;
		y = (ScreenUtil.mBottomMargin - textHeight) / 2
				+ ScreenUtil.screenHeight - ScreenUtil.mBottomMargin + 2;
		canvas.drawRect(x, y, x + ScreenUtil.batteryWidth - 4, y + textHeight
				- 4, myBackgroundPaint);
		// 绘制剩余电量
		int batteryLevel = myWidget.getBatteryLevel();
		int batteryFullWidth = (int) (ScreenUtil.batteryWidth - 4 - 4);

		int batteryWidth = batteryLevel * batteryFullWidth / 100;
		// 100电量对应的宽度为

		x = ScreenUtil.mLeftMargin + dx + 5 + 2 + 2;
		y = (ScreenUtil.mBottomMargin - textHeight) / 2
				+ ScreenUtil.screenHeight - ScreenUtil.mBottomMargin + 2 + 2;
		canvas.drawRect(x + batteryFullWidth - batteryWidth, y, x
				+ batteryFullWidth, y + textHeight - 4 - 4, mTitleOrFooterPaint);

		// 绘制时间
		x = ScreenUtil.mLeftMargin + dx + 5 + ScreenUtil.batteryWidth + 20;
		String timeInfo = timeFormat.format(new Date());
		canvas.drawText(timeInfo, x, baseLine, mTitleOrFooterPaint);

		// 绘制页码
		int pageTotal = myWidget.getPageTotal();
		String pageInfo = null;
		if (pageTotal > 0) {
			pageInfo = "第" + pageIndex + "页/共" + pageTotal + "页";
		} else {
			pageInfo = "第" + pageIndex + "页";
		}

		x = dx + ScreenUtil.screenWidth - ScreenUtil.mRightMargin
				- mTitleOrFooterPaint.measureText(pageInfo);
		canvas.drawText(pageInfo, x, baseLine, mTitleOrFooterPaint);
	}

	private void drawLeftShadow(Canvas canvas, float dx) {
		LinearGradient lg = new LinearGradient(dx - ScreenUtil.shadowWidth, 0,
				dx, 0, colorsLeft, null, Shader.TileMode.CLAMP);
		Paint p = new Paint();
		p.setShader(lg);

		canvas.drawRect(dx - ScreenUtil.shadowWidth, 0, dx, myHeight + 1, p);
	}

	private void drawRightShadow(Canvas canvas, float dx) {
		LinearGradient lg = new LinearGradient(dx + myWidth, 0, dx + myWidth
				+ ScreenUtil.shadowWidth, 0, colorsRight, null,
				Shader.TileMode.CLAMP);
		Paint p = new Paint();
		p.setShader(lg);

		canvas.drawRect(dx + myWidth, 0, dx + myWidth + ScreenUtil.shadowWidth,
				myHeight + 1, p);
	}

	public void appendPosX(int posX) {
		mPosX += posX;
	}

	public float getmPosX() {
		return mPosX;
	}

	public void setmPosX(int mPosX) {
		this.mPosX = mPosX;
	}

	public void setMyWidget(TextWidget mWidget) {
		myWidget = mWidget;
	}

	public void startAfterDragScrolling(Scroller mScroller, final int pageIndex) {
		int dx = 0;

		if (mPosX > 0) {
			// 回弹
			if (mPosX < myWidth / 4) {
				dx = -mPosX;
			}
			// 往对面滑(显示上一页)
			else {
				dx = myWidth - mPosX;
				toPageIndex = pageIndex - 1;
			}
		} else if (mPosX < 0) {
			// 回弹
			if (-mPosX < myWidth / 4) {
				dx = -mPosX;
			}
			// 往对面滑(到下一页)
			else {
				dx = -(myWidth + mPosX);
				toPageIndex = pageIndex + 1;
			}
		}

		scroll(mScroller, dx);
	}

	private void scroll(Scroller mScroller, int dx) {
		mScroller.startScroll(mPosX, 0, dx, 0);
	}

	private int toPageIndex = -1;

	public int getToPageIndex() {
		return toPageIndex;
	}

	public void setToPageIndex(int toPageIndex) {
		this.toPageIndex = toPageIndex;
	}
}