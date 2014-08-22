package com.harry.model;

import java.util.ArrayList;

public class Paragraph {
	/**
	 * 段落的第一个字在全文中的偏移量
	 */
	private long myTextOffset;
	/**
	 * 段落在全文中的段落号
	 */
	private int paraIndex;
	/**
	 * 段落中包含的所有的字
	 */
	private ArrayList<TextWord> mTextWords;

	public Paragraph() {
		mTextWords = new ArrayList<TextWord>();
	}

	public long getTextOffset() {
		return myTextOffset;
	}
	
	public void setTextOffset(long offset){
		myTextOffset = offset;
	}


	public int getParaIndex() {
		return paraIndex;
	}


	public void setParaIndex(int mParaIndex) {
		paraIndex = mParaIndex;
	}

	/**
	 * @return the mTextWords
	 */
	public ArrayList<TextWord> getmTextWords() {
		return mTextWords;
	}

	/**
	 * @param mTextWords the mTextWords to set
	 */
	public void setmTextWords(ArrayList<TextWord> textWords) {
		mTextWords = textWords;
	}

	
}
