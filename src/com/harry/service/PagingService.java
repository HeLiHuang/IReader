package com.harry.service;

import com.harry.db.DbManager;
import com.harry.file.FileUtil;
import com.harry.image.BitmapManager;
import com.harry.model.Book;
import com.harry.util.ActionUtil;
import com.harry.util.FontUtil;
import com.harry.util.MsgUtil;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;

public class PagingService extends Service {
	private PageInitThread pageThread;
	private DbManager dManager;
	private BitmapManager bManager;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// 分页进度
			case MsgUtil.INIT_PAGE_PROGRESS_MSG: {
				Intent intent = new Intent(
						ActionUtil.ACTION_NAME_PAGING_PROGRESS);
				intent.putExtra("data", msg.arg1);
				sendBroadcast(intent);
				break;
			}
			// 分页成功
			case MsgUtil.INIT_PAGE_SUCCESS_MSG: {
				Intent intent = new Intent(
						ActionUtil.ACTION_NAME_PAGING_SUCCESS);
				intent.putExtra("data", msg.arg1);
				sendBroadcast(intent);
				break;
			}
			// 重启分页线程
			case MsgUtil.INIT_PAGE_RESTART_MSG:
				createPagingThread();
				break;
			}
		}
	};

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		dManager = DbManager.getInstance(this);
		bManager = BitmapManager.getInstance(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (!TextUtils.isEmpty(FileUtil.fileName)
				&& !dManager.isExistBook(FileUtil.fileName)) {
			bManager.clear();
			if (pageThread == null) {
				createPagingThread();
			} else if (pageThread.isExit()) {
				createPagingThread();
			} else {
				// 结束并重启一个新的分页线程
				pageThread.exitAndStartAnother();
			}
		} else if (dManager.isExistBook(FileUtil.fileName)) {
			Intent i = new Intent(ActionUtil.ACTION_NAME_PAGING_SUCCESS);
			sendBroadcast(i);
		}

		return super.onStartCommand(intent, flags, startId);
	}

	private void createPagingThread() {
		// 清空DB
		dManager.clear();
		// 把书籍信息放入数据库中
		Book book = new Book();
		book.setPath(FileUtil.fileName);
		book.setTitle(FileUtil.getShortName());
		dManager.setBook(book);

		// 初始化页数和当前页为1
		Intent intent = new Intent(ActionUtil.ACTION_NAME_PAGE_INIT);
		sendBroadcast(intent);

		pageThread = new PageInitThread(handler, this,
				FontUtil.fontInfo.getSize());
		new Thread(pageThread).start();
	}
}
