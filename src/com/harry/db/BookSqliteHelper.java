package com.harry.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class BookSqliteHelper extends SQLiteOpenHelper {
	/**
	 * 书籍对应字体大小的页的索引表
	 */
	// private final String PAGEINDEX_TABLE_NAME = "PAGESINDEX";
	// 字体大小
	// private final String PAGEINDEX_FONTSIZE = "FONTSIZE";
	// 当前页号
	// private final String PAGEINDEX_PAGENUM = "PAGENUM";
	// 页的第一个第一段的段号
	// private final String PAGEINDEX_PARANUM = "PARANUM";
	// 此页是从段落的第几个字开始
	// private final String PAGEINDEX_WORDINDEXOFPARA = "WORDINDEXOFPARA";

	public BookSqliteHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table book (id integer primary key autoincrement,title text,path text);";
		db.execSQL(sql);

		/**
		 * offset 段在文件中的偏移量 paraIndex 段号
		 */
		sql = "create table paragraphs (id integer primary key autoincrement,paraIndex integer,offset text);";
		db.execSQL(sql);

		/**
		 * fontsize 字体大小 pageSum fontsize对应的页的总数 pageState 页的状态 未分页 正在分页 分页完成
		 */
		sql = "create table pages (id integer primary key autoincrement,fontsize text,pageTotal integer,pageState integer);";
		db.execSQL(sql);

		/**
		 * 书籍对应字体大小的页的索引表 id fontsize 字体大小 pageNum 页号 paraNum 此页对应的第一段的段号
		 * wordOffsetOfPara 此页是从段落的第几个字开始 chapter 章节
		 */
		sql = "create table pagesindex (id integer primary key autoincrement,fontsize text,pageIndex text,paraIndex text,wordOffsetOfPara text, chapter text);";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql = "drop table if exists book";
		db.execSQL(sql);

		sql = "drop table if exists paragraphs";
		db.execSQL(sql);

		sql = "drop table if exists pages";
		db.execSQL(sql);

		sql = "drop table if exists pagesindex";
		db.execSQL(sql);

		onCreate(db);
	}
}