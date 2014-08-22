package com.harry.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import com.harry.util.EncodeUtil;

public class FileManager {
	private static FileManager instance;

	public synchronized static FileManager getInstance() {
		if (instance == null)
			instance = new FileManager();
		return instance;
	}

	private FileManager() {

	}

	

	/**
	 * 在文件中从某个偏移量读取一个段落
	 */
	public String readParagraphByOffset(long offset) {
		
		File file = new File(FileUtil.fileName);
		String line = null;
		try {
			RandomAccessFile raf = new RandomAccessFile(file, "r");
			raf.seek(offset);
			line = raf.readLine();

			if(line != null){
				line = new String(line.getBytes("8859_1"), EncodeUtil.ENCODE);
				// 把全角替换为半角
				line = line.replace((char) 12288, ' ');
				// 只能去掉半角空格
				line = line.trim();
			}
			

			raf.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		

		return line;
	}

	/**
	 * 读取整个文件然后放到段落数组中
	 * 
	 * @param paragraphs
	 */
//	public void readFileFromLocal(ArrayList<Paragraph> paragraphs) {
//		File file = new File(FileUtil.fileName);
//
//		try {
//			RandomAccessFile raf = new RandomAccessFile(file, "r");
//			String line = null;
//			long offset = raf.getFilePointer();
//			int paraIndex = 0;
//			while ((line = raf.readLine()) != null) {
//				line = new String(line.getBytes("8859_1"), EncodeUtil.ENCODE);
//				line = line.replace((char) 12288, ' ');
//				line = line.trim();
//				if (!TextUtils.isEmpty(line)) {
//					Paragraph p = new Paragraph();
//
//					p.setTextOffset(offset);
//					p.setParaIndex(paraIndex);
//
//					paragraphs.add(p);
//
//					++paraIndex;
//				}
//
//				offset = raf.getFilePointer();
//			}
//
//			raf.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
}