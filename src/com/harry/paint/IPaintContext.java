package com.harry.paint;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;

import com.harry.model.TextLineInfo;
import com.harry.model.TextWord;
import com.harry.util.ScreenUtil;

import android.graphics.*;

public final class IPaintContext extends IPaint {

	private final Canvas myCanvas;

	private final int myWidth;
	private final int myHeight;

	public IPaintContext(Canvas canvas) {
		myCanvas = canvas;
		myWidth = ScreenUtil.screenWidth;
		myHeight = ScreenUtil.screenHeight;
	}

	public void drawBackground() {
		myCanvas.drawRect(0, 0, myWidth, myHeight, myBackgroundPaint);
	}

	public int getWidth() {
		return myWidth;
	}

	public int getHeight() {
		return myHeight;
	}

	public float getStringWidth(String string) {
		return myTextPaint.measureText(string) + 0.5f;
	}

	public float getStringHeightInternal() {
		return myTextPaint.getTextSize() + 0.5f;
	}

	public float getDescentInternal() {
		return myTextPaint.descent() + 0.5f;
	}

	public void drawPage(ArrayList<TextLineInfo> lineInfos) {
		for (int i = 0; i < lineInfos.size(); ++i) {
			TextLineInfo lineInfo = lineInfos.get(i);
			drawLine(lineInfo);
		}
	}

	private void drawLine(TextLineInfo lineInfo) {
		ArrayList<TextWord> tWords = lineInfo.gettWords();
		float x = ScreenUtil.mLeftMargin;

		float maxWidth = ScreenUtil.screenWidth - ScreenUtil.mLeftMargin
				- ScreenUtil.mRightMargin;
		if (lineInfo.isFistLine()) {
			x += ScreenUtil.firstLineIndent;
			maxWidth -= ScreenUtil.firstLineIndent;
		}

		int wordsCount = tWords.size();
		double spanSpace = 0.0;
		double span = 0.0;
		if (wordsCount > 1 && !lineInfo.isEndLine()) {
			MathContext mc = new MathContext(10, RoundingMode.HALF_DOWN);
			BigDecimal a = new BigDecimal(maxWidth - lineInfo.getWidth());
			BigDecimal b = new BigDecimal(wordsCount - 1);
			BigDecimal c = a.divide(b, mc);
			spanSpace = c.doubleValue();
		}

		for (int i = 0; i < tWords.size(); ++i) {
			TextWord tWord = tWords.get(i);
			myCanvas.drawText(tWord.data, x + (int) Math.round(span),
					lineInfo.getY(), myTextPaint);
			x += tWord.getWidth();
			span += spanSpace;
		}
	}

	public void drawTitle(String string) {
		float x = ScreenUtil.mLeftMargin;
		float y = ScreenUtil.mTopMargin;
		myCanvas.drawText(string, x, y, mTitleOrFooterPaint);
	}
}