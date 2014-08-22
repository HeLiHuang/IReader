package com.harry.paint;

import com.harry.util.ColorUtil;
import com.harry.util.FontUtil;

import android.graphics.Paint;
import android.graphics.Typeface;

public class IPaint {
	public final static Paint myBackgroundPaint = new Paint();
	public final static Paint myTextPaint = new Paint();
	public final static Paint mTitleOrFooterPaint = new Paint();
	
	public static void init(){
		myBackgroundPaint.setColor(ColorUtil.colorInfo.getBackgroundColor());

		myTextPaint.setLinearText(false);
		myTextPaint.setAntiAlias(true);
		myTextPaint.setDither(true);
		myTextPaint.setSubpixelText(true);
		myTextPaint.setTypeface(Typeface.DEFAULT);
		myTextPaint.setTextSize(FontUtil.fontInfo.getSize());
		myTextPaint.setColor(ColorUtil.colorInfo.getTextColor());

		mTitleOrFooterPaint.setLinearText(false);
		mTitleOrFooterPaint.setAntiAlias(true);
		mTitleOrFooterPaint.setColor(ColorUtil.colorInfo.getTitleColor());
		mTitleOrFooterPaint.setTextSize(FontUtil.headerFooterFontSize);
	}
	
	public static void resetColor(){
		myBackgroundPaint.setColor(ColorUtil.colorInfo.getBackgroundColor());
		myTextPaint.setColor(ColorUtil.colorInfo.getTextColor());
		mTitleOrFooterPaint.setColor(ColorUtil.colorInfo.getTitleColor());
	}
	
	public static void resetFont(){
		myTextPaint.setTextSize(FontUtil.fontInfo.getSize());
	}
}
