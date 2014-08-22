package com.harry.util;

import com.harry.ireader.R;

import android.content.Context;

public final class ScreenUtil {

	public static int screenWidth;
	public static int screenHeight;

	/**
	 * 首行缩进
	 */
	public static float firstLineIndent;
	/**
	 * 页面边距
	 */
	public static float mLeftMargin;
	public static float mRightMargin;
	public static float mTopMargin;
	public static float mBottomMargin;

	/**
	 * 行间距
	 */
	public static float lineSpace;

	/**
	 * 段后距(段与段之间的距离)
	 */
	public static float paragraphSpaceAfter;

	/**
	 * 阴影宽度
	 */
	public static float shadowWidth;
	
	/**
	 * 电池宽度
	 */
	public static float batteryWidth;

	public static void init(Context context) {
		mLeftMargin = context.getResources().getDimension(R.dimen.left_margin);
		mRightMargin = context.getResources()
				.getDimension(R.dimen.right_margin);
		mTopMargin = context.getResources().getDimension(R.dimen.top_margin);
		mBottomMargin = context.getResources().getDimension(
				R.dimen.bottom_margin);

		lineSpace = context.getResources().getDimension(R.dimen.line_space);

		paragraphSpaceAfter = context.getResources().getDimension(
				R.dimen.para_after_space);

		shadowWidth = context.getResources().getDimension(R.dimen.shadow_width);
		
		batteryWidth = context.getResources().getDimension(R.dimen.battery_width);
	}
}