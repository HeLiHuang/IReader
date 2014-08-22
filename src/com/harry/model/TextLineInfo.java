package com.harry.model;

import java.util.ArrayList;

public final class TextLineInfo {
	private ArrayList<TextWord> tWords;
	private float width;
	private float y;
	/**
	 * 是否是段落的首行
	 */
	private boolean isFistLine;
	/**
	 * 是否是段落的末行
	 */
	private boolean isEndLine;

	/**
	 * @return the tWords
	 */
	public ArrayList<TextWord> gettWords() {
		return tWords;
	}

	/**
	 * @param mWords the tWords to set
	 */
	public void settWords(ArrayList<TextWord> mWords) {
		tWords = mWords;
	}

	/**
	 * @return the width
	 */
	public float getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(float mWidth) {
		width = mWidth;
	}

	/**
	 * @return the y
	 */
	public float getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(float mY) {
		y = mY;
	}

	/**
	 * @return the isFistLine
	 */
	public boolean isFistLine() {
		return isFistLine;
	}

	/**
	 * @param isFistLine the isFistLine to set
	 */
	public void setFistLine(boolean FistLine) {
		isFistLine = FistLine;
	}

	/**
	 * @return the isEndLine
	 */
	public boolean isEndLine() {
		return isEndLine;
	}

	/**
	 * @param isEndLine the isEndLine to set
	 */
	public void setEndLine(boolean EndLine) {
		isEndLine = EndLine;
	}
	
	
}
