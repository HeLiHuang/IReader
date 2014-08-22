package com.harry.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.os.Environment;

public class FileUtil {
	public static String fileName;
	public static final String BOOK_PATH_KEY = "BookPath";
	private final static String helpDir = Environment.getExternalStorageDirectory()
			.getPath()
			+ File.separator
			+ "IReader"
			+ File.separator;
	public final static String helpFilePath = helpDir + "help.txt";

	/**
	 * 获取书的标题
	 */
	public static String getShortName() {
		String shortName = null;
		int index = FileUtil.fileName.lastIndexOf(File.separator);
		shortName = FileUtil.fileName.substring(index + 1);
		index = shortName.indexOf(".");
		shortName = shortName.substring(0, index);
		return shortName;
	}

	public static boolean assetsCopyData(Context context) {
		boolean bIsSuc = true;
		InputStream inputStream = null;
		OutputStream outputStream = null;
		
		File dir = new File(helpDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		File file = new File(helpFilePath);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				bIsSuc = false;
			}

		} else {// 存在
			return true;
		}

		try {
			inputStream = context.getAssets().open("help.txt");
			outputStream = new FileOutputStream(file);

			int nLen = 0;

			byte[] buff = new byte[1024 * 1];
			while ((nLen = inputStream.read(buff)) > 0) {
				outputStream.write(buff, 0, nLen);
			}

			// 完成
		} catch (IOException e) {
			bIsSuc = false;
		} finally {
			try {
				if (outputStream != null) {
					outputStream.close();
				}

				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				bIsSuc = false;
			}

		}

		return bIsSuc;
	}

}
