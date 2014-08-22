package org.vimgadgets.linebreak;

import java.util.ArrayList;

import com.harry.model.TextWord;

public final class LineBreakerTool {
	private static LineBreaker lineBreaker = new LineBreaker("zh");

	/**
	 * 填充段落数据
	 * 
	 * @param line  一个段落
	 * @param tWords 存储一个段落的所有字
	 */
	public static void fillParagraphData(String line, ArrayList<TextWord> tWords) {
		int length = line.length();
		final byte[] breaks = new byte[length];
		lineBreaker.setLineBreaks(line, breaks);

		// index 为此'字'在段落所有字中的偏移量
		int index = 0;
		for (int n = 0; n < length; ++n) {
			if (breaks[n] == LineBreaker.MUSTBREAK) {
				String word = line.substring(index, n + 1);
				TextWord tWord = new TextWord(word, index);
				tWords.add(tWord);
				break;
			} else if (breaks[n] == LineBreaker.ALLOWBREAK) {
				String word = line.substring(index, n + 1);
				TextWord tWord = new TextWord(word, index);
				tWords.add(tWord);
				index = n + 1;
			} else if (breaks[n] == LineBreaker.NOBREAK) {

			}
		}
	}
}
