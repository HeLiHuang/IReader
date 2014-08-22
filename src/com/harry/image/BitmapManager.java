package com.harry.image;

import java.io.IOException;
import java.util.ArrayList;

import org.vimgadgets.linebreak.LineBreakerTool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import com.harry.db.DbManager;
import com.harry.file.FileManager;
import com.harry.file.FileUtil;
import com.harry.model.Page;
import com.harry.model.Paragraph;
import com.harry.model.TextLineInfo;
import com.harry.model.TextWord;
import com.harry.paint.IPaintContext;
import com.harry.util.ScreenUtil;

public class BitmapManager extends KVCache<Integer, Bitmap> {
	private static BitmapManager instance;
	private FileManager fManager;
	private DbManager dManager;
	private boolean exit = false;
	private BMPFactoryThread bmpRunnable;

	public synchronized static BitmapManager getInstance(Context context) {
		if (instance == null)
			instance = new BitmapManager(context);
		return instance;
	}

	private BitmapManager(Context mContext) {
		super(mContext);
		fManager = FileManager.getInstance();
		dManager = DbManager.getInstance(mContext);
		bmpRunnable = new BMPFactoryThread();
	}

	public void destroy() {
		exit = true;
		clearAll();
	}
	
	// 颜色和字体大小变化时使用
	public void clear() {
		clearAll();
	}

	public void start() {
		exit = false;
		new Thread(bmpRunnable).start();
	}

	class BMPFactoryThread implements Runnable {
		private int pageIndex;

		@Override
		public void run() {
			while (!exit) {
				if (pageIndex > 0) {
					setBitmap(pageIndex);
				}
			}
		}

		public void setPageIndex(int mPageIndex) {
			pageIndex = mPageIndex;
		}
	}

	private void setBitmap(int pageIndex) {
		if (!containsInMem(pageIndex)) {
			createBitmap(pageIndex);
		}

		if (pageIndex == 1) {
			if (!containsInMem(pageIndex + 1)) {
				createBitmap(pageIndex + 1);
			}
			if (!containsInMem(pageIndex + 2)) {
				createBitmap(pageIndex + 2);
			}
		} else if (pageIndex == 2) {
			if (!containsInMem(pageIndex + 1)) {
				createBitmap(pageIndex + 1);
			}
			if (!containsInMem(pageIndex + 2)) {
				createBitmap(pageIndex + 2);
			}
			if (!containsInMem(pageIndex - 1)) {
				createBitmap(pageIndex - 1);
			}
		} else if (pageIndex > 2) {
			if (!containsInMem(pageIndex + 1)) {
				createBitmap(pageIndex + 1);
			}
			if (!containsInMem(pageIndex + 2)) {
				createBitmap(pageIndex + 2);
			}
			if (!containsInMem(pageIndex - 1)) {
				createBitmap(pageIndex - 1);
			}
			if (!containsInMem(pageIndex - 2)) {
				createBitmap(pageIndex - 2);
			}
		}
	}

	public Bitmap getBitmap(final int pageIndex) {
		bmpRunnable.setPageIndex(pageIndex);
		try {
			return get(pageIndex);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private void createBitmap(final int pageIndex) {
		if (!dManager.isExistBook(FileUtil.fileName)) {
			return;
		}
		Bitmap bitmap = null;
		try {
			bitmap = Bitmap.createBitmap(ScreenUtil.screenWidth,
					ScreenUtil.screenHeight, Bitmap.Config.RGB_565);
		} catch (OutOfMemoryError e) {
			System.gc();
			System.gc();
			bitmap = Bitmap.createBitmap(ScreenUtil.screenWidth,
					ScreenUtil.screenHeight, Bitmap.Config.RGB_565);
		}
		if (drawOnBitmap(bitmap, pageIndex)) {
			try {
				put(pageIndex, bitmap);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			bitmap.recycle();
			System.gc();
		}
	}

	private boolean drawOnBitmap(Bitmap bitmap, final int pageIndex) {
		final IPaintContext context = new IPaintContext(new Canvas(bitmap));
		context.drawBackground();

		/**
		 * 设置首行缩进
		 */
		ScreenUtil.firstLineIndent = context.getStringWidth("中国");

		float[] y = new float[2];
		// 当前高度
		y[0] = ScreenUtil.mTopMargin;
		// 最大高度
		y[1] = ScreenUtil.screenHeight - ScreenUtil.mBottomMargin;

		Page page = dManager.getPage(pageIndex);
		
		ArrayList<Paragraph> paragraphs = null;
		ArrayList<TextLineInfo> lineInfos = null;

		int paraIndex = 0;
		// 第一行第一个字在段落中的偏移量
		int wordOffsetOfPara = 0;
		
		if (page != null) {
			paraIndex = page.getParaIndex();
			wordOffsetOfPara = page.getWordNumOfPara();
			paragraphs = dManager.getParagraphs(paraIndex);
			lineInfos = new ArrayList<TextLineInfo>();
		}
		// 如果没有查到那一页
		else {
			return false;
		}

		for (int i = 0; i < paragraphs.size(); ++i) {
			Paragraph p = paragraphs.get(i);
			String line = fManager.readParagraphByOffset(p.getTextOffset());
			ArrayList<TextWord> tWords = new ArrayList<TextWord>();

			LineBreakerTool.fillParagraphData(line, tWords);

			if (i == 0) {
				fillLinesData(tWords, lineInfos, context, y, wordOffsetOfPara);
			} else {
				fillLinesData(tWords, lineInfos, context, y, 0);
			}

			if (y[0] > y[1]) {
				break;
			}
		}

		context.drawPage(lineInfos);

		if(page != null)
			drawTitle(context, page.getChapter());
		
		return true;
	}

	private void drawTitle(IPaintContext context, String chapter) {
		if(chapter != null){
			context.drawTitle(chapter);
		}
	}

	/**
	 * 填充行的数据
	 * 
	 * @param tWords
	 * @return
	 */
	private void fillLinesData(ArrayList<TextWord> tWords,
			ArrayList<TextLineInfo> lineInfos, IPaintContext context,
			float[] y, int wordOffsetOfPara) {
		int line = 0;
		float width = 0;

		/**
		 * 设置行高 因为DrawString的坐标是从左下角
		 */
		float heightSpan = context.getDescentInternal()
				+ context.getStringHeightInternal();

		y[0] += heightSpan;
		if (y[0] > y[1]) {
			return;
		}

		/**
		 * 设置最大宽度
		 */
		float maxWidth1 = ScreenUtil.screenWidth - ScreenUtil.mRightMargin
				- ScreenUtil.mLeftMargin;
		float maxWidth2 = maxWidth1 - ScreenUtil.firstLineIndent;
		float maxWidth = 0;

		ArrayList<TextWord> mWords = new ArrayList<TextWord>();

		for (int i = wordOffsetOfPara; i < tWords.size(); ++i) {
			TextWord tWord = tWords.get(i);
			// 如果是段落的首行
			if (line == 0 && wordOffsetOfPara == 0) {
				maxWidth = maxWidth2;
			} else {
				maxWidth = maxWidth1;
			}
			float wordWidth = context.getStringWidth(tWord.data);
			tWord.setWidth(wordWidth);
			width += wordWidth;
			if (width > maxWidth) {
				TextLineInfo lineInfo = new TextLineInfo();
				lineInfo.settWords(mWords);
				lineInfo.setWidth(width - wordWidth);
				// 设置y坐标
				lineInfo.setY(y[0]);
				if (line == 0 && wordOffsetOfPara == 0) {
					lineInfo.setFistLine(true);
				}
				lineInfos.add(lineInfo);
				width = context.getStringWidth(tWord.data);
				// 加上行间距
				y[0] += heightSpan + ScreenUtil.lineSpace;
				// 高度大于最大高度
				if (y[0] > y[1]) {
					return;
				}
				++line;

				mWords = new ArrayList<TextWord>();
			}

			mWords.add(tWord);
		}

		TextLineInfo lineInfo = new TextLineInfo();
		lineInfo.settWords(mWords);
		lineInfo.setWidth(width);
		lineInfo.setY(y[0]);
		lineInfo.setEndLine(true);
		if (line == 0 && wordOffsetOfPara == 0) {
			lineInfo.setFistLine(true);
		}
		lineInfos.add(lineInfo);
		// 加上段后距
		y[0] += ScreenUtil.paragraphSpaceAfter;
	}
}