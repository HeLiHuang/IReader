package com.harry.model;

/**
 * 代表一个'字' 可以是"我", "晕,", "啊!", "Hello"
 */
public final class TextWord {
	/**
	 * '字'中的字符数组
	 */
	public String data;
	/**
	 * 在段落中的偏移量(此'字'对应于段落中所有字的偏移量)
	 */
	public int paragraphOffset;
	/**
	 * 此'字'的宽度
	 */
	private float width;

	public TextWord(String mData, int myParagraphOffset) {
		data = mData;
		paragraphOffset = myParagraphOffset;
	}

	/**
	 * @return the width
	 */
	public float getWidth() {
		return width;
	}

	/**
	 * @param width
	 *            the width to set
	 */
	public void setWidth(float mWidth) {
		width = mWidth;
	}

}
