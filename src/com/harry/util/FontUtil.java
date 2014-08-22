package com.harry.util;

import android.content.Context;

import com.harry.ireader.R;
import com.harry.style.FontInfo;

public class FontUtil {
	public static FontInfo fontInfo;

	/**
	 * 页眉页脚字体大小
	 */
	public static float headerFooterFontSize;

	public static void init(Context context, int id) {
		headerFooterFontSize = context.getResources().getDimension(
				R.dimen.header_footer_font_size);
		float textSize = context.getResources().getDimension(
				R.dimen.text_font_size);
		
		fontInfo = new FontInfo();
		fontInfo.setId(0);
		fontInfo.setSize((int) textSize);
	}
}