package com.harry.model;

public final class Page {
	/**
	 * 当前页的页号
	 */
	private int pageIndex;
	/**
	 * 页的第一个字所在段的段号文件中的偏移量
	 */
	private int paraIndex;
	/**
	 * 对应的字体大小
	 */
	private int fontSize;
	/**
	 * 此页从段落的第几个字开始
	 */
	private int wordNumOfPara;
	
	// 这一页所属的章节
	private String chapter;

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int mPageIndex) {
		pageIndex = mPageIndex;
	}

	

	public int getParaIndex() {
		return paraIndex;
	}

	public void setParaIndex(int mParaIndex) {
		paraIndex = mParaIndex;
	}

	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(int mFontSize) {
		fontSize = mFontSize;
	}

	public int getWordNumOfPara() {
		return wordNumOfPara;
	}

	public void setWordNumOfPara(int mLineNumOfPara) {
		wordNumOfPara = mLineNumOfPara;
	}

	/**
	 * @return the chapter
	 */
	public String getChapter() {
		return chapter;
	}

	/**
	 * @param chapter the chapter to set
	 */
	public void setChapter(String chapter) {
		this.chapter = chapter;
	}

	
}
