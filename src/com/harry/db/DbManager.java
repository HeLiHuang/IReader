package com.harry.db;

import java.util.ArrayList;

import com.harry.config.Config;
import com.harry.model.Book;
import com.harry.model.Page;
import com.harry.model.Paragraph;
import com.harry.util.DbUtil;
import com.harry.util.FontUtil;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public class DbManager {
	private BookSqliteHelper helper;
	private SQLiteDatabase db;

	private static DbManager instance;

	public synchronized static DbManager getInstance(Context context) {
		if (instance == null)
			instance = new DbManager(context);
		return instance;
	}

	private DbManager(Context context) {
		helper = new BookSqliteHelper(context, Config.DB_NAME, null,
				getLocalVersions(context));
		db = helper.getWritableDatabase();
	}

	private int getLocalVersions(Context context) {
		PackageManager manager = context.getPackageManager();
		int version = 1;
		try {
			PackageInfo info = manager.getPackageInfo(context.getPackageName(),
					0);
			version = info.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return version;
	}

	public boolean isExistBook(String fileName) {
		if (TextUtils.isEmpty(fileName)) {
			return false;
		}

		Cursor cursor = db.query("book", new String[] { "path" }, "path=?",
				new String[] { fileName }, null, null, null);
		boolean isExist;
		if (cursor.moveToNext()) {
			isExist = true;
		} else {
			isExist = false;
		}
		cursor.close();

		return isExist;
	}

	public String getBookTitle(String fileName) {

		String title = null;
		Cursor cursor = db.query("book", new String[] { "title" }, "path=?",
				new String[] { fileName }, null, null, null);
		if (cursor.moveToNext()) {
			title = cursor.getString(cursor.getColumnIndex("title"));
		}

		cursor.close();

		return title;
	}

	public void clear() {
		db.delete("book", null, null);
		db.delete("paragraphs", null, null);
		db.delete("pages", null, null);
		db.delete("pagesindex", null, null);
	}

	public void setBook(Book book) {
		ContentValues cv = new ContentValues();
		cv.put("path", book.getPath());
		cv.put("title", book.getTitle());
		db.insert("book", null, cv);
	}

	public void setParagraph(Paragraph p) {
		ContentValues cv = new ContentValues();
		cv.put("offset", p.getTextOffset());
		cv.put("paraIndex", p.getParaIndex());

		db.insert("paragraphs", null, cv);
	}

	public Paragraph getParagraph(int paraIndex) {
		Paragraph p = null;
		Cursor cursor = db.query("paragraphs", new String[] { "offset" },
				"paraIndex=?", new String[] { String.valueOf(paraIndex) },
				null, null, null);
		if (cursor.moveToFirst()) {
			p = new Paragraph();
			p.setParaIndex(paraIndex);
			p.setTextOffset(cursor.getLong(cursor.getColumnIndex("offset")));
		}

		cursor.close();

		return p;
	}

	/**
	 * 生成一页时用 一页最多有15段
	 * 
	 * @param paraIndex
	 * @return
	 */
	public ArrayList<Paragraph> getParagraphs(int paraIndex) {
		ArrayList<Paragraph> paragraphs = new ArrayList<Paragraph>();

		Cursor cursor = db.query(
				"paragraphs",
				null,
				"paraIndex>=? and paraIndex<?",
				new String[] { String.valueOf(paraIndex),
						String.valueOf(paraIndex + 15) }, null, null,
				"paraIndex asc");
		while (cursor.moveToNext()) {
			Paragraph p = new Paragraph();
			p.setParaIndex(cursor.getInt(cursor.getColumnIndex("paraIndex")));
			p.setTextOffset(cursor.getLong(cursor.getColumnIndex("offset")));
			paragraphs.add(p);
		}
		cursor.close();
		return paragraphs;
	}

	/**
	 * 设置分页状态
	 */
	public void setPageState(int state, int fontSize) {
		ContentValues cv = new ContentValues();

		cv.put("fontsize", fontSize);
		cv.put("pageState", state);

		db.insert("pages", null, cv);
	}

	public int getPageState(int fontSize) {
		int state = 0;
		Cursor cursor = db.query("pages", new String[] { "pageState" },
				"fontsize=?", new String[] { String.valueOf(fontSize) }, null,
				null, null);

		if (cursor.moveToFirst()) {
			state = cursor.getInt(cursor.getColumnIndex("pageState"));
		}

		cursor.close();

		return state;
	}

	/**
	 * 设置书籍对应字体的总页数
	 */
	public void setPageTotal(int pageTotal, int fontSize) {
		ContentValues cv = new ContentValues();
		cv.put("pageTotal", pageTotal);
		cv.put("pageState", DbUtil.PAGE_STATE_PAGED);
		db.update("pages", cv, "fontsize=?",
				new String[] { String.valueOf(fontSize) });
	}

	/**
	 * 获取书籍对应字体的总页数
	 */
	public int getPageTotal() {
		int pageTotal = 0;
		Cursor cursor = db.query("pages", new String[] { "pageTotal" },
				"fontsize=?",
				new String[] { String.valueOf(FontUtil.fontInfo.getSize()) },
				null, null, null);

		if (cursor.moveToFirst()) {
			pageTotal = cursor.getInt(cursor.getColumnIndex("pageTotal"));
		}

		cursor.close();

		return pageTotal;
	}

	public void setPage(Page page) {
		ContentValues cv = new ContentValues();

		cv.put("fontsize", page.getFontSize());
		cv.put("pageIndex", page.getPageIndex());
		cv.put("paraIndex", page.getParaIndex());
		cv.put("wordOffsetOfPara", page.getWordNumOfPara());
		cv.put("chapter", page.getChapter());

		db.insert("pagesindex", null, cv);
	}

	public Page getPageByParaNum(int paraIndex) {
		Page page = null;
		Cursor cursor = db.query("pagesindex", null,
				"fontsize=? and paraIndex>=?",
				new String[] { String.valueOf(FontUtil.fontInfo.getSize()),
						String.valueOf(paraIndex) }, null, null,
				"paraIndex asc");
		if (cursor.moveToFirst()) {
			page = new Page();
			page.setPageIndex(cursor.getInt(cursor.getColumnIndex("pageIndex")));
			page.setParaIndex(cursor.getInt(cursor.getColumnIndex("paraIndex")));
		}

		cursor.close();
		return page;
	}

	public Page getPage(int pageIndex) {
		Page page = null;
		Cursor cursor = db.query(
				"pagesindex",
				null,
				"pageIndex=? and fontsize=?",
				new String[] { String.valueOf(pageIndex),
						String.valueOf(FontUtil.fontInfo.getSize()) }, null,
				null, null);

		if (cursor.moveToFirst()) {
			page = new Page();
			page.setParaIndex(cursor.getInt(cursor.getColumnIndex("paraIndex")));
			page.setWordNumOfPara(cursor.getInt(cursor
					.getColumnIndex("wordOffsetOfPara")));
			page.setFontSize(cursor.getInt(cursor.getColumnIndex("fontsize")));
			page.setPageIndex(cursor.getInt(cursor.getColumnIndex("pageIndex")));
			page.setChapter(cursor.getString(cursor.getColumnIndex("chapter")));
		}

		cursor.close();
		return page;
	}
}