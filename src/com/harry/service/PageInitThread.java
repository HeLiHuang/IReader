package com.harry.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import org.vimgadgets.linebreak.LineBreakerTool;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.harry.db.DbManager;
import com.harry.file.FileUtil;
import com.harry.model.Page;
import com.harry.model.Paragraph;
import com.harry.model.TextWord;
import com.harry.util.DbUtil;
import com.harry.util.EncodeUtil;
import com.harry.util.FontUtil;
import com.harry.util.MsgUtil;
import com.harry.util.ScreenUtil;

/**
 * 分页线程
 */
public class PageInitThread implements Runnable {
	private Handler handler;
	private Context context;
	private DbManager dManager;

	private Paint myTextPaint;

	// 页的索引
	private int pageIndex = 1;
	// 行高
	private float heightSpan;
	// 段落非首行的最大宽度
	private float maxWidth1;
	// 段落第一行的最大宽度
	private float maxWidth2;

	// 段落的偏移量
	// 保存页信息时使用
	// 每一页的第一段的索引
	private int firstParaForPage = 0;
	// 从段落的第几个字开始
	private int startWordIndex;

	// 首行缩进
	private float firstLineIndent;
	private int fontSize;

	private String chapter = null;

	public PageInitThread(Handler mHandler, Context mContext, int mFontSize) {
		handler = mHandler;
		context = mContext;
		dManager = DbManager.getInstance(context);

		myTextPaint = new Paint();
		myTextPaint.setLinearText(false);
		myTextPaint.setAntiAlias(true);
		myTextPaint.setDither(true);
		myTextPaint.setSubpixelText(true);
		myTextPaint.setTypeface(Typeface.DEFAULT);
		myTextPaint.setTextSize(mFontSize);

		fontSize = mFontSize;

		/**
		 * 设置首行缩进
		 */
		firstLineIndent = getStringWidth("中国");

		/**
		 * 设置行高
		 */
		heightSpan = getDescentInternal() + getStringHeightInternal();

		/**
		 * 设置最大宽度
		 */
		maxWidth1 = ScreenUtil.screenWidth - ScreenUtil.mRightMargin
				- ScreenUtil.mLeftMargin;
		maxWidth2 = maxWidth1 - firstLineIndent;

		chapter = dManager.getBookTitle(FileUtil.fileName);

		// 设置分页状态
		dManager.setPageState(DbUtil.PAGE_STATE_PAGEING, fontSize);
	}

	private boolean exit = false;

	public void exitAndStartAnother() {
		exit = true;
	}

	public boolean isExit() {
		return exit;
	}

	private void updateChapter(String line) {
		String regex = "[\u7B2C][\\w\\W]{1,4}[\u7AE0][\\w\\W]*";
		if (line.matches(regex)) {
			chapter = line;
		}
	}

	@Override
	public void run() {

		int olPprogress = 0;

		float[] y = new float[2];
		y[0] = ScreenUtil.mTopMargin;
		y[1] = ScreenUtil.screenHeight - ScreenUtil.mBottomMargin;

		ArrayList<Paragraph> paragraphs = new ArrayList<Paragraph>();

		// ---------------------------------------------------------------------------
		File file = new File(FileUtil.fileName);
		long fileSize = file.length();
		try {
			RandomAccessFile raf = new RandomAccessFile(file, "r");
			String line = null;
			long offset = raf.getFilePointer();
			int paraIndex = 0;
			while ((line = raf.readLine()) != null) {
				line = new String(line.getBytes("8859_1"), EncodeUtil.ENCODE);
				line = line.replace((char) 12288, ' ');
				line = line.trim();

				if (!TextUtils.isEmpty(line)) {
					System.out.println(line);
					Paragraph p = new Paragraph();

					p.setTextOffset(offset);
					p.setParaIndex(paraIndex);
					paragraphs.add(p);
					dManager.setParagraph(p);
					offset = raf.getFilePointer();

					// ---------------------处理分页
					updateChapter(line);

					// 获取段落中的所有的字
					ArrayList<TextWord> tWords = new ArrayList<TextWord>();
					LineBreakerTool.fillParagraphData(line, tWords);

					// 下一段的段号
					int paraNextNum = paraIndex + 1;
					// 其实这里并没有填充，只是计算
					// 调用一次计算一个段落
					//
					fillLinesData(tWords, y, paraIndex, paraNextNum);

					if (fontSize == FontUtil.fontInfo.getSize()) {
						int newProgress = (int) (offset * 100 / fileSize);
						if (newProgress != olPprogress) {
							olPprogress = newProgress;
							Message message = handler.obtainMessage();
							message.what = MsgUtil.INIT_PAGE_PROGRESS_MSG;
							message.arg1 = newProgress;
							handler.sendMessage(message);
						}
					}

					++paraIndex;
				}

				offset = raf.getFilePointer();

				if (exit) {
					break;
				}
			}

			raf.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// ------------------------------------------------------------------------------

		// 保存最后一页
		setPageInfo(pageIndex, firstParaForPage, startWordIndex);

		// 保存总页数
		dManager.setPageTotal(pageIndex, fontSize);

		if (fontSize == FontUtil.fontInfo.getSize()) {
			Message message = handler.obtainMessage();
			message.what = MsgUtil.INIT_PAGE_SUCCESS_MSG;
			message.arg1 = pageIndex;
			handler.sendMessage(message);
		}

		if (exit) {
			Message message = handler.obtainMessage();
			message.what = MsgUtil.INIT_PAGE_RESTART_MSG;
			handler.sendMessage(message);
		}

		exit = true;
	}

	/**
	 * 填充行的数据
	 * 
	 * @param tWords
	 *            一段文字里面的数据
	 * @return
	 */
	private void fillLinesData(ArrayList<TextWord> tWords, float[] y,
			int pCurrParaNum, int pNextParaNum) {
		// 是本段的行
		int line = 0;
		float width = 0;

		// 因为绘制text的时候的坐标是左下角
		// 所以这里得加上行高
		y[0] += heightSpan;
		// 如果是此段的第一行处在这一页的最后一行，有可能会超出
		if (y[0] > y[1]) {
			y[0] = heightSpan + ScreenUtil.mTopMargin;
			// 保存页信息------------------------------------------------------------
			setPageInfo(pageIndex, firstParaForPage, startWordIndex);
			// 下面这两个值只有在换页的时候赋
			firstParaForPage = pCurrParaNum;
			startWordIndex = 0;
			// 保存页信息------------------------------------------------------------
			++pageIndex;
		}

		// 一行的最大宽度 区分段首行和段非首行
		float maxWidth = 0;

		for (int i = 0; i < tWords.size(); ++i) {
			TextWord tWord = tWords.get(i);

			// 如果是段落的首行
			if (line == 0) {
				maxWidth = maxWidth2;
			} else {
				maxWidth = maxWidth1;
			}
			float wordWidth = getStringWidth(tWord.data);
			tWord.setWidth(wordWidth);
			width += wordWidth;
			if (width > maxWidth) {
				// 如果大于最大值得换行了
				// 所以宽度重新计算
				width = getStringWidth(tWord.data);
				// 加上行间距
				y[0] += heightSpan + ScreenUtil.lineSpace;
				// 高度大于最大高度
				if (y[0] > y[1]) {
					y[0] = heightSpan + ScreenUtil.mTopMargin;

					// 保存页信息------------------------------------------------------------
					setPageInfo(pageIndex, firstParaForPage, startWordIndex);
					// 下面这两个值只有在换页的时候赋
					firstParaForPage = pCurrParaNum;
					startWordIndex = i;
					// 保存页信息------------------------------------------------------------

					++pageIndex;
				}
				++line;
			}
		}

		// 加上段后距
		y[0] += ScreenUtil.paragraphSpaceAfter;
		if (y[0] > y[1]) {
			// y[0] = heightSpan + ScreenUtil.topMargin;
			// 这里一定要去掉行高heightSpan，因为在这里函数马上就要返回了，在下次进入此函数的开头会加上行高heightSpan
			// 前面在换页时之所以不去掉，是因为函数不会返回，所以换行时得加上行高heightSpan
			y[0] = ScreenUtil.mTopMargin;

			// 保存页信息------------------------------------------------------------
			setPageInfo(pageIndex, firstParaForPage, startWordIndex);
			// 下面这两个值只有在换页的时候赋
			firstParaForPage = pNextParaNum;
			startWordIndex = 0;
			// 保存页信息------------------------------------------------------------

			// 为-1时就没有下一段了，所以就没有下一页了
			if (pNextParaNum != -1) {
				++pageIndex;
			}
		}
	}

	/**
	 * @param pageNum
	 *            页号从1开始
	 * @param paraOffset
	 *            页的第一段在文件中的偏移量
	 * @param lineIndexOfPara
	 *            在此段中第几行开始
	 */
	private void setPageInfo(int mPageIndex, int paraIndex, int wordIndexOfPara) {
		Page page = new Page();
		page.setPageIndex(mPageIndex);
		page.setFontSize(fontSize);
		page.setParaIndex(paraIndex);
		page.setWordNumOfPara(wordIndexOfPara);
		page.setChapter(chapter);

		dManager.setPage(page);
	}

	float singleWordLength = -1;

	public float getStringWidth(String string) {
		if (string.length() == 1) {
			if (singleWordLength == -1) {
				singleWordLength = myTextPaint.measureText(string) + 0.5f;
			}

			return singleWordLength;
		}

		return myTextPaint.measureText(string) + 0.5f;
	}

	public float getStringHeightInternal() {
		return myTextPaint.getTextSize() + 0.5f;
	}

	public float getDescentInternal() {
		return myTextPaint.descent() + 0.5f;
	}
}